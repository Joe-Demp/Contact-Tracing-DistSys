package service.patientInfo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.core.Patient;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class PatientInfoController {

    @Autowired
    private PatientRepository patientRepo;

    private Patient patient;
//    private HashMap<String, Patient> patientList = new HashMap<>();

//    CRUD - add patient to list. Returns 201 CREATED response with URI of new resource in header
//    if duplicate phone numbers being added, will create new patient object using same phone number
//    (Results Discovery service ensures no duplicate phone numbers added)
    @RequestMapping(value = "/patientinfo", method = RequestMethod.POST)
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {

//        patientRepo.deleteAll();
        System.out.println("\nCONTROLLER-ADD: PATIENT RECEIVED: " + patient.getFirstName() + patient.getSurname());

        patientRepo.save(patient);
//        patientList.put(patient.getId(), patient);

        System.out.println("\nCONTROLLER-ADD: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

//        DONE
        // FIXME send something like:
        //  return ResponseEntity.created().location(path).build()

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"
                +patient.getPhoneNumber();
        HttpHeaders headers = new HttpHeaders();

        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("\nCONTROLLER-ADD ERROR: " + e);
        }

        return new ResponseEntity<>(headers, HttpStatus.CREATED);

        /*
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/book/"+phone_number;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Location", path);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
         */
    }

    //    CRUD - returns list of patients
    //returns 200 OK response with list of ALL patients (no filtered list yet)
    @RequestMapping(value = "/patientinfo", method = RequestMethod.GET)
    public @ResponseBody List<Patient> getListPatients(@RequestParam(defaultValue = "") String phoneNumber)    {
        System.out.println("\nCONTROLLER-LISTPATIENTS: Outputting repo patient list");
        List<Patient> patientList = patientRepo.findAll();

//        DONE
        // FIXME: replace with enhanced for
        for (Patient p: patientList) {
            System.out.println(p.toString());
        }
        return patientList;

        /*  filtered list template (optional)
        for (ClientApplication ca : applications.values()) {
            //name not given - add all entries to list
            if (name.isEmpty()) {
                list.add(ca);
            }
            else    {
                //if name given AND it DOES match name from this application, add it to the list
                if (ca.getInfo().getName().equals(name))   {
                    list.add(ca);
                }
                //if name given AND it DOESNT match name from this application, DONT add it to the list
                else    {
                    continue;
                }
            }
        }
        return list;
        */
    }

    //    CRUD-returns 200 OK with specific patient based on input phone number. 404 NOT FOUND if input phone number not in repo
    //    if duplicate phone numbers present will return first matching patient (Results Discovery service ensures no duplicate phone numbers added)
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.GET)
    @ResponseStatus(value=HttpStatus.OK)
    public Patient getPatient(@PathVariable String phoneNumber) {
        System.out.println("\nEntered CONTROLLER-GETPATIENT. Phone number: " + phoneNumber);
        List<Patient> patientList = patientRepo.findAll();
        Patient pTemp = null;

//        DONE
        // FIXME: replace with enhanced for
        // FIXME: if the patient is not present here, the method returns the last Patient in the list
        for (Patient p: patientList) {
            if (p.getPhoneNumber().equals(phoneNumber))  {
                System.out.println("CONTROLLER-GETPATIENT: found match, returning patient " + p.toString());
                pTemp = p;
                break;
            }
//            System.out.println(p.toString());
        }

        if (pTemp==null)    {
            System.out.println("\nCONTROLLER-GETPATIENT: no matching patient found for input phone number");
            throw new NoSuchPatientException();
//            return null;
        }
        return pTemp;
    }

    //    CRUD - remove patient based on phone number. will return 204 NO CONTENT if success and 404 NOT FOUND if fail
    //    if duplicate phone numbers present will delete first matching patient (Results Discovery service ensures no duplicate phone numbers added)
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.DELETE)
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String phoneNumber) {
        System.out.println("\nEntered CONTROLLER-REMOVE");

        Patient pTemp = null;
        try {
            pTemp = patientRepo.findFirstByPhoneNumber(phoneNumber);
        }
        catch(Exception e) {
            System.out.println("CONTROLLER-REMOVE: error " + e);
            return;
        }
        if (pTemp == null) {
            throw new NoSuchPatientException();
        }

        System.out.println("\nCONTROLLER-REMOVE: Removing patient:" + pTemp);
        patientRepo.delete(pTemp);

        System.out.println("\nCONTROLLER-REMOVE: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

    }

    /*
//    CRUD - replace entry based on phone number
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.PUT)
    public ResponseEntity<Patient> replacePatient(@PathVariable String phoneNumber, @RequestBody Patient entry) {
        System.out.println("\nEntered CONTROLLER-REPLACEPATIENT. Phone number: " + phoneNumber + "\nPatient: " + entry);
        List<Patient> patientList = patientRepo.findAll();
        Iterator<Patient> patientIterator = patientList.iterator();
        Patient pTemp = null;

        while (patientIterator.hasNext())   {
            pTemp = patientIterator.next();
//            System.out.println(pTemp.toString());
//            System.out.println(pTemp.getPhoneNumber() + " " + phoneNumber);
            if (pTemp.getPhoneNumber().equals(phoneNumber))  {
                System.out.println("CONTROLLER-REPLACEPATIENT: found match, replacing patient " + pTemp.toString());
                break;
            }
        }

        if (pTemp==null)    {
            System.out.println("\nCONTROLLER-REPLACEPATIENT: no matching patient found for input phone number");
            throw new NoSuchPatientException();
//            return null;
        }

        patientRepo.delete(pTemp);
        patientRepo.save(entry);

        System.out.println("\nCONTROLLER-REPLACEPATIENT: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"+phoneNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Location", path);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

     */

    // todo consider the following method:
    /*
    @RequestMapping(value = "/patientinfo/listpatients", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Patient> listPatients(
        @RequestParam(name = "ct", required = false, defaultValue = "null") Boolean contactTraced
        ) {
            ArrayList<Patient> patientList;

            if (contactTraced == null) {
                patientList = patientRepo.findAll();
            } else if (contactTraced) {
                // todo following requires new patientRepo methods

                patientList = patientRepo.findAllContactTraced();
            } else {
                patientList = patientRepo.findAllNotContactTraced();
            }
            return patientList;
    }
    */

    //Callback scheduler - return list of all patients that havent been called for contact tracing
    @RequestMapping(value = "/patientinfo/listpatients", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Patient> listPatients()    {
        ArrayList<Patient> patientList = new ArrayList<>();
//        iterate through all record in db, store relevant records in list and return to web ui
        return patientList;
    }

}
