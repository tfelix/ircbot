package de.tfelix.irc.ircbot.chat;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import de.tfelix.irc.ircbot.config.BotConfig;
import de.tfelix.irc.ircbot.event.ChatTextEvent;
import de.tfelix.irc.ircbot.event.EventType;
import de.tfelix.irc.ircbot.trigger.Trigger;

public class ChatBot {

	private static final Logger LOG = LogManager.getLogger(ChatBot.class);

	private class BotThreadRunnable implements Runnable {

		private final AtomicBoolean runFlag = new AtomicBoolean(false);
		private final PircBotX bot;

		public BotThreadRunnable(PircBotX bot) {
			this.bot = bot;
		}

		@Override
		public void run() {
			runFlag.set(true);

			// Connect to the server
			try {
				bot.startBot();
			} catch (IOException | IrcException e) {
				LOG.error("IRC Error.", e);
			}

			runFlag.set(false);

			LOG.info("Disconnected from IRC.");
		}

		public void stop() {
			if (runFlag.get()) {
				bot.close();
			} else {
				LOG.info("IRC is not connected.");
			}
		}
	}

	private class IrcListener extends ListenerAdapter {
		@Override
		public void onGenericMessage(GenericMessageEvent event) {
			// Prepare our custom chat event.
			final ChatTextEvent txtEvent = new ChatTextEvent(event.getUser().getNick(), event.getMessage());

			// Feed our event system.
			triggerListener.get(EventType.CHAT_TEXT).forEach(x -> x.onEvent(txtEvent));
		}
	}

	private IrcListener listener = new IrcListener();
	private Thread botThread;
	private BotThreadRunnable btRun;
	private final EnumMap<EventType, List<Trigger>> triggerListener = new EnumMap<>(EventType.class);
	private ChatApi chatApi;
	private final BotConfig config;

	public ChatBot(BotConfig config) {

		this.config = Objects.requireNonNull(config);
	}

	/**
	 * Connects the bot and starts to listen to IRC events.
	 */
	public void connect() {
		LOG.info("Connecting to IRC.");

		final Configuration.Builder builder = config.getConfiguration();

		builder.addListener(listener);

		final PircBotX bot = new PircBotX(builder.buildConfiguration());
		this.btRun = new BotThreadRunnable(bot);
		this.botThread = new Thread(btRun);
		this.botThread.setName("IrcBotThread");
		this.botThread.start();

		// Possible problem if the api is requested before connect is called.
		final ChatApi pircApi = new PircChatApi("#rocketplays", bot);
		this.chatApi = new AsyncLimitedChatApi(30, Duration.ofSeconds(30), pircApi);
	}

	/**
	 * Stops all operation on the channel.
	 */
	public void disconnect() {
		this.btRun.stop();
	}

	public void subscribe(EventType chatEvent, Trigger trigger) {
		if (!triggerListener.containsKey(chatEvent)) {
			triggerListener.put(chatEvent, new ArrayList<Trigger>());
		}

		triggerListener.get(chatEvent).add(trigger);
	}

	public void registerTrigger(Trigger helloTrigger) {
		helloTrigger.installHook(this);
	}

	public ChatApi getChatApi() {
		return chatApi;
	}

}
