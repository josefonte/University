package com.example.braguia2.model.App;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(
        tableName = "contacts",
        foreignKeys = @ForeignKey(
                entity = App.class,
                parentColumns = "app_name",
                childColumns = "contact_app",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"contact_name"}, unique = true)
        }
)

public class Contacts {
    @PrimaryKey
    @SerializedName("contact_name")
    @ColumnInfo(name = "contact_name")
    @NonNull
    private String contactName;
    @SerializedName("contact_phone")
    @ColumnInfo(name = "contact_phone")
    private String contactPhone;
    @SerializedName("contact_url")
    @ColumnInfo(name = "contact_url")
    private String contactUrl;
    @SerializedName("contact_mail")
    @ColumnInfo(name = "contact_mail")
    private String contactMail;
    @SerializedName("contact_desc")
    @ColumnInfo(name = "contact_desc")
    private String contactDesc;
    @SerializedName("contact_app")
    @ColumnInfo(name = "contact_app", index = true)
    private String contactApp;



    @NonNull
    public String getContactName() {
        return contactName;
    }

    public void setContactName(@NonNull String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getContactDesc() {
        return contactDesc;
    }

    public void setContactDesc(String contactDesc) {
        this.contactDesc = contactDesc;
    }

    public String getContactApp() {
        return contactApp;
    }

    public void setContactApp(String contactApp) {
        this.contactApp = contactApp;
    }


}
