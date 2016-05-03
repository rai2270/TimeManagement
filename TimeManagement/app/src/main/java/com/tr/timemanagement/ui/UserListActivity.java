package com.tr.timemanagement.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.DateModel;
import com.tr.timemanagement.model.UserModel;
import com.tr.timemanagement.ui.adapters.FirebaseListAdapter;
import com.tr.timemanagement.ui.auth.LoginActivity;
import com.tr.timemanagement.utils.AppConstants;


/**
 * Handle User level : Add , update and delete time. Setup Pref Hours using the settings.
 */
public class UserListActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthListener;

    private FirebaseListAdapter<DateModel> mListAdapter;
    private ListView mListView;

    private String mUid = "";
    private String mUidFromOutside;

    ProgressDialog mProgressDialog;

    //static final private int TIME_ACTIVITY = 1; // return value from Time Activity
    //static final private int TIME_UPDATE_ACTIVITY = 2;     // return value from Time Update Activity
    static final private int TIME_ACTIVITY = 3;  // TEST
    static final private int SETTINGS_ACTIVITY = 4; // return value from Settings Activity

    private UserModel mUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("User");

        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);

        Bundle b = getIntent().getExtras();
        mUidFromOutside = b == null ? null : b.getString("uid");
        if (mUidFromOutside != null && mUidFromOutside.length() > 0) {
            mUid = mUidFromOutside;
            setupUserList();
        } else {
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    if (authData != null) {
                        mUid = authData.getUid();
                        setupUserList();
                    } else {
                        //will be called when user logout (menu) -> mFirebaseRef.unauth()
                        Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mIntent);
                        finish();
                        return;
                    }
                }
            };

            mFirebaseRef.addAuthStateListener(mAuthListener);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUidFromOutside == null) {
        /* Cleanup the AuthStateListener */
            mFirebaseRef.removeAuthStateListener(mAuthListener);
        }
    }

    private void setupUserList() {

        // get the settings for current user
        Firebase mFirebaseUserSettingsRef = mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(mUid);
        mFirebaseUserSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null)
                    return;

                mUserModel = snapshot.getValue(UserModel.class);

                mListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        // setup List and Adapters
        Firebase objRef = mFirebaseRef.child(AppConstants.FIREBASE_USERS_TIME).child(mUid);
        mListView = (ListView) this.findViewById(R.id.listView);
        mListAdapter = new FirebaseListAdapter<DateModel>(objRef, DateModel.class, R.layout.date_row, this) {
            @Override
            protected void populateView(View v, DateModel model) {

                // Color up with Red if less that PreferredWorkingHourPerDay, else Green
                if (model != null && model.getHours() != null && model.getHours().length() > 0 &&
                        mUserModel != null && mUserModel.getPrefHours() != null && mUserModel.getPrefHours().length() > 0) {
                    float hours = Float.parseFloat(model.getHours());
                    v.setBackgroundColor(hours > Float.parseFloat(mUserModel.getPrefHours()) ?
                            Color.rgb(0xAB, 0xD4, 0x90) : Color.rgb(0xF5, 0xAA, 0x5F));
                }

                ((TextView) v.findViewById(R.id.date_text_view)).setText(model.getKey());
                ((TextView) v.findViewById(R.id.hours_text_view)).setText(model.getHours() + " hours");
                ((TextView) v.findViewById(R.id.notes_text_view)).setText(model.getNotes());
            }
        };
        mListView.setAdapter(mListAdapter);

        // Update time when clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String date = ((TextView) view.findViewById(R.id.date_text_view)).getText().toString();

                DateModel dateModel = (DateModel) (mListAdapter.getItem((int) id));
                String date = dateModel.getKey();

                onTimeUpdate(date);


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.usersettings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        } else if (id == R.id.action_logout) {
            // mAuthListener will call finish() -> onDestroy()
            mFirebaseRef.unauth();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    synchronized private void startSettingsActivity() {

        Intent intent = new Intent(this, UserSettingsActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", mUid);
        intent.putExtras(b);

        startActivityForResult(intent, SETTINGS_ACTIVITY);
    }

    // Listen for results from settings activity.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        switch (requestCode) {

            /*case TIME_ACTIVITY:
                if (resultCode == RESULT_OK) {

                }
                break;*/

            /*case TIME_UPDATE_ACTIVITY:
                if (resultCode == RESULT_OK) {

                }
                break;*/

            case TIME_ACTIVITY:
                if (resultCode == RESULT_OK) {

                }
                break;
            case SETTINGS_ACTIVITY:
                // This is the standard resultCode that is sent back if the
                // activity crashed or didn't doesn't supply an explicit result.
                if (resultCode == RESULT_OK) {


                }
                break;

            default:
                break;
        }
    }


    public void onTimeUpdate(String date) {
        Intent intent = new Intent(this, UserTimeActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", mUid);
        b.putString("date", date);
        b.putString("name", mUserModel.getName());
        intent.putExtras(b);
        startActivityForResult(intent, TIME_ACTIVITY);
    }


    public void onAddNewTime(View v) {

        onTimeUpdate("");


    }

}
