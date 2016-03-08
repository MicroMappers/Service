app.controller('ClickerDetailCtrl', function ($scope, ClickersService, CrisisService, data) {
	
	$scope.client_app_status = ["Pending", "AIDR Only", "Micromappers Only", "Inactive", "Disabled", "AIDR Micromappers Both"];
	$scope.client_app_type = ["","Multiple Choice", "Image", "Video", "Map", "Aerial", "3W"];
	$scope.clicker = data;
	
	CrisisService.query().$promise.then(function(crisisList){
		$scope.crisis = crisisList;
	});
	
	$scope.isEditMode = false;
	
	$scope.toggleTranslationRequired = function() {
		$scope.temp_clicker.translationRequired = !$scope.temp_clicker.translationRequired;
		$scope.temp_clicker.tcProjectID = null;
	};
	
	$scope.toggleIsCustom = function() {
		$scope.temp_clicker.isCustom = !$scope.temp_clicker.isCustom;
	};
	
	$scope.switchEditMode = function(mode) {
		if(mode) {
			$scope.temp_clicker = angular.copy($scope.clicker);
			$scope.temp_clicker.status = $scope.temp_clicker.status + "";
			$scope.temp_clicker.translationRequired = $scope.temp_clicker.tcProjectID != null;
		}
		$scope.isEditMode = mode;
	}; 
	
	$scope.updateClicker = function() {
		if($scope.selectedCrisis) {
			console.log($scope.selectedCrisis);
			$scope.temp_clicker.crisisID = $scope.selectedCrisis.crisisID;
			$scope.temp_clicker.crisisName = $scope.selectedCrisis.displayName;
		}
		ClickersService.update($scope.temp_clicker).$promise.then(function(data){
			$scope.clicker = angular.copy($scope.temp_clicker);
			$scope.isEditMode = false;
		});
	};
	
});
