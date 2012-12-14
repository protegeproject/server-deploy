package org.protege.owl.server.deploy;

import java.io.File;

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
}
