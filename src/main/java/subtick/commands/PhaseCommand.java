package subtick.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

import subtick.TickHandler;
import subtick.TickPhase;
import subtick.SubTick;

public class PhaseCommand
{
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
  {
    dispatcher.register(
      literal("phaseStep")
      .then(argument("count", integer(1))
        .executes((c) -> stepCount(c.getSource(), getInteger(c, "count")))
      )
      .then(argument("phase", word())
        .suggests((c, b) -> suggest(TickPhase.getCommandKeys(), b))
        .then(literal("force")
          .executes((c) -> stepToPhase(c.getSource(), TickPhase.byCommandKey(getString(c, "phase")), true))
        )
        .executes((c) -> stepToPhase(c.getSource(), TickPhase.byCommandKey(getString(c, "phase")), false))
      )
      .executes((c) -> stepCount(c.getSource(), 1))
    );
  }

  private static int stepCount(CommandSourceStack c, int count)
  {
    TickHandler handler = SubTick.getTickHandler(c);
    TickPhase phase = handler.current_phase.next(count);
    int ticks = (count + handler.current_phase.getId()) / TickPhase.getCommandKeys().size();
    return TickCommand.step(c, ticks, phase);
  }

  private static int stepToPhase(CommandSourceStack c, TickPhase phase, boolean force)
  {
    TickHandler handler = SubTick.getTickHandler(c);
    if(!phase.isPosteriorTo(handler.current_phase) && force)
      return TickCommand.step(c, 1, phase);
    else
      return TickCommand.step(c, 0, phase);
  }
}
