package uk.harry.hospital_maternity_api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.Month;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

@Service
public class PatientService {

    private final String hospitalApiUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/patients";

    public List<String> findPatientsNeverAdmitted() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity(hospitalApiUrl, List.class);
        List<Map<String, Object>> patients = response.getBody();
    
        List<String> neverAdmittedPatients = new ArrayList<>();
    
        if (patients != null) {
            for (Map<String, Object> patient : patients) {
                List<Map<String, Object>> admissions = (List<Map<String, Object>>) patient.get("admissions");
                if (admissions == null || admissions.isEmpty()) {
                    Object nameObj = patient.get("name");
                    if (nameObj != null) {
                        neverAdmittedPatients.add(nameObj.toString());
                    }
                }
            }
        }
        return neverAdmittedPatients;
    }    

    public List<String> findPatientsReAdmittedWithin7Days() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity(hospitalApiUrl, List.class);
        List<Map<String, Object>> patients = response.getBody();
    
        List<String> reAdmittedPatients = new ArrayList<>();
    
        if (patients != null) {
            for (Map<String, Object> patient : patients) {
                List<Map<String, Object>> admissions = (List<Map<String, Object>>) patient.get("admissions");
                if (admissions != null && admissions.size() >= 2) {
                    // Sort admissions by admission date
                    admissions.sort((a, b) -> {
                        String dateA = (String) a.get("admissionDate");
                        String dateB = (String) b.get("admissionDate");
                        return dateA.compareTo(dateB);
                    });
    
                    for (int i = 0; i < admissions.size() - 1; i++) {
                        String dischargeDateStr = (String) admissions.get(i).get("dischargeDate");
                        String nextAdmissionDateStr = (String) admissions.get(i + 1).get("admissionDate");
    
                        if (dischargeDateStr != null && nextAdmissionDateStr != null) {
                            LocalDate dischargeDate = LocalDate.parse(dischargeDateStr);
                            LocalDate nextAdmissionDate = LocalDate.parse(nextAdmissionDateStr);
    
                            long daysBetween = ChronoUnit.DAYS.between(dischargeDate, nextAdmissionDate);
    
                            if (daysBetween >= 0 && daysBetween <= 7) {
                                Object nameObj = patient.get("name");
                                if (nameObj != null) {
                                    reAdmittedPatients.add(nameObj.toString());
                                    break; // Only add once even if multiple re-admissions
                                }
                            }
                        }
                    }
                }
            }
        } 
        return reAdmittedPatients;
    }    

    public String findMonthWithMostAdmissions() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity(hospitalApiUrl, List.class);
        List<Map<String, Object>> patients = response.getBody();
    
        Map<Month, Integer> monthCount = new HashMap<>();
    
        if (patients != null) {
            for (Map<String, Object> patient : patients) {
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
        }
    
        // Find the month with the most admissions
        Month bestMonth = null;
        int maxAdmissions = 0;
        for (Map.Entry<Month, Integer> entry : monthCount.entrySet()) {
            if (entry.getValue() > maxAdmissions) {
                bestMonth = entry.getKey();
                maxAdmissions = entry.getValue();
            }
        }  
        return (bestMonth != null) ? bestMonth.toString() : "No admissions found";
    }
    
    public List<String> findPatientsWithMultipleStaff() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity(hospitalApiUrl, List.class);
        List<Map<String, Object>> patients = response.getBody();
    
        List<String> patientsWithMultipleStaff = new ArrayList<>();
    
        if (patients != null) {
            for (Map<String, Object> patient : patients) {
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
        }
    
        return patientsWithMultipleStaff;
    }    
}
