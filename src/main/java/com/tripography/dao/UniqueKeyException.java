package com.tripography.dao;

/**
 * @author gscott
 */

import org.springframework.core.NestedRuntimeException;

public class UniqueKeyException extends NestedRuntimeException {

    private String _keyName;

    public UniqueKeyException(String keyName, Throwable reason) {
        super(reason.getMessage(), reason);
        _keyName = keyName;
    }

    /**
     * The key that failed.
     * @return
     */
    public String getKey() {
        return _keyName;
    }
}
