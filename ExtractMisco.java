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
import org.jsoup.select.Elements;

public class ExtractMisco {
	public static void main(String[] args) throws Exception {
	       Date dateStart = new Date();	        
	       System.out.println("Start: " + dateStart.toString());
	       ParsingMisco();
	       Date dateEnd = new Date();	        
	       System.out.println("End: " + dateEnd.toString());
	}
	
	public static void ParsingMisco() throws Exception {

		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/laptop", // URL of local mySQL
				"root", // user name
				"opo229"); // password

		String retail = null;
		int size = 0; //number of laptops per web page
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
		String firstLink = "http://www.misco.co.uk/Cat/Computers/Laptops?Items=20&Viewtype=list&page=1";
		pageList.add(firstLink);
		int id = 0; // id for database
		int total =0;
		
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
		Document DocF = Jsoup.parse(urlF, 50000);
		Elements numPage = DocF.select("div.NumberOfItemsContainer");
		String numPageS = numPage.get(0).text();
		numPageS = numPageS.replaceFirst("Items Found: ", "");
		double num = Math.ceil((Double.parseDouble(numPageS)) / 20);
		//System.out.println(num);
	
		while (pageList.size() <= num) {
*/
		/***for evaluation**************************************************************************/
		while (pageList.size() <= 3) {

		    /***for WWW*********************************************************************************/
/*			String parsePage = pageList.get(pageList.size() - 1);
			//System.out.println("parsePage: "+parsePage);	
			URL url = new URL(parsePage);
			System.out.println("parsePage: "+parsePage);	

			//URL url = new URL("http://www.laptopsdirect.co.uk/ct/laptops-and-netbooks/laptops?srt=444");
			Document Doc = Jsoup.parse(url, 50000);
*/			
			/***for evaluation**************************************************************************/
			String parseURL = pageList.get(pageList.size() - 1);
			//System.out.println(parseURL);
			String parsePage = parseURL.replaceAll("http://www.misco.co.uk/Cat/Computers/Laptops\\?Items=20&Viewtype=list&","");
			System.out.println(parsePage);
			File input = new File("tmp/Misco_temp/"+parsePage+".html");
			Document Doc = Jsoup.parse(input, "UTF-8", "");
					
			// retail name
			Elements titles = Doc.select("title");
			String titleS = titles.get(0).text();
			String[] titleSplit = titleS.split(" - ");
			String retailAll = titleSplit[2];		
			String[] retailSplit = retailAll.split("\\.");
			retail = retailSplit[0];
			//System.out.println(retailAll);
			
			// access link of next page 
			Elements sli_pagination = Doc.getElementsByClass("PageNavigation_NextPageText");
			Elements nexthrefs = sli_pagination.select("a[href]");
			//ArrayList<String> linkList = new ArrayList<String>();
			String page_link = nexthrefs.get(1).attr("href");
			pageList.add(page_link);
			//System.out.println(page_link);
			
			//extract the link of more detail pages
			Elements ProductListingHeaders = Doc.select("div.ProductListingHeaders");
			Elements hrefs = ProductListingHeaders.select("a[class*=ProductHeaderBlue b]");
		    size= hrefs.size();
		    total = total+ size;
		    System.out.println("Total: "+total);

			for (int i = 0; i < size; i++){
							    
			     /***for WWW*********************************************************************************/									
/*				String link = hrefs.get(i).attr("href"); // get the link of more detail of page
				String linkFull = "http://www.misco.co.uk"+link;
				 productLinkList.add("http://www.misco.co.uk/"+link);
				//System.out.println(linkFull);
				URL urlDetail = new URL(linkFull);
				//URL urlDetail = new URL("http://www.argos.co.uk/static/Product/partNumber/4183765.htm");
				Document DocDetail = Jsoup.parse(urlDetail, 50000);
*/			         
				/***for evaluation**************************************************************************/
				 String link = hrefs.get(i).attr("href"); // get the link of more detail of page
				 productLinkList.add("http://www.misco.co.uk/"+link);

				 //System.out.println(link);
				 String[] linkSplit = link.split("/");
				 String linkDetail = linkSplit[2];	         
		         //System.out.println("linkDetail:"+linkDetail);			        
				 
						File inputDetail = new File("tmp/Misco_temp/moredetail/"+linkDetail+".html");
						Document DocDetail = Jsoup.parse(inputDetail, "UTF-8", "");
					
						//extract brand
						Elements titleDetail = DocDetail.select("h1.seo");
						String titleDetailS = titleDetail.get(0).text();
						String[] titleDetailSSplit = titleDetailS.split(" ");
						String brand = titleDetailSSplit[0];
						//System.out.println(brand);
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
						
						//extract spec in more detail page
						Elements specTable = DocDetail.select("table.MarketingSpecificationTable");
						String spec = specTable.text();
						//System.out.println(specTable.text());
						
						//extract OS
						String os =null;
						String[] specSplitOS = spec.split("Operating System");
						
						if (specSplitOS.length > 1){
							String OSPart = specSplitOS[1];
							String[] OSPartSplitCPU = OSPart.split("Processor");
							os = OSPartSplitCPU[0];	
							os = os.replaceFirst("^ *", "");
						}
						osList.add(os);						
						//System.out.println(os);
						
						//extract cpu
						String cpu = null;
						String[] specSplitCPU = spec.split("Processor");
						
						if (specSplitCPU.length>1){
							String cpuPart = specSplitCPU[1];
							String[] cpuPartSplitMEM = cpuPart.split("Memory");
                            cpu = cpuPartSplitMEM[0];
                            cpu = cpu.replaceFirst("^ *", "");						
						}
						cpuList.add(cpu);
						//System.out.println(cpu);
						
						//extract mem
						String mem = null;
						String[] specSplitMem = spec.split("Memory");
						if (specSplitMem.length>1){
							String memPart = specSplitMem[1];
							String[] memPartSplitHdd = memPart.split("Storage");
							String memPart2 = memPartSplitHdd[0];
							memPart2 = memPart2.replaceFirst("^ *", "");	
							String[] memSplit = memPart2.split(" ");
							mem = memSplit[0]+memSplit[1];
							//System.out.println(mem.length());
							String[] memSplit1 = mem.split("1");
							if(mem.length()>9&&memSplit1.length>1){
								mem = memSplit[0];
							}
						}
						
						memList.add(mem);
						//System.out.println(mem);
						
						//extract HDD
						String hdd = null;
						String[] specSplitHdd =  spec.split("Storage");
						if (specSplitHdd.length>1){
							String hddPart = specSplitHdd[1];
							String[] hddPartSplit = hddPart.split(" ");						
							hdd = hddPartSplit[1]+hddPartSplit[2];
							hdd = hdd.replaceFirst("PCIe", "");	
							String[] hddSplitBrackets = hdd.split("\\)");
							if (hddSplitBrackets.length>1){
								hdd=hddSplitBrackets[1];
							}
						
						}
                        hddList.add(hdd);
						//System.out.println(hdd);
					
						
						//extract panel_size
						BigDecimal panel = new BigDecimal(0);
						String panelS =null;
						String[] specSplitDisplay =  spec.split("Display");
						if(specSplitDisplay.length>1){
							String panelPart = specSplitDisplay[1];
							String[] panelSplit = panelPart.split(" ");
							panelS = panelSplit[1];
							panelS = panelS.replaceAll("\"", "");
							panel = (BigDecimal) decimalFormatPanel.parse(panelS);
						}
						panelList.add(panel);
						//System.out.println(panelS);
						
						//extract colour
						String colour = null;
						String[] specSplitColour = spec.split("Colour");
						if (specSplitColour.length>1){
							String colourPart = specSplitColour[1];
							String[] colourSplit = colourPart.split(" ");
							colour = colourSplit[1];
							colour = colour.replace(",", "");
							if (colour.equals("Graphite")){
								colour = colourSplit[1]+" "+colourSplit[2];
							}
						}
						if(colour!=null){
							colour = colour.replace("white", "White");
							colour = colour.replace("SILVER", "Silver");
							colour = colour.replace("silver", "Silver");
							colour = colour.replace("black", "Black");
						}
                        colourList.add(colour);
						//System.out.println(colour);
                        
                        //extract price
                        BigDecimal price = null;
                        String priceS = null;
                        Elements priceClass = DocDetail.select("span[class*=size20 b]");
                        priceS = priceClass.get(0).text();
                        priceS = priceS.replaceAll("£", "");
                        priceS = priceS.replaceAll(",", "");
                        price = (BigDecimal) decimalFormatPanel.parse(priceS);
                        priceList.add(price);
                        //System.out.println(price);
			}
	
			
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
		String resetFlag = "UPDATE extract_data set Flag=0 "; //initial flag = 0 
    	statement.executeUpdate(resetFlag);
    	//set Flag = 1 when other retails' data already existed in database
    	String setFlag = "UPDATE extract_data set Flag=1 WHERE retail = 'PCWorld' OR retail = 'Argos' OR retail = 'Laptopsdirect' OR retail = 'Amazon'"; 
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
