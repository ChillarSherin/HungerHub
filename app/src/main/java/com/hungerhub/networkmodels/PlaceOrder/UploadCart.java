package com.hungerhub.networkmodels.PlaceOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadCart {
    @SerializedName("itemID")
    @Expose
    private String itemID;
    @SerializedName("outletConfigID")
    @Expose
    private String outletConfigID;
    @SerializedName("orderSessionTimingID")
    @Expose
    private String orderSessionTimingID;
    @SerializedName("orderCategoryID")
    @Expose
    private String orderCategoryID;
    @SerializedName("itemAmount")
    @Expose
    private String itemAmount;
    @SerializedName("itemQuantity")
    @Expose
    private String itemQuantity;


    @SerializedName("itemPrice")
    @Expose
    private String itemPrice;
    @SerializedName("itemSGSTPrice")
    @Expose
    private String itemSGSTPrice;
    @SerializedName("itemCGSTPrice")
    @Expose
    private String itemCGSTPrice;

    @SerializedName("itemLiveStatus")
    @Expose
    private String itemLiveStatus;



    public UploadCart(String itemID, String outletConfigID, String orderSessionTimingID, String orderCategoryID, String itemAmount, String itemQuantity,String itemPrice,String itemSGSTPrice,String itemCGSTPrice ,String itemLiveStatus ) {
        this.itemID = itemID;
        this.outletConfigID = outletConfigID;
        this.orderSessionTimingID = orderSessionTimingID;
        this.orderCategoryID = orderCategoryID;
        this.itemAmount = itemAmount;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
        this.itemSGSTPrice = itemSGSTPrice;
        this.itemCGSTPrice = itemCGSTPrice;
        this.itemLiveStatus = itemLiveStatus;
    }

    public UploadCart() {
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemSGSTPrice() {
        return itemSGSTPrice;
    }

    public void setItemSGSTPrice(String itemSGSTPrice) {
        this.itemSGSTPrice = itemSGSTPrice;
    }

    public String getItemCGSTPrice() {
        return itemCGSTPrice;
    }

    public void setItemCGSTPrice(String itemCGSTPrice) {
        this.itemCGSTPrice = itemCGSTPrice;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getOutletConfigID() {
        return outletConfigID;
    }

    public void setOutletConfigID(String outletConfigID) {
        this.outletConfigID = outletConfigID;
    }

    public String getOrderSessionTimingID() {
        return orderSessionTimingID;
    }

    public void setOrderSessionTimingID(String orderSessionTimingID) {
        this.orderSessionTimingID = orderSessionTimingID;
    }

    public String getOrderCategoryID() {
        return orderCategoryID;
    }

    public void setOrderCategoryID(String orderCategoryID) {
        this.orderCategoryID = orderCategoryID;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
