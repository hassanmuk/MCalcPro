package e.hassanmukhtar.mcalcpro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.roumani.i2c.MPro;


public class MCalcPro_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.tts = new TextToSpeech(this,this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcalcpro_layout);
    }
    public void buttonClicked(View v)
    {
        try
        {
            EditText principleView = (EditText) findViewById(R.id.pBox);
            String principle = principleView.getText().toString();
            EditText amortizationView = (EditText) findViewById(R.id.aBox);
            String amortization = amortizationView.getText().toString();
            EditText interestView = (EditText) findViewById(R.id.iBox);
            String interest = interestView.getText().toString();


            MPro mp = new MPro();
            mp.setPrinciple(principle);
            mp.setAmortization(amortization);
            mp.setInterest(interest);

            String s = "Monthly Payment = " + mp.computePayment("%,.2f");
            s += "\n\n";
            s += "By making this payments monthly for " + amortization +
                    "\n years, the mortgage will be paid in full. But if you terminate the mortgage on its nth" +
                    "anniversary, the balance still owing depends" +
                    "on n as shown below: \n\n";
            s += String.format("%8s", "n") + String.format("%16s", "Balance");
            s += "\n";
            for (int i = 0; i <= 5; i++)
            {
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                s += "\n";
            }
            for (int j = 10; j <= 20; j += 5)
            {
                s += String.format("%8d", j) + mp.outstandingAfter(j, "%,16.0f");
                s += "\n";
            }
            ((TextView) findViewById(R.id.output)).setText(s);

            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);

        }
        catch (Exception e)
        {
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }
    }



    @Override
    public void onInit(int status) {
        this.tts.setLanguage(Locale.US);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax*ax + ay*ay + az*az);
        if (a > 20)
        {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }
}
