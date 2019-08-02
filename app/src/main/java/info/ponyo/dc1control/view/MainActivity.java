package info.ponyo.dc1control.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import info.ponyo.dc1control.R;

/**
 * @author zxq
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, DeviceFragment.newInstance())
                .commit();
    }
}
