package com.example.android.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    EditText number;
    TextView submit;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name =  findViewById(R.id.name_et);
        email = findViewById(R.id.email_et);
        password = findViewById(R.id.Password_et);
        number = findViewById(R.id.Number_et);
        submit = findViewById(R.id.submit_tv);
        login =  findViewById(R.id.login_tv);

        //If user is already logged in then open the petList activity.
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,allListsActivity.class));
            finish();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty()
                 || password.getText().toString().isEmpty() || number.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
                    return;
                }
                handleSignUp();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,loginPage.class));
            }
        });
    }

    //Handling Signup using Email and Password.
    void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .setValue(new User(name.getText().toString(),number.getText().toString(),email.getText().toString(),""));
                          startActivity(new Intent(MainActivity.this,allListsActivity.class));
                          Toast.makeText(MainActivity.this,"Sign up successful",Toast.LENGTH_SHORT).show();
                      }
                      else{
                          Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                      }
                  }
              });
    }
}