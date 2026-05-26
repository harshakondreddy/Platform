/*
 *
 *   ---------------------------------------------------------------------
 *    HONEYWELL International Inc.
 *
 *    101 Columbia Road, Morristown
 *
 *    Morris Plains, New Jersey, United States
 *
 *   ---------------------------------------------------------------------
 *
 *    Copyright © 2018 Honeywell International Inc.
 *
 *    UNPUBLISHED - ALL RIGHTS RESERVED UNDER THE COPYRIGHT LAWS.
 *
 *    PROPRIETARY AND CONFIDENTIAL INFORMATION.
 *
 *    DISTRIBUTION, USE AND DISCLOSURE RESTRICTED BY HONEYWELL.
 *
 *   ---------------------------------------------------------------------
 *
 */

package com.hon.reds.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.SourceSet

class TestTasksPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		project.afterEvaluate {   createIntegrationTestTasks(project)  }
	}

	private void createIntegrationTestTasks(Project project){

		project.sourceSets{
			integrationTest{
				java.srcDirs project.file ("src/integrationtest/java")
				compileClasspath += main.output + test.output
				runtimeClasspath += main.output + test.output
				resources.srcDirs project.file("src/integrationtest/resources")
			}

			componentTest{
				java.srcDirs project.file ("src/integrationtest/java")
				compileClasspath += main.output + test.output
				runtimeClasspath += main.output + test.output
				resources.srcDirs project.file("src/integrationtest/resources")
			}

			syncConsumerContractTest{
				java.srcDirs project.file ("src/contracttest/sync/consumer/java")
				compileClasspath += main.output + test.output
				runtimeClasspath += main.output + test.output
				resources.srcDirs project.file("src/contracttest/sync/consumer/resources")
			}

			syncProviderContractTest{
				java.srcDirs project.file ("src/contracttest/sync/provider/java")
				compileClasspath += main.output + test.output
				runtimeClasspath += main.output + test.output
				resources.srcDirs project.file("src/contracttest/sync/provider/resources")
			}

			asyncConsumerContractTest{
				java.srcDirs project.file ("src/contracttest/async/consumer/java")
				compileClasspath += main.output + test.output
				runtimeClasspath += main.output + test.output
				resources.srcDirs project.file("src/contracttest/async/consumer/resources")
			}

			asyncProviderContractTest{
				java.srcDirs project.file ("src/contracttest/async/provider/java")
				compileClasspath += main.output + test.output
				runtimeClasspath += main.output + test.output
				resources.srcDirs project.file("src/contracttest/async/provider/resources")
			}
		}

		project.configurations {

			componentTestImplementation.extendsFrom testImplementation
			componentTestRuntime.extendsFrom testRuntime

			integrationTestImplementation.extendsFrom testImplementation
			integrationTestRuntime.extendsFrom testRuntime

			syncConsumerContractTestImplementation.extendsFrom testImplementation
			syncConsumerContractTestRuntime.extendsFrom testRuntime

			syncProviderContractTestImplementation.extendsFrom testImplementation
			syncProviderContractTestRuntime.extendsFrom testRuntime

			asyncConsumerContractTestImplementation.extendsFrom testImplementation
			asyncConsumerContractTestRuntime.extendsFrom testRuntime

			asyncProviderContractTestImplementation.extendsFrom testImplementation
			asyncProviderContractTestRuntime.extendsFrom testRuntime
		}

		project.dependencies {

			integrationTestImplementation project.sourceSets.main.output
			integrationTestImplementation project.sourceSets.test.output

			componentTestImplementation project.sourceSets.main.output
			componentTestImplementation project.sourceSets.test.output

			syncConsumerContractTestImplementation project.sourceSets.main.output
			syncConsumerContractTestImplementation project.sourceSets.test.output

			syncProviderContractTestImplementation project.sourceSets.main.output
			syncProviderContractTestImplementation project.sourceSets.test.output

			asyncConsumerContractTestImplementation project.sourceSets.main.output
			asyncConsumerContractTestImplementation project.sourceSets.test.output

			asyncProviderContractTestImplementation project.sourceSets.main.output
			asyncProviderContractTestImplementation project.sourceSets.test.output
		}


		project.task('integrationTest', type: Test){
			systemProperties = System.properties
			systemProperties["mode"] = "online"
			systemProperties["spring.profiles.active"] = "integrationTest"

			def cucumberTags = "--tags @IntegrationTest"
			if(project.hasProperty("dependentComponent")) {
				cucumberTags += " --tags @" + project.rootProject.name + "-" + project.getProperty("dependentComponent")
			}

			systemProperties["cucumber.options"] = cucumberTags + " --plugin json:target/IntegrationTest/cucumber.json" + " --plugin com.epam.reportportal.cucumber.StepReporter"
			//testClassesDir = project.sourceSets.integrationTest.output.classesDir
			testClassesDirs = project.sourceSets.integrationTest.output.classesDirs
			classpath = project.sourceSets.integrationTest.runtimeClasspath
			testLogging.showStandardStreams = true
		}

		project.task('componentTest', type: Test){
			systemProperties = System.properties
			systemProperties["mode"] = "offline"
			systemProperties["spring.profiles.active"] = "componentTest"
			systemProperties["cucumber.options"] = "--tags @ComponentTest --plugin json:target/ComponentTest/cucumber.json" + " --plugin com.epam.reportportal.cucumber.StepReporter"
			testClassesDirs = project.sourceSets.integrationTest.output.classesDirs
			classpath =  project.sourceSets.integrationTest.runtimeClasspath
			testLogging.showStandardStreams = true
		}

		project.task('syncConsumerContractTest', type: Test){
			testClassesDirs = project.sourceSets.syncConsumerContractTest.output.classesDirs
			classpath = project.sourceSets.syncConsumerContractTest.runtimeClasspath
			testLogging.showStandardStreams = true
		}

		project.task('syncProviderContractTest', type: Test){
			def zipDir = "${project.rootDir}/contracts"
			project.configurations.testCompile.each { File f ->
				if (f.name.endsWith(".zip")) {
					project.copy {
						from project.zipTree(f)
						into zipDir
					}
				}
			}
			testClassesDirs = project.sourceSets.syncProviderContractTest.output.classesDirs
			classpath = project.sourceSets.syncProviderContractTest.runtimeClasspath
			testLogging.showStandardStreams = true
		}

		project.task('asyncConsumerContractTest', type: Test){
			testClassesDirs = project.sourceSets.asyncConsumerContractTest.output.classesDirs
			classpath = project.sourceSets.asyncConsumerContractTest.runtimeClasspath
			testLogging.showStandardStreams = true
		}

		project.task('asyncProviderContractTest', type: Test){
			testClassesDirs = project.sourceSets.asyncProviderContractTest.output.classesDirs
			classpath = project.sourceSets.asyncProviderContractTest.runtimeClasspath
			testLogging.showStandardStreams = true
		}

		project.task('createHTMLReport',type: JavaExec){
			def testType = ""
			systemProperties = System.properties
			if(systemProperties["mode"]=="offline")
				testType = "componentTest"
			else
				testType = "integrationTest"
			classpath = project.sourceSets.test.runtimeClasspath
			main = "com.intelligrated.testUtils.ReportGenerator"
			args testType
		}
	}
}
