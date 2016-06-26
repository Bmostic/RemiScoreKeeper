package com.brankomostic.remiscorekeeper.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.brankomostic.remiscorekeeper.GameFragment;
import com.brankomostic.remiscorekeeper.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogFactory {

    private static String [] temp;
    private static String [] players;
    private static Dialog display;

    public static void startNewGame(Context context) {
        if(display != null) {
            display.dismiss();
            display = null;
        }
        if(Remi.gameInProgress()) {
            newGameConfirmation(context).show();
        } else {
            numOfPlayers(context).show();
        }
    }

    public static void startNextRound(Context context) {
        if(display != null) {
            display.dismiss();
            display = null;
        }
        addScore(context, 0).show();
    }

    public static void showWinner(Context context) {
        if(display != null) {
            display.dismiss();
            display = null;
        }
        winnerDialog(context).show();
    }

    public static void clearAll() {
        temp = null;
        players = null;
    }

    private static Dialog numOfPlayers(final Context context) {
        String [] array = new String [Remi.MAX_PLAYERS - 1];
        for(int i = 1; i < Remi.MAX_PLAYERS; i++) {array[i-1] = "" + (i+1);}
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set the Number of Players")
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        players = new String[which + 2];
                        newPlayer(context, 0).show();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        display = null;
                    }
                })
                .setCancelable(true);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog newPlayer(final Context context, final int current) {
        String text;

        if(current == 0) {
            text = "Enter the player who will deal first";
        }
        else {
            text = "Enter the player to the left of " + players[current-1];
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Player " + (current+1) + " Name");
        alert.setMessage(text);

        // Set an EditText view to get user input
        final EditText input = new NameEditText(context);
        alert.setView(input);

        alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.isEmpty()) {
                    Toast.makeText(context, R.string.name_error, Toast.LENGTH_SHORT).show();
                    newPlayer(context, current).show();
                } else {
                    players[current] = value.trim();
                    if (current + 1 == players.length) {
                        confirmationOfPlayersList(context).show();
                    } else {
                        newPlayer(context, current + 1).show();
                    }
                }
            }
        });

        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (current > 0) {
                    newPlayer(context, current - 1).show();
                } else {
                    numOfPlayers(context).show();
                }
            }
        });

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                display = null;
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog confirmationOfPlayersList(final Context context) {
        String message;
        String lineBreak = "\n";
        message = "Going clockwise starting from the first dealer:" + lineBreak;
        for (String name : players) {
            message = message.concat(name + lineBreak);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Is This Correct?")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alternateDialog(context).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        newPlayer(context, players.length - 1).show();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        display = null;
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog newGameConfirmation(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Start New Game?")
                .setMessage("Your previous game will be overwritten!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Remi.setGameInProgress(false);
                        Remi.resetScorecard();
                        Remi.clearAll();
                        GameFragment.setVisibility(false);
                        numOfPlayers(context).show();
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        display = null;
                    }
                });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog addScore(final Context context, final int current) {

        if(current == 0) {
            players = Remi.getPlayers();
            temp = new String [players.length];
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(players[current]);
        alert.setMessage("Enter score");

        // Set an EditText view to get user input
        final EditText input = new ScoreEditText(context);
        alert.setView(input);

        alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (isValid(value)) {
                    temp[current] = value.replace("+", "");

                    if (current + 1 == players.length) {
                        confirmScores(context).show();
                    } else {
                        addScore(context, current + 1).show();
                    }
                } else {
                    Toast.makeText(context, R.string.score_error, Toast.LENGTH_SHORT).show();
                    addScore(context, current).show();
                }
            }
        });

        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (current > 0) {
                    addScore(context, current - 1).show();
                } else {
                    dialog.dismiss();
                    temp = null;
                }
            }
        });

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                display = null;
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog confirmScores(final Context context) {
        String message = "";
        final String lineBreak = "\n";

        for(int i = 0; i < players.length; i++) {
            message = message.concat(players[i] +  ": " + temp[i] + lineBreak);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Is This Correct?")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String[] result;
                        if (Remi.getGamesDone() > 0) {
                            String[] previous = Remi.getLatestScores();

                            result = new String[players.length];

                            for (int i = 0; i < temp.length; i++) {
                                int x = Integer.parseInt(previous[i]);
                                int y = Integer.parseInt(temp[i]);
                                result[i] = Integer.toString(x + y);
                            }
                        } else {
                            result = temp;
                        }

                        Remi.addGame(result);
                        GameFragment.init();
                        temp = null;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        addScore(context, players.length - 1).show();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        display = null;
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog winnerDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Finished!")
                .setMessage(Remi.getWinner() + " won the game.")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        display = null;
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static Dialog alternateDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alternate Directions?")
                .setMessage("Alternate the direction of play every round.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Remi.startNew(players, true);
                        GameFragment.init();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Remi.startNew(players, false);
                        GameFragment.init();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        display = null;
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        display = dialog;

        return dialog;
    }

    private static boolean isValid(String value) {
        value = value.replace("+","");
        final Pattern pattern = Pattern.compile("^[\\d-]+$");
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private static class NameEditText extends EditText {
        public NameEditText(Context context) {
            super(context);
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
    }

    private static class ScoreEditText extends EditText {
        public ScoreEditText(Context context) {
            super(context);
            setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }
    }
}
