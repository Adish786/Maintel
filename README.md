# Mantle

Mantle is the about.com commmon implemenation of Globe, which is the presentation platform of about.com sites. Mantle contains Globe specific interfaces to Selene, Proctor, and other systems. Mantle is an about.com opinionated base implementation of `globe-core`. The `mantle` project hiearchy is as follows:

    mantle
      ├── mantle-server
      ├── mantle-resources
      └── mantle-venus

### mantle-server
This project contains all of the Java code that implements the various classes that provide integration with about.com services and classes that enforce and implement business processes used for presenting the pages that about.com serves. It does not have a entry point and is not designed to be run independently. Instead, it provides a basis on top of which one can implement a presentation application that will work with to the about.com application ecosystem and will correctly make use of the about.com infrastructure.
This project also contains generic components that are designed to be used by multiple verticals and solidify best practices and conventions on common logic and layout. Any resources that are part of a component such as stylesheets and scripts files can be found in `mantle-resources`
**All code here are about.com opinionated but not specific to a vertical implementation.**

### mantle-resources
This project contains shared resources such as javascript, sass and other resource files that are part of components found in `mantle-resources`. **All code here are about.com opinionated but not specific to a vertical implementation.**

### mantle-venus
This project contains the testing framework setup for mantle components. Often these test suites are inheritable into a vertical and ran there as mantle doesn't have a display layer on it's own.

## Configuration
When running locally, Globe starts up on port 8080 for HTTP and 8443 for HTTPS.
The configuration for Globe locally is sourced from production CIA (https://nywebcfg1.ops.about.com) and the overrideConfigs.json file.

Most notably useful configurations are:
`com.about.selene.api.base.url` url of the Selene instance, to which requests will be made for data

For information on how to update the Changelog or seeing changes on mantle for your vertical of choice, please refer to `Making Changes to Mantle?`

## Setup

### Environment

* Make sure you have Oracle Java 8 installed and set as default JDK
	- For Mac:
		- Download and install [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
		- Open up your `~/.bash_profile` file.  Add the following line:

				export JAVA_HOME=`/usr/libexec/java_home -v 1.8`

	- For Ubuntu:
		- Open `Software & Updates`
		- Switch to the `Other Software` Tab and click `Add`
		- enter `deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main`
		- run `sudo apt-get install oracle-java8-installer`

  - For Windows:
    - Download and install [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
    - Make a note of the destination folder (should be something like `C:\Program Files\Java\jdk{version}`).
    - Go to `Control Panel -> System and Security -> System -> Advanced System Settings (in left pane) -> Environment Variables...`
    - Under `User Variables for [your username]`, click `New...`
    - For `Variable name`, enter `JAVA_HOME`. For `Variable value`, use the JDK install destination folder.
    - Click `OK`.
    - Click `PATH` under user variables and then `Edit...`
    - In the window that appears, add `%JAVA_HOME%\bin`.
* Install [Maven 3](https://maven.apache.org/download.cgi).  
*For Mac: if you have HomeBrew installed, `brew install maven` can be an easier alternative*
*For Windows, extract the downloaded archive and add the path to the `bin` folder within the archive to the user PATH variable, as above for the JDK.
  - E.g. if the archive was extracted to the user's Desktop, the entry `C:\Users\{username}\Desktop\apache-maven-{version}\bin` should be added to the PATH.

* Modify the Maven settings file:
	* If you don't have a `<user.home>/.m2/settings.xml` file then add this [settings.xml](https://confluence.corp.about-inc.com/download/attachments/3538953/settings.xml?version=5&modificationDate=1384205959168&api=v2) file to your `<user.home>/.m2/` directory.
	* If you already have a `<user.home>/.m2/settings.xml` file, modify it to include the following snippet:

			<mirrors>
				<mirror>
					<id>abt-repo</id>
					<name>About Maven Respository</name>
					<url>http://nymaven1.ops.about.com:8081/nexus/content/groups/public</url>
					<mirrorOf>*</mirrorOf>
				</mirror>
			</mirrors>
			<!-- Needed to enable lookup of snapshots -->
			<profiles>
				<profile>
					<id>snapshots</id>
					<repositories>
						<repository>
							<id>abt-snapshots</id>
							<url>http://nymaven1.ops.about.com:8081/nexus/content/repositories/abt-snapshots</url>
							<snapshots><enabled>true</enabled></snapshots>
						</repository>
					</repositories>
				</profile>
			</profiles>
			<activeProfiles>
				<activeProfile>snapshots</activeProfile>
			</activeProfiles>

* Install [Eclipse Mars J2EE package](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars1)

* Install the [Globe IDE Plugin](https://confluence.corp.about-inc.com/display/TECH/Globe+IDE) ***(Optional but highly recommended)***

* We use node, NVM and an internal repo called mantle-frontend-build to manage our front end assets. These setup instructions are in the local environment setup docs on [Confluence](https://dotdash.atlassian.net/wiki/spaces/GLBE/pages/2027789/Local+Environment+Setup)

### Packages

* Clone **[globe-core](https://stash.ops.about.com/projects/FRON/repos/globe-core/browse)** from git and import the project into Eclipse: __*(You can skip this step if you are not developing the globe-core project*)__
	* __*TEMP:*__ switch to the **develop** branch
	* In Eclipse, go to menu `File -> Import`
	* Select `Existing Maven Project` under the `Maven` folder
	* For "Root Directory", select the top-level `globe-core` directory
	* Uncheck all but `globe-core-server` project and hit OK
* Clone **[mantle](https://stash.ops.about.com/projects/FRON/repos/mantle/browse)** from git & import the project into Eclipse
	* __*TEMP:*__ switch to the **develop** branch
	* In Eclipse, go to menu `File -> Import`
	* Select `Existing Maven Project` under the `Maven` folder
	* For "Root Directory", select the top-level **mantle** directory
	* You can import everything, but the minimum necessary is the `mantle-server` and `mantle-resources` projects, you can uncheck the rest
* In the **Project Explorer** pane on the left, right click on any project and select `Maven-> Update Project`. Check all globe-core and mantle projects, as well as the checkbox that says `Force Update of Snapshots/Releases`
* If you are using seperate Eclipse workspace, you should add the `globe-core-server` project as a dependency for `mantle-server`:

	* In the **Project Explorer** pane on the left, right-click on the `mantle-server` project, and select `Properties` at the bottom of the menu
	* select `Java Build Path` from the tree on the left
	* select the `Projects` tab, and `Add...` the `globe-core-server` project.

### Resources
* install [`nvm`](https://github.com/creationix/nvm#install-script).
  * For Windows, download and extract the `nvm-setup.zip` file from [here](https://github.com/coreybutler/nvm-windows/releases) instead.
  * Note the destination location and add it as a user environment variable named `NVM_HOME`. Add `NVM_HOME` to the PATH.
  * If the installer asks to control an already-installed version of Node.js, click `Yes`.
* install `nodejs`
	* run `nvm install 4.2.6`
	* run `nvm use 4.2.6` *You'll need to do this everytime you start a shell session, unless you set your default version to 4.2.6 using `nvm alias default 4.2.6`*
* install `npm`
* install grunt globally via `sudo npm install -g grunt-cli`

## Compilation
Java portion of the project is compiled by maven while the resources are compiles and minified via Grunt.

* __*If you have globe-core checked out,*__ from the terminal go to the `globe-core` root directory and run `mvn clean install`.
* From the terminal, go to the `mantle` root directory and run `mvn clean install`

## Inter-Project Dependencies
 *TODO*

## Caveats
 *TODO*

## Making changes to Mantle?
* A guide has been written in [confluence](https://dotdash.atlassian.net/wiki/spaces/GLBE/pages/376242276/Make+a+Change+to+Mantle) so refer to these best practices if you need to make changes to Mantle.
* If you're updating Mantle, you should make sure your changes are recorded in the Changelog, our record of chronologically ordered changes for Mantle. To do this, you can use the Gradle Add Changelog task (ex: by running `gradle addChangelog -Pchange="Fixing CSS issue"`). See
[this wiki article](https://dotdash.atlassian.net/wiki/spaces/GLBE/pages/2927526136/Globe+Gradle+Tasks#Changelog-Tasks) for more details.
