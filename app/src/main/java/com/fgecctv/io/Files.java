package com.fgecctv.io;

import com.squirrel.voyage.Voyage;

import java.io.File;
import java.io.IOException;

public class Files {

    private static final String TAG = "Files";

    private Files() {
    }

    public static boolean delete(File file) {
        if (file.isDirectory())
            for (File f : file.listFiles())
                if (!delete(f))
                    Voyage.e(TAG, "Failed to delete " + file);

        return file.delete();
    }

    public static File mkdirs(String path) {
        File file = new File(path);
        mkdirs(file);
        return file;
    }

    public static File mkdirs(File file) {
        if (!file.exists() && !file.mkdirs())
            Voyage.e(TAG, "Create Directory Failed: " + file.getAbsolutePath());
        return file;
    }

    public static void delete(String path) {
        File file = new File(path);
        if (file.exists())
            delete(file);
    }

    public static File createNewFile(String path) {
        File file = new File(path);
        if (file.exists())
            delete(file);

        File parent = file.getParentFile();

        if(!parent.exists())
            mkdirs(parent);

        try {
            if (file.createNewFile()) return file;
        } catch (IOException ignored) {
        }

        String cause = "Can not create file: " + path;

        Voyage.e(TAG, cause);

        throw new AssertionError(cause);
    }
}