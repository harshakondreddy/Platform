package com.hon.intellig.plans;

import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.exceptions.PropertiesValidationException;
import com.atlassian.bamboo.specs.api.util.EntityPropertiesBuilders;
import org.junit.Test;

public class PlanSpecMasterBranchTest {
    @Test
    public void checkYourPlanOffline() throws PropertiesValidationException {
        String compName="component-order";
        String planKey="AUTOORD";
        String bbProjectKey="RC";
        String gitSshUrl="ssh://git@bitbucket.honeywell.com:7999/rc/component-order.git";
        String sccConfigProject="BL";
        String manifestBBProjectName="DOE";
        String manifestRepoName="manifests";
        String manifestRepoUrl="ssh://git@bitbucket.honeywell.com:7999/doe/manifests.git";
        Plan plan = new PlanSpecMasterBranch().createPlan(compName, planKey, bbProjectKey, gitSshUrl, sccConfigProject);

        EntityPropertiesBuilders.build(plan);
    }
}
