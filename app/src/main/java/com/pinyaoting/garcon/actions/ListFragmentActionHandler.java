package com.pinyaoting.garcon.actions;

import static com.pinyaoting.garcon.utils.ConstantsAndUtils.SHARED_LIST_URL;

import android.content.Context;
import android.content.Intent;

import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.activities.MainActivity;
import com.pinyaoting.garcon.activities.ShareActivity;
import com.pinyaoting.garcon.fragments.MapFragment;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.ListFragmentActionHandlerInterface;
import com.pinyaoting.garcon.utils.ConstantsAndUtils;
import com.pinyaoting.garcon.viewstates.Plan;

public class ListFragmentActionHandler implements ListFragmentActionHandlerInterface {

    Context mContext;
    IdeaShareHandlerInterface mShareHandler;
    IdeaInteractorInterface mIdeaInteractor;

    public ListFragmentActionHandler(Context context, IdeaInteractorInterface ideaInteractor) {
        mContext = context;
        if (context instanceof IdeaShareHandlerInterface) {
            mShareHandler = (IdeaShareHandlerInterface) context;
        }
        mIdeaInteractor = ideaInteractor;
    }

    @Override
    public void onExternalShareButtonClick() {
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
    public void onShareButtonClick() {
        Intent shareIntent = new Intent(mContext, ShareActivity.class);
        shareIntent.putExtra(ConstantsAndUtils.LIST_ID, mIdeaInteractor.getPlan().getId());
        mContext.startActivity(shareIntent);
    }

    @Override
    public void onSearchButtonClick() {
        Plan plan = mIdeaInteractor.getPlan();
        mShareHandler.search(plan);
    }

    @Override
    public void onNearbyStoreButtonClick() {
        ((MainActivity) mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_home, MapFragment.newInstance(), "MapFragment")
                .addToBackStack(null)
                .commit();
    }

    public interface IdeaShareHandlerInterface {
        void share(Intent intent);

        void search(Plan plan);
    }
}
