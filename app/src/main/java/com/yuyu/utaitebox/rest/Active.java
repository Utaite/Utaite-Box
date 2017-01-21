package com.yuyu.utaitebox.rest;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Active {

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("avartar")
    private String avartar;

    @SerializedName("_mid")
    private String _mid;

    @SerializedName("point")
    private String point;

}
