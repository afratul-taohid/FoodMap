package com.nerdgeeks.foodmap.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nerdgeeks.foodmap.BuildConfig;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.app.AppConfig;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.io.IOException;

public class UpdateChecker extends AsyncTask<String, String, JSONObject> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    private MaterialDialog materialDialog;
    private DialogNegativeButtonClickListener dialogNegativeButtonClickListener;

    private String latestVersion;
    private String currentVersion;

    public static UpdateChecker with(Context context, DialogNegativeButtonClickListener dialogNegativeButtonClickListener){
        return new UpdateChecker(context, dialogNegativeButtonClickListener);
    }

    private UpdateChecker(Context context, DialogNegativeButtonClickListener dialogNegativeButtonClickListener){
        this.mContext = context;
        this.dialogNegativeButtonClickListener = dialogNegativeButtonClickListener;
        currentVersion = BuildConfig.VERSION_NAME;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" +
                    BuildConfig.APPLICATION_ID+ "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(latestVersion!=null){
            if(!currentVersion.equalsIgnoreCase(latestVersion)){
                if(!((Activity)mContext).isFinishing()){
                    showForceUpdateDialog();
                }
            } else {
                dialogNegativeButtonClickListener.onNegativeClick(null);
            }
        }
        super.onPostExecute(jsonObject);
    }

    private void showForceUpdateDialog(){

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title("Update Available")
                .titleColorRes(android.R.color.white)
                .content("A new version of " + mContext.getResources().getString(R.string.app_name) + " is available.\nPlease update to new version")
                .contentColor(mContext.getResources().getColor(android.R.color.white))
                .backgroundColor(mContext.getResources().getColor(R.color.snippetBackground))
                .icon(mContext.getResources().getDrawable(R.drawable.ic_splash))
                .limitIconToDefaultSize()
                .positiveText("Update Now")
                .positiveColor(mContext.getResources().getColor(android.R.color.white))
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        mContext.startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                    }
                    materialDialog.dismiss();
                }).negativeText("Update Later")
                .negativeColor(mContext.getResources().getColor(android.R.color.white))
                .onNegative((dialog, which) -> {
                    dialogNegativeButtonClickListener.onNegativeClick(dialog);
                });

        materialDialog = builder.build();
        materialDialog.show();
    }

    public interface DialogNegativeButtonClickListener{

        void onNegativeClick(MaterialDialog materialDialog);

    }
}