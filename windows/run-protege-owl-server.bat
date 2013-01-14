@ECHO OFF

REM User configurable variables

set hostname=@hostname@
set memory=@memory@
set user=@sandbox.user@
set install.dir=C:\Program Files\Protege OWL Server

REM End of user configurable variables section

REM Keep configuring if you like but this should be less likely

set classpath=lib\felix.jar;lib\ProtegeLauncher.jar

set command=java.exe -Djava.awt.headless=true
set command=%command% -Xmx%memory% -Djava.rmi.server.hostname=%hostname%
set command=%command% -DentityExpansionLimit=1000000 -Dfile.encoding=UTF-8
set command=%command% -Dorg.protege.owl.server.configuration=metaproject.owl
set command=%command% -Djava.util.logging.config.file=logging.properties
set command=%command% -classpath %classpath% org.protege.osgi.framework.Launcher

runas "/user:%user%" "cmd /k cd %install.dir% & %command%"
