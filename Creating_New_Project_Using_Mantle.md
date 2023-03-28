#Setting Up a New Vertical

### Create Stash Repo
Should follow the following project hierarchy
	
	myrepo
	├── myrepo-resource
	├── myrepo-server
	└── myrepo-venus
		├── myrepo-venus-test
		└── myrepo-venus-test

### Create pom.xml files
(Copy from **TODO** or use project generator **TODO**)

### Add Spring config and generate Launch file
com.about.myrepo.spring.MyRepoSprintConfiguration extends MantleSpringConfiguration

must be annotated with org.springframework.context.anotation.Configuration

create com.about.myrepo.app.MyRepoJettyApp extands AbstractMantleJettyApp
constructor must call `super("com.about.globe", true)`
1st param is CIA namespace, 2nd is Rekon registration

main() is necessary to start the app:
```
public static void main(String args[]) throws Exception {
	new MyRepoJettyApp().startServer(MyRepoSpringConfiguration.class);
}
```

Create Launch file in myrepo-server root MyRepoJettyApp.launch
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication">
<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
<listEntry value="/myrepo-server/src/main/java/com/about/myrepo/app/MyRepoJettyApp.java"/>
</listAttribute>
<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
<listEntry value="1"/>
</listAttribute>
<booleanAttribute key="org.eclipse.jdt.launching.ATTR_USE_START_ON_FIRST_THREAD" value="true"/>
<stringAttribute key="org.eclipse.jdt.launching.CLASSPATH_PROVIDER" value="org.eclipse.m2e.launchconfig.classpathProvider"/>
<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="com.about.myrepo.app.MyRepoJettyApp"/>
<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="mantle-ref-server"/>
<stringAttribute key="org.eclipse.jdt.launching.SOURCE_PATH_PROVIDER" value="org.eclipse.m2e.launchconfig.sourcepathProvider"/>
<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="-server -Xmx1024m -Dcom.sun.management.jmxremote -DconfigUrl=https://nywebcfg1.ops.about.com:8443/configuration -Denvironment=local -Dapplication=myrepo -Ddatacenter=ny -Dservername=localhost"/>
</launchConfiguration>
```

### overrideConfigs.json
Clone from mantle-ref
**TODO** make an actual guide

### Override 500 page
myrepo-server/src/main/resource/web/html/500.html

### Extend mntl-layout-html
Add mntl-javascript-resources and mntl-stylesheet-resources as desired
Add your own inclusions of custom CSS, JS

### Define Susy grid dimensions
Recommend putting this in myrepo/src/main/resources/css/_susy-settings.scss

```
@import "susy";

// Set some column counts for breakpoints
$bp-small-columns: 100%;
$bp-medium-columns: 12;
$bp-large-columns: 15;

$bp-medium: 650px;
$bp-large: 1024px;

// Set a map of base variables for a grid
$susy: (
  columns: 4,
  column-width: 45px,
  gutters: 40px/45px,
  gutter-position: after,
  global-box-sizing: border-box,
  math: static,
  debug: (image: show) // This debug tool should only be used in a dev / debug mode
);
```

### Define GPT general properties
**Very TODO**

### Create actions.xml
myrepo-server/src/main/resources/definitions
```
<?xml version="1.0" encoding="UTF-8"?>
<actions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../xsd/actions.xsd">
	<action name="reference-template" method="HEAD,GET">
		<pathPattern>/</pathPattern>
		<header name="Content-Type" value="text/html" />
		<header name="Cache-Control" value="max-age=3600, private" />
		<task name="renderTemplate">
			<property name="templateName" value="reference-template" />
		</task>
	</action>
	[...]
</actions>
```

### Create 



