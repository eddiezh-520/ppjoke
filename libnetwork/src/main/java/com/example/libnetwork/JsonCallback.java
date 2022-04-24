package com.example.libnetwork;

//不写成interface的原因是可以选择性的复写方法，用interface就不可以选择性的复写方法
public abstract class JsonCallback<T> {

    public void onSuccess(ApiResponse<T> response) {

    }

    public void onError(ApiResponse<T> response) {

    }

    public void onCacheSuccess(ApiResponse<T> response) {

    }
}
