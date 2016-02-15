package net.FENGberd.Nukkit.FNPC.utils;

import net.FENGberd.Nukkit.FNPC.npc.NPC;

public class RegisteredNPC
{
	public Class<NPC> npcClass=null;
	public String name="",description="";

	public RegisteredNPC()
	{

	}

	public RegisteredNPC(Class<NPC> npcClass,String name,String description)
	{
		this.npcClass=npcClass;
		this.name=name;
		this.description=description;
	}
}