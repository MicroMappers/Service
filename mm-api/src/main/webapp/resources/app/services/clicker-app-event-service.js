app.service('ClickerAppEventService', function($resource) {
	
	return $resource('/MMAPI/rest/client_app_event/:id/:operation', {
		id: '@id',
		operation: '@operation'
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
			method: 'POST',
			params: {
				operation: 'generateEvents'
			}
		}
	});
	
});