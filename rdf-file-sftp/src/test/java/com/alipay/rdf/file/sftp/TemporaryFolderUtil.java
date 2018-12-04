package com.alipay.rdf.file.sftp;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TemporaryFolderUtil {
    private File                folder;

    private static final String randomLong = String.valueOf(new Random().nextLong());

    public void create() throws IOException {
        folder = new File(System.getProperty("java.io.tmpdir") + "/" + randomLong);
        folder.delete();
        folder.mkdir();
    }

    public File getRoot() {
        return folder;
    }

    /**
     * Getter method for property <tt>randomLong</tt>.
     * 
     * @return property value of randomLong
     */
    public String getRandomLong() {
        return randomLong;
    }

    public void delete() {
        recursiveDelete(folder);
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each);
            }
        }
        file.delete();
    }
}
