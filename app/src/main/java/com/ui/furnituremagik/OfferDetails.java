package com.ui.furnituremagik;

import java.io.Serializable;

public class OfferDetails implements Serializable {

    String offerId,productName,productItem,price,discountPrice,path;

    OfferDetails(String offerId,String productName,String productItem,String price,String discountPrice,String path)
    {
        this.offerId=offerId;
        this.productName=productName;
        this.productItem=productItem;
        this.price=price;
        this.discountPrice=discountPrice;
        this.path=path;
    }
}
