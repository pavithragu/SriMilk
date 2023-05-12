package com.saneforce.milksales.Common_Class;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.saneforce.milksales.Interface.AlertBox;


public class AlertDialogBox {
    public static void showDialog(Context context, String title, String message, String positiveBtnCaption, String negativeBtnCaption, boolean isCancelable, final AlertBox target) {
        {
       /*  AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();*/
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
        /* int imageResource = android.R.drawable.ic_dialog_alert;
            Drawable image = context.getResources().getDrawable(imageResource);
            */
            builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    target.PositiveMethod(dialog, id);
                }
            }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    target.NegativeMethod(dialog, id);
                }
            });

            AlertDialog alert = builder.create();
            alert.setCancelable(isCancelable);
            alert.show();
            if (isCancelable) {
                alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface arg0) {
                        target.NegativeMethod(null, 0);
                    }
                });
            }
        }
    }

}
