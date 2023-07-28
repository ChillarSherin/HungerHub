
package com.hungerhub.networkmodels.OrderItemsDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("itemID")
    @Expose
    private String itemID;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("outletName")
    @Expose
    private String outletName;
    @SerializedName("outletConfigID")
    @Expose
    private String outletConfigID;
    @SerializedName("itemPrice")
    @Expose
    private String itemPrice;
    @SerializedName("itemGSTPrice")
    @Expose
    private String itemGSTPrice;
    @SerializedName("itemSGSTPrice")
    @Expose
    private String itemSGSTPrice;
    @SerializedName("itemCGSTPrice")
    @Expose
    private String itemCGSTPrice;
    @SerializedName("itemAvailability")
    @Expose
    private String itemAvailability;
    @SerializedName("orderSessionID")
    @Expose
    private String orderSessionID;
    @SerializedName("categoryID")
    @Expose
    private String categoryID;
    @SerializedName("orderSessionTimingID")
    @Expose
    private String orderSessionTimingID;
    @SerializedName("orderCategoryID")
    @Expose
    private String orderCategoryID;
    @SerializedName("itemType")
    @Expose
    private String itemType;
    @SerializedName("itemLiveStatus")
    @Expose
    private String itemLiveStatus;

    @SerializedName("cookingTime")
    @Expose
    private String cookingTime;

    public String getItemLiveStatus() {
        return itemLiveStatus;
    }

    public void setItemLiveStatus(String itemLiveStatus) {
        this.itemLiveStatus = itemLiveStatus;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletConfigID() {
        return outletConfigID;
    }

    public void setOutletConfigID(String outletConfigID) {
        this.outletConfigID = outletConfigID;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemGSTPrice() {
        return itemGSTPrice;
    }

    public void setItemGSTPrice(String itemGSTPrice) {
        this.itemGSTPrice = itemGSTPrice;
    }

    public String getItemAvailability() {
        return itemAvailability;
    }

    public void setItemAvailability(String itemAvailability) {
        this.itemAvailability = itemAvailability;
    }

    public String getOrderSessionID() {
        return orderSessionID;
    }

    public void setOrderSessionID(String orderSessionID) {
        this.orderSessionID = orderSessionID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
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
}
