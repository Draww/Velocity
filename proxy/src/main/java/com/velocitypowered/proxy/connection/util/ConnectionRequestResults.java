package com.velocitypowered.proxy.connection.util;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.proxy.protocol.packet.Disconnect;
import java.util.Optional;
import net.kyori.text.Component;
import net.kyori.text.serializer.ComponentSerializers;

public class ConnectionRequestResults {

  public static final ConnectionRequestBuilder.Result SUCCESSFUL = plainResult(
      ConnectionRequestBuilder.Status.SUCCESS);

  private ConnectionRequestResults() {
    throw new AssertionError();
  }

  /**
   * Returns a plain result (one with a status but no reason).
   * @param status the status to use
   * @return the result
   */
  public static ConnectionRequestBuilder.Result plainResult(
      ConnectionRequestBuilder.Status status) {
    return new ConnectionRequestBuilder.Result() {
      @Override
      public ConnectionRequestBuilder.Status getStatus() {
        return status;
      }

      @Override
      public Optional<Component> getReason() {
        return Optional.empty();
      }
    };
  }

  public static ConnectionRequestBuilder.Result forDisconnect(Disconnect disconnect) {
    Component deserialized = ComponentSerializers.JSON.deserialize(disconnect.getReason());
    return forDisconnect(deserialized);
  }

  /**
   * Returns a disconnect result with a reason.
   * @param component the reason for disconnecting from the server
   * @return the result
   */
  public static ConnectionRequestBuilder.Result forDisconnect(Component component) {
    return new ConnectionRequestBuilder.Result() {
      @Override
      public ConnectionRequestBuilder.Status getStatus() {
        return ConnectionRequestBuilder.Status.SERVER_DISCONNECTED;
      }

      @Override
      public Optional<Component> getReason() {
        return Optional.of(component);
      }
    };
  }
}
