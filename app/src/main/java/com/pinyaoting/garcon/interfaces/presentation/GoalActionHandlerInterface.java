package com.pinyaoting.garcon.interfaces.presentation;

import com.pinyaoting.garcon.viewholders.GoalViewHolder;

public interface GoalActionHandlerInterface {

    void onPreviewButtonClick(GoalViewHolder holder, int pos);

    interface PreviewHandlerInterface {
        void preview(GoalViewHolder holder, int pos);
    }

}
