package com.pedropadilha;

import java.util.List;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Class that contains methods to manage Twitter Account
 *
 * @author pedropadilha
 */
public final class Account {

    String consumerKey;
    String consumerSecret;
    String accessToken;
    String accessSecret;
    ConfigurationBuilder cb;
    Twitter twitter;

    /**
     * Constructs new Account Object using the parameters specified
     *
     * @param consumerKey
     * @param consumerSecret
     * @param accessToken
     * @param accessSecret
     * @throws twitter4j.TwitterException
     */
    public Account(String consumerKey, String consumerSecret, String accessToken, String accessSecret) throws TwitterException {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        cb = getConfigurationBuilder(consumerKey, consumerSecret, accessToken, accessSecret);
        twitter = new TwitterFactory(cb.build()).getInstance();
    }

    ConfigurationBuilder getConfigurationBuilder(String consumerKey, String consumerSecret, String accessToken, String accessSecret) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);
        return configurationBuilder;
    }

    /**
     * Method used to tweet new status
     *
     * @param newStatus status to be tweeted
     */
    public void updateStatus(String newStatus) {
        try {
            System.out.println("Tweeting from @" + twitter.getScreenName() + "'s account");
            Status status = twitter.updateStatus(newStatus);
            System.out.println("Successfully updated the status to [" + status.getText() + "].");
        } catch (TwitterException te) {
            te.printStackTrace();
            System.exit(-1);
        }
    }

    public List<Status> getTimeline() {
        List<Status> statuses = null;
        try {
            statuses = twitter.getUserTimeline();
        } catch (TwitterException te) {
            te.printStackTrace();
        }
        return statuses;
    }
    
    public Status getLastTweet() {
       return getTimeline().get(0); 
    }

    public void showTimeline() {
        List<Status> statuses = getTimeline();
        System.out.println("Showing timeline:");
        int i = 0;
        for (Status status : statuses) {
            System.out.println(i++ + status.getUser().getName() + ": " + status.getText());
        }
    }

    public int getTweetCount() throws TwitterException {
        return twitter.verifyCredentials().getStatusesCount();
    }

}
