package org.protege.owl.server.deploy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Utility {

	private Utility() {
	}

	public static void deleteRecursively(File f) {
		if (f.exists() && f.isDirectory()) {
			for (File child : f.listFiles()) {
				deleteRecursively(child);
			}
		}
		f.delete();
	}
	
	public static void copy(URL source, File target) throws IOException {
		InputStream input = source.openStream();
		OutputStream output = new FileOutputStream(target);
		try {
			for (int c = input.read(); c >= 0; c = input.read()) {
				output.write((byte) c);
			}
		}
		finally {
			input.close();
			output.close();
		}
	}
}
