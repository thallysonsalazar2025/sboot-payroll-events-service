CREATE TABLE IF NOT EXISTS payroll_event_type_config (
  id UUID PRIMARY KEY,
  code VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(255),
  category VARCHAR(50),
  impacts_salary BOOLEAN,
  is_discount BOOLEAN,
  calculation_hint VARCHAR(50),
  active BOOLEAN,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payroll_event (
  id UUID PRIMARY KEY,
  company_id UUID,
  employee_id UUID,
  event_type_code VARCHAR(100),
  description VARCHAR(255),
  event_date DATE,
  quantity DECIMAL,
  amount DECIMAL,
  metadata CLOB,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
