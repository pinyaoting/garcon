package com.pinyaoting.garcondecuisine.actions;

import android.content.Context;

import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.viewholders.GoalViewHolder;

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
    public void onPreviewButtonClick(int pos) {
        mPreviewHandler.preview(pos);
    }

    @Override
    public void onPreviewButtonClick(GoalViewHolder holder, int pos) {
        mPreviewHandler.preview(holder, pos);
    }

}
