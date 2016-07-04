package de.tfelix.irc.ircbot.chat;

public interface ChatApi {

	/**
	 * Sends a public message to the whole channel.
	 * 
	 * @param text
	 */
	public void sendPublicMsg(String text);

	/**
	 * Sends a private message to the given user.
	 * 
	 * @param username
	 * @param text
	 */
	public void sendPrivateMsg(String username, String text);

}
