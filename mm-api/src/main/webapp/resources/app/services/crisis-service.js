app.service('CrisisService', function($resource) {
	
	return $resource('/MMAPI/rest/crisis/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		all: {
			method: 'GET',
			params: {
				operation: 'all'
			},
			isArray: true
		}
	});
	
});