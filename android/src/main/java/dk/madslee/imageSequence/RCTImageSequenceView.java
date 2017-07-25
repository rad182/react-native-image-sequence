package dk.madslee.imageSequence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;


public class RCTImageSequenceView extends ImageView {
    private Integer framesPerSecond = 24;
    private ArrayList<AsyncTask> activeTasks;
    private HashMap<Integer, Bitmap> bitmaps;
    private RCTResourceDrawableIdHelper resourceDrawableIdHelper;
    private Integer drawableWidth = 0;
    private Integer drawableHeight = 0;

    public RCTImageSequenceView(Context context) {
        super(context);

        resourceDrawableIdHelper = new RCTResourceDrawableIdHelper();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final Integer index;
        private final String uri;
        private final Context context;
        private final Integer desiredWidth;
        private final Integer desiredHeight;

        public DownloadImageTask(Integer index, String uri, Context context, Integer width, Integer height) {
            this.index = index;
            this.uri = uri;
            this.context = context;
            this.desiredWidth = width;
            this.desiredHeight = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (this.uri.startsWith("http")) {
                return this.loadBitmapByExternalURL(this.uri);
            }

            return this.loadBitmapByLocalResource(this.uri);
        }


        private Bitmap loadBitmapByLocalResource(String uri) {
            String filePrefix = "file://";
            Bitmap bitmap = null;
            if (uri.startsWith(filePrefix)) {
                String filepath = uri.substring(uri.indexOf(filePrefix) + filePrefix.length());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(filepath, options);
            } else {
                bitmap = BitmapFactory.decodeResource(this.context.getResources(), resourceDrawableIdHelper.getResourceDrawableId(this.context, uri));
            }

            if (this.desiredWidth != 0 && this.desiredHeight != 0) {
                return Bitmap.createScaledBitmap(bitmap, this.desiredWidth, this.desiredHeight, true);
            }

            return bitmap;
        }

        private Bitmap loadBitmapByExternalURL(String uri) {
            Bitmap bitmap = null;

            try {
                InputStream in = new URL(uri).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (this.desiredWidth != 0 && this.desiredHeight != 0) {
                return Bitmap.createScaledBitmap(bitmap, this.desiredWidth, this.desiredHeight, true);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (!isCancelled()) {
                onTaskCompleted(this, index, bitmap);
            }
        }
    }

    private void onTaskCompleted(DownloadImageTask downloadImageTask, Integer index, Bitmap bitmap) {
        if (index == 0) {
            // first image should be displayed as soon as possible.
            this.setImageBitmap(bitmap);
        }

        bitmaps.put(index, bitmap);
        activeTasks.remove(downloadImageTask);

        if (activeTasks.isEmpty()) {
            setupAnimationDrawable();
        }
    }

    public void play() {
        final CustomAnimationDrawable animation = (this.getDrawable() instanceof CustomAnimationDrawable ? (CustomAnimationDrawable)this.getDrawable() : null);
        if (animation != null) {
            animation.stop();
            animation.selectDrawable(0);
            animation.start();
        }
    }

    public void stop() {
        final CustomAnimationDrawable animation = (this.getDrawable() instanceof CustomAnimationDrawable ? (CustomAnimationDrawable)this.getDrawable() : null);
        if (animation != null) {
            animation.stop();
            animation.selectDrawable(0);
        }
    }

    public void setImages(ArrayList<String> uris) {
        if (isLoading()) {
            // cancel ongoing tasks (if still loading previous images)
            for (int index = 0; index < activeTasks.size(); index++) {
                activeTasks.get(index).cancel(true);
            }
        }

        activeTasks = new ArrayList<>(uris.size());
        bitmaps = new HashMap<>(uris.size());

        for (int index = 0; index < uris.size(); index++) {
            DownloadImageTask task = new DownloadImageTask(index, uris.get(index), getContext(), this.drawableWidth, this.drawableHeight);
            activeTasks.add(task);

            try {
                task.execute();
            } catch (RejectedExecutionException e){
                Log.e("react-native-image-sequence", "DownloadImageTask failed" + e.getMessage());
                break;
            }
        }
    }

    public void setSize(Integer width, Integer height) {
        this.drawableWidth = width;
        this.drawableHeight = height;
    }

    public void setFramesPerSecond(Integer framesPerSecond) {
        this.framesPerSecond = framesPerSecond;

        // updating frames per second, results in building a new AnimationDrawable (because we cant alter frame duration)
        if (isLoaded()) {
            setupAnimationDrawable();
        }
    }

    private boolean isLoaded() {
        return !isLoading() && bitmaps != null && !bitmaps.isEmpty();
    }

    private boolean isLoading() {
        return activeTasks != null && !activeTasks.isEmpty();
    }

    private void setupAnimationDrawable() {
        final CustomAnimationDrawable animation = new CustomAnimationDrawable() {
            @Override
            void onAnimationFinish() {
                //Do something when finish animation
                ReactContext reactContext = (ReactContext) getContext();
                sendEvent(reactContext, "onEnd", null);
            }
        };

        for (int index = 0; index < bitmaps.size(); index++) {
            BitmapDrawable drawable = new BitmapDrawable(this.getResources(), bitmaps.get(index));
            animation.addFrame(drawable, 1000 / framesPerSecond);
        }
        this.setImageDrawable(animation);
        animation.stop();
        animation.selectDrawable(0);
        animation.setOneShot(true);

        ReactContext reactContext = (ReactContext) getContext();
        sendEvent(reactContext, "onLoad", null);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}