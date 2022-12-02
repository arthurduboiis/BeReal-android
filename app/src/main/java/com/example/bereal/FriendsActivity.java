package com.example.bereal;

import static com.google.firebase.firestore.SetOptions.merge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FriendsActivity extends AppCompatActivity {

    EditText email_friends;
    Button addFriendBtn;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    FirebaseUser currentUser;

    CollectionReference mUsersRef, mRequestsRef;

    String currentState = "Nothing_happen";

    private final String TAG = "FriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);



        mUsersRef = FirebaseFirestore.getInstance().collection("Users");
        mRequestsRef = FirebaseFirestore.getInstance().collection("requests");

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        currentUser = firebaseAuth.getCurrentUser();

        email_friends = findViewById(R.id.email_friends_request);
        addFriendBtn = findViewById(R.id.btn_add_friends);



        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FriendsActivity", "Click");
                fStore.collection("users").whereEqualTo("username", email_friends.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        DocumentSnapshot result = task.getResult().getDocuments().get(0);
                        performAction(result.getId(), email_friends.getText().toString().trim());
                        Log.d(TAG, "get adresse mail " + result.getId());
                    }
                });
                //performAction("trg'çèfh'h!'éfneidfsjkn");
            }
        });

    }

    private void performAction(String userId, String username) {
        if(currentState.equals("Nothing_happen")){
            HashMap<String, Object> ownRequests = new HashMap<>();

            //ownRequests.put("userId",userId );

            HashMap<String, String> userData = new HashMap<>();
            userData.put("status","sending");
            userData.put("username", username);

            ownRequests.put(userId, userData);

            Map<String, Object> docData = new HashMap<>();
            docData.put("ownRequests", ownRequests);

            mRequestsRef.document(currentUser.getUid()).set(docData, merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(FriendsActivity.this, "Friends add successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Request sended");
                    currentState = "pending_request";
                    addFriendBtn.setText(R.string.cancel);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FriendsActivity.this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error writing document", e);
                }
            });
            docData.remove("ownRequests");
            ownRequests.remove(userId);

            HashMap<String, String> userDataReceiver = new HashMap<>();
            userDataReceiver.put("status","pending");
            userDataReceiver.put("username", currentUser.getEmail());


            ownRequests.put(currentUser.getUid(), userDataReceiver);


            docData.put("friendsRequests", ownRequests);
            mRequestsRef.document(userId).set(docData, merge());

        }
        if(currentState.equals("pending_request")){

            Map<String, Object> docData = new HashMap<>();
            docData.put("ownRequests." +userId, FieldValue.delete());

           mRequestsRef.document(currentUser.getUid()).update(docData);


            Map<String, Object> docDataReceiver = new HashMap<>();
            docDataReceiver.put("friendsRequests." + currentUser.getUid(), FieldValue.delete());

            mRequestsRef.document(userId).update(docDataReceiver).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    addFriendBtn.setText(R.string.add_to_my_friends);
                    currentState = "Nothing_happen";
                }
            });
        }/*
        if(currentState.equals("wainting_accept")){
            mRequestRef.child(fUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        HashMap hashMap = new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("username", email_friends.getText().toString().trim());
                        friendRef.child(fUser.getUid()).child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    friendRef.child(userId).child(fUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(FriendsActivity.this, "You added friend ", Toast.LENGTH_SHORT).show();
                                            currentState = "friends";
                                            //TODO clean edit text and state etc

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
        if(currentState.equals("friends")){

        }*/

    }
}