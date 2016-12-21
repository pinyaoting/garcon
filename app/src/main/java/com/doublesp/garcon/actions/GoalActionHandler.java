package com.doublesp.garcon.actions;

import android.content.Context;

import com.doublesp.garcon.interfaces.presentation.GoalActionHandlerInterface;
import com.doublesp.garcon.interfaces.presentation.GoalInteractorInterface;
import com.doublesp.garcon.viewholders.GoalViewHolder;

public class GoalActionHandler implements GoalActionHandlerInterface {

    PreviewHandlerInterface mPreviewHandler;
    GoalInteractorInterface mInteractor;

    public GoalActionHandler(Context context,
                             GoalInteractorInterface interactor) {
        if (context instanceof PreviewHandlerInterface) {
            mPreviewHandler = (PreviewHandlerInterface) context;
        }
        mInteractor = interactor;
    }

    @Override
    public void onPreviewButtonClick(GoalViewHolder holder, int pos) {
        mPreviewHandler.preview(holder, pos);
    }

}
