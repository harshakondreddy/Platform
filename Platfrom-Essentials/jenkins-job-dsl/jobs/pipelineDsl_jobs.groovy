def component_details = new ConfigSlurper().parse(readFileFromWorkspace("resources/components.groovy"))
def componentFolder = "components"
folder (componentFolder)

component_details.components.each { name, data ->
    def args = [:]
    args.name = name
    args.repoUrl = data.url
    args.repo = data.repo
    args.hasSyncConsumerContractTest = data.hasSyncConsumerContractTest
	args.hasSyncProviderContractTest = data.hasSyncProviderContractTest
	args.hasAsyncConsumerContractTest = data.hasAsyncConsumerContractTest
	args.hasAsyncProviderContractTest = data.hasAsyncProviderContractTest
    args.hasIntgTest = data.hasIntgTest
    args.hasComponenttest = data.hasComponenttest
    args.folder = componentFolder
    args.compName=name
    args.hasReportal = data.hasReportal

    // Master Build JOBS
    args.pullrequestJob=false

    new com.intelligrated.nexgen.jenkins.ComponentJobsBuilder(args).buildComponentJob(this)

    //PullRequest JOBS
    args.pullrequestJob=true
    args.hasIntgTest = false

    new com.intelligrated.nexgen.jenkins.ComponentJobsBuilder(args).buildComponentPRJob(this)


}

