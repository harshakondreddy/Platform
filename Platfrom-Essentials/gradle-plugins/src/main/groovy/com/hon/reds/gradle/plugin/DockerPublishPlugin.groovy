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

import java.io.ByteArrayOutputStream
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.internal.logging.StandardOutputCapture
import org.gradle.api.GradleException

import com.hon.reds.gradle.plugin.DockerExtension

class DockerPublishPlugin implements Plugin<Project> {

	static final String EXTENSION_NAME = 'docker'

	public static final String BUILD_DOCKER_TASKNAME = 'buildDockerImage'
	public static final String PUSH_DOCKER_TAKSNAME = 'pushDockerImage'
	public static final String TASK_GROUP = "docker"
	@Override
	public void apply(Project project) {
		project.extensions.create(EXTENSION_NAME, DockerExtension)
		project.afterEvaluate {
			DockerExtension dockerExt = project.extensions."${EXTENSION_NAME}"
			def imageName = dockerExt.repository+"/"+ project.rootProject.name

			createBuildDockerTask(project, imageName)
			createPushDocker(project, imageName)
		}
	}

	private  createBuildDockerTask(Project project, def imageName) {
		def buildImageTask = project.task(BUILD_DOCKER_TASKNAME,group : TASK_GROUP, type: Exec, description: 'Build docker image for component') {
			def default_image_with_tag = imageName + ":" + project.rootProject.version

			// Building Docker image with component version i.e. <component-name>-<version>
			commandLine 'docker', 'build', '-t', default_image_with_tag, '.'
		}
		project.tasks[BUILD_DOCKER_TASKNAME].dependsOn(project.tasks['build'])
	}

	private  createPushDocker(Project project, imageName) {
		project.task(PUSH_DOCKER_TAKSNAME, group : TASK_GROUP,  description: 'Upload docker image to registry') {
			doLast {
				def default_image_with_tag = imageName + ":" + project.rootProject.version
				def tags = ['latest']

				String gitCommitId = project.rootProject.getProperty('gitCommitId')
				if(gitCommitId) {
					tags << "${project.rootProject.version}-${gitCommitId}"
				}

				def customTagDefined = project.hasProperty('customTag')
				def inputImgDefined = project.hasProperty('inputImg')

				// Checking if custom tag was provided
				// To pass custom tag using command line, use ./gradlew pushDockerImage -PcustomTag="<tag_name>" -PinputImg="<image_name>"

				if(customTagDefined){
					// Checking if input image was not provided
					if(!inputImgDefined){
						throw new GradleException('No input image provided for custom tag!!')
					}
					else {
						def customTag = project.customTag
						def inputImg = project.inputImg

						// Tagging docker image with custom tag provided
						project.exec{
							commandLine 'docker', 'tag', inputImg, imageName + ":" + customTag
						}

						// Publishing docker image with custom tag
						project.exec{
							commandLine 'docker', 'push', imageName + ":" + customTag
						}
					}

				}

				else {
					// Publishing default docker image built with version tag
					project.exec { commandLine 'docker', 'push', default_image_with_tag }

					for (tag in tags) {
						// Tagging docker image with latest & version-gitCommitId
						project.exec {
							commandLine 'docker', 'tag', default_image_with_tag, imageName + ":" + tag
						}
						// Publishing docker images with latest & version-gitcommitId tags
						project.exec {
							commandLine 'docker', 'push', imageName + ":" + tag
						}
					}

				}

			}
		}
		project.tasks[PUSH_DOCKER_TAKSNAME].dependsOn(project.tasks['getGitCommit'])
		project.tasks[PUSH_DOCKER_TAKSNAME].dependsOn(project.tasks[BUILD_DOCKER_TASKNAME])
	}
}


