
--------------------------------------------------------------------------------
-- 1. Apple 2023 맥북프로 16 M3 Max
--------------------------------------------------------------------------------
INSERT INTO competitor_product (
category_id, brand_name, model_name, release_price, current_price,
competitor_product_image_url, release_date, average_star_rating,
total_review_count, competitor_product_url, last_crawled_at, created_at, updated_at, required_specs
) VALUES (
  (SELECT category_id FROM category WHERE category_name = '노트북' LIMIT 1),
      'Apple', '2023 맥북프로 16 M3 Max (36GB, SSD 1TB)', 4800000, 4560000,
     'https://img.danawa.com/prod_img/500000/123/456/1.jpg', '2023-11-01T00:00:00+09:00', 4.9,
     1250, 'https://prod.danawa.com/info/?pcode=12345678', now(), now(), now(),
     '{
       "technical_specs": {
         "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
         "power_consumption": {"label": "소비 전력", "value": "140", "unit": "W"},
         "dimensions": {"label": "가로x세로x두께", "value": "355.7x248.1x16.8", "unit": "mm"},
         "weight": {"label": "무게", "value": "2.16", "unit": "kg"}
       },
       "product_specs": {
         "processor": {"label": "프로세서(CPU)", "selectedOption": "M3 Max"},
         "ram": {"label": "메모리(RAM)", "selectedOption": "32GB"},
         "graphics": {"label": "그래픽(GPU)", "selectedOption": "내장그래픽"},
         "storage": {"label": "저장장치(SSD)", "selectedOption": "1TB"},
         "display_res": {"label": "해상도", "selectedOption": "4K(UHD)"},
         "refresh_rate": {"label": "주사율", "selectedOption": "120Hz"},
         "os": {"label": "운영체제", "selectedOption": "macOS Sonoma"}
       },
       "compliance": {
         "kc_auth": {"label": "KC 인증 번호", "value": "R-R-APL-A2991"},
         "warranty": {"label": "무상 보증 기간", "selectedOption": "1년"}
       }
     }'::jsonb
 );

--------------------------------------------------------------------------------
-- 2. LG전자 2024 그램 Pro 16 (RTX 3050 탑재)
--------------------------------------------------------------------------------
INSERT INTO competitor_product (
category_id, brand_name, model_name, release_price, current_price,
competitor_product_image_url, release_date, average_star_rating,
total_review_count, competitor_product_url, last_crawled_at, created_at, updated_at, required_specs
) VALUES ((SELECT category_id FROM category WHERE category_name = '노트북' LIMIT 1),
      'LG전자', '2024 그램 Pro 16 16Z90SP-GA7BK', 2540000, 2390000,
     'https://img.danawa.com/prod_img/500000/234/567/1.jpg', '2024-01-15T00:00:00+09:00', 4.8,
     850, 'https://prod.danawa.com/info/?pcode=23456789', now(), now(), now(),
     '{
       "technical_specs": {
         "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
         "power_consumption": {"label": "소비 전력", "value": "65", "unit": "W"},
         "dimensions": {"label": "가로x세로x두께", "value": "357x252x12.4", "unit": "mm"},
         "weight": {"label": "무게", "value": "1.19", "unit": "kg"}
       },
       "product_specs": {
         "processor": {"label": "프로세서(CPU)", "selectedOption": "Intel i9-14세대"},
         "ram": {"label": "메모리(RAM)", "selectedOption": "32GB"},
         "graphics": {"label": "그래픽(GPU)", "selectedOption": "RTX 4060"},
         "storage": {"label": "저장장치(SSD)", "selectedOption": "512GB"},
         "display_res": {"label": "해상도", "selectedOption": "QHD"},
         "refresh_rate": {"label": "주사율", "selectedOption": "144Hz"},
         "os": {"label": "운영체제", "selectedOption": "Windows 11 Home"}
       },
       "compliance": {
         "kc_auth": {"label": "KC 인증 번호", "value": "XU100702-23001A"},
         "warranty": {"label": "무상 보증 기간", "selectedOption": "1년"}
       }
     }'::jsonb
 );

--------------------------------------------------------------------------------
-- 3. 삼성전자 2024 갤럭시북4 Pro NT960XGK-K71A
--------------------------------------------------------------------------------
INSERT INTO competitor_product (
category_id, brand_name, model_name, release_price, current_price,
competitor_product_image_url, release_date, average_star_rating,
total_review_count, competitor_product_url, last_crawled_at, created_at, updated_at, required_specs
) VALUES ((SELECT category_id FROM category WHERE category_name = '노트북' LIMIT 1),
     '삼성전자', '갤럭시북4 Pro NT960XGK-K71A', 2150000, 1980000,
     'https://img.danawa.com/prod_img/500000/345/678/1.jpg', '2023-12-18T00:00:00+09:00', 4.7,
     2100, 'https://prod.danawa.com/info/?pcode=34567890', now(), now(), now(),
     '{
       "technical_specs": {
         "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
         "power_consumption": {"label": "소비 전력", "value": "65", "unit": "W"},
         "dimensions": {"label": "가로x세로x두께", "value": "355.4x250.4x12.5", "unit": "mm"},
         "weight": {"label": "무게", "value": "1.56", "unit": "kg"}
       },
       "product_specs": {
         "processor": {"label": "프로세서(CPU)", "selectedOption": "Intel i9-14세대"},
         "ram": {"label": "메모리(RAM)", "selectedOption": "16GB"},
         "graphics": {"label": "그래픽(GPU)", "selectedOption": "내장그래픽"},
         "storage": {"label": "저장장치(SSD)", "selectedOption": "512GB"},
         "display_res": {"label": "해상도", "selectedOption": "QHD"},
         "refresh_rate": {"label": "주사율", "selectedOption": "120Hz"},
         "os": {"label": "운영체제", "selectedOption": "Windows 11 Home"}
       },
       "compliance": {
         "kc_auth": {"label": "KC 인증 번호", "value": "R-R-SEC-NT960XGK"},
         "warranty": {"label": "무상 보증 기간", "selectedOption": "1년"}
       }
     }'::jsonb
 );

--------------------------------------------------------------------------------
-- 4. ASUS ROG 스트릭스 G18 (고성능 게이밍 노트북)
--------------------------------------------------------------------------------
INSERT INTO competitor_product (
category_id, brand_name, model_name, release_price, current_price,
competitor_product_image_url, release_date, average_star_rating,
total_review_count, competitor_product_url, last_crawled_at, created_at, updated_at, required_specs
) VALUES ((SELECT category_id FROM category WHERE category_name = '노트북' LIMIT 1),
     'ASUS', 'ROG 스트릭스 G18 G814JIR-N6002', 3290000, 3150000,
     'https://img.danawa.com/prod_img/500000/456/789/1.jpg', '2024-02-10T00:00:00+09:00', 4.6,
     420, 'https://prod.danawa.com/info/?pcode=45678901', now(), now(), now(),
     '{
       "technical_specs": {
         "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
         "power_consumption": {"label": "소비 전력", "value": "330", "unit": "W"},
         "dimensions": {"label": "가로x세로x두께", "value": "399x294x30.8", "unit": "mm"},
         "weight": {"label": "무게", "value": "3.00", "unit": "kg"}
       },
       "product_specs": {
         "processor": {"label": "프로세서(CPU)", "selectedOption": "Intel i9-14세대"},
         "ram": {"label": "메모리(RAM)", "selectedOption": "32GB"},
         "graphics": {"label": "그래픽(GPU)", "selectedOption": "RTX 4080"},
         "storage": {"label": "저장장치(SSD)", "selectedOption": "1TB"},
         "display_res": {"label": "해상도", "selectedOption": "QHD"},
         "refresh_rate": {"label": "주사율", "selectedOption": "240Hz"},
         "os": {"label": "운영체제", "selectedOption": "FreeDOS(미설치)"}
       },
       "compliance": {
         "kc_auth": {"label": "KC 인증 번호", "value": "R-R-ASU-G814J"},
         "warranty": {"label": "무상 보증 기간", "selectedOption": "1년"}
       }
     }'::jsonb
 );

--------------------------------------------------------------------------------
-- 5. HP 비터스 16 (가성비 게이밍/작업용)
--------------------------------------------------------------------------------
INSERT INTO competitor_product (
category_id, brand_name, model_name, release_price, current_price,
competitor_product_image_url, release_date, average_star_rating,
total_review_count, competitor_product_url, last_crawled_at, created_at, updated_at, required_specs
) VALUES ((SELECT category_id FROM category WHERE category_name = '노트북' LIMIT 1),
     'HP', '비터스 16-r0085TX', 1450000, 1290000,
     'https://img.danawa.com/prod_img/500000/567/890/1.jpg', '2023-08-20T00:00:00+09:00', 4.5,
     3100, 'https://prod.danawa.com/info/?pcode=56789012', now(), now(), now(),
     '{
       "technical_specs": {
         "rated_voltage": {"label": "정격 전압", "value": "100-240V", "unit": "V"},
         "power_consumption": {"label": "소비 전력", "value": "230", "unit": "W"},
         "dimensions": {"label": "가로x세로x두께", "value": "369x259x23.9", "unit": "mm"},
         "weight": {"label": "무게", "value": "2.35", "unit": "kg"}
       },
       "product_specs": {
         "processor": {"label": "프로세서(CPU)", "selectedOption": "Intel i9-14세대"},
         "ram": {"label": "메모리(RAM)", "selectedOption": "16GB"},
         "graphics": {"label": "그래픽(GPU)", "selectedOption": "RTX 4060"},
         "storage": {"label": "저장장치(SSD)", "selectedOption": "512GB"},
         "display_res": {"label": "해상도", "selectedOption": "FHD"},
         "refresh_rate": {"label": "주사율", "selectedOption": "144Hz"},
         "os": {"label": "운영체제", "selectedOption": "Windows 11 Home"}
       },
       "compliance": {
         "kc_auth": {"label": "KC 인증 번호", "value": "R-R-HPK-TPN-Q263"},
         "warranty": {"label": "무상 보증 기간", "selectedOption": "1년"}
       }
     }'::jsonb
 );
