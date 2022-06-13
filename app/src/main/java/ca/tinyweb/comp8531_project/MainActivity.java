package ca.tinyweb.comp8531_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.rs.hellocompute.ScriptC_mono;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;
    private final int ITERATIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView in = findViewById(R.id.displayin);
        ImageView out = findViewById(R.id.displayout);

        long startTimeInMills = System.currentTimeMillis();

        for (int i=0; i<=ITERATIONS; i++) {
            int rand = new Random().nextInt((4 - 1) + 1) + 1;

            switch (rand) {
                case 1:
                    mBitmapIn = loadBitmap(R.drawable.data);
                    break;
                case 2:
                    mBitmapIn = loadBitmap(R.drawable.data1);
                    break;
                case 3:
                    mBitmapIn = loadBitmap(R.drawable.data2);
                    break;
                case 4:
                    mBitmapIn = loadBitmap(R.drawable.data3);
                    break;
            }
            mBitmapIn = loadBitmap(R.drawable.data);
            mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(), mBitmapIn.getConfig());

            in.setImageBitmap(mBitmapIn);
            createScript();
            out.setImageBitmap(mBitmapOut);
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
}