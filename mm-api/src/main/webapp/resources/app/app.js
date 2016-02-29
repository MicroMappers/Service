var app = angular.module('micromaps', [ 'ngRoute', 'ngResource', 'ngAnimate', 'ui.bootstrap']);

app.config(function($routeProvider) {
           
	$routeProvider.when('/clickers', {
       templateUrl: 'resources/app/admin-panel/clickers-list.html',
       controller: 'ClickersCtrl',
       resolve: {
    	   data: function(ClickersService){
               return ClickersService.all().$promise;
           }
       }
     })
     .when('/phones/:phoneId', {
       templateUrl: 'partials/phone-detail.html',
       controller: 'PhoneDetailCtrl'
     })
     .otherwise({
           redirectTo: '/clickers'
     });
	
});