
package com.hungerhub.networkmodels.cardtransactionhistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cardhistory {

    @SerializedName("code")
    @Expose
    private Code code;
    @SerializedName("status")
    @Expose
    private Status status;

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
