package net.FENGberd.Nukkit.FNPC.npc;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import net.FENGberd.Nukkit.FNPC.Main;
import net.FENGberd.Nukkit.FNPC.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class CommandNPC extends NPC
{
	public ArrayList<String> command=new ArrayList<>();

	public CommandNPC(String nid,String nametag,double x,double y,double z,Item handItem)
	{
		super(nid,nametag,x,y,z,handItem);
	}

	public CommandNPC(String nid,String nametag,double x,double y,double z)
	{
		this(nid,nametag,x,y,z,null);
	}

	public CommandNPC(String nid)
	{
		this(nid,"",0,0,0);
	}

	@Override
	public void onTouch(Player player)
	{
		this.command.forEach(cmd->{
			cmd=cmd.replace("%p",player.getName()).replace("%x",String.valueOf(player.getX())).replace("%y",String.valueOf(player.getY())).replace("%z",String.valueOf(player.getZ()));
			if(! player.isOp() && cmd.contains("%op"))
			{
				cmd=cmd.replace("%op","");
				player.setOp(true);
				Main.getInstance().getServer().dispatchCommand(player,cmd);
				player.setOp(false);
			}
			else
			{
				cmd=cmd.replace("%op","");
				Main.getInstance().getServer().dispatchCommand(player,cmd);
			}
		});
	}

	@Override
	public HashMap<String,Object> reload()
	{
		HashMap<String,Object> cfg=super.reload();
		if(cfg!=null)
		{
			this.command=Utils.cast(cfg.getOrDefault("command",new ArrayList<String>()));
		}
		return cfg;
	}

	public void addCommand(String data)
	{
		this.command.add(data);
		this.save();
	}

	public boolean removeCommand(String data)
	{
		return this.command.remove(data);
	}

	@Override
	public void save()
	{
		this.save(new HashMap<>());
	}

	@Override
	public void save(HashMap<String,Object> extra)
	{
		extra.put("type","command");
		extra.put("command",this.command);
		super.save(extra);
	}
}
