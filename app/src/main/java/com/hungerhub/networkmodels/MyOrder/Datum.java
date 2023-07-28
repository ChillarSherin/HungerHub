
package com.hungerhub.networkmodels.MyOrder;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("orderPurchaseID")
    @Expose
    private String orderPurchaseID;
    @SerializedName("realOrderPurchaseID")
    @Expose
    private String realOrderPurchaseID;
    @SerializedName("orderPurchaseStatus")
    @Expose
    private String orderPurchaseStatus;
    @SerializedName("requestTime")
    @Expose
    private String requestTime;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;
    @SerializedName("totalValNoTax")
    @Expose
    private String totalValNoTax;
    @SerializedName("totalSGSTPrice")
    @Expose
    private String totalSGSTPrice;
    @SerializedName("totalCGSTPrice")
    @Expose
    private String totalCGSTPrice;
    @SerializedName("expectedpicTime")
    @Expose
    private String expectedpicTime;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;


    public String getExpectedpicTime() {
        return expectedpicTime;
    }

    public void setExpectedpicTime(String expectedpicTime) {
        this.expectedpicTime = expectedpicTime;
    }

    public String getTotalValNoTax() {
        return totalValNoTax;
    }

    public void setTotalValNoTax(String totalValNoTax) {
        this.totalValNoTax = totalValNoTax;
    }

    public String getTotalSGSTPrice() {
        return totalSGSTPrice;
    }

    public void setTotalSGSTPrice(String totalSGSTPrice) {
        this.totalSGSTPrice = totalSGSTPrice;
    }

    public String getTotalCGSTPrice() {
        return totalCGSTPrice;
    }

    public void setTotalCGSTPrice(String totalCGSTPrice) {
        this.totalCGSTPrice = totalCGSTPrice;
    }

    public String getOrderPurchaseID() {
        return orderPurchaseID;
    }

    public void setOrderPurchaseID(String orderPurchaseID) {
        this.orderPurchaseID = orderPurchaseID;
    }

    public String getRealOrderPurchaseID() {
        return realOrderPurchaseID;
    }

    public void setRealOrderPurchaseID(String realOrderPurchaseID) {
        this.realOrderPurchaseID = realOrderPurchaseID;
    }

    public String getOrderPurchaseStatus() {
        return orderPurchaseStatus;
    }

    public void setOrderPurchaseStatus(String orderPurchaseStatus) {
        this.orderPurchaseStatus = orderPurchaseStatus;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}
