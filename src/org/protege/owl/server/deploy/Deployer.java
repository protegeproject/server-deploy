package org.protege.owl.server.deploy;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.protege.owl.server.deploy.task.UnzipDistributionTask;

public class Deployer {
	private Configuration configuration;
	private File serverLocation;
	
	public Deployer() {
		configuration = obtainConfiguration();
		serverLocation = new File(configuration.getParameterValue(Parameter.SERVER_PREFIX));
	}
	
	private Configuration obtainConfiguration() {
		Console console = System.console();
		String sandboxUser;
		String hostname;
		String javacmd;
		int memoryMb;
		if (console != null) {
			console.printf("Sandbox user: ");
			sandboxUser = console.readLine();
			console.printf("Hostname: ");
			hostname = console.readLine();
			console.printf("Java command: ");
			javacmd = console.readLine();
			console.printf("Memory (in megabytes): ");
		    memoryMb = Integer.parseInt(console.readLine());
		}
		else {
			throw new IllegalStateException("Could not obtain console");
		}
		return new Configuration(sandboxUser, hostname, javacmd, memoryMb);
		
	}
	
    public void run() throws IOException {
    	installServer();
    }
    
    public void installServer() throws IOException {
    	Utility.deleteRecursively(serverLocation);
    	if (serverLocation.mkdirs()) {
    		UnzipDistributionTask task = new UnzipDistributionTask(getResource(Configuration.SERVER_DISTRIBUTION), serverLocation);
    		task.setPrefixToRemove(Configuration.PREFIX_TO_REMOVE_FROM_DISTRO);
    		task.run();
    		switch (configuration.getOperatingSystem()) {
    		case LINUX:
    			installLinuxFiles();
    			break;
    		case OS_X:
    		case WINDOWS_32_BIT:
    		case WINDOWS_64_BIT:
    		}
    	}
    	else {
    		System.out.println("Could not create server distribution directory.  Are you running as root?");
    	}
    }
    
    private void installLinuxFiles() throws IOException {
    	UnzipDistributionTask task = new UnzipDistributionTask(getResource(Configuration.UNIX_SCRIPTS), new File(serverLocation, "bin"));
    	task.run();
    }
    
    private URL getResource(String name) {
    	return getClass().getClassLoader().getResource(name);
    }
    
    public static void test() throws IOException {
    	System.out.println(OperatingSystem.detectOperatingSystem());
    	URL serverDistURL = Deployer.class.getResource(Configuration.SERVER_DISTRIBUTION);
        UnzipDistributionTask task1 = new UnzipDistributionTask(serverDistURL, new File("build/extracted"));
        task1.setPrefixToRemove(Configuration.PREFIX_TO_REMOVE_FROM_DISTRO);
        task1.run();
        new UnzipDistributionTask(Deployer.class.getResource(Configuration.UNIX_SCRIPTS), new File("build/extracted/bin")).run();
        Configuration conf = new Configuration("redmond", "localhost", "/usr/bin/java", 1700);
        conf.copyWithReplacements(Deployer.class.getResource("/unix/protege.defaults"), new File("build/protege.copied"));   	
    }

	public static void main(String[] args) throws IOException {
		new Deployer().run();
	}

}
