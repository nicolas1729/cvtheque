(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('ExperienceDetailController', ExperienceDetailController);

    ExperienceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Experience', 'User'];

    function ExperienceDetailController($scope, $rootScope, $stateParams, previousState, entity, Experience, User) {
        var vm = this;

        vm.experience = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvthequeApp:experienceUpdate', function(event, result) {
            vm.experience = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
