package com.doublesp.garcon.interfaces.presentation;

import com.doublesp.garcon.viewholders.GoalViewHolder;

public interface GoalActionHandlerInterface {

    void onPreviewButtonClick(GoalViewHolder holder, int pos);

    interface PreviewHandlerInterface {
        void preview(GoalViewHolder holder, int pos);
    }

}
