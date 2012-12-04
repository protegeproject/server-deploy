package org.protege.owl.server.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public enum Parameter {
	SERVER_PREFIX("server.prefix"), DATA_PREFIX("data.prefix"), LOG_PREFIX("log.prefix"), 
	SANDBOX_USER("sandbox.user"), HOSTNAME("hostname"), 
	JAVA_CMD("java.cmd"), MEMORY_IN_MB("memory");
	
	private String name;
	
	private Parameter(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/*
	 * Something has to give with this.  The sources will initially be resources in the jar file.
	 * If I want to copy them out maybe I will unzip them first?
	 */
	public static void copyWithReplacements(File src, File dest, Map<Parameter, String> parameterMap) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(src));
		FileWriter writer = new FileWriter(dest);
		try {
			for (String line = reader.readLine(); line != null; line=reader.readLine()) {
				for (Entry<Parameter,String> entry : parameterMap.entrySet()) {
					Parameter parameter      = entry.getKey();
					String    parameterValue = entry.getValue();
					if (parameterValue != null) {
						line.replaceAll("@" + parameter.getName() + "@", parameterValue);
					}
				}
				writer.write(line);
			}
		}
		finally {
			reader.close();
			writer.close();
		}
	}

}
