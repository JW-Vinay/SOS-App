package com.emergency.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class EmergencyActivity extends Activity implements View.OnClickListener, MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private final String TAG = ((Object) this).getClass().getSimpleName();
    private TextView mTextView, mStatusTextView;
    private Button mClickMe;

    private GoogleApiClient mGoogleApiClient;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mStatusTextView = (TextView) stub.findViewById(R.id.statusLabel);
                mClickMe = (Button) stub.findViewById(R.id.btn);
                mClickMe.setOnClickListener(EmergencyActivity.this);
                mProgress = (ProgressBar) stub.findViewById(R.id.progress);
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                mProgress.setVisibility(View.VISIBLE);
                mStatusTextView.setVisibility(View.VISIBLE);
                mStatusTextView.setText(getString(R.string.connectin));
                sendCommand("SMS");
//                sendMessage();
//                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                sendIntent.putExtra("address"  , new String("9833116738"));
//                sendIntent.putExtra("sms_body", "Hi...");
////                sendIntent.setData(Uri.parse("smsto:"));
//                sendIntent.setType("vnd.android-dir/mms-sms");
//                startActivity(sendIntent);
                break;

        }
    }

    private void sendCommand(final String command) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (final Node node : getConnectedNodesResult.getNodes()) {
                            PendingResult<MessageApi.SendMessageResult> result =
                                    Wearable.MessageApi.sendMessage(
                                            mGoogleApiClient, node.getId(), command, null);
                            result.setResultCallback(mSendMessageResultCallback);
                        }
                    }
                }
        );
    }

    private ResultCallback<MessageApi.SendMessageResult> mSendMessageResultCallback =
            new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    if (sendMessageResult.getStatus().isSuccess())
                    {
                        mStatusTextView.setText(getString(R.string.sending));
                        Toast.makeText(EmergencyActivity.this, "Send Success", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(EmergencyActivity.this, sendMessageResult.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed: " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        final String path  = messageEvent.getPath();

        if(!TextUtils.isEmpty(path))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    mStatusTextView.setText(path);
                    if(path.contains("DELIVERED"))
                        mStatusTextView.setVisibility(View.GONE);
                        mProgress.setVisibility(View.GONE);
                }
            });
        }
    }
}
