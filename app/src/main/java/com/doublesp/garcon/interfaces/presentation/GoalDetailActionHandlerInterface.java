package com.doublesp.garcon.interfaces.presentation;

import com.doublesp.garcon.viewmodels.Goal;

public interface GoalDetailActionHandlerInterface {

    void onCreateIdeaListClick(int pos);

    void onBookmarkClick(int pos);

    interface ListCompositionDialogHandlerInterface {

        void compose(Goal goal);

    }
}
