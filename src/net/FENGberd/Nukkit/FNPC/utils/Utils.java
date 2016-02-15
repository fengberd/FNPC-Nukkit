package net.FENGberd.Nukkit.FNPC.utils;

import java.io.*;
import java.awt.image.*;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class Utils
{
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj)
	{
		return (T) obj;
	}

	public static byte[] getPngSkin(File file)
	{
		return Utils.getPngSkin(file,true);
	}
	
	public static byte[] getPngSkin(File file,boolean useCache)
	{
		File cache=new File(file.getParent(),"cache/"+file.getName()+".cache");
		if(useCache && cache.exists())
		{
			FileInputStream in=null;
			ByteBuffer buff=null;
			try
			{
				in=new FileInputStream(cache);
				buff=ByteBuffer.allocate(in.available());
				while(in.available()!=0)
				{
					buff.put((byte)in.read());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			if(in!=null)
			{
				try
				{
					in.close();
				}
				catch(Exception e) {}
			}
			return buff.array();
		}
		if(!file.isFile())
		{
			return new byte[0];
		}
		try
		{
			BufferedImage img=ImageIO.read(file);
			ColorModel color=img.getColorModel();
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			for(int y=0;y<img.getHeight();y++)
			{
				for(int x=0;x<img.getWidth();x++)
				{
					Object pos=img.getRaster().getDataElements(x,y,null);
					buffer.write((byte)color.getRed(pos));
					buffer.write((byte)color.getGreen(pos));
					buffer.write((byte)color.getBlue(pos));
					buffer.write((byte)color.getAlpha(pos));
				}
			}
			byte[] data=buffer.toByteArray();
			buffer.close();
			if(cache.exists())
			{
				cache.delete();
			}
			cache.createNewFile();
			FileOutputStream stream=new FileOutputStream(cache);
			stream.write(data,0,data.length);
			stream.close();
			return data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new byte[0];
		}
	}
}
