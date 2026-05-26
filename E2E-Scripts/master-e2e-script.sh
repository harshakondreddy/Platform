    pipeline {
    agent none
    options {
        buildDiscarder(logRotator(numToKeepStr: '100'))
        disableConcurrentBuilds()
        timeout(time: 7, unit: 'HOURS')
        timestamps()
			}
    environment { CI = 'true' }
    
    stages {
        
		stage("Read Pre-Manifest") {
			agent { label 'MASTER-E2E-Server' }
			steps {
					step([$class: 'WsCleanup'])
					echo "<<-----------------Reading Pre-Manifest File Stage BEGINS------------------->>"
					script {
						
						//Checkout source code from given repository on 'master' branch.
                        checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
                                        extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
                                        userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/doe/manifests.git"]]])
                        sh """
							set +x
                            figlet PRE MANIFEST STAGE
							pwd
							rm release_notes_final || true
							cd master
							chmod 775 *  && python difftag.py
							cat diff_tags_file
							sed -i '/momentumtemplate/d' diff_tags_file
							sed -i '/hazelcast-server/d' diff_tags_file
							cat diff_tags_file
							dos2unix log_generater.sh
							sh log_generater.sh
							python jira_t.py
							python jira.py
							cp ../release_notes_final ./
							if [ -s release_notes_final ]; then
                                echo "release_notes_final exists."
                                sh release_H_Pdf.sh
							    echo "Adding Release Notes"
							cd ../
							git add master/release_notes_final master/release_notes_final.html master/release_notes_final.pdf
							git commit -m "updating release_notes_final by Build Number: ${currentBuild.number}" || true
							git push origin master | true
                           else 
                               echo "log does not exist."
                           fi
                           pwd
						    echo "Release Notes Recording Ends... ... Starting Bronze_Tag_File Creation ... ... "
							chmod 775 * && python e2e_process.py -rel master --function create_bronze_tags_file
							cat master/bronze_tags_file
                            echo "Adding exiting Bronze tags to file <<bronze_tags_file>> for E2E deployment. This is like pre-manifest. TODD: check on this logic!!"
                            git add master/bronze_tags_file
							git commit -m "updating bronze_tags_file by Build Number: ${currentBuild.number}" || true
							git push origin master

						"""
					}
				}
				    
				}
		stage("Deploy Components") {
			agent {label 'MASTER-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					//Checkout source code from given repository on 'master' branch.
                        checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
                                        extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
                                        userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/doe/manifests.git"]]])
					echo "<<-----------------Updating SCC server and GIT repo------------------->>"
                        sh """
							set +x
							#figlet Recreating DB
							docker-compose -f /data/redsqa/docker-compose-components.yml stop
							#docker-compose -f /data/redsqa/PostgreSQL.yml stop
							#docker rm -f \$(docker ps -aq --filter "name=postgressDB") | true
							#/data/redsqa/start-components.sh /data/redsqa/PostgreSQL.yml
							#sleep 180s
                            docker rm -f \$(docker ps -aq --filter "name=sccserver") | true
							chmod 775 *
							figlet UPDATING SCC and HAZELCAST
							docker-compose -f /data/redsqa/docker-compose-components.yml stop
                            docker rm -f \$(docker ps -aq --filter "name=sccserver") | true
							chmod 775 *
							/data/redsqa/start-components.sh /data/redsqa/docker-compose-scc.yml
							docker rm -f \$(docker ps -aq --filter "name=hazelcast") | true
							#hctag=\$(grep -zoP '\\n(\\s*)hazelcast-server:(\\n\\1\\s+.*)+dockerTag: \\K.*' pre-manifest.yml)
							hctag=\$(grep 'hazelcast' master/bronze_tags_file|cut -d ' ' -f2)
							echo "Deploying hazelcast with \$hctag"
							
							python e2e_process.py -rel master --function deploy_custom  -cname hazelcast -dtag \$hctag
                            docker ps -a | grep "hazel"
							sleep 30s
														
							"""
					sh """
								set +x
								echo "<<-----------------Deployment Stage BEGINS------------------->>"
								figlet DEPLOYMENT STARTS
								cat master/bronze_tags_file
							    sleep 5s
								python e2e_process.py -rel master --function deploy_bronze
								sleep 20s
								#docker restart \$(docker ps -a -q --filter name=putaway)
								
						"""

		             }
				    post {
					    success{
						echo "Passed!!"
						mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }
			}
					
		stage("Execute E2E Tests") {
			agent {label 'MASTER-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					sh """
							set +x
						    echo "<<-----------------Execute E2E Tests Stage BEGINS------------------->>"
						    figlet E2E STARTS
							docker rm -f \$(docker ps -aq --filter "name=e2e") | true
                            docker rmi -f \$(docker images |grep mastere2e |awk '{print \$3}')
							/data/redsqa/start-components.sh /data/redsqa/docker-compose-components.yml e2e
							cowsay "Let's wait for Components to come up"
							sleep 1000s
							echo "<<==============DOCKER CONTAINER STATUS JUST BEFORE EXECUTING E2E TESTS======>>"
							docker ps -a
							fortune | cowsay -f ghostbusters
							container_status=\$(docker ps -aq --format '{{.Names}}' --filter status=exited)

                            if [[ ! "\$container_status" == "" ]]; then
                                echo "Exited Components: "
                                echo "\$container_status"
                                exit 1
                                
                            fi
                            echo "... Calling E2E test execution end-point /endtoend/runtests now ..."
							curl -sS http://10.224.92.202:10301/endtoend/runtests | true
						"""
		                cucumber fileIncludePattern: '*.*', jsonReportDirectory: '/data/redsqa/e2ereports', buildStatus: 'Failure', failedStepsNumber: 1
		      }
			  post {
					    success{
						echo "Passed!!"
						mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }
				
			}

		stage("Publish Images and Update Manifest"){
			agent {label 'MASTER-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					step([$class: 'WsCleanup'])
					echo "<<-----------------Publish Images and Update Manifest Stage Begins------------------->>"
					script {
								//Checkout source code from given repository on 'master' branch.
								checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
												extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
												userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/doe/manifests.git"]]])
						sh """
								set +x
								#cd /data/jenkins/workspace/deployments/
								figlet UPDATE MANIFEST STAGE
								cd master
								python promote_gold_tag.py
								git add bronze_tags_file_to_gold deployment-manifest.yml
								git commit -m "updating deployment-manifest.yml and bronze_tags_file_to_gold by Build Number: ${currentBuild.number}" || true
								git push origin master
							"""

								}
					}
					post {
					    success{
						echo "passed"
						mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											error "Failed, exiting now..."
										}
					}
		        }					
				    
			}
		stage("SYNC Premanifest with GOLD Tags"){
			agent {label 'MASTER-E2E-Server'}
			steps {
					step([$class: 'WsCleanup'])
					echo "<<-----------------Syncing the Pre-Manifest file with deployment-manifest for GOLD TAGS------------------->>"
					script {
							//Checkout source code from given repository on 'master' branch.
							checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false,
                                        extensions: [[$class: 'LocalBranch', localBranch: '**'], [$class: 'PerBuildTag']], submoduleCfg: [],
                                        userRemoteConfigs: [[url: "ssh://git@bitbucket.honeywell.com:7999/doe/manifests.git"]]])
					
					sh """
							set +x
							#cd /data/jenkins/workspace/deployments/
							cd master
							python preManSync.py || true
							echo "Checking pre-manifest is changed/updated or not......................by using git status"
							#rm bronze_tags_file bronze_tags_file_to_gold
                            git status
							# tag format: gold-<rel>-<version>_<#build>, i.e. manifest-master-vxxx.x_xx
							git tag -amf "gold-master-v\$(grep -i "file_version" deployment-manifest.yml |cut -d ":" -f2 |cut -c 3-7)_${currentBuild.number}"
							git status
							git add --all
							git status
							git commit -m "Updating deployment-manifest.yml with GOLD TAGS by Build Number: ${currentBuild.number}" || true
							git push origin --tags							
                            git push origin master
                            figlet CONGRATS you got the GOLD 
					"""
							}
					}
				post {
					    success{
						echo "passed"
						mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"SUCCESS: ${currentBuild.fullDisplayName}", body: " Deploy Components Stage .. Passed!!. BUILD-URL- ${env.BUILD_URL} "
                    }
					    failure {
                            echo "Check console output at $BUILD_URL to view the results."
							echo "Failed!!"
							mail to:"DL-Intelligrated-India-SW@honeywell.com", subject:"FAILURE: ${currentBuild.fullDisplayName}", body: "Oops!! Deploy Components Stage .. Failed!!.  BUILD-URL- ${env.BUILD_URL}"
							script{
											//sh "exit 1"
											error "Failed, exiting now..."
										}
					}
		        }
				}
		
		}
	}
