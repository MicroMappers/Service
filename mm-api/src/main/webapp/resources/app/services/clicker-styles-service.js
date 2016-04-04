app.service('ClickerStylesService', function($resource) {
	
	return $resource('/MMAPI/marker_style/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		update: {
			method: 'PUT'
		}
	});
	
});