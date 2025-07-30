package com.codex.agent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("About");
        }

        TextView versionText = findViewById(R.id.text_version);
        TextView descriptionText = findViewById(R.id.text_description);
        TextView githubLink = findViewById(R.id.text_github);

        versionText.setText("Version 1.0.0");
        descriptionText.setText("AgentC is an advanced Android client for AI-powered code assistance. " +
                "It provides a seamless interface for interacting with AI models for coding tasks, " +
                "file management, and code analysis.");

        githubLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://github.com/codex-agent/agentc"));
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}