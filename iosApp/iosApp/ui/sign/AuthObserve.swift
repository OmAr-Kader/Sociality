//
//  AuthObserve.swift
//  iosApp
//
//  Created by OmAr on 14/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class AuthObserve : ObservableObject {

    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    @MainActor func setName(_ name: String) {
        self.state = self.state.copy(name: name)
    }

    @MainActor func setEmail(_ email: String) {
        self.state = self.state.copy(email: email)
    }

    @MainActor func setPassword(_ password: String) {
        self.state = self.state.copy(password: password)
    }

    @MainActor func toggleScreen() {
        self.state = self.state.copy(isLoginScreen: !state.isLoginScreen)
    }
    
    @MainActor func createNewUser(invoke: @MainActor @escaping (UserBase) -> Unit, failed: @MainActor @escaping () -> Unit) {
        let state = state
        setMainProcess(true)
        scope.launchBack {
            do {
                try await AuthKt.registerAuth(
                    user: UserBase().copy(
                    username: state.email.components(separatedBy: "@").first ?? state.email,
                    email: state.email,
                    name: state.name
                ), passwordUser:  state.password, invoke: { _ in
                    self.scope.launchBack {
                        await self.doSignUp(state: state, invoke: invoke, failed: failed)
                    }
                }, failed: {
                    self.scope.launchMain {
                        self.setMainProcess(false)
                        failed()
                    }
                    
                })
            } catch {
                self.scope.launchMain {
                    failed()
                }
            }
        }
    }
    
    
    private func doSignUp(state: State, invoke: @MainActor @escaping (UserBase) -> Void, failed: @MainActor @escaping () -> Void) async {
        if let userBase = try? await AuthKt.userInfo() {
            let user = User().copy(
                userId: userBase.id,
                username: userBase.username,
                email: userBase.email,
                name: state.name
            )
            if let newUser = try? await project.profile.addNewUser(item: user) {
                do {
                    try await project.friendship.addNewFriendship(item: Friendship().copy(userId: newUser.userId, friends: []))
                    try await project.pref.updatePref(pref: PreferenceData(keyString: ConstKt.PREF_NAME, value: state.name), newValue: state.name)
                    try await project.pref.updatePref(pref: PreferenceData(keyString: ConstKt.PREF_PROFILE_IMAGE, value: newUser.profilePicture), newValue: newUser.profilePicture)
                    
                    scope.launchMain {
                        self.setMainProcess(false)
                        invoke(userBase.copy(name: user.name, profilePicture: user.profilePicture))
                    }
                } catch {
                    scope.launchMain {
                        self.setMainProcess(false)
                        failed()
                    }
                }
            } else {
                scope.launchMain {
                    self.setMainProcess(false)
                    failed()
                }
            }
        } else {
            scope.launchMain {
                self.setMainProcess(false)
                failed()
            }
        }
    }
    
    @MainActor
    func loginUser(invoke: @MainActor @escaping (UserBase) -> Void, failed: @MainActor @escaping () -> Void) {
        let state = state
        setMainProcess(true)
        scope.launchBack {
            await self.doLogin(state: state, invoke: invoke, failed: failed)
        }
    }

    
    private func doLogin(state: State, invoke: @MainActor @escaping (UserBase) -> Void, failed: @MainActor @escaping () -> Void) async {
        do {
            try await AuthKt.signInAuth(emailUser: state.email, passwordUser: state.password, invoke: {
                self.scope.launchBack {
                    if let userBase = try? await AuthKt.userInfo() {
                        if let user = try? await self.project.profile.getProfileOnUserId(userId: userBase.id) {
                            do {
                                try await self.project.pref.updatePref(pref: PreferenceData(keyString: ConstKt.PREF_NAME, value: state.name), newValue: state.name)
                                try await self.project.pref.updatePref(pref: PreferenceData(keyString: ConstKt.PREF_PROFILE_IMAGE, value: user.profilePicture), newValue: user.profilePicture)
                                
                                self.scope.launchMain {
                                    self.setMainProcess(false)
                                    invoke(userBase.copy(name: user.name, profilePicture: user.profilePicture))
                                }
                            } catch {
                                self.scope.launchMain {
                                    self.setMainProcess(false)
                                    invoke(userBase.copy(name: user.name, profilePicture: user.profilePicture))
                                }
                            }
                        } else {
                            self.scope.launchMain {
                                self.setMainProcess(false)
                                invoke(userBase)
                            }
                        }
                    } else {
                        self.scope.launchMain {
                            self.setMainProcess(false)
                            failed()
                        }
                    }
                }
            }) {_ in
                self.scope.launchMain {
                    self.setMainProcess(false)
                    failed()
                }
            }
        } catch {
            self.scope.launchMain {
                self.setMainProcess(false)
                failed()
            }
        }
    }
    
    private func setProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor private func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        private(set) var name: String = ""
        private(set) var email: String = ""
        private(set) var password: String = ""
        private(set) var isLoginScreen: Bool = false
        private(set) var isProcess: Bool = false
        private(set) var isErrorPressed: Bool = false
        
        @MainActor
        mutating func copy(
            name: String? = nil,
            email: String? = nil,
            password: String? = nil,
            isLoginScreen: Bool? = nil,
            isProcess: Bool? = nil,
            isErrorPressed: Bool? = nil
        ) -> Self {
            self.name = name ?? self.name
            self.email = email ?? self.email
            self.password = password ?? self.password
            self.isLoginScreen = isLoginScreen ?? self.isLoginScreen
            self.isProcess = isProcess ?? self.isProcess
            self.isErrorPressed =  isErrorPressed ?? self.isErrorPressed
            return self
        }
    }
    
}
