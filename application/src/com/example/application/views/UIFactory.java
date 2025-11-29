package com.example.application.views;
public interface UIFactory {
    UIComponent createMainMenu();
    UIComponent createBookMenu();
    UIComponent createOrderMenu();
    UIComponent createRequestMenu();
    UIComponent createSettingMenu();
}
