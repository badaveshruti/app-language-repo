package qnopy.com.qnopyandroid.ui.mediaPicker.capturePhoto;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;

import java.io.File;
import java.io.FileOutputStream;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.util.Util;

import static android.app.Activity.RESULT_OK;
import static android.graphics.BitmapFactory.decodeFile;
import static qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity.dirPath;

/**
 * A simple {@link Fragment} subclass.
 */
public class CapturePhotoFragment extends Fragment implements View.OnClickListener {

    private CameraView camera;
    private ImageView ivCapture;
    private RelativeLayout progressDialogLayout;
    private ImageView ivSwitchCamera;
    private ImageView ivFlashPhoto;
    private static final int FLASH_AUTO = 0;
    private static final int FLASH_OFF = 1;
    private static final int FLASH_ON = 2;

    private static final int[] FLASH_OPTIONS = {
            FLASH_AUTO,
            FLASH_OFF,
            FLASH_ON,
    };

    private static final Flash[] flashOption = {
            Flash.AUTO,
            Flash.OFF,
            Flash.ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };
    private int mCurrentFlash;

    public static CapturePhotoFragment newInstance() {
        return new CapturePhotoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capture_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpCamera(view);
        ivCapture = view.findViewById(R.id.ivBtnCapture);
        ivSwitchCamera = view.findViewById(R.id.ivSwitchCamera);
        ivFlashPhoto = view.findViewById(R.id.ivFlashPhoto);

        progressDialogLayout = view.findViewById(R.id.layoutProgressBar);

        ivSwitchCamera.setOnClickListener(this);
        ivFlashPhoto.setOnClickListener(this);
        ivCapture.setOnClickListener(this);
    }

    private void setUpCamera(View view) {
        camera = view.findViewById(R.id.cameraView);
        camera.setLifecycleOwner(this);
        camera.setMode(Mode.PICTURE);

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                processImageResult(result);
                getActivity().finish();
            }

            private void processImageResult(PictureResult result) {
                String fName = "p_" + System.currentTimeMillis();
                String thumbsPath = dirPath + File.separator + GlobalStrings.THUMBNAILS_DIR + File.separator;

                //original file
                String fileName = fName + ".jpg";
                File dirDest = new File(dirPath, fileName);
                Bitmap bitmap = BitmapFactory.decodeByteArray(result.getData(),
                        0, result.getData().length);
                Util.saveBitmapToSDCard(bitmap, dirDest, GlobalStrings.COMPRESSION_RATE_100);

/*                //1000*1000 file
                String fileNameThousand = fName + GlobalStrings.THOUSAND_EXTENSION;
                File dirThousandDest = new File(thumbsPath, fileNameThousand);
                Bitmap thousandBmp = Util.getResizedBitmap(bitmap, 1000, 1000);
                Util.saveBitmapToSDCard(thousandBmp, dirThousandDest, GlobalStrings.COMPRESSION_RATE_100);*/

                if (!dirDest.exists()) {
                    Toast.makeText(getActivity(), "Unable to save file.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    return;
                }

                Bitmap bitmapCorrection = correctBitmapRotation(dirDest.getAbsolutePath(),
                        result.getRotation());
                Bitmap cropIMg = Util.cropToSquare(bitmapCorrection);
                String path = Util.saveBitmapToSDCard(cropIMg, dirDest, GlobalStrings.COMPRESSION_RATE_100);

                //thumbnail file
                String thumbFileName = fName + GlobalStrings.THUMBNAIL_EXTENSION;
                File dirThumbDest = new File(thumbsPath, thumbFileName);
                Bitmap thumbBmp = Util.getResizedBitmap(cropIMg, 160, 160);
                Util.saveBitmapToSDCard(thumbBmp, dirThumbDest, GlobalStrings.COMPRESSION_RATE_100);

                Intent intent = new Intent();
                intent.putExtra(GlobalStrings.KEY_SELECTED_IMAGE_PATH, path);
                intent.putExtra(GlobalStrings.KEY_SELECTED_IMAGE_THUMB_PATH, dirThumbDest.getAbsolutePath());

                //15/10/22 Note: for now we are putting same path as original file as our camera lib
                //currently not processing image greater than 900px so we cant enlarge the image using resize code
                //so until we receive good quality(of course we need to work on quality thing as
                //this is workaround only) image to process image further we will follow this.
                intent.putExtra(GlobalStrings.KEY_SELECTED_IMAGE_1000_PATH, path);

                try {
                    if (((MediaPickerActivity) getActivity()).isFromFormMaster)
                        intent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, ((MediaPickerActivity) getActivity()).fieldParamId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    private Bitmap correctBitmapRotation(String path, int rotationValue) {
        Bitmap sourceBitmap = BitmapFactory.decodeFile(path, new BitmapFactory.Options());

        Matrix matrix = new Matrix();
        if (rotationValue != 0) {
            matrix.preRotate(rotationValue);
        }

        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight(), matrix, true);
    }

    private void showProgressDialog() {
        progressDialogLayout.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCameraOpen();
    }

    private void setCameraOpen() {
        if (!camera.isOpened()) {
            camera.open();

            /*Parameters cameraParameters = camera.getParameters();
            cameraParameters.setPictureFormat(ImageFormat.JPEG);
            cameraParameters.setJpegQuality(50);
            camera.setParameters(cameraParameters);*/
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        camera.close();
    }

    private static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(height, width);
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = Math.max(cropW, 0);
        int cropH = (height - width) / 2;
        cropH = Math.max(cropH, 0);

        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    private String getRightAngleImage(String absolutePath) {

        try {
            ExifInterface ei = new ExifInterface(absolutePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree, absolutePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return absolutePath;
    }

    private String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            return imagePath;
        }
        try {
            Bitmap b = decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSwitchCamera:
                toggleCamera();
                break;
            case R.id.ivFlashPhoto:
                setCameraFlash();
                break;
            case R.id.ivBtnCapture:
/*                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    showCameraPermissionInfoAlert();
                else {*/
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    showProgressDialog();
                    camera.takePicture();
                }
                break;
        }
    }

    private void showCameraPermissionInfoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Camera Permission");
        builder.setMessage("We require this permission for app to be able to capture photos for attachments");
        builder.setPositiveButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showProgressDialog();
                camera.takePicture();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setCameraFlash() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
        ivFlashPhoto.setImageResource(FLASH_ICONS[mCurrentFlash]);
        camera.setFlash(flashOption[FLASH_OPTIONS[mCurrentFlash]]);
    }

    private void toggleCamera() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        switch (camera.toggleFacing()) {
            case BACK:
                ivSwitchCamera.setImageDrawable(ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_camera_rear));
                break;

            case FRONT:
                ivSwitchCamera.setImageDrawable(ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_camera_front));
                break;
        }
    }
}
