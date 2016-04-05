app.service('ClickerStylesService', function($resource) {
	
	return $resource('/MMAPI/rest/marker_style/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		update: {
			method: 'PUT'
		}
	});
	
});