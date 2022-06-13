package ca.tinyweb.comp8531_project;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.widget.ImageView;

import com.example.android.rs.hellocompute.ScriptC_mono;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmapIn = loadBitmap(R.drawable.data);
        mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(),
                mBitmapIn.getConfig());

        ImageView in = findViewById(R.id.displayin);
        in.setImageBitmap(mBitmapIn);

        ImageView out = findViewById(R.id.displayout);
        out.setImageBitmap(mBitmapOut);

        createScript();
    }

    private void createScript() {
        RenderScript mRS = RenderScript.create(this);
        Allocation mInAllocation = Allocation.createFromBitmap(mRS,
                mBitmapIn,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());
        ScriptC_mono mScript = new ScriptC_mono(mRS);
        mScript.forEach_root(mInAllocation, mOutAllocation);
        mOutAllocation.copyTo(mBitmapOut);
    }

    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }
}