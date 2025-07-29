// Exercise.java
package com.example.sql;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

public class Exercise implements Parcelable {
    private int    id, userId;
    private String name, duration, iconId, type,
            description, fitnessGoal, experienceLevel;

    public Exercise() { }

    public Exercise(String name, String duration, String type,
                    String description, String fitnessGoal,
                    String experienceLevel, String iconId) {
        this.name            = name;
        this.duration        = duration;
        this.type            = type;
        this.description     = description;
        this.fitnessGoal     = fitnessGoal;
        this.experienceLevel = experienceLevel;
        this.iconId          = iconId;
    }

    protected Exercise(Parcel in) {
        name            = in.readString();
        duration        = in.readString();
        iconId          = in.readString();
        type            = in.readString();
        description     = in.readString();
        id              = in.readInt();
        userId          = in.readInt();
        fitnessGoal     = in.readString();
        experienceLevel = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }
        @Override public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(duration);
        dest.writeString(iconId);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(fitnessGoal);
        dest.writeString(experienceLevel);
    }

    @Override public int describeContents() { return 0; }

    // --- Getters ---
    public int    getId()              { return id; }
    public int    getUserId()          { return userId; }
    public String getName()            { return name; }
    public String getDuration()        { return duration; }
    public String getIconId()          { return iconId; }
    public String getType()            { return type; }
    public String getDescription()     { return description; }
    public String getFitnessGoal()     { return fitnessGoal; }
    public String getExperienceLevel() { return experienceLevel; }

    // --- Setters ---
    public void setId(int id)                             { this.id = id; }
    public void setUserId(int userId)                     { this.userId = userId; }
    public void setName(String name)                      { this.name = name; }
    public void setDuration(String duration)              { this.duration = duration; }
    public void setIconId(String iconId)                  { this.iconId = iconId; }
    public void setType(String type)                      { this.type = type; }
    public void setDescription(String description)        { this.description = description; }
    public void setFitnessGoal(String fitnessGoal)        { this.fitnessGoal = fitnessGoal; }
    public void setExperienceLevel(String experienceLevel){ this.experienceLevel = experienceLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise e = (Exercise) o;
        return id == e.id &&
                userId == e.userId &&
                Objects.equals(name, e.name) &&
                Objects.equals(duration, e.duration) &&
                Objects.equals(iconId, e.iconId) &&
                Objects.equals(type, e.type) &&
                Objects.equals(description, e.description) &&
                Objects.equals(fitnessGoal, e.fitnessGoal) &&
                Objects.equals(experienceLevel, e.experienceLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, duration,
                iconId, type, description,
                fitnessGoal, experienceLevel);
    }
}
