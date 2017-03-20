(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .factory('ExperienceSearch', ExperienceSearch);

    ExperienceSearch.$inject = ['$resource'];

    function ExperienceSearch($resource) {
        var resourceUrl =  'api/_search/experiences/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
