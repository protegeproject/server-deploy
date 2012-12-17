package org.protege.owl.server.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

	@Override
	public final void install() throws IOException {
		log("Deleting " + serverLocation);
    	Utility.deleteRecursively(serverLocation);
    	if (serverLocation.mkdirs()) {
    		log("Extracting server");
    		UnzipDistributionTask task = new UnzipDistributionTask(getResource(Configuration.SERVER_DISTRIBUTION), serverLocation);
    		task.setPrefixToRemove(Configuration.PREFIX_TO_REMOVE_FROM_DISTRO);
    		task.run();
    		configureServer();
    		postInstall();
    	}
    	else {
    		throw new IOException("Could not create server distribution directory.  Are you running as root?");
    	}
	}
	
	private void configureServer() throws IOException {

		StringBuffer setDataDirCommand = new StringBuffer();
		setDataDirCommand.append(" org.protege.owl.server.command.SetMetaprojectDataDir ");
		setDataDirCommand.append(new File(serverLocation, "metaproject.owl").getAbsoluteFile().toString());
		setDataDirCommand.append(' ');
		setDataDirCommand.append(new File(configuration.getParameterValue(Parameter.DATA_PREFIX), "ontologies"));
		runJava(setDataDirCommand.toString());
		
		
		StringBuffer setPortCommand = new StringBuffer();
		setPortCommand.append(" org.protege.owl.server.command.SetMetaProjectPort ");
		setPortCommand.append(new File(serverLocation, "metaproject.owl").getAbsoluteFile().toString());
		setPortCommand.append(' ');
		setPortCommand.append(SERVER_PORT);
		runJava(setPortCommand.toString());
	}
	
	protected abstract void postInstall() throws IOException;
	
	@Override
	public void uninstall() throws IOException {
		if (serverLocation.exists()) {
			undeploy();
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
    
    protected Configuration getConfiguration() {
		return configuration;
	}
    
    protected File getServerLocation() {
		return serverLocation;
	}
    
    protected void run(String command) throws IOException {
    	log("Exec: " + command);
    	runNoAnnounce(command);
    }
    
    protected void runNoAnnounce(String command) throws IOException {
    	Process p = Runtime.getRuntime().exec(command);
        BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while (true) {
        	String outLine = out.readLine();
        	if (outLine != null) {
        		log("  " + outLine);
        	}
        	String outErr = err.readLine();
        	if (outErr != null) {
        		log("  " + outErr);
        	}
        	if (outLine == null && outErr == null) {
        		break;
        	}
        }
    }
    
    protected void runJava(String javaCommand) throws IOException {
    	log("Java Exec: " + javaCommand);
		StringBuffer unixCommand = new StringBuffer();
		unixCommand.append(configuration.getParameterValue(Parameter.JAVA_CMD));
		unixCommand.append(" -classpath ");
		unixCommand.append(new File(serverLocation, "bundles/org.semanticweb.owl.owlapi.jar").getAbsolutePath());
		unixCommand.append(':');
		unixCommand.append(new File(serverLocation, "bundles/org.protege.owl.server.jar").getAbsolutePath());
		unixCommand.append(' ');
		unixCommand.append(javaCommand);
		runNoAnnounce(unixCommand.toString());
    }
    
    protected void log(String message) {
    	System.out.println(message);
    }
}
