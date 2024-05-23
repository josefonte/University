package com.example.braguia2.model.Trails;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "trail",indices = @Index(value = {"id"},unique = true))
@TypeConverters({ConverteTrailTable.class})
public class Trail{

    @PrimaryKey
    @NonNull
    //@SerializedName("id")
    @ColumnInfo(name = "id")
    String id;

    @SerializedName("trail_img")
    @ColumnInfo(name = "trail_img")
    String image_url;

    @SerializedName("trail_name")
    @ColumnInfo(name = "trail_name")
    String trail_name;

    @SerializedName("trail_duration")
    @ColumnInfo(name = "trail_duration")
    Integer trail_duration;

    @SerializedName("trail_difficulty")
    @ColumnInfo(name = "trail_difficulty")
    String trail_difficulty;

    @SerializedName("trail_desc")
    @ColumnInfo(name = "trail_desc")
    String trail_desc;

    //@SerializedName("rel_trail")
    //List<RelTrail> rel_trail;

    @SerializedName("edges")
    List<Edge> edges;


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }


    public String getUrl() {
        return image_url;
    }

    public void setUrl(String url) {
        this.image_url = url;
    }

    public String getTrailName() {
        return trail_name;
    }

    public void setTrailname(String name) {
        this.trail_name = name;
    }

    public Integer getTrailDuration() { return trail_duration;}

    public String getTrailDifficulty() { return trail_difficulty;}

    public String getTrailDifficultyExtenso() {
        String result = null;
        if (Objects.equals(this.trail_difficulty, "E")){
            result = "Fácil";
        }
        if (Objects.equals(this.trail_difficulty, "D")){
            result = "Médio";
        }
        if (Objects.equals(this.trail_difficulty, "C")){
            result = "Chato";
        }
        if (Objects.equals(this.trail_difficulty, "B")){
            result = "Difícil";
        }
        if (Objects.equals(this.trail_difficulty, "A")){
            result = "M. Difícil";
        }
        return result;
    }

    public String getTrailDesc() { return trail_desc;}

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Ponto> getPontos() {
        List<Ponto> result = new ArrayList<>();
        int flag = -1;
        for (Edge e : edges){
            if (flag == -1){
                result.add(e.getEdgeStart());
                result.add(e.getEdgeEnd());
                flag = 2;
            }
            else{
                result.add(e.getEdgeEnd());
            }
        }
        return result;
    }

    public double getTrailDistance(){
        List<Ponto> pontos = getPontos();
        double result = 0;
        for (int i = 0; i < pontos.size() - 1; i++) {
            Ponto x = pontos.get(i);
            Ponto y = pontos.get(i+ 1);
            result += calculateDistance(x.getPinLat(), x.getPinLng(), y.getPinLat(), y.getPinLng());
        }

        return result;
    }
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int EARTH_RADIUS_KM = 6371;
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences in latitude and longitude
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Calculate the central angle between the two points using the Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance using the central angle and Earth's radius
        double distance = EARTH_RADIUS_KM * c;

        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trail trail = (Trail) o;
        return id.equals(trail.id) &&
                Objects.equals(image_url, trail.image_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image_url);
    }
}