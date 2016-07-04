package de.tfelix.irc.ircbot.trigger;

import java.util.Objects;

import de.tfelix.irc.ircbot.action.Action;
import de.tfelix.irc.ircbot.chat.ChatBot;
import de.tfelix.irc.ircbot.event.ChatTextEvent;
import de.tfelix.irc.ircbot.event.Event;
import de.tfelix.irc.ircbot.event.EventType;

/**
 * This trigger subscribes for chat text events.
 * 
 * @author Thomas Felix <thomas.felix@tfelix.de>
 *
 */
public class ChatTextTrigger extends Trigger {

	private final String triggerText;

	public ChatTextTrigger(Action action, String cmdText) {
		super(action);

		this.triggerText = Objects.requireNonNull(cmdText);
	}

	@Override
	protected boolean isTriggered(Event event) {
		final ChatTextEvent txtEvent = (ChatTextEvent) event;
		return txtEvent.getText().startsWith(triggerText);
	}

	@Override
	protected void installSubscriptions(ChatBot chatBot) {
		chatBot.subscribe(EventType.CHAT_TEXT, this);
	}

}
