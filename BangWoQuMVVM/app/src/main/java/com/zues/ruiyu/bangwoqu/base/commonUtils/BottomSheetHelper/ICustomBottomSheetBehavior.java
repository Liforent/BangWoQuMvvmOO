package com.zues.ruiyu.bangwoqu.base.commonUtils.BottomSheetHelper;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.util.List;

public interface ICustomBottomSheetBehavior<V extends View> {
    void setNestedScrollingChildRefList(List<View> nestedScrollingChildRefList);

    boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection);
}
