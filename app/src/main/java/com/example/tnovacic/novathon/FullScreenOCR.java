package com.example.tnovacic.novathon;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microblink.geometry.Rectangle;
import com.microblink.hardware.SuccessCallback;
import com.microblink.hardware.camera.CameraType;
import com.microblink.hardware.camera.VideoResolutionPreset;
import com.microblink.hardware.orientation.Orientation;
import com.microblink.hardware.orientation.OrientationChangeListener;
import com.microblink.image.ImageListener;
import com.microblink.metadata.ImageMetadata;
import com.microblink.metadata.Metadata;
import com.microblink.metadata.MetadataListener;
import com.microblink.metadata.MetadataSettings;
import com.microblink.metadata.OcrMetadata;
import com.microblink.metadata.TextMetadata;
import com.microblink.recognition.InvalidLicenceKeyException;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.blinkinput.BlinkInputRecognitionResult;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.util.CameraPermissionManager;
import com.microblink.util.Log;
import com.microblink.view.CameraAspectMode;
import com.microblink.view.CameraEventsListener;
import com.microblink.view.CameraSurface;
import com.microblink.view.OnSizeChangedListener;
import com.microblink.view.OrientationAllowedListener;
import com.microblink.view.ocrResult.IOcrResultView;
import com.microblink.view.ocrResult.OcrResultCharsView;
import com.microblink.view.recognition.RecognizerView;
import com.microblink.view.recognition.ScanResultListener;

public class FullScreenOCR extends Activity implements MetadataListener, CameraEventsListener, ScanResultListener, OnSizeChangedListener, OrientationChangeListener {

    private static final String PREF_LAST_REGION_WIDTH = "pref_last_region_width";
    private static final String PREF_LAST_REGION_HEIGHT = "pref_last_region_height";

    private RecognizerView mRecognizerView;
    private IOcrResultView mOcrResultView;
    private CameraPermissionManager mCameraPermissionManager;
    private Button mTorchButton;
    private boolean mTorchEnabled = false;
    private Button mBackButton;
    private ImageListener mImageListener = null;
    private SimpleRectangleView mRectView = null;
    /**
     * This text field contains debug status messages
     */
    private TextView mDebugStatus = null;
    private ResizableOverlayView mResizableOverlayView = null;

    private Orientation mCurrentOrientation = Orientation.ORIENTATION_PORTRAIT;

    @Override
    public void onOrientationChange(Orientation orientation) {
        mCurrentOrientation = orientation;
    }

    private class RegionSize {
        public float mWidth;
        public float mHeight;
    }

    private RegionSize mLastRegionSize = new RegionSize();

    private enum ActivityState {
        DESTROYED,
        CREATED,
        STARTED,
        RESUMED
    }

    private ActivityState mActivityState = ActivityState.DESTROYED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_ocr);
        mActivityState = ActivityState.CREATED;

        mRecognizerView = (RecognizerView) findViewById(R.id.recognizerView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String licenceKey = null;

        MetadataSettings metadataSettings = new MetadataSettings();
        metadataSettings.setOcrMetadataAllowed(true);

        boolean showDebugStatus = false;

        if (extras != null) {
            licenceKey = extras.getString(IntentConstants.EXTRAS_LICENSE_KEY);

            mRecognizerView.setOptimizeCameraForNearScan(extras.getBoolean(IntentConstants.EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING, false));

            CameraType cameraType = extras.getParcelable(IntentConstants.EXTRAS_CAMERA_TYPE);
            if( cameraType == null ) {
                cameraType = CameraType.CAMERA_DEFAULT;
            }
            mRecognizerView.setCameraType( cameraType );

            CameraAspectMode aspectMode = extras.getParcelable(IntentConstants.EXTRAS_CAMERA_ASPECT_MODE);
            if( aspectMode == null ) {
                aspectMode = CameraAspectMode.ASPECT_FILL;
            }
            mRecognizerView.setAspectMode( aspectMode );

            CameraSurface cameraSurface = extras.getParcelable(IntentConstants.EXTRAS_SET_REQUESTED_CAMERA_SURFACE);
            if( cameraSurface == null ) {
                cameraSurface = CameraSurface.SURFACE_DEFAULT;
            }
            mRecognizerView.setRequestedSurfaceViewForCameraDisplay(cameraSurface);

            VideoResolutionPreset preset = extras.getParcelable(IntentConstants.EXTRAS_CAMERA_VIDEO_PRESET);
            mRecognizerView.setVideoResolutionPreset(preset);

            boolean useLegacyCamera = extras.getBoolean(IntentConstants.EXTRAS_USE_LEGACY_CAMERA_API, false);
            mRecognizerView.setForceUseLegacyCamera(useLegacyCamera);

            mRecognizerView.setPinchToZoomAllowed(extras.getBoolean(IntentConstants.EXTRAS_ALLOW_PINCH_TO_ZOOM, false));

            mImageListener = extras.getParcelable(IntentConstants.EXTRAS_IMAGE_LISTENER);
            if (mImageListener != null) {

                // check if user has set image metadata settings
                MetadataSettings.ImageMetadataSettings ims = extras.getParcelable(IntentConstants.EXTRAS_IMAGE_METADATA_SETTINGS);

                // if not set, then enable all images
                if (ims == null) {
                    // enable all images
                    ims = new MetadataSettings.ImageMetadataSettings();
                    ims.setCurrentVideoFrameEnabled(true);
                    ims.setDewarpedImageEnabled(true);
                    ims.setSuccessfulScanFrameEnabled(true);

                    MetadataSettings.ImageMetadataSettings.DebugImageMetadataSettings dims = new MetadataSettings.ImageMetadataSettings.DebugImageMetadataSettings();
                    dims.setAll(true);

                    ims.setDebugImageMetadataSettings(dims);
                }


                metadataSettings.setImageMetadataSettings(ims);
            }

            RecognitionSettings recognitionSettings = extras.getParcelable(IntentConstants.EXTRAS_RECOGNITION_SETTINGS);

            if (recognitionSettings == null) {
                recognitionSettings = new RecognitionSettings();
            }

            showDebugStatus = recognitionSettings.getRecognitionMode() != RecognitionSettings.RECOGNITION_MODE;

            mRecognizerView.setRecognitionSettings(recognitionSettings);
        }

        if (licenceKey == null) {
            Toast.makeText(this, "Licence key is null!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        try {
            mRecognizerView.setLicenseKey(licenceKey);
        } catch (InvalidLicenceKeyException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid licence key", Toast.LENGTH_SHORT).show();
            finish();
            mRecognizerView = null;
            return;
        }

        mRecognizerView.setAspectMode(CameraAspectMode.ASPECT_FILL);

        if( showDebugStatus ) {
            metadataSettings.setDebugMetadataAllowed( true );
        }

        mRecognizerView.setMetadataListener(this, metadataSettings);
        mRecognizerView.setCameraEventsListener(this);
        mRecognizerView.setScanResultListener(this);
        mRecognizerView.setOnSizeChangedListener(this);
        mRecognizerView.setOrientationChangeListener(this);

        mRecognizerView.setVideoResolutionPreset(VideoResolutionPreset.VIDEO_RESOLUTION_720p);

        // allow OCR in all directions
        mRecognizerView.setOrientationAllowedListener(new OrientationAllowedListener() {
            @Override
            public boolean isOrientationAllowed(Orientation orientation) {
                return true;
            }
        });

        mCameraPermissionManager = new CameraPermissionManager(this);
        View v = mCameraPermissionManager.getAskPermissionOverlay();
        if (v != null) {
            ViewGroup vg = (ViewGroup) findViewById(R.id.full_screen_root);
            vg.addView(v);
        }

        mRecognizerView.create();

        // create OCR result view
        mOcrResultView = new OcrResultCharsView(this, null, mRecognizerView.getHostScreenOrientation());
        mRecognizerView.addChildView(mOcrResultView.getView(), false);

        // load button overlay
        View overlay = getLayoutInflater().inflate(R.layout.default_barcode_camera_overlay, null);
        mBackButton = (Button) overlay.findViewById(R.id.defaultBackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   setResult(RESULT_CANCELED, null);
             //   finish();
                Intent intent = new Intent(FullScreenOCR.this, Activity2.class);
                startActivity(intent);
            }
        });

        mTorchButton = (Button) overlay.findViewById(R.id.defaultTorchButton);

        if( showDebugStatus ) {
            initDebugStatus((FrameLayout) overlay);
        }

        try {
            mResizableOverlayView = (ResizableOverlayView) getLayoutInflater().inflate(R.layout.resizable_camera_overlay, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mResizableOverlayView.setRegionChangeListener(new ResizableOverlayView.RegionChangeListener() {
            @Override
            public void regionStartedChanging() {
                mRecognizerView.pauseScanning();
            }

            @Override
            public void regionStoppedChanging() {
                mRecognizerView.resumeScanning( true );
            }

            @Override
            public void regionChanged(RectF region) {

                region = rotateRegion( currentRegionToPortraitRegion( region ) );

                // setScanningRegion applies region in current
                mRecognizerView.setScanningRegion(new Rectangle(region.left, region.top, region.right - region.left, region.bottom - region.top), false);
                mRecognizerView.setMeteringAreas(new RectF[]{region}, false);
                mLastRegionSize.mWidth = region.right - region.left;
                mLastRegionSize.mHeight = region.bottom - region.top;
            }
        });
        mRecognizerView.addChildView( mResizableOverlayView, false );

        mRecognizerView.addChildView(overlay, true);
    }

    private RectF currentRegionToPortraitRegion( RectF currentRegion ) {
        int activityOrientation = mRecognizerView.getHostScreenOrientation();
        switch ( activityOrientation ) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                return currentRegion;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                return new RectF( 1.f - currentRegion.bottom, currentRegion.left, 1.f - currentRegion.top, currentRegion.right );
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                return new RectF( currentRegion.top, 1.f - currentRegion.right, currentRegion.bottom, 1.f - currentRegion.left );
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                return new RectF( 1.f - currentRegion.right, 1.f - currentRegion.bottom, 1.f - currentRegion.left, 1.f - currentRegion.top );
        }
        throw new IllegalStateException( "Unkown activity orientation " + activityOrientation );
    }

    private RectF rotateRegion( RectF portraitRegion ) {
        // resizable overlay return region in portrait orientation, while setScanning region assumes that set
        // region is set in current orientation.
        // So we first need to express given portrait region in current orientation's context.
        if( mCurrentOrientation == Orientation.ORIENTATION_PORTRAIT ) {
            return portraitRegion;
        } else if (mCurrentOrientation == Orientation.ORIENTATION_LANDSCAPE_RIGHT ) {
            return new RectF( portraitRegion.top, 1.f - portraitRegion.right, portraitRegion.bottom, 1.f - portraitRegion.left );
        } else if (mCurrentOrientation == Orientation.ORIENTATION_LANDSCAPE_LEFT ) {
            return new RectF( 1.f - portraitRegion.bottom, portraitRegion.left, 1.f - portraitRegion.top, portraitRegion.right );
        } else if (mCurrentOrientation == Orientation.ORIENTATION_PORTRAIT_UPSIDE ) {
            return new RectF( 1.f - portraitRegion.right, 1.f - portraitRegion.bottom, 1.f - portraitRegion.left, 1.f - portraitRegion.top );
        }
        throw new IllegalStateException( "Unkown orientation " + mCurrentOrientation );
    }

    @SuppressLint("RtlHardcoded")
    @SuppressWarnings("deprecation")
    protected void initDebugStatus( FrameLayout layout ) {
        mDebugStatus = new TextView(this);
        mDebugStatus.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        mDebugStatus.setBackgroundResource(R.drawable.rounded_border);
        mDebugStatus.setPadding(6, 6, 6, 6);
        mDebugStatus.setTextColor(getResources().getColor(com.microblink.library.R.color.statusForeground));
        mDebugStatus.setTextSize(14);
        mDebugStatus.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mDebugStatus.setLayoutParams( params );

        layout.addView( mDebugStatus );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivityState = ActivityState.STARTED;
        if(mRecognizerView != null) {
            mRecognizerView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentOrientation = Orientation.fromActivityInfoCode( mRecognizerView.getHostScreenOrientation() );

        // load last region
        SharedPreferences sp = getPreferences( Context.MODE_PRIVATE );
        float width = sp.getFloat( PREF_LAST_REGION_WIDTH, -1.f );
        float heigth = sp.getFloat( PREF_LAST_REGION_HEIGHT, -1.f );
        if( width > 0.f && heigth > 0.f ) {
            mResizableOverlayView.setRegionSize( width, heigth );
        }

        mActivityState = ActivityState.RESUMED;
        if(mRecognizerView != null) {
            mRecognizerView.resume();
        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mCameraPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityState = ActivityState.STARTED;
        if(mRecognizerView != null) {
            mRecognizerView.pause();
        }

        SharedPreferences.Editor editor = getPreferences( Context.MODE_PRIVATE ).edit();

        editor.putFloat( PREF_LAST_REGION_WIDTH, mLastRegionSize.mWidth );
        editor.putFloat( PREF_LAST_REGION_HEIGHT, mLastRegionSize.mHeight );

        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivityState = ActivityState.CREATED;
        if(mRecognizerView != null) {
            mRecognizerView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityState = ActivityState.DESTROYED;
        if(mRecognizerView != null) {
            mRecognizerView.destroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mRecognizerView != null) {
            mRecognizerView.changeConfiguration(newConfig);
            mOcrResultView.setHostActivityOrientation(mRecognizerView.getHostScreenOrientation());
        }
    }

    @Override
    public void onMetadataAvailable(Metadata metadata) {
        if (metadata instanceof OcrMetadata) {
            mOcrResultView.setOcrResult(((OcrMetadata) metadata).getOcrResult());
        } else if( metadata instanceof TextMetadata) {
            final String text = ((TextMetadata) metadata).getText();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDebugStatus.setText( text );
                }
            });
        } else if (metadata instanceof ImageMetadata) {
            mImageListener.onImageAvailable( ((ImageMetadata) metadata).getImage() );
        }
    }

    private void enableTorchButtonIfPossible() {
        if (mRecognizerView.isCameraTorchSupported() && mTorchButton != null) {
            mTorchButton.setVisibility(View.VISIBLE);
            mTorchButton.setText(R.string.mbLightOff);
            mTorchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightoff, 0, 0, 0);
            mTorchEnabled = false;
            mTorchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mRecognizerView.setTorchState(!mTorchEnabled, new SuccessCallback() {
                        @Override
                        public void onOperationDone(final boolean success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(FullScreenOCR.this, "Setting torch to {}. Success: {}", !mTorchEnabled, success);
                                    if (success) {
                                        mTorchEnabled = !mTorchEnabled;
                                        if (mTorchEnabled) {
                                            mTorchButton.setText(R.string.mbLightOn);
                                            mTorchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lighton, 0, 0, 0);
                                        } else {
                                            mTorchButton.setText(R.string.mbLightOff);
                                            mTorchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightoff, 0, 0, 0);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onCameraPreviewStarted() {
        if (mActivityState == ActivityState.RESUMED) {
            enableTorchButtonIfPossible();
        }
    }

    @Override
    public void onCameraPreviewStopped() {

    }

    @Override
    public void onError(Throwable exc) {
        Log.e(this, exc, "Error");
        Toast.makeText(this, "Error: " + exc.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onCameraPermissionDenied() {
        mCameraPermissionManager.askForCameraPermission();
    }

    @Override
    public void onAutofocusFailed() {

    }

    @Override
    public void onAutofocusStarted(Rect[] focusAreas) {
        if( mRectView != null ) {
            mRectView.setRectangles( focusAreas );
        }
    }

    @Override
    public void onAutofocusStopped(Rect[] focusAreas) {
        if( mRectView != null ) {
            mRectView.setRectangles( null );
        }
    }

    @Override
    public void onScanningDone(RecognitionResults results) {
        // scanning will be resumed automatically
        // TODO decide when results are good enough and return them to the calling activity
        BaseRecognitionResult[] resArray = results.getRecognitionResults();
        if (resArray.length == 1 && resArray[0] instanceof BlinkInputRecognitionResult) {
            BlinkInputRecognitionResult result = (BlinkInputRecognitionResult) resArray[0];
            String rawString = result.getParsedResult("Raw");
            if(rawString.contains("MINER MIVELA") && rawString.contains("CIPS K PLUS") && rawString.contains("BOM") && (rawString.contains("599") || rawString.contains("5,99")) && (rawString.contains("699") || rawString.contains("6,99")) && (rawString.contains("799") || rawString.contains("7,99"))) {
                Log.w(this, "PROBA Result is: {}", rawString);
                Intent resInent = new Intent();
                resInent.putExtra("EXTRA_RES", rawString);
                setResult(RESULT_OK, resInent);
                finish();
            }
            else if(rawString.contains("K PLUS MLIJE") && rawString.contains("SNACK LED") && rawString.contains("NES CL") && (rawString.contains("329") || rawString.contains("3,29")) && (rawString.contains("600") || rawString.contains("6,00")) && (rawString.contains("2,69") || rawString.contains("2,69"))) {
                Log.w(this, "PROBA Result is: {}", rawString);
                Intent resInent = new Intent();
                resInent.putExtra("EXTRA_RES", rawString);
                setResult(RESULT_OK, resInent);
                finish();
            }

          /*  Log.w(this, "PROBA Result is: {}", rawString);
            boolean resultOK = false;
            if (resultOK) {
                Intent resInent = new Intent();
                resInent.putExtra("EXTRA_RES", rawString);
                setResult(RESULT_OK, resInent);
                finish();
            }
            */
        }
    }


    @Override
    public void onSizeChanged(int width, int height) {
        Log.d(this, "[onSizeChanged] Width:{}, Height:{}", width, height);
        int horizontalMargin = (int) (width * 0.07);
        int verticalMargin = (int) (height * 0.07);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int tmp = horizontalMargin;
            horizontalMargin = verticalMargin;
            verticalMargin = tmp;
        }

        if (mBackButton != null) {
            // set margins for back button
            FrameLayout.LayoutParams backButtonParams = (FrameLayout.LayoutParams) mBackButton.getLayoutParams();
            if (backButtonParams.leftMargin != horizontalMargin || backButtonParams.topMargin != verticalMargin) {
                backButtonParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
                mBackButton.setLayoutParams(backButtonParams);
            }
        }

        if (mTorchButton != null) {
            // set margins for torch button
            FrameLayout.LayoutParams torchButtonParams = (FrameLayout.LayoutParams) mTorchButton.getLayoutParams();
            if (torchButtonParams.leftMargin != horizontalMargin || torchButtonParams.topMargin != verticalMargin) {
                torchButtonParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
                mTorchButton.setLayoutParams(torchButtonParams);
            }
        }
    }
}
