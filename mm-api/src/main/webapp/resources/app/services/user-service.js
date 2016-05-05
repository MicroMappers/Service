app.service('UserService', function($resource) {
	return $resource('/MMAPI/rest/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		current_user: {
			method: 'GET',
			params: {
				operation: 'current_user'
			},
			isArray: false
		}
	});
	
});