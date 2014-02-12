# mimacom Liferay builder maven plugin
The mimacom Liferay builder maven plugin is used to build and deploy the liferay portal, themes, layouts, hooks, portlets and the service builder using maven. Follow these steps to start with a running liferay portal.

1. Download a tomcat (Version 6 or Version 7)
2. Copy it into a folder e.g. /projects/liferay
3. Append following paths to common.loader property in catalina.properties:
```
${catalina.home}/lib/liferay/*.jar,${catalina.home}/../config
```
4. Create a new parent project with this pom.xml (take care that the serverDirectory configuration matches the tomcat folder)
5. execute mvn install on this project
6. Create a new portal project with this pom.xml
7. execute mvn install on this project
8. start tomcat and go to http://localhost:8080/portal

When you have a running liferay, you can start developping Portlets, Layouts, Themes, Hooks and Services.

Enjoy!