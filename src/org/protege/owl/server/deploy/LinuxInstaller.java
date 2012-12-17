package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

import org.protege.owl.server.deploy.task.UnzipDistributionTask;

public class LinuxInstaller extends AbstractInstaller {
	private File initdFile = new File("/etc/init.d/protege");
	private File protegeDefaults  = new File("/etc/default/protege");
	
	public LinuxInstaller(Configuration configuration) {
		super(configuration);
	}

	@Override
	protected void postInstall() throws IOException {
		log("Setting up linux command line scripts");
		File binDirectory = new File(getServerLocation(), "bin");
    	UnzipDistributionTask task = new UnzipDistributionTask(getResource(Configuration.UNIX_SCRIPTS), binDirectory);
    	task.run();
    	for (File executable : binDirectory.listFiles()) {
    		makeExecutable(executable);
    	}
        File logDir  = new File(getConfiguration().getParameterValue(Parameter.LOG_PREFIX));
        File dataDir = new File(getConfiguration().getParameterValue(Parameter.DATA_PREFIX));
        String sandboxUser = getConfiguration().getParameterValue(Parameter.SANDBOX_USER);
        logDir.mkdirs();
        dataDir.mkdirs();
        run("chown -R " + sandboxUser + " " + logDir);
        run("chown -R " + sandboxUser + " " + dataDir);
	}

	@Override
	protected void postDeploy() throws IOException {
		log("Configuring the /etc/init.d and /etc/rc*.d scripts");
        getConfiguration().copyWithReplacements(getResource("unix/protege.defaults"), protegeDefaults);
        getConfiguration().copyWithReplacements(getResource("unix/protege"), initdFile);
        makeExecutable(initdFile);
        run("update-rc.d protege defaults");
        run("/etc/init.d/protege start");
	}

	@Override
	protected void doUndeploy() throws IOException {
		if (initdFile.exists()) {
			log("Stopping Protege OWL Server");
	        run("/etc/init.d/protege stop");
	        log("Deleting /etc/init.d and /etc/rc*.d Protege OWL Server scripts");
	        initdFile.delete();
	        protegeDefaults.delete();
	        run("update-rc.d protege remove");
	        File logDir  = new File(getConfiguration().getParameterValue(Parameter.LOG_PREFIX));
	        logDir.delete();
		}
	}
	
	private void makeExecutable(File f) throws IOException {
		Runtime r = Runtime.getRuntime();
		r.exec("chmod 0555 " + f.getAbsolutePath());
	}

}
