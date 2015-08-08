package com.example.www.cloudtest;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.channels.FileLockInterruptionException;


public class FirstPage extends ActionBarActivity {

    private static final int SELECT_PICTURE = 1;
    private String imagePath;
    private ImageView imageView;
    EditText notifHeader;
    EditText notifBody;
    TextView tv;

    AmazonS3Client s3Client;
    CognitoCachingCredentialsProvider credentialsProvider;

    TransferUtility transferUtility;
    /*CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(),
            "eu-west-1:c75328f5-1b77-469b-b531-9ba19d791b69", // Identity Pool ID
            Regions.EU_WEST_1 // Region
    );

    AmazonS3Client s3Client = new AmazonS3Client();*/

    /*TransferUtility transferUtility = new TransferUtility(s3Client, getApplicationContext());*/

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
        Button imageButton = (Button)findViewById(R.id.imageSelectButton);

        imageView = (ImageView)findViewById(R.id.displayImage);

        notifHeader = (EditText)findViewById(R.id.csName);
        notifBody = (EditText)findViewById(R.id.csRev);

        tv = (TextView) findViewById(R.id.textView);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1:c75328f5-1b77-469b-b531-9ba19d791b69", // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );

        s3Client = new AmazonS3Client(credentialsProvider);

        transferUtility = new TransferUtility(s3Client, getApplicationContext());

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

                /*TransferObserver observer = transferUtility.upload("http://suhasbucket.s3-website-ap-southeast-1.amazonaws.com/","newfile",new File(""));*/

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                //notifHeader.setText(imagePath);
                //notifBody.setText("Done!");
                //getUri().toString())
                //TransferObserver observer = transferUtility.upload("http://suhasbucket.s3-website-ap-southeast-1.amazonaws.com/","newfile",new File();


            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                imagePath = getPath(getApplicationContext(), selectedImageUri);
                //notifHeader.setText(selectedImageUri.toString());
                //tv.append(imagePath);

                System.out.println("Image Path : " + imagePath);
                /*final String docId = DocumentsContract.getDocumentId(selectedImageUri);
                final String[] split = docId.split(":");
                final String type = split[0];*/
                /*if ("primary".equalsIgnoreCase(type)) {
                    String s = Environment.getExternalStorageDirectory() + "/" + split[1];
                    tv.setText(s);
                }*/
                //imageView.setImageURI(selectedImageUri);
                //notifHeader.setText(imagePath);
                //TransferObserver observer = transferUtility.upload("suhasbucket","newfile",new File(selectedImageUri.getPath()));
                imageView.setImageURI(selectedImageUri);
                String fileName = imagePath.split("/")[imagePath.split("/").length-1];
                //notifHeader.append(fileName);
                //File f = new File("/sdcard/Ringtones/hangouts_message.ogg");
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentEncoding("UTF-8");
                TransferManager transferManager = new TransferManager(credentialsProvider);
                //Upload upload = transferManager.upload("suhasbucket", "1.ogg", f, new ObjectMetadata());
                FileInputStream stream;
                BufferedInputStream bis;

                try {
                    stream = new FileInputStream(imagePath);
                    bis = new BufferedInputStream(stream);
                    /*PutObjectRequest putObjectRequest = new PutObjectRequest("suhasbucket", "1.ogg", stream, new ObjectMetadata());
                    PutObjectResult result = s3Client.putObject(putObjectRequest);*/

                    Upload upload = transferManager.upload("suhasbucket", fileName, stream, new ObjectMetadata());
                    //notifBody.append(result.toString());
                    upload.waitForCompletion();
                }
                catch (FileNotFoundException fie) {
                    //notifBody.setText("File not found");
                }
                catch (AmazonServiceException ase) {
                    //notifHeader.setText(ase.getMessage());
                }
                catch (AmazonClientException ace) {
                    //notifBody.setText(ace.getMessage());
                }
                catch (InterruptedException ie) {
                    //notifBody.append(ie.getMessage());
                }


                try {
                    //PutObjectRequest putObjectRequest = new PutObjectRequest("suhasbucket/", "1.ogg", stream, new ObjectMetadata());
                    //s3Client.putObject("suhasbucket","newfIIile",new File(selectedImageUri.getPath()));
                    //s3Client.putObject("suhasbucket",f.getName(), f);
                    //TransferObserver observer = transferUtility.upload("suhasbucket",f.getName(), f);

                    //observer.waitFo
                }
                catch (AmazonServiceException ase) {
                    //notifHeader.setText(ase.getMessage());
                }
                catch (AmazonClientException ace) {
                    //notifBody.setText(ace.getMessage());
                }
                //tv.setText(selectedImageUri.getPath());
                //tv.setText(getRealPathFromURI(selectedImageUri));
                //notifBody.setText(selectedImageUri.getPath());
            }
        }
    }

    public String getPath1(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        //tv.setText(cursor.getString(column_index));
        return cursor.getString(column_index);
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*public Uri getUri(Intent i) {

        return i.getData();

    }*/

    /*public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/

    /*public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }*/

    public String getRealPath(Uri selectedImage) {
        String wholeID = DocumentsContract.getDocumentId(selectedImage);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        //return id;
        String[] column = { MediaStore.Images.Media.DATA };
        return column[0];
        /*
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;*/
        //setImageFromIntent(filePath);
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
