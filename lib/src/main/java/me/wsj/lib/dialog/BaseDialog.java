package me.wsj.lib.dialog;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.viewbinding.ViewBinding;

import me.wsj.lib.R;

/**
 * dialog
 */

public abstract class BaseDialog<T extends ViewBinding> extends AppCompatDialog implements DialogInit<T> {

    private int mGravity = Gravity.CENTER;

    private float widthWeight = 0f;
    private float heightWeight = 0f;

    protected T mBinding;

    public BaseDialog(@NonNull Context context) {
        this(context, 0, 0);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BaseDialog(@NonNull Context context, float widthWeight, float heightWeight) {
        this(context, Gravity.CENTER, widthWeight, heightWeight);
    }

    public BaseDialog(@NonNull Context context, int gravity, float widthWeight, float heightWeight) {
        super(context, R.style.BaseDialogTheme);
        init(gravity, widthWeight, heightWeight);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDialog();
        initView();
        // 必须放在这里,不然通过构造方法传过去的之在该方法之后接收到
//        initData();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * 初始化
     */
    private void init(int gravity, float widthWeight, float heightWeight) {
        mBinding = bindView();

//        setContentView(LayoutInflater.from(getContext()).inflate(getLayout(), null));
        setContentView(mBinding.getRoot());

        mGravity = gravity;
        if (widthWeight <= 1f) {
            this.widthWeight = widthWeight;
        }

        if (heightWeight <= 1f) {
            this.heightWeight = heightWeight;
        }
    }

    /**
     * dialog初始化
     */
    private void initDialog() {
        // 设置宽度为屏宽、位置靠近屏幕底部
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager windowManager = window.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
//        window.setBackgroundDrawableResource(R.color.transparent);
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

        mBinding.getRoot().postInvalidate();
    }
}
