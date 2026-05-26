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
import org.gradle.api.tasks.bundling.Zip


/**
 *
 * Plugin for packaging extension library along with its dependencies into a Zip.
 * Extension dependencies must be configured with custom configuration 'extJars'.
 * The zip would be created under '<component>/build/customDeps>'
 *
 */

class ExtensionJarsZipPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		project.configurations { compile.extendsFrom extJars }

		project.task("zipCustomDeps", type: Zip)  {
			from project.jar
			from project.configurations.extJars
			destinationDir = project.file("${project.buildDir}/customDeps")
		}

		project.afterEvaluate() {
			project.tasks['artifactoryPublish'].dependsOn('zipCustomDeps')
			project.tasks['zipCustomDeps'].dependsOn('compileJava')
		}
	}
}
