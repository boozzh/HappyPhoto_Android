package cn.happyz.happyphoto.Tools;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

public class DialogHelper {

    public static void Alert(Context ctx, CharSequence title, CharSequence message,
                             CharSequence okText, OnClickListener okClickListener) {
        AlertDialog.Builder builder = createDialog(ctx, title, message);
        builder.setPositiveButton(okText, okClickListener);
        builder.create().show();
    }

    public static void Alert(Context ctx, int titleId, int messageId,
                             int okTextId, OnClickListener okClickListener) {
        Alert(ctx, ctx.getText(titleId), ctx.getText(messageId), ctx.getText(okTextId), okClickListener);
    }

    public static void Confirm(Context context, CharSequence title, CharSequence message,
                               CharSequence okText, OnClickListener okClickListener, CharSequence cancelText,
                               OnClickListener cancelListener) {
        AlertDialog.Builder builder = createDialog(context, title, message);
        builder.setPositiveButton(okText, okClickListener);
        builder.setNegativeButton(cancelText, cancelListener);
        builder.create().show();
    }

    public static void Confirm(Context context, int titleId, int messageId,
                               int okTextId, OnClickListener okClickListener,  int cancelTextId,
                               OnClickListener cancelListener) {
        Confirm(context, context.getText(titleId), context.getText(messageId), context.getText(okTextId), okClickListener, context.getText(cancelTextId), cancelListener);
    }

    private static AlertDialog.Builder createDialog(Context context, CharSequence title,
                                                    CharSequence message) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage(message);
        if(title!=null)
        {
            builder.setTitle(title);
        }
        return builder;
    }

    @SuppressWarnings("unused")
    private static AlertDialog.Builder createDialog(Context context,int titleId, int messageId) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage(messageId);
        builder.setTitle(titleId);
        return builder;
    }

    public static void ViewDialog(Context context, CharSequence title, View view,
                                  CharSequence okText, OnClickListener okClickListener, CharSequence cancelText,
                                  OnClickListener cancellistener) {

    }

    public static void ViewDialog(Context context, int titleId, View view,
                                  int okTextId, OnClickListener okClickListener, int cancelTextId,
                                  OnClickListener cancellistener) {

        ViewDialog(context, context.getText(titleId), view, context.getText(okTextId), okClickListener, context.getText(cancelTextId), cancellistener);

    }

    //
    public static void SetDialogShowing(DialogInterface dialog, boolean showing)
    {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, showing);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}