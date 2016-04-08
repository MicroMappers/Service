var app = angular.module('micromaps', [ 'ui.router', 'ngResource', 'ngAnimate', 'ui.bootstrap', 'colorpicker.module', 'angular-loading-bar']);

app.config(function($stateProvider, $urlRouterProvider,cfpLoadingBarProvider) {
	cfpLoadingBarProvider.includeSpinner = true; // Show the spinner.
    cfpLoadingBarProvider.includeBar = true;
	
	$urlRouterProvider.otherwise("/asd");
	
	$stateProvider
    .state('clicker-list', {
      url: "/clickers",
      templateUrl: 'resources/app/admin-panel/clickers-list/clickers-list.html',
      controller: 'ClickersListCtrl',
      resolve: {
   	   data: function(ClickersService){
              return ClickersService.query().$promise;
          }
      }
    })
    .state('clickertabs', {
      url: "/clicker",
      templateUrl: 'resources/app/admin-panel/clicker-tabs/clicker-tabs.html',
      controller: 'ClickerTabsCtrl'
    })
    .state('clickertabs.detail', {
      url: "/detail/:id",
      templateUrl: 'resources/app/admin-panel/clicker-detail/clicker-detail.html',
      controller: 'ClickerDetailCtrl',
      resolve: {
   	   data: function(ClickersService, $stateParams){
   		   return ClickersService.get({"id" : $stateParams.id}).$promise;
          }
      }
    })
    .state('clickertabs.answers', {
      url: "/answers/:id",
      templateUrl: 'resources/app/admin-panel/clicker-answers/clicker-answers.html',
      controller: 'ClickerAnswersCtrl',
      resolve: {
	   data: function(ClickerAnswersService, $stateParams){
		   return ClickerAnswersService.get({"id" : $stateParams.id}).$promise;
	     }
      }
    })
    .state('clickertabs.styles', {
      url: "/styles/:id",
      templateUrl: 'resources/app/admin-panel/clicker-styles/clicker-styles.html',
      controller: 'ClickerStylesCtrl',
      resolve: {
	   clickerAnswers : function(ClickerAnswersService, $stateParams){
		   return ClickerAnswersService.get({"id" : $stateParams.id}).$promise;
	     },
	   clickerStyles : function(ClickerStylesService, $stateParams){
		   return ClickerStylesService.get({"id" : $stateParams.id}).$promise;
	     }
      }
    })
    .state('clickertabs.events', {
      url: "/events/:id",
      templateUrl: 'resources/app/admin-panel/clicker-events/clicker-events.html',
      controller: 'ClickerEventsCtrl',
      resolve: {
   	   data: function(ClickersService, $stateParams){
   		   return ClickersService.get({"id" : $stateParams.id}).$promise;
          }
      }
    });
	
});