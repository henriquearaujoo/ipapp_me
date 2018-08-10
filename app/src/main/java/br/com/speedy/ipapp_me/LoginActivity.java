package br.com.speedy.ipapp_me;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.speedy.ipapp_me.dialog.DialogDadosConfiguracaoSenha;
import br.com.speedy.ipapp_me.enumerated.Posto;
import br.com.speedy.ipapp_me.model.UltimoLogin;
import br.com.speedy.ipapp_me.model.Usuario;
import br.com.speedy.ipapp_me.util.DialogUtil;
import br.com.speedy.ipapp_me.util.HttpConnection;
import br.com.speedy.ipapp_me.util.SessionApp;
import br.com.speedy.ipapp_me.util.SharedPreferencesUtil;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "usuario:senha", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Usuario usuario;
    private Boolean usuarioValido = false;
    private Boolean ultimoLoginValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Utilizar em desenvolvimento
        /*SharedPreferencesUtil.writePreferences(LoginActivity.this, "ip_servidor", "192.168.110.108");

        SharedPreferencesUtil.writePreferences(LoginActivity.this, "endereco_ws", "/ws_ipapp/webapi/myresource/");

        SharedPreferencesUtil.writePreferences(LoginActivity.this, "porta_servidor", "8081");*/

        //Descomentar pára producao
        SharedPreferencesUtil.writePreferences(LoginActivity.this, "ip_servidor", "192.168.110.2");

        SharedPreferencesUtil.writePreferences(LoginActivity.this, "endereco_ws", "/ws_ipapp/webapi/myresource/");

        SharedPreferencesUtil.writePreferences(LoginActivity.this, "porta_servidor", "7777");

        SharedPreferencesUtil.writePreferences(LoginActivity.this, "posto", Posto.EXPEDICAO_1.toString());

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String usuario = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(usuario)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(usuario)) {
            mEmailView.setError(getString(R.string.error_invalid_user));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(usuario, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLogin;
        private final String mPassword;

        UserLoginTask(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            usuario = new Usuario();
            usuario.setLogin(mLogin);
            usuario.setSenha(mPassword);

            String json = generateJSON(usuario);

            String resposta = callServer("post-json", json);

            return degenerateJSON(resposta);

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                limparSessionApp();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                if(!usuarioValido) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }else if(usuarioValido && !ultimoLoginValido){
                    DialogFragment dialogFragment = new DialogDadosConfiguracaoSenha(LoginActivity.this);
                    dialogFragment.show(getSupportFragmentManager(), "configuracaoSenha");
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_imei) {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(LoginActivity.this.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();

            DialogUtil.showDialogInformacao(LoginActivity.this, imei);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String generateJSON(Usuario usuario){
        JSONObject jo = new JSONObject();

        try {
            jo.put("login", usuario.getLogin());
            jo.put("senha", usuario.getSenha());

            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(LoginActivity.this.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();

            jo.put("imei", imei);

        }catch (Exception e){
            e.printStackTrace();
        }

        return jo.toString();
    }

    public Boolean degenerateJSON(String data){

        try {
            JSONObject jo = new JSONObject(data);
            usuarioValido = jo.getBoolean("valido");

            if (usuarioValido) {
                usuario.setId(jo.getLong("id"));
                usuario.setNome(jo.getString("nome"));

                JSONObject jUL = jo.getJSONObject("ultimologin");
                ultimoLoginValido = jUL.getBoolean("valido");

                SessionApp.setUsuario(usuario);

                if (ultimoLoginValido){
                    UltimoLogin ul = new UltimoLogin();
                    ul.setData(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse(jUL.getString("data")));
                    SessionApp.setUltimoLogin(ul);

                    return true;
                }else{
                    return false;
                }
            }else
                return false;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private String callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(LoginActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(LoginActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(LoginActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "logar", method, data);

        return resposta;

    }

    public void limparSessionApp(){
        SessionApp.setPedido(null);
        SessionApp.setProduto(null);
        SessionApp.setItens(null);
        SessionApp.setRomaneio(null);
    }
}



