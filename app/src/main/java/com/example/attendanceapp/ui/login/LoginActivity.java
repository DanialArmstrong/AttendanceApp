package com.example.attendanceapp.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendanceapp.Attendance;
import com.example.attendanceapp.DBHandler;
import com.example.attendanceapp.R;
import com.example.attendanceapp.SimulateDataActivity;
import com.example.attendanceapp.Student;
import com.example.attendanceapp.ui.login.LoginViewModel;
import com.example.attendanceapp.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
       //SQLiteDatabase db = openOrCreateDatabase("AttendanceDB", MODE_PRIVATE, null);
//        db.execSQL("CREATE TABLE Students (\n" +
//                "StudentName TEXT,\n" +
//                "StudentID INTEGER\n" +
//                ")");
        //db.execSQL("DROP TABLE Students");
        final EditText studentID = findViewById(R.id.studentID);
        final EditText studentName = findViewById(R.id.studentName);
        final Button loginButton = findViewById(R.id.loginBtn);
        final Button signupButton = findViewById(R.id.signupBtn);
        final DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                signUp(view);
            }
            public void signUp(View view){

                int id = Integer.parseInt(studentID.getText().toString());
                String name = studentName.getText().toString();
                Student newStudent = new Student(id, name);
                dbHandler.addHandler(newStudent);
                studentID.setText("");
                studentName.setText("");
            }

        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                login(view);
            }

            public void login(View view){
                Intent intent = new Intent (getApplicationContext(), Attendance.class);
                String id = studentID.getText().toString();
                String name = studentName.getText().toString();
                if (id.equals("0") && name.equals("admin"))
                {
                    Intent adminIntent = new Intent (getApplicationContext(), SimulateDataActivity.class);
                    startActivity(adminIntent);
                    Toast.makeText(getApplicationContext(), "Admin Login Successful", Toast.LENGTH_SHORT).show();

                }
                else {
                    Student student = dbHandler.findHandler(id);
                    intent.putExtra("StudentName", name);
                    intent.putExtra("StudentID", id);
                    if (student != null) {
                        if (name.equals(student.getStudentName())) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            //login
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Couldn't Login", Toast.LENGTH_SHORT).show();
                            studentID.setText("");
                            studentName.setText("");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No student record", Toast.LENGTH_SHORT).show();
                        studentID.setText("");
                        studentName.setText("");
                        //student doesn't have login
                    }
                }

            };
        });

        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());

            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });







    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}