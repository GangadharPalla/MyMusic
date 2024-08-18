package com.ganga.mymusic;

import java.io.Serializable;
// implement serializable to pass audio model to other activities

public class AudioModel implements Serializable {
    String path;
    String duration;
    String title;

    public AudioModel(String path, String duration, String title) {
        this.path = path;
        this.duration = duration;
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
