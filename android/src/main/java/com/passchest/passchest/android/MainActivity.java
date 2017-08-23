package com.passchest.passchest.android;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.passchest.passchest.crypto.AES;
import com.passchest.passchest.store.PassEntry;
import com.passchest.passchest.store.PassGroup;
import com.passchest.passchest.store.PassStore;

import org.mortbay.jetty.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    AsyncTask<Void, Void, Boolean> passStoreLoadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

       loadStore();
    }


    public char[] showPasswordDialog(){

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.password_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput =  promptsView.findViewById(R.id.passwordInput);

        final char[][] pass = {new char[0]};
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                pass[0] = (userInput.getText()).toString().toCharArray();
                                try {
                                    PassStore.decryptPassStore(pass[0]);
                                } catch (AES.InvalidPasswordException e) {
                                    Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                                    return;
                                } catch (AES.StrongEncryptionNotAvailableException e) {
                                    Toast.makeText(MainActivity.this, "Decryption unavailable on your device", Toast.LENGTH_SHORT).show();
                                    return;
                                } catch (AES.InvalidAESStreamException e) {
                                    Toast.makeText(MainActivity.this, "Error reading password store", Toast.LENGTH_SHORT).show();
                                    return;
                                } catch (FileNotFoundException e) {
                                    Toast.makeText(MainActivity.this, "Unable to decrypt pass store - file unaccessible", Toast.LENGTH_SHORT).show();
                                    return;
                                } catch (IOException e) {
                                    Toast.makeText(MainActivity.this, "Error reading pass store", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                TextView data = (TextView) findViewById(R.id.passwordData);
                                String pwd = "";
                                for(PassGroup group : PassStore.instance.passwords){
                                    pwd += group.groupName + "\n";
                                    for(PassEntry entry : group.groupEntries){
                                        pwd += "   " + entry.toString() + "\n";
                                    }
                                }
                                data.setText(pwd);
                            }

                        }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        return pass[0];

    }

    private void loadStore(){
        passStoreLoadTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return PassStore.loadPassStore();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Error retrieving pass store", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean loaded) {
                    if(!loaded) {
                        PassStore.createEmptyPassStore();
                    } else {
                        showPasswordDialog();
                    }
            }
        };
        passStoreLoadTask.execute();
    }
}
