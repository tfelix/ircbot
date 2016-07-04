package de.tfelix.irc.ircbot.chat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This implementation fires asynchronously chat events to the server. There
 * will be a rate limit in place (messages will be queued) in order to avoid
 * bans on certain systems.
 * 
 * @author Thomas Felix <thomas.felix@tfelix.de>
 *
 */
public class AsyncLimitedChatApi implements ChatApi {
	
	/**
	 * Container class containing a chat message which will be send out via the API.
	 * 
	 * @author Thomas Felix <thomas.felix@tfelix.de>
	 *
	 */
	private class ChatMessage {
		
		private final String username;
		private final String text;
		
		public ChatMessage(String text) {
			this.username = null;
			this.text = Objects.requireNonNull(text);
		}
		
		public ChatMessage(String username, String text) {
			this.username = Objects.requireNonNull(username);
			this.text = Objects.requireNonNull(text);
		}
		
		public String getUsername() {
			return username;
		}
		
		public String getText() {
			return text;
		}
	}
	
	private class SendRunnable implements Runnable {
		
		public AtomicBoolean isRunning = new AtomicBoolean(true);
		
		private int sendMessageCount = 0;
		private Temporal lastInterval;

		@Override
		public void run() {
			
			lastInterval = LocalDateTime.now();
			
			while(isRunning.get()) {
				final Duration passed = Duration.between(lastInterval, LocalDateTime.now());
				
				if(passed.compareTo(duration) > 0) {
					// Cooldown is over.
					sendMessageCount = 0;
				}
				
				if(sendMessageCount >= maxMessages) {
					// Sleep for the rest of the period.
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// no op.
					}
					continue;
				}
				
				try {
					final ChatMessage msg = messageQueue.poll(5, TimeUnit.SECONDS);
					
					if(msg == null) {
						continue;
					}
					
					if(msg.getUsername() != null) {
						// Private message.
						chatApi.sendPrivateMsg(msg.getUsername(), msg.getText());
					} else {
						// Public message.
						chatApi.sendPublicMsg(msg.getText());
					}
				} catch (InterruptedException e) {
					// no op.
				}			
			}	
		}
	}
	
	private final int maxMessages;
	private final Duration duration;
	private final ChatApi chatApi;
	private final SendRunnable sendRunnable;
	
	private final BlockingQueue<ChatMessage> messageQueue = new LinkedBlockingQueue<>();
	
	private Thread sendThread;
	
	public AsyncLimitedChatApi(int maxMessage, Duration duration, ChatApi chatApi) {
		
		this.maxMessages = maxMessage;
		this.duration = Objects.requireNonNull(duration);
		this.chatApi = Objects.requireNonNull(chatApi);
		
		this.sendRunnable = new SendRunnable();
		sendThread = new Thread(this.sendRunnable);
		sendThread.start();
	}

	public void sendPublicMsg(String text) {
		messageQueue.add(new ChatMessage(text));
	}

	public void sendPrivateMsg(String username, String text) {
		messageQueue.add(new ChatMessage(username, text));
	}

}
