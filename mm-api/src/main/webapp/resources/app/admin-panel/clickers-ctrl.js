app.controller('ClickersCtrl',  ['$scope', 'ClickersService', 'data', function ($scope, ClickersService, data) {
	
	$scope.client_app_status = ["Pending", "AIDR Only", "Micromappers Only", "Inactive", "Disabled", "AIDR Micromappers Both"];
	$scope.client_app_type = ["","Multiple Choice", "Image", "Video", "Map", "Aerial", "3W"];
	$scope.clickers = data;
	/*ClickersService.all().$promise.then(function(data){
		$scope.clickers = data;
	});*/
	
}]);
