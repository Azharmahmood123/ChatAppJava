package com.java.chat.chatapp.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {
    private final MutableLiveData<Event<String>> mErrorText = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> mDataLoading = new MutableLiveData<>();

    public LiveData<Event<String>> errorText = mErrorText;
    public LiveData<Event<Boolean>> dataLoading = mDataLoading;

    protected <T> void onResult(MutableLiveData<T> mutableLiveData, @NonNull Result<T> result) {
        switch (result.states) {
            case LOADING:
                mDataLoading.setValue(new Event<>(true));
                break;
            case ERROR:
                mDataLoading.setValue(new Event<>(false));
                String errorMsg = result.errorMessage;
                if (errorMsg != null) {
                    mErrorText.setValue(new Event<>(errorMsg));
                }
                break;
            default:
                mDataLoading.setValue(new Event<>(false));
                if (result.data != null && mutableLiveData != null) {
                    mutableLiveData.setValue(result.data);
                }
                break;
        }
    }
}
