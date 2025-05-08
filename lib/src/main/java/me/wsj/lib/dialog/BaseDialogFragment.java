package me.wsj.lib.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;


/**
 *
 */
public abstract class BaseDialogFragment<T extends ViewBinding> extends DialogFragment implements DialogInit<T> {

    protected T mBinding;

    protected int mGravity = Gravity.CENTER;

    private float widthWeight = 0f;
    private float heightWeight = 0f;

    public BaseDialogFragment() {
        this(Gravity.CENTER);
    }

    /**
     * dialog
     *
     * @param gravity 在屏幕的位置
     */
    public BaseDialogFragment(int gravity) {
        this(gravity, 0f, 0f);
    }

    /**
     * dialog
     */
    public BaseDialogFragment(float widthWeight, float heightWeight) {
        this(Gravity.CENTER, widthWeight, heightWeight);
    }

    /**
     *
     */
    public BaseDialogFragment(int gravity, float widthWeight, float heightWeight) {
        mGravity = gravity;
        if (widthWeight <= 1f) {
            this.widthWeight = widthWeight;
        }

        if (heightWeight <= 1f) {
            this.heightWeight = heightWeight;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = bindView();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDialog();

        initView();
        initEvent();
    }

    private void initDialog() {
        // 设置宽度为屏宽、位置靠近屏幕底部
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }
        WindowManager windowManager = window.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = mGravity;
        Point displaySize = new Point();
        display.getSize(displaySize);

        if (widthWeight > 0f) {
            wlp.width = (int) (displaySize.x * widthWeight); //设置dialog宽度
        } else {
            wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        if (heightWeight > 0f) {
            wlp.height = (int) (displaySize.y * heightWeight); //设置dialog宽度
        } else {
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        window.setAttributes(wlp);
    }


}
