package com.tripping.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SignInButton mSignIn;
    private Button mSignOut;
    private Button mRevokeAccess;
    private TextView mStatus;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "SignIn";

    private static final int STATE_SIGNED_IN = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private int mSignInProgress;

    private PendingIntent mSignInIntent;
    private int mSignInError;

    private static final int RC_SIGN_IN = 0;
    private static final int DIALOG_PLAY_SERVICE_ERROR = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignIn = (SignInButton) findViewById(R.id.sign_in);
        mSignOut = (Button) findViewById(R.id.sign_out);
        mRevokeAccess = (Button) findViewById(R.id.revoke_access);
        mStatus = (TextView) findViewById(R.id.status_txt);

        mSignIn.setOnClickListener(this);
        mSignOut.setOnClickListener(this);
        mRevokeAccess.setOnClickListener(this);

        mGoogleApiClient = buildApiClient();

    }

    GoogleApiClient buildApiClient(){
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Log.e(TAG, "OnConnectionSuspended " + cause);

    }

    @Override
    public void onConnected(Bundle connectionHint){

        Log.i(TAG, "onConnected");

        mSignIn.setEnabled(false);
        mSignOut.setEnabled(true);
        mRevokeAccess.setEnabled(true);

        mSignInProgress = STATE_SIGNED_IN;
        startActivity(new Intent(this,TripActivity.class));
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        Log.i(TAG, "Signed as " + currentUser.getDisplayName());

        mStatus.setText(String.format("Signed in As: " + currentUser.getDisplayName()));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.i(TAG, "onConnection Failed: connectionResult.getErrorCode " + connectionResult.getErrorCode());

        if(mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = connectionResult.getResolution();
            mSignInError = connectionResult.getErrorCode();
            if (mSignInProgress == STATE_SIGN_IN) {
                resolveSignInError();
            }
        }
        onSignedOut();

    }

    public void resolveSignInError(){
        if(mSignInIntent != null){

            try{
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),RC_SIGN_IN,null,0,0,0);

            }catch(IntentSender.SendIntentException e){
                Log.i(TAG, "Sign in Intent cannot be sent: " + e.getLocalizedMessage());
                mSignInProgress = STATE_SIGN_IN;

            }
        }else {
            showDialog(DIALOG_PLAY_SERVICE_ERROR);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data){

        switch(requestCode){
            case RC_SIGN_IN:
                if(responseCode == RESULT_OK){
                    mSignInProgress = STATE_SIGN_IN;
                }else{
                    mSignInProgress = STATE_SIGNED_IN;
                }
                if(!mGoogleApiClient.isConnecting()){
                    mGoogleApiClient.connect();
                }
                break;
        }

    }

    public void onSignedOut(){
        mSignIn.setEnabled(true);
        mSignOut.setEnabled(false);
        mRevokeAccess.setEnabled(false);
        mStatus.setText("Signed out.");

    }

    public void onClick(View view){
        if(!mGoogleApiClient.isConnecting()){
            switch(view.getId()){
                case R.id.sign_in:
                    mStatus.setText("Signing In");
                    resolveSignInError();
                    startActivity(new Intent(this,TripActivity.class));
                    //startActivity(new Intent(this,NavigatorActivity.class));
                    break;
                case R.id.sign_out:
                    mStatus.setText("Signing Out..");
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    break;
                case R.id.revoke_access:
                    mStatus.setText("Revoking Access...");
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                    mGoogleApiClient = buildApiClient();
                    mGoogleApiClient.connect();
                    break;
            }
        }

    }
}
