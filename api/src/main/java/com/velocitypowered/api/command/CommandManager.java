package com.velocitypowered.api.command;

/**
 * Represents an interface to register a command executor with the proxy.
 */
public interface CommandManager {

  /**
   * Registers the specified command with the manager with the specified aliases.
   *
   * @param command the command to register
   * @param aliases the alias to use
   */
  void register(Command command, String... aliases);

  /**
   * Unregisters a command.
   *
   * @param alias the command alias to unregister
   */
  void unregister(String alias);

  /**
   * Attempts to execute a command from the specified {@code cmdLine}.
   *
   * @param source the command's source
   * @param cmdLine the command to run
   * @return {@link CommandResult#SUCCESSFUL} if run successfully, {@link CommandResult#DECLINED}
   * if run but not handled, and {@link CommandResult#NOT_FOUND} if not found
   */
  CommandResult execute(CommandSource source, String cmdLine);
}
