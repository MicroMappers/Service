app.controller('ClickerAnswersCtrl', function ($scope, $stateParams, $location, ClickerAnswersService, data) {
	$scope.clientapp.id = $stateParams.id;
	$scope.clientapp.currentPath = $location.path();
	$scope.isEditMode = false;
	
	$scope.clientAppAnswer = data;
	
	
	$scope.vote_numbers = [1,2,3,4,5];
	$scope.active_answers = {};
	$scope.tempData  = {
		voteCutOff: null	
	};
	
	if ($scope.clientAppAnswer) {
		$scope.answers = angular.fromJson($scope.clientAppAnswer.answer);
		
		angular.forEach($scope.answers, function(obj) {
			if ($scope.clientAppAnswer.activeAnswerKey && 
					$scope.clientAppAnswer.activeAnswerKey.indexOf(obj.qa) >= 0) {
				$scope.active_answers[obj.qa] = "yes";
			} else {
				$scope.active_answers[obj.qa] = "no";
			}
		});
	}
	$scope.update = function() {
		$scope.activeAnswerKey = [];
		angular.forEach($scope.temp_active_answers, function(value, active_answer_key) {
			if(value == "yes") {
				$scope.activeAnswerKey.push({"qa": active_answer_key});
			}
		});
		
		$scope.tempClientAppAnswer = angular.copy($scope.clientAppAnswer);
		//$scope.tempClientAppAnswer.voteCutOff = $scope.tempVoteCutOff;
		$scope.tempClientAppAnswer.voteCutOff = $scope.tempData.voteCutOff;
		$scope.tempClientAppAnswer.activeAnswerKey = angular.toJson($scope.activeAnswerKey);
		console.log($scope.tempClientAppAnswer);
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
		
	};
	
	$scope.switchEditMode = function(mode) {
		if(mode) {
			$scope.temp_answers = angular.copy($scope.answers);
			$scope.temp_active_answers = angular.copy($scope.active_answers);
			//$scope.tempVoteCutOff = $scope.clientAppAnswer.voteCutOff;
			$scope.tempData.voteCutOff = $scope.clientAppAnswer.voteCutOff;
		}
		$scope.isEditMode = mode;
	};
	
	$scope.toggleValue = function(val) {
		$scope.temp_active_answers[val] = $scope.temp_active_answers[val] == 'yes' ? 'no' : 'yes';
	};
	
});
