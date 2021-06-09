package me.psek.vehicles.commands;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VehiclesCommand implements CommandExecutor {
    private final Vehicles plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("Console may not use this command.");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage("Too little arguments");
            return false;
        }

        //todo add custom messages via dem configy
        switch (args[0]) {
            case "spawn":
                if (!sender.hasPermission("vehicles.spawn")) {
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage("Missing the vehicle name");
                    return false;
                }
                String carName = args[1].toLowerCase();
                if (!plugin.getSubVehicleTypes().containsKey(carName)) {
                    sender.sendMessage("Vehicle " + args[1].toLowerCase() + " does not exist");
                    return false;
                }
                Player player = (Player) sender;
                if (args.length > 2) {
                    Player p = Bukkit.getServer().getPlayer(args[2]);
                    if (p == null) {
                        player.sendMessage("Invalid player");
                        return false;
                    }
                    player = p;
                }
                IVehicle iVehicle = plugin.getSubVehicleTypes().get(carName);
                iVehicle.spawn(plugin, carName, player.getLocation());
                sender.sendMessage("Spawned ur bunda ride at dem playuurrrr");
                return true;
            case "info":
                sender.sendMessage("Created by Psek with luveeeee <3");
                break;
            default:
                sender.sendMessage("Invalid argument(s)");
                break;
        }
        return false;
    }

    public VehiclesCommand(Vehicles plugin) {
        this.plugin = plugin;
    }
}
