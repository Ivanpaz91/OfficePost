
package com.officewall.retrofit.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.officewall.retrofit.errorhandler.CustomErrorHandler;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

public class OfficewallServiceProvider {

    /**
     * Http server.
     */
    private static final String SERVICE_ENDPOINT = "http://expresys.com";

    /**
     * Service executor.
     */
    private static final ExecutorService SERVICE_EXECUTOR = Executors.newCachedThreadPool();

    /**
     * Callback executor.
     */
    private static final MainThreadExecutor CALLBACK_EXECUTOR = new MainThreadExecutor();

    /**
     * Error handler.
     */
    private static final CustomErrorHandler ERROR_HANDLER = new CustomErrorHandler();

    /**
     * Headers that need to be added to every request can be specified using a
     * RequestInterceptor.
     */
    private static final RequestInterceptor REQUEST_INTERCEPTOR = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Content-type", "application/x-www-form-urlencoded");
        }
    };

    /**
     * OfficewallService instance.
     */
    private static final OfficewallService SERVICE = new RestAdapter.Builder()
            .setEndpoint(SERVICE_ENDPOINT).setExecutors(SERVICE_EXECUTOR, CALLBACK_EXECUTOR)
            .setErrorHandler(ERROR_HANDLER).setRequestInterceptor(REQUEST_INTERCEPTOR).build()
            .create(OfficewallService.class);

    /**
     * @return OfficewallService
     */
    public static OfficewallService getService() {
        // TODO Auto-generated method stub
        return SERVICE;
    }

}
