//
//  SearchObserve.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class SearchObserve : ObservableObject {
    
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    
    @MainActor
    func loadSearchHistory(invoke: @MainActor @escaping ([SearchData]) -> Unit) {
        setMainProcess(true)
        scope.launchBack {
            try? await self.project.search.getSearchesHistory { searches in
                self.scope.launchMain {
                    invoke(searches)
                }
                
            }
        }
    }
    
    @MainActor
    func updateSearches(searches: [SearchData]) {
        self.state = self.state.copy(searches: searches, isSearchHistory: true, isProcess: false)
    }
    
    @MainActor
    func onSearchQueryChange(searchText: String) {
        self.state = self.state.copy(searchText: searchText)
    }
    
    @MainActor
    func doSearch(searchText: String, invoke: @MainActor @escaping ([User]) -> Unit) {
        self.setMainProcess(true)
        self.scope.launchBack {
            if let usersOnSearch = try? await self.project.profile.fetchProfilesOnName(name: searchText) {
                let search = SearchData(searchText: searchText, date: DateKt.dateNowMills)
                let _ = try? await self.project.search.updateSearch(search: search, newDate: search.date)
                self.scope.launchMain {
                    invoke(usersOnSearch)
                }
            }
        }
    }
    
    @MainActor
    func updateUsersOnSearch(usersOnSearch: [User]) {
        self.state = self.state.copy(users: usersOnSearch, isSearchHistory: false, isProcess: false)
    }
    
    private func setProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor private func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        private(set) var searchText: String = ""
        private(set) var users: [User] = []
        private(set) var searches: [SearchData] = []
        private(set) var isSearchHistory: Bool = true
        private(set) var isProcess: Bool = false
        
        @MainActor
        mutating func copy(
            searchText: String? = nil,
            users: [User]? = nil,
            searches: [SearchData]? = nil,
            isSearchHistory: Bool? = nil,
            isProcess: Bool? = nil
        ) -> Self {
            self.searchText = searchText ?? self.searchText
            self.users = users ?? self.users
            self.searches = searches ?? self.searches
            self.isSearchHistory = isSearchHistory ?? self.isSearchHistory
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }
}
