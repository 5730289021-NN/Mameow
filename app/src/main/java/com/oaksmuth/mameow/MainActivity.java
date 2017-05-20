package com.oaksmuth.mameow;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.oaksmuth.mameow.task.AskTask;
import com.oaksmuth.mameow.task.Task;
import com.oaksmuth.mameow.task.TeachTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private final int SPEECH_RECOGNITION_CODE = 1;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private EditText editText;
    private Button sendButton;
    private String userInput;
    private Context context;
    private ArrayList<Task> tasks;
    private String said;
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;
    private String mRemoveBANNERPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.textLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.send_button);
        tasks = new ArrayList<>();
        new echoReceiver().execute();

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput = editText.getText().toString();
                if (userInput.isEmpty())
                    return;
                editText.setText("");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = new TextView(context);
                        textView.setText(userInput + "\n");
                        textView.setTextColor(Color.BLACK);
                        linearLayout.addView(textView);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
                Scanner scanner = new Scanner(userInput);
                String command = scanner.next();
                switch (command.toUpperCase()) {
                    case "TEACH": {
                        if (userInput.length() > 5) {
                            Task task = new Task(Task.TEACH, userInput.substring(5, userInput.length()).trim());
                            tasks.add(task);
                            new TeachTask(task).start();
                        }
                        break;
                    }
                    case "สอน": {
                        if (userInput.length() > 3) {
                            Task task = new Task(Task.TEACH, userInput.substring(3, userInput.length()).trim());
                            tasks.add(task);
                            new TeachTask(task).start();
                        }
                        break;
                    }
                    case "REMOVE":
                    case "DELETE":
                    case "DISABLE":{
                        String nextString = scanner.next().toUpperCase();
                        if(nextString.contains("ADS") || nextString.contains("ADVERTISEMENT") || nextString.contains("BANNER"))
                        {
                            Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
                            serviceIntent.setPackage("com.android.vending");
                            bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

                            ArrayList<String> skuList = new ArrayList<String> ();
                            skuList.add("removeBANNER");
                            Bundle querySkus = new Bundle();
                            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                            try {
                                Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                                String sku;
                                if (skuDetails.getInt("RESPONSE_CODE") == 0) {
                                    ArrayList<String> responseList
                                            = skuDetails.getStringArrayList("DETAILS_LIST");
                                    for (String thisResponse : responseList) {
                                        JSONObject object = new JSONObject(thisResponse);
                                        sku = object.getString("productId");
                                        String price = object.getString("price");
                                        if (sku.equals("removeBANNER"))
                                        {
                                            mRemoveBANNERPrice = price;
                                            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                            startIntentSenderForResult(pendingIntent.getIntentSender(),
                                                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                                    Integer.valueOf(0));

                                        }
                                    }

                                }


                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }


                        }
                        break;
                    }
                    case "RESET":
                    case "RESTART": {
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        break;
                    }
                    case "TERMINATE":
                    case "EXIT":
                    case "QUIT": {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                    }
                    default: {
                        Task task = new Task(Task.ASK, userInput.trim());
                        tasks.add(task);
                        new AskTask(task).start();
                        //Toast.makeText(context, "ask task added", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });
        sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Speak something...");
                try {
                    startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Speech recognition is not supported in this device.",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private class echoReceiver extends AsyncTask<Void,String,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null)
            {
                EndProcessDialog dialog = new EndProcessDialog();
                dialog.show(getSupportFragmentManager(),"");
            }
            try {
                Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                if(ownedSkus.get(0).isEmpty())
                {
                    //RM BANNER
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(true)
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("echo receiver","tasks = " + tasks.size());
                for(Task task:tasks)
                {
                    Log.i("echo receiver",task.toString());
                }
                if(!tasks.isEmpty() && !tasks.get(0).returnMessage.isEmpty())
                {
                    Log.i("echo receiver","publish");
                    String message = tasks.get(0).returnMessage;
                    publishProgress(message);
                    tasks.remove(0);
                }
            }
        }

        @Override
        protected void onProgressUpdate(final String... values) {
            super.onProgressUpdate(values);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = new TextView(context);
                    textView.setText(values[0] + "\n");
                    linearLayout.addView(textView);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }


    public static class EndProcessDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Make sure that you have an internet connection before running this application.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            return builder.create();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    said = result.get(0);
                    editText.setText(Helper.simplifyText(said));
                }
                break;
            }
            case 1001://remove banner
                    int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                    String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                    String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                    if (resultCode == RESULT_OK) {
                        try {
                            JSONObject jo = new JSONObject(purchaseData);
                            String sku = jo.getString("productId");
                            Toast.makeText(context, "Thank you for purchasing " + sku,Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException e) {
                            Toast.makeText(context, "Failed to REMOVE BANNER",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

}
