package com.pinyaoting.garcon.repositories;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.pinyaoting.garcon.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.models.v2.User;
import com.pinyaoting.garcon.models.v2.UserList;
import com.pinyaoting.garcon.utils.ConstantsAndUtils;
import com.pinyaoting.garcon.viewstates.Idea;
import com.pinyaoting.garcon.viewstates.Plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by pinyaoting on 12/21/16.
 */

public class FirebaseRepository implements CloudRepositoryInterface {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mListDatabaseReference;
    private DatabaseReference mShoppingListDatabaseReference;
    private DatabaseReference mUsersListsDatabaseReference;
    private DatabaseReference mNotifySharedDatabaseReference;
    private List<Observer<DataSnapshot>> mPlanObservers;
    private List<Observer<ViewState>> mViewStateObservers;
    private User mUser;

    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            getContext());

    public FirebaseRepository() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(ConstantsAndUtils.USERS);
        mShoppingListDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.SHOPPING_LISTS);
        mUsersListsDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.USER_LISTS);
        mNotifySharedDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.NOTIFY);
        mPlanObservers = new ArrayList<>();
        mViewStateObservers = new ArrayList<>();
    }

    @Override
    public void loadPlan(String planId, final Observer<DataSnapshot> observer) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference listsDatabaseReference = firebaseDatabase.getReference().child(
                ConstantsAndUtils.SHOPPING_LISTS).child(planId);
        listsDatabaseReference.addValueEventListener(new ValueEventListener() {
            Observer<DataSnapshot> mObserver = observer;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ConnectableObservable<DataSnapshot> connectableObservable =
                        Observable.just(dataSnapshot).publish();
                for (Observer<DataSnapshot> planObserver : mPlanObservers) {
                    connectableObservable.subscribeOn(Schedulers.immediate()).observeOn(
                            AndroidSchedulers.mainThread()).subscribe(planObserver);
                }
                if (mObserver != null) {
                    connectableObservable.subscribeOn(Schedulers.immediate()).observeOn(
                            AndroidSchedulers.mainThread()).subscribe(mObserver);
                }
                mObserver = null;
                connectableObservable.connect();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void subscribe(Observer<DataSnapshot> observer) {
        mPlanObservers.add(observer);
    }

    @Override
    public void newPlan(Plan plan) {
        mShoppingListDatabaseReference.child(plan.getId()).setValue(plan);
    }

    @Override
    public void savePlan(Plan plan) {
        List<Idea> ideaList = plan.getIdeas();
        mShoppingListDatabaseReference.child(plan.getId()).child(ConstantsAndUtils.IDEAS).setValue(
                ideaList);
    }

    @Override
    public void updateItemInPlan(Plan plan, int pos) {
        List<Idea> ideaList = plan.getIdeas();
        Idea updatedIdea = ideaList.get(pos);
        mShoppingListDatabaseReference.child(plan.getId()).child(ConstantsAndUtils.IDEAS)
                .child(String.valueOf(pos)).setValue(updatedIdea);
    }

    @Override
    public void addNewItemToPlan(Plan plan, int pos) {
        List<Idea> ideaList = plan.getIdeas();
        Idea newIdea = ideaList.get(pos);
        mShoppingListDatabaseReference.child(plan.getId()).child(ConstantsAndUtils.IDEAS)
                .child(String.valueOf(pos)).setValue(newIdea);
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
    public void share(Plan plan, final String userEmail) {
        final String listId = plan.getId();
        final String owner = plan.getOwner();

        HashMap<String, Object> updatedUserData = shareWith(listId, mUser);
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

    @Override
    public void onSignedInInitialize(final FirebaseUser user) {
        String userEmail = user.getEmail().replace(".", ",");
        persistsUserEmail(userEmail);
        mUsersDatabaseReference.child(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    mUser = dataSnapshot.getValue(User.class);
                    return;
                }

                HashMap<String, Object> timestampJoined = new HashMap<>();
                timestampJoined.put(ConstantsAndUtils.TIMESTAMP, ServerValue.TIMESTAMP);
                mUser = new User(
                        user.getDisplayName(),
                        user.getEmail().replace(".", ","),
                        timestampJoined);
                // Add user to db
                mUsersDatabaseReference.child(mUser.getEmail()).setValue(mUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onSignedOutCleanup() {
        mUser = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ConstantsAndUtils.PLAN_ID);
        editor.apply();
        editor.commit();
    }

    @Override
    public void populateListId(final Observer<String> observer) {
        // verify if list id has already been generated and persisted in pref
        if (sharedPreferences.contains(ConstantsAndUtils.PLAN_ID)) {
            return;
        }

        // verify if list id has already been generated and persisted in Firebase
        mUsersListsDatabaseReference.child(ConstantsAndUtils.getOwner(getContext()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null) {
                            createPlanId(observer);
                            return;
                        }
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0) {
                            createPlanId(observer);
                            return;
                        }
                        Iterator it = map.entrySet().iterator();
                        if (!it.hasNext()) {
                            createPlanId(observer);
                            return;
                        }
                        Map.Entry<String, UserList> pair = (Map.Entry) it.next();
                        String planId = pair.getKey();
                        persistsMyPlanId(planId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        createPlanId(observer);
                    }
                });
    }

    @Override
    public String myPlanId() {
        if (sharedPreferences.contains(ConstantsAndUtils.PLAN_ID)) {
            return sharedPreferences.getString(ConstantsAndUtils.PLAN_ID, null);
        }
        return null;
    }

    private void createPlanId(Observer<String> observer) {
        mListDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.USER_LISTS).child(ConstantsAndUtils.getOwner(getContext()));
        String listName = ConstantsAndUtils.getDefaultTitle(getContext());
        // create empty plan and persists to FireBase
        DatabaseReference keyReference = mListDatabaseReference.push();
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(ConstantsAndUtils.TIMESTAMP, ServerValue.TIMESTAMP);
        UserList userList = new UserList(
                listName,
                ConstantsAndUtils.getOwner(getContext()),
                timestampCreated);
        keyReference.setValue(userList);
        String planId = keyReference.getKey();
        persistsMyPlanId(planId);
        Observable.just(planId).subscribe(observer);
    }

    private void persistsUserEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConstantsAndUtils.EMAIL, email);
        editor.apply();
        editor.commit();
    }

    private void persistsMyPlanId(String planId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConstantsAndUtils.PLAN_ID, planId);
        editor.apply();
        editor.commit();
    }

    private HashMap<String, Object> shareWith(String listId, User user) {
        HashMap<String, Object> updatedUserData = new HashMap<>();

        final Map<String, Object> userMap = user.toMap();

        updatedUserData.put("/" + ConstantsAndUtils.SHARED_WITH + "/" + listId + "/"
                + user.getEmail(), userMap);

        return updatedUserData;
    }

}
