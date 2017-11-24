package com.nixu.android.demoapp;

import android.app.Application;

import org.vaultit.mobilesso.mobilessosdk.IdentityProvider;

/**
 * Created by Nixu Oyj on 15/11/2017.
 */

public class DemoApplication extends Application {

    private static DemoApplication instance;

    public static DemoApplication getInstance() {
        return instance;
    }

    private IdentityProvider identityProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        String discoveryEndpointRes = "https://nordic-eid-gluu.qvarnlabs.net/oxauth/.well-known/openid-configuration";
        String clientIdRes = "@!2027.861B.4505.5885!0001!200B.B5FE!0008!67CF.BCDB";
        String clientSecretRes = "android-demo-app";
        String redirectUriRes = "com.nixu.android.demoapp.login://oidc_login";
        String logoutRedirectUriRes = "com.nixu.android.demoapp.logout://oidc_logout";
        String scopeRes = "openid profile";

        identityProvider = new IdentityProvider(
                discoveryEndpointRes,
                clientIdRes,
                clientSecretRes,
                redirectUriRes,
                logoutRedirectUriRes,
                scopeRes);
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }
}
