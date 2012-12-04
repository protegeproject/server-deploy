package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

import org.protege.owl.server.deploy.task.UnzipDistributionTask;

public class Deployer {
    public static final File SERVER_DISTRIBUTION = new File("protege-owl-server.zip");
    
    public static void main(String[] args) throws IOException {
    	System.out.println(OperatingSystem.detectOperatingSystem());
        UnzipDistributionTask task1 = new UnzipDistributionTask(SERVER_DISTRIBUTION, new File("build/extracted"));
        task1.setPrefixToRemove(Configuration.PREFIX_TO_REMOVE);
        task1.run();
        
    }
}
