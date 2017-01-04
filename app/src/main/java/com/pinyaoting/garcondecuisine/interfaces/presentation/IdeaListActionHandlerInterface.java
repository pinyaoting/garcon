package com.pinyaoting.garcondecuisine.interfaces.presentation;

import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

public interface IdeaListActionHandlerInterface {

    void onShareButtonClick(View v, FloatingActionsMenu floatingActionsMenu);

    void onSearchButtonClick(View v, FloatingActionsMenu floatingActionsMenu);

    void onNearbyStoreButtonClick(View v, FloatingActionsMenu floatingActionsMenu);

    void onEmptyButtonClick();

    void onIncreaseQuantity(int pos);

    void onDecreaseQuantity(int pos);

}
