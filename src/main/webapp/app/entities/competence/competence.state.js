(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('competence', {
            parent: 'entity',
            url: '/competence',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Competences'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/competence/competences.html',
                    controller: 'CompetenceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('competence-detail', {
            parent: 'entity',
            url: '/competence/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Competence'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/competence/competence-detail.html',
                    controller: 'CompetenceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Competence', function($stateParams, Competence) {
                    return Competence.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'competence',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('competence-detail.edit', {
            parent: 'competence-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/competence/competence-dialog.html',
                    controller: 'CompetenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Competence', function(Competence) {
                            return Competence.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('competence.new', {
            parent: 'competence',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/competence/competence-dialog.html',
                    controller: 'CompetenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nom: null,
                                niveau: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('competence', null, { reload: 'competence' });
                }, function() {
                    $state.go('competence');
                });
            }]
        })
        .state('competence.edit', {
            parent: 'competence',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/competence/competence-dialog.html',
                    controller: 'CompetenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Competence', function(Competence) {
                            return Competence.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('competence', null, { reload: 'competence' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('competence.delete', {
            parent: 'competence',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/competence/competence-delete-dialog.html',
                    controller: 'CompetenceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Competence', function(Competence) {
                            return Competence.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('competence', null, { reload: 'competence' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
