package com.railway.railwayconductor.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.railway.railwayconductor.DI;
import com.railway.railwayconductor.R;
import com.railway.railwayconductor.activity.listener.QRCodeReaderOnStart;
import com.railway.railwayconductor.activity.listener.QRCodeReaderOnVerifyClick;
import com.railway.railwayconductor.business.api.entity.Ticket;
import com.railway.railwayconductor.business.api.storage.Storage;
import com.railway.railwayconductor.business.api.storage.Storage.AlreadyExists;
import com.railway.railwayconductor.business.security.Signature.SignatureValidator;
import com.railway.railwayconductor.business.security.Ticket.SecureTicket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class QRCodeReaderActivity extends MenuActivity {
    // Esta viagem tem 2 bilhetes
    public String departure = "Station A";
    public String arrival = "Station B";
    public String timestamp = "1422820800000";

    public PieChart chart;
    private int totalTickets;
    private int usedTickets;
    TextView infoTrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);

        this.arrival = getIntent().getStringExtra("arrival");
        this.departure = getIntent().getStringExtra("departure");
        this.timestamp = getIntent().getStringExtra("departureTime");

        this.chart = initializeChart();
        this.infoTrip = (TextView) findViewById(R.id.result);
//        this.infoTrip.setText(departure + " to " + arrival + " on " + new Timestamp(Long.parseLong(timestamp)).toString());

        new QRCodeReaderOnStart(this).execute();
        findViewById(R.id.qrcodereader_verify_button).setOnClickListener(new QRCodeReaderOnVerifyClick());
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        String message = "";
        int icon = 0;
        try{
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                Ticket ticket = new Ticket(new JSONObject(scanResult.getContents()));
                SecureTicket secureTicket = new SecureTicket(ticket);
                SignatureValidator sv = new SignatureValidator(secureTicket,secureTicket);

                boolean validate = sv.validate();
                message = validate ? "Valid Ticket" : "Invalid Ticket";
                icon = validate ? R.drawable.valid : R.drawable.invalid;
                if(validate){
                    DI.get().provideStorage().addValidatedTicketID(Integer.toString(ticket.getId()));
                }
            }
        } catch(AlreadyExists e) {
            icon = R.drawable.danger;
            message = "This ticket was already validated";
        }
        catch (Exception e) {
            icon = R.drawable.invalid;
        } finally {
            new AlertDialog.Builder(this)
                    .setTitle("Ticket")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(icon)
                    .show();
            refreshChartData(true);
        }

    }

    public PieChart initializeChart(){
        PieChart chart = (PieChart) findViewById(R.id.chart);
        chart.notifyDataSetChanged();
        chart.invalidate();
        return chart;
    }

    public void refreshChartData(boolean subtractOne){
        if(subtractOne){
            this.totalTickets--;
            this.usedTickets++;
        }

        PieDataSet dataSet = new PieDataSet(
                new ArrayList<>(Arrays.asList(
                        new Entry(totalTickets,0),
                        new Entry(usedTickets,1)
                )),
                "Tickets"
        );

        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Arrays.asList(Color.rgb(136, 170, 255), Color.rgb(239, 155, 15)));

        this.chart.setData(new PieData(
                new ArrayList<>(Arrays.asList("Total", "Validated")),
                dataSet
        ));
        this.chart.notifyDataSetChanged();
    }


    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public void setUsedTickets(int usedTickets) {
        this.usedTickets = usedTickets;
    }



}
