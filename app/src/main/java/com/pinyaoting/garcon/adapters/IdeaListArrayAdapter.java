package com.pinyaoting.garcon.adapters;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.IdeaListFragmentActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.viewholders.IdeaViewHolder;
import com.pinyaoting.garcon.viewstates.Idea;

import rx.Observer;

public class IdeaListArrayAdapter extends RecyclerView.Adapter {

    IdeaInteractorInterface mIdeaInteractor;
    IdeaListFragmentActionHandlerInterface mIdeaActionHandler;

    public IdeaListArrayAdapter(IdeaInteractorInterface ideaInteractor,
                                       IdeaListFragmentActionHandlerInterface ideaActionHandler) {
        mIdeaInteractor = ideaInteractor;
        mIdeaActionHandler = ideaActionHandler;

        mIdeaInteractor.subscribeIdeaStateChange(new Observer<ViewState>() {
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
                            case RELOAD:
                                notifyDataSetChanged();
                                break;
                        }
                        break;
                }

            }
        });
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

}
