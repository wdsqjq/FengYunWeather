package me.wsj.lib.net.callback;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import me.wsj.lib.bean.BaseBean;
import per.wsj.commonlib.net.ParameterizedTypeImpl;

public abstract class HttpCallback<T> implements CallBack<T> {

    @Override
    public void onNext(String responseBody) {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Type ty = new ParameterizedTypeImpl(BaseBean.class, new Type[]{types[0]});
            BaseBean<T> data = new Gson().fromJson(responseBody, ty);
            onSuccess(data.getResult(), "" + data.getCode(), data.getMsg());
        } else {
            throw new ClassCastException();
        }
    }
}