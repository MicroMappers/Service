app.service('ClickerAnswersService', function($resource) {
	
	return $resource('/MMAPI/rest/clientapp_answer/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		update: {
			method: 'PUT'
		}
	});
	
});