package com.hon.reds.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.text.SimpleDateFormat

class CommonFilesGeneratorPlugin implements Plugin<Project> {

	private static final String XMX_DEFAULT_VALUE = "256m"

	private static final GEN_COMMON_FILES_TASK_NAME = "generateCommonFiles"

	private static final String GRADLE_PLUGINS_VERSION = CommonFilesGeneratorPlugin.class.getPackage().getImplementationVersion()

	private final Properties placeholders = new Properties()

	private static final String versionFile = 'common-files.properties'


	@Override
	void apply(Project project) {

		project.extensions.create("commonFiles", CommonFilesGeneratorPluginExtension)

		Task commonFilesTask =  project.task(GEN_COMMON_FILES_TASK_NAME) doLast {

			createVersionFile(project)

			String projectDir = project.rootProject.projectDir
			String componentVersion = project.rootProject.version
			String platformVersion = project.getProperty('redsStarterVersion')
			String gitCommitId = project.rootProject.getProperty('gitCommitId')
			String rootProjectName = project.rootProject.name
			String appName = project.rootProject.getProperty('appName')
			String rootProjectUniqueId  = project.getProperty('uniqueId')
			// Adding appHasDb property
			Boolean appHasDb = project.commonFiles.appHasDb
			appHasDb = appHasDb != null ? appHasDb : true

			String xmxValue = project.commonFiles.xmxValue
			xmxValue = xmxValue != null ? xmxValue : XMX_DEFAULT_VALUE

			Boolean overwrite = project.commonFiles.overwrite
			overwrite = overwrite != null ? overwrite : false

			Integer basePort = project.commonFiles.basePort

			// Creating META-INF/build-info.properties as a placeholder for updating generated file
			def meta_inf_dir = new File(projectDir+"/src/main/resources/META-INF","build-info.properties")
			// This file needs to be created for every build, there may be change in Commit-Id, Version or any other buildInfo
			meta_inf_dir.parentFile.mkdir()

			placeholders.clear()

			placeholders.setProperty("pluginVersion", GRADLE_PLUGINS_VERSION)
			placeholders.setProperty("componentVersion", componentVersion)
			placeholders.setProperty("platformVersion", platformVersion)
			placeholders.setProperty("generatedTimestamp", new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a z").format(new Date()))
			placeholders.setProperty("gitCommitId", gitCommitId)
			placeholders.setProperty("appName", appName)
			placeholders.setProperty("appPackageName", appName.replaceAll("-",".").toLowerCase())
			placeholders.setProperty("rootProjectName", rootProjectName)
			placeholders.setProperty("rootProjectUniqueId", rootProjectUniqueId)

			placeholders.setProperty("xmxValue", xmxValue)

			placeholders.setProperty("httpPort", String.valueOf(basePort + 1))
			placeholders.setProperty("dbPort", String.valueOf(basePort + 5))
			placeholders.setProperty("appHasDb", String.valueOf(appHasDb))

			// Spring config files
			generateFile("bootstrap.yml.template", projectDir + "/src/main/resources/bootstrap.yml", placeholders, overwrite)
			generateFile("application.yml.template", projectDir + "/src/main/resources/application.yml", placeholders, overwrite)
			generateFile("application-docker.yml.template", projectDir + "/src/main/resources/application-docker.yml", placeholders, overwrite)
			generateFile("application-mssql.yml.template", projectDir + "/src/main/resources/application-mssql.yml", placeholders, overwrite)
			// Build-info file
			generateFile("build-info.properties.template", projectDir + "/src/main/resources/META-INF/build-info.properties", placeholders, overwrite)
			// Dockerfile and Entrypoint.sh files
			generateFile("Dockerfile.template", projectDir + "/Dockerfile", placeholders, overwrite)
			generateFile("entrypoint.sh.template", projectDir + "/entrypoint.sh", placeholders, overwrite)
			// log4j2 configuration generation
			generateFile("log4j2-spring.yml.template", projectDir + "/src/main/resources/log4j2-spring.yml", placeholders, overwrite)
			generateFile("log4j2-spring-docker.yml.template", projectDir + "/src/main/resources/log4j2-spring-docker.yml", placeholders, overwrite)
			generateFile("log4j2-spring-docker-file.yml.template", projectDir + "/src/main/resources/log4j2-spring-docker-file.yml", placeholders, overwrite)
			generateFile("log4j2-spring-docker-console.yml.template", projectDir + "/src/main/resources/log4j2-spring-docker-console.yml", placeholders, overwrite)
			generateFile("log4j2.component.properties.template", projectDir + "/src/main/resources/log4j2.component.properties", placeholders, overwrite)
		}

		// Do not re-generate the files if clean has not been run or the version file has been modified.
		// Not doing so will result in these files getting generated again, making gradle re-run the tests as well.
		commonFilesTask.onlyIf {

			def versionFile = new File(project.buildDir, versionFile)
			if(versionFile.exists()) {
				// compare the version
				Properties props = new Properties()
				props.load(versionFile.newDataInputStream())
				String version = props.getProperty("version")
				if(version != GRADLE_PLUGINS_VERSION)  {
					return true
				} else {
					println "gradle plugins version is unchanged. Skip generating common files."
					return false
				}
			} else {
				return true
			}
		}

		project.tasks['compileJava'].dependsOn(GEN_COMMON_FILES_TASK_NAME)
	}

	void createVersionFile(Project project) {

		def commonArtifactsFile = new File(project.buildDir, versionFile)
		commonArtifactsFile.getParentFile().mkdirs()
		commonArtifactsFile.createNewFile()
		Properties properties = new Properties()
		properties.put("version", GRADLE_PLUGINS_VERSION)

		properties.store(commonArtifactsFile.newWriter(), null)
	}

	private void generateFile(String templateName, String targetPath, Properties placeholderProps, boolean overwrite) {

		String content = com.hon.reds.gradle.plugin.util.PluginUtils.readFile("commonFiles/" + templateName, GEN_COMMON_FILES_TASK_NAME)

		if(content != null) {

			content = replacePlaceholders(content, placeholderProps)
			com.hon.reds.gradle.plugin.util.PluginUtils.writeFile(content, targetPath, overwrite, GEN_COMMON_FILES_TASK_NAME)
		}
	}

	private String replacePlaceholders(String fileData, Properties properties) {

		for(Object k : properties.keySet()) {

			String key = (String) k
			String val = (String) properties.get(key)

			fileData = fileData.replaceAll("@@" + key + "@@", val)
		}

		return fileData
	}
}


class CommonFilesGeneratorPluginExtension {

	String xmxValue = '256m'
	Integer basePort = null
	Boolean overwrite = true
	Boolean appHasDb = true
}