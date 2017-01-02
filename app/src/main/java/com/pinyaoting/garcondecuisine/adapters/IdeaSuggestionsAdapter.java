package com.pinyaoting.garcondecuisine.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.databinding.ItemIdeaSuggestionsBinding;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.viewstates.Idea;

import rx.Observer;

public class IdeaSuggestionsAdapter extends ArrayAdapter<Idea> {

    static final int mResource = R.layout.item_idea_suggestions;
    IdeaInteractorInterface mIdeaInteractor;
    Observer<ViewState> mViewStateObserver;

    public IdeaSuggestionsAdapter(Context context, IdeaInteractorInterface ideaInteractor) {
        super(context, mResource);
        mViewStateObserver = new Observer<ViewState>() {
            ViewState mViewState;

            @Override
            public void onCompleted() {
                switch (mViewState.getState()) {
                    case R.id.state_loaded:
                        notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ViewState viewState) {
                mViewState = viewState;
            }
        };
        mIdeaInteractor = ideaInteractor;
        mIdeaInteractor.subscribeSuggestionStateChange(mViewStateObserver);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Idea viewState = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.binding.setPos(position);
        viewHolder.binding.setViewState(viewState);

        // Return the completed view to render on screen
        return convertView;
    }

    @Nullable
    @Override
    public Idea getItem(int position) {
        return mIdeaInteractor.getSuggestionAtPos(position);
    }

    @Override
    public int getCount() {
        return mIdeaInteractor.getSuggestionCount();
    }

    // View lookup cache
    private static class ViewHolder {
        ItemIdeaSuggestionsBinding binding;

        public ViewHolder(View view) {
            binding = ItemIdeaSuggestionsBinding.bind(view);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (mIdeaInteractor != null && mViewStateObserver != null) {
            mIdeaInteractor.unsubscribeSuggestionStateChange(mViewStateObserver);
        }
        super.finalize();
    }
}
