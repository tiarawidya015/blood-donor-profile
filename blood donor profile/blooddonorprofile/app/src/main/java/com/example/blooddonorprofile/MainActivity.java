package com.example.blooddonorprofile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etNama, etUmur, etTelepon, etAlamat;
    Spinner spGolongan;
    Switch switchPengingat;
    Button btnSimpan, btnHapus;
    TextView tvHasil;

    PrefsManager prefsManager;

    String[] daftarGolongan = {"A", "B", "AB", "O"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan komponen XML
        etNama = findViewById(R.id.etNama);
        etUmur = findViewById(R.id.etUmur);
        etTelepon = findViewById(R.id.etTelepon);
        etAlamat = findViewById(R.id.etAlamat);

        spGolongan = findViewById(R.id.spGolongan);

        // ID Switch sesuai XML
        switchPengingat = findViewById(R.id.swPengingat);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnHapus = findViewById(R.id.btnHapus);

        tvHasil = findViewById(R.id.tvHasil);

        // Menghubungkan SharedPreferences
        prefsManager = new PrefsManager(this);

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

        // Tombol simpan
        btnSimpan.setOnClickListener(v -> simpanData());

        // Tombol hapus
        btnHapus.setOnClickListener(v -> hapusData());

        // Menampilkan data tersimpan
        tampilkanData();
    }

    private void simpanData() {

        String nama = etNama.getText().toString().trim();
        String umur = etUmur.getText().toString().trim();
        String telepon = etTelepon.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();

        String golongan = spGolongan.getSelectedItem().toString();

        boolean pengingat = switchPengingat.isChecked();

        if (nama.isEmpty() || umur.isEmpty()
                || telepon.isEmpty() || alamat.isEmpty()) {

            Toast.makeText(this,
                    "Semua data harus diisi!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        prefsManager.simpanBiodata(
                nama,
                umur,
                telepon,
                golongan,
                alamat,
                pengingat
        );

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

        String hasil =
                "BIODATA DONOR DARAH\n\n" +
                        "Nama: " + prefsManager.getNama() + "\n" +
                        "Umur: " + prefsManager.getUmur() + "\n" +
                        "Telepon: " + prefsManager.getTelepon() + "\n" +
                        "Golongan Darah: " + prefsManager.getGolongan() + "\n" +
                        "Alamat: " + prefsManager.getAlamat() + "\n" +
                        "Pengingat: " +
                        (prefsManager.getPengingat()
                                ? "Aktif" : "Tidak Aktif");

        tvHasil.setText(hasil);
    }

    private void hapusData() {

        prefsManager.hapusBiodata();

        etNama.setText("");
        etUmur.setText("");
        etTelepon.setText("");
        etAlamat.setText("");

        spGolongan.setSelection(0);
        switchPengingat.setChecked(false);

        tvHasil.setText("Belum ada biodata tersimpan.");

        Toast.makeText(this,
                "Biodata berhasil dihapus!",
                Toast.LENGTH_SHORT).show();
    }
}