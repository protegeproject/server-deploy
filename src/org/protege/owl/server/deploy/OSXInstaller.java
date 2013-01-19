package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

public class OSXInstaller extends UnixInstaller {
    private File servicePlist;
    private File startServerScript;
    private File stopServerScript;
	
	public OSXInstaller(Configuration configuration) {
		super(configuration);
		servicePlist = new File("/Library/LaunchDaemons/org.protege.owl.server.plist");
		File serverLocation = new File(getConfiguration().getParameterValue(Parameter.SERVER_PREFIX));
		startServerScript = new File(serverLocation, "bin/start-owl-server");
		stopServerScript  = new File(serverLocation, "bin/stop-owl-server");
	}
	
	@Override
	protected void postInstall() throws IOException {
	    installUnixScripts();
	    getConfiguration().copyWithReplacements(getResource("osx/org.protege.owl.server.plist"), servicePlist);
	    
        Utility.copy(getResource("osx/start-owl-server"), startServerScript);
        makeExecutable(startServerScript);
        
        Utility.copy(getResource("osx/stop-owl-server"), stopServerScript);
        makeExecutable(stopServerScript);
	}

	@Override
	protected void postDeploy() throws IOException {
		run(getServerLocation(), "launchctl", "load", servicePlist.getAbsolutePath().toString());
	}

	@Override
	protected void doUndeploy() throws IOException {
        run(getServerLocation(), "launchctl",  "unload", servicePlist.getAbsolutePath().toString());
        servicePlist.delete();
	}
}
