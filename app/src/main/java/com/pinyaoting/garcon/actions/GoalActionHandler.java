package com.pinyaoting.garcon.actions;

import android.content.Context;

import com.pinyaoting.garcon.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.viewholders.GoalViewHolder;

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
