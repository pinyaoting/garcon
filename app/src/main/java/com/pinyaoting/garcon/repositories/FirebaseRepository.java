package com.pinyaoting.garcon.repositories;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pinyaoting.garcon.interfaces.data.CloudRepositoryInterface;
import com.pinyaoting.garcon.utils.ConstantsAndUtils;
import com.pinyaoting.garcon.viewmodels.Idea;
import com.pinyaoting.garcon.viewmodels.Plan;

import java.util.List;

/**
 * Created by pinyaoting on 12/21/16.
 */

public class FirebaseRepository implements CloudRepositoryInterface  {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mShoppingListDatabaseReference;

    public FirebaseRepository() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mShoppingListDatabaseReference = mFirebaseDatabase.getReference().child(
                ConstantsAndUtils.SHOPPING_LISTS);
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
}
