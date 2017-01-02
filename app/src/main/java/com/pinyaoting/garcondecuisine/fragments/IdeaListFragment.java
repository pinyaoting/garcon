package com.pinyaoting.garcondecuisine.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.AdapterView;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.adapters.IdeaSuggestionsAdapter;
import com.pinyaoting.garcondecuisine.databinding.FragmentIdeaListBinding;
import com.pinyaoting.garcondecuisine.interfaces.domain.IdeaInteractorInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.IdeaListActionHandlerInterface;
import com.pinyaoting.garcondecuisine.interfaces.presentation.InjectorInterface;
import com.pinyaoting.garcondecuisine.utils.ConstantsAndUtils;
import com.pinyaoting.garcondecuisine.utils.ItemClickSupport;
import com.pinyaoting.garcondecuisine.utils.ToolbarUtils;
import com.pinyaoting.garcondecuisine.view.AutoCompleteSearchView;
import com.pinyaoting.garcondecuisine.viewstates.Idea;

import javax.inject.Inject;
import javax.inject.Named;

import jp.wasabeef.blurry.Blurry;

public class IdeaListFragment extends Fragment {

    static final String LIST_COMPOSITION_VIEW_MODELS = "LIST_COMPOSITION_VIEW_MODELS";
    static final String LIST_COMPOSITION_LIST_ID = "LIST_COMPOSITION_LIST_ID";
    static final int LIST_COMPOSITION_BACKGROUND_IMAGE_ROTATION_INTERVAL = 5000;
    public static FragmentIdeaListBinding binding;
    @Inject
    @Named("Composition")
    RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    @Inject
    IdeaSuggestionsAdapter mSuggestionsAdapter;
    @Inject
    IdeaListActionHandlerInterface mActionHandler;
    @Inject
    IdeaInteractorInterface mIdeaInteractor;

    public IdeaListFragment() {
        // Required empty public constructor
    }

    public static IdeaListFragment newInstance() {
        IdeaListFragment fragment = new IdeaListFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_idea_list, container,
                false);
        if (getActivity() instanceof InjectorInterface) {
            InjectorInterface injector = (InjectorInterface) getActivity();
            injector.inject(this);
        }
        ToolbarUtils.configureTitle(
                binding.activityMainToolbarContainer,
                getString(R.string.my_ideas_hint),
                getResources().getInteger(R.integer.toolbar_title_size));
        binding.rvIdeas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvIdeas.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.line_divider_edge_to_edge));
        binding.rvIdeas.addItemDecoration(dividerItemDecoration);
        binding.setHandler(mActionHandler);
        ItemClickSupport.addTo(binding.rvIdeas).setOnItemLongClickListener(
                new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                mIdeaInteractor.removeIdea(position);
                return false;
            }
        }).setOnItemSwipeTouchListener(new ItemClickSupport.OnItemSwipeTouchListener() {
            @Override
            public void onSwipeRight(RecyclerView recyclerView, int position, View v) {
                Idea idea = mIdeaInteractor.getIdeaAtPos(position);
                if (idea.isCrossedOut()) {
                    return;
                }
                mIdeaInteractor.crossoutIdea(position);
            }

            @Override
            public void onSwipeLeft(RecyclerView recyclerView, int position, View v) {
                Idea idea = mIdeaInteractor.getIdeaAtPos(position);
                if (!idea.isCrossedOut()) {
                    return;
                }
                mIdeaInteractor.uncrossoutIdea(position);
            }

            @Override
            public void onSwipeUp(RecyclerView recyclerView, int position, View v) {

            }

            @Override
            public void onSwipeDown(RecyclerView recyclerView, int position, View v) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ConstantsAndUtils.getAndroidSDKVersion() >= ConstantsAndUtils.LATEST) {
            binding.ivIdeaCompositionBackground.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            binding.ivIdeaCompositionBackground.layout(
                    0, 0, binding.ivIdeaCompositionBackground.getMeasuredWidth(),
                    binding.ivIdeaCompositionBackground.getMeasuredHeight());

            Blurry.with(getContext())
                    .capture(binding.ivIdeaCompositionBackground)
                    .into(binding.ivIdeaCompositionBackground);
        }
    }

    @Override
    public void onDestroyView() {
        ItemClickSupport.removeFrom(binding.rvIdeas);
        mIdeaInteractor.discardPlanIfEmpty();
        mIdeaInteractor.clearPlan();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.multipleActions.collapse();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_idea_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem == null) {
            return;
        }
        final AutoCompleteSearchView searchView =
                (AutoCompleteSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setAdapter(mSuggestionsAdapter);
        searchView.setQueryHint(getString(R.string.idea_search_hint));
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
                if (mIdeaInteractor.getSuggestionCount() < 1) {
                    return true;
                }
                mIdeaInteractor.acceptSuggestedIdeaAtPos(0);
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String query = s.trim();
                if (query.isEmpty()) {
                    return true;
                }
                // show auto complete for ingredients
                mIdeaInteractor.getSuggestions(query);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_remove) {
            mActionHandler.onEmptyButtonClick();
            return true;
        }
        return ToolbarUtils.onOptionsItemSelected(getActivity(), item) ||
                super.onOptionsItemSelected(item);
    }

    public void didGainFocus() {
        if (binding == null || binding.activityMainToolbarContainer == null ||
                binding.activityMainToolbarContainer.toolbar == null) {
            return;
        }
        ToolbarUtils.bind((AppCompatActivity)getActivity(),
                binding.activityMainToolbarContainer.toolbar);
        binding.multipleActions.collapse();
    }
}
