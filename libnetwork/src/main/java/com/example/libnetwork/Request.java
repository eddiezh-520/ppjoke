package com.example.libnetwork;



import android.util.Log;

import androidx.annotation.IntDef;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//T is response , R is Request
public abstract class Request<T,R extends Request> {
    private static final String TAG = "Request";
    protected String mUrl;

    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    //仅仅只访问本地缓存,即便缓存不存在，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    //先访问本地缓存，同时进行网络请求，请求成功后缓存到本地
    public static final int CACHE_FIRST= 2;
    //仅仅访问网络，不进行任何存储操作
    public static final int NET_ONLY = 3;
    //先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;
    private String cacheKey;

    protected Type mType;
    protected Class mClaz;

    @IntDef({CACHE_ONLY,CACHE_FIRST,NET_ONLY,NET_CACHE})
    public @interface CacheStrategy{

    }

    public Request(String url) {
         mUrl = url;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R)this;
    }

    public R addParam(String key,Object value) {
        try {
            //Object just allow 8 numbers primitive type
            Field field = value.getClass().getField("TYPE");
            Class claz= (Class)field.get(null);
            if (claz.isPrimitive()) {
                params.put(key, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R)this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R)this;
    }

    public R responseType(Type type){
        mType = type;
        return (R)this;
    }

    public R responseType(Class claz){
        mClaz = claz;
        return (R)this;
    }

    //传callback 异步请求，不传callback 同步请求

    /**
     * 异步请求
     * @param callback
     */
    public void execute(JsonCallback<T> callback) {
        getCall().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApiResponse<T> response = new ApiResponse<>();
                response.message = e.getMessage();
                callback.onError(response);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //这里callback的作用是通过泛型，知道他的类型，解析出真正的对象model
                ApiResponse<T> apiResponse = parseResponse(response,callback);
                if (apiResponse.success) {
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onError(apiResponse);
                }
            }
        });
    }

    /**
     * 同步请求
     */
    public ApiResponse<T> execute() {
        try{
            Response response = getCall().execute();
            ApiResponse<T> result = parseResponse(response,null);
            return result;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Call getCall(){
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    private ApiResponse<T> parseResponse(Response response,JsonCallback<T> callback) {
        boolean success = response.isSuccessful();
        int status = response.code();
        String message = response.message();
        ApiResponse<T> result = new ApiResponse<>();
        Convert sConvert = ApiService.sConvert;
        try {
            String content = response.body().toString();
            if (success) {
                if (callback != null) {
                    //get the callback model type
                    ParameterizedType type= (ParameterizedType)callback.getClass().getGenericSuperclass();
                    Type actualTypeArguments = type.getActualTypeArguments()[0];
                    //把成功返回的body放进去convert
                    result.body = (T)sConvert.convert(content, actualTypeArguments);
                } else if (mType != null) {
                    result.body = (T)sConvert.convert(content, mType);
                } else if (mClaz != null) {
                    result.body = (T)sConvert.convert(content, mClaz);
                } else {
                    Log.e(TAG, "parseResponse: 无法解析");
                }
            } else {
                message = content;
            }
        }catch (Exception e){
            message = e.getMessage();
            success = false;
        }

        result.success = success;
        result.status = status;
        result.message = message;
        return result;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(),entry.getValue());
        }
    };
}
