package ibs.ctdm.testretrofit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.ResponseBody;

import org.parceler.Parcels;

import java.io.IOException;

import ibs.ctdm.testretrofit.R;
import ibs.ctdm.testretrofit.task.network.NetworkConnectionManager;
import ibs.ctdm.testretrofit.task.network.callback.onNetworkCallbackListener;
import ibs.ctdm.testretrofit.task.network.logger.LoggerFactory;
import ibs.ctdm.testretrofit.task.network.model.User;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String KEY_USER = "KEY_USER";

    LinearLayout layoutForm;
    LinearLayout layoutProgress;
    LinearLayout layoutResult;
    EditText edUsername;
    Button btOk;
    TextView tvResult;

    private User user;

    onNetworkCallbackListener  networkCallbackListener = new onNetworkCallbackListener() {
        @Override
        public void onResponse(User user, Retrofit retrofit) {
            //200
            if (user != null){
                setUserData(user);
                showData();
            }

        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {
            //404 (error not null)
            showData();
            try {
                setDataToView("responseBody = " + responseBodyError.string());
                showData();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onBodyErrorIsNull() {
            //404 (error is null)
            setDataToView("responseBody = null");
            showData();

        }

        @Override
        public void onFailure(Throwable t) {
            //fail any course
            setDataToView("Throw = " + t.getMessage());
            showData();

        }
    };

    View.OnClickListener onBtOkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callServer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstant();

        if (savedInstanceState == null){// first running
            showForm();
        }
        else{// running again(such as: configuration change)
            setUserData(user);
            showData();
        }

    }

    private void callServer() {
        showLoading();
        new NetworkConnectionManager().callServer(networkCallbackListener, edUsername.getText().toString());
    }

    private void setUserData(User user) {
        if (user != null){
            this.user = user;
            LoggerFactory.getWftLog(TAG, "user: " + user);
            LoggerFactory.getWftLog(TAG, "user.getName(): " + user.getName());
            LoggerFactory.getWftLog(TAG, "user.getWebsite(): " + user.getWebsite());
            LoggerFactory.getWftLog(TAG, "user.getCompany(): " + user.getCompany());

            String data = "Github Name :" + user.getName() +
                    "\nWebsite :"+user.getWebsite() +
                    "\nCompany Name :" + user.getCompany();

            setDataToView(data);
        }
    }

    private void setDataToView(String data) {
        tvResult.setText(data);
    }

    private void showData() {
        layoutForm.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        layoutProgress.setVisibility(View.GONE);
    }

    private void showForm() {
        layoutForm.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.GONE);
    }

    private void showLoading() {
        layoutForm.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.VISIBLE);
    }

    private void initInstant() {
        layoutForm = (LinearLayout) findViewById(R.id.layoutForm);
        edUsername = (EditText) findViewById(R.id.edUsername);
        btOk = (Button) findViewById(R.id.btOk);

        layoutProgress = (LinearLayout) findViewById(R.id.layoutProgress);

        layoutResult = (LinearLayout) findViewById(R.id.layoutResult);
        tvResult = (TextView) findViewById(R.id.tvResult);

        btOk.setOnClickListener(onBtOkClickListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_USER, Parcels.wrap(user));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = Parcels.unwrap(savedInstanceState.getParcelable(KEY_USER));
    }
}