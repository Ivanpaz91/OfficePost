
package com.officewall.retrofit.callback;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomCallback<T> implements CustomCallbackInterface<T> {

    private String tag;
    private boolean isCanceled;

    @Override
    public void failure(RetrofitError error) {
        // TODO Auto-generated method stub
    }

    @Override
    public void success(T t, Response response) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getTag() {
        // TODO Auto-generated method stub
        return this.tag;
    }

    @Override
    public String setTag(String tag) {
        // TODO Auto-generated method stub
        return this.tag = tag;
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        this.isCanceled = true;
    }

    @Override
    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return this.isCanceled;
    }

}
