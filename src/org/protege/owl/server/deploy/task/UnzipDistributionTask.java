package org.protege.owl.server.deploy.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipDistributionTask {
    private URL zipURL;
    private File targetDir;
    private String prefixToRemove;
    
    public UnzipDistributionTask(URL zipURL, File targetDir) {
        this.zipURL = zipURL;
        this.targetDir = targetDir;
        this.prefixToRemove = "";
    }
    
    public void setPrefixToRemove(String prefixToRemove) {
		this.prefixToRemove = prefixToRemove;
	}
    
    public String getPrefixToRemove() {
		return prefixToRemove;
	}
    
    public void run() throws IOException {
        ZipInputStream is = new ZipInputStream(zipURL.openStream());
        try {
            ZipEntry zEntry = null;
            while ((zEntry = is.getNextEntry()) != null) {
                String originalName =  zEntry.getName();
                if (originalName.startsWith(prefixToRemove)) {
                	File fileToWrite = new File(targetDir, originalName.substring(prefixToRemove.length()));
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
