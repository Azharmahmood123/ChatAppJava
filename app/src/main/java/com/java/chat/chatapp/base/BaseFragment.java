package com.java.chat.chatapp.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.java.chat.chatapp.R;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment implements View.OnClickListener {

    protected FragmentActivity mActivity;

    protected VB binding;

    protected View rootView;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = getViewBinding(container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        getterSetterValue();
        initializeToolBar();
        initializeControls();
        attachListeners();
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {

    }

    protected abstract VB getViewBinding(ViewGroup container);

    protected void getterSetterValue() {
    }

    protected void initializeToolBar() {
    }

    protected abstract void initializeControls();

    protected void attachListeners() {
    }

    protected void observeViewModel() {
    }

    public void addFragment(int slot, Fragment fragment, boolean addToBackStack, boolean isAnimated) {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (isAnimated)
            transaction.setCustomAnimations(
                    R.anim.activity_open_enter,
                    R.anim.activity_open_exit,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.add(slot, fragment, fragment.getTag());
        transaction.commitAllowingStateLoss();

    }

    protected <VMC extends ViewModel> VMC createViewModel(@NonNull Class<VMC> viewModelClass) {
        return new ViewModelProvider(this).get(viewModelClass);
    }
}
