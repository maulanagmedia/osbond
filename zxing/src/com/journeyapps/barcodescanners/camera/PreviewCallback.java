package com.journeyapps.barcodescanners.camera;

import com.journeyapps.barcodescanners.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
