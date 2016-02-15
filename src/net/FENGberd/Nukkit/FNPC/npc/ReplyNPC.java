package net.FENGberd.Nukkit.FNPC.npc;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import net.FENGberd.Nukkit.FNPC.Main;
import net.FENGberd.Nukkit.FNPC.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("unused")
public class ReplyNPC extends NPC
{
	public ArrayList<String> chat=new ArrayList<>();

	public ReplyNPC(String nid,String nametag,double x,double y,double z,Item handItem)
	{
		super(nid,nametag,x,y,z,handItem);
	}

	public ReplyNPC(String nid,String nametag,double x,double y,double z)
	{
		this(nid,nametag,x,y,z,null);
	}

	public ReplyNPC(String nid)
	{
		this(nid,"",0,0,0);
	}

	@Override
	public void onTouch(Player player)
	{
		Object[] arr=this.chat.toArray();
		if(arr.length>0)
		{
			player.sendMessage("<"+this.nametag+"> "+arr[new Random().nextInt(arr.length)]);
		}
	}

	@Override
	public HashMap<String,Object> reload()
	{
		HashMap<String,Object> cfg=super.reload();
		if(cfg!=null)
		{
			this.chat=Utils.cast(cfg.getOrDefault("chat",new ArrayList<String>()));
		}
		return cfg;
	}

	public void addChat(String data)
	{
		this.chat.add(data);
		this.save();
	}

	public boolean removeChat(String data)
	{
		return this.chat.remove(data);
	}

	@Override
	public void save()
	{
		this.save(new HashMap<>());
	}

	@Override
	public void save(HashMap<String,Object> extra)
	{
		extra.put("type","reply");
		extra.put("chat",this.chat);
		super.save(extra);
	}
}
