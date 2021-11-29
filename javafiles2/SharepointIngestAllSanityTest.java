package ai.cognizer.api;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.inject.Inject;

import ai.cognizer.models.SOURCE;
import ai.cognizer.models.SourceItem;
import ai.cognizer.qa.utils.SharePointHelper;
import io.qameta.allure.TmsLink;

@Guice(modules = CognizerApi.class)
@Listeners({ ScreenshotListener.class })
public class SharepointIngestAllSanityTest {

	@Inject
	SharePointHelper sharePointHelper;

	private final static String sharePointUserId = "ee1df653-ba65-44c8-b25a-d2928c0334e2";

	@Inject
	SourceIngestAll sourceIngestAll;

	@BeforeClass(groups = { "Sanity" })
	public void init() {
		sourceIngestAll.setSource(sharePointHelper, SOURCE.SHAREPOINT, sharePointUserId,
				new SourceItem("root", "Root", null, null));
	}

	@Test(priority = 1, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","Sanity" }, description = "C763 - Verify if sharePoint is redirected with valid access token")
	@TmsLink("763")
	public void verifysharePointLogin() throws Exception {
		sourceIngestAll.verifySourceLogin();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin", "Sanity" }, description = "Verify two folder create with atleast one file")
	public void testTwoCreateFolder() throws Exception {
		sourceIngestAll.testTwoCreateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin", "Sanity" }, description = "Verify two folder delete with atleast one file")
	public void testTwoDeleteFolder() throws Exception {
		sourceIngestAll.testTwoDeleteFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin", "Sanity" }, description = "Verify two file rename event in system")
	public void verifyTwoFileRename() throws Exception {
		sourceIngestAll.verifyTwoFileRename();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin", "Sanity" }, description = "Verify two file delete event in system")
	public void verifyTwoFileDelete() throws Exception {
		sourceIngestAll.verifyTwoFileDelete();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin", "Sanity" }, description = "Verify two file upload event in system")
	public void verifyTwoFileUpload() throws Exception {
		sourceIngestAll.verifyTwoFileUpload();
	}

}
