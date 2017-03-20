(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('FormationDialogController', FormationDialogController);

    FormationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Formation', 'User'];

    function FormationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Formation, User) {
        var vm = this;

        vm.formation = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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
            if (vm.formation.id !== null) {
                Formation.update(vm.formation, onSaveSuccess, onSaveError);
            } else {
                Formation.save(vm.formation, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvthequeApp:formationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.datedebut = false;
        vm.datePickerOpenStatus.datefin = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
