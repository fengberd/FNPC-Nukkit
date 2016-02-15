package net.FENGberd.Nukkit.FNPC.tasks;

import net.FENGberd.Nukkit.FNPC.Main;

public class QuickSystemTask extends cn.nukkit.scheduler.PluginTask<Main>
{
	public QuickSystemTask(Main owner)
	{
		super(owner);
	}

	@Override
	public void onRun(int currentTick)
	{
		net.FENGberd.Nukkit.FNPC.npc.NPC.tick();
	}
}
