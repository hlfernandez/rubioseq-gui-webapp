rubioseq-gui-webapp
========================

Project for the RUbioSeq-GUI webapp.

What is RUbioSeq?
----------------------
RUbioSeq is a software suite to perform NGS data analysis in a automatic, paralellized and reliable way which is crucial to eliminate manual steps and to speed up result generation. 

You can find more information in http://rubioseq.sourceforge.net/

What is RUbioSeq-GUI?
----------------------
RUbioSeq-GUI a graphical user interface (GUI) for RUbioSeq. It works as a web application (i.e. in a browser) and it allows users to use RUbioSeq in a user-friendly way. Among the functions that can be performed with this application are: (i) configuration of RUbioSeq configProgramPaths.xml files, (ii) creation of XML configuration files, (iii) editing of existing XML configuration files and  (iv) exeuction and monitoring of experiments.

Requirements
------------
To run RUbioSeq-GUI you need:
  - Java 1.7+
  - RUbioSeq 3.7 (http://rubioseq.sourceforge.net/)
  - rubioseq-gui-persistence 1.0 (https://github.com/hlfernandez/rubioseq-gui-persistence)
  - Maven 2+
  - Git
  - ZK EE 6.5.0. (**Note**: Although ZK EE is a commercial license, you can request a free ZK Open Source License if your project is Open Source. More info: http://www.zkoss.org/license)

Installation
------------
### 1. Download
You can download the rubioseq-gui-webapp project directly from Github using the following command:
`git clone https://github.com/hlfernandez/rubioseq-gui-webapp.git`

You have also to download the rubioseq-gui-persistence project and you can download directloy from Github using the following command:
`git clone https://github.com/hlfernandez/rubioseq-gui-persistence.git`

### 2. Build
Once the rubioseq-gui-webapp and rubioseq-gui-persistence projects have been downloaded, you can build them using Maven. 

First, go to the rubioseq-gui-persistence project base directory and run the following command:
`mvn install`


Then, go to the rubioseq-gui-webapp project base directory and run the following command:
`mvn install`

The files resulting from the build are placed in the `target` directory (of the rubioseq-gui-webapp project). The files you may take are:
    - `rubioseq-gui.war`: the webapp itself. Use this file to deploy the application in a server like Tomcat or jetty.
    - `rubioseq-gui-v\*.\*.war`: the RUbioSeq-GUI distribution that contains all the neccessary to run RUbioSeq-GUI as an standalone application (`rubioseq-gui.war`, `launch-rubioseq-gui.sh` and `jetty-runner*.jar`).

### 3. Launch
After building you can find the distribution file `rubioseq-gui-v\*.\*.war` file in the `target` directory. Uncompress it and run the script `launch-rubioseq-gui.sh`, that deploys the application and opens a browser with the welcome screen of RUbioSeq-GUI.

