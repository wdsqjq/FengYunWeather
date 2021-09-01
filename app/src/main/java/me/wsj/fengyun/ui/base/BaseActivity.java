package me.wsj.fengyun.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import me.wsj.fengyun.R;
import me.wsj.lib.dialog.LoadingDialog;

public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity implements CreateInit<T> {

    private FrameLayout layout_content; // 子类view容器

    protected Context context;

    private LoadingDialog loadingDialog;

    protected T mBinding;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.setContentView(R.layout.activity_base);
        context = this;
        init();
    }

    protected void init() {
        Toolbar toolbar = super.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout_content = super.findViewById(R.id.frameLayout);

        mBinding = bindView();
        setContentView(mBinding.getRoot());

        // 返回键显示默认图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);

        // 初始化数据
        prepareData(getIntent());
        // 初始化view
        initView();
        // 初始化事件
        initEvent();
        // 加载数据
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 初始化数据
        prepareData(intent);
    }

    public void hideTitleBar() {
        getSupportActionBar().hide();
    }

    // 设置标题
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * @param view
     */
    public void setContentView(View view) {
        layout_content.removeAllViews();
        layout_content.addView(view);
    }

    /**
     * 状态栏透明
     */
    protected void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showLoading(boolean show) {
        showLoading(show, null);
    }

    protected void showLoading(boolean show, String tip) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        if (show) {
            if (tip != null && !tip.isEmpty()) {
                loadingDialog.setTip(tip);
            } else {
                loadingDialog.setTip("请稍后...");
            }
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    /*
    @Override
    public Resources getResources() {
        // 本地语言设置
        String language = Locale.getDefault().getLanguage();
        Resources resources = super.getResources();
        Configuration configuration = resources.getConfiguration();
        if (ContentUtil.APP_SETTING_LANG.equals("en")) {
            configuration.locale = Locale.ENGLISH;// 英语
            ContentUtil.SYS_LANG = "en";
        } else {
            if (ContentUtil.APP_SETTING_LANG.equals("sys") && !"zh".equals(language)) {
                configuration.locale = Locale.ENGLISH;// 英语
                ContentUtil.SYS_LANG = "en";
            } else {
                configuration.locale = Locale.SIMPLIFIED_CHINESE;// 简体中文
                ContentUtil.SYS_LANG = "zh";
            }
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }*/

}
