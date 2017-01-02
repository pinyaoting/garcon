package com.pinyaoting.garcondecuisine.interfaces.presentation;

public interface IdeaListActionHandlerInterface {

    void onShareButtonClick();

    void onSearchButtonClick();

    void onNearbyStoreButtonClick();

    void onEmptyButtonClick();

    void onIncreaseQuantity(int pos);

    void onDecreaseQuantity(int pos);

}
