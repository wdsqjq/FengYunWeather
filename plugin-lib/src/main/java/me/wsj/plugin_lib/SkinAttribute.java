package me.wsj.plugin_lib;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

import me.wsj.plugin_lib.utils.ResourceManager;
import me.wsj.plugin_lib.utils.SkinThemeUtils;

/**
 * 这里面放了所有要换肤的view所对应的属性
 */
public class SkinAttribute {
    private static final List<String> mAttributes = new ArrayList<>();

    private static final String USE_PLUGIN_SKIN = "use_plugin_skin";
    private static final String USE_PLUGIN_SKIN_ATTR = "use_plugin_skin_attr";

    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableLeftCompat");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableTopCompat");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableRightCompat");
        mAttributes.add("drawableBottom");
        mAttributes.add("drawableBottomCompat");
    }

    // 记录换肤需要操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    // 记录下一个View身上哪几个属性需要换肤textColor/src
    public void load(View view, AttributeSet attrs) {
        boolean usePluginSkin = false;

        List<SkinPair> mSkinPars = new ArrayList<>();
        // 实现SkinViewSupport的不用遍历属性即可
        if (!(view instanceof SkinViewSupport)) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                // 获得属性名  textColor/background
                String attributeName = attrs.getAttributeName(i);
                if (attributeName.equals(USE_PLUGIN_SKIN)) {
//                    LogUtil.e("view: " + view + " -attributeName: " + attributeName);
                    usePluginSkin = true;
                    continue;
                }
                if (mAttributes.contains(attributeName)) {
                    // #
                    // ?722727272
                    // @722727272
                    String attributeValue = attrs.getAttributeValue(i);
                    // 比如color 以#开头表示写死的颜色 不可用于换肤
                    if (attributeValue.startsWith("#")) {
                        continue;
                    }
                    int resId;
                    // 以?开头的表示使用属性
                    if (attributeValue.startsWith("?")) {
                        int attrId = Integer.parseInt(attributeValue.substring(1));
                        resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                    } else {
                        // 正常以 @ 开头
                        resId = Integer.parseInt(attributeValue.substring(1));
                    }

                    SkinPair skinPair = new SkinPair(attributeName, resId);
                    mSkinPars.add(skinPair);
                }
            }

            /*String attributeValue = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", USE_PLUGIN_SKIN_ATTR);
            if (attributeValue == null) {
                return;
            }*/
        }

        if (usePluginSkin && !mSkinPars.isEmpty() || view instanceof SkinViewSupport) {
            SkinView skinView = new SkinView(view, mSkinPars);
            // 如果选择过皮肤 ，调用 一次 applySkin 加载皮肤的资源
//            Log.e(SkinManager.TAG, "pre apply -> " + view.getClass().getSimpleName());
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }


    /**
     * 对所有的view中的所有的属性进行皮肤修改
     */
    public void applySkin() {
        for (SkinView mSkinView : mSkinViews) {
            mSkinView.applySkin();
        }
    }

    static class SkinView {
        View view;
        // 这个View的能被 换肤的属性与它对应的id 集合
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        /**
         * 对一个View中的所有的属性进行修改
         */
        public void applySkin() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
                return;
            }
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = ResourceManager.getInstance().getBackground(skinPair.resId);
                        //背景可能是 @color 也可能是 @drawable
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = ResourceManager.getInstance().getBackground(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        if (view instanceof TextView) {
                            ((TextView) view).setTextColor(ResourceManager.getInstance().getColorStateList(skinPair.resId));
                        }
                        break;
                    case "drawableLeft":
                    case "drawableLeftCompat":
                        left = ResourceManager.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                    case "drawableTopCompat":
                        top = ResourceManager.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                    case "drawableRightCompat":
                        right = ResourceManager.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                    case "drawableBottomCompat":
                        bottom = ResourceManager.getInstance().getDrawable(skinPair.resId);
                        break;
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }
    }

    static class SkinPair {
        //属性名
        String attributeName;
        //对应的资源id
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
