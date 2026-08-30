package br.com.example.payroll.absence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record AuditableAbsence(
        UUID id,
        String tenantId,
        String employeeId,
        LocalDate absenceDate,
        String reasonCode,
        String justification,
        String createdBy,
        Instant createdAt,
        AbsenceStatus status,
        String decidedBy,
        Instant decidedAt
) {
    public AuditableAbsence {
        Objects.requireNonNull(id, "id");
        tenantId = requireText(tenantId, "tenantId");
        employeeId = requireText(employeeId, "employeeId");
        Objects.requireNonNull(absenceDate, "absenceDate");
        reasonCode = requireText(reasonCode, "reasonCode");
        justification = requireText(justification, "justification");
        createdBy = requireText(createdBy, "createdBy");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(status, "status");
        if (status == AbsenceStatus.PENDING_APPROVAL && (decidedBy != null || decidedAt != null)) {
            throw new IllegalArgumentException("pending absence cannot have decision metadata");
        }
        if (status != AbsenceStatus.PENDING_APPROVAL) {
            decidedBy = requireText(decidedBy, "decidedBy");
            Objects.requireNonNull(decidedAt, "decidedAt");
        }
    }

    public static AuditableAbsence create(
            String tenantId,
            String employeeId,
            LocalDate absenceDate,
            String reasonCode,
            String justification,
            String createdBy,
            Instant createdAt
    ) {
        return new AuditableAbsence(UUID.randomUUID(), tenantId, employeeId, absenceDate, reasonCode,
                justification, createdBy, createdAt, AbsenceStatus.PENDING_APPROVAL, null, null);
    }

    public AuditableAbsence decide(AbsenceStatus decision, String actor, Instant at) {
        if (status != AbsenceStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("absence already decided");
        }
        if (decision != AbsenceStatus.APPROVED
                && decision != AbsenceStatus.REJECTED
                && decision != AbsenceStatus.CANCELLED) {
            throw new IllegalArgumentException("invalid terminal decision");
        }
        return new AuditableAbsence(id, tenantId, employeeId, absenceDate, reasonCode, justification,
                createdBy, createdAt, decision, actor, at);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
