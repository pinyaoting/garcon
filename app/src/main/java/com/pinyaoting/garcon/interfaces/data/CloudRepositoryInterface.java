package com.pinyaoting.garcon.interfaces.data;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.pinyaoting.garcon.models.Plan;

import rx.Observer;

/**
 * Created by pinyaoting on 12/21/16.
 */

public interface CloudRepositoryInterface {

    void loadPlan(String planId, Observer<DataSnapshot> observer);
    void subscribe(Observer<DataSnapshot> observer);
    void newPlan(Plan plan);
    void savePlan(Plan plan);
    void updateItemInPlan(Plan plan, int pos);
    void addNewItemToPlan(Plan plan, int pos);
    void removePlan(Plan plan);

    void share(Plan plan, final String userEmail);

    void onSignedInInitialize(FirebaseUser user);
    void onSignedOutCleanup();
    void populateListId(Observer<String> observer);
    String myPlanId();
}
