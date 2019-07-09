import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractAmazon {
	public static void main(String[] args) throws Exception {
	       Date dateStart = new Date();	        
	       System.out.println("Start: " + dateStart.toString());
	       ParsingAmazon();
	       Date dateEnd = new Date();	        
	       System.out.println("End: " + dateEnd.toString());
	}

	public static void ParsingAmazon() throws Exception {

		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/laptop", // URL of local mySQL
				"root", // user name
				"opo229"); // password

		String retail = null;
		int size = 0; // number of laptops per web page
		ArrayList<String> brandList = new ArrayList<String>();
		ArrayList<String> osList = new ArrayList<String>();
		ArrayList<BigDecimal> panelList = new ArrayList<BigDecimal>();
		ArrayList<String> cpuList = new ArrayList<String>();
		ArrayList<String> memList = new ArrayList<String>();
		ArrayList<String> hddList = new ArrayList<String>();
		ArrayList<String> colourList = new ArrayList<String>();
		ArrayList<BigDecimal> priceList = new ArrayList<BigDecimal>();
		ArrayList<String> productLinkList = new ArrayList<String>();
		
		ArrayList<String> pageList = new ArrayList<String>();
		String firstLink = "http://www.amazon.com/s/ref=sr_pg_1?rh=n%3A172282%2Cn%3A%21493964%2Cn%3A541966%2Cn%3A565108%2Cp_n_condition-type%3A2224371011%2Cp_72%3A1248880011%2Cp_n_availability%3A1248800011&bbn=565108&suppress-ve=1&ie=UTF8&qid=1438785579&lo=none";
		pageList.add(firstLink);
		int id = 0; // id for database

		// set decimal format for price and panel_size
		// Ref.
		// http://stackoverflow.com/questions/18231802/how-can-i-parse-a-string-to-bigdecimal
		DecimalFormatSymbols DecimalFormatSymbols = new DecimalFormatSymbols();
		DecimalFormatSymbols.setGroupingSeparator(',');
		DecimalFormatSymbols.setDecimalSeparator('.');
		String format = "####.##";
		String formatPanel = "##.#";
		DecimalFormat decimalFormat = new DecimalFormat(format, DecimalFormatSymbols);
		DecimalFormat decimalFormatPanel = new DecimalFormat(formatPanel, DecimalFormatSymbols);
		decimalFormat.setParseBigDecimal(true);
		decimalFormatPanel.setParseBigDecimal(true);
		
		/***for WWW*********************************************************************************/		
		// calculate the number of pages
/*		URL urlF = new URL(firstLink);
		Document DocF = Jsoup.parse(urlF, 50000);
		Elements numPage = DocF.select("div.s-first-column");
		String numPageS = numPage.get(0).text();
		String[] numPageSsplit = numPageS.split(" ");
		String numPageSs = numPageSsplit[2];
		numPageSs=numPageSs.replace(",", "");
		double num = Math.ceil((Double.parseDouble(numPageSs)) / 24); // 24 laptops per web page
				
		while (pageList.size() <= num) {
*/
		/***for evaluation**************************************************************************/
		while (pageList.size() <= 2) {
		
		    /***for WWW*********************************************************************************/
/*			String parsePage = pageList.get(pageList.size() - 1);
			//System.out.println("parsePage: "+parsePage);	
			URL url = new URL(parsePage);
			System.out.println("parsePage: "+parsePage);	

			//URL url = new URL(" ");
			Document Doc = Jsoup.parse(url, 50000);
*/			
			/***for evaluation**************************************************************************/
			String parseURL = pageList.get(pageList.size() - 1);
			//System.out.println("parseURL:"+parseURL);
			String[] parseURLSplit =  parseURL.split("\\?");
			String parsePage = parseURLSplit[0];
			//System.out.println(parseURL);
			parsePage = parsePage.replaceAll("http://www.amazon.com/s/ref=","");
			System.out.println("parsePage:"+parsePage);
			File input = new File("tmp/Amazon_temp/"+parsePage+".html");
			Document Doc = Jsoup.parse(input, "UTF-8", "");
			
			//extract name of retail
			Elements retailName = Doc.select("div.nav-left");
			String retailPart = retailName.get(0).text();
			String[] retailPartSplit = retailPart.split(" ");
			retail = retailPartSplit[0];
			//System.out.println("retail: "+retail);	
			
			// access link of next page 
			Elements sli_pagination = Doc.getElementsByClass("pagnNext");
			Elements nexthrefs = sli_pagination.select("a[href]");			
			//ArrayList<String> linkList = new ArrayList<String>();
			String page_link = nexthrefs.get(0).attr("href");
			page_link = "http://www.amazon.com"+page_link;
			//System.out.println("page_link:"+page_link );
			pageList.add(page_link);
			
			//extract more detail page
			Elements moredetail = Doc.select("div.a-row");
			Elements hrefs = moredetail.select("a[class*=a-link-normal s-access-detail-page  a-text-normal]");
            int hrefsSize = hrefs.size();     
			//System.out.println("moredetail: "+hrefsSize);	
            
			for (int i = 0; i < hrefsSize; i++){
				
		     /***for WWW*********************************************************************************/													
           
 /*         String link = hrefs.get(i).attr("href"); // get the link of more detail of page
			 productLinkList.add(link);
			URL urlDetail = new URL(link);			
			Document DocDetail = Jsoup.parse(urlDetail, 500000);
*/			         
			/***for evaluation**************************************************************************/
			String link = hrefs.get(i).attr("href"); // get the link of more detail of page
			 productLinkList.add(link);
			 
			String[] linkSplit = link.split("/");
			String linkDetail = linkSplit[5];
			//System.out.println("link: "+link);	
			//System.out.println("linkDetail: "+linkDetail);	
			File inputDetail = new File("tmp/Amazon_temp/moredetail/"+linkDetail+".html");
			Document DocDetail = Jsoup.parse(inputDetail, "UTF-8", "");
			
			
			//extract brand
			String brand = null;
			Element titleSection= DocDetail.getElementById("titleSection");
			String title = titleSection.text();
			title = title.replaceAll("2015","");
			title = title.replaceAll("Newest","");
			title = title.replaceAll("Model","");
			title = title.replaceFirst("^ *", "");
			String[] titleSplit = title.split(" ");
			brand = titleSplit[0];
			if (brand!=null){
				brand = brand.replaceAll("ASUS", "Asus");
				brand = brand.replaceAll("APPLE", "Apple");
				brand = brand.replaceAll("ACER", "Acer");
				brand = brand.replaceAll("LENOVO", "Lenovo");
				brand = brand.replaceAll("MICROSOFE", "Microsoft");
				brand = brand.replaceAll("MacBook", "Apple");
				brand = brand.replaceAll("TOSHIBA", "Toshiba");
				brand = brand.replaceAll("MICROSOFT", "Microsoft");
			}
			brandList.add(brand);
			//System.out.println("brand:"+brand);
            
			//extract price
			BigDecimal price = new BigDecimal(0);
			String priceS = null;
			Element priceblock_ourprice = DocDetail.getElementById("priceblock_ourprice");
			if (priceblock_ourprice!=null){
			priceS = priceblock_ourprice.text();
			priceS = priceS.replace("$", "");
			priceS = priceS.replace(",", "");
			//System.out.println("priceS:"+priceS);
            //price = (BigDecimal) decimalFormatPanel.parse(priceS);           
            price = (BigDecimal) decimalFormatPanel.parse(priceS);
			}
			else{
				Elements rightPrice = DocDetail.select("span.a-size-medium.a-color-price");
				priceS = rightPrice.get(0).text();
				priceS = priceS.replace("$", "");
				priceS = priceS.replace(",", "");
				price = (BigDecimal) decimalFormatPanel.parse(priceS);
				//System.out.println("price:"+price);
			}
            priceList.add(price);
			
			Elements pdTabs = DocDetail.select("div.pdTab"); // extract summary table and other technical detail table
			
			BigDecimal panel = new BigDecimal(0);
			String cpu = null;
			String mem = null;
			String hdd = null;
			String os = null;
			String colour = null;

			
			if (pdTabs.size() > 0){
			//extract summary in more detail page for panel/cpu/mem/hdd
			Element summaryTable = pdTabs.get(0);
			String summary = summaryTable.text();
			
			// extract panel
			//BigDecimal panel = new BigDecimal(0);
			String panelS = null;
			String[] sumSplitPanel = summary.split("Screen Size ");
			if(sumSplitPanel.length>1){
			   String panelPart = sumSplitPanel[1];
			   String[] panelPartSplit = panelPart.split(" ");
			   panelS = panelPartSplit[0];
			   panel = (BigDecimal) decimalFormatPanel.parse(panelS);
			}
			//panelList.add(panel);
			//System.out.println("panel:"+panel);
			
			//extract cpu
			//String cpu = null;
			String[] sumSplitCpu = summary.split("Processor ");
			if (sumSplitCpu.length >1){
				String cpuPart = sumSplitCpu[1];
				String[] cpuPartSplitRAM = cpuPart.split("RAM");
				cpu = cpuPartSplitRAM[0];
			}
			//cpuList.add(cpu);
			//System.out.println("cpu:"+cpu);
            
			//extract memory
			//String mem = null;
			String[] sumSplitMem = summary.split("RAM ");
			if (sumSplitMem.length>1){
				String memPart = sumSplitMem[1];
				String[] memPartSplit = memPart.split(" ");
				mem = memPartSplit[0]+memPartSplit[1];
			}
			//memList.add(mem);
			//System.out.println("mem:"+mem);
			
			//extract HDD
			//String hdd = null;
			String[] sumSplitHdd = summary.split("Hard Drive ");
			if (sumSplitHdd.length>1){
				String hddPart = sumSplitHdd[1];
				String[] hddPartSplit = hddPart.split(" ");
				hdd = hddPartSplit[0]+hddPartSplit[1];
				hdd = hdd.replaceAll("Solid", "");
			}
			//hddList.add(hdd);
			//System.out.println(hdd);
			
			
			//extract other technical detail in more detail page for colour/OS
			Element otherTechTable = pdTabs.get(1);
			String otherTech = otherTechTable.text();
			//System.out.println(otherTech);
			
			//extract os
			//String os = null;
			String[] sumSplitOs = otherTech.split("Operating System ");
			if (sumSplitOs.length >1){
				String osPart = sumSplitOs[1];
				//System.out.println("os:"+osPart );
				String[] osPartSplit = osPart.split(" ");				
				os = osPartSplit[0]+" "+osPartSplit[1];
				os = os.replace(";", "");
				if (osPartSplit[1].equals("Item")){
					os = osPartSplit[0];
				}
			}
			//osList.add(os);
			//System.out.println("os:"+os );
			
			//extract colour
			//String colour = null;
			String[] sumSplitColour = otherTech.split("Color ");
			if (sumSplitColour.length>1){
				String colourPart = sumSplitColour[1];
				String[] colourSplitWeight = colourPart.split(" Processor Brand");
				colour = colourSplitWeight[0];
				if(colour.length()>20){
					String[] colourSplit = colour.split(" ");
					colour = colourSplit[0];
				}
			}
			if(colour!=null){
				colour = colour.replace("white", "White");
				colour = colour.replace("SILVER", "Silver");
				colour = colour.replace("silver", "Silver");
				colour = colour.replace("black", "Black");
				colour = colour.replace("Dark grey", "Dark Grey");

			}
			//colourList.add(colour);
			//System.out.println("colour:"+colour );
			}
			panelList.add(panel);
			cpuList.add(cpu);
			memList.add(mem);
			hddList.add(hdd);
			osList.add(os);
			colourList.add(colour);

			}
			
			//TimeUnit.SECONDS.sleep(3);
			System.out.println("retail: " + retail);
			System.out.println("brand: " + brandList);
			System.out.println("panel: " + panelList);
			System.out.println("colour: " + colourList);
			System.out.println("OS:" + osList);
			System.out.println("CPU:" + cpuList);
			System.out.println("mem:" + memList);			// System.out.println("graphic:"+graList);
			System.out.println("hdd:" + hddList);
			System.out.println("price:" + priceList);
			System.out.println("-----------------------------------------------");
		}

		// insert extracted data in database
		Statement statement = conn.createStatement();
		statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		int flag = 0;
		int productsSize = brandList.size();
		System.out.println("productsSize: "+productsSize);
		String resetFlag = "UPDATE extract_data set Flag=0 "; //initial flag = 0 
    	statement.executeUpdate(resetFlag);
    	
    	String setFlag = "UPDATE extract_data set Flag=1 WHERE retail = 'PCWorld' OR retail = 'Argos' OR retail = 'Laptopsdirect' OR retail = 'Misco'"; //set Flag = 1 when other retails' data already existed in database
    	statement.executeUpdate(setFlag);
    			
		for (int i = 0; i < productsSize; i++) {
			String brand = brandList.get(i);
			BigDecimal panel = panelList.get(i);
			String colour = colourList.get(i);
			String CPU = cpuList.get(i);
			String OS = osList.get(i);
			String mem = memList.get(i);
			String hdd = hddList.get(i);
			BigDecimal price = priceList.get(i);
			String productLink = productLinkList.get(i);

			//without select price
			String queryCheck =" SELECT  id, retail as retail, brand as brand, panel_size as panel_size, colour as colour, OS as OS, CPU as CPU, memory as memory, "
					+ "price as price, hdd as hdd FROM extract_data WHERE  retail = '" + retail + "' AND brand='" + brand + "' AND colour='" + colour + "' "
							+ "AND OS='" + OS + "' AND CPU='" + CPU + "' AND memory='" + mem + "'  AND hdd='" + hdd + "' AND panel_size=" + panel +" AND productlink='" + productLink +"'";

			ResultSet rs = statement.executeQuery(queryCheck);
            if (rs.next()){         
            	int dataId = rs.getInt("id");           	
            	BigDecimal dataPrice = rs.getBigDecimal("price");
            	String selectID = "SELECT * FROM extract_data WHERE id="+dataId+"";
            	
            	ResultSet resultUpdate = statement.executeQuery(selectID);  // when laptop exists, flag = 1  
            	resultUpdate.last();
            	resultUpdate.updateInt("Flag", 1);
            	resultUpdate.updateRow(); 
            	System.out.println("ID"+dataId+":"+"Data is existed.");       

            	if(!dataPrice.equals(price)){ // when current price of laptop is different with existed laptop, the system will update the newest price      
            		resultUpdate.last(); 
            		resultUpdate.updateBigDecimal("price", price);                 
            		resultUpdate.updateRow(); 
                System.out.println("ID"+dataId+":"+" Update price.");
            	}
                   }	
			else{
				flag =1;
			    String insertData = "INSERT INTO extract_data " + "VALUES ('" + flag + "', " + id + ",'" + retail + "', '" + brand + "','" + panel + "',"
						+ "'" + colour + "','" + OS + "', '" + CPU + "', '" + mem + "', '" + hdd + "','" + price + "','" + productLink + "')";
				statement.executeUpdate(insertData);				
				System.out.println("Insert new data of laptop in database");
			}
		}
    	
    	// delete the raw with flag = 0
		String selectFlag = "SELECT COUNT(*),id FROM extract_data WHERE Flag=0"; 
		ResultSet rsFlag = statement.executeQuery(selectFlag);
		int column = 0 ;		
		if(rsFlag.next()){
			column =  rsFlag.getInt(1);
			//System.out.println("column."+column);
						
		if(column>0){	  //check the number of laptop with flag =0 are more than one
		String deleteInexistentLaptop = "DELETE FROM extract_data WHERE Flag=0";
	    statement.executeUpdate(deleteInexistentLaptop);	    
		System.out.println("Data of inexistent laptop is removed.");    	
		}
		}
	}
}
