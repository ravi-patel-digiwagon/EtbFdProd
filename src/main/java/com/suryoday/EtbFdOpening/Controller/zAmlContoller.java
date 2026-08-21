package com.suryoday.EtbFdOpening.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.suryoday.EtbFdOpening.Service.CustomerDetailsService;
import com.suryoday.EtbFdOpening.Service.VKYCService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
@CrossOrigin(origins = "*")
public class zAmlContoller {

    private static final Logger logger = LoggerFactory.getLogger(zAmlContoller.class);

    @Autowired
    CustomerDetailsService custsomerdetailsservice;
    @Autowired
	VKYCService vkycservice;
    
    
    
//    @PostMapping(value = "/fetchCus", produces = "application/json")
//    public ResponseEntity<Object> ekycOtp(@RequestBody String bm) {
//        try {
//            JSONObject jsonObject = new JSONObject(bm);
//            String customerId = jsonObject.getJSONObject("Data").getString("CustomerID");
//
//            FdOpeningNTB fdOpeningNTB = vkycservice.fetchByCifCustomerId(customerId);
//            JSONObject pdResponse = new JSONObject();
//
//            if (fdOpeningNTB != null) {
//                pdResponse.put("ApplicationNumber", fdOpeningNTB.getApplicationNo());
//                pdResponse.put("CifNumber", fdOpeningNTB.getCifCustomerId());
//                return ResponseEntity.ok(pdResponse.toMap());
//            } else {
//                pdResponse.put("message", "please enter valid aadharnumber");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pdResponse.toMap());
//            }
//        } catch (Exception e) {
//            JSONObject error = new JSONObject();
//            error.put("message", "Invalid request format");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.toMap());
//        }
//    }
        
    

    @RequestMapping(value = "/amlCsvUpload", method = RequestMethod.POST, produces = "text/csv")
    public ResponseEntity<byte[]> uploadCsvAndReturn(@RequestParam("file") MultipartFile file) {
        logger.info("Received AML CSV upload request, file name: {}", file.getOriginalFilename());

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            logger.debug("CSV file parsed successfully. Headers: {}", csvParser.getHeaderMap().keySet());

            // Capture original headers + add new columns
            List<String> headers = new ArrayList<>(csvParser.getHeaderMap().keySet());

            headers.add("AdharCard");
            headers.add("BirthDt");
            headers.add("FirstName");
            headers.add("LastName");
            headers.add("AML_response");

            StringWriter stringWriter = new StringWriter();
            try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {

                for (CSVRecord record : csvParser) {
                    String customerId = record.get("cif_customer_id");
                    String applicationNo=record.get("application_no");
                    String cifReq = record.get("cif_request");
                    String eKycDetails = record.get("ekyc_details");
                    logger.info("Processing record for Customer_ID: {}", customerId);
                    
                    try {
//                        FdOpeningNTB fdOpeningNTB = vkycservice.fetchByCifCustomerId(customerId);
                    	                        	
                        String amlResponse = "";
                        String adharCard = "";
                        String birthDt = "";
                        String firstName = "";
                        String lastName = "";
                       

                        if (true) {
                            try {
//                            	String cifReq = fdOpeningNTB.getCifRequest();
//                            	String eKycDetails = fdOpeningNTB.getEkycDetails();
                                JSONObject cifReqobj = new JSONObject(cifReq);
                                JSONObject eKycobj = new JSONObject(eKycDetails);
                                JSONObject individual = cifReqobj.getJSONObject("Data").getJSONObject("Individual");

                                adharCard = eKycobj.optString("adharCard", "");
                                birthDt = individual.getJSONObject("DateAndPlaceOfBirth").optString("BirthDt", "");
                                firstName = individual.optString("FirstName", "");
                                lastName = individual.optString("LastName", "");

                                JSONObject input = buildInputJson(adharCard, birthDt, firstName, lastName, applicationNo);
//                                 custsomerdetailsservice.checkAml(input);
                                
                                amlResponse = checkAml(input.toString());

                            } catch (Exception ex) {
                                logger.error("Error parsing NTB record for Customer_ID " + customerId, ex);
                                amlResponse = "ERROR_PROCESSING_NTB";
                            }
                        } else {
                            amlResponse = "NO_RECORD_FOUND";
                        }

                        // Copy all original columns and add new fields and AML response
                        List<String> row = new ArrayList<>();
                        for (String header : csvParser.getHeaderMap().keySet()) {
                            row.add(record.get(header));
                        }
                        row.add(adharCard);
                        row.add(birthDt);
                        row.add(firstName);
                        row.add(lastName);
                        row.add(amlResponse);

                        csvPrinter.printRecord(row);

                    } catch (Exception recordEx) {
                        logger.error("Error processing record for Customer_ID: {}", customerId, recordEx);
                    }
                }
            }

            byte[] csvBytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);

            HttpHeaders headersResponse = new HttpHeaders();
            headersResponse.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.csv");
            headersResponse.set(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

            return ResponseEntity.ok()
                    .headers(headersResponse)
                    .body(csvBytes);

        } catch (Exception e) {
            logger.error("Error processing CSV upload", e);
            throw new RuntimeException("Error processing CSV", e);
        }
    }
    
    
    public static String checkAml(String jsonPayload) {
        String url = "https://applyonline.suryodaybank.com/FDOpeningService/fdOpening/checkAml";
      

        try {
            // Create connection
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send request
            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonPayload.getBytes("UTF-8"));
                os.flush();
            }

            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            JSONObject jsonObject = new JSONObject(response.toString());
            return  jsonObject.getString("data");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    
    



    public static JSONObject buildInputJson(String aadharNo, String dob, String firstName, String lastName, String applicationNo) {
        JSONObject data = new JSONObject()
            .put("AadharNo", aadharNo)
            .put("Dob", dob)
            .put("FirstName", firstName)
            .put("LastName", lastName);
//        	.put("ApplicationNo",applicationNo);
        	
        	

        return new JSONObject().put("Data", data);
    }
}
