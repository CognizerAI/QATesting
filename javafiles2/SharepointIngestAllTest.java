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
public class SharepointIngestAllTest {

	@Inject
	SharePointHelper sharePointHelper;

	private final static String sharePointUserId = "ee1df653-ba65-44c8-b25a-d2928c0334e2";

	@Inject
	SourceIngestAll sourceIngestAll;

	@BeforeClass(groups = { "SharepointSanity" })
	public void init() {
		sourceIngestAll.setSource(sharePointHelper, SOURCE.SHAREPOINT, sharePointUserId,
				new SourceItem("root", "Root", null, null));
	}

	@Test(priority = 1, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "C763 - Verify if sharePoint is redirected with valid access token")
	@TmsLink("763")
	public void verifysharePointLogin() throws Exception {
		sourceIngestAll.verifySourceLogin();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify folder create with one file")
	public void testCreateFolder() throws Exception {
		sourceIngestAll.testCreateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "Verify two folder create with atleast one file")
	public void testTwoCreateFolder() throws Exception {
		sourceIngestAll.testTwoCreateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify folder delete with atleast one file")
	public void testDeleteFolder() throws Exception {
		sourceIngestAll.testDeleteFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "Verify two folder delete with atleast one file")
	public void testTwoDeleteFolder() throws Exception {
		sourceIngestAll.testTwoDeleteFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify file duplicate")
	public void testFileDuplicate() throws Exception {
		sourceIngestAll.testFileDuplicate();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "Verify two file duplicate")
	public void testTwoFileDuplicate() throws Exception {
		sourceIngestAll.testTwoFileDuplicate();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify file rename event in system")
	public void verifyFileRename() throws Exception {
		sourceIngestAll.verifyFileRename();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "Verify two file rename event in system")
	public void verifyTwoFileRename() throws Exception {
		sourceIngestAll.verifyTwoFileRename();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify file delete event in system")
	public void verifyFileDelete() throws Exception {
		sourceIngestAll.verifyFileDelete();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "Verify two file delete event in system")
	public void verifyTwoFileDelete() throws Exception {
		sourceIngestAll.verifyTwoFileDelete();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify file upload event in system")
	public void verifyFileUpload() throws Exception {
		sourceIngestAll.verifyFileUpload();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify two file upload event in system")
	public void verifyTwoFileUpload() throws Exception {
		sourceIngestAll.verifyTwoFileUpload();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify folder duplicate")
	public void testDuplicateFolder() throws Exception {
		sourceIngestAll.testDuplicateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "Verify two folder duplicate")
	public void testTwoDuplicateFolder() throws Exception {
		sourceIngestAll.testTwoDuplicateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify file move event in system")
	public void verifyFileMove() throws Exception {
		sourceIngestAll.verifyFileMove();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify two file move event in system")
	public void verifyTwoFileMove() throws Exception {
		sourceIngestAll.verifyTwoFileMove();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin" }, description = "Verify folder move event in system")
	public void verifyFolderMove() throws Exception {
		sourceIngestAll.verifyFolderMove();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = {
			"Failed" }, description = "Verify folder move event in system")
	public void verifyTwoFolderMove() throws Exception {
		sourceIngestAll.verifyTwoFolderMove();
	}

	@Test(priority = 3, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
			"sharePointLogin","SharepointSanity" }, description = "C783 - Verify if sharePoint account is removed with valid access token")
	@TmsLink("783")
	public void verifyDropBoxLogout() {
		sourceIngestAll.verifySourceLogout();
	}

}
