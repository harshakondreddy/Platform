package com.hon.reds.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.bundling.Jar

class Model2CodeGeneratorPlugin implements Plugin<Project> {
	static final String EXTENSION_NAME = 'model2code'

	private static final Logger LOG = Logging.getLogger(Model2CodeGeneratorPlugin.class)

	private static final String QUERYDSL_TASK_NAME = 'generateQueryDSL'
	private static final String CODE_GEN_TASK_NAME = 'runCodeGenerator'
	private static final String WEAVING_TASK_NAME = 'performJPAWeaving'

	private static final String METADATA_PATH_FLAG = '-mp'
	private static final String ROOT_PROJECT_PATH_FLAG = '-rpp'
	private static final String CLIENT_PROJECT_PATH_FLAG = '-cpp'
	private static final String DATABASE_FLAG = '-db'
	private static final String GENERATE_TEST_DATA_FLAG = '-gtd'
	private static final String SERVICE_NAME = '-sn'
	private static final String GENERATION_RULE = '-gr'
	private static final String CLIENT_TYPE = '-ct'
	private static final String UNIQUE_ID = '-ui'

	@Override
	void apply(Project project){
		if (!project.plugins.hasPlugin(JavaPlugin)) {
			project.plugins.apply(JavaPlugin)
		}
		project.extensions.create(EXTENSION_NAME, Model2CodeGeneratorExtension)

		project.afterEvaluate {
			def pathingJarTask = createPathingJar(project)
			createCodeGenTask(project, pathingJarTask)
			createQueryDslTask(project)
			createWeavingTask(project)
		}
	}

	private JavaCompile createQueryDslTask(Project project) {

		// Query DSL must be added to the root project
		def generatedJavaDir = 'gensrc/main/java'
		def genJavaSrc = project.file(generatedJavaDir)

		def generateQueryDSLTask = project.task(QUERYDSL_TASK_NAME, type: JavaCompile, dependsOn: CODE_GEN_TASK_NAME, description: 'Generates the QueryDSL query types') {
			source = project.sourceSets.main.java
			classpath = project.configurations.compileClasspath
			options.annotationProcessorPath = project.configurations.annotationProcessor
			destinationDir = project.file('gensrc/main/java')
		}
	}
	private JavaExec createCodeGenTask(Project project, pathingJar) {

		String buildDir = project.rootProject.buildDir
		def rootProjectDir = project.projectDir
		def codegenModelDir = project.file(project.model2code.modelDir)
		if (!codegenModelDir.exists()) {
			LOG.error("Model File is NOT FOUND: ${codegenModelDir.path}")
		}
		else{
			LOG.info("Model File is: ", codegenModelDir.path )
			println("Model File is: " + codegenModelDir.path )
		}
		def serviceName = project.rootProject.getProperty('appName')
		if (!serviceName) {
			LOG.error("serviceName is NOT FOUND: ${serviceName}")
		}
		else{
			LOG.info("serviceName is FOUND: ${serviceName}" )
			println("serviceName is FOUND: ${serviceName}" )
		}
		def outputDir1 = new File("dtos/gensrc/main/java")
		if (!outputDir1) {
			LOG.error("DTOS NOT FOUND: ${outputDir1}")
		}
		else{
			LOG.info("DTOS Location: ${outputDir1}" )
			println("DTOS Location: ${outputDir1}" )
		}
		def outputDir2 = new File("client/gensrc/main/java")
		if (!outputDir2) {
			LOG.error("CLIENT NOT FOUND: ${outputDir2}")
		}
		else{
			LOG.info("CLIENT Location: ${outputDir2}" )
		}
		def outputDir3 = new File("gensrc/main/java")

		if (!outputDir3) {
			LOG.error("GENSRC NOT FOUND: ${outputDir3}" )
		}
		else{
			LOG.info("GENSRC Location: ${outputDir3}" )
			println("GENSRC Location: ${outputDir3}" )
		}
		def outputDir4 = new File("cache/gensrc/main/java")

		if (!outputDir4) {
			LOG.error("GENSRC NOT FOUND: ${outputDir4}" )
		}
		else{
			LOG.info("GENSRC Location: ${outputDir4}" )
			println("GENSRC Location: ${outputDir4}" )
		}

		def clientType = project.model2code.clientType

		def codegenTask = project.task(CODE_GEN_TASK_NAME, type: JavaExec, description: 'Run Code generator', dependsOn: "pathingJar") {
			main = "com.hon.reds.model2code.main.CodeGenerator"
			classpath = project.configurations.compileClasspath
		}
		codegenTask.doFirst {
			classpath = project.files("$buildDir/classes/main", "$buildDir/resources/main",pathingJar.archivePath)
		}

		def uniqueId = project.rootProject.getProperty('uniqueId')

		def defaultArgs = [
			METADATA_PATH_FLAG,
			"${rootProjectDir}/src/main/resources/models",
			ROOT_PROJECT_PATH_FLAG,
			"${rootProjectDir}",
			CLIENT_PROJECT_PATH_FLAG,
			"${rootProjectDir}/client",
			DATABASE_FLAG,
			project.model2code.databaseFlag,
			SERVICE_NAME,
			serviceName,
			GENERATE_TEST_DATA_FLAG,
			project.model2code.generatedTestDataFlag,
			GENERATION_RULE,
			project.model2code.generationRule,
			CLIENT_TYPE,
			clientType,
			UNIQUE_ID,
			uniqueId
		]

		codegenTask.args(defaultArgs)
		println("Runnig CODEGEN with arguments:- ${defaultArgs}")
		codegenTask.inputs.dir(codegenModelDir)
		codegenTask.outputs.dirs outputDir1, outputDir2, outputDir3,outputDir4

		// JavaExec task always searches for main class. So remove it.
		codegenTask.dependsOn.remove('findMainClass')
		// Make sure codegen is run before java compile.
		project.tasks['compileJava'].dependsOn codegenTask
		project.tasks['compileJava'].options.annotationProcessorGeneratedSourcesDirectory = project.file('gensrc/main/java')
		return codegenTask

	}

	private  createPathingJar (Project project) {
		def pathingJarTask = project.task("pathingJar", type: Jar, description: 'crete pathing jar for long classpaths' ) {
			dependsOn project.configurations.runtimeClasspath
			doFirst {
				manifest {
					attributes "Class-Path": project.configurations.runtimeClasspath.files.collect {it.toURL().toString().replaceFirst("file:/", '/')}.join(" ")
				}
			}
		}
		return pathingJarTask
	}

	private JavaExec createWeavingTask(Project project) {
		def logLevel = 'INFO'
		if(LOG.isInfoEnabled() || LOG.isDebugEnabled() || LOG.isTraceEnabled())
		{
			logLevel = 'FINE'
		}
		def weavingTask = project.task(WEAVING_TASK_NAME, type: JavaExec, description: 'Execute static weaving') {
			inputs.dir project.compileJava.destinationDir
			outputs.dir project.compileJava.destinationDir
			main 'org.eclipse.persistence.tools.weaving.jpa.StaticWeave'
			args '-persistenceinfo',
					'gensrc/main/resources',
					'-logLevel', logLevel,
					project.compileJava.destinationDir.absolutePath,
					project.compileJava.destinationDir.absolutePath
			classpath = project.configurations.compileClasspath
		}

		project.tasks['compileJava'].doLast {
			def disableWeaving = "true".equalsIgnoreCase(System.getenv('DISABLEWEAVING'))
			LOG.lifecycle("disableWeaving = {}", disableWeaving)
			def folder = new File( 'gensrc/main/resources' )
			if (!disableWeaving && folder.exists())  {
				LOG.lifecycle("Weaving the code base")
				weavingTask.finalizedBy()
				LOG.lifecycle("Finished weaving...")
				LOG.lifecycle("Post-compile validation")
				LOG.lifecycle("Finished post-compile validation...")
			}
		}

		return weavingTask
	}


}
