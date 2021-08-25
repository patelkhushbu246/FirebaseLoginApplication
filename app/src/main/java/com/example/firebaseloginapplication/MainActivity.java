package com.example.firebaseloginapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText emailedt,pwdedt;
    TextView forgotpwd,signup;
    Button login;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailedt=findViewById(R.id.uname_login);
        pwdedt=findViewById(R.id.pwd_login);
        forgotpwd=findViewById(R.id.forgotpwd_txt);
        signup=findViewById(R.id.signup_txt);
        login=findViewById(R.id.login_btn);

        firebaseAuth=FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailedt.getText().toString().trim();
                String pass=pwdedt.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    emailedt.setError("Username is Required");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    pwdedt.setError("Password is Required");
                    return;
                }
                if (pass.length()<6){
                    pwdedt.setError("Password must be >=6");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login..", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(MainActivity.this,DisplayActivity.class);
                            startActivity(i);
                        }else {
                            Toast.makeText(MainActivity.this, "Error !.." +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetemail=new EditText(v.getContext());
                AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                builder.setTitle("Reset Password");
                builder.setMessage("Emter your Email");
                builder.setView(resetemail);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String email=resetemail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Reset Link send to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error ! Reset Link id not send"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });

    }
}