package com.example.bereal;

import static com.google.firebase.firestore.SetOptions.merge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bereal.Entities.FriendDetails;
import com.example.bereal.Entities.UserRequests;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FriendsRequest extends Fragment {

    FloatingActionButton addFriendsBtn;
    FloatingActionButton rejectFriendsBtn;
    TextView emailToSet;
    View view;

    private final String TAG = "FriendsRequest";

    boolean deleteSuccess = false;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    FirebaseUser currentUser;

    CollectionReference mRequestsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends_request, container, false);

        assert getArguments() != null;
        String username = getArguments().getString("username");
        String userId = getArguments().getString("userId");

        addFriendsBtn = (FloatingActionButton) view.findViewById(R.id.btn_accept_friend);
        rejectFriendsBtn = (FloatingActionButton) view.findViewById(R.id.btn_reject_friend);
        emailToSet = (TextView) view.findViewById(R.id.email_request_to_set);

        emailToSet.setText(username);

        mRequestsRef = FirebaseFirestore.getInstance().collection("requests");

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        currentUser = firebaseAuth.getCurrentUser();
        ;
        addFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteRequest(userId, username, true);
            }
        });

        rejectFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRequest(userId, username, false);
            }
        });

        return view;
    }


    public void deleteRequest(String userId, String username, boolean canAddFriend) {


        Map<String, Object> docData = new HashMap<>();
        docData.put("ownRequests." +currentUser.getUid(), FieldValue.delete());

        mRequestsRef.document(userId).update(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: delete request");
                deleteSuccess = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: delete request");
                deleteSuccess =false;
            }
        });


        Map<String, Object> docDataReceiver = new HashMap<>();
        docDataReceiver.put("friendsRequests." + userId, FieldValue.delete());

        mRequestsRef.document(currentUser.getUid()).update(docDataReceiver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: delete request");
                deleteSuccess = true;
                if(canAddFriend){
                    addFriend(userId, username);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: delete request");
                deleteSuccess = false;
            }
        });

    }

    private void addFriend(String userId, String username) {

        UserRequests userSender = new UserRequests();
        ArrayList<FriendDetails> friendDetailsReceiver = new ArrayList<>();
        friendDetailsReceiver.add(new FriendDetails("friend", currentUser.getEmail()));
        userSender.setFriends(new HashMap<>(Collections.singletonMap(currentUser.getUid(), friendDetailsReceiver)));

        mRequestsRef.document(userId).set(userSender, merge());

        UserRequests thisUser = new UserRequests();
        ArrayList<FriendDetails> thisFriendsDetails = new ArrayList<>();
        thisFriendsDetails.add(new FriendDetails("friend", username));
        thisUser.setFriends(new HashMap<>(Collections.singletonMap(userId, thisFriendsDetails)));

        mRequestsRef.document(currentUser.getUid()).set(thisUser, merge());

    }



}