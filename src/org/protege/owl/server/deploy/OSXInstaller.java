package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

public class OSXInstaller extends UnixInstaller {
    private File servicePlist;
	
	public OSXInstaller(Configuration configuration) {
		super(configuration);
		servicePlist = new File("/Library/LaunchDaemons/org.protege.owl.server.plist");
	}
	
	@Override
	protected void postInstall() throws IOException {
	    installUnixScripts();
	}

	@Override
	protected void postDeploy() throws IOException {
		getConfiguration().copyWithReplacements(getResource("osx/org.protege.owl.server.plist"), servicePlist);
		run("launchctl load " + servicePlist.getAbsolutePath());
	}

	@Override
	protected void doUndeploy() throws IOException {
        run("launchctl unload " + servicePlist.getAbsolutePath());
        servicePlist.delete();
	}
}
