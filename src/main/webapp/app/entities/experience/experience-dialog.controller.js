(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .controller('ExperienceDialogController', ExperienceDialogController);

    ExperienceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Experience', 'User'];

    function ExperienceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Experience, User) {
        var vm = this;

        vm.experience = entity;
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
            if (vm.experience.id !== null) {
                Experience.update(vm.experience, onSaveSuccess, onSaveError);
            } else {
                Experience.save(vm.experience, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvthequeApp:experienceUpdate', result);
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
