package com.example.thaiinsurancesoftware.thaiinsurance.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thaiinsurancesoftware.thaiinsurance.R;
import com.example.thaiinsurancesoftware.thaiinsurance.model.ImageList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class FirstFragment extends Fragment implements View.OnClickListener {
    ImageButton imgbtn_browse_image;
    ImageButton imgbtn_browse_camera;
    ImageButton imgbtn_upload_image;
    EditText edit_image_name;
    ImageView imageView;
    Uri imgUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String mImageFileLocation = "";

    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;
    public static final int REQUEST_CAMERA = 12345;


    public FirstFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        initInstances(rootView, savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);


        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        imgbtn_browse_image = (ImageButton) rootView.findViewById(R.id.imgbtn_browse_image);
        imgbtn_browse_camera = (ImageButton) rootView.findViewById(R.id.imgbtn_browse_camera);
        imgbtn_upload_image = (ImageButton) rootView.findViewById(R.id.imgbtn_upload_image);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        edit_image_name = (EditText) rootView.findViewById(R.id.edit_image_name);

        imgbtn_browse_image.setOnClickListener(this);
        imgbtn_browse_camera.setOnClickListener(this);
        imgbtn_upload_image.setOnClickListener(this);

        // Init 'View' instance(s) with rootView.findViewById here
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    @Override
    public void onClick(View view) {
        if (imgbtn_browse_image == view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);
        } else if (imgbtn_browse_camera == view) {

            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
            startActivityForResult(intent, REQUEST_CAMERA);


        } else if (imgbtn_upload_image == view) {
            if (imgUri != null) {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setTitle("Upload Image");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax(100);
                dialog.setProgress(100);
                dialog.show();

                StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

                ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();

                        ImageList imageList = new ImageList(edit_image_name.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(imageList);
                        imageView.setImageBitmap(null);
                        edit_image_name.setText("");


                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("xxx", e.toString());

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int bytesTransferred = (int) taskSnapshot.getBytesTransferred();
                                int totalBytes = (int) taskSnapshot.getTotalByteCount();
                                double progress = (100.0 * bytesTransferred / taskSnapshot.getTotalByteCount());

                                dialog.setMessage("Uploaded " + (int) progress + "%");


                            }
                        });


            } else {
                Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            imgUri = data.getData();
            Log.d("REQUEST_CODE", imgUri + "");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {
                // Toast.makeText(getActivity(), "Picture taken successfully", Toast.LENGTH_SHORT).show();
               // imgUri = data.getData();
               // Log.d("REQUEST_CAMERA", imgUri + "");
                try {
                  //  Bundle extras = data.getExtras();
                  //  Bitmap photoCapture = (Bitmap) extras.get("data");
                 //   imageView.setImageBitmap(photoCapture);
                    Bitmap photoCapture = BitmapFactory.decodeFile(mImageFileLocation);
                    imageView.setImageBitmap(photoCapture);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();
        Log.d("mImageFileLocation", mImageFileLocation + "");

        return image;
    }

}
