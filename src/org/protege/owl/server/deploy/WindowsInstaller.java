package org.protege.owl.server.deploy;

import java.io.File;
import java.io.IOException;

public class WindowsInstaller extends AbstractInstaller {
	private File serverLocation;
	private File serviceManager;
	
	
	public WindowsInstaller(Configuration configuration) {
		super(configuration);
		serverLocation = new File(configuration.getParameterValue(Parameter.SERVER_PREFIX));
		serviceManager = new File(serverLocation, "bin/OWLDocService.exe");
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
		String serviceManagerResource = getConfiguration().getOperatingSystem() == OperatingSystem.WINDOWS_32_BIT ? "windows/prunsrv-32.exe" : "windows/prunsrv-64.exe";
		Utility.copy(getResource(serviceManagerResource), serviceManager);
		loadService();
	}
	
	private void loadService() throws IOException {
	    installService();
        log("Starting OWLServer Service");
        run("" + serviceManager.getAbsolutePath() + "//ES//OWLServer");
	}
	
	private void installService() throws IOException {
	    log("Installing OWLServer service");
	    StringBuffer installCommand = new StringBuffer();
	    installCommand.append(serviceManager.toString());
	    installCommand.append(" //IS//OWLServer");
	    installCommand.append(" --Install=");
	    installCommand.append(serviceManager.getAbsolutePath());
	    installCommand.append(" --Jvm=auto");
	    installCommand.append(" --Description=Protege-OWL-Document-Service");
	    installCommand.append(" --DisplayName=OWLServer");
	    installCommand.append(" --LogPath=");
	    installCommand.append(getConfiguration().getParameterValue(Parameter.LOG_PREFIX));
	    installCommand.append(" --StdOutput=auto");
	    installCommand.append(" --StdError=auto");
	    installCommand.append(" --Startup=auto");
	    installCommand.append(" --Classpath=");
	    installCommand.append(new File(serverLocation, "bin/winsvc.jar").getAbsolutePath());
	    installCommand.append(';');
	    installCommand.append(new File(serverLocation, "lib/felix.jar").getAbsolutePath());
	    installCommand.append(';');
	    installCommand.append(new File(serverLocation, "lib/ProtegeLauncher.jar").getAbsolutePath());
	    installCommand.append(" --JvmMx=${memory.mb}");
	    installCommand.append(" ++JvmOptions=-DentityExpansionLimit=100000000");
	    installCommand.append(" ++JvmOptions=-Dfile.encoding=UTF-8");
	    installCommand.append(" ++JvmOptions=-Djava.rmi.server.hostname=");
	    installCommand.append(getConfiguration().getParameterValue(Parameter.HOSTNAME));
	    installCommand.append(" ++JvmOptions=-Dorg.protege.owl.server.configuration=");
	    installCommand.append(new File(serverLocation, "metaproject.owl").getAbsolutePath());
	    installCommand.append(" ++JvmOptions=-Djava.util.logging.config.file=");
	    installCommand.append(new File(serverLocation, "logging.properties"));
	    installCommand.append(" --StartPath=");
	    installCommand.append(serverLocation.getAbsolutePath());
	    installCommand.append(" --StartMode=jvm");
	    installCommand.append(" --StartClass=org.protege.owl.server.deploy.windows.WindowsOWLServer");
	    installCommand.append(" --StartParams=start");
	    installCommand.append(" --StopPath=");
	    installCommand.append(serverLocation.getAbsolutePath());
	    installCommand.append(" --StopMode=jvm");
	    installCommand.append(" --StopClass=org.protege.owl.server.deploy.windows.WindowsOWLServer");
	    installCommand.append(" --StopParams=stop");
	    installCommand.append(" --LogLevel=Debug");
	    runNoAnnounce(installCommand.toString());
	}

	@Override
	protected void doUndeploy() throws IOException {
        log("Starting OWLServer Service");
        run("" + serviceManager.getAbsolutePath() + "//DS//OWLServer");
	}
}
