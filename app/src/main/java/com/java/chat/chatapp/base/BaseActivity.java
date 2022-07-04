package com.java.chat.chatapp.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.java.chat.chatapp.R;

import java.util.List;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity implements View.OnClickListener {

    protected VB binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewBinding();
        setContentView(binding.getRoot());
        initializeToolBar();
        initializeControls();
        attachListeners();
        observeViewModel();
    }

    protected abstract VB getViewBinding();

    protected void initializeToolBar() {
    }

    protected abstract void initializeControls();

    protected void attachListeners() {
    }

    protected void observeViewModel() {
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onClick(View view) {

    }

    protected <VMC extends ViewModel> VMC createViewModel(@NonNull Class<VMC> viewModelClass) {
        return new ViewModelProvider(this).get(viewModelClass);
    }

    public void replaceFragment(int slot, Fragment fragment, boolean addToBackStack, boolean isAnimated) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isAnimated)
            transaction.setCustomAnimations(
                    R.anim.activity_open_enter,
                    R.anim.activity_open_exit,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(slot, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public Fragment getCurrentTopFragment(FragmentActivity activity) {
        if (activity == null)
            return null;
        FragmentManager manager = activity.getSupportFragmentManager();
        List<Fragment> fragmentList = manager.getFragments();
        if (fragmentList.size() > 0) {
            for (int i = fragmentList.size() - 1; i > -1; i--) {
                Fragment fragment = fragmentList.get(i);
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void startSpecificActivity(Class<?> otherActivityClass) {
        Intent intent = new Intent(BaseActivity.this, otherActivityClass);
        startActivity(intent);
    }

}
