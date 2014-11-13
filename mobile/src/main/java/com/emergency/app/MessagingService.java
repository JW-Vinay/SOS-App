package com.emergency.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by ramkrishnan_v on 7/8/2014.
 */
public class MessagingService  extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private final String TAG = ((Object)this).getClass().getSimpleName();

    private final String SMS_SENT = "STATUS_SENT";
    private final String SMS_DELIVERED = "STATUS_DELIVERED";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.d(TAG, "onConnected: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnected: " + connectionResult);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
        IntentFilter filter = new IntentFilter(SMS_SENT);
        filter.addAction(SMS_DELIVERED);
        registerReceiver(mReciever, filter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReciever);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        super.onMessageReceived(messageEvent);

        String path  = messageEvent.getPath();

        if(!TextUtils.isEmpty(path) && path.compareToIgnoreCase("SMS") == 0)
        {
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            sendMessage();
//            sendMsgByIntent();
        }
    }

    private BroadcastReceiver mReciever =  new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    if(action.compareTo(SMS_SENT) == 0)
                    {
                        Toast.makeText(MessagingService.this, "SMS SENT", Toast.LENGTH_SHORT).show();
                        sendCommand(SMS_SENT);
                    }
                    else if(action.compareTo(SMS_DELIVERED) == 0)
                    {
                        Toast.makeText(MessagingService.this, "SMS DELIVERED", Toast.LENGTH_SHORT).show();
                        sendCommand(SMS_DELIVERED);
                    }
                    break;

                default:
                    break;
            }
        }
    };

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
//                    if (sendMessageResult.getStatus().isSuccess())
//                        Toast.makeText(MessagingService.this, "Send Success", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(MessagingService.this, sendMessageResult.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void sendMsgByIntent()
    {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendIntent.putExtra("address"  , new String("9833116738"));
                sendIntent.putExtra("sms_body", "Hi...");
                sendIntent.setData(Uri.parse("smsto:"));
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
    }
    private void sendMessage()
    {
        PendingIntent sendPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = SharedPref.getInstance(this).getSOSMessage();

            if(!TextUtils.isEmpty(SharedPref.getInstance(this).getEmergencyContact_1()))
            {
                smsManager.sendTextMessage(SharedPref.getInstance(this).getEmergencyContact_1(), null, message, sendPendingIntent, deliveryPendingIntent);
            }
            if(!TextUtils.isEmpty(SharedPref.getInstance(this).getEmergencyContact_2()))
                smsManager.sendTextMessage(SharedPref.getInstance(this).getEmergencyContact_2(), null, message, sendPendingIntent, deliveryPendingIntent);
            if(!TextUtils.isEmpty(SharedPref.getInstance(this).getEmergencyContact_3()))
                smsManager.sendTextMessage(SharedPref.getInstance(this).getEmergencyContact_3(), null, message, sendPendingIntent, deliveryPendingIntent);

//            Toast.makeText(getApplicationContext(), "SMS Sent!",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
