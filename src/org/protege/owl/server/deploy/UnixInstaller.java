package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

import org.protege.owl.server.deploy.task.UnzipDistributionTask;

public abstract class UnixInstaller extends AbstractInstaller {
    
    public UnixInstaller(Configuration configuration) {
        super(configuration);
    }
    
    protected void installUnixScripts() throws IOException {
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
        run(logDir, "chown", "-R", sandboxUser, logDir.getAbsolutePath());
        run(dataDir, "chown", "-R", sandboxUser, dataDir.getAbsolutePath());
    }
    
    
    protected void makeExecutable(File f) throws IOException {
        Runtime r = Runtime.getRuntime();
        r.exec("chmod ugo+x " + f.getAbsolutePath());
    }

}
