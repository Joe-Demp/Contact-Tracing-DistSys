package ie.sds.resultsDiscovery.core;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;

// todo replace with the new version in the core
// todo before deleting: Add a constructor with a Patient parameter.
public class PatientResultCallWorkItem implements Serializable {
    private Instant created = Clock.systemUTC().instant();
    private Instant lastAccessed;
    private Status status = Status.TODO;
    private String patientId;
    private String name;
    private String phoneNumber;
    private TestResult result;

    public PatientResultCallWorkItem() {
    }

    public PatientResultCallWorkItem(String patientId, String name, String phoneNumber, TestResult result) {
        this.patientId = patientId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.result = result;
    }

    public PatientResultCallWorkItem(Patient patient) {
        this(patient.getId(), patient.getInfo().getFirstName(), patient.getInfo().getPhoneNumber(), patient.getResult());
    }

    /**
     * Updates the status of this WorkItem and records the update time.
     *
     * @param status the new status of the WorkItem
     */
    public void updateStatus(Status status) {
        this.status = status;
        lastAccessed = Clock.systemUTC().instant();
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Instant lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public enum Status {
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        DONE("Done");

        private String readable;

        Status(String readable) {
            this.readable = readable;
        }

        @Override
        public String toString() {
            return readable;
        }
    }
}