package bangkokguy.development.android.SmsForwarder;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnticipateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*

As Konstantin said, this code will probably be ignored after you exceed 4 lines unless you remove android:maxLines="4"

        This is how you would set the height in code:

        TextView tv = (TextView) findViewById(R.id.TextView02);
        int height_in_pixels = tv.getLineCount() * tv.getLineHeight(); //approx height text
        tv.setHeight(height_in_pixels);

        If you want to use dip units, which allows your app to scale across multiple screen sizes, you would multiply the pixel count by the value returned by getResources().getDisplayMetrics().density;
        This depends on your desired behavior, but you might also consider having the TextView size fixed, and allowing the user to scroll the text:

        TextView tv = (TextView)findViewById(R.id.TextView02);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());

        How to get dynamic value for height_in_pixels? – neha Aug 19 '10 at 13:40
        I edited my answer to get the approximate height of the text. You might want to add a small buffer so that characters that hang below the line aren't cut off ("g", "y", etc). – Aaron C Aug 19 '10 at 13:48
        Thanx Aaron C, but the text has disappeared after setting its height. The height is getting set to 0 despite tv not being null. – neha Aug 19 '10 at 14:09
        getLineCount() will return 0 if the text hasn't been rendered yet. You can call invalidate() to force a redraw, but setText() should be sufficient to make getLineCount() valid. – Aaron C Aug 19 '10 at 14:44
*/

public class ResultActivity extends AppCompatActivity {

    final String TAG = "ResultActivity";
    final boolean DEBUG = true;

    Bundle bundle;
    Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(DEBUG)Log.d(TAG, "onCreate");

        startIntent = getIntent();
        bundle = startIntent.getExtras();

        ((TextView) findViewById(R.id.msgText)).setText(bundle.getString("msgText"));
        ((ListView) findViewById(R.id.msgSenderEmail)).setAdapter(
            new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    bundle.getStringArrayList("msgSenderEmail")));
        ((ListView) findViewById(R.id.msgSenderName)).setAdapter(
            new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    bundle.getStringArrayList("msgSenderName")));
        ((TextView) findViewById(R.id.msgSenderNumber)).setText(bundle.getString("msgSenderNumber"));
        ((TextView) findViewById(R.id.numberOfContactsFound)).setText(bundle.getString("numberOfContactsFound"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(Color.BLUE);
        //toolbar.setTitle(msgText);
        setSupportActionBar(toolbar);

        View v = findViewById(R.id.text_view);
//scaleX  textColor
        ObjectAnimator animation = ObjectAnimator.ofFloat(v, "scaleY", 2.0f, 1.0f);
        //ObjectAnimator animation = ObjectAnimator.ofArgb(v, "textColor", Color.RED, Color.BLUE);
        animation.setDuration(3600);
        animation.setRepeatCount(0);
        animation.setInterpolator(new AnticipateInterpolator());
        animation.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_farka) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
