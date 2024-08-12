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
        prefsTask = scope.launchRealm {
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
    
    func fetchUser() {
        /*job = scope.launchRealm {
            try? await AuthKt.fetchSupaBaseUser(self.project.supaBase) { user, status in
                if user != nil {
                    self.cancelSession()
                }
                self.scope.launchMain {
                    self.state = self.state.copy(user: user, sessionStatus: status)
                }
            }
        }*/
    }
    
    private func inti(invoke: @BackgroundActor @escaping ([PreferenceData]) -> Unit) {
        scope.launchRealm {
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
        scope.launchRealm {
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
        var user: User? = nil

        var args = [Screen : any ScreenConfig]()
        

        mutating func copy(homeScreen: Screen) -> Self {
            self.homeScreen = homeScreen
            return self
        }
        
        mutating func copy(user: User?, sessionStatus: any SessionStatusData) -> Self {
            self.user = user
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
