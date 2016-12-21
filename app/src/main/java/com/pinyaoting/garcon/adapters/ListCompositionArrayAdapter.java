package com.pinyaoting.garcon.adapters;

import static com.pinyaoting.garcon.fragments.ListCompositionFragment.binding;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.ListFragmentActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.ViewState;
import com.pinyaoting.garcon.viewholders.IdeaViewHolder;
import com.pinyaoting.garcon.viewmodels.Idea;

import rx.Observer;

public class ListCompositionArrayAdapter extends RecyclerView.Adapter {

    IdeaInteractorInterface mIdeaInteractor;
    ListFragmentActionHandlerInterface mIdeaActionHandler;

    public ListCompositionArrayAdapter(IdeaInteractorInterface ideaInteractor,
                                       ListFragmentActionHandlerInterface ideaActionHandler) {
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
                                if (mIdeaInteractor.getIdeaCount() == 0) {
                                    Snackbar.make(binding.rvIdeas,
                                            R.string.create_grocery_snackbar_hint,
                                            Snackbar.LENGTH_LONG).show();
                                }
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
        Idea idea = mIdeaInteractor.getIdeaAtPos(position);
        if (holder instanceof IdeaViewHolder) {
            IdeaViewHolder ideaViewHolder = (IdeaViewHolder) holder;
            ideaViewHolder.setPosition(position);
            ideaViewHolder.setViewModel(idea);
            ideaViewHolder.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return mIdeaInteractor.getIdeaCount();
    }

}
