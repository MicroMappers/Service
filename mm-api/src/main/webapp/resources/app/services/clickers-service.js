app.service('ClickersService', function($resource) {
	
	return $resource('/MMAPI/rest/clientapp/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		update: {
			method: 'PUT'
		}
	});
	
});