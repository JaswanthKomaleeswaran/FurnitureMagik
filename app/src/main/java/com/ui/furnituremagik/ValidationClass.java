package com.ui.furnituremagik;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ValidationClass {
    public boolean mobileValidation(String mobilNumber)
    {

        if(mobilNumber.matches("[0-9]{10}$"))
        {
            return true;
        }
        return false;
    }


    public boolean priceValidation(String price)
    {

        if((price.matches("^[0-9]+(\\.?[0-9]{1,2})?+$")))
        {
            return true;
        }
        return false;
    }

    public boolean codeValidation(String code)
    {

        if(code.matches("[0-9]{6}$"))
        {
            return true;
        }
        return false;
    }

    public boolean getMobileDataState(ConnectivityManager connectivityManager)
    {

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
