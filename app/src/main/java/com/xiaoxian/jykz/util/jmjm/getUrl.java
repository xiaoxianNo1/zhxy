package com.xiaoxian.jykz.util.jmjm;

import java.text.SimpleDateFormat;
import java.util.Date;

public class getUrl {
	
	//加密
	public static String encrypt(String id_temp,int flag)
	{
        String zff="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ=&";
		String zff1="0123456789abcdefghmnopqrstuxzBCDEFGHNQTUVZ=&";
        char mmid;
        byte zf,j;
		String id=id_temp;
        if (id.equals("") || id==null)
        {
            return id;
        }

        long i,ii,m,mm,mm1,cs;
		cs=0;
        byte temp[],temp1[],temp0[];
		try
		{
			temp=id.getBytes("GBK");
		}
		catch (Exception e)
		{
			id=null;
			return id;
		}
		ii=temp.length;
        if (ii<1)
        {
            return id;
        }
		temp1=new byte[(int)(ii+1)];//返回加密字符串
		temp0=new byte[(int)(ii-1)];//返回解密字符串
        if (flag==1)
        {
	        mm1=(long)(Math.random()*43);
//			mmid=zff.charAt((int)(mm-1));  修改前
			mmid=zff1.charAt((int)mm1);
			mm=(long)zff.indexOf(mmid);
			mm++; //修改后新添加的
			temp1[0]=(byte)mmid;
        }
        else
        {
//	        mm=zff.indexOf((char)temp[0],1)+1;  修改前
			mm=zff.indexOf((char)temp[0])+1;
//	        id=mid(id,2,id.length());
			for (int p=0;p<temp.length-1 ;p++)
			{
				temp[p]=temp[p+1];
				temp[p+1]=0;
			}
	        ii=ii -1;
        }
        for (i=0;i<ii;i++)
        {
            zf=temp[(int)i];
	        j=zf;
	        if (j<0)
            {
				zf=(byte)(zf+128);
            }
			if (j>127)
			{
				zf=(byte)(zf-128);
			}
//	        m=zff.indexOf((char)zf,1);  修改前
	        m=zff.indexOf((char)zf);
			mm=mm*127+13+cs;
		    while(mm>62)
            {
                mm=mm -63;
            }
	        if (m>=0 && m<64)
            {
                m=f_add_mode2(m,mm,8);
                zf=(byte)zff.charAt((int)m);
            }
	        if (j<0)
            {
//              zf=(char)((int)zf+128);
//				zf=(byte)(-zf);
				zf=(byte)(zf+128);
            }
			if (j>127)
			{
				zf=(byte)(zf+128);
			}
			temp[(int)i]=zf;
//	         mmid=mmid+zf;
        }
		if (flag==1)
		{
			for (int p=0;p<temp.length ;p++ )
			{
				temp1[p+1]=temp[p];
			}
			return (new String(temp1));
		}
		else
		{
			for (int p=0;p<temp.length-1 ;p++ )
			{
				temp0[p]=temp[p];
			}
			return (new String(temp0));
		}
    }

	//两个长整型变量之间的逻辑加
	private static long f_add_mode2(long id1,long id2,long cd)
	{
		long i,ii,i1,i2,k,kk;
		i1=id1;
		i2=id2;
		k=0;
		kk=1;
		for (i=1;i<=cd;i++)
		{
			ii=i1 -(int)(i1/2)*2 + i2 -(int)(i2/2)*2;
			i1=i1/2;
			i2=i2/2;
			if (ii==1)
			{
				k=k+kk;
			}
			kk=kk*2;
		}
	       return k;
	}

	public static String getDatetime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
		return date;
	}

}
