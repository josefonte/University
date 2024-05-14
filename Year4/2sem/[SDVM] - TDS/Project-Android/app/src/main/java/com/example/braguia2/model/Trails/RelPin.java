package com.example.braguia2.model.Trails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(
        tableName = "relpin",
        foreignKeys = @ForeignKey(
                entity = Ponto.class,
                parentColumns = "id",
                childColumns = "pin",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"id"}, unique = true),
        })
public class RelPin {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    int id;

    @ColumnInfo(name = "value")
    @SerializedName("value")
    String value;

    @ColumnInfo(name = "attrib")
    @SerializedName("attrib")
    String attrib;

    @ColumnInfo(name = "pin")
    @SerializedName("pin")
    int pin;


}
