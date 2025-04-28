package uk.harry.hospital_maternity_api.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;


public class PatientServiceTest {  
    @Test
    void testFindPatientsNeverAdmitted() {
        // Mocked patient list (simulate API response)
        List<Map<String, Object>> mockPatients = new ArrayList<>();

        Map<String, Object> patient1 = new HashMap<>();
        patient1.put("name", "Alice");
        patient1.put("admissions", new ArrayList<>()); // no admissions

        Map<String, Object> patient2 = new HashMap<>();
        patient2.put("name", "Bob");
        List<Map<String, Object>> admissions = new ArrayList<>();
        Map<String, Object> admission = new HashMap<>();
        admission.put("admissionDate", "2025-01-01");
        admissions.add(admission);
        patient2.put("admissions", admissions);

        mockPatients.add(patient1);
        mockPatients.add(patient2);

        // Simulate service manually
        List<String> neverAdmittedPatients = new ArrayList<>();

        for (Map<String, Object> patient : mockPatients) {
            List<Map<String, Object>> admissionsList = (List<Map<String, Object>>) patient.get("admissions");
            if (admissionsList == null || admissionsList.isEmpty()) {
                Object nameObj = patient.get("name");
                if (nameObj != null) {
                    neverAdmittedPatients.add(nameObj.toString());
                }
            }
        }

        assertEquals(1, neverAdmittedPatients.size());
        assertEquals("Alice", neverAdmittedPatients.get(0));
    }

    @Test
    void testFindPatientsReAdmittedWithin7Days() {
        List<Map<String, Object>> mockPatients = new ArrayList<>();
    
        Map<String, Object> patient1 = new HashMap<>();
        patient1.put("name", "Charlie");
        List<Map<String, Object>> admissions1 = new ArrayList<>();
    
        Map<String, Object> admission1 = new HashMap<>();
        admission1.put("admissionDate", "2025-01-01");
        admission1.put("dischargeDate", "2025-01-05");
    
        Map<String, Object> admission2 = new HashMap<>();
        admission2.put("admissionDate", "2025-01-07");
        admissions1.add(admission1);
        admissions1.add(admission2);
    
        patient1.put("admissions", admissions1);
    
        mockPatients.add(patient1);
    
        // Simulate service manually
        List<String> reAdmittedPatients = new ArrayList<>();
    
        for (Map<String, Object> patient : mockPatients) {
            List<Map<String, Object>> admissionsList = (List<Map<String, Object>>) patient.get("admissions");
            if (admissionsList != null && admissionsList.size() >= 2) {
                admissionsList.sort((a, b) -> {
                    String dateA = (String) a.get("admissionDate");
                    String dateB = (String) b.get("admissionDate");
                    return dateA.compareTo(dateB);
                });
    
                for (int i = 0; i < admissionsList.size() - 1; i++) {
                    String dischargeDateStr = (String) admissionsList.get(i).get("dischargeDate");
                    String nextAdmissionDateStr = (String) admissionsList.get(i + 1).get("admissionDate");
    
                    if (dischargeDateStr != null && nextAdmissionDateStr != null) {
                        LocalDate dischargeDate = LocalDate.parse(dischargeDateStr);
                        LocalDate nextAdmissionDate = LocalDate.parse(nextAdmissionDateStr);
    
                        long daysBetween = ChronoUnit.DAYS.between(dischargeDate, nextAdmissionDate);
    
                        if (daysBetween >= 0 && daysBetween <= 7) {
                            Object nameObj = patient.get("name");
                            if (nameObj != null) {
                                reAdmittedPatients.add(nameObj.toString());
                            }
                        }
                    }
                }
            }
        }
    
        assertEquals(1, reAdmittedPatients.size());
        assertEquals("Charlie", reAdmittedPatients.get(0));
    }

    @Test
    void testFindMonthWithMostAdmissions() {
        List<Map<String, Object>> mockPatients = new ArrayList<>();
    
        Map<String, Object> patient1 = new HashMap<>();
        patient1.put("name", "Dana");
    
        List<Map<String, Object>> admissions1 = new ArrayList<>();
    
        Map<String, Object> admission1 = new HashMap<>();
        admission1.put("admissionDate", "2025-01-15");
    
        Map<String, Object> admission2 = new HashMap<>();
        admission2.put("admissionDate", "2025-01-20");
    
        admissions1.add(admission1);
        admissions1.add(admission2);
    
        patient1.put("admissions", admissions1);
    
        mockPatients.add(patient1);
    
        // Simulate month counting manually
        Map<Month, Integer> monthCount = new HashMap<>();
    
        for (Map<String, Object> patient : mockPatients) {
            List<Map<String, Object>> admissions = (List<Map<String, Object>>) patient.get("admissions");
            if (admissions != null) {
                for (Map<String, Object> admission : admissions) {
                    String admissionDateStr = (String) admission.get("admissionDate");
                    if (admissionDateStr != null) {
                        LocalDate admissionDate = LocalDate.parse(admissionDateStr);
                        Month month = admissionDate.getMonth();
                        monthCount.put(month, monthCount.getOrDefault(month, 0) + 1);
                    }
                }
            }
        }
    
        // Find month with most admissions manually
        Month bestMonth = null;
        int maxAdmissions = 0;
        for (Map.Entry<Month, Integer> entry : monthCount.entrySet()) {
            if (entry.getValue() > maxAdmissions) {
                bestMonth = entry.getKey();
                maxAdmissions = entry.getValue();
            }
        }
    
        assertEquals(Month.JANUARY, bestMonth);
    }
    
    @Test
    void testFindPatientsWithMultipleStaff() {
        List<Map<String, Object>> mockPatients = new ArrayList<>();
    
        Map<String, Object> patient1 = new HashMap<>();
        patient1.put("name", "Eve");
    
        List<Map<String, Object>> admissions1 = new ArrayList<>();
    
        Map<String, Object> admission1 = new HashMap<>();
        List<Map<String, Object>> staffList1 = new ArrayList<>();
        Map<String, Object> staffMember1 = new HashMap<>();
        staffMember1.put("name", "Dr. Smith");
        Map<String, Object> staffMember2 = new HashMap<>();
        staffMember2.put("name", "Nurse Kelly");
        staffList1.add(staffMember1);
        staffList1.add(staffMember2);
        admission1.put("staff", staffList1);
    
        admissions1.add(admission1);
    
        patient1.put("admissions", admissions1);
    
        mockPatients.add(patient1);
    
        // Simulate service manually
        List<String> patientsWithMultipleStaff = new ArrayList<>();
    
        for (Map<String, Object> patient : mockPatients) {
            Set<String> staffSet = new HashSet<>();
    
            List<Map<String, Object>> admissions = (List<Map<String, Object>>) patient.get("admissions");
            if (admissions != null) {
                for (Map<String, Object> admission : admissions) {
                    List<Map<String, Object>> staffMembers = (List<Map<String, Object>>) admission.get("staff");
                    if (staffMembers != null) {
                        for (Map<String, Object> staff : staffMembers) {
                            String staffName = (String) staff.get("name");
                            if (staffName != null) {
                                staffSet.add(staffName);
                            }
                        }
                    }
                }
            }
    
            if (staffSet.size() > 1) {
                Object nameObj = patient.get("name");
                if (nameObj != null) {
                    patientsWithMultipleStaff.add(nameObj.toString());
                }
            }
        }
    
        assertEquals(1, patientsWithMultipleStaff.size());
        assertEquals("Eve", patientsWithMultipleStaff.get(0));
    }    
}