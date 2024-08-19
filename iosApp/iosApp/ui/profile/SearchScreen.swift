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
                        OutlinedTextField(text: state.searchText, onChange: obs.onSearchQueryChange, hint: "Search Users", isError: false, errorMsg: "Shouldn't be empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.default, backColor: theme.background
                        ).environment(\.layoutDirection, textDirection(for: state.searchText))// TEMP Solutio
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
                    .frame(width: 35, height: 35)
                Spacer().frame(width: 8)
                Text(name).foregroundStyle(theme.textColor)
            }.padding(8).onStart()
            Divider().padding(leading: 15, trailing: 15)
        }.padding(8).onStart()
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
        }.padding(8).onStart()
    }
}

/*
 
 @Composable
 fun SearchItem(name: String, theme: Theme, onClick: () -> Unit) {
     Row(
         modifier = Modifier
             .padding(8.dp)
             .fillMaxWidth()
             .clickable(onClick = onClick),
         verticalAlignment = Alignment.CenterVertically
     ) {
         Image(
             imageVector = rememberSearch(theme.textColor),
             contentDescription = "Profile Image",
             modifier = Modifier
                 .size(35.dp)
                 .clip(CircleShape),
             contentScale = ContentScale.Crop
         )
         Spacer(modifier = Modifier.width(8.dp))
         Text(text = name, color = theme.textColor)
     }
     HorizontalDivider(Modifier.padding(start = 15.dp, end = 15.dp))
 }
 @Composable
 fun UserItem(user: User, theme: Theme, onClick: () -> Unit) {
     Row(
         modifier = Modifier
             .padding(8.dp)
             .fillMaxWidth()
             .clickable(onClick = onClick),
         verticalAlignment = Alignment.CenterVertically
     ) {
         coil.compose.SubcomposeAsyncImage(
             model = LocalContext.current.imageBuildr(user.profilePicture),
             success = { (painter, _) ->
                 Image(
                     painter = painter,
                     contentDescription = "Profile Image",
                     modifier = Modifier
                         .size(40.dp)
                         .clip(CircleShape),
                     contentScale = ContentScale.Crop
                 )
             },
             error = {
                 Icon(
                     modifier = Modifier
                         .size(40.dp)
                         .clip(CircleShape),
                     imageVector = rememberProfile(color = theme.textColor),
                     contentDescription = "Profile"
                 )
             },
             onError = {
                 loggerError("AsyncImagePainter.Error", it.result.throwable.stackTraceToString())
             },
             contentScale = ContentScale.Crop,
             filterQuality = FilterQuality.None,
             contentDescription = "Image"
         )
         Text(text = user.name, color = theme.textColor)
     }
     HorizontalDivider(Modifier.padding(start = 15.dp, end = 15.dp))
 }
 */
