import Foundation
import SwiftUI
import Combine
import shared

class AppObserve : ObservableObject {

    @Inject
    private var project: Project

    private var scope = Scope()

    @MainActor
    @Published var navigationPath = NavigationPath()
    
    @MainActor
    @Published var state = State()

    private var preferences: [PreferenceData] = []
    private var prefsTask: Task<Void, Error>? = nil
    private var job: Task<Void, Error>? = nil
    
    @MainActor
    var navigateHome: (Screen) -> Unit {
        return { screen in
            withAnimation {
                self.state = self.state.copy(homeScreen: screen)
            }
            return ()
        }
    }
    
    @MainActor
    func navigateHomeNoAnimation(_ screen: Screen) -> Unit {
        self.state = self.state.copy(homeScreen: screen)
    }
    
    @MainActor
    func navigateTo(_ screen: Screen) {
        self.navigationPath.append(screen)
    }
    
    @MainActor
    func backPress() {
        if !self.navigationPath.isEmpty {
            self.navigationPath.removeLast()
        }
    }
    
    func fetchUser(invoke: @MainActor @escaping (UserBase?) -> Unit) {
        scope.launchBack {
            self.findPrefString(ConstKt.PREF_NAME) { name in
                self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                    self.scope.launchBack {
                        guard let userBase = try? await AuthKt.userInfo() else {
                            self.scope.launchMain {
                                invoke(nil)
                            }
                            return
                        }
                        let user = UserBase(id: userBase.id, username: userBase.username, email: userBase.email, name: name ?? "", profilePicture: profileImage ?? "")
                        self.scope.launchMain {
                            self.state = self.state.copy(userBase: user)
                            invoke(user)
                        }
                    }
                }
            }
        }
    }
    
    func findUser(invoke: @MainActor @escaping (UserBase?) -> Unit) {
        scope.launchBack {
            self.findPrefString(ConstKt.PREF_NAME) { name in
                self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                    self.scope.launchBack {
                        guard let user = try? await AuthKt.userInfo()?.copy(name: name ?? "", profilePicture: profileImage ?? "") else {
                            self.scope.launchMain {
                                invoke(nil)
                            }
                            return
                        }
                        self.scope.launchMain {
                            self.state = self.state.copy(userBase: user)
                            invoke(user)
                        }
                    }
                }
            }
        }
    }

    
    func findUserLive(invoke: @MainActor @escaping (UserBase?) -> Unit) {
        scope.launchBack {
            do {
                try await AuthKt.fetchSupaBaseUser { userBase, status in
                    let _ = logger("fetchSupaBaseUser", "fetchSupaBaseUser")
                    guard let userBase = userBase else {
                        let _ = logger("fetchSupaBaseUser", (userBase?.id ?? "nil"))
                        if (status === SessionStatusDataNotAuthenticated()) {
                            self.scope.launchMain {
                                invoke(nil)
                            }
                        }
                        self.scope.launchMain {
                            self.state = self.state.copy(sessionStatus: status)
                        }
                        return
                    }
                    self.findPrefString(ConstKt.PREF_NAME) { name in
                        self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                            let user = UserBase(id: userBase.id, username: userBase.username, email: userBase.email, name: name ?? "", profilePicture: profileImage ?? "")
                            self.scope.launchMain {
                                self.state = self.state.copy(userBase: user)
                                invoke(user)
                            }
                        }
                    }
                }
            } catch {
                logger("findUserLive", error.localizedDescription)
                self.scope.launchMain {
                    invoke(nil)
                }
            }
        }
    }
    
    @BackgroundActor
    private func findPrefString(
        _ key: String,
        value: @BackgroundActor @escaping (String?) -> Unit
    ) {
        if (preferences.isEmpty) {
            self.inti { pref in
                value(pref.first { it1 in it1.keyString == key }?.value)
            }
        } else {
            value(preferences.first { it in it.keyString == key }?.value)
        }
    }
    
    
    private func inti(invoke: @BackgroundActor @escaping ([PreferenceData]) -> Unit) {
        scope.launchBack {
            do {
                try await self.project.pref.prefs { list in
                    invoke(list)
                }
            } catch {
                invoke([])
            }
        }
    }
    
    @MainActor
    func getArgument<T: ScreenConfig>(screen: Screen) -> T? {
        return state.argOf(screen)
    }
    
    @MainActor
    func writeArguments(_ route: Screen,_ screenConfig: ScreenConfig) {
        state = state.copy(route, screenConfig)
    }
    
    @MainActor
    func signOut(_ invoke: @escaping @MainActor () -> Unit,_ failed: @escaping @MainActor () -> Unit) {
        scope.launchBack {
            guard let result = try? await self.project.pref.deletePrefAll() else {
                self.scope.launchMain {
                    failed()
                }
                return
            }
            if result.int32Value == RealmKt.REALM_SUCCESS {
                self.scope.launchMain {
                    invoke()
                }
            } else {
                self.scope.launchMain {
                    failed()
                }
            }
        }
    }

    
    private func cancelSession() {
        prefsTask?.cancel()
        prefsTask = nil
    }

    struct State {
        
        private(set) var homeScreen: Screen = .AUTH_SCREEN_ROUTE
        private(set) var sessionStatus: SessionStatusData = SessionStatusDataLoadingFromStorage()
        private(set) var userBase: UserBase? = nil
        private(set) var args = [Screen : any ScreenConfig]()
        
        @MainActor
        mutating func copy(
            homeScreen: Screen? = nil,
            sessionStatus: SessionStatusData? = nil,
            userBase: UserBase? = nil,
            args: [Screen : any ScreenConfig]? = nil
        ) -> Self {
            self.homeScreen = homeScreen ?? self.homeScreen
            self.sessionStatus = sessionStatus ?? self.sessionStatus
            self.userBase = userBase ?? self.userBase
            self.args = args ?? self.args
            return self
        }
        
        mutating func argOf<T: ScreenConfig>(_ screen: Screen) -> T? {
            return args.first { (key: Screen, value: any ScreenConfig) in
                key == screen
            } as? T
        }
        
        mutating func copy<T : ScreenConfig>(_ screen: Screen, _ screenConfig: T) -> Self {
            args[screen] = screenConfig
            return self
        }
    }

    deinit {
        prefsTask?.cancel()
        prefsTask = nil
        scope.deInit()
    }
    
}
