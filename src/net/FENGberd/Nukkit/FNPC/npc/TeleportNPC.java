package net.FENGberd.Nukkit.FNPC.npc;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.*;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

import net.FENGberd.Nukkit.FNPC.Main;
import net.FENGberd.Nukkit.FNPC.protocol.StrangePacket;
import net.FENGberd.Nukkit.FNPC.utils.Utils;

import java.util.HashMap;

@SuppressWarnings("unused")
public class TeleportNPC extends NPC
{
	HashMap<String,Object> teleport=new HashMap<>();

	public TeleportNPC(String nid,String nametag,double x,double y,double z,Item handItem)
	{
		super(nid,nametag,x,y,z,handItem);
	}

	public TeleportNPC(String nid,String nametag,double x,double y,double z)
	{
		this(nid,nametag,x,y,z,null);
	}

	public TeleportNPC(String nid)
	{
		this(nid,"",0,0,0);
	}
	
	@Override
	public void onTouch(Player player)
	{
		if(this.teleport.getOrDefault("ip",null)!=null && this.teleport.getOrDefault("port",null)!=null)
		{
			StrangePacket pk=new StrangePacket();
			pk.address=Utils.cast(this.teleport.getOrDefault("ip",""));
			pk.port=Short.parseShort(Utils.cast(this.teleport.getOrDefault("port",0)));
			player.dataPacket(pk);
		}
		else if(this.teleport.getOrDefault("x",null)==null || this.teleport.getOrDefault("y",null)==null || this.teleport.getOrDefault("z",null)==null)
		{
			player.sendMessage("[System] "+TextFormat.RED+"该NPC未设置传送目标");
		}
		else if(this.teleport.getOrDefault("level",null)!=null)
		{
			Level level=Main.getInstance().getServer().getLevelByName(Utils.cast(this.teleport.get("level")));
			if(level==null)
			{
				player.sendMessage("[System] "+TextFormat.RED+"目标传送世界不存在");
			}
			else
			{
				player.teleport(new Position(Utils.cast(this.teleport.getOrDefault("x",0)),Utils.cast(this.teleport.getOrDefault("y",0)),Utils.cast(this.teleport.getOrDefault("z",0)),level));
				player.sendMessage("[System] "+TextFormat.GREEN+"传送成功");
			}
		}
		else
		{
			player.teleport(new Vector3(Utils.cast(this.teleport.getOrDefault("x",0)),Utils.cast(this.teleport.getOrDefault("y",0)),Utils.cast(this.teleport.getOrDefault("z",0))));
			player.sendMessage("[System] "+TextFormat.GREEN+"传送成功");
		}
	}

	@Override
	public HashMap<String,Object> reload()
	{
		HashMap<String,Object> cfg=super.reload();
		if(cfg!=null)
		{
			this.teleport=Utils.cast(cfg.getOrDefault("teleport",new HashMap<String,Object>()));
		}
		return cfg;
	}

	public void setTeleport(HashMap<String,Object> data)
	{
		this.teleport.clear();
		if(data!=null)
		{
			this.teleport.putAll(data);
		}
		this.save();
	}

	public void setTeleport(Vector3 data)
	{
		this.teleport.clear();
		if(data!=null)
		{
			this.teleport.put("x",data.getX());
			this.teleport.put("y",data.getY());
			this.teleport.put("z",data.getZ());
			if(data instanceof Position)
			{
				Position pos=Utils.cast(data);
				if(pos.getLevel()!=null)
				{
					this.teleport.put("level",pos.getLevel().getFolderName());
				}
			}
		}
		this.save();
	}

	@Override
	public void save()
	{
		this.save(new HashMap<>());
	}

	@Override
	public void save(HashMap<String,Object> extra)
	{
		extra.put("type","teleport");
		extra.put("teleport",this.teleport);
		super.save(extra);
	}
}
