package com.pinyaoting.garcondecuisine.actions;

import static com.pinyaoting.garcondecuisine.utils.ConstantsAndUtils.SHARED_LIST_URL;

import android.content.Context;
import android.content.Intent;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.activities.MainActivity;
import com.pinyaoting.garcondecuisine.fragments.MapFragment;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.IdeaListActionHandlerInterface;
import com.pinyaoting.garcondecuisine.models.Plan;

public class IdeaListActionHandler implements IdeaListActionHandlerInterface {

    Context mContext;
    IdeaShareHandlerInterface mShareHandler;
    IdeaInteractorInterface mIdeaInteractor;

    public IdeaListActionHandler(Context context, IdeaInteractorInterface ideaInteractor) {
        mContext = context;
        if (context instanceof IdeaShareHandlerInterface) {
            mShareHandler = (IdeaShareHandlerInterface) context;
        }
        mIdeaInteractor = ideaInteractor;
    }

    @Override
    public void onShareButtonClick() {
        Plan plan = mIdeaInteractor.getPlan();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        StringBuilder sharableContentBuilder = new StringBuilder();
        sharableContentBuilder
                .append(SHARED_LIST_URL)
                .append(plan.getId());

        // with app link
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharableContentBuilder.toString());
        mShareHandler.share(shareIntent);
    }

    @Override
    public void onSearchButtonClick() {
        Plan plan = mIdeaInteractor.getPlan();
        mShareHandler.search(plan);
    }

    @Override
    public void onNearbyStoreButtonClick() {
        ((MainActivity) mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.rlHome, MapFragment.newInstance(), "MapFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEmptyButtonClick() {
        mIdeaInteractor.removeAllIdeas();
    }

    @Override
    public void onIncreaseQuantity(int pos) {
        mIdeaInteractor.increaseQuantityAtPos(pos);
    }

    @Override
    public void onDecreaseQuantity(int pos) {
        mIdeaInteractor.decreaseQuantityAtPos(pos);
    }

    public interface IdeaShareHandlerInterface {
        void share(Intent intent);

        void search(Plan plan);
    }
}
