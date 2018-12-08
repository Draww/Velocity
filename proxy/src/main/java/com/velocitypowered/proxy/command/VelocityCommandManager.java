package com.velocitypowered.proxy.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandResult;
import com.velocitypowered.api.command.CommandSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class VelocityCommandManager implements CommandManager {

  private final Map<String, Command> commands = new HashMap<>();

  @Override
  public void register(@NonNull final Command command, final String... aliases) {
    Preconditions.checkNotNull(aliases, "aliases");
    Preconditions.checkNotNull(command, "executor");
    for (int i = 0, length = aliases.length; i < length; i++) {
      final String alias = aliases[i];
      Preconditions.checkNotNull(alias, "alias at index %s", i);
      this.commands.put(alias.toLowerCase(Locale.ENGLISH), command);
    }
  }

  @Override
  public void unregister(@NonNull final String alias) {
    Preconditions.checkNotNull(alias, "name");
    this.commands.remove(alias.toLowerCase(Locale.ENGLISH));
  }

  @Override
  public CommandResult execute(@NonNull CommandSource source, @NonNull String cmdLine) {
    Preconditions.checkNotNull(source, "invoker");
    Preconditions.checkNotNull(cmdLine, "cmdLine");

    String[] split = cmdLine.split(" ", -1);
    if (split.length == 0) {
      return CommandResult.NOT_FOUND;
    }

    String alias = split[0];
    @SuppressWarnings("nullness")
    String[] actualArgs = Arrays.copyOfRange(split, 1, split.length);
    Command command = commands.get(alias.toLowerCase(Locale.ENGLISH));
    if (command == null) {
      return CommandResult.NOT_FOUND;
    }

    try {
      if (!command.shouldHandle(source, actualArgs)) {
        return CommandResult.DECLINED;
      }

      return command.execute(source, actualArgs) ? CommandResult.SUCCESSFUL :
          CommandResult.DECLINED;
    } catch (Exception e) {
      throw new RuntimeException("Unable to invoke command " + cmdLine + " for " + source, e);
    }
  }

  public boolean hasCommand(String command) {
    return commands.containsKey(command);
  }

  /**
   * Offer suggestions to fill in the command.
   * @param source the source for the command
   * @param cmdLine the partially completed command
   * @return a {@link List}, possibly empty
   */
  public List<String> offerSuggestions(CommandSource source, String cmdLine) {
    Preconditions.checkNotNull(source, "source");
    Preconditions.checkNotNull(cmdLine, "cmdLine");

    String[] split = cmdLine.split(" ", -1);
    if (split.length == 0) {
      // No command available.
      return ImmutableList.of();
    }

    String alias = split[0];
    if (split.length == 1) {
      // Offer to fill in commands.
      List<String> availableCommands = new ArrayList<>();
      for (Map.Entry<String, Command> entry : commands.entrySet()) {
        if (entry.getKey().regionMatches(true, 0, alias, 0, alias.length())
            && entry.getValue().shouldHandle(source, new String[0])) {
          availableCommands.add("/" + entry.getKey());
        }
      }
      return availableCommands;
    }

    @SuppressWarnings("nullness")
    String[] actualArgs = Arrays.copyOfRange(split, 1, split.length);
    Command command = commands.get(alias.toLowerCase(Locale.ENGLISH));
    if (command == null) {
      // No such command, so we can't offer any tab complete suggestions.
      return ImmutableList.of();
    }

    try {
      if (!command.shouldHandle(source, actualArgs)) {
        return ImmutableList.of();
      }

      return command.suggest(source, actualArgs);
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to invoke suggestions for command " + alias + " for " + source, e);
    }
  }
}
