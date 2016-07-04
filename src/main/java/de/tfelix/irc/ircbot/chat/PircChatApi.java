package de.tfelix.irc.ircbot.chat;

import java.util.Objects;

import org.pircbotx.PircBotX;

public class PircChatApi implements ChatApi {
	
	private final PircBotX pirc;
	private final String channel;
	
	public PircChatApi(String channel, PircBotX pirc) {
		this.pirc = Objects.requireNonNull(pirc);
		this.channel = Objects.requireNonNull(channel);
	}

	@Override
	public void sendPublicMsg(String text) {
		pirc.send().message(channel, text);
	}

	@Override
	public void sendPrivateMsg(String username, String text) {
		pirc.send().message(username, text);
	}

}
