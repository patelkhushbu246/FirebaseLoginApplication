package com.example.firebaseloginapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class SignUpActivity extends AppCompatActivity {

    EditText edtUnmae,edtEmail,edtPass,edtCpass;
    Button regis;

    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    FirebaseFirestore firestore;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail=findViewById(R.id.email_reg);
        edtUnmae=findViewById(R.id.uname_reg);
        edtPass=findViewById(R.id.pwd_reg);
        edtCpass=findViewById(R.id.cpass_reg);
        regis=findViewById(R.id.reg_btn);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progress);

        if (firebaseAuth.getCurrentUser()!=null){
            Intent i=new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=edtEmail.getText().toString();
                String uname=edtUnmae.getText().toString();
                String pass=edtPass.getText().toString();
                String cpass=edtCpass.getText().toString();


                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(uname)){
                    edtUnmae.setError("Username is Required");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    edtPass.setError("Password is Required");
                    return;
                }
                if (pass.length()<6){
                    edtPass.setError("Password must be >=6");
                    return;
                }
                if (!pass.equals(cpass)){
                    edtCpass.setError("Password is not matched");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "User Created..", Toast.LENGTH_SHORT).show();
                            userid=firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=firestore.collection("Users").document(userid);
                            Map<String,Object> user=new HashMap<>();
                            user.put("Email",email);
                            user.put("Uname",uname);
                            user.put("Password",pass);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"OnSuccess: user profile is created for"+userid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"OnSuccess: "+e.toString());
                                }
                            });
                            Intent i=new Intent(SignUpActivity.this,DisplayActivity.class);
                            startActivity(i);
                        }else {
                            Toast.makeText(SignUpActivity.this, "Error" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}