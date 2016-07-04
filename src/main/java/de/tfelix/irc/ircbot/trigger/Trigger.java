package de.tfelix.irc.ircbot.trigger;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tfelix.irc.ircbot.action.Action;
import de.tfelix.irc.ircbot.chat.ChatBot;
import de.tfelix.irc.ircbot.event.Event;

public abstract class Trigger {

	private static final Logger LOG = LogManager.getLogger(Trigger.class);

	private final Action action;
	private ChatBot bot;

	public Trigger(Action action) {
		this.action = Objects.requireNonNull(action);
	}

	protected abstract boolean isTriggered(Event event);

	/**
	 * This method will be called if one subscription defined in
	 * {@link #hook(ChatBot)} fires. The received event is checked if it
	 * triggers the action.
	 * 
	 * @param event
	 *            The event of the hooked event type.
	 */
	public void onEvent(Event event) {
		if (isTriggered(event)) {
			if (bot.getChatApi() == null) {
				LOG.warn("Trigger was executed but no chat API registered.");
				return;
			}
			action.performAction(event, bot.getChatApi());
		}
	}

	/**
	 * Is called from the chat bot if a trigger is registered.
	 * 
	 * @param chatBot
	 */
	public void installHook(ChatBot chatBot) {
		this.bot = chatBot;
		installSubscriptions(chatBot);
	}
	
	protected abstract void installSubscriptions(ChatBot chatBot);
}
