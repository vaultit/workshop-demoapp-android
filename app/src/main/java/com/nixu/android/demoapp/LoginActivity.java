package com.nixu.android.demoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.vaultit.mobilesso.mobilessosdk.NotificationReceiver;
import org.vaultit.mobilesso.mobilessosdk.Session;
import org.vaultit.mobilesso.mobilessosdk.SessionError;
import org.vaultit.mobilesso.mobilessosdk.SessionManager;

public class LoginActivity extends AppCompatActivity implements SessionManager.SessionListener {

    private SessionManager sessionManager;

    private ViewGroup loginActionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // SessionManager initialization
        sessionManager = new SessionManager(this, DemoApplication.getInstance().getIdentityProvider());

        // Register to listen to login complete events.
        // Since LoginActivity will be stopped (and the listener removed) by the time login with a
        // browser completes, this uses a bit of a different approach than the rest of the listener
        // methods.
        //
        // Removing the session listener in onStop does not prevent these events from coming through to the
        // notification callback. The LOGIN_COMPLETE event is used to finish() this activity once the
        // transition to the next activity is complete.
        sessionManager.registerForEvent(NotificationReceiver.EventType.LOGIN_COMPLETE);

        // Views from layout
        loginActionsLayout = (ViewGroup) findViewById(R.id.loginActionsLayout);
        loginActionsLayout.setVisibility(View.GONE);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button sessionCheckButton = (Button) findViewById(R.id.sessionCheckButton);

        // When login button is clicked, start the login process.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SessionManager.authorize needs to parameters:
                // - A context.
                // - The intent that is sent when the authorization succeeds. This is most often an intent
                //   to start an another activity.
                Intent responseIntent = new Intent(LoginActivity.this, LoggedInActivity.class);
                sessionManager.authorize(LoginActivity.this, responseIntent);
            }
        });

        sessionCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Same requirements as with SessionManager.authorize.
                Intent responseIntent = new Intent(LoginActivity.this, LoggedInActivity.class);
                sessionManager.performSessionCheck(LoginActivity.this, responseIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionManager.addSessionListener(this);
        sessionManager.initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sessionManager.removeSessionListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sessionManager.unregisterAllEvents();
        sessionManager.dispose();
    }

    // This method is called when the SDK has finished initializing the session. Depending on the
    // outcome, we will either display the login button or redirect the user directly to the next
    // activity.
    @Override
    public void initialized(@Nullable Session session, @Nullable SessionError sessionError) {
        onGetSession(session);
    }

    private void onGetSession(Session session) {
        if (session == null) {
            // The user cannot be directly logged in. A session might still exist in browser,
            // however.
            loginActionsLayout.setVisibility(View.VISIBLE);
        }
        else {
            // The user is already logged in.
            Intent loggedInIntent = new Intent(this, LoggedInActivity.class);

            startActivity(loggedInIntent);
            finish();
        }
    }

    @Override
    public void notification(@NonNull NotificationReceiver.EventType eventType) {
        if (eventType.equals(NotificationReceiver.EventType.LOGIN_COMPLETE)) {
            // Finish when login is complete.
            finish();
        }
    }

    // None of the below callbacks are very important for a login activity since they are session
    // state related.

    @Override
    public void didFailAuthorize(@NonNull SessionError sessionError) {

    }

    @Override
    public void didFailLogout(@NonNull SessionError sessionError) {

    }

    @Override
    public void didLoseSession(@NonNull SessionError sessionError) {

    }

    @Override
    public void didResumeSession(Session session) {

    }

    @Override
    public void didRefreshSession(Session session) {

    }

    @Override
    public void didLoseNetwork() {

    }

    @Override
    public void didGainNetwork() {

    }

}
