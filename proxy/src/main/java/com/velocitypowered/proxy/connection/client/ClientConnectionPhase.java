package com.velocitypowered.proxy.connection.client;

import com.velocitypowered.proxy.connection.forge.legacy.LegacyForgeHandshakeClientPhase;
import com.velocitypowered.proxy.protocol.packet.PluginMessage;

/**
 * Provides connection phase specific actions.
 *
 * <p>Note that Forge phases are found in the enum
 * {@link LegacyForgeHandshakeClientPhase}.</p>
 */
public interface ClientConnectionPhase {

  /**
   * Handle a plugin message in the context of
   * this phase.
   *
   * @param player The player
   * @param handler The {@link ClientPlaySessionHandler} that is handling
   *                packets
   * @param message The message to handle
   * @return true if handled, false otherwise.
   */
  default boolean handle(ConnectedPlayer player,
                         ClientPlaySessionHandler handler,
                         PluginMessage message) {
    return false;
  }

  /**
   * Instruct Velocity to reset the connection phase
   * back to its default for the connection type.
   *
   * @param player The player
   */
  default void resetConnectionPhase(ConnectedPlayer player) {
  }

  /**
   * Perform actions just as the player joins the
   * server.
   *
   * @param player The player
   */
  default void onFirstJoin(ConnectedPlayer player) {
  }

  /**
   * Indicates whether the connection is considered complete.
   * @return true if so
   */
  default boolean consideredComplete() {
    return true;
  }
}
