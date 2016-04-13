app.controller('ClickerTabsCtrl', function ($scope, $location, ClickersService, CrisisService) {
	
	$scope.clientapp = { "name": "Loading..."};
	$scope.currentPath = $location.path();
	
	$scope.checkPath = function(match){
		return $scope.clientapp.currentPath.indexOf(match) > 0;
	};
	
	$scope.gotoURL = function(url) {
		$location.path(url + $scope.clientapp.id);
	};
	
	$scope.gotoList = function(url) {
		$location.path(url);
	};
	
});
