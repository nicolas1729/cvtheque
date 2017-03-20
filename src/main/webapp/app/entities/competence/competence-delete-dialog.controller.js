(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('CompetenceDeleteController',CompetenceDeleteController);

    CompetenceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Competence'];

    function CompetenceDeleteController($uibModalInstance, entity, Competence) {
        var vm = this;

        vm.competence = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Competence.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
