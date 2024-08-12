import Foundation
import SwiftUI
import Swinject
import shared

//https://github.com/Swinject/Swinject

func buildContainer() -> Container {
    let container = Container()
    let theme = Theme(isDarkMode: UITraitCollection.current.userInterfaceStyle.isDarkMode)
    
    container.register(Project.self) { _  in
        return DIHelper().project
    }.inObjectScope(.container)
    container.register(Theme.self) { _  in
        return theme
    }.inObjectScope(.container)
    return container
}


class Resolver {
    static let shared = Resolver()
    
    //get the IOC container
    private var container = buildContainer()
    
    func resolve<T>(_ type: T.Type) -> T {
        container.resolve(T.self)!
    }
}

@propertyWrapper
struct Inject<I> {
    let wrappedValue: I
    init() {
        self.wrappedValue = Resolver.shared.resolve(I.self)
    }
}


