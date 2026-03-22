package com.example.patientservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.patientservice.model.Patient;
import com.example.patientservice.repository.PatientRepository;
import com.example.patientservice.util.AESUtil;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientRepository repo;

    // 🔐 POST → Encrypt before saving + Decrypt before returning
    @PostMapping
    public Patient addPatient(@RequestBody Patient p) {

        if (p.getName() != null) {
            p.setName(AESUtil.encrypt(p.getName()));
        }

        if (p.getDisease() != null) {
            p.setDisease(AESUtil.encrypt(p.getDisease()));
        }

        Patient saved = repo.save(p);

        // 🔓 Decrypt before sending response
        if (saved.getName() != null) {
            saved.setName(AESUtil.decrypt(saved.getName()));
        }

        if (saved.getDisease() != null) {
            saved.setDisease(AESUtil.decrypt(saved.getDisease()));
        }

        return saved;
    }

    // 🔐 GET BY ID → Decrypt before returning
    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Long id) {

        Optional<Patient> optionalPatient = repo.findById(id);

        if (optionalPatient.isPresent()) {
            Patient p = optionalPatient.get();

            if (p.getName() != null) {
                p.setName(AESUtil.decrypt(p.getName()));
            }

            if (p.getDisease() != null) {
                p.setDisease(AESUtil.decrypt(p.getDisease()));
            }

            return p;
        }

        return null;
    }

    // 🔐 GET ALL → Decrypt all records
    @GetMapping
    public List<Patient> getAllPatients() {

        List<Patient> patients = repo.findAll();

        for (Patient p : patients) {

            if (p.getName() != null) {
                p.setName(AESUtil.decrypt(p.getName()));
            }

            if (p.getDisease() != null) {
                p.setDisease(AESUtil.decrypt(p.getDisease()));
            }
        }

        return patients;
    }

    // ✅ Debug endpoint (optional)
    @GetMapping("/test")
    public String test(org.springframework.security.core.Authentication auth) {
        return auth.toString();
    }
}