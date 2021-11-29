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
public class SharepointDenyListTest {

	@Inject
	SharePointHelper sharePointHelper;

	private final static String sharePointUserId = "ee1df653-ba65-44c8-b25a-d2928c0334e2";

	@Inject
	SourceDenyList sourceDenyList;

	@BeforeClass
	public void init() {
		sourceDenyList.setSource(sharePointHelper, SOURCE.SHAREPOINT, sharePointUserId,
				new SourceItem("root", "Root", null, null));
	}

	@Test(priority = 1, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "C763 - Verify if sharePoint is redirected with valid access token")
	@TmsLink("763")
	public void verifysharePointLogin() throws Exception {
		sourceDenyList.verifySourceLogin();

	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify folder create with one file")
	public void testCreateFolder() throws Exception {
		sourceDenyList.testCreateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify two folder create with atleast one file")
	public void testTwoCreateFolder() throws Exception {
		sourceDenyList.testTwoCreateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify folder delete with atleast one file")
	public void testDeleteFolder() throws Exception {
		sourceDenyList.testTwoCreateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify two folder delete with atleast one file")
	public void testTwoDeleteFolder() throws Exception {
		sourceDenyList.testTwoDeleteFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify file duplicate")
	public void testFileDuplicate() throws Exception {
		sourceDenyList.testFileDuplicate();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify two file duplicate")
	public void testTwoFileDuplicate() throws Exception {
		sourceDenyList.testTwoFileDuplicate();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify file rename event in system")
	public void verifyFileRename() throws Exception {
		sourceDenyList.verifyFileRename();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify two file rename event in system")
	public void verifyTwoFileRename() throws Exception {
		sourceDenyList.verifyTwoFileRename();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify file delete event in system")
	public void verifyFileDelete() throws Exception {
		sourceDenyList.verifyFileDelete();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify two file delete event in system")
	public void verifyTwoFileDelete() throws Exception {
		sourceDenyList.verifyTwoFileDelete();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify file upload event in system")
	public void verifyFileUpload() throws Exception {
		sourceDenyList.verifyFileUpload();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify two file upload event in system")
	public void verifyTwoFileUpload() throws Exception {
		sourceDenyList.verifyTwoFileUpload();
	}


	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify folder duplicate")
	public void testDuplicateFolder() throws Exception {
		sourceDenyList.testDuplicateFolder();
	}

	@Test(priority = 2, dependsOnMethods = { "verifysharePointLogin" }, groups = { "sharePoint", "sharePointPositive",
	"sharePointLogin" }, description = "Verify two folder duplicate")
	public void testTwoDuplicateFolder() throws Exception {
		sourceDenyList.testTwoDuplicateFolder();
	}


	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify file move event in system")
	public void verifyFileMove() throws Exception {
		sourceDenyList.verifyFileMove();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify two file move event in system")
	public void verifyTwoFileMove() throws Exception {
		sourceDenyList.verifyTwoFileMove();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify selected file move to rootFolder")
	public void verifySelectedFileMoveToRootFolder() throws Exception {
		sourceDenyList.verifySelectedFileMoveToRootFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify unselected file move to selected folder")
	public void verifyUnselectedFileMoveToSelectedFolder() throws Exception {
		sourceDenyList.verifyUnselectedFileMoveToSelectedFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move selected file to unselected folder")
	public void verifySelectedFileMoveToUnselectedFolder() throws Exception {
		sourceDenyList.verifySelectedFileMoveToUnselectedFolder();
	}

	@Test(priority = 2, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify add file to unselected folder")
	public void verifyAddFileToUnselectedFolder() throws Exception {
		sourceDenyList.verifyAddFileToUnselectedFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify folder move event in system")
	public void verifyFolderMove() throws Exception {
		sourceDenyList.verifyFolderMove();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify folder move event in system")
	public void verifyTwoFolderMove() throws Exception {
		sourceDenyList.verifyTwoFolderMove();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move allowed folder to another allowed folder")
	public void verifyMoveAllowedFolderToAllowed() throws Exception {
		sourceDenyList.verifyMoveAllowedFolderToAllowed();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move allowed folder to root folder")
	public void verifyMoveAllowedFolderToRootFolder() throws Exception {
		sourceDenyList.verifyMoveAllowedFolderToRootFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move allowed child folder to root folder")
	public void verifyMoveDeniedChildFolderToRootFolder() throws Exception {
		sourceDenyList.verifyMoveDeniedChildFolderToRootFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move unselected folder to selected folder")
	public void verifyMoveUnselectedFolderToSelectedFolder() throws Exception {
		sourceDenyList.verifyMoveUnselectedFolderToSelectedFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move unselected folder to unselected folder")
	public void verifyMoveUnselectedFolderToUnselectedFolder() throws Exception {
		sourceDenyList.verifyMoveUnselectedFolderToUnselectedFolder();
	}

	@Test(priority = 3, dependsOnMethods = {
	"verifysharePointLogin" }, description = "Verify move two allowed folder to another allowed folder")
	public void verifyTwoMoveAllowedFolderToAllowed() throws Exception {
		sourceDenyList.verifyTwoMoveAllowedFolderToAllowed();
	}

	@Test(priority = 4, groups = { "sharePoint", "sharePointPositive" }, dependsOnMethods = {
	"verifysharePointLogin" }, description = "C783 - Verify if Dropbox account is removed with valid access token")
	@TmsLink("783")
	public void verifyBoxLogout() {
		sourceDenyList.verifySourceLogout();
	}

}
