package dk.madslee.imageSequence;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;

import javax.annotation.Nullable;


public class RCTImageSequenceManager extends SimpleViewManager<RCTImageSequenceView> {

    private RCTImageSequenceView imageSequenceView = null;

    @Override
    public String getName() {
        return "RCTImageSequence";
    }

    @Override
    protected RCTImageSequenceView createViewInstance(ThemedReactContext reactContext) {
        if (imageSequenceView == null) {
            imageSequenceView = new RCTImageSequenceView(reactContext);
        }
        return imageSequenceView;
    }

    @ReactMethod
    public void reset() {
        imageSequenceView.reset();
    }

    /**
     * sets the speed of the animation.
     *
     * @param view
     * @param framesPerSecond
     */
    @ReactProp(name = "framesPerSecond")
    public void setFramesPerSecond(final RCTImageSequenceView view, Integer framesPerSecond) {
        view.setFramesPerSecond(framesPerSecond);
    }

    /**
     * @param view
     * @param images an array of ReadableMap's {uri: "http://...."} return value of the resolveAssetSource(....)
     */
    @ReactProp(name = "images")
    public void setImages(final RCTImageSequenceView view, ReadableArray images) {
        ArrayList<String> uris = new ArrayList<>();
        for (int index = 0; index < images.size(); index++) {
            ReadableMap map = images.getMap(index);
            uris.add(map.getString("uri"));
        }

        view.setImages(uris);
    }

    /**
     * sets the size for scaling bitmaps
     *
     * @param size size object {width: xxx, height: xxx }
     */
    @ReactProp(name = "size")
    public void setSize(final RCTImageSequenceView view, @Nullable ReadableMap size) {
        if (size != null)
            view.setSize(size.getInt("width"), size.getInt("height"));
    }
}
