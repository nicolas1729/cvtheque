(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('CompetenceDialogController', CompetenceDialogController);

    CompetenceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Competence', 'User'];

    function CompetenceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Competence, User) {
        var vm = this;

        vm.competence = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.competence.id !== null) {
                Competence.update(vm.competence, onSaveSuccess, onSaveError);
            } else {
                Competence.save(vm.competence, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvthequeApp:competenceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
