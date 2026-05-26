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
public class PlanSpecFFLibBLReleaseBranch {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {

        String fileName = "compdetails-fflib-bl.json";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(PlanSpecFFLibBLReleaseBranch.class.getClassLoader().getResource(fileName).getFile());

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
            String manifestBBProjectName="bl";
            String manifestRepoName="manifests";
            String manifestRepoUrl="ssh://git@bitbucket.honeywell.com:7999/bl/manifests.git";
            Plan plan = new PlanSpecFFLibBLReleaseBranch().createPlan(compName, planKey, bbProjectKey, gitSshUrl, sccConfigProject,manifestBBProjectName,manifestRepoName,manifestRepoUrl );
            bambooServer.publish(plan);
            PlanPermissions planPermission = new PlanSpecFFLibBLReleaseBranch().createPlanPermission(plan.getIdentifier());
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
                .name("Bamboo-specs:Bamboo Plans for BigLots Branch builds")
                .key("BLRLEASE");
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
                                                .description("clean build")
                                                .inlineBody("export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el7_8.x86_64\nexport PATH=$JAVA_HOME/bin:$PATH\n\nchmod 755 * && ./gradlew clean build -x spotlessCheck --refresh-dependencies\n\n./gradlew artifactoryPublish"))
                                .finalTasks(new CleanWorkingDirectoryTask())
                                .requirements(new Requirement("HIC021033(10.224.92.159)"))
                                .cleanWorkingDirectory(true)))
                .planRepositories(new BitbucketServerRepository()
                        .name(compName)
                        .repositoryViewer(new BitbucketServerRepositoryViewer())
                        .server(new ApplicationLink()
                                .name("Honeywell Bitbucket")
                                .id("eb31011a-a875-39ec-98a0-1e914cf0d5d7"))
                        .projectKey(bbProjectKey)
                        .repositorySlug(compName)
                        .sshCloneUrl(gitSshUrl)
                        .branch("bl-release")
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
