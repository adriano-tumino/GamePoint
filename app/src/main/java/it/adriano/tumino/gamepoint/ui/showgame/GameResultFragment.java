package it.adriano.tumino.gamepoint.ui.showgame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ImageWriter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import it.adriano.tumino.gamepoint.R;
import it.adriano.tumino.gamepoint.backgroundprocesses.AsyncResponse;
import it.adriano.tumino.gamepoint.backgroundprocesses.CatchGame;
import it.adriano.tumino.gamepoint.backgroundprocesses.catchgame.CatchGameFromSteam;
import it.adriano.tumino.gamepoint.data.Game;
import it.adriano.tumino.gamepoint.data.GameSearchResult;
import it.adriano.tumino.gamepoint.data.GameShow;
import it.adriano.tumino.gamepoint.utils.TaskRunner;
import it.adriano.tumino.gamepoint.utils.Utils;

public class GameResultFragment extends Fragment implements AsyncResponse<Game>, TabLayout.OnTabSelectedListener {
    private GameSearchResult gameSearchResult;
    private static final String BASE_TEXT = "Garda che bel gioco ho trovato: ";

    private final Fragment[] fragments = new Fragment[4];

    private Bundle information = new Bundle();


    public GameResultFragment() {
        fragments[0] = new DescriptionFragment();
        fragments[1] = new GalleryFragment();
        fragments[2] = new GameSpecificationsFragment();
        fragments[3] = new GameCommentsFragment();

    }

    private TaskRunner<Integer, String> game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("game"))
            gameSearchResult = getArguments().getParcelable("game");

        information.putString("store", gameSearchResult.getStore());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(gameSearchResult.getTitle().toUpperCase()); //Setto il titolo del fragment
        switch (gameSearchResult.getStore()) {
            case "STEAM":
                game = new CatchGameFromSteam(gameSearchResult.getAppID());
                ((CatchGameFromSteam) game).delegate = this;
                break;
            case "MCS":
                game = new CatchGame();
                //((CatchGame) game).delegate = this;
                break;
            case "PSN":
                game = new CatchGame();
                //((CatchGame) game).delegate = this;
                break;
            case "ESHOP":
                game = new CatchGame();
                //((CatchGame) game).delegate = this;
                break;
            default:
                //nessuno di questo
                break;
        }
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_result, container, false);

        //if (gameSearchResult == null) Navigation.findNavController(view).navigate(R.id.no_game_action); //doesn't works, idk why
        game.execute(0);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout); //setto le impostazioni per il tabLayout
        tabLayout.addOnTabSelectedListener(this); //imposto il listener

        return view;
    }

    private void setFragmentLayout(Fragment fragment) { //imposto che cosa mostrare nel tab layout
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager(); //avvio la tansizione per cosa inserire
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView logo = view.findViewById(R.id.storeIconView);
        Drawable d;
        try {
            d = Drawable.createFromStream(getContext().getAssets().open("logo_steam.png"), null);
        } catch (IOException exception) {
            d = new ColorDrawable(Color.CYAN);
        }
        logo.setImageDrawable(d);

        ImageButton sharedButton = view.findViewById(R.id.shareButton); //setto il bottone condividi
        sharedButton.setOnClickListener(v -> { //listener per la condivisione
            shareButton("", "TESTO");
        });
    }

    private void shareButton(String url, String text) {
        Picasso.get().load(url).into(new Target() { //carico l'immagine in cache
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND); //Inizializzo Intent da fare
                i.setType("image/*"); //setto il tipo del contenuto
                i.putExtra(Intent.EXTRA_STREAM, Utils.getLocalBitmapUri(getContext(), bitmap)); //Inserisco l'immagine
                i.putExtra(Intent.EXTRA_TEXT, BASE_TEXT + text); //Inserisco il testo per l'immagine
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //Uso i permessi
                startActivity(Intent.createChooser(i, "Share Image")); //Inizio l'activity
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) { //Nel caso non riesco a prelevare l'immagine
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { //per prelevare l'immagine
            }
        });
    }

    private Game result;

    @Override
    public void processFinish(Game result) {
        this.result = result;
        //aggiorno l'interfaccia grafica
        if (result == null) Navigation.findNavController(view).navigate(R.id.no_game_action);

        information.putParcelable("game", result);
        for (int i = 0; i < fragments.length; i++) fragments[i].setArguments(information);

        setFragmentLayout(fragments[0]); //imposto il layout da visualizzare

        ImageView imageView = view.findViewById(R.id.gameHeaderImageView);

        Picasso.get().load(result.getImage())
                .resize(imageView.getMaxWidth(), 700)
                .centerInside()
                .into(imageView);
        ((TextView) view.findViewById(R.id.titleGameTextView)).setText(result.getName());
        ((TextView) view.findViewById(R.id.priceGameTextView)).setText(result.getPrice());
        ((TextView) view.findViewById(R.id.releaseDataTextView)).setText(result.getDate());


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) { //Listener per capire che Layout mostrare
        setFragmentLayout(fragments[tab.getPosition()]); //imposto il frammento
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


}