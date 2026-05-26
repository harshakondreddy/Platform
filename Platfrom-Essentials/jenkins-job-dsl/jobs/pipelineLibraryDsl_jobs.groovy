def component_details = new ConfigSlurper().parse(readFileFromWorkspace("resources/libraries.groovy"))
def librariesFolder = "libraries"
folder (librariesFolder)

component_details.libraries.each { name, data ->
    def args = [:]
    args.name = name
    args.repoUrl = data.url
    args.repo = data.repo
    args.folder = librariesFolder
    args.libName=name

    // Master Build JOBS
    args.pullrequestJob=false

    new com.intelligrated.nexgen.jenkins.LibraryJobsBuilder(args).buildLibraryJob(this)

    //PullRequest JOBS
    args.pullrequestJob=true

    new com.intelligrated.nexgen.jenkins.LibraryJobsBuilder(args).buildLibraryPRJob(this)


}

