package it.adriano.tumino.gamepoint.backgroundprocesses.catchgame;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import it.adriano.tumino.gamepoint.backgroundprocesses.AsyncResponse;
import it.adriano.tumino.gamepoint.data.storegame.Game;
import it.adriano.tumino.gamepoint.data.storegame.PlayStationGame;
import it.adriano.tumino.gamepoint.utils.TaskRunner;
import it.adriano.tumino.gamepoint.utils.Utils;

public class CatchGameFromPSN extends TaskRunner<Void, Game> {
    public static final String TAG = "CatchGameFromPSN";

    private final String finalURL;

    public AsyncResponse<Game> delegate = null;

    public CatchGameFromPSN(String gameUrl) {
        finalURL = gameUrl;
    }

    @Override
    public Game doInBackground(Void... i) {
        String jsonText;
        try {
            jsonText = Utils.getJsonFromUrl(finalURL);
        } catch (IOException exception) {
            Log.e(TAG, exception.getMessage());
            return null;
        }

        if (jsonText.isEmpty()) {
            Log.e(TAG, "Nessun elemento prelevato dall'ulr: " + finalURL);
            return null;
        }

        try {
            return jsonParser(jsonText);
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
            return null;
        }
    }

    private PlayStationGame jsonParser(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        PlayStationGame game = new PlayStationGame();

        String titleGame = "N.A.";
        String releaseData = "N.A.";
        String description = "N.A.";
        String legalText = "";
        String rating = "0";
        String price = "N.A.";
        String numberOfPlayers = "N.A.";
        String inGamePurchases = "N.A.";
        String onlinePlayMode = "N.A.";
        String imageHeader = "https://images6.alphacoders.com/591/thumb-1920-591158.jpg";
        ArrayList<String> screenshotsUrl = new ArrayList<>();
        screenshotsUrl.add("https://wallpaperaccess.com/full/4419873.png");
        ArrayList<String> genres = new ArrayList<>();
        genres.add("N.A.");
        ArrayList<String> categories = new ArrayList<>();
        categories.add("N.A.");
        ArrayList<String> voiceLaunguage = new ArrayList<>();
        voiceLaunguage.add("N.A.");
        ArrayList<String> subtitleLanguage = new ArrayList<>();
        subtitleLanguage.add("N.A.");
        ArrayList<String> platforms = new ArrayList<>();
        platforms.add("N.A.");
        ArrayList<String> subGenreList = new ArrayList<>();

        if (jsonObject.has("name")) titleGame = jsonObject.getString("name");
        game.setTitle(titleGame);

        if (jsonObject.has("release_date")) releaseData = jsonObject.getString("release_date");
        game.setReleaseData(releaseData);

        if (jsonObject.has("long_desc")) description = jsonObject.getString("long_desc");

        if (jsonObject.has("legal_text")) legalText = jsonObject.getString("legal_text");
        if (!legalText.isEmpty()) description += "<br/>" + legalText + "<br/>";
        game.setDescription(description);

        if (jsonObject.has("images")) {
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tmp = jsonArray.getJSONObject(i);
                if (tmp.getInt("type") == 13) imageHeader = tmp.getString("url");
            }
        }
        game.setImageHeaderUrl(imageHeader);

        if (jsonObject.has("mediaList") && jsonObject.getJSONObject("mediaList").has("screenshots")) {
            screenshotsUrl.clear();
            JSONArray jsonArray = jsonObject.getJSONObject("mediaList").getJSONArray("screenshots");
            for (int i = 0; i < jsonArray.length(); i++) {
                screenshotsUrl.add(jsonArray.getJSONObject(i).getString("url"));
            }
        }
        game.setScreenshotsUrl(screenshotsUrl);

        if (jsonObject.has("content_rating"))
            rating = jsonObject.getJSONObject("content_rating").getString("description");
        game.setRating(rating);

        if (jsonObject.has("attributes") && jsonObject.getJSONObject("attributes").has("facets") && jsonObject.getJSONObject("attributes").getJSONObject("facets").has("genre")) {
            JSONArray jsonArray = jsonObject.getJSONObject("attributes").getJSONObject("facets").getJSONArray("genre"); //non c'è sempre sia attributes che genre
            genres.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                genres.add(jsonArray.getJSONObject(i).getString("name"));
            }
        }
        game.setGenres(genres);

        if (jsonObject.has("content_descriptors")) {
            JSONArray jsonArray = jsonObject.getJSONArray("content_descriptors");
            categories.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                categories.add(jsonArray.getJSONObject(i).getString("name"));
            }
        }
        game.setCategories(categories);

        //lingue
        if (jsonObject.has("skus")) {
            JSONObject skus = jsonObject.getJSONArray("skus").getJSONObject(0);

            if (skus.has("display_price")) price = skus.getString("display_price"); //this

            JSONObject lingue = skus.getJSONArray("entitlements").getJSONObject(0).getJSONObject("metadata");
            if (lingue.has("voiceLanguageCode")) {
                voiceLaunguage.clear();
                JSONArray jsonArray = lingue.getJSONArray("voiceLanguageCode"); //non sempre c'è
                for (int i = 0; i < jsonArray.length(); i++) {
                    voiceLaunguage.add(jsonArray.getString(i));
                }
            }

            if (lingue.has("subtitleLanguageCode")) {
                subtitleLanguage.clear();
                JSONArray jsonArray = lingue.getJSONArray("subtitleLanguageCode");
                for (int i = 0; i < jsonArray.length(); i++) {
                    subtitleLanguage.add(jsonArray.getString(i));
                }
            }
        }
        game.setVoiceLaunguage(voiceLaunguage);
        game.setSubtitleLanguage(subtitleLanguage);
        game.setPrice(price);

        //console supportate
        if (jsonObject.has("playable_platform")) {
            platforms.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("playable_platform");
            for (int i = 0; i < jsonArray.length(); i++) {
                platforms.add(jsonArray.getString(i));
            }
        }
        game.setPlatforms(platforms);

        if (jsonObject.has("metadata")) {
            JSONObject metadataObject = jsonObject.getJSONObject("metadata");
            if (metadataObject.has("game_subgenre")) {
                JSONArray tmp = metadataObject.getJSONObject("game_subgenre").getJSONArray("values");
                for (int i = 0; i < tmp.length(); i++) {
                    subGenreList.add(tmp.getString(i));
                }
            }

            if (metadataObject.has("cn_numberOfPlayers"))
                numberOfPlayers = metadataObject.getJSONObject("cn_numberOfPlayers").getJSONArray("values").getString(0);

            if (metadataObject.has("cn_inGamePurchases")) {
                JSONArray tmp = metadataObject.getJSONObject("cn_inGamePurchases").getJSONArray("values");
                if (!tmp.getString(0).equals("NOTREQUIRED"))
                    inGamePurchases = tmp.getString(0);
            }

            if (metadataObject.has("cn_onlinePlayMode"))
                onlinePlayMode = metadataObject.getJSONObject("cn_onlinePlayMode").getJSONArray("values").getString(0);

        }
        game.setSubGenreList(subGenreList);
        game.setNumberOfPlayers(numberOfPlayers);
        game.setInGamePurchases(inGamePurchases);
        game.setOnlinePlayMode(onlinePlayMode);

        return game;
    }

    @Override
    public void onPostExecute(Game output) {
        delegate.processFinish(output);
    }
}
