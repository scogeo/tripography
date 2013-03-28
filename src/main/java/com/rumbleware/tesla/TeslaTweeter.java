package com.rumbleware.tesla;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * @author gscott
 */
public class TeslaTweeter {

    public TeslaTweeter() throws Exception {
        AccessToken token = new AccessToken("1166647171-bK1Pj7Atpl6VFt9brzIzxw0RELOtTURkr8A1AWD",
                "cQv0Tbis44ttMxnNg8IpOOexi3coYNtwqaimYCvKOg");

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("fsXkjevOLrPxZfyZCRlwwQ", "eYxl4Qwv7Jz39V96GnkUStc1SzY7ShcnFjY7xQ0Uc");
        twitter.setOAuthAccessToken(token);
        twitter.updateStatus("Test");

    }
}
