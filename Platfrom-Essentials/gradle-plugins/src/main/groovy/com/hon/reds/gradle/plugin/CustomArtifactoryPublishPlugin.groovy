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
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
import java.nio.file.*
import java.nio.file.Paths

import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPluginUtil
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention


class CustomArtifactoryPublishPlugin implements Plugin<Project> {

	static final String EXTENSION_NAME = 'artifactory'


	@Override
	void apply(Project project) {
		project.extensions.create(EXTENSION_NAME, ArtifactoryPublishPluginExtension)
		ArtifactoryPublishPluginExtension artifactoryExt = project.extensions."${EXTENSION_NAME}"

		if(!project.getPlugins().hasPlugin(ArtifactoryPlugin.class)){
			project.getPlugins().apply(ArtifactoryPlugin.class)
		}

		ArtifactoryPluginConvention convention = ArtifactoryPluginUtil.getArtifactoryConvention(project)

		project.task("sourceJar", type: Jar) {
			classifier = 'sources'
			from project.sourceSets.main.allSource
		}
		project.task("contractsZip", type: Zip) {
			outputs.upToDateWhen { false }
			from('target/pacts/')
			destinationDir = project.file("${project.buildDir}/libs")
			archiveName = "${project.rootProject.name}-contracts.zip"
		}
		project.artifacts {
			String zipPath = "${project.buildDir}/libs/${project.rootProject.name}-contracts.zip"
			File zipFile = new File(zipPath)
			if(project.rootProject == project){
				if(project.getPlugins().hasPlugin(ExtensionJarsZipPlugin.class)){
					archives project.tasks['zipCustomDeps']
				}
				if(zipFile.exists()){
					archives project.tasks['contractsZip']
				}
			}
			archives project.tasks['sourceJar']
		}
		project.tasks['artifactoryPublish'].dependsOn(project.tasks['sourceJar'])
		project.tasks['artifactoryPublish'].dependsOn(project.tasks['contractsZip'])

		project.afterEvaluate(){

			Closure artifactory = {

				convention.contextUrl = artifactoryExt.artifactoryUrl
				convention.publish(){
					repository(){
						String version = project.rootProject.version
						repoKey = version.endsWith('SNAPSHOT') ? artifactoryExt.unstableRepo : artifactoryExt.stableRepo
						username = project.rootProject.getProperty('artifactoryUser')
						password = project.rootProject.getProperty('artifactoryPassword')
					}

					defaults(){
						publishConfigs('archives')
						publishIvy = true
						publishPom = true
					}
				}
			}
			project.tasks['artifactoryPublish'].configure(artifactory)
		}

		project.tasks['sourceJar'].dependsOn('compileJava')
		project.tasks['contractsZip'].dependsOn('compileJava')
	}
}
