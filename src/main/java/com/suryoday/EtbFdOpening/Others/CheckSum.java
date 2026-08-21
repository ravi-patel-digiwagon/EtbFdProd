package com.suryoday.EtbFdOpening.Others;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class CheckSum {

//	public static void main(String[] args) throws Exception {
//		String payload = "{\r\n"
//				+ "        \"invtIdentifier\": \"868535060384456\",\r\n"
//				+ "        \"type\": \"1\",\r\n"
//				+ "        \"userId\": \"123\"\r\n"
//				+ "    }";
//		JSONObject jsonObject = new JSONObject(payload);
//		String Salt = "zMRUqsM2iXkORlDlEZgkD3LfHdfLGRRy";
//		String checksum = CheckSum.getHashedJson(jsonObject.toString(), Salt);
//		String finresp = "{\"checksum\":\"" + checksum + "\",\"payload\":" + jsonObject + "}";
//		System.out.println(finresp);
//		
//		String req="bwYbd0|verify_payment|order_t67svtq5767665t56787|1rmv7S62JBSTQUHyQESTUg7oBWw4fvMe";
//		String salt2="1rmv7S62JBSTQUHyQESTUg7oBWw4fvMe";
//		String hashedText = CheckSum.getHashedText(req, salt2);
//		System.out.println("HashedText"+hashedText);
//	}

	private static String convertByteToHex(byte[] data) throws Exception {
		try {
			StringBuilder hexData = new StringBuilder();
			for (int byteIndex = 0; byteIndex < data.length; byteIndex++) {
				hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));
			}
			return hexData.toString();
		} catch (Exception ex) {
			throw new Exception(ex);
		}
	}

	public static String getHashedJson(String jsonToHash, String salt) {
		try {
			return getHashedText(getSorted(jsonToHash), salt);
		} catch (Exception ex) {
			return "";
		}
	}

	public static String getHashedText(String textToHash, String salt) throws Exception {
		try {
			MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			sha512.update(salt.getBytes());
			return convertByteToHex(sha512.digest(textToHash.getBytes()));
		} catch (Exception ex) {
			throw new Exception(ex);
		}
	}
	
	public static String generateHash(String input) {
//        String input = "bwYbd0|verify_payment|order_t67svtq5767665t56787|1rmv7S62JBSTQUHyQESTUg7oBWw4fvMe";
        return sha512(input);
    }
	
	 private static String sha512(String input) {
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-512");
	            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
	            StringBuilder sb = new StringBuilder();
	            for (byte b : hashBytes) {
	                sb.append(String.format("%02x", b));
	            }
	            return sb.toString();
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        }
	    }
	 
//	 
//	 public static void main(String[] args) {
//		 //RR PAYU
//		 String temp ="bwYbd0|verify_payment|testingfortest1|1rmv7S62JBSTQUHyQESTUg7oBWw4fvMe";
//		 
//		 String hash = generateHash(temp);
//		 System.out.println("Hash value :: "+ hash);
//	}

	private static void addKeys(String currentPath, JsonNode jsonNode, List<String> vals) {
		if (jsonNode.isObject()) {
			ObjectNode objectNode = (ObjectNode) jsonNode;
			Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
			String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> entry = iter.next();
				vals.add(entry.getKey());
				addKeys(pathPrefix + entry.getKey(), entry.getValue(), vals);
			}
		} else if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) jsonNode;
			for (int i = 0; i < arrayNode.size(); i++) {
				addKeys(currentPath + "[" + i + "]", arrayNode.get(i), vals);
			}
		} else if (jsonNode.isValueNode()) {
			ValueNode valueNode = (ValueNode) jsonNode;
			if (valueNode.asText().trim().length() > 0) {
				vals.add(valueNode.asText());
			}
		} else {

		}
	}

	public static String getSorted(String json) throws Exception {
		List<String> vals = new ArrayList<String>();
//		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			addKeys("", new ObjectMapper().readTree(jsonObject.toString()), vals);
			Collections.sort(vals);
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return vals.toString();
	}

}
