package com.example.braguia2.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "favoritos")
public class Favoritos {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    String username;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "trail_id")
    String trail_id;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "list_name")
    String list_name;

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }
}
