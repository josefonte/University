package com.example.braguia2.model.Trails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

@Entity(
        tableName = "edge",
        foreignKeys = @ForeignKey(
                entity = Trail.class,
                parentColumns = "id",
                childColumns = "edge_trail",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = {"id"}, unique = true),
                @Index(value = {"edge_start"}, unique = true),
                @Index(value = {"edge_end"}, unique = true)
        })
@TypeConverters({ConverteEdgeTable.class})
public class Edge {

    @PrimaryKey
    @NonNull
    //@SerializedName("id")
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "edge_transp")
    @SerializedName("edge_transp")
    String edge_transport;

    @ColumnInfo(name = "edge_duration")
    @SerializedName("edge_duration")
    int edge_duration;

    @ColumnInfo(name = "edge_desc")
    @SerializedName("edge_desc")
    String edge_desc;

    // FK
    @ColumnInfo(name = "edge_trail")
    @SerializedName("edge_trail")
    int edge_trail;

    // Inicio do Trail
    @ColumnInfo(name= "edge_start")
    @SerializedName("edge_start")
    Ponto edge_start;

    // Fim do Trail
    @ColumnInfo(name= "edge_end")
    @SerializedName("edge_end")
    Ponto edge_end;

    public String getEdgeDesc(){return edge_desc;}
    public Ponto getEdgeStart() {
        return edge_start;
    }

    public Ponto getEdgeEnd() {
        return edge_end;
    }
}
