package com.example.assignment1zahin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final double ECONOMY_LIMIT = 20.0;
    private static final double BUSINESS_LIMIT = 40.0;
    private static final double TAX_RATE = 0.08;

    private View btn;
    private View btnReset;
    private TextInputEditText etWeight, etRate;
    private MaterialButtonToggleGroup toggleClass;
    private TextView tvExcessWeight, tvExcessCharge, tvTax, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        btnReset = findViewById(R.id.btnReset);
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        toggleClass = findViewById(R.id.toggleClass);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);

        btn.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        Log.d(TAG, "onCreate: views initialised");
    }

    // ----- Toolbar menu -----
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareApp();
            return true;
        } else if (id == R.id.action_about) {
            Log.d(TAG, "Opening About page");
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_message) + " " + getString(R.string.app_url));
        Log.d(TAG, "shareApp: opening share sheet");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    // ----- Calculator -----
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button) {
            calculate();
        } else if (id == R.id.btnReset) {
            reset();
        }
    }

    private void calculate() {
        String weightText = etWeight.getText().toString().trim();
        String rateText = etRate.getText().toString().trim();

        if (weightText.isEmpty()) {
            Log.w(TAG, "calculate: weight is empty");
            etWeight.setError("Please enter the baggage weight in kg");
            Toast.makeText(this, "Weight is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rateText.isEmpty()) {
            Log.w(TAG, "calculate: rate is empty");
            etRate.setError("Please enter the rate per kg in RM");
            Toast.makeText(this, "Rate is required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            double rate = Double.parseDouble(rateText);

            if (weight < 0 || rate < 0) {
                Log.w(TAG, "calculate: negative value entered");
                Toast.makeText(this, "Values cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isBusiness = toggleClass.getCheckedButtonId() == R.id.btnBusiness;
            double limit = isBusiness ? BUSINESS_LIMIT : ECONOMY_LIMIT;

            double excessWeight = Math.max(0, weight - limit);
            double excessCharge = excessWeight * rate;
            double tax = excessCharge * TAX_RATE;
            double total = excessCharge + tax;

            Log.d(TAG, "calculate: weight=" + weight
                    + ", class=" + (isBusiness ? "business" : "economy")
                    + ", rate=" + rate
                    + ", excess=" + excessWeight
                    + ", total=" + total);

            tvExcessWeight.setText(String.format(Locale.US, "Excess weight: %.2f kg", excessWeight));
            tvExcessCharge.setText(String.format(Locale.US, "Excess charge: RM %.2f", excessCharge));
            tvTax.setText(String.format(Locale.US, "Service tax (8%%): RM %.2f", tax));
            tvTotal.setText(String.format(Locale.US, "Total payable: RM %.2f", total));

            if (excessWeight == 0) {
                Toast.makeText(this, "Within free allowance, no excess charge", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "calculate: invalid number entered", e);
            etWeight.setError("Invalid number");
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void reset() {
        etWeight.setText("");
        etRate.setText("");
        etWeight.setError(null);
        etRate.setError(null);
        toggleClass.check(R.id.btnEconomy);
        tvExcessWeight.setText("Excess weight: 0.00 kg");
        tvExcessCharge.setText("Excess charge: RM 0.00");
        tvTax.setText("Service tax (8%): RM 0.00");
        tvTotal.setText("Total payable: RM 0.00");
        Log.d(TAG, "reset: form cleared");
    }
}