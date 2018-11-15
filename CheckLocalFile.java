package DNSRelay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CheckLocalFile {
	//use map to store the local database 
	public static Map<String, String> ipTable = new HashMap<String, String>();
	public static long fileSize;
	public static void readDB(String path) throws IOException
	{
		String line = "";
		File f = new File(path);
		fileSize = f.length();
		if(!f.exists())
		{
			System.out.println("File doesn't exist!");
		}
		else
		{
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			//read file data into map in the format "ipAddr  ipDomainName"
			while((line=br.readLine())!=null)
			{
				String[] ip = line.split(" ");
				String ipAddr = ip[0];
				String ipDomainName = ip[1];
				ipTable.put(ipDomainName,ipAddr);
			}
			fis.close();
			isr.close();
			br.close();
			
		}
	}
}
