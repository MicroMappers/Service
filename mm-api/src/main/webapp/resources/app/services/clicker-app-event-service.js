app.service('ClickerAppEventService', function($resource) {
	
	return $resource('/MMAPI/client_app_event/:id/:operation/:otherId', {
		id: '@id',
		operation: '@operation',
		otherId: '@otherId'
	}, {
		update: {
			method: 'PUT'
		},
		getClientEvents: {
			method: 'GET',
			params: {
				operation: 'getClientEvents'
			},
			isArray: true
		},
		getOtherClientApp: {
			method: 'GET',
			params: {
				operation: 'getOtherClientApp'
			},
			isArray: true
		},
		generateEvents: {
			method: 'GET',
			params: {
				operation: 'generateEvents'
			}
		}
	});
	
});