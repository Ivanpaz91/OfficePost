
package com.officewall.fragments;

import java.util.Arrays;
import java.util.List;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;

import com.google.gson.JsonObject;
import com.officewall.activities.OfficeWallApp;
import com.officewall.activities.R;
import com.officewall.activities.UserWallsActivity;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.imageloader.ImageLoader;
import com.officewall.onscreenmessages.OnScreenMsg;
import com.officewall.pojo.wrappers.AddPostRs;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class AddPostFragment extends Fragment implements OnClickListener, OnCheckedChangeListener,
        TextWatcher {

    // views
    private ScrollView svImage;
    private ImageView ivImage, ivColor, ivClose, ivGallery;
    private EditText edtPostText, edtImageCaption;
    private Button btnPost;
    private RadioGroup rgColorbar;

    // required fields to add post
    private String bgColor;
    private String base64Image;

    // post color codes list
    private List<String> listPostColorCodes;

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

        // get color codes
        listPostColorCodes = Arrays
                .asList(getResources().getStringArray(R.array.arrPostColorCodes));
    }

    /**
     * called on fragments view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View mainView = inflater.inflate(R.layout.fragment_add_post, container, false);

        // setup layout controls
        initializeControls(mainView);
        initializeActions();
        attachKeyboardListeners();

        return mainView;
    }

    /**
     * initialize controls
     * 
     * @param mainView
     */
    private void initializeControls(View mainView) {
        // TODO Auto-generated method stub
        // scrollview
        svImage = (ScrollView)mainView.findViewById(R.id.sv_add_post_image);

        // edittext
        edtPostText = (EditText)mainView.findViewById(R.id.edt_add_post_text);
        edtImageCaption = (EditText)mainView.findViewById(R.id.edt_add_post_caption_text);

        // imageview
        ivImage = (ImageView)mainView.findViewById(R.id.iv_add_post_image);
        ivColor = (ImageView)mainView.findViewById(R.id.iv_add_post_bg_color);
        ivClose = (ImageView)mainView.findViewById(R.id.iv_add_post_close);
        ivGallery = (ImageView)mainView.findViewById(R.id.iv_add_post_gallery);

        // button
        btnPost = (Button)mainView.findViewById(R.id.btn_add_post_post);

        // radiogroup
        rgColorbar = (RadioGroup)mainView.findViewById(R.id.rg_add_post_colorbar);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        // edittext
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
        edtPostText.requestFocus();
        edtPostText.addTextChangedListener(this);

        // imageview
        ivClose.setOnClickListener(this);
        ivGallery.setOnClickListener(this);

        // button
        btnPost.setOnClickListener(this);

        // radiogroup
        rgColorbar.setOnCheckedChangeListener(this);

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
     * attach keyboard listener
     */
    private void attachKeyboardListeners() {
        // TODO Auto-generated method stub
        if (keyboardListenersAttached) {
            return;
        }
        svImage.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        // set flag
        keyboardListenersAttached = true;
    }

    /**
     * keyboard listener
     */
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = svImage.getRootView().getHeight() - svImage.getHeight();
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
        edtPostText.clearComposingText();
    }

    /**
     * handles radiobutton check event
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        int resId = 0;
        switch (checkedId) {
            case R.id.rb_add_post_round_1:
                resId = R.drawable.bg_wall_tile_1;
                bgColor = listPostColorCodes.get(0);
                break;
            case R.id.rb_add_post_round_2:
                resId = R.drawable.bg_wall_tile_2;
                bgColor = listPostColorCodes.get(1);
                break;
            case R.id.rb_add_post_round_3:
                resId = R.drawable.bg_wall_tile_3;
                bgColor = listPostColorCodes.get(2);
                break;
            case R.id.rb_add_post_round_4:
                resId = R.drawable.bg_wall_tile_4;
                bgColor = listPostColorCodes.get(3);
                break;
            case R.id.rb_add_post_round_5:
                resId = R.drawable.bg_wall_tile_5;
                bgColor = listPostColorCodes.get(4);
                break;
            case R.id.rb_add_post_round_6:
                resId = R.drawable.bg_wall_tile_6;
                bgColor = listPostColorCodes.get(5);
                break;
            case R.id.rb_add_post_round_7:
                resId = R.drawable.bg_wall_tile_7;
                bgColor = listPostColorCodes.get(6);
                break;
        }
        // set resource to post background
        if (resId != 0) {
            ivColor.setImageResource(resId);
            base64Image = "";
        }
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_add_post_close:
                // go back
                getActivity().onBackPressed();
                break;
            case R.id.iv_add_post_gallery:
                if (isImageAdded) {
                    // remove image from the view
                    removeImage();
                } else {
                    // choose photo from gallery
                    choosePhotoFromGallery();
                }
                break;
            case R.id.btn_add_post_post:
                // add post
                addPost();
                break;
        }
    }

    private void removeImage() {
        // TODO Auto-generated method stub
        // remove image
        edtImageCaption.setVisibility(View.GONE);
        ivImage.setVisibility(View.GONE);
        ivGallery.setImageResource(R.drawable.ic_post_gallery_white);
        // show color
        rgColorbar.setVisibility(View.VISIBLE);
        edtPostText.setVisibility(View.VISIBLE);
        ivColor.setVisibility(View.VISIBLE);
        // set status
        isImageAdded = false;
    }

    private void addImage() {
        // TODO Auto-generated method stub
        // remove color
        rgColorbar.setVisibility(View.GONE);
        edtPostText.setVisibility(View.INVISIBLE);
        ivColor.setVisibility(View.GONE);
        // add color
        edtImageCaption.setVisibility(View.VISIBLE);
        ivImage.setVisibility(View.VISIBLE);
        svImage.post(new Runnable() {
            @Override
            public void run() {
                svImage.fullScroll(View.FOCUS_DOWN);
            }
        });
        ivGallery.setImageResource(R.drawable.ic_post_gallery_white_remove);
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
        ivColor.setImageResource(0);
        ivImage.setImageBitmap(bitmap);
        // convert bitmap to base64 string
        base64Image = Utils.encodeTobase64(bitmap);
        bgColor = "";
        // add image to the view
        addImage();
    }

    /**
     * add post
     */
    private void addPost() {
        // TODO Auto-generated method stub
        /* show progressbar */
        ((UserWallsActivity)getActivity()).showProgressbar();
        /* call api */
        OfficewallService service = OfficewallServiceProvider.getService();
        service.addPost(getAddPostRequestJson(), mCallback);
    }

    /**
     * creates http params json for add post request.
     */
    private JsonObject getAddPostRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_ADD_POST;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String wallId = UserWallsActivity.SELECTED_WALL_ID;
        String text = edtPostText.getText().toString();
        String image = base64Image;
        String color = bgColor;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_ADD_POST_WALL_ID, wallId);
        objJson.addProperty(HttpConstants.PARAM_ADD_POST_TEXT, text);
        objJson.addProperty(HttpConstants.PARAM_ADD_POST_IMAGE, image);
        objJson.addProperty(HttpConstants.PARAM_ADD_POST_BG_COLOR, color);

        return objJson;
    }

    /**
     * callback to handle add post response
     */
    private CustomCallback<AddPostRs> mCallback = new CustomCallback<AddPostRs>() {

        @Override
        public void success(AddPostRs addPostRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (addPostRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                String message = "Post added successfully.";
                OnScreenMsg.showToast(getActivity(), message);
                ivClose.performClick();
                // ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_OK,
                // message);
            } else {
                // show error
                String message = addPostRs.getUserMessage();
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
                .hideSoftInputFromWindow(edtPostText.getWindowToken(), 0);
        /* remove keyboard listener */
        if (keyboardListenersAttached) {
            svImage.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}
