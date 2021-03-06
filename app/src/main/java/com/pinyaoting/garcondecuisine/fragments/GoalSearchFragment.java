package com.pinyaoting.garcondecuisine.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.databinding.FragmentGoalSearchBinding;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.GoalInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.InjectorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.ViewState;
import com.pinyaoting.garcondecuisine.utils.ConstantsAndUtils;
import com.pinyaoting.garcondecuisine.utils.ImageUtils;
import com.pinyaoting.garcondecuisine.utils.ToolbarUtils;
import com.pinyaoting.garcondecuisine.view.AutoCompleteSearchView;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observer;

public class GoalSearchFragment extends Fragment {

    FragmentGoalSearchBinding binding;
    @Inject
    GoalActionHandlerInterface mActionHandler;
    @Inject
    @Named("Goal")
    RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    @Inject
    GoalInteractorInterface mGoalInteractor;
    Observer<ViewState> mViewStateObserver;

    public GoalSearchFragment() {
        // Required empty public constructor
    }

    public static GoalSearchFragment newInstance() {
        GoalSearchFragment fragment = new GoalSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_goal_search, container, false);
        if (getActivity() instanceof InjectorInterface) {
            InjectorInterface injector = (InjectorInterface) getActivity();
            injector.inject(this);
        }
        didGainFocus((AppCompatActivity) getActivity());
        binding.rvIdeaSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvIdeaSearchResults.setAdapter(mAdapter);
        binding.rvIdeaSearchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrollY = binding.rvIdeaSearchResults.computeVerticalScrollOffset();
                float max = 0;
                float translationY = Math.min(max, -scrollY);
                binding.flGoalSearchBackground.setTranslationY(translationY);
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.line_divider));
        binding.rvIdeaSearchResults.addItemDecoration(dividerItemDecoration);
        if (ConstantsAndUtils.getAndroidSDKVersion() >= ConstantsAndUtils.LATEST) {
            // NOTE: disabled image rotation on low end device for better performance
            ImageUtils.loadDefaultImageRotation(binding.ivIdeaSearchBackground);
        } else {
            binding.ivIdeaSearchBackground.setImageResource(R.drawable.background_0);
        }

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (mGoalInteractor.getDisplayGoalFlag()) {
                    case R.id.flag_explore_recipes:
                        mGoalInteractor.search(null);
                        break;
                    default:
                        binding.swipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        });
        // Configure the refreshing colors
        binding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mViewStateObserver = new Observer<ViewState>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ViewState viewState) {
                if (viewState.getState() == R.id.state_loaded) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        mGoalInteractor.subscribeToGoalStateChange(mViewStateObserver);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int padding = ImageUtils.topImagePadding(getActivity().getWindowManager(), getResources());
        binding.rvIdeaSearchResults.setPadding(0, padding, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mGoalInteractor.unsubscribeFromGoalStateChange(mViewStateObserver);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_goal_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem == null) {
            return;
        }
        final AutoCompleteSearchView searchView =
                (AutoCompleteSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.goal_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mGoalInteractor.search(query);
                // WORKAROUND: to avoid issues with some emulators and keyboard devices
                // firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ToolbarUtils.onOptionsItemSelected(getActivity(), item) ||
                super.onOptionsItemSelected(item);
    }

    public void didGainFocus(AppCompatActivity activity) {
        if (binding == null || binding.activityMainToolbarContainer == null ||
                binding.activityMainToolbarContainer.toolbar == null) {
            return;
        }
        ToolbarUtils.bind(activity, binding.activityMainToolbarContainer.toolbar);
        ToolbarUtils.configureTitle(binding.activityMainToolbarContainer,
                getString(R.string.app_name), getResources().getInteger(R.integer.app_title_size));
    }
}
