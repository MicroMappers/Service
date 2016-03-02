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
	};
	
	$scope.toggleIsCustom = function() {
		$scope.temp_clicker.isCustom = !$scope.temp_clicker.isCustom;
	};
	
	$scope.switchEditMode = function(mode) {
		if(mode) {
			$scope.temp_clicker = angular.copy($scope.clicker);
		}
		$scope.isEditMode = mode;
	};
	
});
