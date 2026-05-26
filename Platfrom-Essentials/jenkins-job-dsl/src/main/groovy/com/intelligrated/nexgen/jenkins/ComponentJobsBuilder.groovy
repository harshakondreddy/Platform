package com.intelligrated.nexgen.jenkins

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class ComponentJobsBuilder {

    String name
    String compName
    String repoUrl
    String repo
    boolean hasSyncConsumerContractTest = false
    boolean hasSyncProviderContractTest = false
	boolean hasAsyncConsumerContractTest = false
	boolean hasAsyncProviderContractTest=false
    boolean hasIntgTest = false
    boolean hasComponenttest = true
    String folder
    boolean pullrequestJob
    boolean hasReportal = false
    String branchName

    Job buildComponentJob(DslFactory dslFactory){

        def job = dslFactory.pipelineJob("${folder}/${name}") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/component-jenkinsfile-master'))
                }
            }
            parameters {
                stringParam('componentName', compName, 'Component Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('hasSyncConsumerContractTest', hasSyncConsumerContractTest, 'True If component has Sync ConsumerContractTest')
                booleanParam('hasSyncProviderContractTest', hasSyncProviderContractTest, 'True If component has Sync ProviderContractTest')
				booleanParam('hasAsyncConsumerContractTest', hasAsyncConsumerContractTest, 'True If component has Async ConsumerContractTest')
				booleanParam('hasAsyncProviderContractTest', hasAsyncProviderContractTest, 'True If component has Async ProviderContractTest')
                booleanParam('hasIntgTest', hasIntgTest, 'True If component has IntgTest')
                booleanParam('hasComponentTest', hasComponenttest, 'True if component has component tests')
                booleanParam('hasReportal', hasReportal, 'True if component has enabled Reportportal reporting')
                //booleanParam('prJob', pullrequestJob, 'True if Pull Request Job')
            }
            triggers {
                scm(' ')
                        {
                            ignorePostCommitHooks(false)
                        }
            }
        }

    }

    Job buildComponentPRJob(DslFactory dslFactory){

        pullrequestJob = true
        def job = dslFactory.pipelineJob("${folder}/${name}-pr") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/component-jenkinsfile-pr'))
                }
            }
            parameters {
                stringParam('componentName', compName, 'Component Name')
                stringParam('branchName', branchName, 'Pull Reques Branch Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('hasSyncConsumerContractTest', hasSyncConsumerContractTest, 'True If component has SyncConsumerContractTest')
				booleanParam('hasAsyncConsumerContractTest', hasAsyncConsumerContractTest, 'True If component has Async ConsumerContractTest')
                booleanParam('prJob', pullrequestJob, 'True if Pull Request Job')
//                booleanParam('hasIntgTest', hasIntgTest, 'True If component has IntgTest')
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
