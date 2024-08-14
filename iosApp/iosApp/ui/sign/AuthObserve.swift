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
        state.name = name
    }

    @MainActor func setEmail(_ email: String) {
        state.email = email
    }

    @MainActor func setPassword(_ password: String) {
        state.password = password
    }

    @MainActor func toggleScreen() {
        state.isLoginScreen.toggle()
    }
    
    @MainActor func createNewUser(invoke: @escaping () -> Unit, failed: @escaping () -> Unit) {
        let state = state
        setIsProcess(true)
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
                        self.setIsProcess(false)
                        failed()
                    }
                    
                })
            } catch {
                failed()
            }
        }
        
    }
    
    
    private func doSignUp(state: State, invoke: @escaping () -> Void, failed: @escaping () -> Void) async {
        if let userBase = try? await AuthKt.userInfo() {
            let user = User().copy(
                userId: userBase.id,
                username: userBase.username,
                email: userBase.email,
                name: state.name
            )
            /*if let newUser = try? await project.profile.addNewUser(item: user) {
                Friendship(userId: newUser.userId, friends: [])
                await project.friendship.addNewFriendship()
                await project.pref.updatePref(PreferenceData(PREF_NAME, state.name), state.name)
                await project.pref.updatePref(PreferenceData(PREF_PROFILE_IMAGE, newUser.profilePicture), newUser.profilePicture)

                scope.launchMain {
                    self.setIsProcess(false)
                    invoke()
                }
            } else {
                scope.launchMain {
                    self.setIsProcess(false)
                    failed()
                }
            }*/
        } else {
            scope.launchMain {
                self.setIsProcess(false)
                failed()
            }
        }
    }
    
    private func setProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state.isProcess = isProcess
        }
    }
    
    @MainActor private func setIsProcess(_ isProcess: Bool) {
        state.isProcess = isProcess
    }
    /**
     fun createNewUser(invoke: () -> Unit, failed: () -> Unit) {
             uiState.value.let { state ->
                 setIsProcess(true)
                 launchBack {
                     registerAuth(
                         UserBase(
                             username = state.email.split(regex = Regex("@")).firstOrNull() ?: state.email,
                             email = state.email,
                             name = state.name
                         ), state.password, invoke = {
                             doSignUp(state, invoke, failed)
                         },
                     ) {
                         setIsProcess(false)
                         failed()
                     }
                 }
             }
         }
*/

    
    
    struct State {
        var name: String = ""
        var email: String = ""
        var password: String = ""
        var isLoginScreen: Bool = false
        var isProcess: Bool = false
        var isErrorPressed: Bool = false
    }
    
}
