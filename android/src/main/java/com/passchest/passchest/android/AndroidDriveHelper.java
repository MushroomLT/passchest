package com.passchest.passchest.android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

/**
 * Created by Kasparas on 2017-08-23.
 */

public class AndroidDriveHelper {
    static final int PERMISSIONS_GET_ACCOUNTS = 0;
    static final String PREF_ACCOUNT_NAME = "accountName";
    private static GoogleAccountCredential credential;

    public static Drive getService(Activity context){
        credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
        SharedPreferences settings = context.getPreferences(Context.MODE_PRIVATE);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.GET_ACCOUNTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        PERMISSIONS_GET_ACCOUNTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null), context);
            if (credential.getSelectedAccountName() != null) {
                return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("PassChest").build();
            } else {
                context.startActivityForResult(credential.newChooseAccountIntent(),
                    LoginActivity.ACCOUNT_PICKER_REQUEST_CODE);
            }
        }
        return null;
    }

    public static void setSelectedAccountName(String accountName, Activity context) {
        SharedPreferences settings = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
    }
}
