package com.nixu.android.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.vaultit.mobilesso.mobilessosdk.NotificationReceiver;
import org.vaultit.mobilesso.mobilessosdk.Session;
import org.vaultit.mobilesso.mobilessosdk.SessionError;
import org.vaultit.mobilesso.mobilessosdk.SessionManager;

/**
 * Created by Nixu Oyj on 15/11/2017.
 */

public class LoggedInActivity extends AppCompatActivity implements SessionManager.SessionListener {

    private SessionManager sessionManager;
    private TextView sessionStatusLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        TextView usernameTextView = (TextView) findViewById(R.id.username);
        Button logoutButton = (Button) findViewById(R.id.logoutButton);

        // Here we create a new instance of SessionManager. It will share data with other
        // SessionManager instances.
        sessionManager = new SessionManager(this, DemoApplication.getInstance().getIdentityProvider());

        // Register for LOGOUT_COMPLETE to be able to finish() this activity when logout completes.
        sessionManager.registerForEvent(NotificationReceiver.EventType.LOGOUT_COMPLETE);

        // Set the name of the logged in user to a TextView.
        String name = sessionManager.getSession().getIdTokenPayload().name;
        usernameTextView.setText(name);

        // Set the online state
        sessionStatusLabel = (TextView) findViewById(R.id.sessionStatusLabel);
        updateSessionStatusLabel();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout works mostly the same way as login. It will use the browser to logout,
                // then send a given result intent to continue. Here we simply go to the LoginActivity
                // again after logout.
                Intent intent = new Intent(LoggedInActivity.this, LoginActivity.class);
                sessionManager.logout(intent);
            }
        });
    }

    private void updateSessionStatusLabel() {
        boolean isOnline = sessionManager.getSession().isOnline();

        sessionStatusLabel.setText(isOnline ? "ONLINE" : "OFFLINE");
        sessionStatusLabel.setTextColor(isOnline ? 0xFF00FF00 : 0xFFFF0000); // green or red
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionManager.addSessionListener(this);

        // NOTE: No need to call sessionManager.initialize() in this activity since they all
        // share the same data!
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

    // SessionListener interface implementation

    @Override
    public void didLoseSession(@NonNull SessionError sessionError) {
        Toast.makeText(this, "Session has expired or logout has occurred!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(LoggedInActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void notification(@NonNull NotificationReceiver.EventType eventType) {
        if (eventType.equals(NotificationReceiver.EventType.LOGOUT_COMPLETE)) {
            // When the logout is successful, there is no need for this activity anymore.
            finish();
        }
    }

    @Override
    public void didLoseNetwork() {
        updateSessionStatusLabel();
    }

    @Override
    public void didGainNetwork() {
        updateSessionStatusLabel();
    }

    @Override
    public void didFailLogout(@NonNull SessionError sessionError) {
        Toast.makeText(this, sessionError.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void initialized(@Nullable Session session, @Nullable SessionError sessionError) {
        // No need to implement this method here. The state is already initialized in LoginActivity.
    }

    @Override
    public void didFailAuthorize(@NonNull SessionError sessionError) {
        // No need to implement this method here. We will not authorize the user in this activity.
    }

    @Override
    public void didResumeSession(Session session) {

    }

    @Override
    public void didRefreshSession(Session session) {

    }

}
