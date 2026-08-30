# H11 — Falta comum auditável

Primeira fatia técnica: registrar a ausência como evento derivado auditável, sem aplicar cálculo trabalhista.

Guardrails:
- tenant e funcionário permanecem explícitos no domínio e devem vir de contexto autenticado na futura API;
- justificativa é obrigatória;
- decisão é única e auditável;
- estados internos são workflow de produto, não interpretação legal;
- desconto, tolerância, banco de horas, adicional ou regra coletiva ficam fora desta fatia;
- documentos de saúde/atestado não são armazenados aqui;
- persistência/API posteriores devem ser expand-only e negar acesso tenant A↔B.
