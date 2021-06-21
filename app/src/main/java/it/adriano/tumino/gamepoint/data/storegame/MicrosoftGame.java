package it.adriano.tumino.gamepoint.data.storegame;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MicrosoftGame extends Game {
    private String pegi;
    private String console;
    private String systemRequirement;
    private ArrayList<String> categories;
    private ArrayList<String> metadata;

    public MicrosoftGame() {
    }

    private MicrosoftGame(Parcel in) {
        super(in);
        pegi = in.readString();
        console = in.readString();
        systemRequirement = in.readString();
        categories = in.createStringArrayList();
        metadata = in.createStringArrayList();
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(pegi);
        out.writeString(console);
        out.writeString(systemRequirement);
        out.writeStringList(categories);
        out.writeStringList(metadata);
    }

    public static final Parcelable.Creator<MicrosoftGame> CREATOR = new Parcelable.Creator<MicrosoftGame>() {
        public MicrosoftGame createFromParcel(Parcel in) {
            return new MicrosoftGame(in);
        }

        public MicrosoftGame[] newArray(int size) {
            return new MicrosoftGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
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

    public String getSystemRequirement() {
        return systemRequirement;
    }

    public void setSystemRequirement(String systemRequirement) {
        this.systemRequirement = systemRequirement;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<String> metadata) {
        this.metadata = metadata;
    }
}
