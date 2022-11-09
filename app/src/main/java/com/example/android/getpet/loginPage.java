package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class loginPage extends AppCompatActivity {

    EditText email;
    EditText pass;
    TextView sub;
    TextView signup;
    ImageView psw_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email_login_et);
        pass = findViewById(R.id.password_login_et);
        sub = findViewById(R.id.submit_login_tv);
        signup = findViewById(R.id.signup_tv);
        psw_show = findViewById(R.id.psd_eye);

        psw_show.setImageResource(R.drawable.ic_hide_pwd);
        psw_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then hide it.
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change icon
                    psw_show.setImageResource(R.drawable.ic_hide_pwd);
                }
                else{
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    psw_show.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
                    return;
                }
                handleLogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginPage.this,MainActivity.class));
            }
        });
    }

    //Login using email and password.
    private void handleLogin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){

                     FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                     if(firebaseUser.isEmailVerified()){
                         Toast.makeText(getApplicationContext(),"Logged in successful",Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(loginPage.this,allListsActivity.class));
                     }
                     else{
                         firebaseUser.sendEmailVerification();
                         FirebaseAuth.getInstance().signOut();
                         showAlertDialogBox();
                     }

                 }
                 else{
                     Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                 }
             }
         });
    }

    private void showAlertDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(loginPage.this);
        builder.setTitle("Email not verified")
                .setMessage("Please verify your email.You cannot use the app without email verification.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       Intent intent = new Intent(Intent.ACTION_MAIN);
                       intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To open email app in new window not within our app
                        startActivity(intent);
                    }
                }).show();
    }
}