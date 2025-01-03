-- 회원 테이블
CREATE TABLE `MEMBERS` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '회원 고유 ID',
    `email` VARCHAR(255) NOT NULL UNIQUE COMMENT '이메일',
    `password` VARCHAR(255) NOT NULL COMMENT '비밀번호',
    `name` VARCHAR(255) NOT NULL COMMENT '이름',
    `phone_number` VARCHAR(255) NOT NULL COMMENT '연락처',
    `address1` VARCHAR(255) NOT NULL COMMENT '시도주소',
    `address2` VARCHAR(255) NOT NULL COMMENT '상세주소',
    `balance` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '잔액',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '회원 생성 일자',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '회원 수정 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '탈퇴 여부',
    `role` ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER' COMMENT '회원 역할'
);

-- 콘서트 정보 테이블
CREATE TABLE `CONCERTS` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '콘서트 고유 ID',
    `title` VARCHAR(255) NOT NULL COMMENT '콘서트 제목',
    `description` TEXT NOT NULL COMMENT '콘서트 상세 설명',
    `location` VARCHAR(255) NOT NULL COMMENT '콘서트 장소',
    `play_start_at` DATE NOT NULL COMMENT '공연 일정 시작 일자',
    `play_end_at` DATE COMMENT '공연 일정 종료 일자',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '콘서트 등록 일자',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '콘서트 수정 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부'
);

-- 콘서트 일정 테이블
CREATE TABLE `CONCERT_SCHEDULES` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '콘서트 일정 고유 ID',
    `concert_id` BIGINT UNSIGNED NOT NULL COMMENT '콘서트 ID (CONCERTS 테이블 참조)',
    `schedule_at` DATETIME NOT NULL COMMENT '콘서트 일시',
    `running_time` INT NOT NULL COMMENT '관람시간',
    `total_seat` INT NOT NULL COMMENT '전체 좌석 수',
    `remain_seat` INT NOT NULL COMMENT '남은 좌석 수',
    `total_seat_status` enum('SOLD_OUT','AVAILABLE') DEFAULT 'AVAILABLE' NOT NULL COMMENT '전체 좌석 상태',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '일정 생성 일자',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '일정 수정 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    FOREIGN KEY (`concert_id`) REFERENCES `CONCERTS` (`id`)
);

-- 콘서트 좌석 테이블
CREATE TABLE `SEATS` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '좌석 고유 ID',
    `schedule_id` BIGINT UNSIGNED NOT NULL COMMENT '콘서트 일정 ID(CONCERT_SCHEDULES 테이블 참조)',
    `seat_number` INT NOT NULL COMMENT '좌석 번호',
    `price` DECIMAL(10, 0) NOT NULL DEFAULT '0' COMMENT '좌석 가격',
    `status` ENUM('AVAILABLE', 'TEMP_RESERVED', 'RESERVED') NOT NULL COMMENT '좌석 상태(예약 가능/임시 예약/예약 완료)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좌석 생성 일자',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '좌석 정보 수정 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    UNIQUE KEY `unique_schedule_seat` (`schedule_id`, `seat_number`),-- 동일한 schedule_id 내에서 좌석 번호가 중복되지 않도록 유니크 키 추가
    FOREIGN KEY (`schedule_id`) REFERENCES `CONCERT_SCHEDULES` (`id`)
);

-- 예약 테이블
CREATE TABLE `RESERVATIONS` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '예약 고유 ID',
    `member_id` BIGINT UNSIGNED NOT NULL COMMENT '회원 ID (MEMBERS 테이블 참조)',
    `schedule_id` BIGINT UNSIGNED NOT NULL COMMENT '콘서트 일정 ID(CONCERT_SCHEDULE 테이블 참조)',
    `seat_id` BIGINT UNSIGNED NOT NULL COMMENT '좌석 ID (SEATS 테이블 참조)',
    `status` ENUM('TEMP_RESERVED', 'RESERVED', 'CANCELED') NOT NULL COMMENT '예약 상태(예약 대기 중/예약 확정/예약 취소)',
    `reserved_at` DATETIME NOT NULL COMMENT '예약 일자',
    `created_at` DATETIME NOT NULL COMMENT '예약 생성 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    FOREIGN KEY (`member_id`) REFERENCES `MEMBERS` (`id`),
    FOREIGN KEY (`schedule_id`) REFERENCES `CONCERT_SCHEDULES` (`id`),
    FOREIGN KEY (`seat_id`) REFERENCES `SEATS` (`id`)
);


-- 결제 테이블
CREATE TABLE `PAYMENTS` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '결제 고유 ID',
    `member_id` BIGINT UNSIGNED NOT NULL COMMENT '회원 ID(MEMBERS 테이블 참조)',
    `reservation_id` BIGINT UNSIGNED NOT NULL COMMENT '예약 ID(RESERVATIONS 테이블 참조)',
    `price` DECIMAL(10, 0) NOT NULL COMMENT '결제 금액',
    `status` ENUM('PROGRESS', 'DONE', 'CANCELED', 'REFUNDED') NOT NULL DEFAULT 'PROGRESS' COMMENT '결제 상태(결제 진행중/결제 완료/결제 실패/환불 완료)',
    `paid_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '결제 완료 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    FOREIGN KEY (`reservation_id`) REFERENCES `RESERVATIONS` (`id`),
    FOREIGN KEY (`member_id`) REFERENCES `MEMBERS` (`id`)
);

-- 알림 테이블
CREATE TABLE `NOTIFICATIONS` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '알림 고유 ID',
    `member_id` BIGINT UNSIGNED NOT NULL COMMENT '회원 ID(MEMBERS 테이블 참조)',
    `message` TEXT NOT NULL COMMENT '알림 메시지 내용',
    `notification_type` ENUM(
        'RESERVATION_CONFIRMED',
        'RESERVATION_CANCELLED',
        'PAYMENT_RECEIVED',
        'NOTIFICATION_FAIL'
    ) NOT NULL COMMENT '알림 유형(예약 확정 알림/예약 취소 알림/결제 완료 알림/알림 전송 실패)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '알림 생성 일자',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    FOREIGN KEY (`member_id`) REFERENCES `MEMBERS` (`id`)
);