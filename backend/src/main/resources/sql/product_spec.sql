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

-- 4. 가습기
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             2, 2, '퓨어 미스트 스마트 가습기', '자동 습도 조절 기능이 탑재된 4L 대용량 초음파 가습기', 'https://example.com/images/humidifier-pure.png',
             '2026-11-01T10:00:00+09:00', 89000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V", "value": "220"},
                 "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W", "value": 25},
                 "weight": {"label": "무게", "type": "number", "unit": "kg", "value": 1.5}
               },
               "product_specs": {
                 "humidification_method": {"label": "가습 방식", "type": "select", "options": ["초음파식", "가열식", "자연기화식"], "unit": "", "value": "초음파식"},
                 "tank_capacity": {"label": "물통 용량", "type": "number", "options": [], "unit": "L", "value": 4.0},
                 "max_output": {"label": "최대 가습량", "type": "number", "options": [], "unit": "cc/h", "value": 300}
               },
               "compliance": {
                 "kc_auth": {"label": "KC 인증 번호", "type": "string", "unit": "", "value": "HU07123-26001"},
                 "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년", "2년"], "unit": "", "value": "1년"}
               }
             }'::jsonb
         );

-- 5. 고데기
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             6, 2, '글램 뷰티 프로 스트레이트너', '모발 손상을 최소화하는 세라믹 코팅 전문가용 고데기', 'https://example.com/images/hair-styler.png',
             '2026-12-05T09:00:00+09:00', 120000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V", "value": "220"},
                 "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W", "value": 45},
                 "weight": {"label": "무게", "type": "number", "unit": "kg", "value": 0.35}
               },
               "product_specs": {
                 "plate_material": {"label": "발열판 소재", "type": "select", "options": ["세라믹 코팅", "티타늄", "토르말린"], "unit": "", "value": "세라믹 코팅"},
                 "max_temperature": {"label": "최고 온도", "type": "number", "options": [], "unit": "°C", "value": 200},
                 "heating_time": {"label": "예열 시간", "type": "number", "options": [], "unit": "초", "value": 30}
               },
               "compliance": {
                 "kc_auth": {"label": "KC 인증 번호", "type": "string", "unit": "", "value": "HD10293-26002"},
                 "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년"], "unit": "", "value": "1년"}
               }
             }'::jsonb
         );

-- 6. 헤어드라이기
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             6, 2, '에어로젯 BLDC 헤어드라이어', '항공기 모터 기술이 적용된 초고속 건조 드라이기', 'https://example.com/images/hair-dryer.png',
             '2026-11-20T09:00:00+09:00', 159000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V", "value": "220"},
                 "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W", "value": 1800},
                 "weight": {"label": "무게", "type": "number", "unit": "kg", "value": 0.45}
               },
               "product_specs": {
                 "motor_type": {"label": "모터 종류", "type": "select", "options": ["BLDC", "DC", "AC"], "unit": "", "value": "BLDC"},
                 "speed_levels": {"label": "풍속 조절", "type": "select", "options": ["2단", "3단", "4단"], "unit": "", "value": "3단"},
                 "cool_shot": {"label": "냉풍 기능", "type": "select", "options": ["지원", "미지원"], "unit": "", "value": "지원"}
               },
               "compliance": {
                 "kc_auth": {"label": "KC 인증 번호", "type": "string", "unit": "", "value": "HD10293-26003"},
                 "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년", "2년"], "unit": "", "value": "2년"}
               }
             }'::jsonb
         );

-- 7. 음식물처리기
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             5, 2, '에코 클린 미생물 처리기', '냄새 걱정 없는 친환경 미생물 발효 음식물 처리기', 'https://example.com/images/food-waste.png',
             '2027-01-10T10:00:00+09:00', 650000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V", "value": "220"},
                 "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W", "value": 60},
                 "weight": {"label": "무게", "type": "number", "unit": "kg", "value": 12.5}
               },
               "product_specs": {
                 "processing_method": {"label": "처리 방식", "type": "select", "options": ["미생물발효", "건조분쇄", "습식분쇄"], "unit": "", "value": "미생물발효"},
                 "daily_capacity": {"label": "1일 최대 처리량", "type": "number", "options": [], "unit": "kg", "value": 1.5},
                 "filter_type": {"label": "필터 종류", "type": "select", "options": ["복합 활성탄 탈취필터", "UV 살균 필터", "없음"], "unit": "", "value": "복합 활성탄 탈취필터"}
               },
               "compliance": {
                 "kc_auth": {"label": "KC 인증 번호", "type": "string", "unit": "", "value": "KA88234-27001"},
                 "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년", "2년"], "unit": "", "value": "1년"}
               }
             }'::jsonb
         );

-- 8. 공기청정기
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             3, 2, '브리즈 에어 360', '초미세먼지와 펫 냄새까지 잡아주는 360도 공기청정기', 'https://example.com/images/air-purifier.png',
             '2026-09-01T09:00:00+09:00', 420000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V", "value": "220"},
                 "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W", "value": 40},
                 "weight": {"label": "무게", "type": "number", "unit": "kg", "value": 8.2}
               },
               "product_specs": {
                 "coverage_area": {"label": "사용 면적", "type": "number", "options": [], "unit": "㎡", "value": 50},
                 "filter_grade": {"label": "헤파필터 등급", "type": "select", "options": ["H11", "H13", "H14"], "unit": "", "value": "H13"},
                 "sensor_type": {"label": "탑재 센서", "type": "select", "options": ["PM1.0", "PM2.5", "가스센서"], "unit": "", "value": "PM1.0"}
               },
               "compliance": {
                 "kc_auth": {"label": "KC 인증 번호", "type": "string", "unit": "", "value": "AP44912-26005"},
                 "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년", "2년", "3년"], "unit": "", "value": "2년"}
               }
             }'::jsonb
         );

-- 9. TV
INSERT INTO product_spec (
    category_id, member_id, product_name, product_description, product_image_url,
    planned_launch_date, planned_price,
    created_at, updated_at, required_specs
) VALUES (
             1, 2, '뷰맥스 65인치 스마트 OLED TV', '압도적인 명암비와 4K 해상도를 자랑하는 스마트 TV', 'https://example.com/images/tv-65oled.png',
             '2026-08-15T09:00:00+09:00', 1850000,
             now(), now(),
             '{
               "technical_specs": {
                 "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V", "value": "220"},
                 "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W", "value": 150},
                 "weight": {"label": "무게(스탠드 포함)", "type": "number", "unit": "kg", "value": 22.5}
               },
               "product_specs": {
                 "resolution": {"label": "해상도", "type": "select", "options": ["FHD", "4K UHD", "8K"], "unit": "", "value": "4K UHD"},
                 "panel_type": {"label": "패널 종류", "type": "select", "options": ["LED", "QLED", "OLED"], "unit": "", "value": "OLED"},
                 "refresh_rate": {"label": "주사율", "type": "select", "options": ["60Hz", "120Hz", "144Hz"], "unit": "", "value": "120Hz"}
               },
               "compliance": {
                 "kc_auth": {"label": "KC 인증 번호", "type": "string", "unit": "", "value": "TV99012-26008"},
                 "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년", "2년(패널 3년)"], "unit": "", "value": "2년(패널 3년)"}
               }
             }'::jsonb
         );