def component_details = new ConfigSlurper().parse(readFileFromWorkspace("resources/ref-components.groovy"))

def refArchFolder = "ref-arch"
folder (refArchFolder)
component_details.components.each { name, data ->
    def args = [:]
    args.name = name
    args.repoUrl = data.url
    args.repo = data.repo
    args.hasSyncConsumerContractTest = data.hasSyncConsumerContractTest
	args.hasAsyncConsumerContractTest = data.hasAsyncConsumerContractTest
    args.folder = refArchFolder
    args.compName=name

    // Master Build JOBS
    args.pullrequestJob=false

    new com.intelligrated.nexgen.jenkins.ComponentJobsBuilder(args).buildComponentJob(this)

    //PullRequest JOBS
    args.pullrequestJob=true

    new com.intelligrated.nexgen.jenkins.ComponentJobsBuilder(args).buildComponentPRJob(this)


}


