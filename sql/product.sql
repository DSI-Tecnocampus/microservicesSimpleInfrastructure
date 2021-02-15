DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(45) NOT NULL,
    `description` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO product (name, description) VALUES ('Soap','The finest soap you can face');
INSERT INTO product (name, description) VALUES ('After sun','When there is sun, there is aftersun');
