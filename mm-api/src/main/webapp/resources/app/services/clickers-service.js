app.service('ClickersService', function($resource) {
	
	return $resource('/MMAPI/clientapp/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		update: {
			method: 'PUT'
		}
	});
	
});