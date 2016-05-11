
package com.officewall.retrofit.callback;

import retrofit.Callback;

public interface CustomCallbackInterface<T> extends Callback<T> {
    public String getTag();

    public String setTag(String tag);

    public void cancel();

    public boolean isCanceled();
}
