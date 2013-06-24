package com.bscardiotracker.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WorkoutDataEntity implements Serializable {
    private static final long serialVersionUID = -400294700021163051L;
    Long workoutDate;
    Integer duration;
    Double distance;
    Integer pace; //Seconds per mile, displayed as minutes per mile
    List<LatLng> locations;

    public WorkoutDataEntity() {

    }

    public WorkoutDataEntity(Long workoutDate, Integer duration, Double distance, Integer pace, List<LatLng> locations) {
        this.workoutDate = workoutDate;
        this.duration = duration;
        this.distance = distance;
        this.pace = pace;
        this.locations = locations;
    }

    public void setWorkoutDate(Long workoutDate) {
        this.workoutDate = workoutDate;
    }
    public Long getWorkoutDate() {
        return workoutDate;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Integer getDuration() {
        return duration;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
    public Double getDistance() {
        return distance;
    }

    public void setPace(Integer pace) {
        this.pace = pace;
    }
    public Integer getPace() {
        return pace;
    }

    public void setLocations(List<LatLng> locations) {
        this.locations = locations;
    }
    public List<LatLng> getLocations() {
        return locations;
    }
}
