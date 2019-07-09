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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractPCworld {

	public static void main(String[] args) throws Exception {
	       Date dateStart = new Date();	//record start time        
	       System.out.println("Start: " + dateStart.toString());
	       ParsingPCWorld();
	       Date dateEnd = new Date(); //record end time 	        
	       System.out.println("End: " + dateEnd.toString());
	}

	public static void ParsingPCWorld() throws Exception {

		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/laptop", // URL of local database
				"root", // user name
				"opo229"); // password

		ArrayList<String> modelNameList = new ArrayList<String>();
		ArrayList<String> brandList = new ArrayList<String>();
		ArrayList<BigDecimal> panelList = new ArrayList<BigDecimal>();
		ArrayList<String> colourList = new ArrayList<String>();
		ArrayList<String> OSList = new ArrayList<String>();
		ArrayList<String> cpuList = new ArrayList<String>();
		ArrayList<String> memList = new ArrayList<String>();
		ArrayList<String> hddList = new ArrayList<String>();
		ArrayList<BigDecimal> priceList = new ArrayList<BigDecimal>();
		ArrayList<String> pageList = new ArrayList<String>();
		ArrayList<String> productLinkList = new ArrayList<String>();
		String retail = null;
		int id = 0; // id for database
		int totalLap = 0;
		
		// set decimal format for price and panel_size; use BigDecimal to avoid inaccuracy type such as float
		  //Ref. http://stackoverflow.com/questions/18231802/how-can-i-parse-a-string-to-bigdecimal
		DecimalFormatSymbols DecimalFormatSymbols = new DecimalFormatSymbols();
		DecimalFormatSymbols.setGroupingSeparator(',');
		DecimalFormatSymbols.setDecimalSeparator('.');
		String format = "####.##";
		String formatPanel = "##.#";
		DecimalFormat decimalFormat = new DecimalFormat(format, DecimalFormatSymbols);
		DecimalFormat decimalFormatPanel = new DecimalFormat(formatPanel, DecimalFormatSymbols);			
		decimalFormat.setParseBigDecimal(true);
		decimalFormatPanel.setParseBigDecimal(true);
	
		String firstLink = "http://www.pcworld.co.uk/gbuk/computing/laptops/laptops/703_7006_70006_xx_xx/1_20/relevance-desc/xx-criteria.html";	
		pageList.add(firstLink);
		
	    /***for WWW*********************************************************************************/				
		// calculate the number of pages
/*		URL urlF = new URL(firstLink);		
		Document DocF = Jsoup.parse(urlF, 10000);
		Elements numPage = DocF.select("strong");
		String numPageS = numPage.get(0).text();
		String[] numPageParts = numPageS.split(" ");
		String numS = numPageParts[5];
		double num = Math.ceil((Double.parseDouble(numS)) / 20);
	    System.out.println(num);
					
		// the number of pages in the Web-site
		while (pageList.size() <= num) {
*/		
		/***for evaluation**************************************************************************/
		while (pageList.size() <= 3) { 

		    /***for WWW*********************************************************************************/
/*			String parsePage = pageList.get(pageList.size() - 1); // get the link with "next page"
			URL url = new URL(parsePage);
			Document Doc = Jsoup.parse(url, 20000);
*/									
			/***for evaluation**************************************************************************/
			String parseURL = pageList.get(pageList.size() - 1);
			String parsePage = parseURL.replaceAll("http://www.pcworld.co.uk/gbuk/computing/laptops/laptops/703_7006_70006_xx_xx/","");
			File input = new File("tmp/PCWorld_temp/"+parsePage);
			Document Doc = Jsoup.parse(input, "UTF-8", "http://www.pcworld.co.uk/gbuk/computing/laptops/laptops/");
				
			// access retail's name
			Elements retailStrings = Doc.getElementsByTag("title");
			String retailString = retailStrings.get(0).text();
			String[] partRetails = retailString.split("|");// divide info by "|"
		    retail = partRetails[35] + partRetails[36] + partRetails[38] + partRetails[39] + partRetails[40] + partRetails[41]
				+ partRetails[42];
		    
			Elements productTitles = Doc.getElementsByClass("productTitle"); // access model name

			for (Element title : productTitles) {
				String titleName = title.text();
				titleName = titleName.replaceAll("”", "\"");
				modelNameList.add(titleName);
				// System.out.println(titleName);

				// access brand
				String[] partbrands = titleName.split(" "); // divide info by space
				String brand = partbrands[0]; // access the first string
				
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
			}
			
			// Extract price
			Elements productPrices = Doc.getElementsByClass("price"); // access
																		// price
			for (Element price : productPrices) {
				String amount = price.text();		 
				amount = amount.replace("£", "");
				amount = amount.replace(",", "");			
				BigDecimal a = new BigDecimal(0);// store the BigDecimal type price	
				if (amount == "") {
					a = new BigDecimal(0);
				}
				else {
					a = (BigDecimal) decimalFormat.parse(amount);
				}
				priceList.add(a);
			}
			
			// store all links which are at the bottom of web page
			ArrayList<String> linkList = new ArrayList<String>();
			Elements links = Doc.getElementsByClass("pagination");
			String page_link = null;
			for (Element alink : links) {
				Elements hrefs = alink.select("a[href]");

				for (Element href : hrefs) {
					String subPage = href.attr("href");
					linkList.add(subPage);
				}

				int sizeLink = hrefs.size();
				page_link = linkList.get(sizeLink - 1); // access every web-site's the last page link to the deeper extract data
				linkList.add(page_link);
				pageList.add(page_link);
			}
			
			//access detail of laptop
			Elements desc = Doc.select("a.desc");
			Elements hrefsubs = Doc.select("a[class*=desc]");
			totalLap= totalLap+hrefsubs.size();
		    System.out.println("sub: "+hrefsubs.size());
		    System.out.println("total: "+totalLap);
            			 
			//enter to product page
			for (int k = 0; desc.size() > k; k++) {
				
				String link = hrefsubs.get(k).attr("href"); 
				productLinkList.add(link);
			    /***for WWW*********************************************************************************/			
				//System.out.println("link: "+link);
				//URL urlDetail = new URL(link);
				//Document DocDetail = Jsoup.parse(urlDetail, 20000);
				
				/***for evaluation**************************************************************************/
				String linkDetail = link.replaceAll("http://www.pcworld.co.uk/gbuk/computing/laptops/laptops/", "");				
				File inputDetail = new File("tmp/PCWorld_temp/"+linkDetail);
				Document DocDetail = Jsoup.parse(inputDetail, "UTF-8", "http://www.pcworld.co.uk/gbuk/computing/laptops/laptops/");
								
				Element fullDetails = DocDetail.getElementById("tab2");
				Elements selectD = fullDetails.select("td");
	
				//extract OS
				String os = selectD.get(1).text();
				os = os.replaceFirst("-", "");
				os = os.replaceFirst("^ *", "");
				os = os.replaceFirst("32-bit", "32bit");
				os = os.replaceFirst("64-bit", "64bit");
				String[] osSplit = os.split("-");
				os = osSplit[0];
				OSList.add(os);
				//System.out.println("os: "+os);
				
				//extract cpu
				String cpu = selectD.get(2).text();
				cpu = cpu.replaceFirst("-", "");
				cpu = cpu.replaceFirst("^ *", "");
				cpuList.add(cpu);
				
				//extract mem
				String mem = selectD.get(3).text();
				mem = mem.replaceAll("GB", "");
				String[] memSplit = mem.split(" ");	
				if(mem.substring(0, 1).matches("[0-9]")){
					//mem = memSplit[1]+"GB";
					
				int memCheck = Integer.parseInt(memSplit[0]);
				if (memCheck > 32){
				mem = selectD.get(4).text();
				memSplit = mem.split(" ");
				}						
				mem = memSplit[0]+"GB";
				}
				else{
					mem = memSplit[1]+"GB";
				}
				mem.replaceAll("DDR3L", "");
				
				memList.add(mem);
				//System.out.println("mem:"+mem);
				
				
				Elements selectTr = fullDetails.select("tr");
				String selectTrS = selectTr.text();
				
				//extract storage
				String[] selectStorage = selectTrS.split("Storage");
				String hdd = null;
				//System.out.println("selectDSSplit:"+selectDSSplit);
				if(selectStorage.length >1){
				String hddPart = selectStorage[1];				
				hddPart = hddPart.replaceFirst("-", "");
				hddPart = hddPart.replaceFirst("^ *", "");
				String[] hddPartSplit = hddPart.split(" ");
				hdd = hddPartSplit[0]+hddPartSplit[1];
				hdd = hdd.replace(",", "");
				}			
				hddList.add(hdd);
				//System.out.println("hdd:"+hdd);
				
				//extract colour
				String[] selectColour = selectTrS.split(" Colour");
				String colour = null;
				if(selectColour.length >1){
					colour = selectColour[1];
					String[] selectBox = colour.split(" Box");
					colour = selectBox[0];
					colour = colour.replaceFirst("^ *", "");
					colour = colour.replaceFirst("&", "");
					String[] colourSplit = colour.split(" Accessories");
					if(colourSplit.length>1){
						colour = colourSplit[0];
					}
					
				}
				if(colour!=null){
					colour = colour.replace("white", "White");
					colour = colour.replace("SILVER", "Silver");
					colour = colour.replace("silver", "Silver");
					colour = colour.replace("black", "Black");
				}
				colourList.add(colour);
				//System.out.println("colour:"+colour);
								
				//extract panel size
				String[] selectPanel = selectTrS.split("Screen size");
				String panelS = null;
				//Float panelF = (float)(0);
				 BigDecimal panelD = new BigDecimal(0);
				if(selectColour.length >1){
				   panelS=selectPanel[1];
				   String[] selectSType = panelS.split("Screen type");
				   panelS = selectSType[0];
				   panelS = panelS.replaceAll("\"", "");
				   panelS = panelS.replaceAll("”","");
				   panelS = panelS.replaceFirst("^ *", "");
					//System.out.println("panelS:"+panelS);
                   
				   //panelF = Float.parseFloat(panelS);
				   panelD = (BigDecimal) decimalFormatPanel.parse(panelS);
				}				
				panelList.add(panelD);
				//System.out.println("panelD:"+panelD);
			}
		
			System.out.println("retail: " + retail);
			System.out.println("brand: " + brandList);
			System.out.println("panel: " + panelList);
			System.out.println("colour: " + colourList);
			System.out.println("OS:" + OSList);
			System.out.println("CPU:" + cpuList);
			System.out.println("mem:" + memList);
			System.out.println("hdd:" + hddList);
			System.out.println("price:" + priceList);
			System.out.println("URL:" + productLinkList);
			System.out.println("-------------------------------------------------------------------------------");

		}
		
		
		
		// insert extracted data in database
		int size = modelNameList.size();
		Statement statement = conn.createStatement();
		statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
		int flag = 0;
		String resetFlag = "UPDATE extract_data set Flag=0 "; //initial flag as 0 
    	statement.executeUpdate(resetFlag);
    	
    	String setFlag = "UPDATE extract_data set Flag=1 WHERE retail = 'Misco' OR retail = 'Argos' OR retail = 'Laptopsdirect' OR retail = 'Amazon'"; //set Flag = 1 when other retails' data already existed in database
    	statement.executeUpdate(setFlag);
		
		for (int i = 0; i < size; i++) {
			//int flag = 0;
			String brand = brandList.get(i);
			BigDecimal panel = panelList.get(i);
			String colour = colourList.get(i);
			String OS = OSList.get(i);
			String CPU = cpuList.get(i);
			String mem = memList.get(i);
			String hdd = hddList.get(i);
			BigDecimal price = priceList.get(i);
			String productLink = productLinkList.get(i);
				
			//without select price
			String queryCheck =" SELECT  id, retail as retail, brand as brand, panel_size as panel_size, colour as colour, OS as OS, CPU as CPU, memory as memory, "
					+ "price as price, hdd as hdd FROM extract_data WHERE  retail = '" + retail + "' AND brand='" + brand + "' AND colour='" + colour + "' "
							+ "AND OS='" + OS + "' AND CPU='" + CPU + "' AND memory='" + mem + "'  AND hdd='" + hdd + "' AND panel_size='" + panel +"' AND productlink='" + productLink +"'";

			ResultSet rs = statement.executeQuery(queryCheck);
			//System.out.println(rs.getString("brand"));
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
