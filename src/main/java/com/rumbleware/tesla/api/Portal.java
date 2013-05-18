package com.rumbleware.tesla.api;

import com.ning.http.client.*;
import com.rumbleware.web.executors.SharedExecutors;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author gscott
 */
public class Portal {

    private static final Logger logger = LoggerFactory.getLogger(Portal.class);

    private final Client restClient;

    private final AsyncHttpClient asyncClient;

    public static final String DEFAULT_PORTAL_HOST = "https://portal.vn.teslamotors.com";
    public static final String DEFAULT_STREAMING_HOST = "https://streaming.vn.teslamotors.com";

    public static final String USER_CREDENTIAL_COOKIE = "user_credentials";

    private final String portalHost;
    private final String streamingHost;

    private final VehicleRequest<VehicleDescriptor> vehicleById = new VehicleRequest<VehicleDescriptor>(VehicleDescriptor.class);

    private final VehiclePathRequest<CommandResponse> mobileEnabled = new VehiclePathRequest<CommandResponse>("mobile_enabled", CommandResponse.class);

    private final VehicleCommand<GuiSettings> guiSettingsCommand = new VehicleCommand<GuiSettings>("gui_settings", GuiSettings.class);
    private final VehicleCommand<DriveState> driveStateCommand = new VehicleCommand<DriveState>("drive_state", DriveState.class);
    private final VehicleCommand<VehicleState> vehicleStateCommand = new VehicleCommand<VehicleState>("vehicle_state", VehicleState.class);
    private final VehicleCommand<ChargeState> chargeStateCommand = new VehicleCommand<ChargeState>("charge_state", ChargeState.class);
    private final VehicleCommand<ClimateState> climateStateCommand = new VehicleCommand<ClimateState>("climate_state", ClimateState.class);


    private final VehicleSimpleCommand honkHornCommand = new VehicleSimpleCommand("honk_horn");
    private final VehicleSimpleCommand wakeupCommand = new VehicleSimpleCommand("wake_up");

    public Portal() {
        this(DEFAULT_PORTAL_HOST, DEFAULT_STREAMING_HOST);
    }

    public Portal(String portalHost, String streamingHost) {
        this.portalHost = portalHost;
        this.streamingHost = streamingHost;

        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);

        restClient = Client.create(cc);
        //restClient.addFilter(new LoggingFilter(System.out));

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();

        builder.setExecutorService(SharedExecutors.executorService());
        builder.setScheduledExecutorService(SharedExecutors.scheduledExecutorService());
        // builder.setConnectionTimeoutInMs(5 * 60 * 1000);
        builder.setRequestTimeoutInMs(5 * 60 * 1000);
        builder.setConnectionTimeoutInMs(5 * 60 * 1000);
        builder.setIdleConnectionTimeoutInMs(5 * 60 * 1000);

        asyncClient = new AsyncHttpClient(builder.build());

    }

    public VehicleDescriptor[] vehicles(PortalCredentials credentials) {
        WebResource.Builder resource = restClient.resource(portalHost + "/vehicles").getRequestBuilder();

        credentials.sign(resource);

        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);


        return response.getEntity(VehicleDescriptor[].class);
    }


    public VehicleDescriptor vehicle(final PortalCredentials credentials, String id) {
        return vehicleById.execute(credentials, id);
    }

    public CommandResponse mobileEnabled(final PortalCredentials credentials, String id) {
        return mobileEnabled.execute(credentials, id);
    }

    public CommandResponse honkHorn(final PortalCredentials credentials, String id) {
        return honkHornCommand.execute(credentials, id);
    }

    public ClimateState climateState(final PortalCredentials credentials, String id) {
        return climateStateCommand.execute(credentials, id);
    }

    public ChargeState chargeState(final PortalCredentials credentials, String id) {
        return chargeStateCommand.execute(credentials, id);
    }

    public DriveState driveState(final PortalCredentials credentials, final String id) {
        return driveStateCommand.execute(credentials, id);
    }

    public VehicleState vehicleState(final PortalCredentials credentials, final String id) {
        return vehicleStateCommand.execute(credentials, id);
    }

    public CommandResponse wakeUp(final PortalCredentials credentials, final String id) {
        return wakeupCommand.execute(credentials, id);
    }

    public GuiSettings guiSettings(final PortalCredentials credentials, final String id) {
        return guiSettingsCommand.execute(credentials, id);
    }

    /**
     * Other stream parameters:
     *   range
     *
     * @param handler
     */
    public void stream(final PortalCredentials credentials, final VehicleDescriptor descriptor, final StreamDataHandler handler) {

        if (logger.isDebugEnabled()) {
            logger.debug("opening stream for " + credentials.getUsername() + " vehicle " + descriptor.getVehicleId());
        }

        // allocate 1k for now.  May need to tune based on number of streams/size of data.
        final ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer(1024);

        try {
            Realm realm = new Realm.RealmBuilder()
                    .setPrincipal(credentials.getUsername())
                    .setPassword(descriptor.getTokens().get(0))
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .build();

            RequestBuilder builder = new RequestBuilder();

            builder.setRealm(realm);


            asyncClient
                    .prepareGet(streamingHost + "/stream/" + descriptor.getVehicleId() + "/?values=speed,odometer,soc,elevation,est_heading,est_lat,est_lng,power,shift_state")
                    .setHeader("User-Agent", "tripography/1.0")
                    .setRealm(realm)
                    .execute(new AsyncHandler<Object>() {

                @Override
                public void onThrowable(Throwable t) {
                    handler.exceptionOccured(t);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Caught exception", t);
                    }
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
                        channelBuffer.readBytes(pos);
                        channelBuffer.discardReadBytes();

                        String[] values = value.split(",", -1);

                        StreamData data = new StreamData(values);

                        boolean stop = handler.handleData(data);

                        if (stop) {
                            return STATE.ABORT;
                        }
                    }

                    return STATE.CONTINUE;
                }

                @Override
                public STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
                    // TODO handle illegal login exception
                    int statusCode = responseStatus.getStatusCode();

                    switch (statusCode) {
                        case 200:
                            return STATE.CONTINUE;
                        default:
                            return STATE.ABORT;
                    }
                }

                @Override
                public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
                    //logger.info("headers are " + headers.getHeaders());
                    return STATE.CONTINUE;
                }

                @Override
                public Object onCompleted() throws Exception {
                    handler.streamClosed();
                    return null;
                }
            });
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    public void close() {
        asyncClient.close();
    }

    public class VehicleRequest<Response> {

        protected final Class<Response> responseClass;

        public VehicleRequest(Class<Response> responseClass) {
            this.responseClass = responseClass;
        }

        protected String getUriTemplate(String ...args) {
            return portalHost + "/vehicles/" + args[0];
        }

        public Response execute(PortalCredentials credentials, String id) {
            WebResource.Builder resource = restClient.resource(getUriTemplate(id)).getRequestBuilder();

            credentials.sign(resource);

            ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

            return response.getEntity(responseClass);
        }

    }

    public class VehiclePathRequest<Response> extends VehicleRequest<Response> {

        private String pathName;

        public VehiclePathRequest(String pathName, Class<Response> responseClass) {
            super(responseClass);
            this.pathName = pathName;
        }

        protected String getUriTemplate(String ...args) {
            return super.getUriTemplate(args[0]) + "/" + pathName;
        }

    }

    public class VehicleCommand<Response> extends VehicleRequest<Response> {

        private final String commandName;

        public VehicleCommand(String commandName, Class<Response> responseClass) {
            super(responseClass);
            this.commandName = commandName;
        }

        protected String getUriTemplate(String ...args) {
            return super.getUriTemplate(args[0]) + "/command/" + commandName;
        }

    }

    public class VehicleSimpleCommand extends VehicleCommand<CommandResponse> {

        public VehicleSimpleCommand(String commandName) {
            super(commandName, CommandResponse.class);
        }
    }



}
