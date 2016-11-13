package net.FENGberd.Nukkit.FNPC.utils;

import java.io.*;
import java.util.*;
import java.nio.*;
import java.awt.image.*;

import javax.imageio.ImageIO;

import cn.nukkit.lang.*;

import net.FENGberd.Nukkit.FNPC.*;

public class Utils
{
	private static BaseLang lang=null;
	
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj)
	{
		return (T) obj;
	}
	
	public static String t(String str)
	{
		return lang.translateString(str);
	}
	
	public static String t(String str,String... params)
	{
		return lang.translateString(str,params);
	}
	
	public static void loadLang(BaseLang lang)
	{
		try
		{
			Utils.lang=lang;
			InputStream stream=Main.getInstance().getResource("lang/"+lang.getLang()+".ini");
			if(stream!=null)
			{
				loadLangStream(lang.getLangMap(),stream);
			}
			else
			{
				loadLangStream(lang.getLangMap(),Main.getInstance().getResource("lang/chs.ini"));
			}
		}
		catch(Exception e)
		{
			Main.getInstance().getLogger().error("Error while loading language file: ",e);
		}
	}
	
	public static void loadLangStream(Map<String,String> d,InputStream stream) throws Exception
	{
		String content=cn.nukkit.utils.Utils.readFile(stream);
		for(String line:content.split("\n"))
		{
			line=line.trim();
			if(line.equals("") || line.charAt(0)=='#')
			{
				continue;
			}
			String[] t=line.split("=");
			if (t.length<2)
			{
				continue;
			}
			String key=t[0];
			String value="";
			for(int i=1;i<t.length-1;i++)
			{
				value+=t[i]+"=";
			}
			value+=t[t.length-1];
			if(value.equals(""))
			{
				continue;
			}
			d.put(key,value);
		}
		stream.close();
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
