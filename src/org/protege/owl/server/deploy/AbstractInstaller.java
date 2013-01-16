package org.protege.owl.server.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.protege.owl.server.deploy.task.UnzipDistributionTask;

public abstract class AbstractInstaller implements Installer {
	public static final int SERVER_PORT = 4875;
	
	private Configuration configuration;
	private File serverLocation;

	public AbstractInstaller(Configuration configuration) {
		this.configuration = configuration;
		serverLocation = new File(configuration.getParameterValue(Parameter.SERVER_PREFIX));
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public final void install() throws IOException {
		log("Deleting " + serverLocation);
    	Utility.deleteRecursively(serverLocation);
    	serverLocation.mkdirs();
    	if (serverLocation.exists()) {
    		log("Extracting server");
    		UnzipDistributionTask task = new UnzipDistributionTask(getResource(Configuration.SERVER_DISTRIBUTION), serverLocation);
    		task.setPrefixToRemove(Configuration.PREFIX_TO_REMOVE_FROM_DISTRO);
    		task.run();
    		configureServer();
    		createDataAndLogDirs();
    		installLogger();
    		postInstall();
    	}
    	else {
    		throw new IOException("Could not create server distribution directory.  Are you running as root?");
    	}
	}
	
	private void configureServer() throws IOException {
        runJava("org.protege.owl.server.command.SetMetaprojectDataDir",
                "metaproject.owl",
                new File(configuration.getParameterValue(Parameter.DATA_PREFIX), "ontologies").getAbsolutePath());

        runJava("org.protege.owl.server.command.SetMetaProjectPort",
        		"metaproject.owl",
                "" + SERVER_PORT);
	}
	
	private void createDataAndLogDirs() throws IOException {
		String dataPrefixString = configuration.getParameterValue(Parameter.DATA_PREFIX);
		if (dataPrefixString != null) {
			File ontologyDir = new File(dataPrefixString, "ontologies");
			if (!ontologyDir.exists()) {
				ontologyDir.mkdirs();
			}
			if (ontologyDir.listFiles().length == 0) {
				URL pizzaHistoryURL = getResource("PizzaOntology.history");
				Utility.copy(pizzaHistoryURL, new File(ontologyDir, "PizzaOntology.history"));
			}
		}
		String logPrefixString = configuration.getParameterValue(Parameter.LOG_PREFIX);
		if (logPrefixString != null) {
			File logDir = new File(logPrefixString);
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
		}
	}
	
	protected abstract void installLogger() throws IOException;
	protected abstract void postInstall() throws IOException;
	
	@Override
	public void uninstall() throws IOException {
		if (serverLocation.exists()) {
			try {
				undeploy();
			}
			catch (IOException ioe) {
				System.out.println("Undeploy failed: " + ioe.getMessage());
			}
			log("Deleting " + serverLocation);
			Utility.deleteRecursively(serverLocation);
		}
	}

	@Override
	public final void deploy() throws IOException {
		if (!serverLocation.exists()) {
			install();
		}
		postDeploy();
	}

	protected abstract void postDeploy() throws IOException;
	
	
	@Override
	public void undeploy() throws IOException {
		if (serverLocation.exists()) {
			doUndeploy();
		}
	}
	
	protected abstract void doUndeploy() throws IOException;
	
	
	protected URL getResource(String name) {
    	return getClass().getClassLoader().getResource(name);
    }
    
    protected File getServerLocation() {
		return serverLocation;
	}
    
    protected void run(File directory, String... command) throws IOException {
    	StringBuffer sb = new StringBuffer("Exec:");
    	for (String commandPart : command) { 
    		sb.append(' ');
    		sb.append(commandPart);
    	}
    	log(sb.toString());
    	runNoAnnounce(directory, command);
    }
    
    protected void runNoAnnounce(File directory, String... command) throws IOException {
    	final Process p = Runtime.getRuntime().exec(command, null, directory);
    	new Thread(new DisplayOutputRunner(p.getInputStream())).start();
    	new Thread(new DisplayOutputRunner(p.getErrorStream())).start();
    	try {
    	    p.waitFor();
    	}
    	catch (InterruptedException ie) {
    		throw new RuntimeException("Where is this coming from?  There is no thread interrupt logic here.");
    	}
    }
    
    private class DisplayOutputRunner implements Runnable {
    	private InputStream is;
    	
    	public DisplayOutputRunner(InputStream is) {
    		this.is = is;
    	}
    	
    	@Override
		public void run() {
    		try {
    			BufferedReader out = new BufferedReader(new InputStreamReader(is));
    			for (String outLine = out.readLine(); outLine != null; outLine = out.readLine()) {
    				log(outLine);
    			}
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    		finally {
    			try {
    				is.close();
    			}
    			catch (IOException ioe) {
    				ioe.printStackTrace();
    			}
    		}
		}
    }
    
    protected void runJava(String... javaCommand) throws IOException {
    	StringBuffer sb = new StringBuffer("Java Exec:");
    	for (String commandPart : javaCommand) { 
    		sb.append(' ');
    		sb.append(commandPart);
    	}
    	log(sb.toString());
    	int paddingCount = 3;
    	String[] unixCommand = new String[javaCommand.length + paddingCount];
    	unixCommand[0] = configuration.getParameterValue(Parameter.JAVA_CMD);
    	unixCommand[1] = "-classpath";
    	unixCommand[2] = "bundles/org.semanticweb.owl.owlapi.jar" + File.pathSeparator + "bundles/org.protege.owl.server.jar";
    	// unixCommand[3] = "-agentlib:jdwp=transport=dt_socket,address=8300,server=y,suspend=y";
    	for (int i = 0; i < javaCommand.length; i++) {
    		unixCommand[i+paddingCount] = javaCommand[i];
    	}
		runNoAnnounce(serverLocation, unixCommand);
    }
    
    protected void log(String message) {
    	System.out.println(message);
    }

}
