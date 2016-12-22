package com.pinyaoting.garcon.interfaces.data;

import com.pinyaoting.garcon.viewstates.Plan;
import com.pinyaoting.garcon.viewstates.User;

/**
 * Created by pinyaoting on 12/21/16.
 */

public interface CloudRepositoryInterface {

    void savePlan(Plan plan);
    void updateItemInPlan(Plan plan, int start, int count);
    void addNewItemsToPlan(Plan plan, int start, int count);
    void removePlan(Plan plan);

    void share(Plan plan, final String userEmail, final User currentUser);
}
