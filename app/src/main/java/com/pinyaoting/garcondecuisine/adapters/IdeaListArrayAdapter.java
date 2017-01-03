package com.pinyaoting.garcondecuisine.adapters;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.IdeaListActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.viewholders.IdeaViewHolder;
import com.pinyaoting.garcondecuisine.viewstates.Idea;

import rx.Observer;

public class IdeaListArrayAdapter extends RecyclerView.Adapter {

    IdeaInteractorInterface mIdeaInteractor;
    IdeaListActionHandlerInterface mIdeaActionHandler;
    Observer<ViewState> mViewStateObserver;

    public IdeaListArrayAdapter(IdeaInteractorInterface ideaInteractor,
                                       IdeaListActionHandlerInterface ideaActionHandler) {
        mIdeaInteractor = ideaInteractor;
        mIdeaActionHandler = ideaActionHandler;

        mViewStateObserver = new Observer<ViewState>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ViewState state) {
                switch (state.getState()) {
                    case R.id.state_refreshing:
                        // TODO: reflect pending state on UI, maybe grey out the
                        // row and show a loding icon
                        break;
                    case R.id.state_loaded:
                        switch (state.getOperation()) {
                            case ADD:
                                notifyItemInserted(state.getStart());
                                break;
                            case UPDATE:
                                notifyItemChanged(state.getStart());
                                break;
                            case REMOVE:
                                notifyItemRemoved(state.getStart());
                                break;
                            case RELOAD:
                                notifyDataSetChanged();
                                break;
                        }
                        break;
                }

            }
        };
        mIdeaInteractor.subscribeIdeaStateChange(mViewStateObserver);
    }

    @Override
    public int getItemViewType(int position) {
        Idea idea = mIdeaInteractor.getIdeaAtPos(position);
        return idea.getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_idea, parent, false);
        IdeaViewHolder holder = new IdeaViewHolder(view);
        holder.setHandler(mIdeaActionHandler);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Idea viewState = mIdeaInteractor.getIdeaAtPos(position);
        if (holder instanceof IdeaViewHolder) {
            IdeaViewHolder ideaViewHolder = (IdeaViewHolder) holder;
            ideaViewHolder.setPosition(position);
            ideaViewHolder.setViewState(viewState);
            ideaViewHolder.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return mIdeaInteractor.getIdeaCount();
    }

    @Override
    protected void finalize() throws Throwable {
        if (mIdeaInteractor != null && mViewStateObserver != null) {
            mIdeaInteractor.unsubscribeIdeaStateChange(mViewStateObserver);
        }
        super.finalize();
    }
}
