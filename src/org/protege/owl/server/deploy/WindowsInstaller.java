package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

public class WindowsInstaller extends AbstractInstaller {
	public static final boolean NO_DEPLOY = true;
	private File serverLocation;
	private File serviceManager;
	
	
	public WindowsInstaller(Configuration configuration) {
		super(configuration);
		serverLocation = new File(configuration.getParameterValue(Parameter.SERVER_PREFIX));
		serviceManager = new File(serverLocation, "bin/OWLDocService.exe");
	}
	
	@Override
	protected void installLogger() throws IOException {
		File logConfiguration = new File(getServerLocation(), "logging.properties");
		getConfiguration().copyWithReplacements(getResource("windows/logging.properties"), logConfiguration);
	}
	
	
	@Override
	protected void postInstall() throws IOException {
		getConfiguration().copyWithReplacements(getResource("windows/run-protege-owl-server.bat"), new File(serverLocation, "bin/run-protege-owl-server.bat"));
		String dataPrefix  = getConfiguration().getParameterValue(Parameter.DATA_PREFIX);
		String logPrefix   = getConfiguration().getParameterValue(Parameter.LOG_PREFIX);
		String sandBoxUser = getConfiguration().getParameterValue(Parameter.SANDBOX_USER);
		new File(dataPrefix).mkdirs();
		new File(logPrefix).mkdirs();
		run(getServerLocation(), "icacls", dataPrefix, "/T", "/grant", sandBoxUser + ":F");
		run(getServerLocation(), "icacls", logPrefix,  "/T", "/grant", sandBoxUser + ":F");
	}

	@Override
	protected void postDeploy() throws IOException {
		if (NO_DEPLOY) {
			return;
		}
		getConfiguration().copyWithReplacements(getResource("windows/winsvc.jar"), new File(serverLocation, "lib/winsvc.jar"));
		String serviceManagerResource = getConfiguration().getOperatingSystem() == OperatingSystem.WINDOWS_32_BIT ? "windows/prunsrv-32.exe" : "windows/prunsrv-64.exe";
		Utility.copy(getResource(serviceManagerResource), serviceManager);
		loadService();
	}
	
	private void loadService() throws IOException {
	    installService();
        log("Starting OWLServer Service");
        run(serviceManager.getParentFile(), serviceManager.getAbsolutePath(), "//ES//OWLServer");
	}
	
	private void installService() throws IOException {
	    log("Installing OWLServer service");
	    run(serviceManager.getParentFile(), serviceManager.getAbsolutePath(), "//DS//OWLServer");
        run(getServerLocation(),
                       serviceManager.getAbsolutePath(),
                       "//IS//OWLServer",
                       "--Install=" + serviceManager.getAbsolutePath(),
                       "--Jvm=auto",
                       "--Description=Protege-OWL-Document-Service",
                       "--DisplayName=OWLServer",
                       "--LogPath=" + getConfiguration().getParameterValue(Parameter.LOG_PREFIX),
                       "--StdOutput=auto",
                       "--StdError=auto",
                       "--Startup=auto",
                       "--Classpath=lib/winsvc.jar;lib/felix.jar;lib/ProtegeLauncher.jar",
                       "--JvmMx=" + getConfiguration().getParameterValue(Parameter.MEMORY_IN_MB),
                       "++JvmOptions=-DentityExpansionLimit=100000000",
                       "++JvmOptions=-Dfile.encoding=UTF-8",
                       "++JvmOptions=-Djava.rmi.server.hostname=" + getConfiguration().getParameterValue(Parameter.HOSTNAME),
                       "++JvmOptions=-Dorg.protege.owl.server.configuration=metaproject.owl",
                       "++JvmOptions=-Djava.util.logging.config.file=logging.properties",
                       "--StartPath=" + serverLocation.getAbsolutePath(),
                       "--StartMode=jvm",
                       "--StartClass=org.protege.owl.server.deploy.windows.WindowsOWLServer",
                       "--StartParams=start",
                       "--StopPath=" + serverLocation.getAbsolutePath(),
                       "--StopMode=jvm",
                       "--StopClass=org.protege.owl.server.deploy.windows.WindowsOWLServer",
                       "--StopParams=stop",
                       "--LogLevel=Debug");
	}

	@Override
	protected void doUndeploy() throws IOException {
		if (NO_DEPLOY) {
			return;
		}
        log("Starting OWLServer Service");
        run(serviceManager.getParentFile(), serviceManager.getAbsolutePath(), "//DS//OWLServer");
	}
}
