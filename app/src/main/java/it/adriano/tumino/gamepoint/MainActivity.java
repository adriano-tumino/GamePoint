package it.adriano.tumino.gamepoint;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import it.adriano.tumino.gamepoint.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SharedViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Initialization Main activity and Bottom Navigation View");

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_news, R.id.navigation_search, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        viewModel = null;
    }
}