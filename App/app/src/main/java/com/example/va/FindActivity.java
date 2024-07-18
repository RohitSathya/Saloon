package com.example.va;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FindActivity extends BaseActivity {

    private SearchView searchView;
    private RecyclerView paidVideosRecyclerView, paidShowsRecyclerView, freeVideosRecyclerView, freeShowsRecyclerView, genresRecyclerView, languagesRecyclerView;
    private VideoAdapter paidVideosAdapter, paidShowsAdapter, freeVideosAdapter, freeShowsAdapter;
    private GenreAdapter genreAdapter;
    private ChangeLanguageAdapter languageAdapter;
    private List<VideoItem> paidVideosList, paidShowsList, freeVideosList, freeShowsList, genresList, languagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        searchView = findViewById(R.id.search_view);

        paidVideosRecyclerView = findViewById(R.id.recycler_view_paid_videos);
        paidShowsRecyclerView = findViewById(R.id.recycler_view_paid_shows);
        freeVideosRecyclerView = findViewById(R.id.recycler_view_free_videos);
        freeShowsRecyclerView = findViewById(R.id.recycler_view_free_shows);
        genresRecyclerView = findViewById(R.id.recycler_view_genres);
        languagesRecyclerView = findViewById(R.id.recycler_view_languages);

        paidVideosList = new ArrayList<>();
        paidShowsList = new ArrayList<>();
        freeVideosList = new ArrayList<>();
        freeShowsList = new ArrayList<>();
        genresList = new ArrayList<>();
        languagesList = new ArrayList<>();

        setupRecyclerView(paidVideosRecyclerView, paidVideosList, true);
        setupRecyclerView(paidShowsRecyclerView, paidShowsList, true);
        setupRecyclerView(freeVideosRecyclerView, freeVideosList, true);
        setupRecyclerView(freeShowsRecyclerView, freeShowsList, true);
        setupRecyclerView(genresRecyclerView, genresList, false);
        setupRecyclerView(languagesRecyclerView, languagesList, false);

        loadDummyData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search
                return false;
            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<VideoItem> itemList, boolean isCategory) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        VideoAdapter adapter = new VideoAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        if (recyclerView == paidVideosRecyclerView) {
            paidVideosAdapter = adapter;
        } else if (recyclerView == paidShowsRecyclerView) {
            paidShowsAdapter = adapter;
        } else if (recyclerView == freeVideosRecyclerView) {
            freeVideosAdapter = adapter;
        } else if (recyclerView == freeShowsRecyclerView) {
            freeShowsAdapter = adapter;
        } else if (recyclerView == genresRecyclerView) {
            genreAdapter = new GenreAdapter(this, itemList);
            recyclerView.setAdapter(genreAdapter);
        } else if (recyclerView == languagesRecyclerView) {
            languageAdapter = new ChangeLanguageAdapter(this, itemList);
            recyclerView.setAdapter(languageAdapter);
        }
    }

    private void loadDummyData() {
        // Paid Videos
        paidVideosList.add(new VideoItem("1", "Paid Video 1", "data:image/jpeg;base64,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", "https://www.w3schools.com/html/mov_bbb.mp4"));
        paidVideosList.add(new VideoItem("2", "Paid Video 2", "https://m.media-amazon.com/images/M/MV5BYWE3MDVkN2EtNjQ5MS00ZDQ4LTliNzYtMjc2YWMzMDEwMTA3XkEyXkFqcGdeQXVyMTEzMTI1Mjk3._V1_.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        paidVideosAdapter.notifyDataSetChanged();

        // Paid Shows
        paidShowsList.add(new VideoItem("1", "Paid Show 1", "https://example.com/paidshow1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        paidShowsList.add(new VideoItem("2", "Paid Show 2", "https://example.com/paidshow2.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        paidShowsAdapter.notifyDataSetChanged();

        // Free Videos
        freeVideosList.add(new VideoItem("1", "Free Video 1", "https://example.com/freevideo1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        freeVideosList.add(new VideoItem("2", "Free Video 2", "https://example.com/freevideo2.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        freeVideosAdapter.notifyDataSetChanged();

        // Free Shows
        freeShowsList.add(new VideoItem("1", "Free Show 1", "https://example.com/freeshow1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        freeShowsList.add(new VideoItem("2", "Free Show 2", "https://example.com/freeshow2.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        freeShowsAdapter.notifyDataSetChanged();

        // Genres
        genresList.add(new VideoItem("1", "Girls Haircut", "https://example.com/girlshaircut1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        genresList.add(new VideoItem("2", "Women Haircut", "https://example.com/womenhaircut1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        genresList.add(new VideoItem("3", "Desi Makeover", "https://example.com/desimakeover1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        genreAdapter.notifyDataSetChanged();

        // Languages
        languagesList.add(new VideoItem("1", "Hair Play", "https://example.com/hairplay1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        languagesList.add(new VideoItem("2", "Headshave", "https://example.com/headshave1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        languagesList.add(new VideoItem("3", "Haircut", "https://example.com/haircut1.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        languageAdapter.notifyDataSetChanged();
    }
}
