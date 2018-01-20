package com.techweblearn.musicbeat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techweblearn.musicbeat.Models.FeedbackModel;
import com.techweblearn.musicbeat.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.feedback)
    EditText feedback;
    @BindView(R.id.submit)
    Button submit;

    ProgressDialog progressDialog;
    private Unbinder unbinder;
    private DatabaseReference mDatabase;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Util.getTheme(this));
        setContentView(R.layout.activity_feed_back);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unbinder = ButterKnife.bind(this, this);
        submit.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {


        if (isValidate()) {
            progressDialog.show();
            mDatabase.child(Util.getReadableDurationString(System.currentTimeMillis()))
                    .setValue(new FeedbackModel(name.getText().toString(), email.getText().toString(), feedback.getText().toString()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Thanks For Feedback", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error in Submitting Feedback", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Check Your Field ", Toast.LENGTH_SHORT).show();

        }
    }

    public boolean isValidate() {
        if (isEmptyText(name.getText().toString()))
            return false;
        if (isEmptyText(email.getText().toString()))
            return false;
        if (isEmptyText(feedback.getText().toString()))
            return false;
        if (email.getText().toString().contains("@"))
            return true;
        else {
            Toast.makeText(this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean isEmptyText(String s) {
        if (s == null)
            return true;
        if (s.length() == 0)
            return true;
        return false;
    }

}
