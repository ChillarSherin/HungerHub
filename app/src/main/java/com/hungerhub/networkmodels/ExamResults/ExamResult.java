
package com.hungerhub.networkmodels.ExamResults;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExamResult {

    @SerializedName("code")
    @Expose
    private Code code;
    @SerializedName("status")
    @Expose
    private Status status;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExamResult() {
    }

    /**
     * 
     * @param status
     * @param code
     */
    public ExamResult(Code code, Status status) {
        super();
        this.code = code;
        this.status = status;
    }

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
