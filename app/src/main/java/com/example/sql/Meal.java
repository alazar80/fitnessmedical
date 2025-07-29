package com.example.sql;

import android.os.Parcel;
import android.os.Parcelable;

public class Meal implements Parcelable {
    private int id;
    private String title;
    private int calories;
    private String description;
    private String category;
    private String fitnessGoal;
    private String mealType;
    private String imageId;

    // Full constructor
    public Meal(int id, String title, int calories, String description,
                String category, String fitnessGoal,
                String mealType, String imageId) {
        this.id = id;
        this.title = title;
        this.calories = calories;
        this.description = description;
        this.category = category;
        this.fitnessGoal = fitnessGoal;
        this.mealType = mealType;
        this.imageId = imageId;
    }

    // Convenience constructor (no id)
    public Meal(String title, int calories, String description,
                String category, String fitnessGoal,
                String mealType, String imageId) {
        this(0, title, calories, description, category, fitnessGoal, mealType, imageId);
    }

    // Parcelable constructor
    protected Meal(Parcel in) {
        id = in.readInt();
        title = in.readString();
        calories = in.readInt();
        description = in.readString();
        category = in.readString();
        fitnessGoal = in.readString();
        mealType = in.readString();
        imageId = in.readString();
    }

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }
        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getCalories() { return calories; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getFitnessGoal() { return fitnessGoal; }
    public String getMealType() { return mealType; }
    public String getImageId() { return imageId; }

    // Setter for id
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Setters for updating the meal data
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setCategory(String category) { this.category = category; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setImageId(String imageId) { this.imageId = imageId; }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(calories);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(fitnessGoal);
        dest.writeString(mealType);
        dest.writeString(imageId);
    }

}
