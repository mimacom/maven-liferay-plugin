maven-liferay-plugin
====================

Welcome to alternative Liferay Portal maven plugin repository. This maven plugin was prepared and is developed by mimacom company to streamline working with Liferay Portal. 


# Features
Plugins allow to download, install and deploy needed Liferay Portal dependencies either on local or remote repository. 
You can build Liferay Portal and override it's internal implementations.
You can build all Liferay Portal artifacts:
* Hooks,
* Themes,
* Layouts,
* Portlets,
* Services.

# Roadmap
* Include maven patching tool integration in example project.

# Getting started
Please follow these steps to setup your development environment:

1. mvn install mimacom-liferay-dependency-maven-plugin and follow it's documentation (README and maven site)

2. mvn install mimacom-liferay-adapter-api

3. mvn install mimacom-liferay-adapter; if needed create/uncomment adapter related to Liferay Portal version that you've installed in step 1

4. mvn install mimacom-liferay-builder-maven-plugin and follow it's documentation (README and maven site)

The code is licensed with Apache License 2.0.