package com.saneforce.milksales.Interface;

public interface UpdateResponseUI {
    void onLoadDataUpdateUI(String apiDataResponse, String key);
    default void onErrorData(String msg){}
}