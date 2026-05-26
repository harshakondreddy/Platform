def component_details = new ConfigSlurper().parse(readFileFromWorkspace("resources/components.groovy"))
def componentFolder = "reds-customer-builds"
folder (componentFolder)

component_details.components.each { name, data ->
    def args = [:]
    args.name = name
    args.repoUrl = data.url
    args.repo = data.repo
    args.hasIntgTest = data.hasIntgTest
    args.hasComponenttest = data.hasComponenttest
    args.folder = componentFolder
    args.compName=name

    // BL-release Build JOBS
    args.pullrequestJob=false

    new com.intelligrated.nexgen.jenkins.CustomerJobsBuilder(args).buildBLReleaseJob(this)

    //BL-release PR JOBS
    args.pullrequestJob=true
    args.hasIntgTest = false

    new com.intelligrated.nexgen.jenkins.CustomerJobsBuilder(args).buildBLReleasePRJob(this)

     // DMWH-release Build JOBS
    args.pullrequestJob=false

    new com.intelligrated.nexgen.jenkins.CustomerJobsBuilder(args).buildDMWHReleaseJob(this)

    //DMWH-release PR JOBS
    args.pullrequestJob=true
    args.hasIntgTest = false

    new com.intelligrated.nexgen.jenkins.CustomerJobsBuilder(args).buildDMWHReleasePRJob(this)

	//target-release Build JOBS
	args.pullrequestJob=false

	new com.intelligrated.nexgen.jenkins.CustomerJobsBuilder(args).buildTargetReleaseJob(this)

	//target-release PR JOBS
	args.pullrequestJob=true
	args.hasIntgTest = false

	new com.intelligrated.nexgen.jenkins.CustomerJobsBuilder(args).buildTargetReleasePRJob(this)

	
	
}

