package com.pinyaoting.garcondecuisine.adapters;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.viewholders.SavedGoalViewHolder;
import com.pinyaoting.garcondecuisine.viewstates.Goal;

import rx.Observer;

/**
 * Created by pinyaoting on 12/27/16.
 */

public class SavedGoalArrayAdapter extends RecyclerView.Adapter {

    GoalInteractorInterface mInteractor;
    GoalActionHandlerInterface mActionHandler;
    Observer<ViewState> mViewStateObserver;

    public SavedGoalArrayAdapter(GoalInteractorInterface interactor,
            GoalActionHandlerInterface actionHandler) {
        mInteractor = interactor;
        mActionHandler = actionHandler;
        mViewStateObserver = new Observer<ViewState>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ViewState state) {

                int start;
                int count;
                switch (state.getState()) {
                    case R.id.state_refreshing:
                        // TODO: reflect pending state on UI
                        break;
                    case R.id.state_loaded:
                        switch (state.getOperation()) {
                            case RELOAD:
                                notifyDataSetChanged();
                                break;
                            case ADD:
                                start = state.getStart();
                                notifyItemInserted(start);
                                break;
                            case UPDATE:
                                start = state.getStart();
                                count = state.getCount();
                                if (start == -1) {
                                    return;
                                }
                                notifyItemRangeChanged(start, count);
                                break;
                            case REMOVE:
                                start = state.getStart();
                                count = state.getCount();
                                notifyItemRangeRemoved(start, count);
                                break;
                            case CLEAR:
                                notifyDataSetChanged();
                                break;
                        }
                        break;
                }
            }
        };
        mInteractor.subscribeToGoalStateChange(mViewStateObserver);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_saved_goal, parent, false);
        SavedGoalViewHolder viewHolder = new SavedGoalViewHolder(view);
        viewHolder.setHandler(mActionHandler);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Goal viewState = mInteractor.getGoalAtPos(position);
        if (holder instanceof SavedGoalViewHolder) {
            SavedGoalViewHolder viewHolder = (SavedGoalViewHolder) holder;
            viewHolder.setPosition(position);
            viewHolder.setViewState(viewState);
            viewHolder.executePendingBindings();
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof SavedGoalViewHolder) {
            SavedGoalViewHolder viewHolder = (SavedGoalViewHolder) holder;
            viewHolder.recycle();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mInteractor.getGoalCount();
    }

    @Override
    protected void finalize() throws Throwable {
        if (mInteractor != null && mViewStateObserver != null) {
            mInteractor.unsubscribeFromGoalStateChange(mViewStateObserver);
        }
        super.finalize();
    }
}
