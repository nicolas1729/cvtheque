(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('ExperienceDeleteController',ExperienceDeleteController);

    ExperienceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Experience'];

    function ExperienceDeleteController($uibModalInstance, entity, Experience) {
        var vm = this;

        vm.experience = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Experience.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
