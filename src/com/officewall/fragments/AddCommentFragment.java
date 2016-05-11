
package com.officewall.fragments;

import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;

import com.google.gson.JsonObject;
import com.officewall.activities.OfficeWallApp;
import com.officewall.activities.R;
import com.officewall.activities.UserWallsActivity;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.imageloader.ImageLoader;
import com.officewall.onscreenmessages.OnScreenMsg;
import com.officewall.pojo.wrappers.AddCommentRs;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class AddCommentFragment extends Fragment implements OnClickListener, TextWatcher {

    // views
    private ViewGroup viewGroup;
    private ScrollView svImage;
    private ImageView ivHeader, ivClose, ivImage, ivGallery;
    private EditText edtSaySomething;
    private Button btnPost;

    // currently selected post id
    private String POST_ID;

    // header bg
    private String postBgColor;
    private String postBgImage;

    // required fields to add comment
    private String base64Image;

    // keyboard show/hide flag
    private boolean keyboardListenersAttached = false;
    private boolean isKeyboardShown = false;

    // flag to check image added
    private boolean isImageAdded;

    /**
     * called on fragment created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // hide actionbar and set progressbar from the top
        ((UserWallsActivity)getActivity()).hideActionbarAndDrawer();
        ((UserWallsActivity)getActivity()).setPaddingTopToProgressbar();

        // get bundled data
        Bundle bundle = getArguments();
        if (bundle != null) {
            POST_ID = bundle.getString(DefaultConstants.EXTRA_POST_ID);
            postBgColor = bundle.getString(DefaultConstants.EXTRA_POST_BG_COLOR);
            postBgImage = bundle.getString(DefaultConstants.EXTRA_POST_BG_IMAGE);
        }
    }

    /**
     * called on fragments view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View mainView = inflater.inflate(R.layout.fragment_add_comment, container, false);

        // setup layout controls
        initializeControls(mainView);
        initializeActions();
        setHeaderBg();
        attachKeyboardListeners();

        return mainView;
    }

    /**
     * initialize controls
     */
    private void initializeControls(View mainView) {
        // TODO Auto-generated method stub
        // view group
        viewGroup = (ViewGroup)mainView;

        // scrollview
        svImage = (ScrollView)mainView.findViewById(R.id.sv_add_comment_image);

        // imageview
        ivHeader = (ImageView)mainView.findViewById(R.id.iv_add_comment_header);
        ivClose = (ImageView)mainView.findViewById(R.id.iv_add_comment_close);
        ivImage = (ImageView)mainView.findViewById(R.id.iv_add_comment_image);
        ivGallery = (ImageView)mainView.findViewById(R.id.iv_add_comment_gallery);

        // edittext
        edtSaySomething = (EditText)mainView.findViewById(R.id.edt_add_comment_text);

        // button
        btnPost = (Button)mainView.findViewById(R.id.btn_add_comment_post);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        // imageview
        ivClose.setOnClickListener(this);
        ivGallery.setOnClickListener(this);

        // edittext
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
        edtSaySomething.requestFocus();
        edtSaySomething.addTextChangedListener(this);

        // button
        btnPost.setOnClickListener(this);

        // show view according to image added status
        if (isImageAdded) {
            // add image to the view
            addImage();
        } else {
            // remove image from the view
            removeImage();
        }
    }

    /**
     * set header bg
     */
    private void setHeaderBg() {
        // TODO Auto-generated method stub
        if (postBgColor != null && !postBgColor.equals("")) {
            int index = Arrays.asList(getResources().getStringArray(R.array.arrPostColorCodes))
                    .indexOf(postBgColor);
            if (index != -1) {
                ivHeader.setScaleType(ScaleType.FIT_XY);
                ivHeader.setImageResource(getResources().obtainTypedArray(
                        R.array.typedArrCommentHeaderDrawables).getResourceId(index, 0));
            }
        } else {
            try {
                // required size
                int reqWidth, reqHeight;
                // get screen size
                DisplayMetrics dm = Utils.getScreenSize(getActivity());
                int sw = dm.widthPixels;
                // get bitmap
                Bitmap bitmap = Utils.decodeFromBase64(postBgImage.substring(postBgImage
                        .indexOf(",")));
                int bw = bitmap.getWidth();
                int bh = bitmap.getHeight();
                // calculate ratio to scale bitmap according
                reqWidth = sw;
                reqHeight = (bh * sw) / bw;
                // scale bitmap
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
                int headerHeight = (int)getResources().getDimension(R.dimen.height_comment_header);
                // crop to header size
                Bitmap bmp = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), headerHeight);
                ivHeader.setImageBitmap(bmp);
                // clear bitmap
                bitmap.recycle();
                scaled.recycle();
                bitmap = scaled = null;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * attach keyboard listener
     */
    private void attachKeyboardListeners() {
        // TODO Auto-generated method stub
        if (keyboardListenersAttached) {
            return;
        }
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        // set flag
        keyboardListenersAttached = true;
    }

    /**
     * keyboard listener
     */
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = viewGroup.getRootView().getHeight() - viewGroup.getHeight();
            int contentViewTop = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                    .getTop();
            if (heightDiff <= contentViewTop) {
                // on hide keyboard
                if (isKeyboardShown) {
                    ivClose.performClick();
                    isKeyboardShown = false;
                }
            } else {
                // on show keyboard
                isKeyboardShown = true;
            }
        }
    };

    /**
     * handles text change events
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        // to remove underline from text while typing
        edtSaySomething.clearComposingText();
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_add_comment_close:
                // go back
                getActivity().onBackPressed();
                break;
            case R.id.iv_add_comment_gallery:
                if (isImageAdded) {
                    // remove image from the view
                    removeImage();
                } else {
                    // choose photo from gallery
                    choosePhotoFromGallery();
                }
                break;
            case R.id.btn_add_comment_post:
                // add comment
                addComment();
                break;
        }
    }

    /**
     * remove image
     */
    private void removeImage() {
        // TODO Auto-generated method stub
        base64Image = "";
        svImage.setVisibility(View.GONE);
        ivGallery.setImageResource(R.drawable.ic_gallery);
        // set status
        isImageAdded = false;
    }

    /**
     * add image
     */
    private void addImage() {
        // TODO Auto-generated method stub
        svImage.setVisibility(View.VISIBLE);
        svImage.post(new Runnable() {
            @Override
            public void run() {
                svImage.fullScroll(View.FOCUS_DOWN);
            }
        });
        ivGallery.setImageResource(R.drawable.ic_gallery_remove);
        // set status
        isImageAdded = true;
    }

    /**
     * choose photo from gallery
     */
    private void choosePhotoFromGallery() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, DefaultConstants.REQUEST_GALLERY);
    }

    /**
     * handles activity result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DefaultConstants.REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                String imagePath = null;
                // get path from URI
                if (data != null && data.getData() != null && !data.getData().equals(Uri.EMPTY)) {
                    imagePath = Utils.getRealPathFromURI(getActivity(), data.getData());
                }
                // load bitmap from path
                if (imagePath != null) {
                    loadBitmap(imagePath);
                } else {
                    // show toast
                    String error_message = getResources().getString(R.string.strMsgError);
                    OnScreenMsg.showToast(getActivity(), error_message);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                // show toast
                String error_message = getResources().getString(R.string.strMsgError);
                OnScreenMsg.showToast(getActivity(), error_message);
            }
        }
    }

    /**
     * load bitmap
     * 
     * @param imagePath
     */
    private void loadBitmap(String imagePath) {
        // TODO Auto-generated method stub
        try {
            // get screen size
            DisplayMetrics dm = Utils.getScreenSize(getActivity());
            int sw = dm.widthPixels;
            int sh = dm.heightPixels;
            // get bitmap
            ImageLoader imageLoader = new ImageLoader(getActivity());
            Bitmap bitmap = imageLoader.getBitmapFromSDCard(imagePath, (sw < sh) ? sw : sh);
            // calculate ratio to scale bitmap according
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            int reqWidth, reqHeight;
            if (sw < sh) {
                // portrait
                reqWidth = sw;
                reqHeight = (bh * sw) / bw;
            } else {
                // landscape
                reqWidth = (bw * sh) / bh;
                reqHeight = sh;
            }
            // scale bitmap and return to load on the view
            Bitmap scaledBitmap = imageLoader
                    .scaleBitmapToRequiredSize(bitmap, reqWidth, reqHeight);
            // set bitmap
            if (scaledBitmap != null) {
                setPhoto(scaledBitmap);
            } else {
                // show toast
                String error_message = getResources().getString(R.string.strMsgError);
                OnScreenMsg.showToast(getActivity(), error_message);
            }
            // clear bitmap
            bitmap.recycle();
            bitmap = null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            // show toast
            String error_message = getResources().getString(R.string.strMsgError);
            OnScreenMsg.showToast(getActivity(), error_message);
        }
    }

    /**
     * set photo
     * 
     * @param bitmap
     */
    private void setPhoto(Bitmap bitmap) {
        // TODO Auto-generated method stub
        // set image
        ivImage.setImageBitmap(bitmap);
        // convert bitmap to base64 string
        base64Image = Utils.encodeTobase64(bitmap);
        // add image to the view
        addImage();
    }

    /**
     * add comment
     */
    private void addComment() {
        // TODO Auto-generated method stub
        /* show progressbar */
        ((UserWallsActivity)getActivity()).showProgressbar();
        /* call api */
        OfficewallService service = OfficewallServiceProvider.getService();
        service.addComment(getAddCommentRequestJson(), mCallback);
    }

    /**
     * creates http params json for add comment request.
     */
    private JsonObject getAddCommentRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_ADD_POST;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String postId = POST_ID;
        String text = edtSaySomething.getText().toString();
        String image = base64Image;
        String bgColor = "";

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_ADD_COMMENT_POST_ID, postId);
        objJson.addProperty(HttpConstants.PARAM_ADD_COMMENT_TEXT, text);
        objJson.addProperty(HttpConstants.PARAM_ADD_COMMENT_IMAGE, image);
        objJson.addProperty(HttpConstants.PARAM_ADD_COMMENT_BG_COLOR, bgColor);

        return objJson;
    }

    /**
     * callback to handle add comment response
     */
    private CustomCallback<AddCommentRs> mCallback = new CustomCallback<AddCommentRs>() {

        @Override
        public void success(AddCommentRs addCommentRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (addCommentRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                String message = "Comment added successfully.";
                OnScreenMsg.showToast(getActivity(), message);
                ivClose.performClick();
                // ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_OK,
                // message);
            } else {
                // show error
                String message = addCommentRs.getUserMessage();
                ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
            }

        }

        @Override
        public void failure(RetrofitError error) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            // show error
            String message = error.getCause().getMessage();
            ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    };

    /**
     * called on fragments view destroyed
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        /* cancel request */
        mCallback.cancel();
        /* hide progressbar */
        ((UserWallsActivity)getActivity()).hideProgressbar();
        /* hide soft keyboard */
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(edtSaySomething.getWindowToken(), 0);
        /* remove keyboard listener */
        if (keyboardListenersAttached) {
            viewGroup.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }

}
