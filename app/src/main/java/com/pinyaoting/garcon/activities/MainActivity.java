package com.pinyaoting.garcon.activities;

import static com.pinyaoting.garcon.adapters.HomeFragmentPagerAdapter.MY_IDEAS;
import static com.pinyaoting.garcon.adapters.HomeFragmentPagerAdapter.SAVED_GOALS;
import static com.pinyaoting.garcon.adapters.HomeFragmentPagerAdapter.SEARCH_GOAL;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.batch.android.Batch;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.actions.ListFragmentActionHandler;
import com.pinyaoting.garcon.adapters.HomeFragmentPagerAdapter;
import com.pinyaoting.garcon.adapters.IdeaSuggestionsAdapter;
import com.pinyaoting.garcon.application.GarconApplication;
import com.pinyaoting.garcon.databinding.ActivityMainBinding;
import com.pinyaoting.garcon.dependencies.components.presentation.MainActivitySubComponent;
import com.pinyaoting.garcon.dependencies.modules.presentation.MainActivityModule;
import com.pinyaoting.garcon.fragments.GoalDetailViewPagerFragment;
import com.pinyaoting.garcon.fragments.GoalPreviewFragment;
import com.pinyaoting.garcon.fragments.GoalSearchFragment;
import com.pinyaoting.garcon.fragments.ListCompositionFragment;
import com.pinyaoting.garcon.fragments.SavedGoalsFragment;
import com.pinyaoting.garcon.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalDetailActionHandlerInterface;
import com.pinyaoting.garcon.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcon.interfaces.presentation.InjectorInterface;
import com.pinyaoting.garcon.utils.TabUtils;
import com.pinyaoting.garcon.utils.ToolbarBindingUtils;
import com.pinyaoting.garcon.view.AutoCompleteSearchView;
import com.pinyaoting.garcon.viewholders.GoalViewHolder;
import com.pinyaoting.garcon.viewstates.Goal;
import com.pinyaoting.garcon.viewstates.Plan;

import java.util.Arrays;

import javax.inject.Inject;

import rx.Observer;

public class MainActivity extends AppCompatActivity implements InjectorInterface,
        GoalActionHandlerInterface.PreviewHandlerInterface,
        GoalDetailActionHandlerInterface.ListCompositionDialogHandlerInterface,
        ListFragmentActionHandler.IdeaShareHandlerInterface {

    public static final int RC_SIGN_IN = 1;
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
        ToolbarBindingUtils.bind(this, binding.activityMainToolbarContainer.toolbar);
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
        mGoalInteractor.search(null);

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
                    Snackbar.make(binding.viewpager,
                            R.string.create_grocery_snackbar_hint,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        };
    }

    public MainActivitySubComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent =
                    ((GarconApplication) getApplication()).getPresentationLayerComponent()
                            .newListCompositionActivitySubComponent(
                                    new MainActivityModule(this, R.id.idea_category_recipe_v2));
        }
        return mActivityComponent;
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

        mDialogFragment = ListCompositionFragment.newInstance();
        binding.activityMainToolbarContainer.toolbarTitle.setText(
                getString(R.string.create_grocery_hint));
        binding.activityMainToolbarContainer.toolbarTitle.setTextSize(
                getResources().getInteger(R.integer.toolbar_title_size));
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
                .replace(R.id.activity_home, mDialogFragment)
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
                .replace(R.id.activity_home, mDialogFragment)
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
                .replace(R.id.activity_home, mDialogFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Batch.onNewIntent(this, intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Batch.onStart(this);
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
    public void share(Intent i) {
        startActivity(i);
    }

    @Override
    public void inject(ListCompositionFragment fragment) {
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
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem == null) {
            return true;
        }
        final AutoCompleteSearchView searchView =
                (AutoCompleteSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setAdapter(new IdeaSuggestionsAdapter(
                MainActivity.this, mIdeaInteractor));
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mIdeaInteractor.acceptSuggestedIdeaAtPos(position);
                searchView.setQuery("", false);
                searchView.clearFocus();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mDialogFragment != null && mDialogFragment instanceof ListCompositionFragment) {
                    if (mIdeaInteractor.getSuggestionCount() < 1) {
                        return true;
                    }
                    mIdeaInteractor.acceptSuggestedIdeaAtPos(0);
                    searchView.setQuery("", false);
                    return true;
                }
                int currentViewPagerIndex = binding.viewpager.getCurrentItem();
                switch (currentViewPagerIndex) {
                    case SEARCH_GOAL:
                        mGoalInteractor.search(query);
                        break;
                    case MY_IDEAS:
                        break;
                    case SAVED_GOALS:
                        break;
                }

                // WORKAROUND: to avoid issues with some emulators and keyboard devices
                // firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String query = s.trim();
                if (query.isEmpty()) {
                    return true;
                }
                if (mDialogFragment != null && mDialogFragment instanceof ListCompositionFragment) {
                    // show auto complete for ingredients
                    mIdeaInteractor.getSuggestions(query);
                    return true;
                } else {
                    // TODO: add auto complete for recipes
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        configureTitle(position);
        processData(position);
    }

    private void configureTitle(int position) {
        binding.activityMainToolbarContainer.appBar.setExpanded(true, true);
        String title = getString(R.string.app_name);
        float titleSize = getResources().getInteger(R.integer.app_title_size);
        switch (position) {
            case SEARCH_GOAL:
                mGoalInteractor.setDisplayGoalFlag(R.id.flag_explore_recipes);
                title = getString(R.string.app_name);
                titleSize = getResources().getInteger(R.integer.app_title_size);
                break;
            case MY_IDEAS:
                title = getString(R.string.my_ideas_hint);
                titleSize = getResources().getInteger(R.integer.toolbar_title_size);
                break;
            case SAVED_GOALS:
                mGoalInteractor.setDisplayGoalFlag(R.id.flag_saved_recipes);
                title = getString(R.string.saved_goals_hint);
                titleSize = getResources().getInteger(R.integer.toolbar_title_size);
                break;
            default:
                break;
        }
        binding.activityMainToolbarContainer.toolbarTitle.setText(title);
        binding.activityMainToolbarContainer.toolbarTitle.setTextSize(titleSize);
    }

    private void processData(int position) {
        switch (position) {
            case MY_IDEAS:
                mIdeaInteractor.loadPlan(mIdeaInteractor.myPlanId(), mEmptyPlanObserver);
                break;
        }
    }
}
