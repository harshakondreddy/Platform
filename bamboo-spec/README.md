# This project is driven from guidelines provided on Atlassian Bamboo Tutorial at:
 https://confluence.atlassian.com/bamboo/tutorial-create-a-simple-plan-with-bamboo-java-specs-894743911.html

# To Create First Time Project:

` mvn archetype:generate -B \
    -DarchetypeGroupId=com.atlassian.bamboo -DarchetypeArtifactId=bamboo-specs-archetype \
	-DarchetypeVersion=6.10.4\
    -DgroupId=com.atlassian.bamboo -DartifactId=bamboo-specs -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=tutorial -Dtemplate=minima `
> Please note: 
    - archetypeVersion=6.10.4 is Bamboo Server Version.
    - For Honeywell Bamboo instance, it is currently on "Continuous integration powered by Atlassian Bamboo version 6.10.4 build 61009 - 21 Nov 19"
    - If this changes, we need to update the version in 'pom.xml'
    
* **'compdetails.json'** under 'src/main/resources' holds the all component-details like - repo-url, component-name, plan-key, etc...
* We will create **'PlanSpec<branch-name/project-name/customer-name>'** and respective 'component-details' will be placed under 'src/main/resources/'
* This has to be evolved, and it is not final copy.

# How to Run this project to create Bamboo Plans:

## As of now, this project has to be run from IDE (IntelliJ or Eclipse). Below are steps working in IntelliJ
 -  Go to 'PlanSpecMasterBranch.java' (taking this class for the example, you can go to your PlanSpecXXXX.java) -->> right click: click on run 'PlanSpecMaster... as main()'
 - You need to add a file with name **'.credentials'** manually in you project (regardless of IntelliJ or Eclipse).
 - This file holds your credential (LDAP Creds), so that you can update/add/ceate Bamboo Plans from your system.
 - > ** .credentails ** file must not be checked in to the repository. It is already added to .gitignore and no one should remove it from there!!
   
 - ### Below compoents are configured as adhoc Babmboo plans - 
   >  {
    "compName" : "component-task-v2",
    "planKey" : "TASKMANAGER",
    "bbProjectKey" : "RC",
    "gitSshUrl" : "ssh://git@bitbucket.honeywell.com:7999/rc/component-task-v2.git",
    "sccConfigProject" : "RPS"
  },
   >{
        "compName" : "component-hazelcast",
        "planKey" : "HAZ",
        "bbProjectKey" : "RC",
        "gitSshUrl" : "ssh://git@bitbucket.honeywell.com:7999/rc/component-hazelcast.git",
        "sccConfigProject" : "RPS"
      },
   >