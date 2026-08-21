//package com.suryoday.FdOpening;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.security.SecureRandom;
//import java.text.SimpleDateFormat;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.Date;
//
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.json.JSONObject;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//public class Test {
//	public static void main(String[] args)
//	{
//		String str="1Y1M1D";
//		str=str.replace("Y","Y ");
//		str=str.replace("M","M ");
//		
//		JSONObject data=new JSONObject();
//		JSONObject poa=new JSONObject();
//		poa.put("Abc","Abc");
//		data.put("Poa", poa);
//		if(!data.has("Poc"))
//		{
//			System.out.println("Success");
//		}
//		System.out.println(data);
//		String newSTr="";
//		if(newSTr.startsWith("{"))
//		{
//			System.out.println("valid json");
//		}
//		
//		System.out.println(LocalDateTime.now().toLocalDate().toString());
//		String s=null;
//		if(s==null||s.equals("N"))
//		{
//			System.out.println("Suucess Date "+LocalDateTime.now());
//		}
//		SecureRandom secureRandom = new SecureRandom();
//		System.out.println(secureRandom.nextInt(1000000));
//	}
//}
