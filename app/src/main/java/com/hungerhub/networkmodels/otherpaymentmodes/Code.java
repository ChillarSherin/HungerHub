
package com.hungerhub.networkmodels.otherpaymentmodes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Code {

    @SerializedName("payment_modes")
    @Expose
    private List<PaymentMode> paymentModes = null;
    @SerializedName("payment_items")
    @Expose
    private List<PaymentItem> paymentItems = null;

    public List<PaymentMode> getPaymentModes() {
        return paymentModes;
    }

    public void setPaymentModes(List<PaymentMode> paymentModes) {
        this.paymentModes = paymentModes;
    }

    public List<PaymentItem> getPaymentItems() {
        return paymentItems;
    }

    public void setPaymentItems(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems;
    }

}
