import Foundation
import SwiftUI
import Combine
import shared

class AppObserve : ObservableObject {

    @Inject
    private var project: Project

    private var scope = Scope()

    @Published var navigationPath = NavigationPath()
    
    @Published var state = State()

    private var preferences: [PreferenceData] = []
    private var prefsTask: Task<Void, Error>? = nil
    private var job: Task<Void, Error>? = nil

    init() {
        prefsTask?.cancel()
        prefsTask = scope.launchBack {
            try? await self.project.pref.prefs { list in
                self.scope.launchMain {
                    self.preferences = list
                }
            }
        }
    }
    
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
    func navigateTo(_ screen: Screen) {
        self.navigationPath.append(screen)
    }
    
    @MainActor
    func backPress() {
        if !self.navigationPath.isEmpty {
            self.navigationPath.removeLast()
        }
    }
    
    func fetchUser(invoke: @escaping (UserBase?) -> Unit) {
        scope.launchBack {
            self.findPrefString(ConstKt.PREF_NAME) { name in
                self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                    self.scope.launchBack {
                        guard let userBase = try? await AuthKt.userInfo() else {
                            invoke(nil)
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
    
    func findUserLive(invoke: @escaping (UserBase?) -> Unit) {
        scope.launchBack {
            do {
                try await AuthKt.fetchSupaBaseUser { userBase, status in
                    guard let userBase = userBase else {
                        if (status === SessionStatusDataNotAuthenticated()) {
                            invoke(nil)
                        }
                        self.state = self.state.copy(sessionStatus: status)
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
                invoke(nil)
            }
        }
    }
    
    private func findPrefString(
        _ key: String,
        value: @escaping (String?) -> Unit
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
            try? await self.project.pref.prefs { list in
                invoke(list)
            }
        }
    }

    func getArgument<T: ScreenConfig>(screen: Screen) -> T? {
        return state.argOf(screen)
    }

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
            if result == RealmKt.REALM_SUCCESS.toNumber {
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
    
    struct State {
        var homeScreen: Screen = .SPLASH_SCREEN_ROUTE
        var sessionStatus: SessionStatusData = SessionStatusDataLoadingFromStorage()
        var userBase: UserBase? = nil

        var args = [Screen : any ScreenConfig]()
        

        mutating func copy(homeScreen: Screen) -> Self {
            self.homeScreen = homeScreen
            return self
        }
        
        mutating func copy(userBase: UserBase? = nil, sessionStatus: any SessionStatusData = SessionStatusDataLoadingFromStorage()) -> Self {
            self.userBase = userBase ?? self.userBase
            self.sessionStatus = sessionStatus
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

    private func cancelSession() {
        prefsTask?.cancel()
        prefsTask = nil
    }

    /*func hiIamJustBuilt() {
        import AVFoundation
        var player: AVAudioPlayer?
        guard let path = Bundle.main.path(forResource: "beep", ofType:"mp3") else {
            return
        }
        let url = URL(fileURLWithPath: path)
        do {
            player = try AVAudioPlayer(contentsOf: url)
            player?.play()
        } catch let error {
            print(error.localizedDescription)
        }
    }*/
    
    deinit {
        prefsTask?.cancel()
        prefsTask = nil
        scope.deInit()
    }
    
}
