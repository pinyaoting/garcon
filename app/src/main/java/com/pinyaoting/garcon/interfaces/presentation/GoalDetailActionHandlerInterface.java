package com.pinyaoting.garcon.interfaces.presentation;

import com.pinyaoting.garcon.viewstates.Goal;

public interface GoalDetailActionHandlerInterface {

    void onCreateIdeaListClick(int pos);

    void onBookmarkClick(int pos);

    interface ListCompositionDialogHandlerInterface {

        void compose(Goal goal);

    }
}
