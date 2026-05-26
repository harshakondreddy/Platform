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
 *    Copyright � 2018 Honeywell International Inc.
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

import org.gradle.testing.jacoco.plugins.JacocoPlugin

class CodeCoveragePlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		if(!project.getPlugins().hasPlugin(JacocoPlugin.class)){
			project.getPlugins().apply(JacocoPlugin.class)
		}

		project.afterEvaluate(){
			project.jacocoTestReport{
				classDirectories =
						project.fileTree(dir: "${project.buildDir}/classes", excludes: ['gensrc/**'])
				sourceDirectories = project.files(["src/main/java"])
				reports {   xml.enabled = true  }
			}
		}
	}
}
