
package com.officewall.onscreenmessages;

import com.officewall.activities.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spannable;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The class OnScreenMsg contains message popups/dialogs used by the app Office
 * Wall.
 */
public class OnScreenMsg {

    /**
     * listener for sending result back to caller
     */
    public interface OnScreenDialogActionListener {
        public void onDialogActionPerform(String action, String data);
    }

    /**
     * show toast
     * 
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        // TODO Auto-generated method stub
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * show dialog
     * 
     * @param context
     * @param title
     * @param message
     */
    public static void showDialog(Context context, String title, String message) {
        // get spannable message
        Spannable mTitle = (Spannable)Html.fromHtml(title);
        Spannable mMessage = (Spannable)Html.fromHtml(message);
        Spannable mButtonOk = (Spannable)Html.fromHtml(context.getResources().getString(
                R.string.strOk));

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(mTitle);
        builder.setMessage(mMessage).setPositiveButton(mButtonOk,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // dismiss dialog
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
    }
}
