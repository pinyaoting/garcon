package com.pinyaoting.garcondecuisine.interfaces.presentation;

import com.pinyaoting.garcondecuisine.viewstates.Goal;

public interface GoalDetailActionHandlerInterface {

    void onCreateIdeaListClick(int pos);

    interface IdeaListHandlerInterface {

        void compose(Goal goal);

    }
}
