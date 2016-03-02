app.controller('ClickersListCtrl', function ($scope, $location, ClickersService, data) {
	
	$scope.client_app_status = ["Pending", "AIDR Only", "Micromappers Only", "Inactive", "Disabled", "AIDR Micromappers Both"];
	$scope.client_app_type = ["","Multiple Choice", "Image", "Video", "Map", "Aerial", "3W"];
	$scope.clickers = data;
	
	$scope.goToDetail = function(id) {
		$location.path("/clickers/"+id);
	};
	
});
