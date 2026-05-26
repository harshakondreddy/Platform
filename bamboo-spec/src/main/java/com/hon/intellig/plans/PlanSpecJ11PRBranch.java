package com.hon.intellig.plans;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.docker.DockerConfiguration;
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
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;
import com.atlassian.bamboo.specs.builders.task.*;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpecJ11PRBranch {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {

        String fileName = "compdetails.json";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(PlanSpecJ11PRBranch.class.getClassLoader().getResource(fileName).getFile());

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
            Plan plan = new PlanSpecJ11PRBranch().createPlan(compName, planKey, bbProjectKey, gitSshUrl, sccConfigProject,manifestBBProjectName,manifestRepoName,manifestRepoUrl );
            bambooServer.publish(plan);
            PlanPermissions planPermission = new PlanSpecJ11PRBranch().createPlanPermission(plan.getIdentifier());
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
                .name("Momentum-Components--PR Branches")
                .key("REDSPR");
    }

    Plan createPlan(String compName, String planKey, String bbProjectKey, String gitSshUrl, String sccConfigProject ,
                    String manifestBBProjectName, String manifestRepoName,String manifestRepoUrl) {
        return new Plan(
                project(),
                "j11pr-"+compName, planKey)
                .description("Plan created from ( Bmaboo Spec )")
                .pluginConfigurations(new ConcurrentBuilds()
                                .useSystemWideDefault(false),
                        new AllOtherPluginsConfiguration()
                                .configuration(new MapBuilder()
                                        .put("custom.buildExpiryConfig.enabled", "false")
                                        .build()))
                .stages(new Stage("Basic Stage")
                        .description("build___unit-test___component-test")
                        .jobs(new Job("Basic Build Tasks",
                                new BambooKey("JOB1"))
                                .description("build, unit test and component tests")
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
                                                .description("clean build")
                                                .inlineBody("export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n echo -e \"\\e[1;34m ********gradle clean build******** \\e[0m\"\nchmod 755 * && ./gradlew clean build -x spotlessCheck --refresh-dependencies"),
                                        new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                                                .description("JUnit report")
                                                .resultDirectories("**/test-results/test/*.xml"),
                                        new ScriptTask()
                                                .description("getSCC")
                                                .inlineBody("#get SCC\nrm -rf ~/configuration\ngit clone ssh://git@bitbucket.honeywell.com:7999/${bamboo.scc.config.projectkey}/configuration.git ~/configuration\nchmod 777 -R ~/configuration"),
                                        new ScriptTask()
                                                .description("runComponentTest")
                                                .inlineBody("#!/bin/bash\nset -e\nexport JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n echo -e \"\\e[1;34m ********compoenent test cases******** \\e[0m\"\nif [ ${bamboo.component.test} == true ]\nthen\n   echo \"EXECUTING COMPNENT TESTS...\"\n   #start component test\n    docker stop $(docker ps -aq) && docker rm $(docker ps -aq) || true\n    export SCCREPOURL=~/configuration && \\\n    ./start-deps-pets.sh && sleep 60s && \\\n    ./gradlew build -x test -x spotlessCheck\n    timeout ${bamboo.build.timeout.sec} ./gradlew componentTest -x test -x integrationTest -x spotlessCheck\nelse\n   echo \"Component Test --${bamboo.component.test}-- skipping  compoenent test cases \"\nfi"))
                                .finalTasks(new ScriptTask()
                                                .description("test report")
                                                .inlineBody("echo -e '\\033[34mTEST REPORT\\033[0m'\n\nlynx -width=5000 -dump build/reports/tests/componentTest/index.html || true\n\necho -e '\\033[34m############\\033[0m'"),
                                        new ScriptTask()
                                                .description("cleanUp")
                                                .inlineBody("export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.8.10-0.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n sleep  15 \n./gradlew --stop || true\necho -e \"\\e[1;34m ********docker prune******** \\e[0m\"\ndocker stop $(docker ps -aq) && docker rm $(docker ps -aq) && docker system prune -a -f || true\nrm -rf ~/configuration"),
                                        new CleanWorkingDirectoryTask())
                                .requirements(new Requirement("wes")
                                        .matchValue("build")
                                        .matchType(Requirement.MatchType.EQUALS))
                                .cleanWorkingDirectory(true)
                                .dockerConfiguration(new DockerConfiguration()
                                        .enabled(false))))
                .planRepositories(new BitbucketServerRepository()
                        .name(compName)
                        .repositoryViewer(new BitbucketServerRepositoryViewer())
                        .server(new ApplicationLink()
                                .name("Honeywell Bitbucket")
                                .id("eb31011a-a875-39ec-98a0-1e914cf0d5d7"))
                        .projectKey(bbProjectKey)
                        .repositorySlug(compName)
                        .sshCloneUrl(gitSshUrl)
                        .changeDetection(new VcsChangeDetection())
                        )
                .variables(new Variable("build.timeout.sec",
                                "6000"),
                        new Variable("component.test",
                                "true"),
                        new Variable("scc.config.projectkey",
                                "rc"))
                .planBranchManagement(new PlanBranchManagement()
                        .createForVcsBranchMatching("(?=(j11pr\\/)).*$")
                        .delete(new BranchCleanup()
                                .whenRemovedFromRepositoryAfterDays(2)
                                .whenInactiveInRepositoryAfterDays(10))
                        .defaultTrigger(new RepositoryPollingTrigger()
                                .withPollingPeriod(Duration.ofSeconds(10)))
                        .notificationForCommitters())
                .forceStopHungBuilds();
    }


}
