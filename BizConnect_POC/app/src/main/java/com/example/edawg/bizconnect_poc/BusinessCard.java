package com.example.edawg.bizconnect_poc;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by edawg on 11/5/16.
 */

public class BusinessCard implements Parcelable {

    private String name, number, email, industry, linkedIn, event;

    public BusinessCard(String n, String nu, String e, String i, String l, String ev)  {
        name = n;
        number = nu;
        email = e;
        industry = i;
        linkedIn = l;
        event = ev;
    }

    protected BusinessCard(Parcel in) {
        name = in.readString();
        number = in.readString();
        email = in.readString();
        industry = in.readString();
        linkedIn = in.readString();
        event = in.readString();
    }

    public static final Creator<BusinessCard> CREATOR = new Creator<BusinessCard>() {
        @Override
        public BusinessCard createFromParcel(Parcel in) {
            return new BusinessCard(in);
        }

        @Override
        public BusinessCard[] newArray(int size) {
            return new BusinessCard[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getIndustry() {
        return industry;
    }

    public String getLinkedIn() { return linkedIn; }

    public String getEvent() { return event; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(email);
        parcel.writeString(industry);
        parcel.writeString(linkedIn);
        parcel.writeString(event);
    }
}
