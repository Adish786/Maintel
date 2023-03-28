package com.about.mantle.visual.global;

import com.about.mantle.commons.MntlVisualCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.visual.VisualBasePage;
import com.about.mantle.visual.MntlVisualTestClass;
import com.about.mantle.visual.VisualTestData;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class VisualGlobalTest extends MntlVenusTest implements MntlVisualCommonTestMethods {

    private List<String> hideComps;

    public void setHideComps(List<String> hideComps){
        this.hideComps = hideComps;
    }

    protected void testHeaderComponent(MntlVisualTestClass mntlVisualTestInstance, String testComponentName) {
        try {
            List<VisualTestData> visualTestDataList = getVisualTestDataList(VisualBasePage.header, testComponentName);
            runVisualTest(mntlVisualTestInstance, visualTestDataList, collector);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mntlVisualTestInstance.tearDown();
        }
    }

    protected void testFooterComponent(MntlVisualTestClass mntlVisualTestInstance, String testComponentName) {
        try {
            List<VisualTestData> visualTestDataList = getVisualTestDataList(VisualBasePage.footer, testComponentName);
            runVisualTest(mntlVisualTestInstance, visualTestDataList, collector);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mntlVisualTestInstance.tearDown();
        }
    }

    private List<VisualTestData> getVisualTestDataList(By elementLocator, String testComponentName){
        if (testComponentName.trim().contains("\\s"))
            throw new IllegalArgumentException("[ERR] Test component name should not have spaces in between!");
        List<VisualTestData> visualTestDataList = new ArrayList<>();
        VisualTestData visualTestData = new VisualTestData();
        visualTestData.setComponentUnderTest(testComponentName.trim());
        visualTestData.setElementLocator(elementLocator);
        if (hideComps !=null && !hideComps.isEmpty())
            visualTestData.setHideComps(hideComps);
        visualTestDataList.add(visualTestData);
        return visualTestDataList;
    }

    private void runVisualTest(MntlVisualTestClass mntlVisualTest, List<VisualTestData> datas, ErrorCollector collector) {
        datas.get(0).setWithFullScroll(false);
        mntlVisualTest.visualTestMethod(datas, collector);
    }
}
