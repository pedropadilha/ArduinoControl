package com.pedropadilha;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import twitter4j.Status;
import twitter4j.TwitterException;

public final class ArduinoControl {

    String command;
    static SerialComm arduino;
    static Account pedropadilha13;

    static final String[] KEYS = setKeysFromFile();
    static final String CONSUMER_KEY = KEYS[0];
    static final String CONSUMER_SECRET = KEYS[1];
    static final String ACCESS_TOKEN = KEYS[2];
    static final String ACCESS_SECRET = KEYS[3];

    public ArduinoControl(String title) throws IOException, TwitterException {

        pedropadilha13 = new Account(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_SECRET);

        try {
            arduino = new SerialComm("COM4");
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        montaJFrame(title);
    }

    public void montaJFrame(String title) throws TwitterException {
        JFrame arduinoControl = new JFrame(title);
        arduinoControl.setSize(250, 300);
        arduinoControl.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                //pedropadilha13.updateStatus("#ArduinoControl exiting. Goodbye!");
                JOptionPane.showMessageDialog(arduinoControl, "Exiting ArduinoControl. Goodbye!");
            }
        });
        arduinoControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        arduinoControl.setLayout(new GridLayout(5, 1));

        JPanel top = new JPanel();
        JLabel label = new JLabel("Arduino Control");
        top.add(label);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER));

        JToggleButton red = new JToggleButton("RED");
        red.setMnemonic(KeyEvent.VK_R);
        red.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("red");
            }
        });
        buttons.add(red);

        JToggleButton yellow = new JToggleButton("YELLOW");
        yellow.setMnemonic(KeyEvent.VK_Y);
        yellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("yellow");
            }
        });
        buttons.add(yellow);

        JToggleButton green = new JToggleButton("GREEN");
        green.setMnemonic(KeyEvent.VK_G);
        green.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("green");
            }
        });
        buttons.add(green);

        JPanel values = new JPanel();
        values.setLayout(new GridLayout(3, 2));

        JButton getDistance = new JButton("Distance");
        getDistance.setMnemonic(KeyEvent.VK_D);
        JTextField distance = new JTextField();
        distance.setEditable(false);
        JButton getLight = new JButton("Light");
        getLight.setMnemonic(KeyEvent.VK_L);
        JTextField light = new JTextField();
        light.setEditable(false);
        JButton getPirState = new JButton("PIR");
        getPirState.setMnemonic(KeyEvent.VK_P);
        JTextField pirState = new JTextField();
        pirState.setEditable(false);

        getDistance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("ping");
                distance.setText(arduino.read());
            }
        });
        getLight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("light");
                light.setText(arduino.read());
            }
        });
        getPirState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("pir");
                pirState.setText(arduino.read());
            }
        });

        values.add(getDistance);
        values.add(distance);
        values.add(getLight);
        values.add(light);
        values.add(getPirState);
        values.add(pirState);

        JPanel relayPanel = new JPanel();
        relayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JToggleButton relay = new JToggleButton("Toggle relay");
        relay.setMnemonic(KeyEvent.VK_T);
        relay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                arduino.write("relay");
            }
        });
        relayPanel.add(relay);

        JPanel twitter = new JPanel();
        twitter.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton tweet = new JButton("New Tweet!");
        tweet.setMnemonic(KeyEvent.VK_N);
        tweet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tweet();
            }
        });
        twitter.add(tweet);

        arduinoControl.add(top);
        arduinoControl.add(buttons);
        arduinoControl.add(values);
        arduinoControl.add(relayPanel);
        arduinoControl.add(twitter);

        arduinoControl.setLocationRelativeTo(null);
        arduinoControl.setResizable(false);
        arduinoControl.setVisible(true);
        Status lastTweet = pedropadilha13.getLastTweet();
        Status latestTweet = lastTweet;
        boolean firstTime = true;
        while (true) {
            if (!latestTweet.equals(lastTweet) || firstTime) {
                System.out.println("Got new tweet!");
                List<Status> statuses = pedropadilha13.getTimeline();
                Status lastestTweet = statuses.get(0);
                String lastTweetText = lastestTweet.getText();
                System.out.println("Tweet is: " + lastTweetText);
                String[] words = lastTweetText.split(" ");
                for (int i = 0; i < words.length; i++) {
                    if (words[i].equals("#ArduinoControl")) {
                        String command = words[i + 1];
                        switch (command) {
                            case "red":
                                red.doClick();
                                break;
                            case "yellow":
                                yellow.doClick();
                                break;
                            case "green":
                                green.doClick();
                                break;
                            case "relay":
                                relay.doClick();
                                break;
                            case "pir":
                                getPirState.doClick();
                                break;
                            case "ping":
                                getDistance.doClick();
                                break;
                            case "light":
                                getLight.doClick();
                                break;
                            case "exiting.":
                                System.out.println("Last tweet was closing tweet. Doing nothing...");
                                break;
                            default:
                                pedropadilha13.updateStatus("ArduinoControl syntax error: \"" + command + "\" is not a valid command.");
                        }
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interruptedException) {

                }
                firstTime = false;
            }
            lastTweet = latestTweet;
            latestTweet = pedropadilha13.getTimeline().get(0);
        }

    }

    public static void main(String[] args) throws IOException, TwitterException {
        ArduinoControl arduinoControl = new ArduinoControl("Arduino Control");
    }

    private static String getNewTweet() {
        String newTweet = JOptionPane.showInputDialog("What would you like to Tweet?");
        return newTweet;
    }

    public static String[] setKeysFromFile() {
        String[] keys = null;
        try {
            String fileName = "D:\\Documents\\NetBeansProjects\\ArduinoControl\\src\\com\\pedropadilha\\keys";
            FileInputStream fileInputStream = new FileInputStream(fileName);
            String reading = "";
            int lastReading = fileInputStream.read();
            while (lastReading != -1) {
                reading += (char) lastReading;
                lastReading = fileInputStream.read();
            }
            keys = reading.split(" ");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    public static String[] setKeysFromFile(String path) {

        String[] keys = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            String reading = "";
            int lastReading = fileInputStream.read();
            while (lastReading != -1) {
                reading += (char) lastReading;
                lastReading = fileInputStream.read();
            }
            keys = reading.split(" ");
            System.err.println(keys.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    public static void tweet() {
        String newTweet = getNewTweet();
        if (newTweet != null && !newTweet.equals("")) {
            pedropadilha13.updateStatus(newTweet);
        } else {
            JOptionPane.showMessageDialog(null, "Cannot tweet nothing!");
        }

    }

}
