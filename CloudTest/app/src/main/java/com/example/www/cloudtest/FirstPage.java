package com.example.www.cloudtest;

import android.app.NotificationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;


public class FirstPage extends ActionBarActivity {

    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(),
            "eu-west-1:c75328f5-1b77-469b-b531-9ba19d791b69", // Identity Pool ID
            Regions.EU_WEST_1 // Region
    );

    AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);

    TransferUtility transferUtility = new TransferUtility(s3Client, getApplicationContext());

    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.bell)
                    .setContentTitle("Alert!")
                    .setContentText("This is a sample alert!");





    /*int mNotificationId = 001;
    NotificationManager mNotifyMgr =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        Button b = (Button)findViewById(R.id.button);
        final EditText notifHeader = (EditText)findViewById(R.id.csName);
        final EditText notifBody = (EditText)findViewById(R.id.csRev);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mNotificationId = 001;
                if (notifHeader.getText().toString() != null) {
                    mBuilder.setContentTitle(notifHeader.getText().toString());
                }
                if(notifBody.getText().toString() != null) {
                    mBuilder.setContentText(notifBody.getText().toString());
                }
                NotificationManager mNotifyMgr =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

                TransferObserver observer = transferUtility.upload("","",new File(""));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void clicked() {
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }*/
}
