package br.com.example.payroll.absence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AuditableAbsenceTest {
    private static final Instant CREATED_AT = Instant.parse("2026-08-30T10:00:00Z");

    @Test
    void createsPendingAbsenceWithoutPayrollCalculationSemantics() {
        AuditableAbsence absence = absence();

        assertEquals(AbsenceStatus.PENDING_APPROVAL, absence.status());
        assertEquals("tenant-a", absence.tenantId());
        assertEquals("employee-1", absence.employeeId());
        assertEquals("COMMON_ABSENCE", absence.reasonCode());
    }

    @Test
    void recordsApprovalAuditMetadata() {
        AuditableAbsence approved = absence().decide(
                AbsenceStatus.APPROVED, "manager-1", CREATED_AT.plusSeconds(60));

        assertEquals(AbsenceStatus.APPROVED, approved.status());
        assertEquals("manager-1", approved.decidedBy());
    }

    @Test
    void preventsContradictorySecondDecision() {
        AuditableAbsence approved = absence().decide(
                AbsenceStatus.APPROVED, "manager-1", CREATED_AT.plusSeconds(60));

        assertThrows(IllegalStateException.class, () -> approved.decide(
                AbsenceStatus.REJECTED, "manager-2", CREATED_AT.plusSeconds(120)));
    }

    @Test
    void rejectsBlankJustification() {
        assertThrows(IllegalArgumentException.class, () -> AuditableAbsence.create(
                "tenant-a", "employee-1", LocalDate.of(2026, 8, 30),
                "COMMON_ABSENCE", " ", "manager-1", CREATED_AT));
    }

    private static AuditableAbsence absence() {
        return AuditableAbsence.create(
                "tenant-a", "employee-1", LocalDate.of(2026, 8, 30),
                "COMMON_ABSENCE", "Ausência registrada para tratamento auditável",
                "manager-1", CREATED_AT);
    }
}
