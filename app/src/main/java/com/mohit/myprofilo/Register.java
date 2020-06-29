package com.mohit.myprofilo;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class Register extends AppCompatActivity {


    public static final String TAG = "TAG, ";
    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegisterBtn;
    FirebaseAuth fAuth;
    TextView mLoginBtn;
    String userID;
    ProgressBar progressBar;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName=findViewById(R.id.fullName);
        mEmail=findViewById(R.id.email);
        mPassword=findViewById(R.id.password);
        mPhone=findViewById(R.id.phoneNumber);
        mRegisterBtn=findViewById(R.id.registerBtn);
        mLoginBtn=findViewById(R.id.rText);


        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar);




        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                final String fullName=mFullName.getText().toString();
                final String phone=mPhone.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Emial is Requried");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Requried");
                    return;
                }

                if(password.length()<6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //register user in firebase//

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Successfully Created.", Toast.LENGTH_SHORT).show();
                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fstore.collection("user").document(userID);
                            Map<String,Object>user=new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d(TAG ,"onSuccess: User Profile is Created for "+ userID);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG ,"onFailure:"+ e.toString());

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else{
                            Toast.makeText(Register.this, "Error ! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


                mLoginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Login.class));
                    }
                });




    }
}
