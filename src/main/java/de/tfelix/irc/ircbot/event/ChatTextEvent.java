package de.tfelix.irc.ircbot.event;

import java.util.Objects;

/**
 * Encapsulates chat text events written by users.
 * 
 * @author Thomas Felix <thomas.felix@tfelix.de>
 *
 */
public class ChatTextEvent extends Event {

	private final String text;
	private final String username;

	public ChatTextEvent(String username, String text) {
		this.text = Objects.requireNonNull(text, "Text can not be null.");
		this.username = Objects.requireNonNull(username);
	}

	public String getText() {
		return text;
	}

	/**
	 * Returns the username who created this chat event.
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

}
