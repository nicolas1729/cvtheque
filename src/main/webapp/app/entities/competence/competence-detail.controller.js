(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('CompetenceDetailController', CompetenceDetailController);

    CompetenceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Competence', 'User'];

    function CompetenceDetailController($scope, $rootScope, $stateParams, previousState, entity, Competence, User) {
        var vm = this;

        vm.competence = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvthequeApp:competenceUpdate', function(event, result) {
            vm.competence = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
