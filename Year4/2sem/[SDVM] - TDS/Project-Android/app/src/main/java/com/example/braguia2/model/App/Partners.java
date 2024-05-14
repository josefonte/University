package com.example.braguia2.model.App;


import androidx.annotation.NonNull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
        tableName = "partners",
        foreignKeys = @ForeignKey(
                entity = App.class,
                parentColumns = "app_name",
                childColumns = "partner_app",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"partner_name"}, unique = true)
        }
)
public class Partners {
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "partner_name")
    @SerializedName("partner_name")
    private String partnerName;

    @ColumnInfo(name = "partner_phone")
    @SerializedName("partner_phone")
    private String partnerPhone;
    @ColumnInfo(name = "partner_url")
    @SerializedName("partner_url")
    private String partnerUrl;
    @ColumnInfo(name = "partner_mail")
    @SerializedName("partner_mail")
    private String partnerMail;
    @ColumnInfo(name = "partner_desc")
    @SerializedName("partner_desc")
    private String partnerDesc;
    @ColumnInfo(name = "partner_app")
    @SerializedName("partner_app")
    private String partnerApp;

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerPhone() {
        return partnerPhone;
    }

    public void setPartnerPhone(String partnerPhone) {
        this.partnerPhone = partnerPhone;
    }

    public String getPartnerUrl() {
        return partnerUrl;
    }

    public void setPartnerUrl(String partnerUrl) {
        this.partnerUrl = partnerUrl;
    }

    public String getPartnerMail() {
        return partnerMail;
    }

    public void setPartnerMail(String partnerMail) {
        this.partnerMail = partnerMail;
    }

    public String getPartnerDesc() {
        return partnerDesc;
    }

    public void setPartnerDesc(String partnerDesc) {
        this.partnerDesc = partnerDesc;
    }

    public String getPartnerApp() {
        return partnerApp;
    }

    public void setPartnerApp(String partnerApp) {
        this.partnerApp = partnerApp;
    }

}
