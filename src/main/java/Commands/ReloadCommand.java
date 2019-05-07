package Commands;

import me.shakeforprotein.lazytools.LazyTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private LazyTools pl;

    public ReloadCommand(LazyTools main){
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        pl.reloadConfig();
        return true;
    }
}
