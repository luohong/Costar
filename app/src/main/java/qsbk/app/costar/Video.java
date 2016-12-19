package qsbk.app.costar;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;

public class Video implements Serializable {
    public int index;
    public String path;
    public boolean recorderCompleted;

    public Video(int index) {
        this.index = index;
        this.path = getTargetPath() + "/Video" + index + ".mp4";
    }

    public String getTargetPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Costar/Record";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
}