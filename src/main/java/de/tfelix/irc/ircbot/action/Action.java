package de.tfelix.irc.ircbot.action;

import de.tfelix.irc.ircbot.chat.ChatApi;
import de.tfelix.irc.ircbot.chat.ChatBot;
import de.tfelix.irc.ircbot.event.Event;

/**
 * Actions consist of a trigger which will register itself via the
 * {@link ChatBot}. If such a trigger is executed the
 * {@link #performAction(Event)} will be executed.
 * 
 * @author Thomas Felix <thomas.felix@tfelix.de>
 *
 */
public interface Action {

	/**
	 * Performs the chat event.
	 * 
	 * @param event
	 */
	public void performAction(Event event, ChatApi chat);
}
