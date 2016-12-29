package com.pinyaoting.garcon.actions;

import static com.pinyaoting.garcon.utils.ConstantsAndUtils.SHARED_LIST_URL;

import android.content.Context;
import android.content.Intent;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.activities.MainActivity;
import com.pinyaoting.garcon.fragments.MapFragment;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.IdeaListFragmentActionHandlerInterface;
import com.pinyaoting.garcon.viewstates.Plan;

public class IdeaListFragmentActionHandler implements IdeaListFragmentActionHandlerInterface {

    Context mContext;
    IdeaShareHandlerInterface mShareHandler;
    IdeaInteractorInterface mIdeaInteractor;

    public IdeaListFragmentActionHandler(Context context, IdeaInteractorInterface ideaInteractor) {
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

    public interface IdeaShareHandlerInterface {
        void share(Intent intent);

        void search(Plan plan);
    }
}
