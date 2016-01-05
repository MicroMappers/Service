ALTER TABLE `mm_scheduler`.`clientApp` 
CHANGE COLUMN `clientAppID` `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `clientID` `client_id` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `crisisID` `crisis_id` BIGINT(20) UNSIGNED NULL DEFAULT NULL ,
CHANGE COLUMN `nominalAttributeID` `nominal_attribute_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `platformAppID` `platform_app_id` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `shortName` `short_name` VARCHAR(50) NOT NULL ,
CHANGE COLUMN `taskRunsPerTask` `task_run_per_task` INT(10) UNSIGNED NOT NULL DEFAULT '1' ,
CHANGE COLUMN `iconURL` `icon_url` VARCHAR(200) NULL DEFAULT 'http://i.imgur.com/lgZAWIc.png' ,
CHANGE COLUMN `appType` `app_type` INT(11) NULL DEFAULT '1' ,
CHANGE COLUMN `isCustom` `is_custom` INT(1) NOT NULL , RENAME TO  `mm_scheduler`.`client_app` ;

ALTER TABLE `mm_scheduler`.`client_app` 
CHANGE COLUMN `tcProjectId` `tc_project_id` INT(11) NULL DEFAULT NULL ;

ALTER TABLE `mm_scheduler`.`client_app` 
CHANGE COLUMN `is_custom` `is_custom` INT(1) NOT NULL DEFAULT 0 ;

ALTER TABLE `mm_scheduler`.`clientAppAnswer` 
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `voteCutOff` `vote_cut_off` INT(10) UNSIGNED NOT NULL DEFAULT '2' ,
CHANGE COLUMN `activeAnswerKey` `active_answer_key` TEXT NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`client_app_answer` ;

ALTER TABLE `mm_scheduler`.`clientAppDeployment` 
CHANGE COLUMN `deploymentID` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`client_app_deployment` ;

ALTER TABLE `mm_scheduler`.`clientAppEvent` 
CHANGE COLUMN `clientAppEventID` `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `eventID` `event_id` BIGINT(20) NOT NULL , RENAME TO  `mm_scheduler`.`client_app_event` ;

ALTER TABLE `mm_scheduler`.`clientAppSource` 
CHANGE COLUMN `clientAppSourceID` `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `sourceURL` `source_url` VARCHAR(200) NOT NULL , RENAME TO  `mm_scheduler`.`client_app_source` ;

ALTER TABLE `mm_scheduler`.`crisis` 
CHANGE COLUMN `crisisID` `external_collection_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `crisisName` `crisis_name` VARCHAR(50) NULL DEFAULT NULL ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `displayName` `display_name` VARCHAR(100) NOT NULL ,
CHANGE COLUMN `activationStart` `activation_start` TIMESTAMP NULL DEFAULT NULL ,
CHANGE COLUMN `activationEnd` `activation_end` TIMESTAMP NULL DEFAULT NULL ,
CHANGE COLUMN `clickerType` `clicker_type` VARCHAR(100) NULL DEFAULT NULL ,
CHANGE COLUMN `refreshInMinute` `refresh_in_minute` INT(11) NOT NULL DEFAULT '60' ;


ALTER TABLE `mm_scheduler`.`crisis` 
CHANGE COLUMN `crisisID` `collection_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `displayName` `display_name` VARCHAR(100) NOT NULL ,
CHANGE COLUMN `activationStart` `activation_start` TIMESTAMP NULL DEFAULT NULL ,
CHANGE COLUMN `activationEnd` `activation_end` TIMESTAMP NULL DEFAULT NULL ,
CHANGE COLUMN `clickerType` `clicker_type` VARCHAR(100) NULL DEFAULT NULL ;



ALTER TABLE `mm_scheduler`.`imageMetaData` 
CHANGE COLUMN `fileName` `file_name` VARCHAR(150) NOT NULL ,
CHANGE COLUMN `sourceType` `source_type` INT(11) NOT NULL DEFAULT '1' , RENAME TO  `mm_scheduler`.`image_meta_data` ;

ALTER TABLE `mm_scheduler`.`markerStyle` 
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `appType` `app_type` VARCHAR(25) NOT NULL ,
CHANGE COLUMN `isDefault` `is_default` INT(1) NOT NULL , RENAME TO  `mm_scheduler`.`marker_style` ;

ALTER TABLE `mm_scheduler`.`reportTemplate` 
CHANGE COLUMN `reportTemplateID` `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `taskQueueID` `task_queue_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `taskID` `task_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `tweetID` `tweet_id` VARCHAR(500) NOT NULL , RENAME TO  `mm_scheduler`.`report_template` ;

ALTER TABLE `mm_scheduler`.`taskLog` 
CHANGE COLUMN `taskQueueID` `task_queue_id` BIGINT(20) UNSIGNED NOT NULL , RENAME TO  `mm_scheduler`.`task_log` ;


ALTER TABLE `mm_scheduler`.`taskQueue` 
CHANGE COLUMN `taskQueueID` `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `taskID` `task_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `documentID` `document_id` BIGINT(20) NULL DEFAULT NULL ,
CHANGE COLUMN `clientAppSourceID` `client_app_source_id` BIGINT(20) NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`task_queue` ;

ALTER TABLE `mm_scheduler`.`taskQueueResponse` 
CHANGE COLUMN `taskQueueID` `task_queue_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `taskInfo` `task_info` TEXT NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`task_queue_response` ;

ALTER TABLE `mm_scheduler`.`userToken` 
RENAME TO  `mm_scheduler`.`user_token` ;

ALTER TABLE `mm_scheduler`.`client` 
CHANGE COLUMN `clientID` `ID` INT(20) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `aidrUserID` `aidr_user_id` BIGINT(20) UNSIGNED NULL DEFAULT NULL ,
CHANGE COLUMN `hostURL` `host_url` VARCHAR(200) NOT NULL ,
CHANGE COLUMN `hostAPIKey` `host_api_key` VARCHAR(200) NULL DEFAULT NULL ,
CHANGE COLUMN `queueSize` `queue_size` INT(10) UNSIGNED NOT NULL DEFAULT '50' ,
CHANGE COLUMN `aidrHostURL` `aidr_host_url` VARCHAR(100) NULL DEFAULT NULL ,
CHANGE COLUMN `defaultTaskRunsPerTask` `default_task_run_per_task` INT(10) UNSIGNED NOT NULL DEFAULT '3' ;






