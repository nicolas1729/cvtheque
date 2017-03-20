(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('FormationDeleteController',FormationDeleteController);

    FormationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Formation'];

    function FormationDeleteController($uibModalInstance, entity, Formation) {
        var vm = this;

        vm.formation = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Formation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
