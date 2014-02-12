# mimacom Liferay dependency maven plugin

The mimacom Liferay dependency maven plugin is used to download and install/deploy the liferay portal maven dependencies that are needed to build Liferay Portal and it's artifacts with mimacom-liferay-builder-maven-plugin.

# Setup the maven environment

* Add the mimacom maven repository definition to settings.xml:

```xml
<profiles>
    ...
    <profile>
        <id>mimacom</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <repositories>
            <repository>
              <id>mimacom-releases-open</id>
              <name>mimacom open releases repo</name>
              <url>https://nexus.mimacom.com/service/local/repositories/mimacom-releases-open/content</url>
              <layout>default</layout>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
              <id>mimacom-releases-open</id>
              <name>mimacom open releases repo</name>
              <url>https://nexus.mimacom.com/service/local/repositories/mimacom-releases-open/content</url>
              <layout>default</layout>
           </pluginRepository>
        </pluginRepositories>
    </profile>
    ...
</profiles>
```

* Add your remote maven repository definition to settings.xml (if you wish to deploy liferay dependencies for your team):

```xml
<profiles>
    ...
    <profile>
        <id>local-repo</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <repositories>
            <repository>
              <id>localhost-3rd-party</id>
              <name>Localhost Third Party Nexus</name>
              <url>http://localhost:8081/nexus/content/repositories/thirdparty</url>
              <layout>default</layout>
            </repository>
       </repositories>
    </profile>
    ...
</profiles>
```

* Download following Liferay Portal files from [sourceforge.net](http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.2.0%20GA1/):

1. [liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip](http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.2.0%20GA1/liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip/download)
2. [liferay-portal-src-6.2.0-ce-ga1-20131101192857659.zip](http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.2.0%20GA1/liferay-portal-src-6.2.0-ce-ga1-20131101192857659.zip/download)
3. [liferay-portal-dependencies-6.2.0-ce-ga1-20131101192857659.zip](http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.2.0%20GA1/liferay-portal-dependencies-6.2.0-ce-ga1-20131101192857659.zip/download)

Remove datestamp suffix "-20131101192857659" from all files. They should only have version suffix "6.2.0-ce-ga1".

* You can also try automatic download, however it often doesn't work because of changes in download URLs.

```
mvn org.mimacom.maven.plugins:mimacom-liferay-dependency-maven-plugin:downloadLiferay -Dversion=6.2.0-ce-ga1 -DfileVersion=6.2.0-ce-ga1 -DserverType=tomcat
```

* Install it into the local maven repository:

```
mvn org.mimacom.maven.plugins:mimacom-liferay-dependency-maven-plugin:installLiferay -Dversion=6.2.0-ce-ga1 -DfileVersion=6.2.0-ce-ga1 -DserverType=tomcat
```

* Or deploy it into a remote repository:

```
mvn org.mimacom.maven.plugins:mimacom-liferay-dependency-maven-plugin:deployLiferay -DrepositoryId=localhost-3rd-party -DrepositoryUrl=http://localhost:8081/nexus/content/repositories/thirdparty -Dversion=6.2.0-ce-ga1 -DfileVersion=6.2.0-ce-ga1 -DserverType=tomcat
```

Liferay Portal maven dependencies are installed. You are now able to build your Liferay Portal with with mimacom-liferay-builder-maven-plugin.