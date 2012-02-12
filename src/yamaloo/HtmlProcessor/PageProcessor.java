package yamaloo.HtmlProcessor;
import java.io.File;

import yamaloo.HtmlProcessor.SinglePageProcessor;
public class PageProcessor{
	private Config config = new Config();
	private String brandpath = "";
	private SinglePageProcessor singlepage = new SinglePageProcessor();
	public String setBrandPath(){
		brandpath = brandpath + config.getSitePath() + config.getCurrentBrand() + "\\";
		return brandpath;
	}
	public void batchPages(String path){
		File dir = new File(path); 
        File[] files = dir.listFiles(); 
        if (files == null) 
            return; 
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
            	batchPages(files[i].getAbsolutePath()); 
            }
            else
            { 
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                System.out.println("---"+strFileName);
//                filelist.add(files[i].getAbsolutePath());
                if(sendSingleTask(strFileName))
                	singlepage.downLoadPic(singlepage.pro.PictureUrl, singlepage.pro.getMD5ofPicUrl());
            } 
        } 
	}
	public boolean sendSingleTask(String filePath){
		if(singlepage.excuteProcess(filePath))
		{
			System.out.println("Product Name: " + singlepage.pro.ProductName + "\r\nBrand: " + singlepage.pro.Brand);
			return true;
		}
		else
		{
			return false;
		}
	}
}