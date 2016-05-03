package com.tr.timemanagement.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.UserModel;
import com.tr.timemanagement.ui.UserListActivity;
import com.tr.timemanagement.utils.AppConstants;


/**
 * Handle registration process with Firebase . Start as User level 0
 */
public class RegisterActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthListener;

    EditText mEditName, mEditEmail, mEditPassword;
    Button mButtonSignup;

    ProgressDialog mProgressDialog;

    private String mName;
    private String mEmail;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Register");

        mPrefs = getApplication().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, 0);

        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");

        mEditName = (EditText) findViewById(R.id.edt_name);
        mEditEmail = (EditText) findViewById(R.id.edt_mail);
        mEditPassword = (EditText) findViewById(R.id.edt_pass);
        mButtonSignup = (Button) findViewById(R.id.btn_reg);

        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {

                    // Check if we just started => logoff
                    if (mEditEmail == null || mEditEmail.getText().toString().length() == 0) {
                        mFirebaseRef.unauth();
                        return;
                    }

                    mEmail = ((String) authData.getProviderData().get("email"));
                    mPrefs.edit().putString("email", mEmail).apply();//.commit();

                    // save email + name in the DB under his uid
                    mName = mEditName.getText().toString();
                    Firebase firebaseUserSettingsRef = mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(authData.getUid());
                    UserModel userModel = new UserModel();
                    userModel.setName(mName);
                    userModel.setEmail(mEmail);
                    userModel.setLevel(AppConstants.LEVEL_USER);
                    firebaseUserSettingsRef.setValue(userModel);

                    // Start User Activity
                    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mEmail = null;
                }
                mProgressDialog.dismiss();
            }
        };

        mFirebaseRef.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (mAuthListener != null)
            mFirebaseRef.removeAuthStateListener(mAuthListener);
    }

    public void onRegister(View v) {
        mProgressDialog.show();

        mEmail = mEditEmail.getText().toString();
        final String password = mEditPassword.getText().toString();
        if (mEmail != null && mEmail.length() > 0 && password != null && password.length() > 0) {
            mFirebaseRef.createUser(mEmail, password, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    mFirebaseRef.authWithPassword(mEmail, password, null);
                    //mProgressDialog.dismiss(); // onAuthStateChanged will dismiss this Dialog
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    showErrorToast(firebaseError.toString());
                    mProgressDialog.dismiss();
                }
            });
        }

    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

}
