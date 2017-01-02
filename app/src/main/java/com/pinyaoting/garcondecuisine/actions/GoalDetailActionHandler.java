package com.pinyaoting.garcondecuisine.actions;

import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalDetailActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.viewstates.Goal;

public class GoalDetailActionHandler implements GoalDetailActionHandlerInterface {

    IdeaListHandlerInterface mDialogHandler;
    GoalInteractorInterface mInteractor;

    public GoalDetailActionHandler(
            IdeaListHandlerInterface dialogHandler,
            GoalInteractorInterface interactor) {
        mDialogHandler = dialogHandler;
        mInteractor = interactor;
    }

    @Override
    public void onCreateIdeaListClick(int pos) {
        Goal goal = mInteractor.getGoalAtPos(pos);
        mDialogHandler.compose(goal);
    }

}
