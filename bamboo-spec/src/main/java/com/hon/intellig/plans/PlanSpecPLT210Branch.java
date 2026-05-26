package com.hon.intellig.plans;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.docker.DockerConfiguration;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.repository.VcsChangeDetection;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.builders.notification.EmailRecipient;
import com.atlassian.bamboo.specs.builders.notification.JobWithoutAgentNotification;
import com.atlassian.bamboo.specs.builders.notification.PlanCompletedNotification;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;
import com.atlassian.bamboo.specs.builders.task.*;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;
import com.atlassian.bamboo.specs.model.task.InjectVariablesScope;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpecPLT210Branch {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {

        String fileName = "compdetails-plt210.json";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(PlanSpecPLT210Branch.class.getClassLoader().getResource(fileName).getFile());

        System.out.println(fileName + " File Found : " + file.exists());

        String content = new String(Files.readAllBytes(file.toPath()));
        System.out.println(content);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> data = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>(){});
        System.out.println(data);
        for(Map<String, Object> item: data) {
            String compName=(String) item.get("compName");
            String planKey=(String) item.get("planKey");
            String bbProjectKey=(String) item.get("bbProjectKey");
            String gitSshUrl=(String) item.get("gitSshUrl");
            String sccConfigProject=(String) item.get("sccConfigProject");

            BambooServer bambooServer = new BambooServer("https://acsbamboo.honeywell.com");
            String manifestBBProjectName="DOE";
            String manifestRepoName="manifests";
            String manifestRepoUrl="ssh://git@bitbucket.honeywell.com:7999/doe/manifests.git";
            Plan plan = new PlanSpecPLT210Branch().createPlan(compName, planKey, bbProjectKey, gitSshUrl, sccConfigProject,manifestBBProjectName,manifestRepoName,manifestRepoUrl );
            bambooServer.publish(plan);
            PlanPermissions planPermission = new PlanSpecPLT210Branch().createPlanPermission(plan.getIdentifier());
            bambooServer.publish(planPermission);
        }


    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permission = new Permissions()
                .userPermissions("admin", PermissionType.ADMIN, PermissionType.CLONE, PermissionType.EDIT)
                .groupPermissions("bamboo-admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.VIEW)
                .anonymousUserPermissionView();
        return new PlanPermissions(planIdentifier.getProjectKey(), planIdentifier.getPlanKey()).permissions(permission);
    }

    Project project() {
        return new Project()
                .name("Momentum-Components:palfrom adoption_2.0 (Bamboo Pecs)")
                .key("RPA");
    }

    Plan createPlan(String compName, String planKey, String bbProjectKey, String gitSshUrl, String sccConfigProject ,
                    String manifestBBProjectName, String manifestRepoName,String manifestRepoUrl) {
        return new Plan(
                project(),
                compName, planKey)
                .description("Plan created from ( Bmaboo Spec )")
                .pluginConfigurations(new ConcurrentBuilds()
                                .useSystemWideDefault(false),
                        new AllOtherPluginsConfiguration()
                                .configuration(new MapBuilder()
                                        .put("custom.buildExpiryConfig.enabled", "false")
                                        .build()))
                .stages(new Stage("Basic Stage")
                                .description("IRON")
                                .jobs(new Job("Basic build Job",
                                        new BambooKey("JOB1"))
                                        .pluginConfigurations(new AllOtherPluginsConfiguration()
                                                .configuration(new MapBuilder()
                                                        .put("custom", new MapBuilder()
                                                                .put("auto", new MapBuilder()
                                                                        .put("regex", "")
                                                                        .put("label", "")
                                                                        .build())
                                                                .put("buildHangingConfig.enabled", "false")
                                                                .put("ncover.path", "")
                                                                .put("clover", new MapBuilder()
                                                                        .put("path", "")
                                                                        .put("license", "")
                                                                        .put("useLocalLicenseKey", "true")
                                                                        .build())
                                                                .put("sysbliss", new MapBuilder()
                                                                        .put("pre", new MapBuilder()
                                                                                .put("env", "")
                                                                                .put("command.run.location", "A")
                                                                                .build())
                                                                        .put("post.command.run.location", "A")
                                                                        .put("success", new MapBuilder()
                                                                                .put("env", "")
                                                                                .put("command", "")
                                                                                .build())
                                                                        .put("failed", new MapBuilder()
                                                                                .put("env", "")
                                                                                .put("command", "")
                                                                                .build())
                                                                        .build())
                                                                .build())
                                                        .build()))
                                        .tasks(new CleanWorkingDirectoryTask(),
                                                new VcsCheckoutTask()
                                                        .description("checkout")
                                                        .checkoutItems(new CheckoutItem().defaultRepository()),
                                                new ScriptTask()
                                                        .description("get commit-id from revision")
                                                        .inlineBody("export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\nrevision=${bamboo.planRepository.revision}\necho ${revision:0:7}\necho \"commitid=${revision:0:7}\" >> build.properties\nchmod 777 * \nsnapshot=`./gradlew properties -q | grep \"^version:\" | awk '{print $2}' | awk -F\"-SNAPSHOT\" '{print $1}'`\nversion=$snapshot\necho \"version=$snapshot\" >>build.properties\ncompName=$(./gradlew properties -q | grep \"^name:\" | awk '{print $2}')\necho \"compName=$compName\" >> build.properties\nfullVersion=$(./gradlew properties -q | grep \"^version:\" | awk '{print $2}')\necho \"fullVersion=$fullVersion\" >> build.properties\n\nrelease=\"plt210\"\necho \"release=plt210\" >> build.properties\necho \"bronzeVersionTag=bronze-$release-$version-${revision:0:7}\" >> build.properties\necho \"ironCommitTag=iron-$release-$version-${revision:0:7}\" >> build.properties\n\necho \"versionCommitImg=igs-wms-docker-stable-local.artifactory-na.honeywell.com/$compName:$fullVersion\" >> build.properties\n\necho \"cat build.properties\"\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"),
                                                new InjectVariablesTask()
                                                        .description("setup inject variable")
                                                        .path("build.properties")
                                                        .namespace("inject")
                                                        .scope(InjectVariablesScope.RESULT),
                                                new ScriptTask()
                                                        .description("clean build")
                                                        .inlineBody("export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\necho -e \"\\e[1;34m ******** gradle clean build test ******** \\e[0m\"\nchmod 755 * && ./gradlew clean build -x spotlessCheck --refresh-dependencies\necho -e \"\\e[1;34m ******** Rebuilding to get the composite cache jars built, before publishing *****\\e[0m\"\n./gradlew build -x test -x componentTest -x integrationTest -x spotlessCheck && ./gradlew artifactoryPublish -x spotlessCheck\n\necho -e \"\\e[1;34m ######################               Publishing Iron Images                             ################### \\e[0m\"\n\nrevision=${bamboo.planRepository.revision}\ncompName=${bamboo.inject.compName}\nfullVersion=${bamboo.inject.fullVersion}\nversion=${bamboo.inject.version}\n\necho -e \"\\e[1;34m ######################                        Variables                                 ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<compName:Version:revision:-->$compName:$version:$revision>>  ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<ironCommitTag:${bamboo.inject.ironCommitTag}>>               ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<fullVersion:$fullVersion>>                                   ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<versionCommitImg:${bamboo.inject.versionCommitImg}>>         ################### \\e[0m\"\necho -e \"\\e[1;34m ## Build Docker Image ## \\e[0m\"\n./gradlew buildDockerImage -x spotlessCheck  -x test\necho -e \"\\e[1;34m ## Publish docker image with ${ironCommitTag} tag ## \\e[0m\"\ndocker images | grep \"${bamboo.inject.compName}\" |head -2 || true\n./gradlew -x spotlessCheck -x test -x buildDockerImage pushDockerImage -PcustomTag=${bamboo.inject.ironCommitTag} -PinputImg=${bamboo.inject.versionCommitImg}\necho -e \"\\e[1;34m ## Deleting Local Docker Image ## \\e[0m\"\ndocker rmi -f $(docker images -f \"reference=*/${bamboo.inject.compName}\" -q --no-trunc)|| true\n\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"),
                                                new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                                                        .description("JUnit report")
                                                        .resultDirectories("**/test-results/test/*.xml"),
                                                new VcsTagTask()
                                                        .defaultRepository()
                                                        .description("tagRepo")
                                                        .tagName("${bamboo.inject.ironCommitTag}-${bamboo.buildNumber}"))
                                        .finalTasks(new ScriptTask()
                                                        .description("gradle stop")
                                                        .inlineBody("sleep  15 \n./gradlew --stop || true"),
                                                new CleanWorkingDirectoryTask())
                                        .requirements(new Requirement("wes")
                                                .matchValue("build")
                                                .matchType(Requirement.MatchType.EQUALS))
                                        .cleanWorkingDirectory(true)),
                        new Stage("Component Test")
                                .jobs(new Job("component test",
                                        new BambooKey("CT"))
                                        .tasks(new CleanWorkingDirectoryTask(),
                                                new VcsCheckoutTask()
                                                        .description("checkout")
                                                        .checkoutItems(new CheckoutItem().defaultRepository()),
                                                new ScriptTask()
                                                        .description("git checkout to same commit-id")
                                                        .inlineBody("git checkout $bamboo_inject_revision"),
                                                new ScriptTask()
                                                        .description("getSCC")
                                                        .inlineBody("#get SCC\ngit clone ssh://git@bitbucket.honeywell.com:7999/RC/configuration.git ~/configuration"),
                                                new ScriptTask()
                                                        .description("component test")
                                                        .inlineBody("#start component test\nexport JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n\ndocker stop $(docker ps -aq) && docker rm -f $(docker ps -aq) || true\nexport SCCREPOURL=~/configuration && chmod 755 * && ./start-deps-pets.sh && sleep 60s \\\n&& ./gradlew build -x test -x componentTest -x integrationTest -x spotlessCheck\n\necho -e \"\\e[1;34m ############################################################################################# \\e[0m\"\necho -e \"\\e[1;34m ###########################        Executing Component Test       ########################## \\e[0m\"\necho -e \"\\e[1;34m ############################################################################################# \\e[0m\"\ntimeout ${bamboo.build.timeout.sec} ./gradlew componentTest -x test -x integrationTest -x spotlessCheck\nexit_status=`echo $?`\nif [ $exit_status != 0 ]\nthen\necho -e \"\\e[1;31m ############################################################################################# \\e[0m\"\necho -e \"\\e[1;31m ## TIME OUT: Task is running for more the specified time ${bamboo.build.timeout.sec} ######## \\e[0m\"\necho -e \"\\e[1;31m ############################################################################################# \\e[0m\"\nexit 137\nfi\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"))
                                        .finalTasks(new ScriptTask()
                                                        .description("test report")
                                                        .inlineBody("echo -e '\\033[34mTEST REPORT\\033[0m'\n\nlynx -width=5000 -dump build/reports/tests/componentTest/index.html || true\n\necho -e '\\033[34m############\\033[0m'"),
                                                new ScriptTask()
                                                        .description("cleanUp")
                                                        .inlineBody("sleep  15 \n./gradlew --stop || true\necho -e \"\\e[1;34m ********docker prune******** \\e[0m\"\ndocker stop $(docker ps -aq) && docker rm $(docker ps -aq) && docker system prune -a -f || true\nrm -rf ~/configuration"),
                                                new CleanWorkingDirectoryTask())
                                        .requirements(new Requirement("wes")
                                                .matchValue("build")
                                                .matchType(Requirement.MatchType.EQUALS))),
                        new Stage("Integration Test")
                                .jobs(new Job("Intg Test",
                                                new BambooKey("IT"))
                                                .enabled(false)
                                                .tasks(new CleanWorkingDirectoryTask(),
                                                        new VcsCheckoutTask()
                                                                .description("checkout")
                                                                .checkoutItems(new CheckoutItem().defaultRepository()),
                                                        new ScriptTask()
                                                                .description("git checkout")
                                                                .inlineBody("git checkout $bamboo_inject_revision"),
                                                        new ScriptTask()
                                                                .description("getSCC")
                                                                .inlineBody("#!/bin/bash\nset -e\n\n#get SCC\ngit clone ssh://git@bitbucket.honeywell.com:7999/RC/configuration.git ~/configuration\n#stop running docker containers\ndocker stop $(docker ps -aq) && docker rm $(docker ps -aq) || true\nsleep 10s"),
                                                        new ScriptTask()
                                                                .description("Intg test")
                                                                .inlineBody("#!/bin/bash\nset -e\nexport JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n\n#start Intg test\nexport SCCREPOURL=~/configuration && chmod 755 * && \\\n./start-deps-pets.sh && \\\nsleep 60s && \\\n./start-deps-components.sh && \\\nsleep 150s \\\n&& ./gradlew integrationTest -x test -x componentTest -x spotlessCheck\n\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"))
                                                .finalTasks(new ScriptTask()
                                                                .description("StopDockerContainers")
                                                                .inlineBody("#!/bin/bash\nset -e\n\n#stop running docker containers\ndocker stop $(docker ps -aq) && docker rm $(docker ps -aq) || true\nrm -rf ~/configuration || true"),
                                                        new CleanWorkingDirectoryTask())
                                                .requirements(new Requirement("wes")
                                                        .matchValue("build")
                                                        .matchType(Requirement.MatchType.EQUALS)),
                                        new Job("Contract Test",
                                                new BambooKey("CTS"))
                                                .description("Contract Test")
                                                .tasks(new CleanWorkingDirectoryTask(),
                                                        new VcsCheckoutTask()
                                                                .description("checkout")
                                                                .checkoutItems(new CheckoutItem().defaultRepository()),
                                                        new ScriptTask()
                                                                .description("git checkout")
                                                                .inlineBody("git checkout $bamboo_inject_revision"),
                                                        new ScriptTask()
                                                                .description("getSCC")
                                                                .inlineBody("#!/bin/bash\nset -e\n\n#get SCC\ngit clone ssh://git@bitbucket.honeywell.com:7999/RC/configuration.git ~/configuration\n#stop running docker containers\ndocker stop $(docker ps -aq) && docker rm $(docker ps -aq) || true\nsleep 10s"),
                                                        new ScriptTask()
                                                                .description("provider Contract Test")
                                                                .inlineBody("#!/bin/bash\nset -e\nexport JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n\n#start Provider Contract tests\nexport SCCREPOURL=~/configuration && \\\nchmod 755 * && ./start-deps-pets.sh && \\\nsleep 60s && \\\n./start-deps-pets.sh && \\\nsleep 120 && \\\n./gradlew providerContractTest -x spotlessCheck\n\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"),
                                                        new ScriptTask()
                                                                .description("consumer tests")
                                                                .enabled(false)
                                                                .inlineBody("#!/bin/bash\nset -e\nexport JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n\n#start Consumer Contract tests\nexport SCCREPOURL=~/configuration && \\\nchmod 755 * && ./start-deps-pets.sh && \\\nsleep 60s && \\\n./start-deps-pets.sh && \\\nsleep 120 && \\\n./gradlew consumerContractTest -x spotlessCheck\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"),
                                                        new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                                                                .description("provider-test-case-result")
                                                                .resultDirectories("**/test-results/providerContractTest/*.xml"))
                                                .finalTasks(new ScriptTask()
                                                                .description("StopDockerContainers")
                                                                .inlineBody("#!/bin/bash\nset -e\n\n#stop running docker containers\ndocker stop $(docker ps -aq) && docker rm $(docker ps -aq) || true\nrm -rf ~/configuration || true"),
                                                        new CleanWorkingDirectoryTask())
                                                .requirements(new Requirement("wes")
                                                        .matchValue("build")
                                                        .matchType(Requirement.MatchType.EQUALS))),
                        new Stage("Extended Stage")
                                .jobs(new Job("Bronze-Post Extended",
                                        new BambooKey("BE"))
                                        .tasks(new CleanWorkingDirectoryTask(),
                                                new VcsCheckoutTask()
                                                        .description("checkout")
                                                        .checkoutItems(new CheckoutItem().defaultRepository()),
                                                new ScriptTask()
                                                        .description("git checkout to same commit-id")
                                                        .inlineBody("git checkout $bamboo_inject_revision"),
                                                new ScriptTask()
                                                        .description("bronze-post success")
                                                        .inlineBody("export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n\nrevision=${bamboo.planRepository.revision}\ncompName=${bamboo.inject.compName}\nfullVersion=${bamboo.inject.fullVersion}\nversion=${bamboo.inject.version}\n\necho -e \"\\e[1;34m ######################   Variables ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<revision:$revision>>  ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<bronzeVersionTag${bamboo.inject.bronzeVersionTag}>>   ################### \\e[0m\"\necho -e \"\\e[1;34m ######################     <<compName:$compName>>     ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<fullVersion:$fullVersion>>     ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<Version:$version>>     ################### \\e[0m\"\necho -e \"\\e[1;34m ######################   <<ironCommitTag:${bamboo.inject.ironCommitTag}>>   ################### \\e[0m\"\necho -e \"\\e[1;34m ## Pull Docker Image from last Stage ## \\e[0m\"\ndocker pull \"igs-wms-docker-stable-local.artifactory-na.honeywell.com/$compName:${bamboo.inject.ironCommitTag}\"\ndocker images | grep \"${bamboo.inject.compName}\" |head -2 || true\necho -e \"\\e[1;34m ## Tag and publish docker versionCommitImg image as bronzeVersionCommitId ## \\e[0m\"\nchmod 777 * && ./gradlew pushDockerImage -PcustomTag=${bamboo.inject.bronzeVersionTag} -PinputImg=\"igs-wms-docker-stable-local.artifactory-na.honeywell.com/$compName:${bamboo.inject.ironCommitTag}\" -x spotlessCheck -x test -x buildDockerImage\n\nexport JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH"),
                                                new VcsTagTask()
                                                        .defaultRepository()
                                                        .description("tagRepo")
                                                        .tagName("${bamboo.inject.bronzeVersionTag}-${bamboo.buildNumber}"))
                                        .finalTasks(new CleanWorkingDirectoryTask())
                                        .requirements(new Requirement("wes")
                                                .matchValue("build")
                                                .matchType(Requirement.MatchType.EQUALS))))
                .planRepositories(new BitbucketServerRepository()
                        .name(compName)
                        .repositoryViewer(new BitbucketServerRepositoryViewer())
                        .server(new ApplicationLink()
                                .name("Honeywell Bitbucket")
                                .id("eb31011a-a875-39ec-98a0-1e914cf0d5d7"))
                        .projectKey(bbProjectKey)
                        .repositorySlug(compName)
                        .sshCloneUrl(gitSshUrl)
                        .branch("platform_adoption_2.0.1")
                        .changeDetection(new VcsChangeDetection())
                        )

                .triggers(new BitbucketServerTrigger()
                        .description("BC"))
                .variables(new Variable("build.timeout.sec",
                                "8000"),
                        new Variable("scc.config.projectkey",
                                sccConfigProject))
                .planBranchManagement(new PlanBranchManagement()
                        .delete(new BranchCleanup())
                        .notificationForCommitters())
                .notifications(new Notification()
                                .type(new JobWithoutAgentNotification())
                                .recipients(new EmailRecipient("DL-SPSIGSDevOps@HoneywellProd.onmicrosoft.com")),
                        new Notification()
                                .type(new PlanCompletedNotification())
                                .recipients(new EmailRecipient("DL-SPSIGSDevOps@HoneywellProd.onmicrosoft.com")))
                .forceStopHungBuilds();
    }


}
