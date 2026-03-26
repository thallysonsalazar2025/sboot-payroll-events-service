# sboot-payroll-events-service

Microserviço reativo responsável por armazenar e consolidar eventos trabalhistas que impactam o cálculo da folha.
Este serviço responde: "O que aconteceu com esse funcionário neste mês?"

Responsabilidade:
- Armazenar eventos (faltas, horas extras, atestados, férias, benefícios, descontos)
- Expor listagem e consolidação por período (apenas agregações)
- Ser totalmente desacoplado e preparado para event-driven (não realiza cálculos financeiros)

Stack:
- Java 21
- Spring Boot 3 (WebFlux)
- Spring Data R2DBC (H2 reativo)
- OpenAPI 3.0
- Lombok
- SLF4J + Logback

Endpoints:
- POST /api/v1/events
  - Headers: X-Company-Id (UUID) required
  - Body: PayrollEventRequest
  - Response: 201 PayrollEventResponse

- GET /api/v1/events?employeeId=&startDate=&endDate=
  - Headers: X-Company-Id (UUID) required
  - Response: list of PayrollEventResponse

- GET /api/v1/events/consolidated?employeeId=&period=YYYY-MM
  - Headers: X-Company-Id (UUID) required
  - Response: ConsolidatedResponse { totalHorasExtras, totalFaltas, totalDescontos, totalBeneficios }

Como rodar local:
1. mvn clean package
2. java -jar target/sboot-payroll-events-service-0.0.1-SNAPSHOT.jar

Exemplos (curl):
1) Criar evento
curl -X POST http://localhost:8080/api/v1/events \
  -H "Content-Type: application/json" \
  -H "X-Company-Id: aaaaaaaa-0000-0000-0000-000000000001" \
  -d '{
    "employeeId":"bbbbbbbb-0000-0000-0000-000000000001",
    "eventTypeCode":"HORA_EXTRA",
    "eventDate":"2026-03-10",
    "quantity":5,
    "amount":250.00,
    "description":"Hora extra noturna 50%"
  }'

2) Listar eventos
curl "http://localhost:8080/api/v1/events?employeeId=bbbbbbbb-0000-0000-0000-000000000001&startDate=2026-03-01&endDate=2026-03-31" \
  -H "X-Company-Id: aaaaaaaa-0000-0000-0000-000000000001"

3) Consolidado
curl "http://localhost:8080/api/v1/events/consolidated?employeeId=bbbbbbbb-0000-0000-0000-000000000001&period=2026-03" \
  -H "X-Company-Id: aaaaaaaa-0000-0000-0000-000000000001"

Observações:
- Não há autenticação. X-Company-Id e X-User-Id (opcional) são recebidos por header. O serviço filtra por companyId.
- Não há foreign keys para companyId/employeeId (desacoplamento).
- Regras financeiras ficam fora deste serviço.
- Próximo passo será adição de testes e CI (já incluídos neste commit).
