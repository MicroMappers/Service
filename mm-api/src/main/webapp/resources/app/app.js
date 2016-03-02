var app = angular.module('micromaps', [ 'ngRoute', 'ngResource', 'ngAnimate', 'ui.bootstrap']);

app.config(function($routeProvider) {
           
	$routeProvider.when('/clickers', {
       templateUrl: 'resources/app/admin-panel/clickers-list/clickers-list.html',
       controller: 'ClickersListCtrl',
       resolve: {
    	   data: function(ClickersService){
               return ClickersService.query().$promise;
           }
       }
     })
     .when('/clickers/:id', {
       templateUrl: 'resources/app/admin-panel/clicker-detail/clicker-detail.html',
       controller: 'ClickerDetailCtrl',
       resolve: {
    	   data: function(ClickersService, $route){
    		   return ClickersService.get({"id" : $route.current.params.id}).$promise;
           }
       }
     })
     .otherwise({
           redirectTo: '/clickers'
     });
	
});