package com.pinyaoting.garcondecuisine.activities;

import static com.pinyaoting.garcondecuisine.adapters.HomeFragmentPagerAdapter.MY_IDEAS;
import static com.pinyaoting.garcondecuisine.adapters.HomeFragmentPagerAdapter.SAVED_GOALS;
import static com.pinyaoting.garcondecuisine.adapters.HomeFragmentPagerAdapter.SEARCH_GOAL;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.batch.android.Batch;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.actions.IdeaListActionHandler;
import com.pinyaoting.garcondecuisine.adapters.HomeFragmentPagerAdapter;
import com.pinyaoting.garcondecuisine.application.GarconApplication;
import com.pinyaoting.garcondecuisine.databinding.ActivityMainBinding;
import com.pinyaoting.garcondecuisine.dependencies.components.presentation.MainActivitySubComponent;
import com.pinyaoting.garcondecuisine.dependencies.modules.presentation.MainActivityModule;
import com.pinyaoting.garcondecuisine.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcondecuisine.fragments.GoalPreviewFragment;
import com.pinyaoting.garcondecuisine.fragments.GoalSearchFragment;
import com.pinyaoting.garcondecuisine.fragments.IdeaListFragment;
import com.pinyaoting.garcondecuisine.fragments.SavedGoalsFragment;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalDetailActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.InjectorInterface;
import com.pinyaoting.garcondecuisine.models.Plan;
import com.pinyaoting.garcondecuisine.utils.TabUtils;
import com.pinyaoting.garcondecuisine.viewholders.GoalViewHolder;
import com.pinyaoting.garcondecuisine.viewstates.Goal;

import java.util.Arrays;

import javax.inject.Inject;

import rx.Observer;

public class MainActivity extends AppCompatActivity implements InjectorInterface,
        GoalActionHandlerInterface.PreviewHandlerInterface,
        GoalDetailActionHandlerInterface.IdeaListHandlerInterface,
        IdeaListActionHandler.IdeaShareHandlerInterface {

    public static final int RC_SIGN_IN = 1;
    public static final String MAIN_ACTIVITY_SNAPSHOT = "MAIN_ACTIVITY_SNAPSHOT";
    ActivityMainBinding binding;
    MainActivitySubComponent mActivityComponent;
    @Inject
    GoalInteractorInterface mGoalInteractor;
    @Inject
    IdeaInteractorInterface mIdeaInteractor;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Fragment mDialogFragment;
    private HomeFragmentPagerAdapter mPagerAdapter;
    private Transition mChangeTransform;
    private Transition mFadeTransform;
    Observer<Plan> mEmptyPlanObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(MainActivity.this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mPagerAdapter = new HomeFragmentPagerAdapter(
                getSupportFragmentManager(), MainActivity.this);
        binding.viewpager.setAdapter(mPagerAdapter);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                didGainFocus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.tabs.setupWithViewPager(binding.viewpager);
        TabUtils.bindIcons(MainActivity.this, binding.viewpager, binding.tabs);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mIdeaInteractor.onSignedInInitialize(user);
                    // startService(new Intent(this, NotificationService.class));
                } else {
                    // User is signed out
                    mIdeaInteractor.onSignedOutCleanup();
                    // stopService(new Intent(this, NotificationService.class));
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(
                                                    AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(
                                                    AuthUI.FACEBOOK_PROVIDER).build()))
                                    .setTheme(R.style.FullscreenTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mChangeTransform = TransitionInflater.from(this).
                inflateTransition(R.transition.transition_to_detail);
        mFadeTransform = TransitionInflater.from(this).
                inflateTransition(android.R.transition.fade);

        mEmptyPlanObserver = new Observer<Plan>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Plan plan) {
                if (mIdeaInteractor.getIdeaCount() == 0) {
                    Snackbar.make(binding.clContent,
                            R.string.create_grocery_snackbar_hint,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        };
        if (savedInstanceState != null && savedInstanceState.containsKey(MAIN_ACTIVITY_SNAPSHOT)) {
            mIdeaInteractor.restoreDataSnapshot(
                    savedInstanceState.getParcelable(MAIN_ACTIVITY_SNAPSHOT));
        } else {
            mGoalInteractor.search(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Batch.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        handleDeeplink();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Batch.onNewIntent(this, intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStop() {
        Batch.onStop(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Batch.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MAIN_ACTIVITY_SNAPSHOT, mIdeaInteractor.getDataSnapshot());
    }

    @Override
    public void share(Intent i) {
        startActivity(i);
    }

    @Override
    public void inject(IdeaListFragment fragment) {
        getActivityComponent().inject(fragment);
    }

    @Override
    public void inject(GoalSearchFragment fragment) {
        getActivityComponent().inject(fragment);
    }

    @Override
    public void inject(GoalPreviewFragment fragment) {
        getActivityComponent().inject(fragment);
    }

    @Override
    public void inject(SavedGoalsFragment fragment) {
        getActivityComponent().inject(fragment);
    }

    @Override
    public void inject(GoalDetailViewPagerFragment fragment) {
        getActivityComponent().inject(fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDialogFragment != null) {
            didGainFocus(binding.viewpager.getCurrentItem());
        }
        mDialogFragment = null;
    }

    @Override
    public void preview(int pos) {
        dismissDialogIfNotNull();
        mDialogFragment = GoalDetailViewPagerFragment.newInstance(pos);
        showDetailFragment();
    }

    @Override
    public void preview(GoalViewHolder holder, int pos) {
        dismissDialogIfNotNull();
        mDialogFragment = GoalDetailViewPagerFragment.newInstance(pos);
        showDetailFragmentWithTransition(holder);
    }

    @Override
    public void compose(final Goal goal) {
        dismissDialogIfNotNull();
        mIdeaInteractor.setPendingIdeas(goal);
        binding.viewpager.setCurrentItem(MY_IDEAS);
    }

    private void compose(String listId) {
        // load existing list
        dismissDialogIfNotNull();
        mIdeaInteractor.loadExternalPlan(listId);

        mDialogFragment = IdeaListFragment.newInstance();
        showFragment();
    }

    @Override
    public void search(Plan plan) {
        mGoalInteractor.searchGoalByIdeas(plan.getIdeas());
        binding.viewpager.setCurrentItem(SEARCH_GOAL);
        dismissDialogIfNotNull();
    }

    private void showDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rlHome, mDialogFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDetailFragmentWithTransition(GoalViewHolder holder) {
        int curr = binding.viewpager.getCurrentItem();
        GoalSearchFragment exitFragment = (GoalSearchFragment) mPagerAdapter.getItem(curr);

        // Setup exit transition on first fragment
        exitFragment.setSharedElementReturnTransition(mChangeTransform);
        exitFragment.setExitTransition(mFadeTransform);

        // Setup enter transition on second fragment
        mDialogFragment.setSharedElementEnterTransition(mChangeTransform);
        mDialogFragment.setEnterTransition(mFadeTransform);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rlHome, mDialogFragment)
                .addSharedElement(holder.binding.ivGoalImage,
                        getString(R.string.transition_goal_image))
                .addSharedElement(holder.binding.tvGoalIndex,
                        getString(R.string.transition_goal_index))
                .addSharedElement(holder.binding.tvGoalTitle,
                        getString(R.string.transition_goal_title))
                .addToBackStack(null)
                .commit();
    }

    private void showFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rlHome, mDialogFragment)
                .addToBackStack(null)
                .commit();
    }

    private void handleDeeplink() {
        Intent intent = getIntent();
        final Uri url = intent.getData();
        if (url == null) {
            return;
        }
        String listId = url.getPath().substring(1);
        if (listId == null) {
            return;
        }
        compose(listId);
    }

    private void dismissDialogIfNotNull() {
        if (mDialogFragment != null) {
            onBackPressed();
        }
    }

    private void didGainFocus(int position) {
        switch (position) {
            case SEARCH_GOAL:
                mGoalInteractor.setDisplayGoalFlag(R.id.flag_explore_recipes);
                mPagerAdapter.getGoalSearchFragment().didGainFocus();
                break;
            case SAVED_GOALS:
                mGoalInteractor.setDisplayGoalFlag(R.id.flag_saved_recipes);
                mPagerAdapter.getSavedGoalFragment().didGainFocus();
                break;
            case MY_IDEAS:
                mIdeaInteractor.loadPlan(mIdeaInteractor.myPlanId(), mEmptyPlanObserver);
                mPagerAdapter.getMyIdeasFragment().didGainFocus();
                break;
        }
        invalidateOptionsMenu();
    }

    private MainActivitySubComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent =
                    ((GarconApplication) getApplication()).getPresentationLayerComponent()
                            .newMainActivitySubComponent(
                                    new MainActivityModule(this, R.id.idea_category_recipe));
        }
        return mActivityComponent;
    }
}
