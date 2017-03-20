(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('FormationDetailController', FormationDetailController);

    FormationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Formation', 'User'];

    function FormationDetailController($scope, $rootScope, $stateParams, previousState, entity, Formation, User) {
        var vm = this;

        vm.formation = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvthequeApp:formationUpdate', function(event, result) {
            vm.formation = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
