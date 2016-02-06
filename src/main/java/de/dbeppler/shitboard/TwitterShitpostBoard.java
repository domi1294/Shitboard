package de.dbeppler.shitboard;

/**
 * @author Dominik Beppler
 * @since 04.02.2016
 */
public class TwitterShitpostBoard {
	public static void main(String[] args) throws Exception {
		UserInterface.launch(UserInterface.class, args);
		/*Twitter twitter = new TwitterFactory().getInstance();
		Status status = twitter.updateStatus("Test Tweet (sollte sich in 5 Sekunden automatisch löschen)");
		Thread.sleep(5000);
		twitter.destroyStatus(status.getId());
		/*StatusUpdate pepe1 = new StatusUpdate("Via Twitter Shitposting Board");
		pepe1.setMedia(new File("E:\\Domi\\Pictures\\Memes\\Pepes\\0790 - TMZndT1.jpg"));
		StatusUpdate pepe2 = new StatusUpdate("Via Twitter Shitposting Board");
		pepe2.setMedia(new File("E:\\Domi\\Pictures\\Memes\\Pepes\\0709 - uMsMO2R.jpg"));
		StatusUpdate pepe3 = new StatusUpdate("Via Twitter Shitposting Board");
		pepe3.setMedia(new File("E:\\Domi\\Pictures\\Memes\\Pepes\\0691 - SF8tSoO.jpg"));
		twitter.updateStatus(pepe1);
		twitter.updateStatus(pepe2);
		twitter.updateStatus(pepe3);*/
		/*
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthenticationURL());
			System.out.println("Enter the PIN (if available) or just hit enter. [PIN]:");
			String pin = br.readLine();
			try {
				//if (pin.length() > 0) {
				//	accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				//} else {
					accessToken = twitter.getOAuthAccessToken();
				//}
			} catch (TwitterException e) {
				if (401 == e.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					e.printStackTrace();
				}
			}
		}*/
	}
}
