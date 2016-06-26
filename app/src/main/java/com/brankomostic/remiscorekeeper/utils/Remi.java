package com.brankomostic.remiscorekeeper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.brankomostic.remiscorekeeper.R;

import java.util.ArrayList;

public class Remi {
    private static SharedPreferences prefs;

    private static int pNum;
    private static String [] players;
    private static String [] data;
    private static int round;
    private static int game;
    private static boolean alternate;

    private static final String NUM_KEY = "num";
    private static final String GAME_KEY = "game";
    private static final String ROUND_KEY = "round";
    private static final String DATA_KEY = "data";
    private static final String ALTERNATE_KEY = "alternate";
    private static final String ACTIVE_KEY = "active";
    private static final String SCORECARD_KEY = "scorecard";
    private static final String DELIMITER = ",";

    public static final int MAX_PLAYERS = 7;


    @SuppressLint("applyPrefEdits")
    public static void instantiate(Context c) {
        PreferenceManager.setDefaultValues(c, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public static void clearAll() {
        pNum = 0;
        players = null;
        data = null;
        round = 0;
        game = 0;
        alternate = true;
    }

    public static void startNew(String [] p, boolean a) {
        clearAll();
        setGameInProgress(true);
        pNum = p.length;
        players = p;
        data = p;
        round = 1;
        game = 1;
        alternate = a;
    }

    public static void setGameData() {
        String temp;
        clearAll();
        pNum = prefs.getInt(NUM_KEY, 0);
        round = prefs.getInt(ROUND_KEY, 0);
        game = prefs.getInt(GAME_KEY, 0);
        alternate = prefs.getBoolean(ALTERNATE_KEY, true);

        temp = prefs.getString(DATA_KEY, null);
        if(temp != null) {
            players = new String[pNum];
            data = temp.split(DELIMITER);
            System.arraycopy(data, 0, players, 0, pNum);
        }
    }

    public static void saveGameData() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(NUM_KEY, pNum);
        editor.putInt(ROUND_KEY, round);
        editor.putInt(GAME_KEY, game);
        editor.putBoolean(ALTERNATE_KEY, alternate);

        if(data != null) {
            String results = data[0];
            for (int i = 1; i < data.length; i++) {
                results = results + DELIMITER + data[i];
            }
            editor.putString(DATA_KEY, results);
        } else {
            editor.putString(DATA_KEY, "");
        }

        editor.apply();
        clearAll();
    }

    public static void addGame(String [] add) {
        if(round == pNum && game == pNum) {
            prefs.edit().putBoolean(SCORECARD_KEY, true).apply();
        } else if(game == pNum) {
            game = 1;
            round++;
        } else {
            game++;
        }
        addScore(add);
    }

    private static void addScore(String[] s) {
        String [] temp = data;
        data = new String[temp.length + s.length];
        
        System.arraycopy(temp, 0, data, 0, temp.length);
        System.arraycopy(s, 0, data, temp.length, s.length);
    }

    public static String[] getLatestScores() {
        String [] result;
        if(data.length > pNum) {
            result = new String[pNum];
            System.arraycopy(data, data.length - pNum, result, 0, pNum);
        } else {
            result = null;
        }
        return result;
    }

    public static String getWinner() {
        ArrayList<String> winners = new ArrayList<>();
        String [] last;
        int smallest;
        String result;

        last = getLatestScores();
        winners.add(players[0]);
        smallest = Integer.parseInt(last[0]);
        for(int i = 1; i < pNum; i++) {
            int hold = Integer.parseInt(last[i]);
            if(hold < smallest) {
                smallest = hold;
                winners.clear();
                winners.add(players[i]);
            } else if(hold == smallest) {
                winners.add(players[i]);
            }
        }

        result = winners.get(0);

        for(int i = 1; i < winners.size(); i++) {
            if(i == (winners.size() - 1)) {
                result = result.concat(" & " + players[i]);
            } else {
                result = result.concat(", " + players[i]);
            }
        }

        return result;
    }

    public static void setGameInProgress(boolean active) {
        prefs.edit().putBoolean(ACTIVE_KEY, active).apply();
    }

    public static boolean gameInProgress() {
        return prefs.getBoolean(ACTIVE_KEY, false);
    }

    public static boolean showScorecard() {
        return prefs.getBoolean(SCORECARD_KEY, false);
    }

    public static void resetScorecard() {
        prefs.edit().putBoolean(SCORECARD_KEY, false).apply();
    }

    public static int getPlayerNum() {return pNum;}

    public static int getRound() {return round;}

    public static int getGame() {return game;}

    public static String[] getPlayers() {return players;}

    public static String[] getGridData() {return data;}

    public static int getGamesDone() {
        return (round - 1) * (pNum) + (game - 1);
    }

    public static boolean shouldAlternate() {return alternate;}
}
