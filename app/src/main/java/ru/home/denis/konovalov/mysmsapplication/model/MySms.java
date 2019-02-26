package ru.home.denis.konovalov.mysmsapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MySms implements Parcelable {
    public enum InType {
        In, Out
    }

    private static int id;

    private String phone;
    private String message;
    private long timestamp;
    private InType smsType;

    public MySms(String phone, String message, long timestamp, InType type){
        this.id++;
        this.phone = phone;
        this.message = message;
        this.timestamp = timestamp;
        this.smsType = type;
    }

    protected MySms(Parcel in) {
        phone = in.readString();
        message = in.readString();
        timestamp = in.readLong();
        smsType = in.readByte() == 0 ? InType.In : InType.Out;
    }

    public static final Creator<MySms> CREATOR = new Creator<MySms>() {
        @Override
        public MySms createFromParcel(Parcel in) {
            return new MySms(in);
        }

        @Override
        public MySms[] newArray(int size) {
            return new MySms[size];
        }
    };

    public int getID(){return id;}

    public String getPhone() {
        return phone;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public InType getType() {
        return smsType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(message);
        dest.writeLong(timestamp);
        dest.writeByte(smsType == InType.In ? (byte)0 : (byte)1);
    }
}
