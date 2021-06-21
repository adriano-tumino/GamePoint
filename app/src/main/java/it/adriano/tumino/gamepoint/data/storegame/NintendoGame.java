package it.adriano.tumino.gamepoint.data.storegame;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class NintendoGame extends Game {
    private String videoTrailerUrl;
    private String pegi;
    private String console;
    private String systemInfo;
    private List<String> featureSheets;

    public NintendoGame() {
    }

    private NintendoGame(Parcel in) {
        super(in);
        videoTrailerUrl = in.readString();
        pegi = in.readString();
        console = in.readString();
        systemInfo = in.readString();
        featureSheets = in.createStringArrayList();
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(videoTrailerUrl);
        out.writeString(pegi);
        out.writeString(console);
        out.writeString(systemInfo);
        out.writeStringList(featureSheets);
    }

    public static final Parcelable.Creator<NintendoGame> CREATOR = new Parcelable.Creator<NintendoGame>() {
        public NintendoGame createFromParcel(Parcel in) {
            return new NintendoGame(in);
        }

        public NintendoGame[] newArray(int size) {
            return new NintendoGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getVideoTrailerUrl() {
        return videoTrailerUrl;
    }

    public void setVideoTrailerUrl(String videoTrailerUrl) {
        this.videoTrailerUrl = videoTrailerUrl;
    }

    public String getPegi() {
        return pegi;
    }

    public void setPegi(String pegi) {
        this.pegi = pegi;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(String systemInfo) {
        this.systemInfo = systemInfo;
    }

    public List<String> getFeatureSheets() {
        return featureSheets;
    }

    public void setFeatureSheets(List<String> featureSheets) {
        this.featureSheets = featureSheets;
    }
}
