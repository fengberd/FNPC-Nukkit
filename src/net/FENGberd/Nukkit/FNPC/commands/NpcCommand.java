package net.FENGberd.Nukkit.FNPC.commands;

import cn.nukkit.*;
import cn.nukkit.item.*;
import cn.nukkit.lang.*;
import cn.nukkit.utils.*;
import cn.nukkit.command.*;
import cn.nukkit.command.data.*;

import net.FENGberd.Nukkit.FNPC.*;
import net.FENGberd.Nukkit.FNPC.npc.*;
import net.FENGberd.Nukkit.FNPC.utils.*;
import net.FENGberd.Nukkit.FNPC.tasks.*;
import net.FENGberd.Nukkit.FNPC.utils.Utils;

import java.util.*;

public class NpcCommand extends Command
{
	public NpcCommand()
	{
		super("fnpc","");
		this.setPermission("FNPC.command.fnpc");
		this.setAliases(new String[]
		{
			"npc"
		});
		this.commandParameters.clear();
		this.commandParameters.put("add",new CommandParameter[]
		{
			new CommandParameter("Type",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("Name",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("remove",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("type",new CommandParameter[0]);
		this.commandParameters.put("skin",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("File",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("name",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("Name",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("command",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("add|remove",CommandParameter.ARG_TYPE_STRING_ENUM,false),
			new CommandParameter("Command",CommandParameter.ARG_TYPE_STRING,false)
		});
		// TODO: command <ID> list
		this.commandParameters.put("tphere",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("teleport",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("transfer",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("IP",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("Port",CommandParameter.ARG_TYPE_INT,false)
		});
		this.commandParameters.put("reset",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("reset",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("add|remove",CommandParameter.ARG_TYPE_STRING_ENUM,false),
			new CommandParameter("Chat",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("item",new CommandParameter[]
		{
			new CommandParameter("ID",CommandParameter.ARG_TYPE_STRING,false),
			new CommandParameter("Item[:Damage]",CommandParameter.ARG_TYPE_STRING,false)
		});
		this.commandParameters.put("help",new CommandParameter[0]);
	}
	
	@Override
	public boolean execute(CommandSender sender,String commandLabel,String[] args)
	{
		if(!this.testPermission(sender))
		{
			return true;
		}
		if(args.length==0)
		{
			sender.sendMessage(new TranslationContainer("commands.generic.usage",""));
			return false;
		}
		try
		{
			NPC npc;
			if(args.length<=0)
			{
				return false;
			}
			switch(args[0])
			{
			case "type":
				{
					final String[] data={TextFormat.GREEN+"=========="+TextFormat.YELLOW+"FNPC Type List"+TextFormat.GREEN+"=========="};
					Main.getRegisteredNpcs().values().forEach(npcF->data[0]+="\n"+TextFormat.YELLOW+npcF.name+TextFormat.WHITE+" - "+TextFormat.AQUA+npcF.description);
					sender.sendMessage(data[0]);
				}
				break;
			case "add":
				if(args.length<4)
				{
					return false;
				}
				if(sender instanceof Player)
				{
					if(NPC.pool.getOrDefault(args[2],null)!=null)
					{
						sender.sendMessage("[NPC] "+TextFormat.RED+"已存在同ID的NPC");
						break;
					}
					args[1]=args[1].toLowerCase();
					RegisteredNPC npcClass=Main.getRegisteredNpcClass(args[1].toLowerCase());
					if(npcClass==null)
					{
						sender.sendMessage("[NPC] "+TextFormat.RED+"指定类型不存在 ,请使用 /fnpc type 查看可用类型");
					}
					else
					{
						Player sender_=Utils.cast(sender);
						npc=npcClass.npcClass.getConstructor(String.class,String.class,double.class,double.class,double.class,Item.class).newInstance(args[2],args[3],sender_.x,sender_.y,sender_.z,sender_.getInventory().getItemInHand());
						npc.level=sender_.getLevel().getFolderName();
						npc.spawnToAll();
						npc.save();
						sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC创建成功");
					}
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"请在游戏中使用这个指令");
				}
				break;
			case "transfer":
				if(args.length<4)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else if(npc instanceof TeleportNPC)
				{
					HashMap<String,Object> data=new HashMap<>();
					data.put("ip",args[2]);
					data.put("port",args[3]);
					((TeleportNPC)npc).setTeleport(data);
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC跨服传送设置成功");
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"该NPC不是传送型NPC");
				}
				break;
			case "remove":
				if(args.length<2)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else
				{
					npc.close();
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"移除成功");
				}
				break;
			case "reset":
				if(args.length<2)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
					break;
				}
				if(npc instanceof TeleportNPC)
				{
					((TeleportNPC)npc).setTeleport(new HashMap<>());
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC传送点移除成功");
				}
				else if(npc instanceof CommandNPC)
				{
					((CommandNPC)npc).command=new ArrayList<>();
					npc.save();
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC指令清空成功");
				}
				else if(npc instanceof ReplyNPC)
				{
					((ReplyNPC)npc).chat=new ArrayList<>();
					npc.save();
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC对话数据清空成功");
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.YELLOW+"该NPC没有可重置的属性");
				}
				break;
			case "teleport":
				if(args.length<2)
				{
					return false;
				}
				if(sender instanceof Player)
				{
					npc=NPC.pool.getOrDefault(args[1],null);
					if(npc==null)
					{
						sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
					}
					else if(npc instanceof TeleportNPC)
					{
						((TeleportNPC)npc).setTeleport((Player)sender);
						sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC传送点设置成功");
					}
					else
					{
						sender.sendMessage("[NPC] "+TextFormat.RED+"该NPC不是传送型NPC");
					}
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"请在游戏中使用这个指令");
				}
				break;
			case "command":
				if(args.length<3)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else if(npc instanceof CommandNPC)
				{
					String cmd="";
					switch(args[2])
					{
					case "add":
						if(args.length<4)
						{
							return false;
						}
						for(int i=3;i<args.length;i++)
						{
							cmd+=args[i]+(i!=args.length-1?" ":"");
						}
						((CommandNPC)npc).addCommand(cmd);
						sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC指令添加成功");
						break;
					case "remove":
						if(args.length<4)
						{
							return false;
						}
						for(int i=3;i<args.length;i++)
						{
							cmd+=args[i]+(i!=args.length-1?" ":"");
						}
						if(((CommandNPC)npc).removeCommand(cmd))
						{
							sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC指令移除成功");
						}
						else
						{
							sender.sendMessage("[NPC] "+TextFormat.RED+"NPC未添加该指令");
						}
						break;
					case "list":
						final String[] data={TextFormat.GREEN+"===NPC指令列表===\n"};
						((CommandNPC)npc).command.forEach(cmdData->data[0]+=TextFormat.YELLOW+cmdData+"\n");
						sender.sendMessage(data[0]);
						break;
					default:
						return false;
					}
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"该NPC不是指令型NPC");
				}
				break;
			case "chat":
				if(args.length<3)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else if(npc instanceof ReplyNPC)
				{
					String chat="";
					switch(args[2])
					{
					case "add":
						if(args.length<4)
						{
							return false;
						}
						for(int i=3;i<args.length;i++)
						{
							chat+=args[i]+(i!=args.length-1?" ":"");
						}
						((ReplyNPC)npc).addChat(chat);
						sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC对话数据添加成功");
						break;
					case "remove":
						if(args.length<4)
						{
							return false;
						}
						for(int i=3;i<args.length;i++)
						{
							chat+=args[i]+(i!=args.length-1?" ":"");
						}
						if(((ReplyNPC)npc).removeChat(chat))
						{
							sender.sendMessage("[NPC] "+TextFormat.GREEN+"NPC对话数据移除成功");
						}
						else
						{
							sender.sendMessage("[NPC] "+TextFormat.RED+"NPC未添加该对话数据");
						}
						break;
					default:
						return false;
					}
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"该NPC不是回复型NPC");
				}
				break;
			case "name":
				if(args.length<3)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else
				{
					npc.setName(args[2]);
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"NameTag设置成功");
				}
				break;
			case "skin":
				if(args.length<3)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else
				{
					npc.setPNGSkin(args[2]);
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"皮肤更换成功");
				}
				break;
			case "item":
				if(args.length<3)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else
				{
					String[] itemData=args[2].split(":");
					npc.setHandItem(Item.get(Integer.parseInt(itemData[0]),Integer.parseInt(itemData.length<2?"0":itemData[1])));
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"手持物品更换成功");
				}
				break;
			case "tphere":
				if(args.length<2)
				{
					return false;
				}
				npc=NPC.pool.getOrDefault(args[1],null);
				if(npc==null)
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"不存在此NPC");
				}
				else if(sender instanceof Player)
				{
					npc.teleport(Utils.cast(sender));
					sender.sendMessage("[NPC] "+TextFormat.GREEN+"传送成功");
				}
				else
				{
					sender.sendMessage("[NPC] "+TextFormat.RED+"请在游戏中使用这个指令");
				}
				break;
			case "help":
				sender.sendMessage(TextFormat.GREEN+"===NPC系统指令帮助===\n"+
					TextFormat.GREEN+"所有指令前面必须加/fnpc \n"+
					TextFormat.YELLOW+"add <Type> <ID> <Name> - 添加一个NPC\n"+
					TextFormat.YELLOW+"type - 列出可用的Type类型\n"+
					TextFormat.YELLOW+"remove <ID> - 移除一个NPC\n"+
					TextFormat.YELLOW+"skin <ID> <File> - 设置NPC皮肤\n"+
					TextFormat.YELLOW+"name <ID> <Name> - 设置NPC名称\n"+
					TextFormat.YELLOW+"command <ID> <add/remove> <Command> - 添加/删除NPC指令\n"+
					TextFormat.YELLOW+"command <ID> list - 列出NPC指令\n"+
					TextFormat.YELLOW+"tphere <ID> - 把NPC传送过来\n"+
					TextFormat.YELLOW+"teleport <ID> - 设置NPC传送目标为你的位置\n"+
					TextFormat.YELLOW+"transfer <ID> <IP> <Port> - 设置NPC跨服传送\n"+
					TextFormat.YELLOW+"reset <ID> - 重置NPC的设置\n"+
					TextFormat.YELLOW+"chat <ID> <add/remove> <Chat> - 添加/删除NPC对话数据\n"+
					TextFormat.YELLOW+"item <ID> <Item[:Damage]> - 设置NPC手持物品\n"+
					TextFormat.YELLOW+"help - 查看帮助");
				break;
			default:
				return false;
			}
		}
		catch(Exception e)
		{
			sender.sendMessage("[NPC] "+TextFormat.RED+"出现了未知错误");
			e.printStackTrace();
		}
		return true;
	}
	/*
	@Override
	public CommandDataVersions generateCustomCommandData(Player player)
	{
		return new CommandDataVersions();
	}
	*/
	public Map<String,CommandDataVersions> processCustomCommandData(Map<String,CommandDataVersions> data)
	{
		if(data.containsKey("fnpc"))
		{
			data.remove("fnpc");
			this.commandParameters.forEach((key,par)->
			{
				CommandData customData=new CommandData();
				customData.description=Server.getInstance().getLanguage().translateString(this.getDescription());
				customData.permission="any";
				customData.aliases=this.getAliases().clone();
				for(int i=0;i<customData.aliases.length;i++)
				{
					customData.aliases[i]=customData.aliases[i]+" "+key;
				}
				CommandOverload overload=new CommandOverload();
				overload.input.parameters=par;
				customData.overloads.put(key,overload);
				CommandDataVersions versions=new CommandDataVersions();
				versions.versions.add(customData);
				data.put(this.getName()+" "+key,versions);
			});
		}
		return data;
	}
}


/*package net.FENGberd.Nukkit.FNPC.commands;

import cn.nukkit.lang.*;
import cn.nukkit.command.*;

import net.FENGberd.Nukkit.FNPC.*;

public abstract class NpcCommand extends Command
{
	public NpcCommand(String subParm)
	{
		this(subParm,null,null);
	}
	
	public NpcCommand(String subParm,String description)
	{
		this(subParm,description,null);
	}
	
	public NpcCommand(String subParm,String description,String usage)
	{
		super("fnpc "+subParm,description==null?"%FNPC.command.fnpc."+subParm+".description":description,usage==null?"%FNPC.command.fnpc."+subParm+".usage":usage);
		this.setPermission("FNPC.command.fnpc."+subParm.toLowerCase());
		this.setAliases(new String[]{"npc "+subParm});
	}
	
	@Override
	public boolean execute(CommandSender sender,String commandLabel,String[] args)
	{
		if(!this.testPermission(sender))
		{
			return true;
		}
		try
		{
			if(!this.onExecute(sender,args))
			{
				sender.sendMessage(new TranslationContainer("commands.generic.usage",this.usageMessage));
				return false;
			}
		}
		catch(Exception e)
		{
			Main.getInstance().getLogger().error("Error while executing "+commandLabel+": ",e);
		}
		return true;
	}
	
	public abstract boolean onExecute(CommandSender sender,String[] args) throws Exception;
}
*/