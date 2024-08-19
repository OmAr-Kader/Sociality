import SwiftUI
import shared

extension View {
    
    @MainActor @ViewBuilder func targetScreen(
        _ target: Screen,
        _ app: AppObserve,
        navigateTo: @MainActor @escaping (Screen) -> Unit,
        navigateToScreen: @MainActor @escaping (ScreenConfig, Screen) -> Unit,
        navigateHome: @MainActor @escaping (Screen) -> Unit,
        backPress: @MainActor @escaping () -> Unit,
        screenConfig: @MainActor @escaping (Screen) -> (any ScreenConfig)?
    ) -> some View {
        switch target {
        /*case .SPLASH_SCREEN_ROUTE :
            SplashScreen(app: app)*/
        case .AUTH_SCREEN_ROUTE:
            AuthScreen(app: app)
        case .HOME_SCREEN_ROUTE:
            HomeScreen(userBase: app.state.userBase ?? UserBase(), navigateTo: navigateTo, navigateToScreen: navigateToScreen, navigateHome: navigateHome)
        case .PROFILE_SCREEN_ROUTE:
            ProfileScreen(userBase: app.state.userBase ?? UserBase(), screenConfig: screenConfig, navigateToScreen: navigateToScreen, backPress: backPress)
        case .POST_SCREEN_ROUTE:
            PostScreen(screenConfig: screenConfig, backPress: backPress)
        case .POST_CREATOR_ROUTE:
            PostCreatorScreen(userBase: app.state.userBase ?? UserBase(), backPress: backPress)
        case .SEARCH_SCREEN_ROUTE:
            SearchScreen(navigateToScreen: navigateToScreen, backPress: backPress)
        case .MESSENGER_SCREEN_ROUTE:
            MessengerScreen(userBase: app.state.userBase ?? UserBase(), navigateToScreen: navigateToScreen, backPress: backPress)
        case .CHAT_SCREEN_ROUTE:
            ChatScreen(userBase: app.state.userBase ?? UserBase(), screenConfig: screenConfig, backPress: backPress)
        }
    }
}

enum Screen : Hashable {
    
    //case SPLASH_SCREEN_ROUTE
    
    case AUTH_SCREEN_ROUTE
    case HOME_SCREEN_ROUTE
    case PROFILE_SCREEN_ROUTE
    case POST_SCREEN_ROUTE
    case POST_CREATOR_ROUTE
    case SEARCH_SCREEN_ROUTE
    case MESSENGER_SCREEN_ROUTE
    case CHAT_SCREEN_ROUTE
}


protocol ScreenConfig {}

struct ProfileRoute : ScreenConfig {
    let userId: String
}

struct PostRoute: ScreenConfig {
    let postMedia: [PostMedia]
    let pos: Int
}

struct ChatRoute : ScreenConfig {
    let chatId: Long
    let chat: Chat?
}
