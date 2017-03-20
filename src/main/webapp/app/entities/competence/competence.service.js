(function() {
    'use strict';
    angular
        .module('cvthequeApp')
        .factory('Competence', Competence);

    Competence.$inject = ['$resource'];

    function Competence ($resource) {
        var resourceUrl =  'api/competences/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
