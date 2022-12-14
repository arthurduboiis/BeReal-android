package com.example.bereal;

import static com.google.firebase.firestore.SetOptions.merge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bereal.Entities.FriendDetails;
import com.example.bereal.Entities.UserRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    CollectionReference  mRequestsRef;

    String currentState = "Nothing_happen";

    private final String TAG = "FriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);




        mRequestsRef = FirebaseFirestore.getInstance().collection("requests");

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        currentUser = firebaseAuth.getCurrentUser();

        email_friends = findViewById(R.id.email_friends_request);
        addFriendBtn = findViewById(R.id.btn_add_friends);

        mRequestsRef.document(currentUser.getUid()).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            if(documentSnapshot.getData() != null){
                UserRequests friendReceive = documentSnapshot.toObject(UserRequests.class);
                assert friendReceive != null;
                friendReceive.getFriendsRequests().forEach((key, value)->{
                    Log.d(TAG, "onComplete: " + key + " " + value);
                    FriendDetails friendDetails = value.get(0) ;
                    Log.d(TAG, "onComplete: " + friendDetails.getUsername() + " " + friendDetails.getStatus());
                    FriendsRequest friendsRequest = new FriendsRequest();


                    Bundle bundle = new Bundle();
                    bundle.putString("username", friendDetails.getUsername());
                    bundle.putString("userId", key);


                    friendsRequest.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                            .add(R.id.fragment_new_friends_request, FriendsRequest.class, bundle)
                            .commit();
                });


            }

        }).addOnFailureListener(e -> Log.d(TAG, "Nothing to show"));



        addFriendBtn.setOnClickListener(view -> {
            Log.d("FriendsActivity", "Click");
            fStore.collection("users").whereEqualTo("username", email_friends.getText().toString().trim()).get().addOnCompleteListener(task -> {
                DocumentSnapshot result = task.getResult().getDocuments().get(0);
                performAction(result.getId(), email_friends.getText().toString().trim());
                Log.d(TAG, "get adresse mail " + result.getId());
            });

        });

    }

    private void performAction(String userId, String username) {
        if(currentState.equals("Nothing_happen")){


            UserRequests userRequester = new UserRequests();
            ArrayList<FriendDetails> friendDetails = new ArrayList<>();
            friendDetails.add(new FriendDetails("sending", username));
            userRequester.setOwnRequests(new HashMap<>(Collections.singletonMap(userId, friendDetails)));


            mRequestsRef.document(currentUser.getUid()).set(userRequester, merge()).addOnSuccessListener(unused -> {
                Toast.makeText(FriendsActivity.this, "Friends add successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Request sended");
                currentState = "pending_request";
                addFriendBtn.setText(R.string.cancel);
            }).addOnFailureListener(e -> {
                Toast.makeText(FriendsActivity.this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error writing document", e);
            });

            UserRequests userReceiver = new UserRequests();
            ArrayList<FriendDetails> friendDetailsReceiver = new ArrayList<>();
            friendDetailsReceiver.add(new FriendDetails("receiving", currentUser.getEmail()));
            userReceiver.setFriendsRequests(new HashMap<>(Collections.singletonMap(currentUser.getUid(), friendDetailsReceiver)));

            mRequestsRef.document(userId).set(userReceiver, merge());

        }
        if(currentState.equals("pending_request")){


            Map<String, Object> docData = new HashMap<>();
            docData.put("ownRequests." +userId, FieldValue.delete());

           mRequestsRef.document(currentUser.getUid()).update(docData);


            Map<String, Object> docDataReceiver = new HashMap<>();
            docDataReceiver.put("friendsRequests." + currentUser.getUid(), FieldValue.delete());

            mRequestsRef.document(userId).update(docDataReceiver).addOnSuccessListener(unused -> {
                addFriendBtn.setText(R.string.add_to_my_friends);
                currentState = "Nothing_happen";
            });
        }

    }
}