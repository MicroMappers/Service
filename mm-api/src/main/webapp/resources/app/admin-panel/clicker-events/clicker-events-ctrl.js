app.controller('ClickerEventsCtrl', function ($scope, $stateParams, $location, $state, ClickersService, ClickerAppEventService, data) {
	
	$scope.client_app_status = ["Pending", "AIDR Only", "Micromappers Only", "Inactive", "Disabled", "AIDR Micromappers Both"];
	$scope.client_app_type = ["","Multiple Choice", "Image", "Video", "Map", "Aerial", "3W"];
	$scope.clicker = data;
	$scope.clientapp.id = parseInt($stateParams.id);
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
						clientId: $scope.clicker.otherClientApp.clientAppID,
						geoClientAppId: $scope.clientapp.id
				};
			} else {
				$scope.obj = {
						geoClientAppId: $scope.clicker.otherClientApp.clientAppID,
						clientId: $scope.clientapp.id
				};
			}
			ClickerAppEventService.generateEvents($scope.obj).$promise.then(function(){
				$state.go($state.current, {}, {reload: true});
				toaster.pop('success', "Events generated successfully");
			}, function(error){
			    // error
				toaster.pop('error', "Error in generating events");
			  });
		}
		
	};
	
});
