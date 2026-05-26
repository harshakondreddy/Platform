    pipeline {
    agent none
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 3, unit: 'HOURS')
        timestamps()
			}
    environment { CI = 'true' }
    
    stages {
        
		stage("Read Pre-Manifest") {
			agent { label 'THD-E2E-Server' }
			steps {
					step([$class: 'WsCleanup'])
					echo "<<-----------------Reading Pre-Manifest File Stage BEGINS------------------->>"
					script {
						
						sh """
                         cd /data/jenkins/workspace/
						   """
						//Checkout source code from given repository on 'master' branch.
                        checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
                                        extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
                                        userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/thdd/manifests.git"]]])
                        sh """
                            #cd /data/jenkins/workspace/manifests
                            figlet PRE MANIFEST STAGE
							chmod 775 * && python reading_yml.py
							cat bronze_tags_file
                            echo "Adding exiting Bronze tags to file <<bronze_tags_file>> for E2E deployment. This is like pre-manifest. TODD: check on this logic!!"
                            git add bronze_tags_file
							git commit -m "updating bronze_tags_file by Build Number: ${currentBuild.number}" || true
							git push origin master

						"""
						
						dir('/data/jenkins/workspace/manifests') {
                        sh 'pwd'
                        //stash includes: '**/bronze_tags_file, deploy_bronze.py, promote_gold_tag.py, preManSync.py, depManParser.py, compManParser.py', name: 'tags_file' 
                        }
					}
				}
				    
				}
		stage("Deploy Components") {
			agent {label 'THD-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					//dir("$WORKSPACE") { unstash 'tags_file'}
					//Checkout source code from given repository on 'master' branch.
                        checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
                                        extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
                                        userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/thdd/manifests.git"]]])
					echo "<<-----------------Updating SCC server and GIT repo------------------->>"
                        sh """
                                docker-compose -f /data/reds-qa/docker-compose-components.yml stop
                                #docker-compose -f /data/reds-qa/docker-compose-components.yml rm
                                docker rm -f \$(docker ps -aq --filter "name=sccserver") | true
                                /data/reds-qa/start-components.sh /data/reds-qa/docker-compose-scc.yml
                                cowsay "Deploying latest SCC"
                                sleep 60s 
                                docker rm -f \$(docker ps -aq --filter "name=hazelcast") | true
                                /data/reds-qa/start-components.sh /data/reds-qa/docker-compose-hazelcast.yml
                                docker ps -a 
                                cowsay "Deploying latest Hazelcast"
                                sleep 60s
														
							"""
					sh """
								echo "<<-----------------Deployment Stage BEGINS------------------->>"
								figlet DEPLOYMENT STARTS
								cat bronze_tags_file
							    sleep 5s
							    #docker ps -a |cut -c 78-129 >temp.txt
							    #sed -e 's/ \\s\\+//g' temp.txt > new_deployed
							    #sed -e 's/:\\+/\\ /g' new_deployed > deployed_tags
							    #grep -vxFf deployed_tags bronze_tags_file > deploying_bronze || echo "All the tags are already deployed in the selected Environment"
							    #mv deploying_bronze bronze_tags_file
							    #rm -f new_deployed deploying_bronze temp.txt deployed_tags -f
								python deploy_bronze.py
								cowsay "Deployed components now, Let's wait for Components to come up"
								sleep 600s
								docker ps -a
								
						"""
					dir("$WORKSPACE") {
								sh 'pwd'
								stash includes: '**/ bronze_tags_file, preManSync.py, promote_gold_tag.py', name: 'tags_file'
		                }
		             }
				    post {
					    success{
						mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }
			}
					
		stage("Execute Test E2E Tests") {
			agent {label 'THD-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					dir("$WORKSPACE") { unstash 'tags_file'}
					sh """
						    echo "<<-----------------Execute E2E Tests Stage BEGINS------------------->>"
						    figlet E2E STARTS
							docker rm -f \$(docker ps -aq --filter "name=e2e") | true
                            docker images |grep basee2e
                            docker images |grep basee2e |awk '{print \$3}'
                            docker rmi -f \$(docker images |grep basee2e |awk '{print \$3}')							
							/data/reds-qa/start-components.sh /data/reds-qa/docker-compose-components.yml e2e
							fortune | cowsay -f ghostbusters
							sleep 120s
							curl -v http://10.224.92.207:10301/endtoend/runtests | true
							sleep 20s
						"""
					

					
					dir("$WORKSPACE") {
                            sh 'pwd'
                            stash includes: '**/bronze_tags_file, bronze_tags_file_to_gold, preManSync.py, promote_gold_tag.py', name: 'tags_file'
		                }
		                cucumber fileIncludePattern: '*.*', jsonReportDirectory: '/data/reds-qa/e2ereports', buildStatus: 'Failure', failedStepsNumber: 1
		      } 
			  post {
					    success{
						mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-SPSIGSDevOps@HoneywellProd.onmicrosoft.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! E2E test case execution Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }
				
			}
		stage("Publish Images and Update Manifest"){
			agent {label 'THD-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					// dir('/data/jenkins/workspace/deployments/') { unstash 'tags_file'}
					step([$class: 'WsCleanup'])
					echo "<<-----------------Publish Images and Update Manifest Stage Begins------------------->>"
					script {
								//Checkout source code from given repository on 'master' branch.
								checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
												extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
												userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/thdd/manifests.git"]]])
						sh """
								#cd /data/jenkins/workspace/deployments/
								figlet MANIFEST UPDATE STAGE
								python promote_gold_tag.py
								git add bronze_tags_file_to_gold deployment-manifest.yml
								git commit -m "updating deployment-manifest.yml and bronze_tags_file_to_gold by Build Number: ${currentBuild.number}" || true
								git push origin master
							"""
					// dir('/data/jenkins/workspace/deployments/manifests') {
					// 				sh 'pwd'
					// 				stash includes: '**/pre-manifest.yml, deployment-manifest.yml, bronze_tags_file, bronze_tags_file_to_gold, preManSync.py', name: 'tags_file'
					// 				}
								}
					}
					post {
					    success{
						mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }					
				    
			}
		stage("SYNC Premanifest with GOLD Tags"){
			agent {label 'THD-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					// dir('/data/jenkins/workspace/deployments/') { unstash 'tags_file'}
					echo "<<-----------------Syncing the Pre-Manifest file with deployment-manifest for GOLD TAGS------------------->>"
					script {
							//Checkout source code from given repository on 'master' branch.
							checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
                                        extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
                                        userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/thdd/manifests.git"]]])
					
					sh """
							#cd /data/jenkins/workspace/deployments/
							python preManSync.py
							python versionIncrementer.py
							echo "Checking pre-manifest is changed/updated or not......................by using git status"
							#rm bronze_tags_file bronze_tags_file_to_gold
							git tag -amf "thdE2E-gold-\$(grep -i "file_version" deployment-manifest.yml |cut -d ":" -f2 |cut -c 3-5)-build-${currentBuild.number}"
							git status
							git add --all
							git status
							git commit -m "Updating deployment-manifest.yml with GOLD TAGS by Build Number: ${currentBuild.number}" || true
							git push origin --tags							
                            git commit -m "Syncing Pre-Manifest.yml with GOLD TAGS by Build Number: ${currentBuild.number}" || true
							git push origin --tags
							git push origin master
							figlet CONGRATS you got the GOLD TAG
					"""
							}
					}
				post {
					    success{
						mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-SPSBigLots-Tribe-Team@Honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }
				}
		
		}
	}
