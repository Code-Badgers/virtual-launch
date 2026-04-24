-- 1. 회원 데이터 삽입
INSERT INTO member (member_id, email, password, created_at, role)
VALUES (2, 'test@test.com', 'test', now(), 'ROLE_USER')
ON CONFLICT (member_id) DO NOTHING;

-- 2. 고성능 전문가용 노트북 (비전북 Pro 16)
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             1, 2, '비전북 Pro 16', 'M3 Max 칩셋 탑재 전문가용 노트북', 'https://example.com/vision-pro.png',
             '2026-10-15T09:00:00+09:00', 3500000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
                 "power_consumption": {"label": "소비 전력", "value": "140", "unit": "W"},
                 "weight": {"label": "무게", "value": "2.1", "unit": "kg"}
               },
               "product_specs": {
                 "processor": {"label": "프로세서(CPU)", "selectedOption": "M3 Max"},
                 "ram": {"label": "메모리(RAM)", "selectedOption": "32GB"},
                 "graphics": {"label": "그래픽(GPU)", "selectedOption": "RTX 4070"},
                 "storage": {"label": "저장장치(SSD)", "selectedOption": "1TB"}
               }
             }'::jsonb
         );

-- 3. 가성비 학생용 노트북 (에듀북 에어)
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             1, 2, '에듀북 에어', '가볍고 배터리 오래가는 학생용 가성비 모델', 'https://example.com/edubook.png',
             '2026-03-01T09:00:00+09:00', 1200000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
                 "power_consumption": {"label": "소비 전력", "value": "65", "unit": "W"},
                 "weight": {"label": "무게", "value": "1.2", "unit": "kg"}
               },
               "product_specs": {
                 "processor": {"label": "프로세서(CPU)", "selectedOption": "Intel i9-14세대"},
                 "ram": {"label": "메모리(RAM)", "selectedOption": "16GB"},
                 "graphics": {"label": "그래픽(GPU)", "selectedOption": "내장그래픽"},
                 "storage": {"label": "저장장치(SSD)", "selectedOption": "512GB"}
               }
             }'::jsonb
         );