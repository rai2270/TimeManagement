package com.tr.timemanagement.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.UserModel;
import com.tr.timemanagement.utils.AppConstants;


/**
 * Handle login process with Firebase . After authentication, start activity based on user level: User, Manager, Admin
 */
public class LoginActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    //private Firebase.AuthStateListener mAuthListener;

    private EditText mEditEmail, mEditPassword;
    private Button mButtonSingin;

    private ProgressDialog mProgressDialog;

    private String mEmail;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Log in");

        mPrefs = getApplication().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, 0);

        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");

        mEditEmail = (EditText) findViewById(R.id.editName);
        mEditPassword = (EditText) findViewById(R.id.editPass);
        mButtonSingin = (Button) findViewById(R.id.buttonSignin);

        setupUsername();

    }

    private void onAuthenticatedStartMain(String uid, String email) {
        mEmail = email;
        mPrefs.edit().putString("email", mEmail).apply();

        // check level of auth uid
        mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserModel mUserModel = snapshot.getValue(UserModel.class);

                Intent intent = new Intent(getApplicationContext(), AppConstants.LEVEL_TO_ACTICITY[mUserModel.getLevel()]);
                startActivity(intent);
                finish();

                /*Intent intent;
                switch (mUserModel.getLevel())
                {
                    case AppConstants.LEVEL_USER: // Users
                        intent = new Intent(getApplicationContext(), UserListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case AppConstants.LEVEL_MANAGER: // Managers
                        intent = new Intent(getApplicationContext(), ManagerListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case AppConstants.LEVEL_ADMIN: // Admin
                        intent = new Intent(getApplicationContext(), AdminListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }*/

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mProgressDialog.dismiss();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        //mFirebaseRef.removeAuthStateListener(mAuthListener);
    }

    private void setupUsername() {
        //SharedPreferences mPrefs = getApplication().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, 0);
        mEmail = mPrefs.getString("email", null);
        if (mEmail != null && mEmail.length() > 0) {
            mEditEmail.setText(mEmail);
        }

    }

    public void onSignin(View v) {
        mProgressDialog.show();

        final String email = mEditEmail.getText().toString();
        final String password = mEditPassword.getText().toString();

        if (email != null && email.length() > 0 && password != null && password.length() > 0) {
            mFirebaseRef.authWithPassword(email, password,
                    new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            onAuthenticatedStartMain(authData.getUid(), (String) authData.getProviderData().get("email"));

                            //mProgressDialog.dismiss();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError error) {
                            // Something went wrong :(
                            switch (error.getCode()) {
                                case FirebaseError.USER_DOES_NOT_EXIST:
                                    showErrorToast("User does not exists. Please register.");
                                    onRegister(null);
                                    break;
                                case FirebaseError.INVALID_PASSWORD:
                                    showErrorToast(error.toString());
                                    break;
                                default:
                                    showErrorToast(error.toString());
                            }
                            mProgressDialog.dismiss();
                        }
                    });
        } else {
            mProgressDialog.dismiss();
        }



    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void onRegister(View v) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}
