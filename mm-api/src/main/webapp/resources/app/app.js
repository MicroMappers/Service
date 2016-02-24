var app = angular.module('micromaps', [ 'ngRoute', 'ngResource', 'ngAnimate', 'ui.bootstrap']);

app.config(['$routeProvider', function($routeProvider) {
           
	$routeProvider.when('/clickers', {
       templateUrl: 'resources/app/admin-panel/clickers-list.html',
       controller: 'ClickersCtrl'
     })
     .when('/phones/:phoneId', {
       templateUrl: 'partials/phone-detail.html',
       controller: 'PhoneDetailCtrl'
     })
     .otherwise({
           redirectTo: '/phones'
     });
	
}]);