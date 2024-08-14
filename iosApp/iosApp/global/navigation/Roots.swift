import SwiftUI

extension View {
    
    @ViewBuilder func targetScreen(
        _ target: Screen,
        _ app: AppObserve
    ) -> some View {
        switch target {
        case .SPLASH_SCREEN_ROUTE :
            SplashScreen(app: app)
        case .AUTH_SCREEN_ROUTE:
            SplashScreen(app: app)
        case .HOME_SCREEN_ROUTE:
            SplashScreen(app: app)
        case .PROFILE_SCREEN_ROUTE:
            SplashScreen(app: app)
        case .POST_SCREEN_ROUTE:
            SplashScreen(app: app)
        case .POST_CREATOR_ROUTE:
            SplashScreen(app: app)
        case .SEARCH_SCREEN_ROUTE:
            SplashScreen(app: app)
        case .MESSENGER_SCREEN_ROUTE:
            SplashScreen(app: app)
        case .CHAT_SCREEN_ROUTE:
            SplashScreen(app: app)
        }
    }
}

enum Screen : Hashable {
    
    case SPLASH_SCREEN_ROUTE
    
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

class SplashConfig: ScreenConfig {
    
}
