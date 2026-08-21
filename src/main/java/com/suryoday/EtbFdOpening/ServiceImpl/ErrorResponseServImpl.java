package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Service.ErrorResponseService;
@Component
public class ErrorResponseServImpl implements ErrorResponseService{
	private static Logger logger = LoggerFactory.getLogger(ErrorResponseServImpl.class);
	@Override
	public String getError(String kycRes, long applicationno) {
		int errorcode = Integer.parseInt(ErrorResponseServImpl.KycRespCall(kycRes,applicationno));
		String jsonresponse = "";
		switch (errorcode) {
		case 100: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"100\",\"ResponseMessage\":\"Personal information demographic data did not match.\"}}"; break;
		case 200: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"200\",\"ResponseMessage\":\"Personal address demographic data did not match.\"}}"; break;
		case 300: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"300\",\"ResponseMessage\":\"Biometric data did not match.\"}}"; break;
		case 310: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"310\",\"ResponseMessage\":\"Duplicate fingers used.\"}}"; break;
		case 311: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"311\",\"ResponseMessage\":\"Duplicate Irises used.\"}}"; break;
		case 312: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"312\",\"ResponseMessage\":\"FMR and FIR cannot be used in same transaction.\"}}"; break;
		case 313: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"313\",\"ResponseMessage\":\"Single FIR record contains more than one finger.\"}}"; break;
		case 314: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"314\",\"ResponseMessage\":\"Number of FMR/FIR should not exceed 10.\"}}"; break;
		case 315: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"315\",\"ResponseMessage\":\"Number of IIR should not exceed 2.\"}}"; break;
		case 316: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"316\",\"ResponseMessage\":\"Number of FID should not exceed 1.\"}}"; break;
		case 330: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"330\",\"ResponseMessage\":\"Biometrics locked by Aadhaar holder.\"}}"; break;
		case 400: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"400\",\"ResponseMessage\":\"Invalid OTP value.\"}}"; break;
		case 402: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"402\",\"ResponseMessage\":\"“txn” value did not match with “txn” value used in Request OTP API.\"}}"; break;
		case 500: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"500\",\"ResponseMessage\":\"Invalid encryption of session key.\"}}"; break;
		case 501: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"501\",\"ResponseMessage\":\"Invalid certificate identifier in “ci” attribute of “Skey”.\"}}"; break;
		case 502: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"502\",\"ResponseMessage\":\"Invalid encryption of PID.\"}}"; break;
		case 503: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"503\",\"ResponseMessage\":\"Invalid encryption of Hmac.\"}}"; break;
		case 504: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"504\",\"ResponseMessage\":\"Session key re-initiation required due to expiry or key out of sync.\"}}"; break;
		case 505: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"505\",\"ResponseMessage\":\"Synchronized Key usage not allowed for the AUA.\"}}"; break;
		case 510: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"510\",\"ResponseMessage\":\"Invalid Auth XML format.\"}}"; break;
		case 511: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"511\",\"ResponseMessage\":\"Invalid PID XML format.\"}}"; break;
		case 512: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"512\",\"ResponseMessage\":\"Invalid Aadhaar holder consent in “rc” attribute of “Auth”.\"}}"; break;
		case 520: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"520\",\"ResponseMessage\":\"Invalid “tid” value.\"}}"; break;
		case 521: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"521\",\"ResponseMessage\":\"Invalid “dc” code under Meta tag.\"}}"; break;
		case 524: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"524\",\"ResponseMessage\":\"Invalid “mi” code under Meta tag.\"}}"; break;
		case 527: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"527\",\"ResponseMessage\":\"Invalid “mc” code under Meta tag.\"}}"; break;
		case 530: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"530\",\"ResponseMessage\":\"Invalid authenticator code.\"}}"; break;
		case 540: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"540\",\"ResponseMessage\":\"Invalid Auth XML version.\"}}"; break;
		case 541: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"541\",\"ResponseMessage\":\"Invalid PID XML version.\"}}"; break;
		case 542: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"542\",\"ResponseMessage\":\"AUA not authorized for ASA. This error will be returned if AUA and ASA do not have linking in the portal.\"}}"; break;
		case 543: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"543\",\"ResponseMessage\":\"Sub-AUA not associated with “AUA”. This error will be returned if Sub-AUA specified in “sa” attribute is not added as “Sub-AUA” in portal.\"}}"; break;
		case 550: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"550\",\"ResponseMessage\":\"Invalid “Uses” element attributes.\"}}"; break;
		case 551: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"551\",\"ResponseMessage\":\"Invalid “tid” value.\"}}"; break;
		case 553: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"553\",\"ResponseMessage\":\"Registered devices currently not supported. This feature is being implemented in a phased manner.\"}}"; break;
		case 554: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"554\",\"ResponseMessage\":\"Public devices are not allowed to be used.\"}}"; break;
		case 555: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"555\",\"ResponseMessage\":\"rdsId is invalid and not part of certification registry.\"}}"; break;
		case 556: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"556\",\"ResponseMessage\":\"rdsVer is invalid and not part of certification registry.\"}}"; break;
		case 557: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"557\",\"ResponseMessage\":\"dpId is invalid and not part of certification registry.\"}}"; break;
		case 558: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"558\",\"ResponseMessage\":\"Invalid dih.\"}}"; break;
		case 559: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"559\",\"ResponseMessage\":\"Device Certificate has expired.\"}}"; break;
		case 560: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"560\",\"ResponseMessage\":\"DP Master Certificate has expired.\"}}"; break;
		case 561: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"561\",\"ResponseMessage\":\"Request expired (“Pid->ts” value is older than N hours where N is a configured threshold in authentication server).\"}}"; break;
		case 562: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"562\",\"ResponseMessage\":\"Timestamp value is future time (value specified “Pid->ts” is ahead of authentication server time beyond acceptable threshold).\"}}"; break;
		case 563: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"563\",\"ResponseMessage\":\"Duplicate request (this error occurs when exactly same authentication request was re-sent by AUA).\"}}"; break;
		case 564: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"564\",\"ResponseMessage\":\"HMAC Validation failed.\"}}"; break;
		case 565: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"565\",\"ResponseMessage\":\"AUA license has expired.\"}}"; break;
		case 566: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"566\",\"ResponseMessage\":\"Invalid non-decryptable license key.\"}}"; break;
		case 567: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"567\",\"ResponseMessage\":\"Invalid input (this error occurs when unsupported characters were found in Indian language values, “lname” or “lav”).\"}}"; break;
		case 568: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"568\",\"ResponseMessage\":\"Unsupported Language.\"}}"; break;
		case 569: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"569\",\"ResponseMessage\":\"Digital signature verification failed (means that authentication request XML was modified after it was signed).\"}}"; break;
		case 570: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"570\",\"ResponseMessage\":\"Invalid key info in digital signature (this means that certificate used for signing the authentication request is not valid – it is either expired, or does not belong to the AUA or is not created by a well-known Certification Authority).\"}}"; break;
		case 571: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"571\",\"ResponseMessage\":\"PIN requires reset.\"}}"; break;
		case 572: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"572\",\"ResponseMessage\":\"Invalid biometric position.\"}}"; break;
		case 573: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"573\",\"ResponseMessage\":\"Pi usage not allowed as per license.\"}}"; break;
		case 574: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"574\",\"ResponseMessage\":\"Pa usage not allowed as per license.\"}}"; break;
		case 575: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"575\",\"ResponseMessage\":\"Pfa usage not allowed as per license.\"}}"; break;
		case 576: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"576\",\"ResponseMessage\":\"FMR usage not allowed as per license.\"}}"; break;
		case 577: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"577\",\"ResponseMessage\":\"FIR usage not allowed as per license.\"}}"; break;
		case 578: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"578\",\"ResponseMessage\":\"IIR usage not allowed as per license.\"}}"; break;
		case 579: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"579\",\"ResponseMessage\":\"OTP usage not allowed as per license.\"}}"; break;
		case 580: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"580\",\"ResponseMessage\":\"PIN usage not allowed as per license.\"}}"; break;
		case 581: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"581\",\"ResponseMessage\":\"Fuzzy matching usage not allowed as per license.\"}}"; break;
		case 582: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"582\",\"ResponseMessage\":\"Local language usage not allowed as per license.\"}}"; break;
		case 586: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"586\",\"ResponseMessage\":\"FID usage not allowed as per license. This feature is being implemented in a phased manner.\"}}"; break;
		case 587: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"587\",\"ResponseMessage\":\"Name space not allowed.\"}}"; break;
		case 588: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"588\",\"ResponseMessage\":\"Registered device not allowed as per license.\"}}"; break;
		case 590: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"590\",\"ResponseMessage\":\"Public device not allowed as per license.\"}}"; break;
		case 710: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"710\",\"ResponseMessage\":\"Missing “Pi” data as specified in “Uses”.\"}}"; break;
		case 720: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"720\",\"ResponseMessage\":\"Missing “Pa” data as specified in “Uses”.\"}}"; break;
		case 721: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"721\",\"ResponseMessage\":\"Missing “Pfa” data as specified in “Uses”.\"}}"; break;
		case 730: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"730\",\"ResponseMessage\":\"Missing PIN data as specified in “Uses”.\"}}"; break;
		case 740: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"740\",\"ResponseMessage\":\"Missing OTP data as specified in “Uses”.\"}}"; break;
		case 800: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"800\",\"ResponseMessage\":\"Invalid biometric data.\"}}"; break;
		case 810: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"810\",\"ResponseMessage\":\"Missing biometric data as specified in “Uses”.\"}}"; break;
		case 811: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"811\",\"ResponseMessage\":\"Missing biometric data in CIDR for the given Aadhaar number.\"}}"; break;
		case 812: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"812\",\"ResponseMessage\":\"Aadhaar holder has not done “Best Finger Detection”. Application should initiate BFD to help Aadhaar holder identify their best fingers.\"}}"; break;
		case 820: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"820\",\"ResponseMessage\":\"Missing or empty value for “bt” attribute in “Uses” element.\"}}"; break;
		case 821: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"821\",\"ResponseMessage\":\"Invalid value in the “bt” attribute of “Uses” element.\"}}"; break;
		case 822: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"822\",\"ResponseMessage\":\"Invalid value in the “bs” attribute of “Bio” element within “Pid”.\"}}"; break;
		case 901: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"901\",\"ResponseMessage\":\"No authentication data found in the request (this corresponds to a scenario wherein none of the auth data – Demo, Pv, or Bios – is present).\"}}"; break;
		case 902: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"902\",\"ResponseMessage\":\"Invalid “dob” value in the “Pi” element (this corresponds to a scenarios wherein “dob” attribute is not of the format “YYYY” or “YYYYMM-DD”, or the age is not in valid range).\"}}"; break;
		case 910: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"910\",\"ResponseMessage\":\"Invalid “mv” value in the “Pi” element.\"}}"; break;
		case 911: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"911\",\"ResponseMessage\":\"Invalid “mv” value in the “Pfa” element.\"}}"; break;
		case 912: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"912\",\"ResponseMessage\":\"Invalid “ms” value.\"}}"; break;
		case 913: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"913\",\"ResponseMessage\":\"Both “Pa” and “Pfa” are present in the authentication request (Pa and Pfa are mutually exclusive).\"}}"; break;
		case 930: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"930\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 931: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"931\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 932: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"932\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 933: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"933\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 934: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"934\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 935: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"935\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 936: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"936\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 937: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"937\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 938: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"938\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 939: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"939\",\"ResponseMessage\":\"Technical error that are internal to authentication server.\"}}"; break;
		case 940: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"940\",\"ResponseMessage\":\"Unauthorized ASA channel.\"}}"; break;
		case 941: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"941\",\"ResponseMessage\":\"Unspecified ASA channel.\"}}"; break;
		case 950: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"950\",\"ResponseMessage\":\"OTP store related technical error.\"}}"; break;
		case 951: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"951\",\"ResponseMessage\":\"Biometric lock related technical error.\"}}"; break;
		case 980: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"980\",\"ResponseMessage\":\"Unsupported option.\"}}"; break;
		case 995: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"995\",\"ResponseMessage\":\"Aadhaar suspended by competent authority.\"}}"; break;
		case 996: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"996\",\"ResponseMessage\":\"Aadhaar cancelled (Aadhaar is no in authenticable status).\"}}"; break;
		case 997: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"997\",\"ResponseMessage\":\"Aadhaar suspended (Aadhaar is not in authenticatable status).\"}}"; break;
		case 998: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"998\",\"ResponseMessage\":\"Invalid Aadhaar Number.\"}}"; break;
		case 999: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"999\",\"ResponseMessage\":\"Unknown error.\"}}"; break;
		case 403: jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"403\",\"ResponseMessage\":\"403 Forbidden.\"}}"; break;
//		default:
//			jsonresponse = "{\"KycRes\":{\"ResponseCode\":\"300\",\"ResponseMessage\":\"Data Error\"}}";
		}
		logger.debug(jsonresponse);
	//	JSONObject response=new JSONObject(jsonresponse);
		return jsonresponse;
	}
	
	private static String KycRespCall(String HmsData,long applicationno) {
		GenerateProperty x=GenerateProperty.getInstance();
		  x.getappprop();
		  x.bypassssl();
			byte[] valueDecoded = Base64.decodeBase64(HmsData);
			String Rar = new String(valueDecoded);
			
			Document doc = convertStringToXMLDocument(Rar);

			writeXmlDocumentToFile(doc, x.temp+""+applicationno+".xml");

			String SendResponseCode = new String(RarRespCall(applicationno));
			return SendResponseCode;

		}

		private static String RarRespCall(long applicationno) {
			GenerateProperty x=GenerateProperty.getInstance();
			  x.getappprop();
	        x.bypassssl();
			String kycRes = "";
			String Rar = "";
			try {
				
				File stocks = new File(x.temp+""+applicationno+".xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(stocks);
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("KycRes");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						kycRes = getValue("Rar", element).toString();
						byte[] valueDecoded = Base64.decodeBase64(kycRes);
						Rar = new String(valueDecoded);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Document doc = convertStringToXMLDocument(Rar);
			applicationno++;
			writeXmlDocumentToFile(doc,x.temp+""+applicationno+".xml");

			String SendResponseCode = new String(ErrorCall(applicationno));
			return SendResponseCode;

		}

		private static String ErrorCall(long applicationno) {
			String SendResponseCode = "";
			GenerateProperty x=GenerateProperty.getInstance();
			  x.getappprop();
	        x.bypassssl();
			try {
				File stocks = new File(x.temp+""+applicationno+".xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(stocks);
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("AuthRes");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						String ResponseCode = new String(element.getAttribute("err"));
						SendResponseCode = ResponseCode;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return SendResponseCode;
		}

		private static String getValue(String tag, Element element) {
			NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
			Node node = (Node) nodes.item(0);
			return node.getNodeValue();
		}

		private static Document convertStringToXMLDocument(String xmlString) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = null;
			try {

				builder = factory.newDocumentBuilder();

				Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
				return doc;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public static String convertXmlDomToString(Document xmlDocument) {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = tf.newTransformer();

				StringWriter writer = new StringWriter();

				transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));

				return writer.getBuffer().toString();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private static void writeXmlDocumentToFile(Document xmlDocument, String fileName) {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = tf.newTransformer();

				FileOutputStream outStream = new FileOutputStream(new File(fileName));

				transformer.transform(new DOMSource(xmlDocument), new StreamResult(outStream));
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
