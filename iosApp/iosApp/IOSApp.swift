import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        #if DEBUG
        IosAppModuleKt.doInitKoin(isDebugMode: true)
        #else
        IosAppModuleKt.doInitKoin(isDebugMode: false)
        #endif
    }

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            Main(app: delegate.app)
        }
    }
}
