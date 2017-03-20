(function() {
    'use strict';

    angular
        .module('cvthequeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('formation', {
            parent: 'entity',
            url: '/formation',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Formations'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/formation/formations.html',
                    controller: 'FormationController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('formation-detail', {
            parent: 'entity',
            url: '/formation/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Formation'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/formation/formation-detail.html',
                    controller: 'FormationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Formation', function($stateParams, Formation) {
                    return Formation.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'formation',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('formation-detail.edit', {
            parent: 'formation-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/formation/formation-dialog.html',
                    controller: 'FormationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Formation', function(Formation) {
                            return Formation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('formation.new', {
            parent: 'formation',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/formation/formation-dialog.html',
                    controller: 'FormationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nom: null,
                                etablissement: null,
                                description: null,
                                datedebut: null,
                                datefin: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('formation', null, { reload: 'formation' });
                }, function() {
                    $state.go('formation');
                });
            }]
        })
        .state('formation.edit', {
            parent: 'formation',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/formation/formation-dialog.html',
                    controller: 'FormationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Formation', function(Formation) {
                            return Formation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('formation', null, { reload: 'formation' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('formation.delete', {
            parent: 'formation',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/formation/formation-delete-dialog.html',
                    controller: 'FormationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Formation', function(Formation) {
                            return Formation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('formation', null, { reload: 'formation' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
