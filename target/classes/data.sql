-- PayrollEventTypeConfig seed (UUIDs fixed)
INSERT INTO payroll_event_type_config (id, code, description, category, impacts_salary, is_discount, calculation_hint, active, created_at) VALUES
('00000000-0000-0000-0000-000000000101', 'HORA_EXTRA', 'Horas extras', 'PROVENTO', true, false, 'MULTIPLIER', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000102', 'FALTA', 'Faltas', 'DESCONTO', true, true, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000103', 'FERIAS', 'Férias', 'PROVENTO', true, false, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000104', 'ATESTADO', 'Atestado médico', 'INFORMATIVO', false, false, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000105', 'BENEF_VR', 'Vale Refeição', 'PROVENTO', true, false, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000106', 'BENEF_VA', 'Vale Alimentação', 'PROVENTO', true, false, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000107', 'BONUS', 'Bônus', 'PROVENTO', true, false, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000108', 'ADIANTAMENTO', 'Adiantamento salarial', 'DESCONTO', true, true, 'FIXED', true, CURRENT_TIMESTAMP()),
('00000000-0000-0000-0000-000000000109', 'PENSION', 'Pensão', 'DESCONTO', true, true, 'FIXED', true, CURRENT_TIMESTAMP());

-- Sample events (company and employees with fixed UUIDs)
INSERT INTO payroll_event (id, company_id, employee_id, event_type_code, description, event_date, quantity, amount, metadata, created_at, updated_at) VALUES
('11111111-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', 'HORA_EXTRA', 'Hora extra noturna 50%', DATE '2026-03-10', 5, 250.00, '{"rate":1.5}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('11111112-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', 'FALTA', 'Falta injustificada', DATE '2026-03-05', 1, 0.00, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('11111113-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000002', 'BENEF_VR', 'Vale refeição mensal', DATE '2026-03-01', 1, 200.00, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
