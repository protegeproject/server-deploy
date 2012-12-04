package org.protege.owl.server.deploy;

import java.util.EnumMap;
import java.util.Map;

public class Configuration {

	public static final String PREFIX_TO_REMOVE = "server";
	
	private Map<Parameter, String> parameterMap;
	
	public Configuration(String sandboxUser, String hostname, String javacmd, String memoryMb) {
		setupParameterMap(sandboxUser, hostname, javacmd, memoryMb);
	}
	
	private void setupParameterMap(String sandboxUser, String hostname, String javacmd, String memoryMb) {
		parameterMap = new EnumMap<Parameter, String>(Parameter.class);
		setupCommonParameterMappings(sandboxUser, hostname, javacmd, memoryMb);
		switch (OperatingSystem.detectOperatingSystem()) {
		case LINUX:
			setupLinuxParameterMap();
			break;
		case WINDOWS_32_BIT:
		case WINDOWS_64_BIT:
			setupWindowsParameterMap();
			break;
		case OS_X:
			setupOSXParameterMap();
			break;
		default:
			throw new IllegalStateException("Programmer missed a case");
		}
	}
	
	private void setupCommonParameterMappings(String sandboxUser, String hostname, String javacmd, String memoryMb) {
		parameterMap.put(Parameter.SANDBOX_USER, sandboxUser);
		parameterMap.put(Parameter.HOSTNAME, hostname);
		parameterMap.put(Parameter.JAVA_CMD, javacmd);
		parameterMap.put(Parameter.MEMORY_IN_MB, memoryMb);
	}
	
	private void setupLinuxParameterMap() {
		parameterMap.put(Parameter.SERVER_PREFIX, "/usr/local/protege.server");
		parameterMap.put(Parameter.DATA_PREFIX, "/var/protege.data");
		parameterMap.put(Parameter.LOG_PREFIX, "/var/log/protege");
	}
	
	private void setupWindowsParameterMap() {
		parameterMap.put(Parameter.SERVER_PREFIX, "C:/Program Files/Protege OWL Server");
		parameterMap.put(Parameter.DATA_PREFIX, "C:/ProgramData/Protege OWL Server");
		parameterMap.put(Parameter.LOG_PREFIX, "C:/ProgramData/Protege OWL Server/logs");
	}
	
	private void setupOSXParameterMap() {
		parameterMap.put(Parameter.SERVER_PREFIX, "/usr/local/protege.server");
		parameterMap.put(Parameter.DATA_PREFIX, "/var/protege.data");
		parameterMap.put(Parameter.LOG_PREFIX, "/var/log/protege");
	}
	
	public Map<Parameter, String> getParameterMap() {
		return parameterMap;
	}

}
