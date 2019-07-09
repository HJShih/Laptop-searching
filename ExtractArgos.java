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

public class ExtractArgos {
	public static void main(String[] args) throws Exception {
	       Date dateStart = new Date();	        
	       System.out.println("Start: " + dateStart.toString());
	       ParsingArgos();
	       Date dateEnd = new Date();	        
	       System.out.println("End: " + dateEnd.toString());		
	}

	public static void ParsingArgos() throws Exception {
		
		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/laptop", // URL of local database
				"root", // user name
				"opo229"); // password

		int id = 0; // id for database
		
	    /***for WWW*********************************************************************************/				
/*		//String firstLink = "http://www.argos.co.uk/static/Browse/c_1/1%7Ccategory_root%7CTechnology%7C33006169/c_2/2%7C33006169%7CLaptops+and+PCs%7C33007795/c_3/3%7Ccat_33007795%7CLaptops+and+netbooks%7C33014243/fs/0/p/1/pp/50/s/Relevance.htm";
		String firstLink = "http://www.argos.co.uk/static/Browse/c_1/1%7Ccategory_root%7CTechnology%7C33006169/c_2/2%7C33006169%7CLaptops+and+PCs%7C33007795/c_3/3%7Ccat_33007795%7CLaptops+and+netbooks%7C33014243/fs/0/p/1/pp/Show+all/s/Relevance.htm";
		URL url = new URL(firstLink);
		Document Doc = Jsoup.parse(url, 50000);
*/
		/***for evaluation**************************************************************************/
		File input = new File("tmp/Argos_temp/Relevance.htm");
		Document Doc = Jsoup.parse(input, "UTF-8", "http://www.argos.co.uk/static/Browse/c_1/1%7Ccategory_root%7CTechnology%7C33006169/c_2/2%7C33006169%7CLaptops+and+PCs%7C33007795/c_3/3%7Ccat_33007795%7CLaptops+and+netbooks%7C33014243/fs/0/p/1/pp/Show+all/s/");
	
		// Extract retail name
		Elements retailTitle = Doc.getElementsByTag("title");
		String retailText = retailTitle.get(0).text();
		String[] retailSplit = retailText.split(" ");
	    String retailAll =  retailSplit[5];
	    String[] retailAllSplit = retailAll.split("\\.");
	    String retail = retailAllSplit[0];
	
		//access model name
		Elements ProductListingHeaders = Doc.select("dt.title");

		ArrayList<String> brandList = new ArrayList<String>();
		ArrayList<BigDecimal> panelList = new ArrayList<BigDecimal>();
		ArrayList<String> memList = new ArrayList<String>();
		ArrayList<String> hddList = new ArrayList<String>();
		ArrayList<String> colourList = new ArrayList<String>();
		ArrayList<String> productLinkList = new ArrayList<String>();
		ArrayList<BigDecimal> priceList = new ArrayList<BigDecimal>();

		
		// set decimal format for price and panel_size
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
		 
        
		int laptopNum = ProductListingHeaders.size();
		//System.out.println(laptopNum);
	    /***for WWW*********************************************************************************/						
/*		for (int i=0;i < laptopNum-3; i++) {
		for (Element ProductListingHeader : ProductListingHeaders) {
*/		
		int numbers =laptopNum-106;
		System.out.println("Total: "+numbers);
		for (int i=0;i < laptopNum-106; i++) { //extract 20 products
		
			// extract whole product information
			String Info = ProductListingHeaders.get(i).text();
			Info = Info.replaceAll("\"", "");
			Info = Info.replaceAll("\''", "");
			Info = Info.replaceAll("inch", "Inch");
			Info = Info.replaceAll("Hewlett Packard", "HP");
			Info = Info.replaceAll("15-r219na15.6", "15-r219na 15.6");
			Info = Info.replaceAll("15.6'", "15.6");

			// extract brand
			String[] brandSplit = Info.split(" ");
			String brand = brandSplit[0];
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

			// extract screen size
			String panel = null;
			BigDecimal panelS = new BigDecimal(0);

			String[] panelSplit = Info.split("Inch");
			// String[] panelSplit3 = Info.split("\\.");

			if (panelSplit.length >= 2) {
				String panelpart = panelSplit[0];
				String[] panelSplit2 = panelpart.split(" ");
				panel = panelSplit2[panelSplit2.length - 1];
				// System.out.println(panel);
				panelS = (BigDecimal) decimalFormatPanel.parse(panel);
				//panelS = Float.parseFloat(panel);
				// System.out.println("panel:" + panel);
			}
			else {
				String[] panelSplit3 = Info.split("\\.");
				if (panelSplit3.length >= 2) {
					String panelpart2 = panelSplit3[0];
					String panelpart3 = panelSplit3[1];
					// System.out.println("P3:" + panelpart3);
					String[] panelSplit4 = panelpart2.split(" ");
					String[] panelSplit5 = panelpart3.split(" ");
					panel = panelSplit4[panelSplit4.length - 1] + "." + panelSplit5[0];
					//panelS = Float.parseFloat(panel);
					
					panelS = (BigDecimal) decimalFormat.parse(panel);
				}

			}
			panelList.add(panelS);
			// System.out.println(panelS);

			// extract MEM
			String[] memSplit = Info.split("GB");
			String mem = null;
			int memSplitSize = memSplit.length;
			if (memSplitSize >= 2) {
				String memPart = memSplit[0];
				String[] memSplit2 = memPart.split(" ");
				mem = memSplit2[memSplit2.length - 1] + "GB";
			}
			memList.add(mem);
			// System.out.println(mem);

			// extract HDD
			String hdd = null;
			String[] hddSplit = Info.split("GB");

			if (hddSplit.length >= 3) {
				hdd = hddSplit[1] + "GB";
				hdd=hdd.replaceFirst("^ *", "");

			}
			if (hddSplit.length < 3 && hddSplit.length >= 2) {
				String[] hddSplit2 = hddSplit[1].split("TB");
				if (hddSplit2.length >= 2) {
					hdd = hddSplit2[0] + "TB";
					hdd=hdd.replaceFirst("^ *", "");
				}
			}
			hddList.add(hdd);
			// System.out.println(hdd);
			
			
			//extract colour
			String[] colourSplit = Info.split("-");
			String colour = null;
			if(colourSplit.length >= 2 ){
				if (colourSplit.length > 2 ){
					colour = colourSplit[2];
					//colour = colour.replaceFirst("^ *", "");
					//colour = colour.replace("\\.", "");
					if (colour.substring(0, 1).matches("[0-9]")||colour.length() >8){
						colour = null;
					}
				}
				else{
				colour = colourSplit[1];
				 if (colour.substring(0, 1).matches("[0-9]")||colour.length() >8){
					colour = null;
				}
				}
				
				//System.out.println("length:"+colour.length());
				//System.out.println("A: "+colour);
			}
			else {
			    String[] colourSplit2 = Info.split(" ");
			    colour = colourSplit2[colourSplit2.length-2];
			    //colour = colour.replaceFirst("^ *", "");
				//colour = colour.replace("\\.", "");
			    if (colour.substring(0, 1).matches("[0-9]")||colour.length() >8){
					colour = null;
				}
			    //System.out.println("B: "+colour);
			}
			
			if (colour!=null&&colour.equals("Screen")){
				colour = null;
			}
			if (colour!=null){
			    colour = colour.replaceFirst("^ *", "");
				colour = colour.replace("\\.", "");
				colour = colour.replace("Blue.", "Blue");
			    colour = colour.replace("white", "White");
				colour = colour.replace("SILVER", "Silver");
				colour = colour.replace("silver", "Silver");
				colour = colour.replace("black", "Black");
				
			}
			colourList.add(colour);
			
			// extract price		
			Elements prices = Doc.select("span.main");


			for (Element price : prices) {
				String priceS = price.text();
				StringBuilder priceBuilder = new StringBuilder(priceS); // remove "?"mark
				priceBuilder = priceBuilder.deleteCharAt(0);
				priceS = priceBuilder.toString();
				  // panelD = (BigDecimal) decimalFormat.parse(panelS);

				BigDecimal priceF = (BigDecimal) decimalFormat.parse(priceS);
				priceList.add(priceF);
				// System.out.println(priceF);
			}
		}

		// extract CPU and OS by access product page
		// Elements hrefs = ProductListingHeaders.select("a[href]");
		Elements hrefs = Doc.select("a[title*=more details on]");
		ArrayList<String> cpuList = new ArrayList<String>();
		ArrayList<String> osList = new ArrayList<String>();
        //System.out.println("hrefs:"+hrefs.size());
		/***for WWW*********************************************************************************/			
/*	    for (int i = 0; i < hrefs.size()-6; i = i + 2) {	     		
		    String link = hrefs.get(i).attr("href"); // get the link of more detail of page
		    productLinkList.add(link);
			System.out.println(link);
			URL urlDetail = new URL(link);
			//URL urlDetail = new URL("http://www.argos.co.uk/static/Product/partNumber/4183765.htm");
			Document DocDetail = Jsoup.parse(urlDetail, 50000);
*/      
		 /***for evaluation*********************************************************************************/
		for (int i = 0; i < hrefs.size()-212; i = i + 2) { // the last product is at line 8067
			//System.out.println(hrefs.size()-180);

			String link = hrefs.get(i).attr("href"); // get the link of more detail of page
			//System.out.println(link);
			productLinkList.add(link);
			String parsePage = link.replaceAll("http://www.argos.co.uk/static/Product/partNumber/","");
			File moreDetial = new File("tmp/Argos_temp/moredetail/"+parsePage);
			//System.out.println(parsePage);
			Document DocDetail = Jsoup.parse(moreDetial, "UTF-8", "http://www.argos.co.uk/static/Product/partNumber/");

			
			Elements fullDetails = DocDetail.select("div.fullDetails");
			String cpu = null;
			String OS = null;

			if (fullDetails.size() != 0) {
				Elements detail = fullDetails.select("li");
				String testDig = null;
				if (detail.get(0).text().length() < 50) {
					cpu = detail.get(0).text();
				}
				else {
					testDig = detail.get(2).text();
					// System.out.println(testDig.length());
					if (testDig.substring(0, 1).matches("[0-9]")) {
						cpu = detail.get(1).text();
						// System.out.println("B: "+cpu)
					}
					else {
						if (testDig.length() < 80) {
							cpu = testDig;
						}
						else {
							String testDig2 = detail.get(7).text();
							if (testDig2.length() > 23) {
								cpu = testDig2;
							}
							else {
								cpu = detail.get(6).text();
							}
						}

					}
				}
			}

			else {
				Element product = DocDetail.getElementById("product");
				Elements productLi = product.select("li");
				// System.out.println(detail.get(0).text()); //
				// System.out.println(productLi.get(0).text().length());

				if (productLi.get(0).text().length() < 50) {
					cpu = productLi.get(0).text();
					// System.out.println("A: "+cpu);
				}
				else {
					String testDig3 = productLi.get(2).text();
					if (testDig3.substring(0, 1).matches("[0-9]")) {
						cpu = productLi.get(1).text();
					}
					else {
						cpu = testDig3;
					}
					// System.out.println("B: "+cpu);
				} //
					// System.out.println("BBBBBBBBBB: "+cpu);
			}
			// System.out.println(detail);
			cpu = cpu.replaceAll("\\.", "");
			// cpu = cpu.replaceAll("\\?", "");
			cpuList.add(cpu);
			// System.out.println("CPUCPUCPUCPU: "+cpu);

			
			// extract OS by "ul"					
			if (fullDetails.size() > 0) {
				Elements OSDetails = fullDetails.select("ul");
				if (OSDetails.size()>0){
					Element description = OSDetails.get(0);
					Elements descriptionAll = description.select("li");
					Element description_li0 = descriptionAll.get(0);
					String description_li0_S = description_li0.text();
					//System.out.println("description_li1_S:"+description_li1_S);
					//System.out.println("description_li1_S:"+description_li1_S.length());
						
					if(description_li0_S.length()>50){ //access from ul_1
					Element OSDetail = OSDetails.get(1); // access "CPU, Memory and Operating System:"
					Elements OSDetails2 = OSDetail.select("li");	
					Element OSDetail2 =null;
					
					if(OSDetails2.size()>4){
					 OSDetail2 = OSDetails2.get(4);
					 if(OSDetail2.text().equals("This device can be upgraded to Windows 10 for free.")){
						 OSDetail2 = OSDetails2.get(3);
					 }
					 
					}
					else {
						if(OSDetails2.size()>3){
						 OSDetail2 = OSDetails2.get(3);
						 if(OSDetail2.text().equals("This device can be upgraded to Windows 10 for free.")){
							 OSDetail2 = OSDetails2.get(2);
						 }
						}
					}
								
					if(OSDetail2!=null){
					OS = OSDetail2.text();
					
					String testRAM = OS;
					String testRAMSplit = testRAM.split(" ")[1];
					//System.out.println("testRAMSplit:"+testRAMSplit);
					testRAMSplit = testRAMSplit.replaceFirst("^ *", "");
					if (testRAMSplit.equals("RAM.")){
						Element OSMAC = OSDetails.get(7);
						Elements OSMACLi = OSMAC.select("li");
						OS = OSMACLi.get(0).text();
					}					
					}									
					}
					if(OS!=null){
						OS = OS.replace("//.", "");
					}			
					osList.add(OS);
				}				
				}				
			}
				
		System.out.println("Retail: "+ retail);
		System.out.println("Brand: "+brandList);
		System.out.println("Screen size: "+panelList);
		System.out.println("Memory: "+memList);
		System.out.println("HDD: "+hddList);
		System.out.println("price: "+priceList);
		System.out.println("CPU: "+cpuList);
		System.out.println("OS:" + osList);
		System.out.println("colour: " + colourList);
		System.out.println("-----------------------------------------------");
		
		
		// insert extracted data in database		
		int size = ProductListingHeaders.size()-3; // "-3" is used to avoid "what's hot" products
		// System.out.println(size);

		Statement statement = conn.createStatement();
		statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		int flag = 0;
		String resetFlag = "UPDATE extract_data set Flag=0 "; //initial flag = 0 
    	statement.executeUpdate(resetFlag);
    	
    	//set Flag = 1 when other retails' data already existed in database
    	String setFlag = "UPDATE extract_data set Flag=1 WHERE retail = 'PCWorld' OR retail = 'Misco' OR retail = 'Laptopsdirect' OR retail = 'Amazon'"; 
    	statement.executeUpdate(setFlag);
    	
	     /***for WWW*********************************************************************************/			
		//for (int i = 0; i < size; i++) {
	     /***for evaluation*********************************************************************************/			
    	for (int i = 0; i < size-103; i++) {
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
            	//idList.add(dataId);
            	
            	BigDecimal dataPrice = rs.getBigDecimal("price");
            	String selectID = "SELECT * FROM extract_data WHERE id="+dataId+"";
            	
            	ResultSet resultUpdate = statement.executeQuery(selectID);  // when laptop exists, flag = 1  
            	resultUpdate.last();
            	resultUpdate.updateInt("Flag", 1);
            	resultUpdate.updateRow(); 
            	//System.out.println("ID"+dataId+":"+"Data is existed.");
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
		if(column>0){	  //check the number of laptop with flag =0 are more than one
		String deleteInexistentLaptop = "DELETE FROM extract_data WHERE Flag=0";
	    statement.executeUpdate(deleteInexistentLaptop);	    
		System.out.println("Data of inexistent laptop is removed.");    	
		}
		}
	}
	
}
