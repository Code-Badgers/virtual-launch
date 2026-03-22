-- always 중복 방지를 위한 ON CONFLICT DO UPDATE 설정 필요
TRUNCATE TABLE category RESTART IDENTITY CASCADE;

-- 공통 스펙을 담을 임시 테이블
CREATE TEMP TABLE temp_common_specs (specs jsonb);

-- 공통 스펙 temp_common_specs 정의
INSERT INTO temp_common_specs VALUES ('{
  "technical_specs": {
    "rated_voltage": {"label": "정격 전압", "type": "string", "unit": "V"},
    "power_consumption": {"label": "소비 전력", "type": "number", "unit": "W"},
    "dimensions": {"label": "가로x세로x두께", "type": "string", "unit": "mm"},
    "weight": {"label": "무게", "type": "number", "unit": "kg"}
  }
}');

----------------------- 컴퓨팅
INSERT INTO category (category_name, required_specs)
SELECT '노트북', specs ||'{
  "product_specs": {
    "processor": {"label": "프로세서(CPU)", "type": "select", "options": ["M3 Max", "Intel i9-14세대", "Ryzen 9"]},
    "ram": {"label": "메모리(RAM)", "type": "select", "options": ["8GB", "16GB", "32GB", "64GB"]},
    "graphics": {"label": "그래픽(GPU)", "type": "select", "options": ["내장그래픽", "RTX 4060", "RTX 4070", "RTX 4080"]},
    "storage": {"label": "저장장치(SSD)", "type": "select", "options": ["256GB", "512GB", "1TB", "2TB"]},
    "display_res": {"label": "해상도", "type": "select", "options": ["FHD", "QHD", "4K(UHD)"]},
    "refresh_rate": {"label": "주사율", "type": "select", "options": ["60Hz", "120Hz", "144Hz", "240Hz"]},
    "os": {"label": "운영체제", "type": "select", "options": ["Windows 11 Home", "Windows 11 Pro", "FreeDOS(미설치)"]}
  },
    "compliance": {
        "kc_auth": {"label": "KC 인증 번호", "type": "string"},
        "warranty": {"label": "무상 보증 기간", "type": "select", "options": ["1년", "2년"]}
    }
}' FROM temp_common_specs
ON CONFLICT (category_name) DO UPDATE SET required_specs = EXCLUDED.required_specs;


----------------------- 계절 가전
INSERT INTO category (category_name, required_specs)
SELECT '가습기', specs ||'{
    "product_specs": {
        "daily_capacity": {"label": "일일 제습량", "type": "number", "unit": "L"},
        "tank_size": {"label": "물통 용량", "type": "number", "unit": "L"},
        "noise": {"label": "소음 수치", "type": "number", "unit": "dB"}
    },
    "compliance": {
        "energy_grade": {"label": "에너지 등급", "type": "select", "options": ["1등급", "2등급", "3등급"]}
    }
}' FROM temp_common_specs
ON CONFLICT (category_name) DO UPDATE SET required_specs = EXCLUDED.required_specs;

INSERT INTO category (category_name, required_specs)
SELECT '공기 청정기', specs || '{
    "product_specs": {
        "coverage_area": {"label": "사용 면적", "type": "number", "unit": "㎡"},
        "filter_type": {"label": "필터 등급", "type": "select", "options": ["H13 헤파", "H14 헤파", "탈취필터"]}
    },
    "compliance": {
        "energy_grade": {"label": "에너지 등급", "type": "select", "options": ["1등급", "2등급", "3등급"]}
    }
}' FROM temp_common_specs
ON CONFLICT (category_name) DO UPDATE SET required_specs = EXCLUDED.required_specs;

----------------------- 생활 가전
INSERT INTO category (category_name, required_specs)
SELECT '무선 청소기', specs || '{
    "product_specs": {
        "suction_power": {"label": "흡입력", "type": "number", "unit": "AW"},
        "weight": {"label": "핸디형 무게", "type": "number", "unit": "kg"}
    },
    "compliance": {
        "kc_auth": {"label": "KC 인증", "type": "string"}
    }
}' FROM temp_common_specs
ON CONFLICT (category_name) DO UPDATE SET required_specs = EXCLUDED.required_specs;

----------------------- 주방 가전
INSERT INTO category (category_name, required_specs)
SELECT '냉장고', specs || '{
    "product_specs": {
        "total_volume": {"label": "전체 용량", "type": "number", "unit": "L"},
        "cooling_type": {"label": "냉각 방식", "type": "select", "options": ["간접냉각", "직접냉각"]}
    },
    "compliance": {
        "energy_grade": {"label": "에너지 등급", "type": "select", "options": ["1등급", "2등급", "3등급"]}
    }
}' FROM temp_common_specs
ON CONFLICT (category_name) DO UPDATE SET required_specs = EXCLUDED.required_specs;

----------------------- 이미용 가전
INSERT INTO category (category_name, required_specs)
SELECT '헤어드라이기', specs || '{
    "product_specs": {
        "wind_speed_steps": {"label": "풍속 조절", "type": "select", "options": ["2단", "3단", "4단 이상"]},
        "cool_shot": {"label": "냉풍 기능", "type": "select", "options": ["있음", "없음"]},
        "motor_type": {"label": "모터 종류", "type": "select", "options": ["DC모터", "BLDC모터", "AC모터"]}
    },
    "compliance": {
        "kc_auth": {"label": "KC 인증 번호", "type": "string"}
    }
}' FROM temp_common_specs
ON CONFLICT (category_name) DO UPDATE SET required_specs = EXCLUDED.required_specs;
