package com.example.braguia2.model.Trails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

@Entity(
        tableName = "ponto",
        foreignKeys = { @ForeignKey(
                        entity = Edge.class,
                        parentColumns = "edge_start",
                        childColumns = "id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Edge.class,
                        parentColumns = "edge_end",
                        childColumns = "id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"id"}, unique = true),
        })
@TypeConverters({ConvertePontoTable.class})
public class Ponto {

    @PrimaryKey
    @NonNull
    //@SerializedName("id")
    @ColumnInfo(name = "id")
    String id;

    @SerializedName("rel_pin")
    List<RelPin> rel_pin;

    @SerializedName("media")
    List<Conteudo> media;

    @ColumnInfo(name = "pin_name")
    @SerializedName("pin_name")
    String pin_name;

    @ColumnInfo(name = "pin_desc")
    @SerializedName("pin_desc")
    String pin_desc;

    @ColumnInfo(name = "pin_lat")
    @SerializedName("pin_lat")
    double pin_lat;

    @ColumnInfo(name = "pin_lng")
    @SerializedName("pin_lng")
    double pin_lng;

    @ColumnInfo(name = "pin_alt")
    @SerializedName("pin_alt")
    double pin_alt;

    public String getPinName() {
        return pin_name;
    }

    public List<Conteudo> getConteudo() {
        return this.media;
    }
    public String getSingleImage(){
        String resposta = null;
        for (Conteudo singleMedia : this.media){
            if (Objects.equals(singleMedia.getMediaType(), "I")){
                resposta = singleMedia.getMediaFile();
            }
        }
        return resposta;
    }

    public double getPinLat(){
        return this.pin_lat;
    }
    public double getPinLng(){
        return this.pin_lng;
    }

    public double getPinAlt(){
        return this.pin_alt;
    }

    public String getId(){return this.id;}

    public String getPinDesc(){return this.pin_desc;}

}
