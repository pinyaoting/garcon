package com.pinyaoting.garcondecuisine.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.databinding.FragmentSavedGoalsBinding;
import com.pinyaoting.garcondecuisine.interfaces.presentation.InjectorInterface;
import com.pinyaoting.garcondecuisine.utils.ConstantsAndUtils;
import com.pinyaoting.garcondecuisine.utils.ToolbarUtils;

import javax.inject.Inject;
import javax.inject.Named;

import jp.wasabeef.blurry.Blurry;

public class SavedGoalsFragment extends Fragment {

    FragmentSavedGoalsBinding binding;
    @Inject
    @Named("SavedGoal")
    RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

    public SavedGoalsFragment() {
        // Required empty public constructor
    }

    public static SavedGoalsFragment newInstance() {
        SavedGoalsFragment fragment = new SavedGoalsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved_goals, container,
                false);
        ToolbarUtils.configureTitle(
                binding.activityMainToolbarContainer,
                getString(R.string.saved_goals_hint),
                getResources().getInteger(R.integer.toolbar_title_size));
        binding.rvSavedIdeas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSavedIdeas.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.line_divider_edge_to_edge));
        binding.rvSavedIdeas.addItemDecoration(dividerItemDecoration);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ConstantsAndUtils.getAndroidSDKVersion() >= ConstantsAndUtils.LATEST) {
            binding.ivSavedIdeasBackground.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            binding.ivSavedIdeasBackground.layout(
                    0, 0, binding.ivSavedIdeasBackground.getMeasuredWidth(),
                    binding.ivSavedIdeasBackground.getMeasuredHeight());

            Blurry.with(getContext())
                    .capture(binding.ivSavedIdeasBackground)
                    .into(binding.ivSavedIdeasBackground);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InjectorInterface) {
            InjectorInterface injector = (InjectorInterface) context;
            injector.inject(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_goal_review, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    }
}
