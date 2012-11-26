package org.protege.owl.server.deploy.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipDistributionTask {
    public static final String PREFIX_TO_REMOVE = "server";
    private File zipFile;
    private File targetDir;
    
    public UnzipDistributionTask(File zipFile, File targetDir) {
        this.zipFile = zipFile;
        this.targetDir = targetDir;
    }
    
    public void run() throws IOException {
        ZipInputStream is = new ZipInputStream(new FileInputStream(zipFile));
        try {
            ZipEntry zEntry = null;
            while ((zEntry = is.getNextEntry()) != null) {
                String originalName =  zEntry.getName();
                File fileToWrite = new File(targetDir, originalName);
                if (zEntry.isDirectory()) {
                    fileToWrite.mkdirs();
                }
                else {
                    fileToWrite.getParentFile().mkdirs();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToWrite));
                    try {
                        copy(is, os);
                    }
                    finally {
                        os.close();
                    }
                }
                is.closeEntry();
            }
        }
        finally {
            is.close();
        }
    }
    
    private void copy(InputStream is, OutputStream os) throws IOException {
        int c;
        while ((c = is.read()) >= 0) {
            os.write((byte) c);
        }
    }

}
