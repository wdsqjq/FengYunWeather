package me.wsj.fengyun.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public abstract class BaseVmActivity<T extends ViewBinding, V extends ViewModel> extends BaseActivity<T> {

    protected V viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
//        autoBindView();
        super.init();
    }

    private void autoBindView() {
        Class<T> vClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            Method inflate = vClass.getMethod("inflate", LayoutInflater.class);
            mBinding = (T) inflate.invoke(null,getLayoutInflater());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Class<V> getViewModelClass() {
        Class<V> xClass = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        return xClass;
    }

    @Override
    public T bindView() {

        return null;
    }
}
