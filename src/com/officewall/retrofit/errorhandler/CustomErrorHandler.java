
package com.officewall.retrofit.errorhandler;

import java.net.SocketTimeoutException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class CustomErrorHandler implements ErrorHandler {

    private static final String CONNECTION_ERROR = "Connection Error. Please try again.";

    private static final String NO_NETWORK = "No Network. Check your network status and try again.";

    private static final String INTERNAL_SERVER_ERROR = "Could not connect to the server. Please try again.";

    @Override
    public Throwable handleError(RetrofitError error) {
        // TODO Auto-generated method stub
        if (error.isNetworkError()) {
            if (error.getCause() instanceof SocketTimeoutException) {
                return new DefaultException(CONNECTION_ERROR);
            } else {
                return new DefaultException(NO_NETWORK);
            }
        } else {
            return new DefaultException(INTERNAL_SERVER_ERROR);
        }
    }

}
