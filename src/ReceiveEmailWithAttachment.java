import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

import com.google.gson.Gson;

import secreat.Key;
import vo.Prediction;
import vo.ResponseVO;
 
/**
 * This class is used to receive email with attachment.
 * @author codesjava
 */
public class ReceiveEmailWithAttachment { 
	private static String USER_AGENT ="Mozilla/5.0";
 public static boolean receiveEmail(String pop3Host,
    String mailStoreType, String userName, String password){
	 boolean humanFound = false;
    //Set properties
    Properties props = new Properties();
    props.put("mail.store.protocol", "pop3");
    props.put("mail.pop3.host", pop3Host);
    props.put("mail.pop3.port", "995");
    props.put("mail.pop3.starttls.enable", "true");
 
    // Get the Session object.
    Session session = Session.getInstance(props);
 
    try {
        //Create the POP3 store object and connect to the pop store.
	Store store = session.getStore("pop3s");
	store.connect(pop3Host, userName, password);
 
	//Create the folder object and open it in your mailbox.
	Folder emailFolder = store.getFolder("INBOX");
	emailFolder.open(Folder.READ_ONLY);
 
	//Retrieve the messages from the folder object.
	Message[] messages = emailFolder.getMessages();
	System.out.println("Total Message" + messages.length);
 
	//Iterate the messages
	for (int i = 0; i < messages.length; i++) {
	   Message message = messages[i];
	   Address[] toAddress = 
             message.getRecipients(Message.RecipientType.TO);
	     System.out.println("---------------------------------");  
	     System.out.println("Details of Email Message " 
                                                   + (i + 1) + " :");  
	     System.out.println("Subject: " + message.getSubject());  
	     System.out.println("From: " + message.getFrom()[0]);  
 
	     //Iterate recipients 
	     System.out.println("To: "); 
	     for(int j = 0; j < toAddress.length; j++){
	       System.out.println(toAddress[j].toString());
	     }
 
	     //Iterate multiparts
	     Multipart multipart = (Multipart) message.getContent();
	     for(int k = 0; k < multipart.getCount(); k++){
	       BodyPart bodyPart = multipart.getBodyPart(k);  
	       String fileName = bodyPart.getFileName();   
	       if (null != fileName) {
	    	   if (fileName.toLowerCase().indexOf("gb2312") != -1) {   
	               fileName = MimeUtility.decodeText(fileName);   
	           } 
		       System.out.println( fileName);
	       }
           
	      
	       InputStream stream =    (InputStream) bodyPart.getInputStream();  
	      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	     
 
	       byte[] buffer = new byte[1024];
			int len;

			// read bytes from the input stream and store them in buffer
			while ((len = stream.read(buffer)) != -1) {
				// write bytes from the buffer into output stream
				byteArray.write(buffer, 0, len);
			}
	       

	        // Close the file 
			byteArray.close(); 
			if (!humanFound) {
				humanFound = findHuman(byteArray.toByteArray());
				 
			}
	        
	      }  
	   }
 
	   //close the folder and store objects
	   emailFolder.close(false);
	   store.close();
	} catch (NoSuchProviderException e) {
		e.printStackTrace();
	} catch (MessagingException e){
		e.printStackTrace();
	} catch (Exception e) {
	       e.printStackTrace();
	}
    System.out.println("Human found ::: "+humanFound);
    return humanFound;
    
    }
 //
//HTTP POST request
	private static boolean findHuman( byte[] bytesArray ) throws Exception {

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
				if (Prediction.humanStr.equals(predition.getLabel())) {
					return true;
				}
			}
		}
		return false;

	}
	
	
	// HTTP GET request
		private static void notifyHumanActivity() throws Exception {

			String url = "http://sanhoo-home-security.appspot.com/HumanDetected?deviceID=3";
			
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
		
 public static void main(String[] args) {
  String pop3Host = "pop.gmail.com";//change accordingly
  String mailStoreType = "pop3";	
  final String userName =Key.userName;
  final String password = Key.password;
 
  while(true) {
	 
	  try {
		  //call receiveEmail
		  boolean humanFound = receiveEmail(pop3Host, mailStoreType, userName, password);
		  if (humanFound) {
			  notifyHumanActivity();
		  }
		Thread.sleep(2000);
	} catch (Exception e) {
		
		e.printStackTrace();
	}
  }
 
 }
}