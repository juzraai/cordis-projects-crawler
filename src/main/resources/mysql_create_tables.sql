create table if not exists cordis_call (
	`rcn`        VARCHAR(255),
	`identifier` VARCHAR(255),
	`title`      VARCHAR(20),
	PRIMARY KEY (`rcn`)
);

create table if not exists cordis_category (
	`code`               VARCHAR(255),
	`availableLanguages` VARCHAR(255),
	`title`              LONGTEXT,
	PRIMARY KEY (`code`)
);

create table if not exists cordis_project (
	`rcn`                 INT,
	`acronym`             VARCHAR(255),
	`availableLanguages`  VARCHAR(255),
	`contentCreationDate` DATE,
	`contentUpdateDate`   DATE,
	`contractDuration`    INT,
	`contractEndDate`     DATE,
	`contractStartDate`   DATE,
	`ecMaxContribution`   DOUBLE,
	`endDate`             DATE,
	`language`            VARCHAR(255),
	`lastUpdateDate`      DATE,
	`objective`           LONGTEXT,
	`reference`           VARCHAR(255),
	`sourceUpdateDate`    DATE,
	`startDate`           DATE,
	`status`              VARCHAR(255),
	`statusDetails`       VARCHAR(255),
	`teaser`              LONGTEXT,
	`title`               VARCHAR(255),
	`totalCost`           DOUBLE,
	PRIMARY KEY (`rcn`)
);

create table if not exists cordis_relation (
	`id`             VARCHAR(255),
	`ownerId`        VARCHAR(255),
	`ownerType`      VARCHAR(20),
	`ownedId`        VARCHAR(255), -- e.g. categories have code which is string
	`ownedType`      VARCHAR(20),
	`type`           VARCHAR(255),
	`classification` VARCHAR(255),
	`context`        BOOLEAN,
	`ecContribution` DOUBLE,
	`order`          INT,
	`terminated`     BOOLEAN,
	PRIMARY KEY (`id`),
	INDEX `owner_id_idx` (`ownerId`),
	INDEX `owned_id_idx` (`ownedId`)
);