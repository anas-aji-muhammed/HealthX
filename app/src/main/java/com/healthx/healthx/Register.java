package com.healthx.healthx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private FirebaseAuth mauth;
    FirebaseFirestore db;
    EditText email;
    EditText password ;
    EditText age ;
    EditText heit ;
    EditText weit ;
    Button register;
    float height;
    float weight;
    Toast t1;
    String emailId;
    String pswd;
    FirebaseUser User;
    public void RegisterUser(View view){try
    {
        mauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User = mauth.getCurrentUser();
                    final String UserId = User.getUid();
                    System.out.println(UserId);
                    db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("Age", age.getText().toString());
                    user.put("height", heit.getText().toString());
                    user.put("weight", weit.getText().toString());
                    try {
                        height = Integer.parseInt(heit.getText().toString());
                        weight = Integer.parseInt(weit.getText().toString());
                    }
                    catch (NumberFormatException e){
                        e.printStackTrace();

                    }
                    int BMI = (int) ((weight/height/height)*10000);
                    user.put("BMI", BMI);

// Add a new document with a generated ID
                    db.collection("users").document(UserId)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                            //Taking user to home screen
                                            Intent i = new Intent(Register.this, Home.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            i.putExtra("UserIdFromRegister",UserId);
                                            startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
                else {
                    System.out.println("not signed up");

                }
            }
        });

    }
    catch (IllegalArgumentException e){
        e.printStackTrace();
    }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        age = findViewById(R.id.Age);
        heit = findViewById(R.id.Height);
        weit = findViewById(R.id.Weight);
        register = findViewById(R.id.Register);

        db = FirebaseFirestore.getInstance();
        mauth = FirebaseAuth.getInstance();


        emailId = email.getText().toString().trim();
        pswd = password.getText().toString().trim();
        t1 = Toast.makeText(this,"success",Toast.LENGTH_LONG);



    }
}
