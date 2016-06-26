package com.brankomostic.remiscorekeeper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.brankomostic.remiscorekeeper.utils.DialogFactory;
import com.brankomostic.remiscorekeeper.utils.Remi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Remi.instantiate(getApplication());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GameFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        GameFragment.setAddIcon(menu.findItem(R.id.action_add_score));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_new_game) {
            DialogFactory.startNewGame(this);
            return true;
        } else if(id == R.id.action_add_score) {
            DialogFactory.startNextRound(this);
            return true;
        } else if(id == R.id.action_report_problem) {
            Intent report = new Intent(Intent.ACTION_SENDTO);
            report.setData(Uri.parse("mailto:"));
            report.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
            report.putExtra(Intent.EXTRA_SUBJECT, "Remi Score Keeper Feedback/Bug Report");
            startActivity(report);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
