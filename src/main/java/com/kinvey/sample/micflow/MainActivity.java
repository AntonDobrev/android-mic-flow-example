package com.kinvey.sample.micflow;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Dialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyMICCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.model.User;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.auth.Credential;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyAbstractReadResponse;
import com.kinvey.java.store.StoreType;

import java.io.IOException;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Client kinveyClient;
    private static final String APP_KEY = "";
    private static final String APP_SECRET = ""; //
    private static final String redirectURI = ""; // myappredirect:// - should match the one in the manifest ( <data android:scheme="myappredirect" />) and in the MIC service
    private static final String collectionName = "";
    private static final String serviceId = ""; // the MIC service id from the app backend

    private DataStore<Book> bookStore;

    private TextView loginStatus;
    private TextView errorView;
    private Button loginWithPage;
    // private Button loginAutomated;
    private Button logout;
    private Button fetchBooksButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: for testing purposes only
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Create the Kinvey Client with an AppKey and AppSecret
        kinveyClient = new Client.Builder(APP_KEY, APP_SECRET, this).build();

        // for debugging purpose only
        kinveyClient.enableDebugLogging();

        // since 3.0.18 MIC v3 is default
        //   kinveyClient.setMICApiVersion("v2");

        bookStore = DataStore.collection(collectionName, Book.class, StoreType.SYNC, kinveyClient);


        bindViews();

        updateStatus();

    }

    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        UserStore.onOAuthCallbackReceived(intent, serviceId, kinveyClient);


    }

    private void bindViews() {
        loginStatus = (TextView) findViewById(R.id.login_status);
        loginWithPage = (Button) findViewById(R.id.loginpage);
        fetchBooksButton = (Button) findViewById(R.id.fetchbooks);

        logout = (Button) findViewById(R.id.logout);
        errorView = (TextView) findViewById(R.id.login_errors);

        loginWithPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginWithLoginPage();

            }
        });
        fetchBooksButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fetchBooks();
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }


    private void fetchBooks() {
        if (kinveyClient.isUserLoggedIn()) {
            Credential cred = null;
            try {
                cred = kinveyClient.getStore().load(kinveyClient.getActiveUser().getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String refreshToken = null;
            if (cred != null) {
                refreshToken = cred.getRefreshToken();
            }

            Log.d("App", "Here is the current refresh token: " + refreshToken);


            User currentUser = kinveyClient.getActiveUser();
            Log.d("App", "Current username before fetching books" + currentUser.getUsername());


            KinveyAbstractReadResponse<Book> booksResult;
            try {
                booksResult = bookStore.pullBlocking(null);
                List<Exception> excList = booksResult.getListOfExceptions();
                List<Book> bookList = booksResult.getResult();
                if (excList.size() == 0) {
                    notifyWithMessage("Fetched " + bookList.size() + " books");
                }
            } catch (IOException e) {
                notifyWithMessage(e.getLocalizedMessage());
            }
        } else {
            notifyWithMessage("No user is logged in. Please sign in again.");
        }

    }

    private void loginWithLoginPage() {
        loading();

//        // this is producing the problem with the original session data not found because it said the browser does not support JS.
        // this is another way to use the login page workflow with an embedded activity

//        UserStore.presentMICLoginActivity(kinveyClient, serviceId, redirectURI, new KinveyMICCallback<User>() {
//            @Override
//            public void onReadyToRender(String s) {
//                //This renders the login page with the device's default browser
//                Uri uri = Uri.parse(s);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri)
//                        //  .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
//                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//
//                startActivity(intent);
//            }
//
//            @Override
//            public void onSuccess(User user) {
//                updateStatus();
//
//                User currentUser = kinveyClient.getActiveUser();
//
//                notifyWithMessage("current username after signing in" + currentUser.getUsername());
//            }
//
//            @Override
//            public void onFailure(Throwable error) {
//                    errorView.setText(error.getMessage());
//                }
//
//        });

        if (kinveyClient.isUserLoggedIn()) {
            notifyWithMessage("There is an active user. Please sign out first.");
        } else {
            UserStore.loginWithAuthorizationCodeLoginPage(kinveyClient, serviceId, redirectURI, new KinveyMICCallback<User>() {

                @Override
                public void onSuccess(User user) {
                    updateStatus();

                    User currentUser = kinveyClient.getActiveUser();

                    notifyWithMessage("The current username after signing in is:" + currentUser.getUsername());
                }

                @Override
                public void onFailure(Throwable error) {
                    errorView.setText(error.getMessage());
                }

                @Override
                public void onReadyToRender(String myURLToRender) {
                    //This renders the login page with the device's default browser
                    Uri uri = Uri.parse(myURLToRender);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    //  .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
                    // .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                    // the login page is opened in the browser - on some devices it can be closed on some not - experiment with the above flags
                    startActivity(intent);
                }
            });
        }
    }

    private void logout() {
        loading();

        try {
            UserStore.logout(kinveyClient, new KinveyClientCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    updateStatus();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    loginStatus.setText("Error when logging out the user");
                }
            });
        } catch (Exception e) {
            errorView.setText(e.getMessage());
        }
    }

    private void loading() {
        loginStatus.setText("loading...");
    }


    private void notifyWithMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void updateStatus() {
        if (kinveyClient.isUserLoggedIn()) {
            loginStatus.setText("User is logged in!");
        } else {
            loginStatus.setText("Not logged in yet!");
        }
        errorView.setText("No Errors!");
    }
}
