package com.tr.timemanagement.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.FilteredAdapterInterface;
import com.tr.timemanagement.model.Manager2Model;
import com.tr.timemanagement.ui.adapters.FilteredAdapter;
import com.tr.timemanagement.ui.auth.LoginActivity;
import com.tr.timemanagement.utils.AppConstants;


/**
 * Show list of all: Users, Managers, Admins. Open the settings for option to select from/to dates report.
 */
public class AdminListActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthListener;

    private String mUid = "";

    private ListView mListView;
    FilteredAdapter<Manager2Model> mAdminListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        setTitle("Admin");

        mListView = (ListView) this.findViewById(R.id.listView);

        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);

        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    mUid = authData.getUid();
                    setupAdminList();
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

    private void setupAdminList() {
        Query firebaseUsersRef = mFirebaseRef.child(AppConstants.FIREBASE_USERS).orderByChild("name");
        mAdminListAdapter = new FilteredAdapter<Manager2Model>(firebaseUsersRef, Manager2Model.class, R.layout.user_row, this, new FilteredAdapterInterface() {
            @Override
            public boolean allowObject(Object x) {
                Manager2Model y = ((Manager2Model) x);
                return !y.getKey().equals(mUid);
            }
        }) {
            @Override
            protected void populateView(View v, Manager2Model model) {
                // show Name + Email + (Level)
                ((TextView) v.findViewById(R.id.user_info)).setText(model.getName() + " - " + model.getEmail() + " (" + AppConstants.LEVEL_TO_STRING[model.getLevel()] + ")");

                final String userUid = model.getKey();

                // remove the button from List that admin see. no need for it.
                Button removeButton = (Button) v.findViewById(R.id.buttonAction);
                if (removeButton != null) {
                    ViewGroup g = (ViewGroup) removeButton.getParent();
                    if (g != null)
                        g.removeView(removeButton);
                }


            }
        };
        mListView.setAdapter(mAdminListAdapter);

        // when List clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Manager2Model mm = (Manager2Model) (mAdminListAdapter.getItem((int) id));
                openActivityBasedOnLevel(mm.getKey(), mm.getLevel());
            }
        });
    }

    private void openActivityBasedOnLevel(String uid, int level) {
        if (level == AppConstants.LEVEL_ADMIN)
            return;
        Intent intent = new Intent(this, AppConstants.LEVEL_TO_ACTICITY[level]);
        Bundle b = new Bundle();
        b.putString("uid", uid);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        mFirebaseRef.removeAuthStateListener(mAuthListener);
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
            startAdminSettingsActivity();
            return true;
        } else if (id == R.id.action_logout) {
            // mAuthListener will call finish() -> onDestroy()
            mFirebaseRef.unauth();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    synchronized private void startAdminSettingsActivity() {

        Intent intent = new Intent(this, AdminSettingsActivity.class);
        startActivity(intent);
    }


}
