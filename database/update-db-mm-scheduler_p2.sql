ALTER TABLE `mm_scheduler`.`FilteredTaskRun` 
RENAME TO  `mm_scheduler`.`filtered_task_run` ;


ALTER TABLE `mm_scheduler`.`namibiaReport` 
CHANGE COLUMN `sourceImage` `source_image` VARCHAR(200) NULL DEFAULT NULL ,
CHANGE COLUMN `slicedImage` `sliced_image` VARCHAR(200) NULL DEFAULT NULL ,
CHANGE COLUMN `foundCount` `found_count` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `noFoundCount` `no_found_count` INT(11) NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`namibia_report` ;

ALTER TABLE `mm_scheduler`.`nimibiaImage` 
CHANGE COLUMN `gridImage` `grid_image` VARCHAR(100) NOT NULL DEFAULT '' , RENAME TO  `mm_scheduler`.`nimibia_image` ;


ALTER TABLE `mm_scheduler`.`reportTemplateTyphoonRuby` 
CHANGE COLUMN `reportTemplateID` `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `clientAppID` `client_app_id` BIGINT(20) UNSIGNED NOT NULL ,
CHANGE COLUMN `taskQueueID` `task_queue_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `taskID` `task_id` BIGINT(20) NOT NULL ,
CHANGE COLUMN `tweetID` `tweet_id` VARCHAR(500) NOT NULL , RENAME TO  `mm_scheduler`.`report_template_typhoon_ruby` ;

ALTER TABLE `mm_scheduler`.`taskRun` 
CHANGE COLUMN `updateInfo` `update_info` LONGTEXT NULL DEFAULT NULL ,
CHANGE COLUMN `duplicateInfo` `duplicate_info` LONGTEXT NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`task_run` ;

ALTER TABLE `mm_scheduler`.`typhoonRubyTextGeoClicker` 
CHANGE COLUMN `tweetID` `tweet_id` VARCHAR(300) NULL DEFAULT NULL ,
CHANGE COLUMN `finalTweetID` `final_tweet_id` VARCHAR(300) NULL DEFAULT NULL , RENAME TO  `mm_scheduler`.`typhoon_ruby_text_geo_clicker` ;



