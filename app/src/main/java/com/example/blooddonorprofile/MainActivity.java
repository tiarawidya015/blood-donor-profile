package com.example.blooddonorprofile;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextInputLayout tilNama, tilUmur, tilTelepon, tilAlamat;
    TextInputEditText etNama, etUmur, etTelepon, etAlamat;
    Spinner spGolongan;
    MaterialSwitch switchPengingat, switchTema;
    MaterialButton btnSimpan, btnHapus;
    TextView tvHasil;

    PrefsManager prefsManager;

    String[] daftarGolongan = {"A", "B", "AB", "O"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Baca preferensi tema SEBELUM layout dibuat agar tidak berkedip
        prefsManager = new PrefsManager(this);
        terapkanTema();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan komponen XML
        tilNama = findViewById(R.id.tilNama);
        tilUmur = findViewById(R.id.tilUmur);
        tilTelepon = findViewById(R.id.tilTelepon);
        tilAlamat = findViewById(R.id.tilAlamat);

        etNama = findViewById(R.id.etNama);
        etUmur = findViewById(R.id.etUmur);
        etTelepon = findViewById(R.id.etTelepon);
        etAlamat = findViewById(R.id.etAlamat);

        spGolongan = findViewById(R.id.spGolongan);
        switchPengingat = findViewById(R.id.swPengingat);
        switchTema = findViewById(R.id.swTema);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnHapus = findViewById(R.id.btnHapus);
        tvHasil = findViewById(R.id.tvHasil);

        // Pilihan golongan darah
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                daftarGolongan
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spGolongan.setAdapter(adapter);

        btnSimpan.setOnClickListener(v -> simpanData());
        btnHapus.setOnClickListener(v -> konfirmasiHapus());

        // Isi form dari data tersimpan hanya saat Activity baru dibuat
        // (bukan saat dibuat ulang karena rotasi/ganti tema, agar ketikan
        // yang belum disimpan tidak tertimpa)
        if (savedInstanceState == null) {
            isiFormDariPrefs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Baca ulang pengaturan tema dari SharedPreferences
        terapkanTema();

        boolean gelap = prefsManager.sudahAdaTema()
                ? prefsManager.isDarkMode()
                : sistemSedangGelap();

        // Lepas listener dulu supaya setChecked() tidak memicu penyimpanan ulang
        switchTema.setOnCheckedChangeListener(null);
        switchTema.setChecked(gelap);
        switchTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setDarkMode(isChecked); // disimpan dengan apply()
            terapkanTema();
        });

        tampilkanData();
    }

    // ---------- Tema ----------

    private void terapkanTema() {
        // Belum pernah memilih -> biarkan mengikuti pengaturan sistem
        if (!prefsManager.sudahAdaTema()) return;

        int mode = prefsManager.isDarkMode()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        // Cek dulu agar tidak recreate berulang
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    private boolean sistemSedangGelap() {
        int flag = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }

    // ---------- Biodata ----------

    private void isiFormDariPrefs() {
        if (prefsManager.getNama().isEmpty()) return;

        etNama.setText(prefsManager.getNama());
        etUmur.setText(prefsManager.getUmur());
        etTelepon.setText(prefsManager.getTelepon());
        etAlamat.setText(prefsManager.getAlamat());
        switchPengingat.setChecked(prefsManager.getPengingat());

        for (int i = 0; i < daftarGolongan.length; i++) {
            if (daftarGolongan[i].equals(prefsManager.getGolongan())) {
                spGolongan.setSelection(i);
                break;
            }
        }
    }

    private boolean validasi(String nama, String umur,
                             String telepon, String alamat) {
        tilNama.setError(null);
        tilUmur.setError(null);
        tilTelepon.setError(null);
        tilAlamat.setError(null);

        boolean valid = true;

        if (nama.length() < 3) {
            tilNama.setError("Nama minimal 3 karakter");
            valid = false;
        }

        int umurInt = -1;
        try {
            umurInt = Integer.parseInt(umur);
        } catch (NumberFormatException ignored) { }
        if (umurInt < 17 || umurInt > 60) {
            tilUmur.setError("Umur pendonor 17–60 tahun");
            valid = false;
        }

        if (!telepon.matches("^(\\+62|62|0)[0-9]{9,13}$")) {
            tilTelepon.setError("Nomor tidak valid (contoh: 081234567890)");
            valid = false;
        }

        if (alamat.length() < 10) {
            tilAlamat.setError("Alamat minimal 10 karakter");
            valid = false;
        }

        return valid;
    }

    private void simpanData() {
        String nama = String.valueOf(etNama.getText()).trim();
        String umur = String.valueOf(etUmur.getText()).trim();
        String telepon = String.valueOf(etTelepon.getText()).trim();
        String alamat = String.valueOf(etAlamat.getText()).trim();
        String golongan = spGolongan.getSelectedItem().toString();
        boolean pengingat = switchPengingat.isChecked();

        if (!validasi(nama, umur, telepon, alamat)) {
            Toast.makeText(this,
                    "Periksa kembali data yang diisi",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        prefsManager.simpanBiodata(
                nama, umur, telepon, golongan, alamat, pengingat);

        Toast.makeText(this,
                "Biodata berhasil disimpan!",
                Toast.LENGTH_SHORT).show();

        tampilkanData();
    }

    private void tampilkanData() {
        String nama = prefsManager.getNama();

        if (nama.isEmpty()) {
            tvHasil.setText("Belum ada biodata tersimpan.");
            return;
        }

        String waktu = new SimpleDateFormat(
                "dd MMM yyyy, HH:mm", new Locale("id", "ID"))
                .format(new Date(prefsManager.getTerakhirDisimpan()));

        String hasil =
                "BIODATA DONOR DARAH\n\n" +
                        "Nama: " + nama + "\n" +
                        "Umur: " + prefsManager.getUmur() + " tahun\n" +
                        "Telepon: " + prefsManager.getTelepon() + "\n" +
                        "Golongan Darah: " + prefsManager.getGolongan() + "\n" +
                        "Alamat: " + prefsManager.getAlamat() + "\n" +
                        "Pengingat: " +
                        (prefsManager.getPengingat() ? "Aktif" : "Tidak Aktif") + "\n\n" +
                        "Terakhir disimpan: " + waktu;

        tvHasil.setText(hasil);
    }

    private void konfirmasiHapus() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hapus biodata?")
                .setMessage("Biodata yang tersimpan akan dihapus. "
                        + "Pengaturan tema tidak terpengaruh.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", (d, w) -> hapusData())
                .show();
    }

    private void hapusData() {
        prefsManager.hapusBiodata();

        etNama.setText("");
        etUmur.setText("");
        etTelepon.setText("");
        etAlamat.setText("");
        tilNama.setError(null);
        tilUmur.setError(null);
        tilTelepon.setError(null);
        tilAlamat.setError(null);

        spGolongan.setSelection(0);
        switchPengingat.setChecked(false);

        tvHasil.setText("Belum ada biodata tersimpan.");

        Toast.makeText(this,
                "Biodata berhasil dihapus!",
                Toast.LENGTH_SHORT).show();
    }
}
