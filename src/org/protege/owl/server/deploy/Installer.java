package org.protege.owl.server.deploy;

import java.io.IOException;

public interface Installer {

	void install() throws IOException;
	
	void deploy() throws IOException;
	
	void undeploy() throws IOException;
	
	void uninstall() throws IOException;
}
