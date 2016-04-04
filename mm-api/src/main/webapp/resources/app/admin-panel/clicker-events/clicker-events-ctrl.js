app.controller('ClickerEventsCtrl', function ($scope, $stateParams, $location, ClickersService, ClickerAppEventService, data) {
	
	$scope.client_app_status = ["Pending", "AIDR Only", "Micromappers Only", "Inactive", "Disabled", "AIDR Micromappers Both"];
	$scope.client_app_type = ["","Multiple Choice", "Image", "Video", "Map", "Aerial", "3W"];
	$scope.clicker = data;
	$scope.clientapp.id = $stateParams.id;
	$scope.clientapp.name = data.name;
	$scope.clientapp.currentPath = $location.path();
	

	ClickerAppEventService.getClientEvents({"id": $scope.clientapp.id}).$promise.then(function(events){
		$scope.events = events;
	});
	
	ClickerAppEventService.getOtherClientApp({"id": $scope.clientapp.id}).$promise.then(function(otherClientApps){
		$scope.otherClientApps = otherClientApps;
	});
	
	
	$scope.generateEvents = function() {
		
		if($scope.clicker.otherClientApp) {
			$scope.obj = {};
			if($scope.clicker.appType == 4) {
				$scope.obj = {
						"id": $scope.clicker.otherClientApp,
						"otherId": $scope.clientapp.id
				};
			} else {
				$scope.obj = {
						"otherId": $scope.clicker.otherClientApp,
						"id": $scope.clientapp.id
				};
			}
			ClickerAppEventService.generateEvents($scope.obj).$promise.then(function(data){
				
			});
		}
		
	};
	
});
