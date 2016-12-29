package com.pinyaoting.garcon.interfaces.presentation;

import com.pinyaoting.garcon.viewstates.Goal;

public interface GoalDetailActionHandlerInterface {

    void onCreateIdeaListClick(int pos);

    interface IdeaListHandlerInterface {

        void compose(Goal goal);

    }
}
