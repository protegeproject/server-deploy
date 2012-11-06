REM User configurable variables

set hostname=localhost
set memory=700M

REM End of user configurable variables section
REM Keep configuring if you like but this should be less likely

set classpath=lib\felix.jar;lib\ProtegeLauncher.jar


java.exe -Djava.awt.headless=true ^
         -Xmx%memory% ^
         -Djava.rmi.server.hostname=%hostname% ^
         -DentityExpansionLimit=1000000 -Dfile.encoding=UTF-8 ^
         -Dorg.protege.owl.server.configuration=metaproject.owl ^
         -Djava.util.logging.config.file=logging.properties ^
         -classpath %classpath% ^
         org.protege.osgi.framework.Launcher > ${LOG_DIR}/cmdline 2>&1 &
