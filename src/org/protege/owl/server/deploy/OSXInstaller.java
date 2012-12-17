package org.protege.owl.server.deploy;

public class OSXInstaller extends AbstractInstaller {
	
	public OSXInstaller(Configuration configuration) {
		super(configuration);
	}
	
	@Override
	protected void postInstall() {
		throw new IllegalStateException("Not implemented yet");
	}

	@Override
	protected void postDeploy() {
		throw new IllegalStateException("Not implemented yet");
	}

	@Override
	protected void doUndeploy() {
		throw new IllegalStateException("Not implemented yet");
	}
}
