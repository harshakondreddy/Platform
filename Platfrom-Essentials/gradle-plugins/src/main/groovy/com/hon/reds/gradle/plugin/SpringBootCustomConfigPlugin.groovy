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

/**
 *
 * A plugin to maintain all custom configuration for Spring Boot Gradle Plugin that is common to all components in one place. 
 *
 */

class SpringBootCustomConfigPlugin implements Plugin<Project>{

	@Override
	void apply(Project project) {
		project.extensions.getByName('springBoot').layout = 'ZIP'
	}
}


