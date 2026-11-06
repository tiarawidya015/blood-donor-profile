package com.example.blooddonorprofile;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String KEY_DARK_MODE = "dark_mode";

    private SharedPreferences preferences;

    public PrefsManager(Context context) {
        preferences = context.getSharedPreferences(
                "DonorPreferences",
                Context.MODE_PRIVATE
        );
    }

    // ---------- Biodata ----------

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
        editor.putLong("terakhir_disimpan", System.currentTimeMillis());

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

    public long getTerakhirDisimpan() {
        return preferences.getLong("terakhir_disimpan", 0L);
    }

    // Hapus biodata saja. JANGAN pakai clear(), karena akan ikut
    // menghapus pengaturan tema gelap/terang.
    public void hapusBiodata() {
        preferences.edit()
                .remove("nama")
                .remove("umur")
                .remove("telepon")
                .remove("golongan")
                .remove("alamat")
                .remove("pengingat")
                .remove("terakhir_disimpan")
                .apply();
    }

    // ---------- Pengaturan tema ----------

    public void setDarkMode(boolean aktif) {
        preferences.edit().putBoolean(KEY_DARK_MODE, aktif).apply();
    }

    public boolean isDarkMode() {
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    // false = pengguna belum pernah memilih tema (ikuti pengaturan sistem)
    public boolean sudahAdaTema() {
        return preferences.contains(KEY_DARK_MODE);
    }
}
