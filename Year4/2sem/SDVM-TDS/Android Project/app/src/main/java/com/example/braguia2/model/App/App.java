package com.example.braguia2.model.App;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;


@Entity(tableName = "app",indices = @Index(value = {"app_name"},unique = true))
@TypeConverters({ConvertAppTable.class})
public class App {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "app_name")
    String app_name;

    @SerializedName("app_desc")
    @ColumnInfo(name = "app_desc")
    String app_desc;

    @SerializedName("app_landing_page_text")
    @ColumnInfo(name = "app_landing_page_text")
    String app_landing_page_text;

    @SerializedName("socials")
    private List<Socials> socials;

    @SerializedName("partners")
    private List<Partners> partners;

    @SerializedName("contacts")
    private List<Contacts> contacts;

    public App(String app_name, String app_desc, String app_landing_page_text, List<Socials> socials, List<Partners>partners, List<Contacts>contacts){
        this.app_name = app_name;
        this.app_desc = app_desc;
        this.app_landing_page_text = app_landing_page_text;
        this.socials = socials;
        this.partners = partners;
        this.contacts = contacts;
    }


    @NonNull
    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(@NonNull String app_name) {
        this.app_name = app_name;
    }

    public String getApp_desc() {
        return app_desc;
    }

    public void setApp_desc(String app_desc) {
        this.app_desc = app_desc;
    }

    public String getApp_landing_page_text() {
        return app_landing_page_text;
    }

    public void setApp_landing_page_text(String app_landing_page_text) {
        this.app_landing_page_text = app_landing_page_text;
    }

    public List<Socials> getSocials() {
        return socials;
    }

    public void setSocials(List<Socials> socials) {
        this.socials = socials;
    }

    public List<Partners> getPartners() {
        return partners;
    }

    public void setPartners(List<Partners> partners) {
        this.partners = partners;
    }

    public List<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contacts> contacts) {
        this.contacts = contacts;
    }
}
