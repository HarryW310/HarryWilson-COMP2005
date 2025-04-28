package uk.harry.hospital_maternity_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.harry.hospital_maternity_api.service.PatientService;
import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/never-admitted")
    public List<String> getPatientsNeverAdmitted() {
        return patientService.findPatientsNeverAdmitted();
    }

    @GetMapping("/re-admitted")
    public List<String> getPatientsReAdmittedWithin7Days() {
        return patientService.findPatientsReAdmittedWithin7Days();
    }

    @GetMapping("/most-admissions-month")
    public String getMonthWithMostAdmissions() {
        return patientService.findMonthWithMostAdmissions();
    }

    @GetMapping("/multiple-staff")
    public List<String> getPatientsWithMultipleStaff() {
        return patientService.findPatientsWithMultipleStaff();
    }
}