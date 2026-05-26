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

import io.spring.gradle.dependencymanagement.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.springframework.boot.gradle.dependencymanagement.DependencyManagementPluginFeatures
import org.springframework.util.ReflectionUtils

class REDSDependencyManagementPlugin implements Plugin<Project> {

	private static final Logger LOG = Logging.getLogger(REDSDependencyManagementPlugin.class)

	private static final String SPRING_BOOT_VERSION = DependencyManagementPluginFeatures.package.implementationVersion

	private static final String SPRING_BOOT_BOM = "org.springframework.boot:spring-boot-starter-parent"

	private static final String SPRING_CLOUD_BOM = "org.springframework.cloud:spring-cloud-dependencies"

	@Override
	void apply(Project project) {

		project.getPlugins().apply(DependencyManagementPlugin.class)
		DependencyManagementExtension dependencyManagement = project.extensions.findByType(DependencyManagementExtension.class)

		def springBootVersion = getSpringBootVersion(project)

		if(springBootVersion == '1.4.5.RELEASE')
			configureBomsForSpring145(dependencyManagement)
		else
			throw new GradleException("Spring boot version '$SPRING_BOOT_VERSION' is not supported by REDS Platform !!")
	}

	private String getSpringBootVersion(Project project) {

		// Return the version defined in the spring boot gradle plugin found on classpath.
		return DependencyManagementPluginFeatures.package.implementationVersion
	}

	/**
	 * Configure the application for using SpringBoot 1.4.5 and SpringCloud Camden.RELEASE
	 */
	private void configureBomsForSpring145(DependencyManagementExtension dependencyManagement) {

		LOG.info("Configuring dependency management plugin to use SpringBoot 1.4.5 and Spring Cloud 'Camden.RELEASE'")

		def closure1 = {

			String springCloudBom = SPRING_CLOUD_BOM + ":Camden.SR5"
			String springBootBom =  SPRING_BOOT_BOM + ":1.4.5.RELEASE"

			ReflectionUtils.findMethod(getDelegate().getClass(), "mavenBom", String.class)
					.invoke(getDelegate(), springCloudBom)

			ReflectionUtils.findMethod(getDelegate().getClass(), "mavenBom", String.class)
					.invoke(getDelegate(), springBootBom)
		}
		dependencyManagement.imports(closure1)
	}
}
