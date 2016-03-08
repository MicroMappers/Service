app.service('ClickersService', function($resource) {
	
	return $resource('/MMAPI/clientapp/:id/:operation', {
		id: '@id',
		operation: '@operation'
	}, {
		all: {
			method: 'GET',
			params: {
				operation: 'all'
			},
			isArray: true
		},
		searchByRecruiter: {
			method: 'GET',
			url: 'companies/search_by_recruiter/:id',
			params: {
				id: '@id'
			},
			isArray: false
		},
		update: {
			method: 'PUT'
		},
		getAllRecruiters: {
			method: 'GET',
			params: {
				operation: 'get_recruiters_by_company'
			}
		},
		search: {
			method: "GET",
			params: {
				operation: 'search'
			}
		},
		autocomplete: {
		  method: "GET",
		  params: {
		    operation: 'autocomplete'
		  },
		  isArray: true,
		  ignoreLoadingBar: true
		},
			getProfileToClaim: {
		  method: "GET",
		  params: {
		    operation: "get_profile_to_claim"
		  },
		},
		change_status: {
		  method: "POST",
		  params: {
		    operation: "change_status"
		  }
		}
	});
	
});