package com.intelligrated.nexgen.jenkins

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class LibraryJobsBuilder {

    String name
    String libName
    String repoUrl
    String repo
    String folder
    boolean pullrequestJob

    Job buildLibraryJob(DslFactory dslFactory){

        def job = dslFactory.pipelineJob("${folder}/${name}") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/library-jenkinsfile-master'))
                }
            }
            parameters {
                stringParam('libraryName', libName, 'Library Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')               
            }
        }
    }

    Job buildLibraryPRJob(DslFactory dslFactory){

        pullrequestJob = true
        def job = dslFactory.pipelineJob("${folder}/${name}-pr") {
            definition {
                cps {
                    sandbox()
                    script(dslFactory.readFileFromWorkspace('resources/library-jenkinsfile-pr'))
                }
            }
            parameters {
                stringParam('libraryName', libName, 'Library Name')
                stringParam('repoUrl', repoUrl, 'Repository URL')
                stringParam('repo', repo, 'Repository Name')
                booleanParam('prJob', pullrequestJob, 'True if Pull Request Job')
            }
        }
    }
}
