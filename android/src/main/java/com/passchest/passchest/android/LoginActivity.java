package com.passchest.passchest.android;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.passchest.passchest.DriveHelper;
import com.passchest.passchest.store.PassStore;

import org.mortbay.jetty.Main;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class LoginActivity extends AppCompatActivity {

    public static final int COMPLETE_AUTHORIZATION_REQUEST_CODE = 0;
    public static final int ACCOUNT_PICKER_REQUEST_CODE = 1;

    private AsyncTask<String, Void, Boolean> authenticationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PassStore.passStoreFile = new File(getFilesDir(), "pass.store");

        setContentView(R.layout.activity_login);

        if(PassStore.passStoreFile.exists()){
            PassStore.service = AndroidDriveHelper.getService(this);
            if(PassStore.service == null)
                return;
            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    public void login(View btn){
        PassStore.service = AndroidDriveHelper.getService(this);
        if(PassStore.service == null)
            return;
        verifyAuth();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case COMPLETE_AUTHORIZATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(this, "Access to drive denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case ACCOUNT_PICKER_REQUEST_CODE:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        AndroidDriveHelper.setSelectedAccountName(accountName, this);                    PassStore.service = AndroidDriveHelper.getService(this);
                        if(PassStore.service == null)
                            return;
                        verifyAuth();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AndroidDriveHelper.PERMISSIONS_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PassStore.service = AndroidDriveHelper.getService(this);
                    if(PassStore.service == null)
                        return;
                    verifyAuth();
                } else {
                    Toast.makeText(this, "Permission to access your Google account denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void verifyAuth(){
        authenticationTask = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                try {
                    FileList files = PassStore.service.files().list()
                            .setSpaces("appDataFolder")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageSize(10)
                            .execute();
                    return true;
                } catch (UserRecoverableAuthIOException authio) {
                    startActivityForResult(authio.getIntent(), COMPLETE_AUTHORIZATION_REQUEST_CODE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error attempting authorization", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(success){
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };
        authenticationTask.execute();
    }
}
