package ca.tinyweb.comp8531_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.rs.hellocompute.ScriptC_mono;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;
    private final int ITERATIONS = 100;
    private final int NUM_THREADS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        long startTimeInMills = System.currentTimeMillis();

        for (int i=0; i<=ITERATIONS; i++) {
            executorService.submit(() -> new RenderScriptTask(this).run());
        }

        while (true) {

            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
            if (threadPoolExecutor.getCompletedTaskCount() == 100) {
                break;
            }
        }

        long endTimeInMills = System.currentTimeMillis();
        long executionTime = endTimeInMills - startTimeInMills;

        Toast.makeText(this, "Execution took " + executionTime + " ms for " + ITERATIONS + " iterations", Toast.LENGTH_LONG).show();

    }

    /**
     * Renderscript execution
     */
    private void createScript() {
        RenderScript mRS = RenderScript.create(this);
        Allocation mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());
        ScriptC_mono mScript = new ScriptC_mono(mRS);
        mScript.forEach_root(mInAllocation, mOutAllocation);
        mOutAllocation.copyTo(mBitmapOut);
    }

    /**
     * Helper method to fetch a bitmap from Drawable
     * @param resource The resource ID to fetch
     * @return The Bitmap that was loaded
     */
    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }

    /**
     * Helper method to randomize the selection of a bitmap
     * @return The resId of the random bitmap
     */
    private int getResId() {
        int rand = new Random().nextInt((4 - 1) + 1) + 1;
        switch (rand) {
            case 1:
                return R.drawable.data;
            case 2:
                return R.drawable.data1;
            case 3:
                return R.drawable.data2;
            case 4:
                return R.drawable.data3;
        }
        return R.drawable.data;
    }

    public class RenderScriptTask implements Runnable {
        MainActivity activity;

        public RenderScriptTask(MainActivity mainActivity) {
            activity = mainActivity;
        }

        public void run() {
            int resId = getResId();
            mBitmapIn = loadBitmap(resId);
            mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(), mBitmapIn.getConfig());

            ((ImageView) activity.findViewById(R.id.displayin)).setImageBitmap(mBitmapIn);
            createScript();
            ((ImageView) activity.findViewById(R.id.displayout)).setImageBitmap(mBitmapOut);
        }
    }
}