package com.brankomostic.remiscorekeeper;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brankomostic.remiscorekeeper.utils.DialogFactory;
import com.brankomostic.remiscorekeeper.utils.GridAdapter;
import com.brankomostic.remiscorekeeper.utils.Remi;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {

    static Context context;
    static int round;
    static int game;
    static int pNum;
    static String [] players;

    static TextView round_tv;
    static TextView game_tv;
    static TextView dealer_tv;
    static TextView cutter_tv;
    static TextView first_tv;
    static RecyclerView score;
    static View divider;

    static MenuItem addIcon;

    public GameFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_game, container, false);

        round_tv = (TextView) rootView.findViewById(R.id.round);
        game_tv = (TextView) rootView.findViewById(R.id.game);
        dealer_tv = (TextView) rootView.findViewById(R.id.dealer);
        cutter_tv = (TextView) rootView.findViewById(R.id.cut);
        first_tv = (TextView) rootView.findViewById(R.id.first);
        score = (RecyclerView) rootView.findViewById(R.id.grid);
        divider = rootView.findViewById(R.id.divider);

        if(Remi.gameInProgress()) {
            Remi.setGameData();
            init();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DialogFactory.clearAll();
        if(Remi.gameInProgress()) {
            Remi.setGameData();
        } else {
            setVisibility(false);
            DialogFactory.startNewGame(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Remi.saveGameData();
        DialogFactory.clearAll();
    }

    public static void init() {
        round = Remi.getRound();
        game = Remi.getGame();
        pNum = Remi.getPlayerNum();
        players = Remi.getPlayers();

        setGrid();
        Bundle data = gameSetup();
        round_tv.setText("Round " + round);
        game_tv.setText("Game " + game);
        dealer_tv.setText(data.getString("dealer") + " Deals");
        cutter_tv.setText(data.getString("cutter") + " Cuts the Deck");
        first_tv.setText(data.getString("first") + " Goes First");

        setVisibility(true);

        if(Remi.showScorecard()) {
            DialogFactory.showWinner(context);
        }
    }

    public static void setVisibility(boolean show) {
        int visible = show ? View.VISIBLE : View.INVISIBLE;
        round_tv.setVisibility(visible);
        game_tv.setVisibility(visible);
        dealer_tv.setVisibility(visible);
        cutter_tv.setVisibility(visible);
        first_tv.setVisibility(visible);
        score.setVisibility(visible);
        iconVisibility();

        divider.setVisibility(visible);
    }

    private static Bundle gameSetup() {
        String dealer;
        String cutter;
        String first;
        int deal;
        Bundle player_data = new Bundle();
        boolean odd = (round % 2 != 0);

        if(odd || !Remi.shouldAlternate()) {
            deal = game-1;
            dealer = players[deal];

            if(deal == 0) {
                cutter = players[pNum-1];
            }
            else {
                cutter = players[deal-1];
            }

            if(deal == pNum-1) {
                first = players[0];
            }
            else {
                first = players[deal+1];
            }
        }
        else {
            if(game == 1) {
                deal = 0;
            } else {
                deal = pNum - game + 1;
            }

            dealer = players[deal];

            if(deal == pNum-1) {
                cutter = players[0];
            }
            else {
                cutter = players[deal+1];
            }

            if(deal == 0) {
                first = players[pNum-1];
            }
            else {
                first = players[deal-1];
            }
        }

        player_data.putString("dealer", dealer);
        player_data.putString("cutter", cutter);
        player_data.putString("first", first);
        return player_data;
    }

    private static void setGrid() {
        String [] data = Remi.getGridData();
        if(data != null && data.length > 0) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(context, pNum);
            score.setLayoutManager(manager);
            GridAdapter gAdapter = new GridAdapter(data);
            score.setAdapter(gAdapter);
        } else {
            DialogFactory.startNewGame(context);
        }
    }

    public static void setAddIcon(MenuItem menuItem) {
        addIcon = menuItem;
        iconVisibility();
    }

    private static void iconVisibility() {
        if(addIcon != null) {
            addIcon.setVisible(!Remi.showScorecard() && Remi.gameInProgress());
        }
    }
}
