package com.tiwarithetiger11.murari.myapplication;

public class SongInfo {
    public String songname,artist,songURL,path;

    public String getSongname() {
        return songname;
    }

    public String getArtist() {
        return artist;
    }

    public String getSongURL() {
        return songURL;
    }

    public String getPath() {
        return path;
    }

    public SongInfo(String songname, String artist, String songURL, String path) {
        this.songname = songname;
        this.artist = artist;
        this.songURL = songURL;
        this.path=path;
    }
}
