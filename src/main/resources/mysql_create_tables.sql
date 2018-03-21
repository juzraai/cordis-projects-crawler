create table if not exists cordis_call (
	`rcn`        INT,
	`identifier` VARCHAR(255),
	`title`      LONGTEXT,
	PRIMARY KEY (`rcn`)
);

create table if not exists cordis_category (
	`code`               VARCHAR(255),
	`availableLanguages` VARCHAR(255),
	`title`              LONGTEXT,
	PRIMARY KEY (`code`)
);

create table if not exists cordis_organization (
	`rcn`                    INT,
	`addressCity`            VARCHAR(255),
	`addressCountry`         VARCHAR(255),
	`addressEmail`           VARCHAR(255),
	`addressFaxNumber`       VARCHAR(255),
	`addressGeolocation`     VARCHAR(255),
	`addressPostalCode`      VARCHAR(255),
	`addressPostBox`         VARCHAR(255),
	`addressStreet`          VARCHAR(255),
	`addressTelephoneNumber` VARCHAR(255),
	`addressUrl`             VARCHAR(255),
	`availableLanguages`     VARCHAR(255),
	`departmentNames`        LONGTEXT,
	`description`            LONGTEXT,
	`id`                     INT,
	`legalName`              LONGTEXT,
	`otherDepartmentName`    LONGTEXT,
	`shortName`              VARCHAR(255),
	`vatNumber`              VARCHAR(255),
	PRIMARY KEY (`rcn`)
);

create table if not exists cordis_person (
	`rcn`                    INT,
	`addressCity`            VARCHAR(255),
	`addressCountry`         VARCHAR(255),
	`addressEmail`           VARCHAR(255),
	`addressFaxNumber`       VARCHAR(255),
	`addressGeolocation`     VARCHAR(255),
	`addressPostalCode`      VARCHAR(255),
	`addressPostBox`         VARCHAR(255),
	`addressStreet`          VARCHAR(255),
	`addressTelephoneNumber` VARCHAR(255),
	`addressUrl`             VARCHAR(255),
	`availableLanguages`     VARCHAR(255),
	`firstNames`             VARCHAR(255),
	`lastName`               VARCHAR(255),
	`title`                  VARCHAR(255),
	PRIMARY KEY (`rcn`)
);

create table if not exists cordis_programme (
	`rcn`                INT,
	`parent`             INT,
	`availableLanguages` VARCHAR(255),
	`code`               VARCHAR(255),
	`frameworkProgramme` VARCHAR(255),
	`pga`                VARCHAR(255),
	`shortTitle`         VARCHAR(255),
	`title`              LONGTEXT,
	`url`                VARCHAR(255),
	PRIMARY KEY (`rcn`)
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
	`title`               LONGTEXT,
	`totalCost`           DOUBLE,
	PRIMARY KEY (`rcn`)
);

create table if not exists cordis_region (
	`rcn`      INT,
	`euCode`   VARCHAR(255),
	`isoCode`  VARCHAR(255),
	`name`     VARCHAR(255),
	`nutsCode` VARCHAR(255),
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

create table if not exists cordis_webitem (
	`id`                 VARCHAR(255),
	`availableLanguages` VARCHAR(255),
	`language`           VARCHAR(255),
	`mimetype`           VARCHAR(255),
	`size`               INT,
	`title`              VARCHAR(255),
	`type`               VARCHAR(255),
	`uri`                VARCHAR(255),
	`url`                VARCHAR(255),
	PRIMARY KEY (`id`)
);