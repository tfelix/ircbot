package de.tfelix.irc.ircbot.action;

import java.util.Objects;

import de.tfelix.irc.ircbot.chat.ChatApi;
import de.tfelix.irc.ircbot.event.ChatTextEvent;
import de.tfelix.irc.ircbot.event.Event;
import de.tfelix.irc.ircbot.trigger.ChatTextTrigger;

/**
 * Sample HelloWorldAction which will respond to the public chat with the name
 * of the given user who write !helloworld.
 * 
 * @author Thomas Felix <thomas.felix@tfelix.de>
 *
 */
public class HelloWorldAction implements Action {

	public final static String CMD_TEXT = "!hello";
	
	public HelloWorldAction() {


	}

	@Override
	public void performAction(Event event, ChatApi chatApi) {
		final ChatTextEvent txtEvent = (ChatTextEvent) event;

		final String msg = String.format("Hallo, %s!", txtEvent.getUsername());

		chatApi.sendPublicMsg(msg);
	}
}
