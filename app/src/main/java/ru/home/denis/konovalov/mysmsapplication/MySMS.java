package ru.home.denis.konovalov.mysmsapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class MySMS implements Parcelable {
    public enum InType {
        In, Out
    }

    private static int id;

    private String phone;
    private String message;
    private InType smsType;

    public MySMS(String phone, String message, InType type){
        this.id++;
        this.phone = phone;
        this.message = message;
        this.smsType = type;
    }

    protected MySMS(Parcel in) {
        phone = in.readString();
        message = in.readString();
        smsType = in.readByte() == 0 ? InType.In : InType.Out;
    }

    public static final Creator<MySMS> CREATOR = new Creator<MySMS>() {
        @Override
        public MySMS createFromParcel(Parcel in) {
            return new MySMS(in);
        }

        @Override
        public MySMS[] newArray(int size) {
            return new MySMS[size];
        }
    };

    public int getID(){return id;}

    public String getPhone() {
        return phone;
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
        dest.writeByte(smsType == InType.In ? (byte)0 : (byte)1);
    }
}
