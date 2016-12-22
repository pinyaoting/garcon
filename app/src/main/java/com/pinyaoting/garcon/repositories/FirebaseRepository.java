package com.pinyaoting.garcon.repositories;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinyaoting.garcon.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcon.utils.ConstantsAndUtils;
import com.pinyaoting.garcon.viewstates.Idea;
import com.pinyaoting.garcon.viewstates.Plan;
import com.pinyaoting.garcon.viewstates.User;
import com.pinyaoting.garcon.viewstates.UserList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pinyaoting on 12/21/16.
 */

public class FirebaseRepository implements CloudRepositoryInterface  {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mShoppingListDatabaseReference;
    private DatabaseReference mUsersListsDatabaseReference;
    private DatabaseReference mNotifySharedDatabaseReference;

    public FirebaseRepository() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mShoppingListDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.SHOPPING_LISTS);
        mUsersListsDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.USER_LISTS);
        mNotifySharedDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.NOTIFY);
    }

    @Override
    public void savePlan(Plan plan) {
        List<Idea> ideaList = plan.getIdeas();
        mShoppingListDatabaseReference.child(plan.getId()).child(ConstantsAndUtils.IDEAS).setValue(
                ideaList);
    }

    @Override
    public void updateItemInPlan(Plan plan, int start, int count) {
        List<Idea> ideaList = plan.getIdeas();
        for (int i = 0; i < count; i++) {
            int pos = start + count - 1;
            Idea updatedIdea = ideaList.get(pos);
            mShoppingListDatabaseReference.child(plan.getId()).child(ConstantsAndUtils.IDEAS)
                    .child(String.valueOf(pos)).setValue(updatedIdea);
        }
    }

    @Override
    public void addNewItemsToPlan(Plan plan, int start, int count) {
        List<Idea> ideaList = plan.getIdeas();
        for (int i = 0; i < count; i++) {
            int pos = start + count - 1;
            Idea newIdea = ideaList.get(pos);
            mShoppingListDatabaseReference.child(plan.getId()).child(
                    ConstantsAndUtils.IDEAS).child(String.valueOf(pos)).setValue(newIdea);
        }
    }

    @Override
    public void removePlan(Plan plan) {
        if (plan == null || plan.getId() == null) {
            return;
        }
        String listId = plan.getId();
        List<Idea> ideas = plan.getIdeas();
        if (ideas == null || ideas.isEmpty() || ideas.size() == 0) {
            mFirebaseDatabase
                    .getReference()
                    .child(ConstantsAndUtils.USER_LISTS)
                    .child(ConstantsAndUtils.getOwner(getContext()))
                    .child(listId)
                    .removeValue();
        }
    }

    @Override
    public void share(Plan plan, final String userEmail, final User currentUser) {
        final String listId = plan.getId();
        final String owner = plan.getOwner();

        HashMap<String, Object> updatedUserData = shareWith(listId, currentUser);
        mFirebaseDatabase.getReference().updateChildren(updatedUserData);
        mUsersListsDatabaseReference.child(owner)
                .child(listId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserList userList = dataSnapshot.getValue(UserList.class);
                mUsersListsDatabaseReference.child(userEmail).child(listId)
                        .setValue(userList);
                mNotifySharedDatabaseReference.child(userEmail).setValue(listId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private HashMap<String, Object> shareWith(String listId, User user) {
        HashMap<String, Object> updatedUserData = new HashMap<>();

        final Map<String, Object> userMap = user.toMap();

        updatedUserData.put("/" + ConstantsAndUtils.SHARED_WITH + "/" + listId + "/"
                + user.getEmail(), userMap);

        return updatedUserData;
    }
}
