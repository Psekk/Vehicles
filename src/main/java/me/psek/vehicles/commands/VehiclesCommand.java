package me.psek.vehicles.commands;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.Actions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VehiclesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case ("spawn"):
               if (CarData.ALL_REGISTERED_CARS.containsKey(args[1].toLowerCase())) {
                   Actions.spawn(((Player) sender).getLocation(), args[1]);
                   return true;
               }
            case ("test"):
                if (CarData.ALL_REGISTERED_CARS.containsKey(args[1].toLowerCase())) {
                    Actions.spawn(((Player) sender).getLocation(), args[1]);

                }
        }
        return false;
    }
}
