import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel

	var body: some View {
		Text(viewModel.greetings)
		    .task { await self.viewModel.startObserving() }
	}
}


extension ContentView {
    @MainActor
    class ViewModel: ObservableObject {
        @Published var greetings: String = ""
        let userService = DIHelper().userService

        func startObserving() async {
            try? await Greeting().greet { phrase in
                self.greetings = phrase + self.userService
            }
        }
    }
}
