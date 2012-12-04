package org.protege.owl.server.deploy;

public enum OperatingSystem {
	LINUX, OS_X, WINDOWS_32_BIT, WINDOWS_64_BIT;  // where is freebsd?
	
	public boolean isWindows() {
		return this == OperatingSystem.WINDOWS_32_BIT || this == OperatingSystem.WINDOWS_64_BIT;
	}
	
	public static OperatingSystem detectOperatingSystem() {
		OperatingSystem os = null;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName == null) {
			;
		}
		else if (osName.indexOf("mac") >= 0) {
			os = OperatingSystem.OS_X;
		}
		else if (osName.indexOf("win") >= 0) {
			os = is64Bit() ? OperatingSystem.WINDOWS_64_BIT : OperatingSystem.WINDOWS_32_BIT;
		}
		else if (osName.indexOf("linux") >= 0) {
			os = OperatingSystem.LINUX;
		}
		return os;
	}
	
	private static boolean is64Bit() {
		String bitnessName = System.getProperty("sun.arch.data.model");
		return "64".equals(bitnessName);
	}
}
