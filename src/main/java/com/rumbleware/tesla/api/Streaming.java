package com.rumbleware.tesla.api;

import com.ning.http.client.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.CharsetString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author gscott
 */
public class Streaming {

    private final static Logger logger = LoggerFactory.getLogger(Streaming.class);

    public static final String DEFAULT_HOSTNAME = "streaming.vn.teslamotors.com";
    public static final String DEFAULT_SCHEME = "https";

    private AsyncHttpClient asyncClient;

    private final String hostname;
    private final String scheme;
    private final String vehicleId;

    public Streaming(String username, String password, String vehicleId) {
        this(username, password, vehicleId, DEFAULT_HOSTNAME, DEFAULT_SCHEME);
    }

    public Streaming(String username, String password, String vehicleId, String hostname, String scheme) {
        this.vehicleId = vehicleId;
        this.hostname = hostname;
        this.scheme = scheme;

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        Realm realm = new Realm.RealmBuilder()
                .setPrincipal(username)
                .setPassword(password)
                .setUsePreemptiveAuth(true)
                .setScheme(Realm.AuthScheme.BASIC)
                .build();

        builder.setRealm(realm);
       // builder.setConnectionTimeoutInMs(5 * 60 * 1000);
        builder.setRequestTimeoutInMs(5 * 60 * 1000);
        builder.setConnectionTimeoutInMs(5 * 60 * 1000);
        builder.setIdleConnectionTimeoutInMs(5 * 60 * 1000);

        asyncClient = new AsyncHttpClient(builder.build());

    }

    /**
     * Other stream parameters:
     *   range
     *
     * @param handler
     */
    public void stream(final StreamDataHandler handler) {

        System.out.println("opengin stream!!!");

        // allocate 1k for now.  May need to tune based on number of streams/size of data.
        final ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer(1024);

        try {

            asyncClient.prepareGet(scheme + "://" + hostname + "/stream/" + vehicleId + "/?values=speed,odometer,soc,elevation,est_heading,est_lat,est_lng,power,shift_state").execute(new AsyncHandler<Object>() {
                @Override
                public void onThrowable(Throwable t) {
                    handler.exceptionOccured(t);
                    System.out.println("Got a throwable " + t);
                    t.printStackTrace();
                }

                @Override
                public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                    channelBuffer.writeBytes(bodyPart.getBodyPartBytes());

                    int pos = 0;
                    while ((pos = channelBuffer.bytesBefore(ChannelBufferIndexFinder.LF)) >= 0) {
                        if (pos == 0) {
                            // If starting newline, then discard and continue
                            channelBuffer.readByte();
                            channelBuffer.discardReadBytes();
                            continue;
                        }
                        String value = channelBuffer.toString(0, pos - 1, Charset.forName("utf-8"));

                        System.out.println("Got value '" + value + "'");

                        channelBuffer.readBytes(pos);
                        channelBuffer.discardReadBytes();

                    }

                    return STATE.CONTINUE;
                }

                @Override
                public STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
                    System.out.println("Got status " + responseStatus);
                    System.out.println("status code is " + responseStatus.getStatusCode());
                    return STATE.CONTINUE;
                }

                @Override
                public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
                    System.out.println("got headers " + headers);
                    System.out.println("headers are " + headers.getHeaders());
                    return STATE.CONTINUE;
                }

                @Override
                public Object onCompleted() throws Exception {
                    System.out.println("completed!!!");
                    return null;
                }
            });
        }
        catch (IOException e) {
            System.out.println("caugh exception");
            throw new IllegalStateException(e);
        }

        System.out.println("stream done");


    }

}
