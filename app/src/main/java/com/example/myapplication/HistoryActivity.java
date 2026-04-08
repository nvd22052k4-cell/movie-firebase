package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TicketAdapter adapter;
    private List<Ticket> ticketList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView tvNoHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lịch sử đặt vé");
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvHistory = findViewById(R.id.rvHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(ticketList);
        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        // Bỏ .orderBy để tránh lỗi FAILED_PRECONDITION (Index)
        db.collection("tickets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ticketList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ticket ticket = document.toObject(Ticket.class);
                            ticketList.add(ticket);
                        }
                        
                        // Sắp xếp danh sách bằng Code Java (Ngày mới nhất lên đầu)
                        Collections.sort(ticketList, (t1, t2) -> {
                            if (t1.getBookingDate() == null || t2.getBookingDate() == null) return 0;
                            return t2.getBookingDate().compareTo(t1.getBookingDate());
                        });

                        if (ticketList.isEmpty()) {
                            tvNoHistory.setVisibility(View.VISIBLE);
                        } else {
                            tvNoHistory.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi";
                        Toast.makeText(HistoryActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
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
