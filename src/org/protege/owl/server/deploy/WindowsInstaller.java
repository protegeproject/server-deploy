package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

public class WindowsInstaller extends AbstractInstaller {
	private File serverLocation;
	
	
	public WindowsInstaller(Configuration configuration) {
		super(configuration);
		serverLocation = new File(configuration.getParameterValue(Parameter.SERVER_PREFIX));
	}
	
	
	@Override
	protected void postInstall() throws IOException {
		getConfiguration().copyWithReplacements(getResource("windows/run-protege-owl-server.bat"), new File(serverLocation, "bin/run-protege-owl-server.bat"));
		String dataPrefix  = getConfiguration().getParameterValue(Parameter.DATA_PREFIX);
		String logPrefix   = getConfiguration().getParameterValue(Parameter.LOG_PREFIX);
		String sandBoxUser = getConfiguration().getParameterValue(Parameter.SANDBOX_USER);
		new File(dataPrefix).mkdirs();
		new File(logPrefix).mkdirs();
		run("icacls " + dataPrefix + " /T /grant " + sandBoxUser + ":F");
		run("icacls " + logPrefix  + " /T /grant " + sandBoxUser + ":F");
	}

	@Override
	protected void postDeploy() throws IOException {
		getConfiguration().copyWithReplacements(getResource("windows/winsvc.jar"), new File(serverLocation, "lib/winsvc.jar"));

	}

	@Override
	protected void doUndeploy() {
		throw new IllegalStateException("Not implemented yet");
	}
}
