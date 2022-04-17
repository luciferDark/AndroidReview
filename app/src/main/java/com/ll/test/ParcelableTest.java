package com.ll.test;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTest implements Parcelable {
    private String name = "";
    private int age = 0;

    public static final Creator<ParcelableTest> CREATOR = new Creator<ParcelableTest>() {
        @Override
        public ParcelableTest createFromParcel(Parcel in) {
            return new ParcelableTest(in);
        }

        @Override
        public ParcelableTest[] newArray(int size) {
            return new ParcelableTest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(age);
    }

    protected ParcelableTest(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ParcelableTest(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "ParcelableTest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
