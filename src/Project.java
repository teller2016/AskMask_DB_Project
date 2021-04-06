import java.io.*;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;

public class Project {

	private static String getTagValue(String tag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		if (nValue == null)
			return null;
		return nValue.getNodeValue();
	}

	public static void main(String[] args) throws SQLException, ParserConfigurationException {
		try {

			Scanner scan = new Scanner(System.in);
			//System.out.println("SQL Programming Test");
			System.out.println("Connecting PostgreSQL database");

			String url = "jdbc:postgresql://localhost:5432/postgres";
			String user = "postgres";
			String password = "nomorewaiting75";

			Connection connection = DriverManager.getConnection(url, user, password);

			if (connection != null) {
				System.out.println("연결 성공");
			} else {
				System.out.println("연결 실패");
			}

			Statement qry = connection.createStatement();

			qry.execute("drop table if exists Station");
			qry.execute("drop table if exists Congestion");
			qry.execute("drop table if exists PM");

			qry.execute("create table Station(line varchar(10), sName varchar(10), region varchar(10))");
			qry.execute(
					"create table Congestion(day varchar(10), line varchar(10), sName varchar(10), time6cong float, time12cong float, time18cong float )");
			qry.execute("create table PM(date bigint , region varchar(10), fineP int, ultP int)");

			/////////////////////////////////////////////////////////////////////////////////////////////// Station
			/////////////////////////////////////////////////////////////////////////////////////////////// 넣기
			List<List<String>> ret = new ArrayList<List<String>>();
			BufferedReader br = null;

			/*********************
			 * Station 데이터 넣기
			 *********************/
			try {
				br = Files.newBufferedReader(Paths.get("C:\\OS\\Station.csv"));
				// Charset.forName("UTF-8");
				String line = "";

				while ((line = br.readLine()) != null) {
					// CSV 1행을 저장하는 리스트
					List<String> tmpList = new ArrayList<String>();
					String array[] = line.split(",");
					// 배열에서 리스트 반환
					tmpList = Arrays.asList(array);
					// System.out.println(tmpList);
					ret.add(tmpList);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//System.out.println("Inserting tuples to Station relations");

			String insert_station = "insert into Station (line, sName, region) values(?,?,?);";
			PreparedStatement station = connection.prepareStatement(insert_station);

			for (int i = 1; i < ret.size(); i++) {
				for (int j = 0; j < ret.get(i).size(); j++) {
					// System.out.print(ret.get(i).get(j) + "\t");
					station.setString(j + 1, ret.get(i).get(j));

				}
				// System.out.println();
				station.executeUpdate();
			}

			/*PreparedStatement q1 = connection.prepareStatement("select * from Station");
			ResultSet res1 = q1.executeQuery();
			
			System.out.println("[line/sName/region]");
			while (res1.next()) {
				String line = res1.getString(1);
				String sname = res1.getString(2);
				String region = res1.getString(3);
				System.out.println(line + "/" + sname + "/" + region);
			}*/

			/***********************
			 * Congestion 데이터 넣기
			 **********************/

			List<List<String>> ret2 = new ArrayList<List<String>>();
			BufferedReader br2 = null;

			try {
				br2 = Files.newBufferedReader(Paths.get("C:\\OS\\Congestion.csv"));
				// Charset.forName("UTF-8");
				String line = "";

				while ((line = br2.readLine()) != null) {
					// CSV 1행을 저장하는 리스트
					List<String> tmpList = new ArrayList<String>();
					String array[] = line.split(",");
					// 배열에서 리스트 반환
					tmpList = Arrays.asList(array);
					// System.out.println(tmpList);
					ret2.add(tmpList);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br2 != null) {
						br2.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//System.out.println("Inserting tuples to Congestion relations");

			String insert_congestion = "insert into Congestion (day, line, sName,time6cong, time12cong, time18cong ) values(?,?,?,?,?,?);";
			PreparedStatement congestion = connection.prepareStatement(insert_congestion);

			for (int i = 1; i < ret2.size(); i++) {

				String day = ret2.get(i).get(0);
				String line = ret2.get(i).get(1);
				String sName = ret2.get(i).get(2);

				float time6cong = Float.parseFloat(ret2.get(i).get(3));
				float time12cong = Float.parseFloat(ret2.get(i).get(4));
				float time18cong = Float.parseFloat(ret2.get(i).get(5));

				congestion.setString(1, day);
				congestion.setString(2, line);
				congestion.setString(3, sName);
				congestion.setFloat(4, time6cong);
				congestion.setFloat(5, time12cong);
				congestion.setFloat(6, time18cong);

				congestion.executeUpdate();

			}

			/*PreparedStatement q2 = connection.prepareStatement("select * from Congestion");
			ResultSet res2 = q2.executeQuery();
			
			System.out.println("[day/line/sName/time6cong/time12cong/time18cong]");
			while (res2.next()) {
				String day = res2.getString(1);
				String line = res2.getString(2);
				String sName = res2.getString(3);
				float a1 = res2.getFloat(4);
				float a2 = res2.getFloat(5);
				float a3 = res2.getFloat(6);
			
				System.out.println(day + "/" + line + "/" + sName + "/" + a1 + "/" + a2 + "/" + a3);
			}*/

			/*********************
			 * PM 데이터 넣기
			 *********************/
			List<List<String>> ret3 = new ArrayList<List<String>>();
			BufferedReader br3 = null;
			
			try {
				br3 = Files.newBufferedReader(Paths.get("C:\\OS\\PM.csv"));
				// Charset.forName("UTF-8");
				String line = "";
			
				while ((line = br3.readLine()) != null) {
					// CSV 1행을 저장하는 리스트
					List<String> tmpList = new ArrayList<String>();
					String array[] = line.split(",");
			
					// 배열에서 리스트 반환
					tmpList = Arrays.asList(array);
					//System.out.println(tmpList);
					ret3.add(tmpList);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br3 != null) {
						br3.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//            String arrayz[] =(ret3.get(1).get(6)).split(" ");
			//System.out.println("Inserting tuples to Congestion relations");
			//            System.out.println(arrayz[0]);
			//            System.out.println(Integer.parseInt(ret3.get(1).get(6)));
			
			String insert_PM = "insert into PM (date, region, fineP, ultP) values(?,?,?,?);";
			PreparedStatement PM = connection.prepareStatement(insert_PM);
			
			for (int i = 1; i < ret3.size(); i++) {
			
				String arr0[] = ret3.get(i).get(0).split(" ");
			//                String arr1[] = ret3.get(i).get(6).split(" ");
			//                String arr2[] = ret3.get(i).get(7).split(" ");
			
				if (ret3.get(i).get(6) == "") {
			
				}
				int fineP = Integer.parseInt(ret3.get(i).get(6));
				int ultP = Integer.parseInt(ret3.get(i).get(7));
				long date = Long.parseLong(arr0[0]);
				String region = ret3.get(i).get(1);
			
			//                if(arr1[0] == ""){
			//                    fineP = 0;
			//                }else{
			//                    fineP = Integer.parseInt(arr1[0]);
			//                }
			
			//                if(arr2[0] == ""){
			//                    ultP = 0;
			//                }else{
			//                    ultP = Integer.parseInt(arr2[0]);
			//                }
			
			//                int ultP = Integer.parseInt(arr2[0]);
			
				PM.setLong(1, date);
				PM.setString(2, region);
				PM.setInt(3, fineP);
			
				PM.setInt(4, ultP);
			
				PM.executeUpdate();
			
			}
			
			/*PreparedStatement q3 = connection.prepareStatement("select * from PM");
			ResultSet res3 = q3.executeQuery();
			
			System.out.println("[date/region/fineP/ultP]");
			while (res3.next()) {
				long date = res3.getLong(1);
				String region = res3.getString(2);
			
				int fineP = res3.getInt(3);
				int ultP = res3.getInt(4);
			
				System.out.println(+date + "/" + region + "/" + fineP + "/" + ultP);
			}*/

			/******************************
			 * OpenAPI 가져오는 거
			 ***************************/

			/////////////////////////////////////////////////////////// 시간
			long systemTime = System.currentTimeMillis();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH", Locale.KOREA);

			String dTime = formatter.format(systemTime);
			int time = Integer.parseInt(dTime);
			time = time - 1;

			String new_dTime = Integer.toString(time);
			new_dTime = new_dTime + "00";
			//System.out.println(new_dTime);
			///////////////////////////////////////////////////////////// 시간구함 new_dTime

			String insert_pm = "insert into PM (date, region, fineP, ultP) values(?,?,?,?);";
			PreparedStatement pm = connection.prepareStatement(insert_pm);

			String APIurl = "http://openAPI.seoul.go.kr:8088/7344787a666a756e383453796f7461/xml/TimeAverageAirQuality/1/25/"
					+ new_dTime;

			DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();

			Document doc = dBuilder.parse(APIurl);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("row");
			//System.out.println("Root element : " + doc.getDocumentElement().getNodeName());

			//System.out.println("리스트 길이: " + nList.getLength());

			//System.out.println(nList);

			//System.out.println("##########CHECK#######");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					String msrdt = getTagValue("MSRDT", eElement);
					String msrste_nm = getTagValue("MSRSTE_NM", eElement);
					String pm10 = getTagValue("PM10", eElement);
					String pm25 = getTagValue("PM25", eElement);
					if(pm25==null)
						pm25="0";
					if(pm10==null)
						pm10="0";
					pm.setLong(1, Long.parseLong(msrdt));
					pm.setString(2, msrste_nm);
					pm.setInt(3, Integer.parseInt(pm10));
					pm.setInt(4, Integer.parseInt(pm25));

				}

				pm.executeUpdate();
			}

			/*PreparedStatement q4 = connection.prepareStatement("select * from PM");
			ResultSet res4 = q4.executeQuery();
			
			System.out.println("[date/region/fineP/ultP]");
			while (res4.next()) {
				long date = res4.getLong(1);
				String region = res4.getString(2);
				int finep = res4.getInt(3);
				int ultp = res4.getInt(4);
			
				System.out.println(date + "/" + region + "/" + finep + "/" + ultp);
			}*/
			
			
			
			/******************************
			 * 쿼리문 시작
			 ***************************/
			
			Scanner sc = scan;
			System.out.println("\n//////////////////////////////////////////\n"
					+ "////////////////ASK MASK//////////////////\n"
					+ "//////////////////////////////////////////\n");
			
			
			int inputInt;
			String currLine ;
			String curr_sName=""; //
			String currDay="";
			//int currTime;
			int scan_time = 0;
			String str_scan_time="";
		 	int ultP=100;
	        int fineP=20;
	        float cong = 80 ;
			
			//String[] Line = {"1","2","3","4","1호선","2호선","3호선","4호선"};
			
	        System.out.printf("<지하철 호선을 입력하세요>\n => ");
			currLine = sc.next();
			PreparedStatement A = connection.prepareStatement("select * from Station where line='"+currLine+"호선"+"'");
	        ResultSet A_res = A.executeQuery();
	         
	         System.out.printf("*****%s호선 지하철역 ( 역이름 / 지역 )*****\n",currLine);
	         int count = 0;
	         while (A_res.next()) {
	            //String line = A_res.getString(1);
	            String sname = A_res.getString(2);
	            String region = A_res.getString(3);
	            System.out.printf("%-30s","<"+ sname + "/" + region+">"); 
	            count++;
	            if(count==3)
	            {
	            	System.out.print("\n");
	            	count=0;
	            }
	         }
			System.out.printf("\n<역이름을 입력하세요>\n => ");
			curr_sName = sc.next();
			System.out.printf("<요일을 입력하세요 (평일/토요일/일요일)>\n => ");
			currDay = sc.next();
			
			SimpleDateFormat curtime_format = new SimpleDateFormat("HH", Locale.KOREA);
			String cur_time = curtime_format.format(systemTime);
			scan_time = Integer.parseInt(cur_time);
			System.out.printf("<현재 시간>\n => %d시",scan_time);
			
			/*System.out.printf("<시간을 입력하세요>\n => ");
		    scan_time = sc.nextInt();      // 몇시
*/		    
	        
	        while(true) {
	        	System.out.println("\n===================================");
				System.out.println("기능1: 혼잡도 조회");
				System.out.println("기능2: 미세먼지/초미세먼지 상태조회");
				System.out.println("기능3: 마스크 추천");
				System.out.println("기능4: 역 정보 변경");
				System.out.println("===================================");
				System.out.print("<원하는 기능을 선택하세요 >\n => ");
	        	
				inputInt = sc.nextInt();
				
				switch (inputInt) {
				case 1:
					
					scan_time = scan_time/6;
			         switch(scan_time) {
			         case 1: str_scan_time = "time6cong";
			               break;
			         case 2: str_scan_time="time12cong";
			         break;
			         
			         case 3: str_scan_time="time18cong";
			         break;
			         
			         default: System.out.println("시간 에러");
			         }
			         
			        
			         PreparedStatement B = connection.prepareStatement("select Station.line, Station.sname, region, "+str_scan_time+", day"
			                 + " from Congestion, Station"
			                 + " where Congestion.sName = Station.sName and Station.sName = '"+curr_sName+"' and Station.line = '"+currLine+"호선"+"'  and day = '"+currDay+"';");
			         
			         
			         
			       
			         ResultSet B_res = B.executeQuery();
			         
	
			         while (B_res.next()) {
			            String line = B_res.getString(1);
			            String sname = B_res.getString(2);
			            String region = B_res.getString(3);
			            cong = B_res.getFloat(4);
			            String day = B_res.getString(5);
			            //System.out.println(line + "/" + sname + "/" + region+"/"+cong+"/"+day);
			            System.out.println("\n===================================");
			            System.out.println("1호선 "+curr_sName+"역,"+region+" - "+day+"기준");
			            System.out.println("-----------------------------------");
			            System.out.println("혼잡도:"+cong+"%");
			            System.out.printf("상태:");
			            if(cong<=80) {
			                System.out.println("여유");
			             }else if(cong>80 && cong<=130){
			                System.out.println("보통");
			             }else if(cong>130 && cong<=150){
			                System.out.println("주의");
			             }else {
			                System.out.println("혼잡");
			             }
			         }
					
					break;
					
					
				case 2:
					PreparedStatement C = connection.prepareStatement("select line, sName, Station.region, fineP, ultP"
				               + " from Station, PM"
				               + " where Station.region=PM.region and line = '"+currLine+"호선' and sName = '"+curr_sName+"' and date = "+new_dTime);
				         
				         ResultSet C_res = C.executeQuery();
				         
				         //System.out.printf("*****%s역*****\n",curr_sName);
				         while (C_res.next()) {
				            String line = C_res.getString(1);
				            String sname = C_res.getString(2);
				            String region = C_res.getString(3);
				            fineP = C_res.getInt(4);
				            ultP = C_res.getInt(5);
				            //System.out.println(line + "/" + sname + "/" + region+"/"+fineP+"/"+ultP);
				         System.out.println("\n===================================");
				         System.out.println(line+" "+curr_sName+"역,"+region);
				         System.out.println("-----------------------------------");
				         System.out.print("미세먼지 : "+ fineP);
				         if(fineP<=30) {
				                System.out.println("(좋음)");
				             }else if(fineP>30 && fineP<=80){
				                System.out.println("(보통)");
				             }else if(fineP>80 && fineP<=150){
				                System.out.println("(나쁨)");
				             }else {
				                System.out.println("(매우나쁨)");
				             }
				         
				         System.out.print("초미세먼지 : "+ ultP);
				         if(ultP<=15) {
				                System.out.println("(좋음)");
				             }else if(ultP>15 && ultP<=35){
				                System.out.println("(보통)");
				             }else if(ultP>35 && ultP<=75){
				                System.out.println("(나쁨)");
				             }else {
				                System.out.println("(매우나쁨)");
				             }
				         }
				         
					break;
				case 3:

				        String state = "";

				        if(75 < ultP  || 150 < fineP|| (150 < cong) ){
				            state = "very_bad";
				        }
				        else if ((35 < ultP && ultP <= 75) || (80< fineP && fineP <= 150 ) || (150 < cong)){
				            state = "bad";
				        }
				        else if ((15 < ultP && ultP <= 35) || (30< fineP && fineP <= 80 ) || (130 < cong && cong <= 150)) {
				            state = "normal";
				        }
				        else if (0 < ultP && ultP < 35 || (0< fineP && fineP <=30 ) || (80 < cong && cong <= 130) ){
				            state = "good";
				        }

				        System.out.println("마스크 종류   :    비말 차단 마스크, KF 80, KF 94, KF 99");
				        System.out.println("미세입자 차단 :    비말 차단 마스크 < KF 80 < KF 94 < KF 99");
				        System.out.println("호흡 용이성   :    비말 차단 마스크 > KF 80 > KF 94 > KF 99\n");

				        System.out.printf("********%s역 추천 마스크********\n => ",curr_sName);
				        switch(state){
				        case "good":
				        System.out.println("비말 차단 마스크 이상");
				        break;

				        case "normal":
				        System.out.println("KF 80 이상");
				        break;

				        case "bad":
				        System.out.println("KF 94 이상");
				        break;

				        case "very_bad":
				        System.out.println("KF 99 이상");
				        break;

				    }
				        
				        
				        
				        
					break;
					
				case 4:
					System.out.printf("<지하철 호선을 입력하세요>\n => ");
					currLine = sc.next();
					A = connection.prepareStatement("select * from Station where line='"+currLine+"호선"+"'");
			        A_res = A.executeQuery();
			         
			         System.out.printf("*****%s호선 지하철역 ( 역이름 / 지역 )*****\n",currLine);
			         count = 0;
			         while (A_res.next()) {
			            //String line = A_res.getString(1);
			            String sname = A_res.getString(2);
			            String region = A_res.getString(3);
			            System.out.printf("%-30s","<"+ sname + "/" + region+">"); 
			            count++;
			            if(count==3)
			            {
			            	System.out.print("\n");
			            	count=0;
			            }
			         }
					System.out.printf("<역이름을 입력하세요>\n => ");
					curr_sName = sc.next();
					System.out.printf("<요일을 입력하세요 (평일/토요일/일요일)>\n => ");
					currDay = sc.next();
					
					cur_time = curtime_format.format(systemTime);
					scan_time = Integer.parseInt(cur_time);
					System.out.printf("<현재 시간>\n => %d시",scan_time);
					break;

				default:
					break;
				}
	        	
	        	
	        }
			
			
			
			

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
