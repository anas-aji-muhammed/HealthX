package com.healthx.healthx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Toast failedToast;
    //Login Function
    public void Authenticate(String email, String pswd){
        mAuth.signInWithEmailAndPassword(email,pswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent i = new Intent(Login.this, Home.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.putExtra("UserIdFromLogin",mAuth.getCurrentUser().getUid());
                    startActivity(i);
                }
                else {
                    failedToast.show();
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        failedToast = Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT);

        //get login credentials
        final EditText email = findViewById(R.id.email);
        final EditText pass = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String EmailId = email.getText().toString();
                final String password = pass.getText().toString();
                Authenticate(EmailId, password);

            }
        });

    }
}
