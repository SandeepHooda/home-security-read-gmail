import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import vo.Prediction;
import vo.ResponseVO;

public class TestJava {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		TestJava http = new TestJava();

		//System.out.println("Testing 1 - Send Http GET request");
		//http.sendGet();
		
		System.out.println("\nTesting 2 - Send Http POST request");
		http.sendPost();

	}

	// HTTP GET request
	private void sendGet() throws Exception {

		String url = "http://www.google.com/search?q=mkyong";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
	
	// HTTP POST request
	private void sendPost() throws Exception {

		String url = "http://localhost:80/v1/vision/detection";
	
		String boundary =  "*****";
		String attachmentName = "image";
		String attachmentFileName = "C:\\Users\\shaurya\\Desktop\\img\\person.jpg";
		String crlf = "\r\n";
		String twoHyphens = "--";
				    
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty(
			    "Content-Type", "multipart/form-data;boundary=" + boundary);
		
		
		DataOutputStream request = new DataOutputStream(
				con.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" +
			    attachmentName + "\";filename=\"" + 
			    attachmentFileName + "\"" + crlf);
			request.writeBytes(crlf);
			File file = new File("C:\\Users\\shaurya\\Downloads\\img\\person.jpg");
			  //init array with file length
			  byte[] bytesArray = new byte[(int) file.length()]; 

			  FileInputStream fis = new FileInputStream(file);
			  fis.read(bytesArray); //read file into bytes[]
			  fis.close();
			request.write(bytesArray);
			
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + 
			    twoHyphens + crlf);
	
			request.flush();
			request.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		Gson  json = new Gson();
		  ResponseVO responseVO = (ResponseVO) json.fromJson(response.toString(), ResponseVO.class);
		//print result
		System.out.println(responseVO.isSuccess());
		System.out.println(response.toString());
		if (responseVO.isSuccess()) {
			for (Prediction predition: responseVO.getPredictions()) {
				System.out.println(predition.getLabel());
			}
		}

	}

}