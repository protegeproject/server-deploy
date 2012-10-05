package org.protege.owl.server.windows.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.protege.osgi.framework.Launcher;
import org.xml.sax.SAXException;


public class WindowsOWLServer {
    private static WindowsOWLServer instance;
    private Framework framework;
    
    
    
    private WindowsOWLServer() {
        ;
    }
    
    public static WindowsOWLServer getInstance() {
        if (instance == null) {
            instance = new WindowsOWLServer();
        }
        return instance;
    }
    
    public void start() throws IOException, ParserConfigurationException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, InterruptedException {
        Launcher launcher = new Launcher(new File("config.xml"));
        launcher.start(true);
        framework = launcher.getFramework();
    }
    
    public void stop() throws BundleException {
        framework.stop();
    }
    
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, InterruptedException {
        String cmd = "start";
        if(args.length > 0) {
           cmd = args[0];
        }
      
        if("start".equals(cmd)) {
           getInstance().start();
        }
        else {
           getInstance().stop();
        }
    }

}
