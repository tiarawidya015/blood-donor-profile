package com.example.blooddonorprofile;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private SharedPreferences preferences;

    public PrefsManager(Context context) {
        preferences = context.getSharedPreferences(
                "DonorPreferences",
                Context.MODE_PRIVATE
        );
    }

    public void simpanBiodata(
            String nama,
            String umur,
            String telepon,
            String golongan,
            String alamat,
            boolean pengingat) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("nama", nama);
        editor.putString("umur", umur);
        editor.putString("telepon", telepon);
        editor.putString("golongan", golongan);
        editor.putString("alamat", alamat);
        editor.putBoolean("pengingat", pengingat);

        editor.apply();
    }

    public String getNama() {
        return preferences.getString("nama", "");
    }

    public String getUmur() {
        return preferences.getString("umur", "");
    }

    public String getTelepon() {
        return preferences.getString("telepon", "");
    }

    public String getGolongan() {
        return preferences.getString("golongan", "");
    }

    public String getAlamat() {
        return preferences.getString("alamat", "");
    }

    public boolean getPengingat() {
        return preferences.getBoolean("pengingat", false);
    }

    public void hapusBiodata() {
        preferences.edit().clear().apply();
    }
}