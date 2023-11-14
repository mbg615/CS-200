package chocan.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.nio.file.Path;

import chocan.database.CredentialsDatabase;
import chocan.database.ServiceDatabase;

public class ProviderReportController { //extends AbstractReportController
    
	private CredentialsDatabase credentials;
    private ServiceDatabase services;
	
    /*@Override
    public void timedMethod() {

    }*/
   
	public ProviderReportController(CredentialsDatabase credentials, ServiceDatabase services) {
    	this.credentials = credentials;
    	this.services = services;
    }
	
	//generates a report for each provider as a separate text file
	public void generateProviderReports(List<String> providerIds) throws IOException {
        // Iterate over each provider ID and generate a report
        for (String providerId : providerIds) {
            //generate a separate file for each provider
            String fileName = "provider_report_" + providerId + ".txt";
            generateProviderReport(providerId, fileName);
        }
	 }
	 
    public void generateProviderReport(String providerId, String filename) throws IOException{
    	//retrieve provider info
    	HashMap<String, String> providerInfo = credentials.getEntry(providerId);
    	
        Path filePath = Path.of(System.getProperty("java.io.tmpdir")).resolve("chocan").resolve("reports");
        

        // Create the directory if it doesn't exist
        if (!filePath.toFile().exists()) {
            filePath.toFile().mkdirs();
        }

        // Create the file named 
        filePath = filePath.resolve(filename);

        // Get a file object from the path
        File file = filePath.toFile();
        
        
        // Use try-with-resources to ensure the writer is closed properly
        try (FileWriter fstream = new FileWriter(file); BufferedWriter writer = new BufferedWriter(fstream)) {
            writeDetails(providerId, writer, providerInfo);
            writeServices("providerId",providerId,writer);
            System.out.println("Report written to " + filePath.toString());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    
    private void writeDetails(String providerId, BufferedWriter writer, HashMap<String, String> providerInfo) throws IOException {
        //formatting provider details
        writer.write("Provider Name:" + String.format("%-25s\n", providerInfo.getOrDefault("name", "")));
        //writer.write("Provider Number:" + String.format("%-9s\n", serviceInfo.getOrDefault("providerId", "")));
        writer.write("Provider Number:" + String.format("%-9s\n", providerId));
        writer.write("Street Address:" + String.format("%-25s\n", providerInfo.getOrDefault("address", "")));
        writer.write("City:" + String.format("%-14s\n", providerInfo.getOrDefault("city", "")));
        writer.write("State:" + String.format("%-2s\n", providerInfo.getOrDefault("state", "")));
        writer.write("ZIP Code:" + String.format("%-5s\n", providerInfo.getOrDefault("zipcode", "")));
    }
    
    private void writeServices(String field, String matchVal, BufferedWriter writer) throws IOException {
        //use match search to get services
        HashMap<String, HashMap<String, String>> matchedRecords = services.matchSearch(field, matchVal);

        // Iterate through the results and print them
        for (String key : matchedRecords.keySet()) {
        	writer.write("-----------------------\n");
            HashMap<String, String> record = matchedRecords.get(key);
            //System.out.println("Record Key: " + key);
            for (String recordField : record.keySet()) {
					writer.write(recordField + ": " + String.format("%-20s\n", record.get(recordField)));
            }
        }
    }


/*public static void main(String[] args) {
	
    // Mock data for CredentialsDatabase and ServiceDatabase would be created here.
    // Since the actual implementation is not provided, I'll assume there are methods to add mock entries.
    
    // Initialize your database classes (adjust this with actual constructors or methods)
    CredentialsDatabase credentialsDatabase = new CredentialsDatabase();
    ServiceDatabase serviceDatabase = new ServiceDatabase();
    
    
    // Assume that these methods add entries to the databases
    // You will need to replace these with your actual methods for adding entries
    credentialsDatabase.addEntry("unclesame", createMockProviderInfo());
    serviceDatabase.addEntry("unclesame", createMockServiceInfo());
    
    // Initialize the controller with the databases
    ProviderReportController controller = new ProviderReportController(credentialsDatabase, serviceDatabase);

    // Generate reports for the providers
    try {
        List<String> providerIds = Arrays.asList("unclesame"); // Add more provider IDs as needed
        controller.generateProviderReports(providerIds);
        
        // If no exception is thrown, and files are created, the method works
        System.out.println("Reports generated successfully.");
    } catch (IOException e) {
        // If there's an IOException, it will be printed to the console
        e.printStackTrace();
    }
    credentialsDatabase.removeEntry("unclesame");
    serviceDatabase.removeEntry("unclesame");
}

// Mock method to create provider information
private static HashMap<String, String> createMockProviderInfo() {
    HashMap<String, String> providerInfo = new HashMap<>();
    providerInfo.put("name", "Test Provider");
    providerInfo.put("password", "doglover");
    providerInfo.put("role", "provider");
    providerInfo.put("address", "123 Test Street");
    providerInfo.put("zipcode", "12345");
    providerInfo.put("state", "TS");
    return providerInfo;
}

// Mock method to create service information
private static HashMap<String, String> createMockServiceInfo() {
    HashMap<String, String> serviceInfo = new HashMap<>();
    serviceInfo.put("dateOfService", "2023-01-01");
    serviceInfo.put("memberId", "member1");
    serviceInfo.put("serviceCode", "service1");
    serviceInfo.put("fee", "100.00");
    serviceInfo.put("providerId", "unclesam");
    return serviceInfo;
}*/
}
