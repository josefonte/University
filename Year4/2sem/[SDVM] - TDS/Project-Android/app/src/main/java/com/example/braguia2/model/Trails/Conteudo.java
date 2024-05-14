package com.example.braguia2.model.Trails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(
        tableName = "conteudo",
        foreignKeys = @ForeignKey(
                entity = Ponto.class,
                parentColumns = "id",
                childColumns = "media_pin",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"id"}, unique = true),
        })
public class Conteudo {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    int id;

    @ColumnInfo(name = "media_file")
    @SerializedName("media_file")
    String media_file;

    @ColumnInfo(name = "media_type")
    @SerializedName("media_type")
    String media_type;

    @ColumnInfo(name = "media_pin")
    @SerializedName("media_pin")
    int media_pin;


    public int getId() {
        return id;
    }

    public String getMediaFile() {
        return media_file;
    }

    public String getMediaType() {
        return media_type;
    }

    public int getMediaPin() {
        return media_pin;
    }

}
