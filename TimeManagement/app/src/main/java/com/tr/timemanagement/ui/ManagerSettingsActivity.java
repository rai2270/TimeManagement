package com.tr.timemanagement.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.ManagerModel;
import com.tr.timemanagement.model.UserModel;
import com.tr.timemanagement.utils.AppConstants;


/**
 * If coming from Admin then open the settings for Promote/Demote. Change Firebase links if there is a change
 */
public class ManagerSettingsActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase mFirebaseUserSettingsRef;


    UserModel mUserModel;
    AuthData mAuthData;
    UserModel mAuthUserModel;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);

        Bundle b = getIntent().getExtras();
        mUid = b.getString("uid");
        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);
        // This will open the manager info under users. so use AppConstants.FIREBASE_USERS.
        mFirebaseUserSettingsRef = mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(mUid);
        mAuthData = mFirebaseRef.getAuth();


        mFirebaseUserSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.getValue());
                if (snapshot == null || snapshot.getValue() == null)
                    return;

                // get info about  this manager
                mUserModel = snapshot.getValue(UserModel.class);

                // check if current auth user is the same as incoming uid
                // if not then it's a manager or admin -> open level option

                if (mAuthData != null) {

                    updateUserLevelDisplay();
                } else {
                    // no user authenticated
                    finish();
                }

                // get info about this admin
                mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(mAuthData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        mAuthUserModel = snapshot.getValue(UserModel.class);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void onUpdate(View v) {
        mFirebaseUserSettingsRef.setValue(mUserModel);

        // If not manager anymore, need to remove users under this manager and erase manager property of those users
        if (mUserModel.getLevel() != AppConstants.LEVEL_MANAGER) {
            mFirebaseRef.child(AppConstants.FIREBASE_MANAGERS).child(mUid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    ManagerModel model = dataSnapshot.getValue(ManagerModel.class);
                    ((ManagerModel) model).setKey(dataSnapshot.getKey());

                    mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(model.getKey()).child("manager").setValue(null);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            mFirebaseRef.child(AppConstants.FIREBASE_MANAGERS).child(mUid).removeValue();
        }

        finish();
    }

    public void onCancel(View v) {
        finish();
    }


    public void onPlus(View v) {
        setLevel(mUserModel.getLevel() + 1);

    }

    public void onMinus(View v) {
        setLevel(mUserModel.getLevel() - 1);

    }

    private void setLevel(int level) {
        if (mAuthUserModel == null) return;

        int requestedLevel = level;

        if (requestedLevel > mAuthUserModel.getLevel())
            requestedLevel = mAuthUserModel.getLevel();

        if (requestedLevel < 0)
            requestedLevel = 0;

        mUserModel.setLevel(requestedLevel);
        updateUserLevelDisplay();
    }

    private void updateUserLevelDisplay() {
        ((TextView) findViewById(R.id.textViewLevel)).setText("User Level: " + mUserModel.getLevel());
    }
}
