package com.owncloud.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;
import com.owncloud.android.MainApp;
import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.datamodel.FileDataStorageManager;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.domain.capabilities.model.OCCapability;
import com.owncloud.android.ui.dialog.LoadingDialog;
import timber.log.Timber;

/**
 * Base Activity with common behaviour for activities dealing with ownCloud {@link Account}s .
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * ownCloud {@link Account} where the main {@link OCFile} handled by the activity is located.
     */
    private Account mCurrentAccount;

    /**
     * Capabilites of the server where {@link #mCurrentAccount} lives.
     */
    private OCCapability mCapabilities;

    /**
     * Flag to signal that the activity is finishing to enforce the creation of an ownCloud {@link Account}.
     */
    private boolean mRedirectingToSetupAccount = false;

    /**
     * Flag to signal when the value of mAccount was set.
     */
    protected boolean mAccountWasSet;

    /**
     * Flag to signal when the value of mAccount was restored from a saved state.
     */
    protected boolean mAccountWasRestored;

    /**
     * Access point to the cached database for the current ownCloud {@link Account}.
     */
    private FileDataStorageManager mStorageManager = null;

    private static final String DIALOG_WAIT_TAG = "DIALOG_WAIT";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.v("onNewIntent() start");
        Account current = AccountUtils.getCurrentOwnCloudAccount(this);
        if (current != null && mCurrentAccount != null && !mCurrentAccount.name.equals(current.name)) {
            mCurrentAccount = current;
        }
        Timber.v("onNewIntent() stop");
    }

    /**
     * Since ownCloud {@link Account}s can be managed from the system setting menu, the existence of the {@link
     * Account} associated to the instance must be checked every time it is restarted.
     */
    @Override
    protected void onRestart() {
        Timber.v("onRestart() start");
        super.onRestart();
        boolean validAccount = (mCurrentAccount != null && AccountUtils.exists(mCurrentAccount.name, this));
        if (!validAccount) {
            swapToDefaultAccount();
        }
        Timber.v("onRestart() end");
    }

    /**
     * Sets and validates the ownCloud {@link Account} associated to the Activity.
     * <p/>
     * If not valid, tries to swap it for other valid and existing ownCloud {@link Account}.
     * <p/>
     * POSTCONDITION: updates {@link #mAccountWasSet} and {@link #mAccountWasRestored}.
     *
     * @param account      New {@link Account} to set.
     * @param savedAccount When 'true', account was retrieved from a saved instance state.
     */
    protected void setAccount(Account account, boolean savedAccount) {
        Account oldAccount = mCurrentAccount;
        boolean validAccount =
                (account != null && AccountUtils.setCurrentOwnCloudAccount(getApplicationContext(),
                        account.name));
        if (validAccount) {
            mCurrentAccount = account;
            mAccountWasSet = true;
            mAccountWasRestored = (savedAccount || mCurrentAccount.equals(oldAccount));

        } else {
            swapToDefaultAccount();
        }
    }

    /**
     * Tries to swap the current ownCloud {@link Account} for other valid and existing.
     * <p/>
     * If no valid ownCloud {@link Account} exists, the the user is requested
     * to create a new ownCloud {@link Account}.
     * <p/>
     * POSTCONDITION: updates {@link #mAccountWasSet} and {@link #mAccountWasRestored}.
     */
    protected void swapToDefaultAccount() {
        // default to the most recently used account
        Account newAccount = AccountUtils.getCurrentOwnCloudAccount(getApplicationContext());
        if (newAccount == null) {
            /// no account available: force account creation
            createAccount(true);
            mRedirectingToSetupAccount = true;
            mAccountWasSet = false;
            mAccountWasRestored = false;

        } else {
            mAccountWasSet = true;
            mAccountWasRestored = (newAccount.equals(mCurrentAccount));
            mCurrentAccount = newAccount;
        }
    }

    /**
     * Launches the account creation activity.
     *
     * @param mandatoryCreation When 'true', if an account is not created by the user, the app will be closed.
     *                          To use when no ownCloud account is available.
     */
    protected void createAccount(boolean mandatoryCreation) {
        AccountManager am = AccountManager.get(getApplicationContext());
        am.addAccount(MainApp.Companion.getAccountType(),
                null,
                null,
                null,
                this,
                new AccountCreationCallback(mandatoryCreation),
                new Handler());
    }

    /**
     * Called when the ownCloud {@link Account} associated to the Activity was just updated.
     * <p>
     * Child classes must grant that state depending on the {@link Account} is updated.
     */
    protected void onAccountSet(boolean stateWasRecovered) {
        if (getAccount() != null) {
            mStorageManager = new FileDataStorageManager(this, getAccount(), getContentResolver());
            mCapabilities = mStorageManager.getCapability(mCurrentAccount.name);
        } else {
            Timber.e("onAccountChanged was called with NULL account associated!");
        }
    }

    public void setAccount(Account account) {
        mCurrentAccount = account;
    }

    /**
     * Getter for the capabilities of the server where the current OC account lives.
     *
     * @return Capabilities of the server where the current OC account lives. Null if the account is not
     * set yet.
     */
    public OCCapability getCapabilities() {
        return mCapabilities;
    }

    /**
     * Getter for the ownCloud {@link Account} where the main {@link OCFile} handled by the activity
     * is located.
     *
     * @return OwnCloud {@link Account} where the main {@link OCFile} handled by the activity
     * is located.
     */
    public Account getAccount() {
        return mCurrentAccount;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAccountWasSet) {
            onAccountSet(mAccountWasRestored);
        }
    }

    /**
     * @return 'True' when the Activity is finishing to enforce the setup of a new account.
     */
    protected boolean isRedirectingToSetupAccount() {
        return mRedirectingToSetupAccount;
    }

    public FileDataStorageManager getStorageManager() {
        return mStorageManager;
    }

    /**
     * Method that gets called when a new account has been successfully created.
     *
     * @param future
     */
    protected void onAccountCreationSuccessful(AccountManagerFuture<Bundle> future) {
        // no special handling in base activity
    }

    /**
     * Helper class handling a callback from the {@link AccountManager} after the creation of
     * a new ownCloud {@link Account} finished, successfully or not.
     */
    public class AccountCreationCallback implements AccountManagerCallback<Bundle> {

        boolean mMandatoryCreation;

        /**
         * Constuctor
         *
         * @param mandatoryCreation When 'true', if an account was not created, the app is closed.
         */
        public AccountCreationCallback(boolean mandatoryCreation) {
            mMandatoryCreation = mandatoryCreation;
        }

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            BaseActivity.this.mRedirectingToSetupAccount = false;
            boolean accountWasSet = false;
            if (future != null) {
                try {
                    Bundle result;
                    result = future.getResult();
                    String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    String type = result.getString(AccountManager.KEY_ACCOUNT_TYPE);
                    if (AccountUtils.setCurrentOwnCloudAccount(getApplicationContext(), name)) {
                        setAccount(new Account(name, type), false);
                        accountWasSet = true;
                    }

                    onAccountCreationSuccessful(future);
                } catch (OperationCanceledException e) {
                    Timber.d("Account creation canceled");

                } catch (Exception e) {
                    Timber.e(e, "Account creation finished in exception");
                }

            } else {
                Timber.e("Account creation callback with null bundle");
            }
            if (mMandatoryCreation && !accountWasSet) {
                finish();
            }
        }
    }

    public void hideSoftKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(),
                    0
            );
        }
    }

    /**
     * Show loading dialog
     */
    public void showLoadingDialog(int messageId) {
        // grant that only one waiting dialog is shown
        dismissLoadingDialog();
        // Construct dialog
        Fragment frag = getSupportFragmentManager().findFragmentByTag(DIALOG_WAIT_TAG);
        if (frag == null) {
            Timber.d("show loading dialog");
            LoadingDialog loading = LoadingDialog.newInstance(messageId, false);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            loading.show(ft, DIALOG_WAIT_TAG);
            fm.executePendingTransactions();
        }
    }

    /**
     * Dismiss loading dialog
     */
    public void dismissLoadingDialog() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(DIALOG_WAIT_TAG);
        if (frag == null) {
            return;
        }

        Timber.d("dismiss loading dialog");
        LoadingDialog loading = (LoadingDialog) frag;
        loading.dismiss();
    }

    /**
     * Show a temporary message in a Snackbar bound to the content view
     *
     * @param message Message to show.
     */
    public void showSnackMessage(String message) {
        final View rootView = findViewById(android.R.id.content);

        if (rootView == null) {
            // If root view is not available don't let the app brake. show the notification anyway.
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}
