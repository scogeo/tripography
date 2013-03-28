package com.rumbleware.tesla.api;

/**
 * @author gscott
 */
public interface StreamDataHandler {

    boolean handleData(StreamData data);

    void exceptionOccured(Throwable t);

    void streamClosed();

}
