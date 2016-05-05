var app = angular.module('mainController', []);

app.controller('MainController', function ($scope, UserService) {
	UserService.current_user().$promise.then(function(result){
		$scope.profile = result.data;
	}, function(error) {
		//error code
	});
	
	
});