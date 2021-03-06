package it.adriano.tumino.gamepoint.processes.handler;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.adriano.tumino.gamepoint.data.BasicGameInformation;
import it.adriano.tumino.gamepoint.data.GameOffers;
import it.adriano.tumino.gamepoint.data.storegame.StoreGame;
import it.adriano.tumino.gamepoint.processes.AsyncResponse;
import it.adriano.tumino.gamepoint.processes.ProcessUtils;
import it.adriano.tumino.gamepoint.processes.catchgame.CatchPlayStationGame;
import it.adriano.tumino.gamepoint.utils.Utils;

public class PlayStationHandler {
    private static final String TAG = "PlayStationHandler";

    private final static String ITALIAN = "IT/it";
    private final static String ENGLISH = "GB/en";

    private final static String PSN_SEARCH_OFFERS_URL = "https://store.playstation.com/store/api/chihiro/00_09_000/container/GB/en/999/STORE-MSF77008-ALLDEALS";
    private final static String FIRST_PSN_URL = "https://store.playstation.com/store/api/chihiro/00_09_000/tumbler/";
    private final static String SECOND_PSN_URL = "/999/";
    private final static String THIRD_PSN_URL = "?suggested_size=100&mode=game";

    public static String generatePlayStationUrl(String title) {
        String language = ENGLISH;
        if (Locale.getDefault().getLanguage().equals("it")) language = ITALIAN;
        return FIRST_PSN_URL + language + SECOND_PSN_URL + title + THIRD_PSN_URL;
    }

    public static List<BasicGameInformation> playStationGames(@NonNull String title) {
        title = title.toLowerCase();
        List<BasicGameInformation> result = new ArrayList<>();
        String titleEncoded = ProcessUtils.encodedTitle(title);
        if (titleEncoded.isEmpty()) return result;

        String finalUrl = generatePlayStationUrl(titleEncoded);
        String json = Utils.getJsonFromUrl(finalUrl);
        if (json.isEmpty()) return result;

        try {
            JSONObject jsonObject = new JSONObject(json);
            int size = jsonObject.optInt("size");

            if (size == 0) {
                Log.i(TAG, "No games found");
                return result;
            }

            if (jsonObject.has("links")) {
                JSONArray links = jsonObject.optJSONArray("links");
                if (links == null || links.length() == 0) {
                    Log.i(TAG, "No games found");
                    return result;
                }

                for (int j = 0; j < links.length(); j++) {
                    JSONObject gameInformation = links.getJSONObject(j);
                    if (gameInformation == null || gameInformation.length() == 0) continue;
                    if (gameInformation.has("top_category") && gameInformation.getString("top_category").equals("theme"))
                        continue;

                    String titleGame = gameInformation.getString("name");

                    if (ProcessUtils.deleteSpecialCharacter(titleGame).toLowerCase().contains(title)) {

                        String imageURL = "https://upload.wikimedia.org/wikipedia/it/thumb/4/4e/Playstation_logo_colour.svg/1200px-Playstation_logo_colour.svg.png";
                        if (gameInformation.has("images"))
                            imageURL = gameInformation.getJSONArray("images").getJSONObject(0).getString("url");

                        String console = "N.A.";
                        if (gameInformation.has("playable_platform"))
                            console = gameInformation.getJSONArray("playable_platform").join(", ").replaceAll("\"", "");

                        String gameURL;
                        if (gameInformation.has("url")) {
                            gameURL = gameInformation.getString("url");
                        } else {
                            continue;
                        }

                        String id = "";
                        if (gameInformation.has("id")) id = gameInformation.getString("id");

                        String price = "N.A.";
                        if (gameInformation.has("default_sku")) {
                            price = gameInformation.getJSONObject("default_sku").getString("display_price").substring(1);
                        }

                        BasicGameInformation basicGameInformation = new BasicGameInformation(titleGame, imageURL, gameURL, id, console, "PSN", price);
                        result.add(basicGameInformation);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static List<GameOffers> playStationOffers() {
        List<GameOffers> gameOffersList = new ArrayList<>();
        String json = Utils.getJsonFromUrl(PSN_SEARCH_OFFERS_URL);
        if (json.isEmpty()) return gameOffersList;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int size = jsonObject.optInt("size");

            if (size == 0) {
                Log.i(TAG, "No games found");
                return gameOffersList;
            }

            if (jsonObject.has("links")) {
                JSONArray links = jsonObject.optJSONArray("links");
                if (links == null || links.length() == 0) {
                    Log.i(TAG, "No games found");
                    return gameOffersList;
                }

                for (int j = 0; j < links.length(); j++) {
                    JSONObject gameInformation = links.getJSONObject(j);
                    if (gameInformation == null || gameInformation.length() == 0) continue;
                    if (gameInformation.has("top_category") && gameInformation.getString("top_category").equals("theme"))
                        continue;

                    String titleGame = gameInformation.getString("name");
                    String imageURL = "https://upload.wikimedia.org/wikipedia/it/thumb/4/4e/Playstation_logo_colour.svg/1200px-Playstation_logo_colour.svg.png";
                    if (gameInformation.has("images"))
                        imageURL = gameInformation.getJSONArray("images").getJSONObject(0).getString("url");

                    String console = "N.A.";
                    if (gameInformation.has("playable_platform"))
                        console = gameInformation.getJSONArray("playable_platform").join(", ").replaceAll("\"", "");

                    String gameURL;

                    if (gameInformation.has("url")) {
                        gameURL = gameInformation.getString("url");
                    } else {
                        continue;
                    }
                    String id = "";
                    if (gameInformation.has("id")) id = gameInformation.getString("id");

                    String price = "N.A.";
                    if (gameInformation.has("default_sku")) {
                        price = gameInformation.getJSONObject("default_sku").getString("display_price");
                    }

                    GameOffers gameOffers = new GameOffers(titleGame, imageURL, gameURL, id, console, "PSN", price, "", "");
                    gameOffersList.add(gameOffers);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }


        return gameOffersList;

    }

    /*Method to catch PlayStation's game*/
    public static void catchGame(String url, AsyncResponse<StoreGame> delegate) {
        if(Locale.getDefault().getLanguage().equals("it")){
            if(url.contains(ENGLISH)) url = url.replace(ENGLISH, ITALIAN);
        }else{
            if(url.contains(ITALIAN)) url = url.replace(ITALIAN, ENGLISH);
        }
        CatchPlayStationGame catchPlayStationGame = new CatchPlayStationGame(url);
        catchPlayStationGame.delegate = delegate;
        catchPlayStationGame.execute();
    }
}
