package de.tfelix.irc.ircbot.config;

import org.pircbotx.Configuration;

/**
 * Can be used to tell the server to join a certain channel and IRC.
 * 
 * @author Thomas Felix <thomas.felix@tfelix.de>
 *
 */
public interface BotConfig {

	public Configuration.Builder getConfiguration();

}
