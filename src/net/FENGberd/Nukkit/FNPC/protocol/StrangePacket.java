package net.FENGberd.Nukkit.FNPC.protocol;

@SuppressWarnings("unused")
public class StrangePacket extends cn.nukkit.network.protocol.DataPacket
{
	public String address;
	public short port=19132;

	@Override
	public byte pid()
	{
		return 0x1b;
	}

	@Override
	public void decode()
	{

	}

	@Override
	public void encode()
	{
		this.reset();
		this.putByte((byte)4);
		for(String data:this.address.split("\\."))
		{
			this.putByte((byte)((~(Integer.parseInt(data)))&0xff));
		}
		this.putShort(this.port);
	}
}
