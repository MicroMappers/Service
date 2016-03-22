app.controller('ClickerStylesCtrl', function ($scope, $stateParams, $location, ClickerStylesService, 
		clickerAnswers, clickerStyles) {
	
	$scope.clientapp.id = $stateParams.id;
	$scope.clientapp.currentPath = $location.path();
	$scope.isEditMode = false;
	
	$scope.clientAppAnswer = clickerAnswers;
	$scope.answers = angular.fromJson($scope.clientAppAnswer.activeAnswerKey);
	
	$scope.clickerStyles = clickerStyles;

	$scope.tempStyle = {'type': '', 'style': []};
	$scope.loadData = function() {
		if($scope.clickerStyles.id == null) {
			$scope.clickerStyles = {
				'clientAppID': $scope.clientapp.id,
				'appType': '',
				'style': angular.toJson($scope.tempStyle),
				'default': false
			};			
		}
		
		console.log($scope.clickerStyles);
		
		$scope.styleJson = angular.fromJson($scope.clickerStyles.style);
		if(!$scope.styleJson) {
			$scope.clickerStyles.style = "{'type':'Aerial', 'style' : []}";
			$scope.styleJson = angular.fromJson($scope.clickerStyles).style;		
		}
		
		$scope.styleMapByLabel = {};
		angular.forEach($scope.styleJson.style, function(obj) {
			$scope.styleMapByLabel[obj.label] = {
					"label_code": obj.label_code,
					"markerColor": obj.markerColor
			};
		});
	};
	$scope.loadData();
	
	$scope.update = function() {
		$scope.tempClickerStyles = angular.copy($scope.clickerStyles);
		$scope.tempClickerStyles.appType = $scope.tempAppType;
		$scope.tempStyleJson = {"type": $scope.tempClickerStyles.appType, "style": []};
		angular.forEach($scope.tempStyleMapByLabel, function(obj, key) {
			$scope.tempStyleJson.style.push({
				"label" : key,
				"label_code" : obj.label_code,
				"markerColor": obj.markerColor
			});
		});
		$scope.tempClickerStyles.style = angular.toJson($scope.tempStyleJson);
		
		if($scope.tempClickerStyles) {
			ClickerStylesService.save($scope.tempClickerStyles).$promise.then(function(data){
				$scope.clickerStyles = angular.copy($scope.tempClickerStyles);
				$scope.loadData();
				$scope.isEditMode = false;
			});
		} else {
			ClickerStylesService.update($scope.tempClickerStyles).$promise.then(function(data){
				$scope.clickerStyles = angular.copy($scope.tempClickerStyles);
				$scope.loadData();
				$scope.isEditMode = false;
			});
		}
		
		
	};
	
	$scope.switchEditMode = function(mode) {
		if(mode) {
			$scope.tempStyleMapByLabel = angular.copy($scope.styleMapByLabel);
			$scope.tempAppType = $scope.clickerStyles.appType;
		}
		$scope.isEditMode = mode;
	};
	
	/*$scope.update = function() {
		
		$scope.styleMapByLabel = angular.copy($scope.tempStyleMapByLabel);
		return;
		$scope.activeAnswerKey = [];
		angular.forEach($scope.temp_active_answers, function(value, active_answer_key) {
			if(value == "yes") {
				$scope.activeAnswerKey.push({"qa": active_answer_key});
			}
		});
		
		$scope.tempClientAppAnswer = angular.copy($scope.clientAppAnswer);
		$scope.tempClientAppAnswer.voteCutOff = $scope.tempVoteCutOff;
		$scope.tempClientAppAnswer.activeAnswerKey = angular.toJson($scope.activeAnswerKey);
		ClickerAnswersService.update($scope.tempClientAppAnswer).$promise.then(function(data){
			$scope.clientAppAnswer = angular.copy($scope.tempClientAppAnswer);
			$scope.active_answers = {};
			angular.forEach($scope.answers, function(obj) {
			  if($scope.clientAppAnswer.activeAnswerKey.indexOf(obj.qa) >= 0) {
				  $scope.active_answers[obj.qa] = "yes";
			  } else {
				  $scope.active_answers[obj.qa] = "no";
			  }
			});
			$scope.isEditMode = false;
		});
	  };*/
});
