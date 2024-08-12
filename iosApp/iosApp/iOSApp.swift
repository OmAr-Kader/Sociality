import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        AppModuleKt.doInitKoin()
    }

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            Main(app: delegate.app)
        }
    }
}
