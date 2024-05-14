package com.example.braguia2.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.braguia2.R;
import com.example.braguia2.model.App.Contacts;
import com.example.braguia2.viewmodel.AppViewModel;

import java.util.List;

public class support extends Fragment {

    public support() {
        super(R.layout.fragment_support);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        TextView supporttitulo = view.findViewById(R.id.CustomerSupportTitulo);
        TextView supportsite = view.findViewById(R.id.customer_site_txt);
        TextView supportemail = view.findViewById(R.id.customer_mail_txt);
        TextView supportphone = view.findViewById(R.id.customer_phone_txt);

        TextView medicaltitulo = view.findViewById(R.id.EmergencyTitulo);
        TextView medicalsite = view.findViewById(R.id.medical_site_txt);
        TextView medicalemail = view.findViewById(R.id.medical_mail_txt);
        TextView medicalphone = view.findViewById(R.id.medical_phone_txt);

        AppViewModel appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.getApp().observe(getViewLifecycleOwner(),app ->{
            List<Contacts> contacts = app.getContacts();
            if(!contacts.isEmpty()){
                Contacts support = contacts.get(0);
                Contacts medical = contacts.get(1);

                String supportUrl = support.getContactUrl();
                String supportMail = support.getContactMail();
                String supportPhone = support.getContactPhone();

                String medicalUrl = medical.getContactUrl();
                String medicalMail = medical.getContactMail();
                String medicalPhone = medical.getContactPhone();

                supporttitulo.setText(support.getContactName());
                supportsite.setText(supportUrl);
                supportemail.setText(supportMail);
                supportphone.setText(supportPhone);

                medicaltitulo.setText(medical.getContactName());
                medicalsite.setText(medicalUrl);
                medicalemail.setText(medicalMail);
                medicalphone.setText(medicalPhone);

                supportsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoUrl(supportUrl);
                    }
                });

                medicalsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoUrl(medicalUrl);
                    }
                });

                supportphone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callNumber(supportPhone);
                    }
                });

                medicalphone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callNumber(medicalPhone);
                    }
                });

                supportemail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMail(supportMail);
                    }
                });

                medicalemail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMail(medicalMail);
                    }
                });
            }
        });

        return view;
    }

    private void sendMail(String mail) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose email Client:"));
    }

    private void callNumber(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phoneNum));
        startActivity(intent);
    }

    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}