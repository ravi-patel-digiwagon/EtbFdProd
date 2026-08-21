package com.suryoday.FdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.suryoday.FdOpening.Others.GenerateProperty;
import com.suryoday.FdOpening.Others.ResponseData;
import com.suryoday.FdOpening.Pojo.CraftMerchantDetails;
import com.suryoday.FdOpening.Repository.CraftMerchantDetailsRepo;
import com.suryoday.FdOpening.Service.UpiMapperService;


@Component
public class UpiMapperServImpl implements UpiMapperService {
	private static Logger logger = LoggerFactory.getLogger(UpiMapperServImpl.class);
	@Autowired CraftMerchantDetailsRepo merchantDetailsRepo;
	@Override
	public JSONObject upiMapper(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();

		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			obj = new URL(x.BASEURL+"upi/mapper/fetch?api_key="+x.api_key);
			logger.debug(x.BASEURL+"upi/mapper/fetch?api_key="+x.api_key);
			
//				 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//				logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", "zvhpsvsjgzghxz5gqb8ypp88");
			sendResponse = getResponse(jsonObject, sendResponse, con, "POST");
//					
//					getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	private static JSONObject getResponse(JSONObject parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
			String MethodType) throws IOException {

		con.setDoOutput(true);
		OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
		os.write(parent.toString());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		logger.debug("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject sendauthenticateResponse1 = new JSONObject();
			sendauthenticateResponse1.put("data", response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		} else {
			logger.debug("GET request not worked");

			JSONObject sendauthenticateResponse1 = new JSONObject();

			JSONObject errr = new JSONObject();
			errr.put("Description", "Server Error " + responseCode);

			JSONObject j = new JSONObject();
			j.put("Error", errr);

			sendauthenticateResponse1.put("data", "" + j);
			sendAuthenticateResponse = sendauthenticateResponse1;
		}

		return sendAuthenticateResponse;

	}

	@Override
	public JSONObject paymentVpa(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();

		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			obj = new URL(x.BASEURL+"payment/vpa");
			logger.debug(x.BASEURL+"payment/vpa");
			
//				 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//				logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", "mffn349274c26me7ydr9jh3g");
			con.setRequestProperty("Merchant-Id", "MER0000000009631");//OLD MER0000000000002
			con.setRequestProperty("X-Correlation-ID", "SuryodayBank");
			sendResponse = getResponse(jsonObject, sendResponse, con, "POST");
//					
//					getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.printStackTrace();
		}
		logger.debug("paymentVpa :: "+ sendResponse.toString());
		return sendResponse;
	}

	@Override
	public JSONObject generateQR(JSONObject json) throws Exception {
		try {
			String mobNo = json.getJSONObject("Data").getString("MobileNo");
			String base64 = "";


			String path = "/opt/digital/apache-tomcat-9.0.105/temp/Abcd_qrcode.jpg";

//			String qrUrl = QRurl + "id=" + id + "&" + "LG" + LG;
			
			String qrUrl="https://msme.suryodaybank.com/WebJourney?referral_mobile_no="+Base64.getEncoder().encodeToString(mobNo.getBytes())+"&QR=1";

			logger.debug("qrUrl" + qrUrl);
			base64 = QrCodeGenerateBase64(qrUrl,path);
			CraftMerchantDetails craftMerchantDetails = merchantDetailsRepo.findById(mobNo).orElseThrow(()-> new NoSuchElementException("No record found"));
			craftMerchantDetails.setQr(Base64.getDecoder().decode(base64));
			craftMerchantDetails.setIsQrGenerated("Y");
			merchantDetailsRepo.save(craftMerchantDetails);
			logger.debug("base64" + base64);

			JSONObject data=new JSONObject();
			JSONObject respObject = new JSONObject();
			respObject.put("base64", base64);
			data.put("Data", respObject);
			sendWhatsapp(mobNo,base64);
			logger.debug("Response JSONObject" + data);
			return data;
		} catch (Exception e) {
			logger.debug("Exception :- " + e);
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}
	
	private String QrCodeGenerateBase64(String intentUrl,String path) throws WriterException, IOException {
		QrCodeGenerate(intentUrl,path);
		File f =  new File(path);
		String encodstring = encodeFileToBase64Binary(f);
		
		return encodstring;
	}

	private void QrCodeGenerate(String intentUrl,String path) throws WriterException, IOException {
		  
		//path where we want to get QR Code  
		
		try {
		File f =  new File(path);
		
		
		//Encoding charset to be used  
		String charset = "UTF-8";  
		Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();  
		//generates QR code with Low level(L) error correction capability  
		hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  
		//invoking the user-defined method that creates the QR code  
		generateQRcode(intentUrl, path, charset, hashMap, 200, 200);//increase or decrease height and width accodingly   
		//prints if the QR code is generated   
		System.out.println("QR Code created successfully."); 
		}
		catch (Exception e) {
			logger.debug("Error"+e.getMessage());
		}
	}

	public static void generateQRcode(String data, String path, String charset, Map map, int h, int w) throws WriterException, IOException  
	{  
	//the BitMatrix class represents the 2D matrix of bits  
	//MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the BarcodeFormat requested and encodes the barcode with the supplied contents.  
	BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);  
	MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));  
	}
	
	private static String encodeFileToBase64Binary(File file){
	    String encodedfile = null;
	    try {
	        FileInputStream fileInputStreamReader = new FileInputStream(file);
	        byte[] bytes = new byte[(int)file.length()];
	        fileInputStreamReader.read(bytes);
	        encodedfile = Base64.getEncoder().encodeToString(bytes);
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	    	
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	
	        e.printStackTrace();
	    }

	    return encodedfile;
	}

	@Override
	public JSONObject faceMatch(JSONObject jsonObject) {
		JSONObject sendResponse = new JSONObject();
		JSONObject Data = new JSONObject();

		JSONObject data = new JSONObject();
		data.put("Image1B64", jsonObject.getJSONObject("Data").getString("Image1"));
		data.put("Image2B64", jsonObject.getJSONObject("Data").getString("Image2"));
		Data.put("Data", data);
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			obj = new URL(x.BASEURL+"face/similarity?api_key="+x.api_key);
			logger.debug(x.BASEURL+"face/similarity?api_key="+x.api_key);
			
//				 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//				logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID", "SuryodayBank");
			sendResponse = getResponse(Data, sendResponse, con, "POST");
//					
//					getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}
	
	
	
	public JSONObject sendWhatsapp(String mobNo,String base64) throws IOException {
		JSONObject response=new JSONObject();
		JSONObject req = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray messages = new JSONArray();
		JSONObject messages1 = new JSONObject();
		messages1.put("Sender", "918422890082");
		messages1.put("To", "91"+mobNo);
		messages1.put("MessageId", "56566556556");
		messages1.put("TransactionId", "56665565656");
		messages1.put("Channel", "wa");
		messages1.put("Type", "mediaTemplate");
		JSONObject mediaTemplate = new JSONObject();
		mediaTemplate.put("ContentType", "image/jpeg");
		mediaTemplate.put("Template","referral_qr_code");
		mediaTemplate.put("LangCode", "en");
		mediaTemplate.put("Filename", mobNo+"_Qr_Code.jpg");
		mediaTemplate.put("Content", base64);
		JSONArray param=new JSONArray();
		JSONObject param1=new JSONObject();
		param1.put("Name","1");
		param1.put("Value",mobNo);
		param.put(param1);
		JSONObject param2=new JSONObject();
		param2.put("Name","2");
		param2.put("Value",mobNo);
		param.put(param2);
		mediaTemplate.put("Parameters",param);
		messages1.put("MediaTemplate", mediaTemplate);
		messages.put(messages1);
		data.put("Messages", messages);
		data.put("ResponseType", "json");
		req.put("Data", data);

		GenerateProperty x = GenerateProperty.getInstance();
		x.bypassssl();
		x.getappprop();
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

//		String sendUrl = Url+"whatsapp/media/template/push/v2?api_key=zvhpsvsjgzghxz5gqb8ypp88";
		String sendUrl = x.BASEURL+"whatsapp/media/template/push?api_key="+x.api_key;
		logger.debug("Send Whatsapp Url:- " + sendUrl);
		URL obj = new URL(sendUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
//con.setRequestProperty("X-Correlation-ID",headerrequest.getJSONObject("header").getString("X_Correlation_ID").toString());
		con.setRequestProperty("x-api-key","NbFTnmpbeyWYQhd3tbsCJYNjjh5LI35bbTpBV-MG");
//			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
		Long coRelationId = ResponseData.generateRandom(10);
		con.setRequestProperty("X-Correlation-ID", coRelationId.toString());
		response = ResponseData.postResponseData(req, response, con, "POST");
		logger.debug("Send whatsapp resp "+response);
		return response;
	}

}
