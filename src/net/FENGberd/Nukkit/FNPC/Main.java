package net.FENGberd.Nukkit.FNPC;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

import net.FENGberd.Nukkit.FNPC.npc.*;
import net.FENGberd.Nukkit.FNPC.utils.*;
import net.FENGberd.Nukkit.FNPC.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Main extends cn.nukkit.plugin.PluginBase implements cn.nukkit.event.Listener
{
    private static HashMap<String,RegisteredNPC> registeredNPC=new HashMap<>();
    private static Main obj=null;

    public static Main getInstance()
    {
        return Main.obj;
    }

    public static RegisteredNPC getRegisteredNpcClass(String name)
    {
        RegisteredNPC npc=Main.registeredNPC.getOrDefault(name.toLowerCase(),null);
        if(npc==null)
        {
            return null;
        }
        return npc;
    }

    public static void unregisterNpc(String name)
    {
        Main.registeredNPC.remove(name.toLowerCase());
    }

    public static boolean registerNpc(String name,String description,Class npcClass)
    {
        return Main.registerNpc(name,description,npcClass,false);
    }

    public static boolean registerNpc(String name,String description,Class npcClass,boolean force)
    {
        name=name.toLowerCase();
        if(NPC.class.isAssignableFrom(npcClass) && ! npcClass.isInterface() && (Main.registeredNPC.getOrDefault(name,null)==null || force))
        {
            Main.registeredNPC.put(name,new RegisteredNPC(Utils.cast(npcClass),name,description));
            NPC.reloadUnknownNPC();
            return true;
        }
        return false;
    }

    /**
     * 静态分割线********************************
     */

    @Override
    public void onEnable()
    {
        if(Main.obj==null)
        {
            Main.obj=this;
            Main.registerNpc("normal","普通NPC(无实际功能)",NPC.class,true);
            Main.registerNpc("reply","回复型NPC(使用/fnpc chat)",ReplyNPC.class,true);
            Main.registerNpc("command","指令型NPC(使用/fnpc command)",CommandNPC.class,true);
            Main.registerNpc("teleport","传送型NPC(使用/fnpc teleport或/fnpc transfer)",TeleportNPC.class,true);
        }
        NPC.init();
        QuickSystemTask quickSystemTask=new QuickSystemTask(this);
        this.getServer().getScheduler().scheduleRepeatingTask(quickSystemTask,1);

        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public boolean onCommand(cn.nukkit.command.CommandSender sender,cn.nukkit.command.Command command,String label,String[] args)
    {
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
                    Main.registeredNPC.values().forEach(npcF->data[0]+="\n"+TextFormat.YELLOW+npcF.name+TextFormat.WHITE+" - "+TextFormat.AQUA+npcF.description);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(cn.nukkit.event.player.PlayerMoveEvent event)
    {
        NPC.playerMove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDataPacketReceive(cn.nukkit.event.server.DataPacketReceiveEvent event)
    {
        NPC.packetReceive(event.getPlayer(),event.getPacket());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(cn.nukkit.event.player.PlayerJoinEvent event)
    {
        NPC.spawnAllTo(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityLevelChange(cn.nukkit.event.entity.EntityLevelChangeEvent event)
    {
        if(event.getEntity() instanceof cn.nukkit.Player)
        {
            NPC.spawnAllTo(Utils.cast(event.getEntity()),event.getTarget());
        }
    }
}