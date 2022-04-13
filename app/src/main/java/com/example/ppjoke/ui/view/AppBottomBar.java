package com.example.ppjoke.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ppjoke.R;
import com.example.ppjoke.model.BottomBar;
import com.example.ppjoke.model.Destination;
import com.example.ppjoke.utils.AppConfig;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppBottomBar extends BottomNavigationView {
    private static int[] sIcons = new int[]{R.drawable.icon_tab_home,R.drawable.icon_tab_sofa,R.drawable.icon_tab_publish,R.drawable.icon_tab_find,R.drawable.icon_tab_mine};
    public AppBottomBar(@NonNull @NotNull Context context) {
        this(context,null);
    }

    public AppBottomBar(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        BottomBar bottomBar = AppConfig.getBottomBar();
        List<BottomBar.Tab> tabs = bottomBar.tabs;

        //设置底部按钮被选中和未被选中的颜色
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(bottomBar.activeColor),Color.parseColor(bottomBar.inActiveColor)};
        ColorStateList colorStateList = new ColorStateList(states,colors);

        setItemIconTintList(colorStateList);
        setItemTextColor(colorStateList);
        setLabelVisibilityMode(LABEL_VISIBILITY_LABELED);
        //设置默认选中的按钮
        setSelectedItemId(bottomBar.selectTab);


        for (int i = 0 ; i < tabs.size() ; i++) {
            BottomBar.Tab tab = tabs.get(i);
            if (!tab.enable) {
                return;
            }
            int id = getId(tab.pageUrl);
            if (id < 0) {
                return;
            }

            MenuItem menuItem = getMenu().add(0, id, tab.index, tab.title);
            //设置d底部menu的icon
            menuItem.setIcon(sIcons[tab.index]);
        }
        //改变每个按钮的大小，必须等每个按钮添加到导航栏之后，才可以改变，所以改变按钮的操作需要用另一个for循环，而不是在同一个for循环一起做了
        for (int i = 0; i < tabs.size(); i++) {
            BottomBar.Tab tab = tabs.get(i);
            int iconSize = dp2px(tab.size);
            BottomNavigationMenuView menuView = (BottomNavigationMenuView)getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView)menuView.getChildAt(tab.index);
            itemView.setIconSize(iconSize);

            //为中间那个没有title的底部按钮设置着色
            if (TextUtils.isEmpty(tab.title)) {
                Log.d("AppBottomBar","tab pageUrl:" + tab.pageUrl);
                Log.d("AppBottomBar","tab tintColor:" + tab.tintColor);
                int tintColor = TextUtils.isEmpty(tab.tintColor) ? Color.parseColor("#ff678f") : Color.parseColor(tab.tintColor);
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                //让中间按钮不可以上下浮动
                itemView.setShifting(false);
            }
        }

    }

    private int dp2px(int size) {
        float value = getContext().getResources().getDisplayMetrics().density * size + 0.5f;
        return (int)value;
    }

    private int getId(String pageUrl) {
        Destination destination = AppConfig.getDestConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.id;
    }
}
