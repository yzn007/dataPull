
DROP TABLE IF EXISTS PARK_PARKING_LOT;CREATE TABLE IF NOT EXISTS PARK_PARKING_LOT(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`PARK_LOT_ID` VARCHAR(10),
`PARK_LOT_REGN_ID` VARCHAR(10),
`PARK_SPC_TOTL_QTY` INT,
`OCUP_PARK_SPC_QTY` INT,
`RESV_PARK_SPC_QTY` INT);
DROP TABLE IF EXISTS PARK_PARKING_LOT_HIS;CREATE TABLE IF NOT EXISTS PARK_PARKING_LOT_HIS(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`PARK_LOT_ID` VARCHAR(10),
`PARK_LOT_REGN_ID` VARCHAR(10),
`PARK_SPC_TOTL_QTY` INT,
`OCUP_PARK_SPC_QTY` INT,
`RESV_PARK_SPC_QTY` INT);
DROP TABLE IF EXISTS PARK_VEHIC_DRV_IN_EVT;CREATE TABLE IF NOT EXISTS PARK_VEHIC_DRV_IN_EVT(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`PLAT_NO` VARCHAR(10),
`PARK_LOT_ID` VARCHAR(10),
`PARK_LOT_REGN_ID` VARCHAR(10),
`DRV_IN_TM` DATETIME);
DROP TABLE IF EXISTS PARK_VEHIC_START_OUT_EVT;CREATE TABLE IF NOT EXISTS PARK_VEHIC_START_OUT_EVT(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`PLAT_NO` VARCHAR(10),
`PARK_LOT_ID` VARCHAR(10),
`PARK_LOT_REGN_ID` VARCHAR(10),
`DRV_IN_TM` DATETIME,
`START_OUT_TM` DATETIME,
`PARK_DURAT` LONG,
`PAY_MOD` INT,
`FEE_AMT` VARCHAR(8));
DROP TABLE IF EXISTS PARK_PARK_SPC_RESV_INFO;CREATE TABLE IF NOT EXISTS PARK_PARK_SPC_RESV_INFO(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`RESV_ID` INT,
`PLAT_NO` VARCHAR(10),
`RESV_PARK_LOT_ID` VARCHAR(10),
`RESV_PARK_LOT_REGN_ID` VARCHAR(10),
`RESV_ARRV_TM` DATETIME,
`RESV_STAT` VARCHAR(10));
DROP TABLE IF EXISTS BUS_VEHIC_LCTN_MSG;CREATE TABLE IF NOT EXISTS BUS_VEHIC_LCTN_MSG(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`APPID` VARCHAR(50),
`TYPE` VARCHAR(10),
`VEHID` BIGINT,
`VEHNUM` VARCHAR(20),
`PLATENUM` VARCHAR(50),
`LATITUDE` DECIMAL(24,9),
`LONGITUDE` DECIMAL(24,9),
`ANGLE` VARCHAR(50),
`SPEED` VARCHAR(50),
`UPDOWN` TINYINT,
`SITENUM` INT,
`MILAGE` DECIMAL(24,9),
`STATE` TINYINT,
`TIME` DATETIME,
`OWNROUTE_ID` BIGINT,
`OWNROUTE_CODE` VARCHAR(50),
`OWNROUTE_NAME` VARCHAR(50),
`RUNROUTE_ID` BIGINT,
`RUNROUTE_CODE` VARCHAR(50),
`RUNROUTE_NAME` VARCHAR(50));
DROP TABLE IF EXISTS BUS_VEHIC_ARRV_DEPRT_MSG;CREATE TABLE IF NOT EXISTS BUS_VEHIC_ARRV_DEPRT_MSG(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`APPID` VARCHAR(50),
`TYPE` VARCHAR(10),
`VEHID` BIGINT,
`VEHNUM` VARCHAR(20),
`PLATENUM` VARCHAR(50),
`LATITUDE` DECIMAL(24,9),
`LONGITUDE` DECIMAL(24,9),
`ANGLE` VARCHAR(50),
`SPEED` VARCHAR(50),
`UPDOWN` TINYINT,
`INOUT` TINYINT,
`SITENUM` INT,
`TIME` DATETIME,
`OWNROUTE_ID` BIGINT,
`OWNROUTE_CODE` VARCHAR(50),
`OWNROUTE_NAME` VARCHAR(50),
`RUNROUTE_ID` BIGINT,
`RUNROUTE_CODE` VARCHAR(50),
`RUNROUTE_NAME` VARCHAR(50));
DROP TABLE IF EXISTS BUS_ACCESS_TOKEN;CREATE TABLE IF NOT EXISTS BUS_ACCESS_TOKEN(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ACCESS_TOKEN` VARCHAR(50),
`EXPIRES_IN` TINYINT);
DROP TABLE IF EXISTS BUS_ROUTE;CREATE TABLE IF NOT EXISTS BUS_ROUTE(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ID` BIGINT,
`CODE` VARCHAR(30),
`NAME` VARCHAR(80),
`STATE` TINYINT,
`STARTSITE_NAME` VARCHAR(50),
`STARTSITE_LATITUDE` DECIMAL(24,9),
`STARTSITE_LONGITUDE` DECIMAL(24,9),
`ENDSITE_NAME` VARCHAR(50),
`ENDSITE_LATITUDE` DECIMAL(24,9),
`ENDSITE_LONGITUDE` DECIMAL(24,9),
`DEPARTTIME` DATETIME,
`RETURNTIME` DATETIME,
`TICKETPRICE` DECIMAL(24,2));
DROP TABLE IF EXISTS BUS_ROUTE_VEHICLE;CREATE TABLE IF NOT EXISTS BUS_ROUTE_VEHICLE(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ID` BIGINT,
`PLATENUM` VARCHAR(50),
`VEHNUM` VARCHAR(20),
`POSITION_LAT` DECIMAL(24,9),
`POSITION_LNG` DECIMAL(24,9),
`POSITION_DIRECT` TINYINT,
`POSITION_SITE` INT);
DROP TABLE IF EXISTS BUS_STATION;CREATE TABLE IF NOT EXISTS BUS_STATION(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ROUTEID` BIGINT,
`ROUTECODE` VARCHAR(50),
`ROUTENAME` VARCHAR(50),
`SITES_NAME` VARCHAR(50),
`SITES_DIRECT` TINYINT,
`SITES_NUM` VARCHAR(50),
`SITES_LAT` DECIMAL(24,9),
`SITES_LNG` DECIMAL(24,9),
`SITES_MILAGE` DECIMAL(24,9),
`SITES_ATTR` INT,
`SITES_TRACK` VARCHAR(50));
DROP TABLE IF EXISTS BUS_VEHICLE;CREATE TABLE IF NOT EXISTS BUS_VEHICLE(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ID` BIGINT,
`VEHNUM` VARCHAR(20),
`PLATENUM` VARCHAR(50),
`OWNROUTE_ID` BIGINT,
`OWNROUTE_CODE` VARCHAR(50),
`OWNROUTE_NAME` VARCHAR(50),
`RUNROUTE_ID` BIGINT,
`RUNROUTE_CODE` VARCHAR(50),
`RUNROUTE_NAME` VARCHAR(50));
DROP TABLE IF EXISTS BUS_ROUTE_DISPT;CREATE TABLE IF NOT EXISTS BUS_ROUTE_DISPT(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ROUTEID` BIGINT,
`ROUTECODE` VARCHAR(50),
`ROUTENAME` VARCHAR(50));
DROP TABLE IF EXISTS BUS_ROUTE_DISPT_DTL;CREATE TABLE IF NOT EXISTS BUS_ROUTE_DISPT_DTL(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ROUTEID` BIGINT,
`TIMES_TIME` TIME,
`TIMES_VEHNUM` VARCHAR(20),
`TIMES_PLATENUM` VARCHAR(50));
DROP TABLE IF EXISTS WEATHER_WEAT_FORE;CREATE TABLE IF NOT EXISTS WEATHER_WEAT_FORE(`DT` DATE,
`WEAT` VARCHAR(50),
`TEMPT` VARCHAR(50));
DROP TABLE IF EXISTS CQEXPO_EXHIBITOR;CREATE TABLE IF NOT EXISTS CQEXPO_EXHIBITOR(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`LCTN_ID` VARCHAR(50),
`EXHTR_NM` VARCHAR(255),
`EXHTR_FULL_NM` VARCHAR(255),
`EXHTR_SHT_NM` VARCHAR(255),
`EXHTR_EN_NM` VARCHAR(255),
`CTY` VARCHAR(50),
`PROV` VARCHAR(50),
`URBN` VARCHAR(50),
`FLR` VARCHAR(50),
`VEN` VARCHAR(50),
`REGN` VARCHAR(50),
`INDS` VARCHAR(50));
DROP TABLE IF EXISTS CQEXPO_SUPRT_FACIL_INFO;CREATE TABLE IF NOT EXISTS CQEXPO_SUPRT_FACIL_INFO(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`FACIL_CATE` VARCHAR(50),
`FEATUREID` VARCHAR(50),
`FACIL_NM` VARCHAR(255),
`BELG_LCTN` VARCHAR(50),
`FLR` VARCHAR(50));
DROP TABLE IF EXISTS GATE_EXPO_AUDI_INFO;CREATE TABLE IF NOT EXISTS GATE_EXPO_AUDI_INFO(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ID` VARCHAR(50),
`CNAME` VARCHAR(50),
`ENAME` VARCHAR(50),
`NATIONALITY` VARCHAR(50),
`CERTIFICATENUM` VARCHAR(50),
`CERTIFICATETYPE` VARCHAR(50),
`GENDER` VARCHAR(50),
`INSTITUTION` VARCHAR(255),
`PHONE` VARCHAR(50),
`POSITION` VARCHAR(50),
`HEADURL` VARCHAR(255),
`ROLETYPE` VARCHAR(50),
`VAPNAME` VARCHAR(50),
`VAPPHONE` VARCHAR(50),
`CERTIFICATELEVEL` VARCHAR(50),
`ISMEETING` VARCHAR(50),
`DOCKINGORGUSERNAME` VARCHAR(50),
`DOCKINGORGUSERPHONE` VARCHAR(50),
`DOCKINGORGNAME` VARCHAR(50),
`TRAVEL` VARCHAR(50),
`CARNUMBER` VARCHAR(50));
DROP TABLE IF EXISTS GATE_PRTC_ACT_INFO;CREATE TABLE IF NOT EXISTS GATE_PRTC_ACT_INFO(`ID` VARCHAR(50),
`ACT_ID` VARCHAR(50),
`ACTIVITYNAME` VARCHAR(255),
`JOINTIME` VARCHAR(50));
DROP TABLE IF EXISTS GATE_GATE_EVT;CREATE TABLE IF NOT EXISTS GATE_GATE_EVT(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`ACTIVITYNAME` VARCHAR(50),
`BARCODE` VARCHAR(255),
`CARDNO` VARCHAR(50),
`CHECKTIME` VARCHAR(50),
`ACCESSPOINT` VARCHAR(50));
DROP TABLE IF EXISTS GATE_AREA_INFO;CREATE TABLE IF NOT EXISTS GATE_AREA_INFO(`ACCESSAREA` VARCHAR(50),
`ACCESSAREA_NM` VARCHAR(50),
`ACCESSPOINT_CNT` INT,
`ACCESSPOINT` VARCHAR(50),
`FLOOR` VARCHAR(50));
DROP TABLE IF EXISTS CQEXPOAPP_EXPO_AUDI_INFO;CREATE TABLE IF NOT EXISTS CQEXPOAPP_EXPO_AUDI_INFO(`DATA_TM` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`RGST_ID` VARCHAR(50),
`ROLETYPE` VARCHAR(50),
`TRVL_WAY` VARCHAR(50));