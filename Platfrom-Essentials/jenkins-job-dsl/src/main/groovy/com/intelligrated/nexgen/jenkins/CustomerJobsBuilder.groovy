package com.intelligrated.nexgen.jenkins

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class CustomerJobsBuilder {

    String name
    String compName
    String repoUrl
    String repo
    boolean hasIntgTest = false
    boolean hasComponenttest = true
    String folder
    boolean pullrequestJob

    Job buildBLReleaseJob(DslFactory dslFactory){

        def job = dslFactory.pipelineJob("${folder}/bl-release/${name}-bl-release") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/bl-release-jenkinsfile'))
                }
            }
            parameters {
                stringParam('componentName', compName, 'Component Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('hasIntgTest', hasIntgTest, 'True If component has IntgTest')
                booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
            }
            triggers {
                scm(' ')
                        {
                            ignorePostCommitHooks(false)
                        }
            }
        }

    }

    Job buildBLReleasePRJob(DslFactory dslFactory){

        pullrequestJob = true
        def job = dslFactory.pipelineJob("${folder}/bl-release/${name}-bl-release-pr") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/bl-release-jenkinsfile-pr'))
                }
            }
            parameters {
                stringParam('componentName', compName, 'Component Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('prJob', pullrequestJob, 'True if Pull Request Job')
                booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
            }
            triggers {
                scm(' ')
                        {
                            ignorePostCommitHooks(false)
                        }
            }
        }

    }

    Job buildDMWHReleaseJob(DslFactory dslFactory){

        def job = dslFactory.pipelineJob("${folder}/dmwh-release/${name}-dmwh-release") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/dmwh-release-jenkinsfile'))
                }
            }
            parameters {
                stringParam('componentName', compName, 'Component Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('hasIntgTest', hasIntgTest, 'True If component has IntgTest')
                booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
            }
            triggers {
                scm(' ')
                        {
                            ignorePostCommitHooks(false)
                        }
            }
        }

    }

    Job buildDMWHReleasePRJob(DslFactory dslFactory){

        pullrequestJob = true
        def job = dslFactory.pipelineJob("${folder}/dmwh-release/${name}-dmwh-release-pr") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/dmwh-release-jenkinsfile-pr'))
                }
            }
            parameters {
                stringParam('componentName', compName, 'Component Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('prJob', pullrequestJob, 'True if Pull Request Job')
                booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
            }
            triggers {
                scm(' ')
                        {
                            ignorePostCommitHooks(false)
                        }
            }
        }

    }
	
	Job buildTargetReleaseJob(DslFactory dslFactory){
		
				def job = dslFactory.pipelineJob("${folder}/target-release/${name}-target-release") {
					definition {
						cps {
							sandbox()
							script(dslFactory.readFileFromWorkspace('resources/target-release-jenkinsfile'))
						}
					}
					parameters {
						stringParam('componentName', compName, 'Component Name')
						stringParam('repoUrl', repoUrl, 'Repository URL')
						stringParam('repo', repo, 'Repository Name')
						booleanParam('hasIntgTest', hasIntgTest, 'True If component has IntgTest')
						booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
					}
					triggers {
						scm(' ')
								{
									ignorePostCommitHooks(false)
								}
					}
				}
		
	 }

	 Job buildTargetReleasePRJob(DslFactory dslFactory){
		 
				 pullrequestJob = true
				 def job = dslFactory.pipelineJob("${folder}/target-release/${name}-target-release-pr") {
					 definition {
						 cps {
							 sandbox()
							 script(dslFactory.readFileFromWorkspace('resources/target-release-jenkinsfile-pr'))
						 }
					 }
					 parameters {
						 stringParam('componentName', compName, 'Component Name')
						 stringParam('repoUrl', repoUrl, 'Repository URL')
						 stringParam('repo', repo, 'Repository Name')
						 booleanParam('prJob', pullrequestJob, 'True if Pull Request Job')
						 booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
					 }
					 triggers {
						 scm(' ')
								 {
									 ignorePostCommitHooks(false)
								 }
					 }
				 }
		 
	 }

}
