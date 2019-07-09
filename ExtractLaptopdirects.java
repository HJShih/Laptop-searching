import java.io.File;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
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

public class ExtractLaptopdirects {

	public static void main(String[] args) throws Exception {
       Date dateStart = new Date();	        
	   System.out.println("Start: " + dateStart.toString());
	   ParsingLaptopDirect();
	   Date dateEnd = new Date();	        
	   System.out.println("End: " + dateEnd.toString());
	}

	public static void ParsingLaptopDirect() throws Exception {

		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/laptop",  // URL of local database
				"root", // user name
				"opo229"); // password
        
		String retail = null;
		ArrayList<String> productInfoList = new ArrayList<String>();
		ArrayList<String> brandList = new ArrayList<String>();
		ArrayList<BigDecimal> panelList = new ArrayList<BigDecimal>();
		ArrayList<String> OSList = new ArrayList<String>();
		ArrayList<String> memList = new ArrayList<String>();
		ArrayList<String> hddList = new ArrayList<String>();
		ArrayList<String> colourList = new ArrayList<String>();
		ArrayList<String> cpuList = new ArrayList<String>();
		ArrayList<BigDecimal> priceList = new ArrayList<BigDecimal>();
		ArrayList<String> productLinkList = new ArrayList<String>();

		ArrayList<String> pageList = new ArrayList<String>();
		String firstLink = "http://www.laptopsdirect.co.uk/ct/laptops-and-netbooks/laptops?srt=0";
		pageList.add(firstLink);
		int id = 0; // id for database
		int total = 0;
		
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
	
		/***for WWW*********************************************************************************/		
		// calculate the number of pages
/*		URL urlF = new URL(firstLink);
		Document DocF = Jsoup.parse(urlF, 20000);
		Elements numPage = DocF.select("div.sli_numresults");
		String numPageS = numPage.get(0).text();
		String[] numPageParts = numPageS.split("of");
		String numS = numPageParts[1];
		numS = numS.replaceFirst("^ *", "");
		double num = Math.ceil((Double.parseDouble(numS)) / 12);
		// System.out.println(num);
	
		while (pageList.size() <= num) {
			String parsePage = pageList.get(pageList.size() - 1);
			System.out.println(parsePage);	
			URL url = new URL(parsePage);
	     	//URL url = new URL("http://www.laptopsdirect.co.uk/ct/laptops-and-netbooks/laptops?srt=444");
			Document Doc = Jsoup.parse(url, 20000);
*/
		/***for evaluation**************************************************************************/
		while (pageList.size() <= 5) {
			String parseURL = pageList.get(pageList.size() - 1);
			String parsePage = parseURL.replaceAll("http://www.laptopsdirect.co.uk/ct/laptops-and-netbooks/","");
			//System.out.println(parsePage);
			parsePage = parsePage.replace("?","");
			System.out.println(parsePage);
			File input = new File("tmp/Laptopsdirect_temp/"+parsePage+".html");
			Document Doc = Jsoup.parse(input, "UTF-8", "http://www.laptopsdirect.co.uk/ct/laptops-and-netbooks/");
			
			//extract retail
			Elements retailTag = Doc.getElementsByTag("title");
			String retailS = retailTag.get(0).text();
			String[] retailSSplit = retailS.split(" ");
			retail = retailSSplit[6];

			Elements productInfos = Doc.getElementsByClass("fwsli-productinfo"); // access model name
			total = total +productInfos.size();
			System.out.println("Total: "+total);		
			String Info = null;
			for (Element productInfo : productInfos) {

				// extract whole product information
				Info = productInfo.text();
				Info = Info.replaceAll("inch", "INCH");
				Info = Info.replaceAll("\"", "INCH");
				Info = Info.replaceAll("Inch", "INCH");
				Info = Info.replaceAll("A1", "");
				Info = Info.replaceAll("A2", "");
				Info = Info.replaceAll("T1", "");
				Info = Info.replaceAll("T2", "");
				Info = Info.replaceAll("T3", "");
				Info = Info.replaceAll("New", "");
				Info = Info.replaceAll("Refurbished", "");
				Info = Info.replaceAll("refurbished", "");
				Info = Info.replaceAll("Graded", "");
				Info = Info.replaceAll("Grade", "");
				Info = Info.replaceAll("GRADE", "");
				Info = Info.replaceAll("Del", "Dell");
				Info = Info.replaceAll("Delll", "Dell");
				Info = Info.replaceAll("Fujistu", "Fujitsu");
				Info = Info.replaceAll("Pre-Onwed", "Pre-Owned");
				Info = Info.replaceAll("PREOWNED", "Pre-Owned");
				Info = Info.replaceAll("Pre-Owned", "");
				Info = Info.replaceAll("Hewlett PAckard", "HP");
				Info = Info.replaceAll("Hewlett Packard", "HP");
				Info = Info.replaceAll("brand new box damaged", "");
				Info = Info.replaceAll("- As new but box opened -", "");
				Info = Info.replaceAll("CF-52", "Panasonic");
				Info = Info.replaceAll("CF-D1", "Panasonic");
				Info = Info.replaceFirst("^ *", ""); // remove first space in the string
				productInfoList.add(Info);
				//System.out.println(Info);

				// extract brand
				String[] brandSplit = Info.split(" ");
				String brand = brandSplit[0];
				if (brand.equals("Packard")||brand.equals("PC")){
					brand = brandSplit[0] + " " + brandSplit[1];					
				}
				if (brand.equals("Hi-")){
					brand = brandSplit[0] + "Grade";	
				}
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
	
				Elements productDetail = productInfo.getElementsByTag("li");               			
				
				// extract panel_size
				String panelSize = null;
				//Float panelSizeF = (float) (0);	
				BigDecimal panel = new BigDecimal(0);
				if (productDetail.size() != 0) {
					// extract screen size
					if (productDetail.size() >= 1) {
						panelSize = productDetail.get(0).text();
						String[] panelSizeSplit = panelSize.split("Size: ");

						if (panelSizeSplit.length >= 2) {
							panelSize = panelSize.replaceAll("Screen Size: ", "");
							panelSize = panelSize.replaceAll("in", "");
							//panelSizeF = Float.parseFloat(panelSize);
							panel = (BigDecimal) decimalFormatPanel.parse(panelSize);

						} 
						//System.out.println("panelSize A: "+ panelSizeF);
					}
				}	
				else{				
					//panel
			        String[] panelSplit = Info.split("INCH");
			        String panelPart = null;
			        if(panelSplit.length >1){
			        panelPart = panelSplit[0];
			        String [] panelSplit2 = panelPart.split(" ");
			        String panelPart2 = panelSplit2[panelSplit2.length-1];
			        //panelSizeF = Float.parseFloat(panelPart2);
					panel = (BigDecimal) decimalFormatPanel.parse(panelSize);

			        }
				}
				panelList.add(panel);
			}
			
			// access price
			Elements productPrices = Doc.select("div.priceandavailability");
            //BigDecimal price = new BigDecimal(0);
			
			for (int i = 0; i <= productPrices.size() - 1; i++) {
	            BigDecimal price = new BigDecimal(0);
				Elements priceAltImg = productPrices.get(i).getElementsByTag("img"); // access the tag "img"
				String priceAlt = priceAltImg.get(0).attr("alt"); // access the attribute "alt"
				StringBuilder priceBuilder = new StringBuilder(priceAlt); // remove "?"mark
				priceBuilder = priceBuilder.deleteCharAt(0);
				String priceS = priceBuilder.toString();
                price = (BigDecimal) decimalFormatPanel.parse(priceS);
				//String price = priceBuilder.toString();
				//float priceF = Float.parseFloat(price);
				priceList.add(price);
				// System.out.println(priceF);
			}
			
			// access link of pages
			Elements sli_pagination = Doc.getElementsByClass("sli_pagination");
			Elements hrefs = sli_pagination.select("a[href]");
			ArrayList<String> linkList = new ArrayList<String>();
			String page_link = null;

			for (Element href : hrefs) {
				String link = href.attr("href");
				linkList.add(link);
				// System.out.println(link);
			}
			int sizeLink = hrefs.size();
			page_link = linkList.get(sizeLink - 1);
			pageList.add(page_link);
			// System.out.println(page_link);
			
			//enter to the page of more detail
			Elements hrefDetails = productInfos.select("a[onmouseover*=return st(this)]");
			int size = hrefDetails.size(); // number of laptop can insert into database
			
			for (int i = 0; i < size; i++) {
				String link = hrefDetails.get(i).attr("href");
				productLinkList.add(link);
				
				String colour = null;			
				String os = null;
				String cpu = null;
				String mem = null;
				String hdd=null;
				//System.out.println("page:" +link);
			    /***for WWW*********************************************************************************/							
/*			    URL urlDetail = new URL(link);
				//URL urlDetail = new URL("http://www.laptopsdirect.co.uk/Panasonic_CF-31_MK4_Performance_Win7_-_BASE_UNIT_Water_and_dust_resistant_CF-31XEUAXCE/version.asp");
				//check URL exist
				HttpURLConnection HttpURLConnection = (HttpURLConnection) urlDetail.openConnection();
				int statusCode = HttpURLConnection.getResponseCode();
								
				if (statusCode != 404 ) {
				Document DocDetail = Jsoup.parse(urlDetail, 20000);
				//System.out.println("DocDetail:" +DocDetail);
*/
				/***for evaluation**************************************************************************/
				String linkDetail = link.replaceAll("http://www.laptopsdirect.co.uk/", "");
				linkDetail = linkDetail.replaceAll("/version.asp", "");
				File inputDetail = new File("tmp/Laptopsdirect_temp/moredetail/"+linkDetail+".html");
				Document DocDetail = Jsoup.parse(inputDetail, "UTF-8", "http://www.laptopsdirect.co.uk/");

				Element fullDetails = DocDetail.getElementById("TechSpec");
				Elements title = DocDetail.select("span[itemprop*=name]");
				String titleS = null;
				if(title.size()>0){
				titleS = title.get(0).text();
				
				Elements detail = fullDetails.select("span");
				String detailS = detail.text();
				//System.out.println("detailS:" +detailS);
				
				//colour	
				String[] colourSplit =  detailS.split("Colour of product");
				if(colourSplit.length>1){
					colour  =  colourSplit[1];
					//System.out.println(colour);
					colour = colour.replace("\""," ");
					colour = colour.replace("/"," ");
					colour = colour.replaceFirst("^ *", "");
					String[] colourSplit2 = colour.split(" ");
					colour = colourSplit2[0];
					//System.out.println(colour);
					colour = colour.replace(","," ");	
					colour = colour.replaceFirst("^ *", "");
					colour = colour.replace("Data", "");
					colour = colour.replace("Black ", "Black");
				}
				if(colour!=null){
					colour = colour.replace("white", "White");
					colour = colour.replace("SILVER", "Silver");
					colour = colour.replace("silver", "Silver");
					colour = colour.replace("black", "Black");
				}
				colourList.add(colour);
				//System.out.println(colour);
				
				//OS
				//String os = null;
				String[] osSplit = detailS.split("Operating system provided");
				if(osSplit.length>1){
					os = osSplit[1];
					os = os.replaceFirst("^ *", "");
					String[] osSplit2 = os.split(" ");
					os = osSplit2[0] + " " +  osSplit2[1];		
					os = os.replaceFirst("Optical", "");
				}
			    if (osSplit.length<=1||os.equals("Optical Drive")||os.equals("Intel Optical")){
					String[] osSplit3 = titleS.split("Windows");
					if(osSplit3.length>1){
						os = osSplit3[1];
						os = os.replaceFirst("^ *", "");
						String[] osSplit4 = os.split(" ");
						os = "Windows"+" "+osSplit4[0];
					}
				//System.out.println(os);
				}

				//CPU
				String cpuModel = null;
				String cpuFamily = null;
				String[] cpuSplit = detailS.split("Processor Model");
				String[] cpuSplit3 = detailS.split("Processor family");
				if(cpuSplit.length >1 ){
					String cpuPart = cpuSplit[1];
					cpuPart = cpuPart.replaceFirst("^ *", "");
					String[] cpuSplit2 = cpuPart.split(" ");
					 cpuModel = cpuSplit2[0];					
				}
				if(cpuSplit3.length>1){
					String cpuPart2 = cpuSplit3[1];
					cpuPart2 = cpuPart2.replaceFirst("^ *", "");
					String[] cpuSplit4 = cpuPart2.split(" ");
					 cpuFamily = cpuSplit4[0]+" " +cpuSplit4[1];					
				}
				if(cpu==null){
					String[] cpuSplit5 = titleS.split("Core");
					if(cpuSplit5.length>1){
						String cpuPart5 = cpuSplit5[1];
						cpuPart5 = cpuPart5.replaceFirst("^ *", "");
						String[] cpuSplit6 =  cpuPart5.split("");
						cpu= "Core" +" "+cpuSplit6[0];
					}

				}
				cpu = cpuModel+" "+cpuFamily;
				cpu = cpu.replaceAll("null null", "null");
				cpu = cpu.replaceAll("Warranty", "");
				cpu = cpu.replaceAll("USB", "");
				cpu = cpu.replaceAll("Type", "");
				cpu = cpu.replaceAll("Solid-state", "");
				
				//mem
				//String mem = null;
				String[] memSplit = detailS.split("Internal memory");
				if(memSplit.length>1){
					mem = memSplit[1];
					mem = mem.replaceFirst("^ *", "");
					String[] memSplit2 = mem.split(" ");
					mem = memSplit2[0]+memSplit2[1];
				if(mem.equals("accessspeed")||!mem.substring(0,1).matches("[0-9]")){
					String[] memSplit3 = titleS.split("GB");
					if(memSplit3.length>1){
						String memPart = memSplit3[0];
						String[] memSplit4 = memPart.split(" ");
						if (memSplit4.length>1){
							mem = memSplit4[memSplit4.length-1]+"GB";
						}
					}
					else{
						mem =null;
					}
				}
				}
				else{
					String[] memSplit3 = titleS.split("GB");
					if(memSplit3.length>1){
						String memPart = memSplit3[0];
						String[] memSplit4 = memPart.split(" ");
						if (memSplit4.length>1){
							mem = memSplit4[memSplit4.length-1]+"GB";
							String[] memSplit5= mem.split("/");
							if (memSplit5.length>1){
								mem = memSplit5[1];
							}
							if (mem.equals("500GB")){
								mem =  memSplit4[memSplit4.length-2]+"B";
							}
						}
					}
				}
				
				//hdd
				//String hdd=null;
				String[] hddSplit = detailS.split("Hard drive capacity");
				if(hddSplit.length>1){
					hdd = hddSplit[1];
					hdd = hdd.replaceFirst("^ *", "");
					String[] hddSplit2 = hdd.split(" ");
					hdd = hddSplit2[0]+hddSplit2[1];
				}
				else{ // no tech spec
					String[] hddSplit3 = titleS.split("TB");
					if(hddSplit3.length>1){
						String hddPart = hddSplit3[0];
						hddPart = hddPart.replaceFirst("^ *", "");
						String[]  hddSplit4 =hddPart.split(" ");
						hdd = hddSplit4[hddSplit4.length-1] + "TB";
					}
					else{
						String[] hddSplit5 = titleS.split("GB");
						if (hddSplit5.length==2){
							hdd = hddSplit5[0];
							hdd = hdd.replaceFirst("^ *", "");
							String[] hddSplit7 = hdd.split(" ");
							hdd = hddSplit7[hddSplit7.length-1]+"GB";
						}
						if(hddSplit5.length==3){
							hdd = hddSplit5[1] + "GB";
							hdd = hdd.replaceFirst("RAM", "");
							hdd = hdd.replaceFirst("-", "");
							hdd = hdd.replaceFirst("/", "");
							hdd = hdd.replaceFirst("1.7GHz", "");
							hdd = hdd.replaceFirst("DDR3", "");
							hdd = hdd.replaceFirst("^ *", "");

                      if(hddSplit5.length>3){
                        	String hddPart2 = hddSplit5[2];
                        	hddPart2 = hddPart2.replaceFirst("^ *", "");
                        	String[] hddSplit6 =hddPart2.split(" ");
                        	hdd = hddSplit6[1]+"GB";
                        }

						}
					}
				}

			}
//for WWW	}			
				colourList.add(colour);
				OSList.add(os);
				cpuList.add(cpu);
				memList.add(mem);
                hddList.add(hdd);
		}
			
			System.out.println("retail: " + retail);
			//System.out.println("model: " + productInfoList);
			System.out.println("brand: " + brandList);
			System.out.println("panel: " + panelList);
			System.out.println("colour: " + colourList);
			// System.out.println("detail:"+detailList);
			System.out.println("OS:" + OSList);
			System.out.println("CPU:" + cpuList);
			System.out.println("mem:" + memList);
			// System.out.println("graphic:"+graList);
			System.out.println("hdd:" + hddList);
			System.out.println("price:" + priceList);
			System.out.println("-----------------------------------------------");

		}
		
		// insert extracted data in database
		Statement statement = conn.createStatement();
		statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		int flag = 0;
		int productsSize = brandList.size();
		String resetFlag = "UPDATE extract_data set Flag=0 "; //initial flag = 0 
    	statement.executeUpdate(resetFlag);
    	
    	String setFlag = "UPDATE extract_data set Flag=1 WHERE retail = 'PCWorld' OR retail = 'Argos' OR retail = 'Misco' OR retail = 'Amazon'"; //set Flag = 1 when other retails' data already existed in database
    	statement.executeUpdate(setFlag);
    			
		for (int i = 0; i < productsSize; i++) {
			String brand = brandList.get(i);
			BigDecimal panel = panelList.get(i);
			String colour = colourList.get(i);
			String CPU = cpuList.get(i);
			String OS = OSList.get(i);
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
		if(column>0){	  //check the number of laptop with flag =0 are more than one
		String deleteInexistentLaptop = "DELETE FROM extract_data WHERE Flag=0";
	    statement.executeUpdate(deleteInexistentLaptop);	    
		System.out.println("Data of inexistent laptop is removed.");    	
		}
		}
	}
}
