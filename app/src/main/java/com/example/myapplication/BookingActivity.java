package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class BookingActivity extends AppCompatActivity {

    private TextView tvMovieTitle;
    private Spinner spShowtimes;
    private EditText etSeat;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String movieId, movieTitle;
    private double moviePrice;
    private static final String CHANNEL_ID = "booking_notifications";
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Đặt vé xem phim");
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        movieId = getIntent().getStringExtra("movieId");
        movieTitle = getIntent().getStringExtra("movieTitle");
        moviePrice = getIntent().getDoubleExtra("moviePrice", 0.0);

        tvMovieTitle = findViewById(R.id.tvBookingMovieTitle);
        spShowtimes = findViewById(R.id.spShowtimes);
        etSeat = findViewById(R.id.etSeat);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        tvMovieTitle.setText(movieTitle);

        String[] times = {"10:00 SA", "01:00 CH", "04:00 CH", "07:00 CH", "10:00 CH"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
        spShowtimes.setAdapter(adapter);

        btnConfirm.setOnClickListener(v -> showConfirmDialog());
        
        createNotificationChannel();
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void showConfirmDialog() {
        String seat = etSeat.getText().toString().trim();
        if (seat.isEmpty()) {
            etSeat.setError("Vui lòng nhập số ghế");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt vé")
                .setMessage("Bạn có chắc chắn muốn đặt vé cho phim: " + movieTitle + " không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> confirmBooking())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmBooking() {
        String showtime = spShowtimes.getSelectedItem().toString();
        String seat = etSeat.getText().toString().trim();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String ticketId = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(ticketId, userId, movieId, movieTitle, showtime, seat, moviePrice, new Date());

        db.collection("tickets").document(ticketId)
                .set(ticket)
                .addOnSuccessListener(aVoid -> {
                    sendLocalNotification(movieTitle, showtime, seat);
                    Toast.makeText(BookingActivity.this, "Đặt vé thành công!", Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(BookingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish(); 
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Booking Channel";
            String description = "Channel for movie booking notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendLocalNotification(String title, String time, String seat) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Đặt vé thành công! 🎬")
                .setContentText("Phim: " + title + " - Suất: " + time + " - Ghế: " + seat)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
