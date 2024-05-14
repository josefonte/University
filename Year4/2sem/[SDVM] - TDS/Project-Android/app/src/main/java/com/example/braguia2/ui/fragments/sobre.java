package com.example.braguia2.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.braguia2.R;
import com.example.braguia2.model.App.Partners;
import com.example.braguia2.model.App.Socials;
import com.example.braguia2.utils.LocationUtility;
import com.example.braguia2.viewmodel.AppViewModel;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class sobre extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final Object REQUEST_CODE_LOCATION_PERMISSION = 0;

    public sobre() {
        // Required empty public constructor
        super(R.layout.fragment_sobre);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        requestPermissions();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sobre, container, false);

        TextView appName = view.findViewById(R.id.app_name);
        TextView slogan = view.findViewById(R.id.slogan);
        TextView desc = view.findViewById(R.id.desc);
        TextView umMail = view.findViewById(R.id.umMail);
        TextView umNome = view.findViewById(R.id.umNome);
        TextView umTel = view.findViewById(R.id.umTel);

        ImageButton facebook = view.findViewById(R.id.facebook);
        ImageButton umbutton = view.findViewById(R.id.UM_buton);

        AppViewModel appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.getApp().observe(getViewLifecycleOwner(), app -> {
            appName.setText(app.getApp_name());
            slogan.setText(app.getApp_desc());
            desc.setText(app.getApp_landing_page_text());

            List<Partners> partners = app.getPartners();
            List<Socials> socials = app.getSocials();


            if(!socials.isEmpty()){
                Socials firstSocial = socials.get(0);
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoUrl(firstSocial.getSocialUrl());
                    }
                });
            }

            if(!partners.isEmpty()){
                Partners firstpartner = partners.get(0);

                umMail.setText(firstpartner.getPartnerMail());
                umNome.setText(firstpartner.getPartnerName());
                umTel.setText(firstpartner.getPartnerPhone());
                umbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoUrl(firstpartner.getPartnerUrl());
                    }
                });

                umMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMail(firstpartner.getPartnerMail());
                    }
                });

                umTel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callNumber(firstpartner.getPartnerPhone());
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
    startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }


    private void requestPermissions(){
        Context context = requireContext();
        if(LocationUtility.hasLocationPermissions(context)){
            return;
        }
        String[] permissions=null;
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }else{
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }
        EasyPermissions.requestPermissions(
                (Fragment) this,
                "Precisa de aceitar as permissões de localização para usar esta app",
                (Integer) REQUEST_CODE_LOCATION_PERMISSION,
                permissions
        );
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            new AppSettingsDialog.Builder(this)
                    .setThemeResId(pub.devrel.easypermissions.R.style.AlertDialog_AppCompat);
        }else{
            requestPermissions();
        }
    }

    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}