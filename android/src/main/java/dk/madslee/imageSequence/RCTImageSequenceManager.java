package dk.madslee.imageSequence;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;


public class RCTImageSequenceManager extends SimpleViewManager<RCTImageSequenceView> {

    public static final int COMMAND_RESET = 1;
    public static final int COMMAND_PLAY = 2;
    public static final int COMMAND_STOP = 3;

    @Override
    public String getName() {
        return "RCTImageSequence";
    }

    @Override
    protected RCTImageSequenceView createViewInstance(ThemedReactContext reactContext) {
        return new RCTImageSequenceView(reactContext);
    }

    @Override
    public Map<String,Integer> getCommandsMap() {
        return MapBuilder.of(
                "reset",
                COMMAND_RESET,
                "play",
                COMMAND_PLAY,
                "stop",
                COMMAND_STOP);
    }

    @Override
    public void receiveCommand(
            RCTImageSequenceView view,
            int commandType,
            @Nullable ReadableArray args) {
        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case COMMAND_RESET: {
                view.reset();
                return;
            }

            case COMMAND_PLAY: {
                view.play();
                return;
            }

            case COMMAND_STOP: {
                view.stop();
                return;
            }

            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }
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