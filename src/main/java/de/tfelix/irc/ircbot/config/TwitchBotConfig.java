package de.tfelix.irc.ircbot.config;

import java.util.Objects;

import org.pircbotx.Configuration;
import org.pircbotx.cap.EnableCapHandler;

public class TwitchBotConfig implements BotConfig {

	private String twitchUsername;
	private String accessToken;

	public TwitchBotConfig(String username, String token) {
		this.twitchUsername = Objects.requireNonNull(username);
		this.accessToken = Objects.requireNonNull(token);
	}

	public Configuration.Builder getConfiguration() {
		// Configure what we want our bot to do
		final Configuration.Builder configuration = new Configuration.Builder()
				.setAutoNickChange(false)
				.setOnJoinWhoEnabled(false)
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
				.setLogin(twitchUsername)
				.setName(twitchUsername)
				.addServer("irc.twitch.tv", 6667)
				.setServerPassword(accessToken)
				.addAutoJoinChannel("#" + twitchUsername);

		return configuration;
	}

}
