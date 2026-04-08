package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách phim");
        }

        db = FirebaseFirestore.getInstance();
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(movieList, this);
        rvMovies.setAdapter(adapter);

        // Làm sạch và đồng bộ phim chuẩn
        syncCleanMovies();
    }

    private void syncCleanMovies() {
        // Xóa triệt để các phim bị lỗi link ảnh
        db.collection("movies").document("1").delete();
        db.collection("movies").document("2").delete();
        db.collection("movies").document("4").delete();

        List<Movie> cleanList = new ArrayList<>();
        cleanList.add(new Movie("3", "Spider-Man", "Miles Morales catapults across the Multiverse.", "https://m.media-amazon.com/images/M/MV5BMzI0NmVkMjEtYmY4MS00ZDMxLTlkZmEtMzU4MDQxYTMzMjU2XkEyXkFqcGdeQXVyMzQ0MzA0NTM@._V1_.jpg", 8.9, "Animation", 100000));
        cleanList.add(new Movie("5", "The Dark Knight", "Batman raises the stakes in his war on crime.", "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg", 9.0, "Action", 80000));
        cleanList.add(new Movie("6", "Inception", "A thief who steals corporate secrets through dream-sharing.", "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg", 8.8, "Sci-Fi", 85000));

        List<Task<Void>> tasks = new ArrayList<>();
        for (Movie m : cleanList) {
            tasks.add(db.collection("movies").document(m.getId()).set(m));
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(t -> {
            fetchFinalList();
        });
    }

    private void fetchFinalList() {
        db.collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                movieList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Movie m = doc.toObject(Movie.class);
                    // Chỉ nạp những phim thuộc danh sách sạch (ID 3, 5, 6)
                    if (m.getId().equals("3") || m.getId().equals("5") || m.getId().equals("6")) {
                        movieList.add(m);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 2, 0, "Lịch sử đặt vé");
        menu.add(0, 1, 0, "Đăng xuất");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (item.getItemId() == 2) {
            startActivity(new Intent(this, HistoryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBookClick(Movie movie) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("movieId", movie.getId());
        intent.putExtra("movieTitle", movie.getTitle());
        intent.putExtra("moviePrice", movie.getPrice());
        startActivity(intent);
    }
}
