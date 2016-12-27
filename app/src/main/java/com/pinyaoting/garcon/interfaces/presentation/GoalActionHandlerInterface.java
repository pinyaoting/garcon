package com.pinyaoting.garcon.interfaces.presentation;

import com.pinyaoting.garcon.viewholders.GoalViewHolder;

public interface GoalActionHandlerInterface {

    void onPreviewButtonClick(int pos);

    void onPreviewButtonClick(GoalViewHolder holder, int pos);

    interface PreviewHandlerInterface {
        void preview(int pos);
        void preview(GoalViewHolder holder, int pos);
    }

}
