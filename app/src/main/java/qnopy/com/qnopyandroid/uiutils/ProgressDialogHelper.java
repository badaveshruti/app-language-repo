package qnopy.com.qnopyandroid.uiutils;

/**
 * Helper to manage dialogs boxes
 * @author Manju
 *
 */
public interface ProgressDialogHelper {

	/**
	 * Load the dialog box
	 */
	public void showLoadingProgressDialog();

	/**
	 * Show the progress dialog widget
	 * @param message - text to be displayed in the dialog box
	 */
	public void showProgressDialog(CharSequence message);

	/**
	 * Close the dialog box
	 */
	public void dismissProgressDialog();
	
}
