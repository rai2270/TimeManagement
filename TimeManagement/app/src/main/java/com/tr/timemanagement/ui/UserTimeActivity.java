package com.tr.timemanagement.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tr.timemanagement.R;
import com.tr.timemanagement.model.DateModel;
import com.tr.timemanagement.model.Report2Model;
import com.tr.timemanagement.utils.AppConstants;
import com.tr.timemanagement.utils.InputFilterMinMax;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Handle User add time. Insert to FIREBASE_USERS_TIME & FIREBASE_REPORT_DATES for faster search from -> to filter
 */
public class UserTimeActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase mFirebaseUserTimeRef;
    private Firebase mFirebaseReportDates;

    private int mYear, mMonth, mDay;

    private TextView mDateView;
    private TextView mEditTextHours;
    private TextView mEditTextNotes;

    private Button mDeleteButton;

    private String mUid;
    private String mName;

    //String mReportDatesPostId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_user);

        mDateView = (TextView) findViewById(R.id.dateView);
        mEditTextHours = (TextView) findViewById(R.id.editTextHours);
        mEditTextHours.setFilters(new InputFilter[]{new InputFilterMinMax(AppConstants.HOURS_MIN, AppConstants.HOURS_MAX)});
        mEditTextNotes = (TextView) findViewById(R.id.editTextNotes);
        mDeleteButton = (Button) findViewById(R.id.buttonDelete);

        Bundle b = getIntent().getExtras();
        mUid = b.getString("uid");
        String date = b.getString("date");
        mName = b.getString("name");
        mFirebaseRef = new Firebase(AppConstants.FIREBASE_URL);
        mFirebaseUserTimeRef = mFirebaseRef.child(AppConstants.FIREBASE_USERS_TIME).child(mUid);
        mFirebaseReportDates = mFirebaseRef.child(AppConstants.FIREBASE_REPORT_DATES);

        if (date.isEmpty()) {
            // if no date, then it's new records. remove delete button. set current time.
            mDeleteButton.setVisibility(View.INVISIBLE);

            Calendar calendar = Calendar.getInstance();
            showDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            mFirebaseUserTimeRef.child(date).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //System.out.println(snapshot.getValue());
                    if (snapshot == null || snapshot.getValue() == null)
                        return;

                    DateModel dateModel = snapshot.getValue(DateModel.class);
                    dateModel.setKey(snapshot.getKey());

                    Calendar calendar = Calendar.getInstance();
                    if (dateModel.getKey() != null) {
                        try {
                            SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT);
                            Date date = df.parse(dateModel.getKey());
                            calendar.setTime(date);
                            showDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        } catch (Exception e) {
                        }
                    } else {
                        showDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    }

                    if (dateModel.getHours() != null)
                        mEditTextHours.setText(dateModel.getHours());

                    if (dateModel.getNotes() != null)
                        mEditTextNotes.setText(dateModel.getNotes());


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    //System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }


    }

    public void showDatePickerDialog(View v) {
        new DatePickerDialog(this, myDateListener, mYear, mMonth, mDay).show();
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = mYear
            // arg2 = mMonth
            // arg3 = mDay
            showDate(arg1, arg2, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;

        SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT);
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, day);
        String formattedDate = df.format(newDate.getTime());
        mDateView.setText(formattedDate);
    }

    public void onSubmit(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        Intent intent = new Intent();

        // Put the value in Firbase.reportDates.GUID. Sabe postID in Firbase.usersTime.Uid
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT);
            cal.setTime(df.parse(mDateView.getText().toString()));
            Report2Model r2m = new Report2Model(mName, mUid, mEditTextHours.getText().toString(), cal.getTimeInMillis(), mDateView.getText().toString());
            Firebase newPostRef = mFirebaseReportDates.push();
            newPostRef.setValue(r2m);
            //mReportDatesPostId = newPostRef.getKey();
        } catch (ParseException e) {
        }


        // Put the value in Firbase.usersTime.Uid.Date
        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("hours", mEditTextHours.getText().toString());
        userMap.put("notes", mEditTextNotes.getText().toString());
        //userMap.put("reportDatesPostId", mReportDatesPostId);
        Map<String, Object> datePost = new HashMap<String, Object>();
        datePost.put(mDateView.getText().toString(), userMap);
        mFirebaseUserTimeRef.updateChildren(datePost);


        setResult(RESULT_OK, intent);


        finish();

    }

    public void onCancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onDelete(View v) {
        Intent intent = new Intent();

        // Remove the value in Firbase.usersTime.Uid.Date
        mFirebaseUserTimeRef.child(mDateView.getText().toString()).removeValue();

        setResult(RESULT_OK, intent);

        finish();
    }
}
