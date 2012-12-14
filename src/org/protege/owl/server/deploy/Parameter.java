package org.protege.owl.server.deploy;


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
}
