package com.hon.reds.gradle.plugin

import org.ajoberstar.grgit.Grgit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class GitCommitDeterminerPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		Task gitCommitIdDeterminerTask =  project.task("getGitCommit") doLast {

			Grgit git = Grgit.open(project.rootProject.projectDir)
			String gitCommitId = git.head().abbreviatedId

			project.ext.set("gitCommitId", gitCommitId)
		}

		project.tasks['generateCommonFiles'].dependsOn("getGitCommit")
	}
}
