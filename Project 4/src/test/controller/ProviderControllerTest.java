package test.controller;

import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.io.InputStream;

import chocan.controller.ProviderController;
import chocan.database.CredentialsDatabase;
import chocan.database.ServiceDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProviderControllerTest {

    private CredentialsDatabase mockCredentialsDatabase;
    private ServiceDatabase mockServiceDatabase;
    private ProviderController providerController;

    @Before
    public void setUp() {
        mockCredentialsDatabase = new CredentialsDatabase();
        mockServiceDatabase = new ServiceDatabase();
        providerController = new ProviderController(mockCredentialsDatabase, mockServiceDatabase);
    }

    @After
    public void tearDown() {
        mockCredentialsDatabase.delete();
        mockServiceDatabase.delete();
    }

    @Test
    public void testValidateMemberValid() {
        // Arrange
        String memberId = "123456";
        String name = "John Doe";
        String password = "banana";
        String role = "member";
        String address = "101 Main St.";
        String zipcode = "11111";
        String state = "AL";

        // Create member data with additional fields
        HashMap<String, String> memberData = createMemberData(name, password, role, address, zipcode, state);
        mockCredentialsDatabase.addEntry(memberId, memberData);

        // Act
        // Redirect system output to capture console output for validation
        String consoleOutput = redirectSystemOut(() -> providerController.validateMember(), null);

        // Assert
        assertTrue(consoleOutput.contains("Member found: " + name));
    }

    @Test
    public void testValidateMemberInvalid() {
        // Arrange
        String invalidMemberId = "999999"; // Assuming this member ID is invalid
        ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidMemberId.getBytes());

        // Act
        String consoleOutput = redirectSystemOut(() -> {
            System.setIn(inputStream);
            System.out.print("Provide card (Member number): ");
            providerController.validateMember();
        }, inputStream);

        // Assert
        assertTrue(consoleOutput.contains("Invalid member number."));
    }

    @Test
    public void testBillChocAnWithValidTransaction() {
        // Ensure the database is in a clean state before this test
        mockCredentialsDatabase.delete();
        mockServiceDatabase.delete();

        // Act
        String consoleOutput = redirectSystemOut(() -> providerController.billChocAn(), null);

        // Assert
        assertEquals("No fee listed under this service!\n", consoleOutput);
    }

    @Test
    public void testBillChocAnWithInvalidTransaction() {
        // Ensure the database is in a clean state before this test
        mockCredentialsDatabase.delete();
        mockServiceDatabase.delete();

        // Act
        String consoleOutput = redirectSystemOut(() -> providerController.billChocAn(), null);

        // Assert
        assertEquals("Invalid Transaction ID.\n", consoleOutput);
    }

    private HashMap<String, String> createMemberData(String name, String password, String role,
            String address, String zipcode, String state) {
        HashMap<String, String> memberData = new HashMap<>();
        memberData.put("name", name);
        memberData.put("password", password);
        memberData.put("role", role);
        memberData.put("address", address);
        memberData.put("zipcode", zipcode);
        memberData.put("state", state);

        return memberData;
    }

    private String redirectSystemOut(Runnable action, ByteArrayInputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        try {
            System.setOut(new PrintStream(outputStream));
            if (inputStream != null) {
                System.setIn(inputStream);
            }
            action.run();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        return outputStream.toString();
    }
}
