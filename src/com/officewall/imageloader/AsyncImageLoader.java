
package com.officewall.imageloader;

import java.lang.ref.WeakReference;

import com.officewall.imageloader.ImageLoader;
import com.officewall.utils.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class AsyncImageLoader extends AsyncTask<Void, Void, Bitmap> {

    private Context mContext;
    private String mTaskId;
    private String mBase64Image;
    private ImageLoader imageLoader;
    private WeakReference<ImageView> imageViewReference;

    // req size
    private int reqWidth, reqHeight;

    /**
     * constructor
     * 
     * @param context
     * @param taskId
     * @param image
     * @param imageView
     */
    public AsyncImageLoader(Context context, String taskId, String base64Image, ImageView imageView) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mTaskId = taskId;
        mBase64Image = base64Image;
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    /**
     * async task runner
     */
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        imageLoader = new ImageLoader(mContext);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        // TODO Auto-generated method stub
        try {
            //
            Bitmap bitmap = null;
            if (mBase64Image != null) {
                // get screen size
                DisplayMetrics dm = Utils.getScreenSize(mContext);
                int sw = dm.widthPixels;
                // int sh = dm.heightPixels;
                // get bitmap
                bitmap = Utils.decodeFromBase64(mBase64Image.substring(mBase64Image.indexOf(",")));
                // calculate ratio to scale bitmap according
                int bw = bitmap.getWidth();
                int bh = bitmap.getHeight();
                // if (sw < sh) {
                // portrait
                reqWidth = sw;
                reqHeight = (bh * sw) / bw;
                // } else {
                // landscape
                // reqWidth = (bw * sh) / bh;
                // reqHeight = sh;
                // }

                // scale bitmap and return to load on the view
                return imageLoader.scaleBitmapToRequiredSize(bitmap, reqWidth, reqHeight);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // TODO Auto-generated method stub
        super.onPostExecute(bitmap);
        // set bitmap null if download canceled
        if (isCancelled()) {
            bitmap = null;
        }
        // load bitmap on the imageView
        if (imageViewReference != null) {
            final ImageView imageView = imageViewReference.get();
            final AsyncImageLoader bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * called from the adapter to load an image asynchronously
     * 
     * @param context
     * @param taskId
     * @param base64Image
     * @param imageView
     */
    public static void loadBitmap(Context context, String taskId, String base64Image,
            ImageView imageView) {
        if (cancelPotentialWork(taskId, imageView)) {
            Resources resources = context.getResources();
            final AsyncImageLoader asyncTask = new AsyncImageLoader(context, taskId, base64Image,
                    imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(resources, null, asyncTask);
            imageView.setImageDrawable(asyncDrawable);
            asyncTask.execute();
        }
    }

    /**
     * async drawable class
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<AsyncImageLoader> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, AsyncImageLoader bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<AsyncImageLoader>(bitmapWorkerTask);
        }

        public AsyncImageLoader getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * check for work associated with imageview
     * 
     * @param image
     * @param imageView
     * @return true if no task associated with imageview, false otherwise.
     */
    private static boolean cancelPotentialWork(String taskId, ImageView imageView) {
        final AsyncImageLoader bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String runningTask = bitmapWorkerTask.mTaskId;
            if (runningTask == null || !runningTask.equals(taskId)) {
                // cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // the same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    /**
     * @param imageView
     * @return http image loader instance
     */
    private static AsyncImageLoader getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
