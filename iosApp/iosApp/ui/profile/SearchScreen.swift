//
//  SearchScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SearchScreen : View {
    
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let backPress: @MainActor () -> Unit
    
    @Inject
    private var theme: Theme
    @FocusState private var isFoucesed: Bool

    @StateObject private var obs: SearchObserve = SearchObserve()
    
    var body: some View {
        let state = obs.state
        ZStack {
            VStack {
                HStack(alignment: .center) {
                    BackPressButton {
                        backPress()
                    }
                    HStack {
                        OutlinedTextField(text: state.searchText, onChange: obs.onSearchQueryChange, hint: "Search Users", isError: false, errorMsg: "Shouldn't be empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.alphabet
                        ).environment(\.layoutDirection, state.searchText.textDirection)
                        Spacer()
                        ImageAsset(icon: "search", tint: theme.textColor)
                            .onTapGesture {
                                obs.doSearch(searchText: state.searchText) { usersOnSearch in
                                    withAnimation {
                                        obs.updateUsersOnSearch(usersOnSearch: usersOnSearch)
                                    }
                                }
                            }
                            .padding(all: 8)
                            .frame(width: 40, height: 40)
                            .background(Circle().fill(theme.backDark))
                    }.padding(leading: 7, trailing: 7)
                }
                Spacer().frame(height: 16)
                if (state.isSearchHistory) {
                    ScrollView {
                        LazyVStack {
                            ForEach(Array(state.searches.enumerated()), id: \.offset) { index, data in
                                let searchHistory = data as SearchData
                                SearchItem(name: searchHistory.searchText, theme: theme) {
                                    obs.onSearchQueryChange(searchText: searchHistory.searchText)
                                    obs.doSearch(searchText: searchHistory.searchText) { usersOnSearch in
                                        withAnimation {
                                            obs.updateUsersOnSearch(usersOnSearch: usersOnSearch)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    ScrollView {
                        LazyVStack {
                            ForEach(Array(state.users.enumerated()), id: \.offset) { index, data in
                                let user = data as User
                                UserItem(user: user, theme: theme) {
                                    navigateToScreen(ProfileRoute(userId: user.userId), .PROFILE_SCREEN_ROUTE)
                                }
                            }
                        }
                    }
                }
            }
        }.background(theme.background)
            .toolbar(.hidden)
            .onAppear {
                obs.loadSearchHistory { searches in
                    withAnimation {
                        obs.updateSearches(searches: searches)
                    }
                }
            }
    }
}

struct SearchItem : View {

    let name: String
    let theme: Theme
    let onClick: () -> Unit

    var body: some View {
        VStack {
            HStack {
                ImageAsset(icon: "search", tint: theme.textColor)
                    .frame(width: 25, height: 25)
                Spacer().frame(width: 8)
                Text(name).foregroundStyle(theme.textColor)
            }.padding(8).onStart()
            Divider().padding(leading: 15, trailing: 15)
        }.padding(8).onStart().onTapGesture(perform: onClick)
    }
}

struct UserItem : View {
    
    let user: User
    let theme: Theme
    let onClick: () -> Unit
    
    var body: some View {
        VStack {
            HStack {
                ImageCacheView(user.profilePicture, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                    .frame(width: 40, height: 40)
                    .clipShape(Circle())
                Spacer().frame(width: 8)
                Text(user.name).foregroundStyle(theme.textColor)
            }.padding(8).onStart()
            Divider().padding(leading: 15, trailing: 15)
        }.padding(8).onStart().onTapGesture(perform: onClick)
    }
}
