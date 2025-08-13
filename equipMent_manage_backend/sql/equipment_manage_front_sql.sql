CREATE DATABASE `equipment` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;



-- equipment.device definition

CREATE TABLE `device` (
                          `equipment_id` varchar(100) NOT NULL,
                          `equipment_name` varchar(100) NOT NULL,
                          `equipment_code` varchar(100) NOT NULL,
                          `equipment_img` varchar(100) NOT NULL,
                          `equipment_desc` varchar(500) NOT NULL,
                          `equipment_status` varchar(100) NOT NULL,
                          `equipment_tag` varchar(100) NOT NULL,
                          `equipment_create_time` varchar(100) NOT NULL,
                          `equipment_out_or_on_status` varchar(100) NOT NULL,
                          PRIMARY KEY (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- equipment.`user` definition

CREATE TABLE `user` (
                        `user_account` varchar(100) NOT NULL,
                        `user_name` varchar(100) NOT NULL,
                        `password` varchar(100) NOT NULL,
                        `login_time` varchar(100) DEFAULT NULL,
                        `root_type` varchar(100) DEFAULT NULL,
                        PRIMARY KEY (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- equipment.borrow_and_return_order definition

CREATE TABLE `borrow_and_return_order` (
                                           `order_id` varchar(100) NOT NULL,
                                           `user_id` varchar(100) NOT NULL,
                                           `user_name` varchar(100) NOT NULL,
                                           `device_id` varchar(100) NOT NULL,
                                           `device_name` varchar(100) NOT NULL,
                                           `borrow_time` varchar(100) NOT NULL,
                                           `return_time` varchar(100) NOT NULL,
                                           `order_type` varchar(100) NOT NULL,
                                           `device_code` varchar(100) NOT NULL,
                                           `op_type` varchar(100) NOT NULL,
                                           PRIMARY KEY (`order_id`),
                                           KEY `borrow_and_return_order_device_FK` (`device_id`),
                                           KEY `borrow_and_return_order_user_FK` (`user_id`),
                                           CONSTRAINT `borrow_and_return_order_device_FK` FOREIGN KEY (`device_id`) REFERENCES `device` (`equipment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                           CONSTRAINT `borrow_and_return_order_user_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_account`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- equipment.borrow_equipment_check definition

CREATE TABLE `borrow_equipment_check` (
                                          `check_id` varchar(100) NOT NULL,
                                          `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                          `equipment_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                          `equipment_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                          `check_time` varchar(100) NOT NULL,
                                          PRIMARY KEY (`check_id`),
                                          KEY `borrow_equipment_check_device_FK` (`equipment_id`),
                                          KEY `borrow_equipment_check_user_FK` (`user_id`),
                                          CONSTRAINT `borrow_equipment_check_device_FK` FOREIGN KEY (`equipment_id`) REFERENCES `device` (`equipment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                          CONSTRAINT `borrow_equipment_check_user_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_account`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- equipment.device_borrow definition

CREATE TABLE `device_borrow` (
                                 `device_borrow_user_id` varchar(100) NOT NULL,
                                 `device_borrow_device_id` varchar(100) NOT NULL,
                                 `order_id` varchar(100) NOT NULL,
                                 PRIMARY KEY (`device_borrow_device_id`),
                                 KEY `device_borrow_user_FK` (`device_borrow_user_id`),
                                 KEY `device_borrow_borrow_and_return_order_FK` (`order_id`),
                                 CONSTRAINT `device_borrow_borrow_and_return_order_FK` FOREIGN KEY (`order_id`) REFERENCES `borrow_and_return_order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                 CONSTRAINT `device_borrow_device_FK` FOREIGN KEY (`device_borrow_device_id`) REFERENCES `device` (`equipment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                 CONSTRAINT `device_borrow_user_FK` FOREIGN KEY (`device_borrow_user_id`) REFERENCES `user` (`user_account`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- equipment.device_problem definition

CREATE TABLE `device_problem` (
                                  `problem_id` varchar(100) NOT NULL,
                                  `user_account` varchar(100) NOT NULL,
                                  `equipment_id` varchar(100) NOT NULL,
                                  `reasons` varchar(100) NOT NULL,
                                  `detailed_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                  `order_id` varchar(100) NOT NULL,
                                  PRIMARY KEY (`problem_id`),
                                  KEY `device_problem_user_FK` (`user_account`),
                                  KEY `device_problem_device_FK` (`equipment_id`),
                                  KEY `device_problem_borrow_and_return_order_FK` (`order_id`),
                                  CONSTRAINT `device_problem_borrow_and_return_order_FK` FOREIGN KEY (`order_id`) REFERENCES `borrow_and_return_order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                  CONSTRAINT `device_problem_device_FK` FOREIGN KEY (`equipment_id`) REFERENCES `device` (`equipment_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- equipment.user_root definition

CREATE TABLE `user_root` (
                             `user_account` varchar(100) NOT NULL,
                             `user_password` varchar(100) NOT NULL,
                             `root_type` varchar(100) NOT NULL,
                             `user_name` varchar(100) NOT NULL,
                             `login_time` varchar(100) NOT NULL,
                             PRIMARY KEY (`user_account`),
                             CONSTRAINT `user_root_user_FK` FOREIGN KEY (`user_account`) REFERENCES `user` (`user_account`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;