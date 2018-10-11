package com.moselo.HomingPigeon.API.View;

import com.moselo.HomingPigeon.Model.HpErrorModel;

/**
 * Created by Fadhlan on 6/15/17.
 */

public abstract class HpDefaultDataView<T> implements HpView<T> {
    @Override
    public void startLoading() {

    }

    @Override
    public void endLoading() {

    }

    @Override
    public void onEmpty(String message) {

    }

    @Override
    public void onSuccess(T response) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void onError(HpErrorModel error) {

    }

    @Override
    public void onError(String errorMessage) {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
