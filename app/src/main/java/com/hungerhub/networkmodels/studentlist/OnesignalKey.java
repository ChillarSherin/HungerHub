
package com.hungerhub.networkmodels.studentlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OnesignalKey {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("value")
    @Expose
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
