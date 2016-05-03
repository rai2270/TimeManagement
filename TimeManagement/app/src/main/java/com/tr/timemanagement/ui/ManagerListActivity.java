package com.tr.timemanagement.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.Manager2Model;
import com.tr.timemanagement.model.ManagerModel;
import com.tr.timemanagement.ui.adapters.FirebaseListAdapter;
import com.tr.timemanagement.ui.auth.LoginActivity;
import com.tr.timemanagement.utils.AppConstants;


/**
 * Handle Manager activity to add/remove users . if coming from Admin then open the settings for Promote/Demote
 */
public class ManagerListActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthListener;

    private String mUid = "";
    private String mUidFromOutside;

    private ListView mListView;
    FirebaseListAdapter<ManagerModel> mManagerListAdapter;
    FirebaseListAdapter<Manager2Model> mManager2ListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_list);

        setTitle("Manager");

        mListView = (ListView) this.findViewById(R.id.listView);

        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);

        Bundle b = getIntent().getExtras();
        mUidFromOutside = b == null ? null : b.getString("uid");
        if (mUidFromOutside != null && mUidFromOutside.length() > 0) {
            mUid = mUidFromOutside;
            setupManagerList();
        } else {
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    if (authData != null) {
                        mUid = authData.getUid();
                        setupManagerList();
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

    private void setupManagerList() {

        // get all users related to this manager
        // TODO: may need to replace this to the one used in SelectListActivity
        Query firebaseManagerRef = mFirebaseRef.child(AppConstants.FIREBASE_MANAGERS).child(mUid).orderByChild("name");
        mManagerListAdapter = new FirebaseListAdapter<ManagerModel>(firebaseManagerRef, ManagerModel.class, R.layout.user_row, this) {
            @Override
            protected void populateView(View v, ManagerModel model) {
                // show Name + Email
                ((TextView) v.findViewById(R.id.user_info)).setText(model.getName() + " - " + model.getEmail());


                // 1) remove user from manager.
                // 2) remove manager from user
                final String userUid = model.getKey();
                Button removeButton = (Button) v.findViewById(R.id.buttonAction);
                removeButton.setText("Remove");
                if (removeButton != null)
                    removeButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mFirebaseRef.child(AppConstants.FIREBASE_MANAGERS).child(mUid).child(userUid).removeValue();
                            mFirebaseRef.child(AppConstants.FIREBASE_USERS).child(userUid).child("manager").setValue(null);
                        }
                    });
            }
        };
        mListView.setAdapter(mManagerListAdapter);

        // when List clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ManagerModel mm = (ManagerModel) (mManagerListAdapter.getItem((int) id));
                openUser(mm.getKey());
            }
        });
    }

    private void openUser(String uid) {
        Intent intent = new Intent(this, UserListActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", uid);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUidFromOutside == null) {
        /* Cleanup the AuthStateListener */
            mFirebaseRef.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // remove settings if not Admin
        if (mUidFromOutside == null) {
            menu.findItem(R.id.action_settings).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.managersettings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startManagerSettingsActivity();
            return true;
        } else if (id == R.id.action_logout) {
            // mAuthListener will call finish() -> onDestroy()
            mFirebaseRef.unauth();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    synchronized private void startManagerSettingsActivity() {

        Intent intent = new Intent(this, ManagerSettingsActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", mUid);
        intent.putExtras(b);

        startActivity(intent);
    }

    public void onAddUsers(View v) {
        Intent intent = new Intent(this, SelectListActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", mUid);
        intent.putExtras(b);
        startActivity(intent);
    }


}
