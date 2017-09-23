package com.example.tnovacic.novathon;

import com.microblink.image.ImageListener;
import com.microblink.view.CameraSurface;

/**
 * Created by dodo on 29/02/16.
 */
public interface IntentConstants {
    /**
     * Key for setting the license key
     */
    public static final String EXTRAS_LICENSE_KEY = "EXTRAS_LICENSE_KEY";

    /**
     * Key for setting the license owner
     */
    public static final String EXTRAS_LICENSEE = "EXTRAS_LICENSEE";

    /**
     * Key for setting the settings object used to initialize recognizer
     */
    public static final String EXTRAS_RECOGNITION_SETTINGS = "EXTRAS_RECOGNITION_SETTINGS";

    /**
     * Define an {@link ImageListener} that will obtain images that are being processed.
     * Make sure that your ImageListener implementation correctly implements Parcelable
     * interface with static CREATOR field. Without this, you might encounter a runtime error.
     */
    public static final String EXTRAS_IMAGE_LISTENER = "EXTRAS_IMAGE_LISTENER";

    /**
     * Define a {@link com.microblink.metadata.MetadataSettings.ImageMetadataSettings} that will define
     * which images will be sent to {@link ImageListener} set via {@link #EXTRAS_IMAGE_LISTENER} extra.
     * If not set, {@link ImageListener} set via {@link #EXTRAS_IMAGE_LISTENER} will receive all possible
     * images.
     */
    public static final String EXTRAS_IMAGE_METADATA_SETTINGS = "EXTRAS_IMAGE_METADATA_SETTINGS";

    /**
     * Recognition results after recognition
     */
    public static final String EXTRAS_RECOGNITION_RESULTS = "EXTRAS_RECOGNITION_RESULTS";

    public static final String EXTRAS_CONTINUOUS_SCANNING = "EXTRAS_CONTINUOUS_SCANNING";

    /**
     * Request usage of maximum possible camera resolution for performing a scan.
     * Note that using maximum possible resolution might result in poor performance.
     * <p/>
     * Request video resolution preset to be used when choosing camera resolution for performing
     * a scan.
     * Note that using maximum possible resolution might result in poor performance and
     * using lowest possible resolution (480p) will result in low scan quality.
     */
    public static final String EXTRAS_CAMERA_VIDEO_PRESET = "EXTRAS_CAMERA_VIDEO_PRESET";

    /**
     * Force using legacy Camera API even on Lollipop devices that support new Camera2 API.
     * Use this only if you have problems with camera management on Lollipop devices.
     */
    public static final String EXTRAS_USE_LEGACY_CAMERA_API = "EXTRAS_USE_LEGACY_CAMERA_API";

    /**
     * Flag which indicates mb should optimize camera parameters for near object scanning.
     * When camera parameters are optimized for near object scanning, macro focus mode will be
     * preferred over autofocus mode. Thus, camera will have easier time focusing on to near objects,
     * but might have harder time focusing on far objects. If you expect that most of your scans
     * will be performed by holding the device very near the object, turn on that parameter. By default,
     * this parameter is set to false, except in cases when from other settings it can be safely concluded
     * that it is better to turn it on.
     */
    public static final String EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING = "EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING";

    /**
     * Whether focus area rectangle will be shown
     */
    public static final String EXTRAS_SHOW_FOCUS_RECTANGLE = "EXTRAS_SHOW_FOCUS_RECTANGLE";

    /**
     * Whether pinch to zoom will be allowed (default is false)
     */
    public static final String EXTRAS_ALLOW_PINCH_TO_ZOOM = "EXTRAS_ALLOW_PINCH_TO_ZOOM";

    /**
     * Type of camera to be used (back facing, front facing, default)
     */
    public static final String EXTRAS_CAMERA_TYPE = "EXTRAS_CAMERA_TYPE";

    /**
     * Aspect mode of camera to be used (fill screen by cropping frame or fit by letterboxing
     */
    public static final String EXTRAS_CAMERA_ASPECT_MODE = "EXTRAS_CAMERA_ASPECT_MODE";

    /**
     * Request which view will be used for displaying camera preview.
     * See {@link com.microblink.view.BaseCameraView#setRequestedSurfaceViewForCameraDisplay(CameraSurface)} for
     * more information.
     */
    public static final String EXTRAS_SET_REQUESTED_CAMERA_SURFACE = "EXTRAS_SET_REQUESTED_CAMERA_SURFACE";
}
