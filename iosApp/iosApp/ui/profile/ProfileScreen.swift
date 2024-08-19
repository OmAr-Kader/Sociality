//
//  ProfileScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ProfileScreen : View {

    let userBase: UserBase
    let screenConfig: (Screen) -> (any ScreenConfig)?
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let backPress: @MainActor () -> Unit

    @Inject
    private var theme: Theme
    
    @StateObject private var obs: ProfileObserve = ProfileObserve()

    var body: some View {
        let state = obs.state
        ZStack {
            VStack {
                ScrollView {
                    LazyVStack {
                        ProfileHeader(user: state.user) {
                            obs.onAddFriendClicked(fromMe: userBase.id, to: state.user.userId)
                        } onAcceptFriendClicked: {
                            obs.onAcceptFriendClicked(userBase: userBase, userId: state.user.userId)
                        } onCancelFriendClicked: {
                            obs.onCancelFriendClicked(userBase: userBase, userId: state.user.userId)
                        } onMessageClicked: {
                            navigateToScreen(ChatRoute(chatId: 0, chat: Chat().copy(members: [userBase.id, state.user.userId])), .CHAT_SCREEN_ROUTE)
                        }
                        ForEach(Array(state.memes.enumerated()), id: \.offset) { index, data in
                            let meme = data as MemeLord
                            PostItem(meme: meme, theme: theme) {
                                navigateToScreen(ProfileRoute(userId: meme.user.userId), .PROFILE_SCREEN_ROUTE)
                            } navigateToImage: { it in
                                navigateToScreen(PostRoute(postMedia: meme.post.postMedia, pos: it), .POST_SCREEN_ROUTE)
                            } onLikeClicked: {
                                obs.onLikeClicked(userId: userBase.id, postId: meme.post.id, isLiked: meme.isLiked)
                            } onCommentClicked: {
                                obs.onCommentClicked(memeLord: meme)
                            } onShareClicked: {
                                obs.onShareClicked()
                            }
                        }
                    }
                }
            }.padding().sheet(isPresented: Binding(get: {
                state.isComment
            }, set: { it in
                obs.hide()
            })) {
                CommentBottomSheet(memeLord: state.commentMeme, commentText: state.commentText, onValueComment: obs.onValueComment, onComment: { it in
                    obs.onComment(userBase: userBase, postId: it.post.id) {
                        
                    }
                })
            }.presentationDetents([.height(50), .medium, .custom(CommentSheetDetent.self)])
                .presentationBackground(theme.backDark)
                .presentationContentInteraction(.scrolls)
                .interactiveDismissDisabled()
            BackButton(action: backPress).onTop().onStart()
            LoadingBar(isLoading: state.isProcess)
    }.background(theme.background)
            .toolbar(.hidden)
            .onAppear {
                guard let profileRoute = screenConfig(.PROFILE_SCREEN_ROUTE) as? ProfileRoute else {
                    return
                }
                obs.loadData(userBase: userBase, userId: profileRoute.userId) { user, friendShip, requests in
                    withAnimation {
                        obs.updateUserFrindShipAndRequests(user: user, friendShip: friendShip, requests: requests)
                    }
                } invokeMems: { memes in
                    withAnimation {
                        obs.updateMemes(memeLords: memes)
                    }
                }
            }
    }
}

struct ProfileHeader : View {

    let user: User
    let onAddFriendClicked: @MainActor () -> Unit
    let onAcceptFriendClicked: @MainActor () -> Unit
    let onCancelFriendClicked: @MainActor () -> Unit
    let onMessageClicked: @MainActor () -> Unit
    
    @Inject
    private var theme: Theme
    
    var body: some View {
        if (user.id == 0) {
            VStack {}
        } else {
            VStack {
                ImageCacheView(user.profilePicture, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                    .frame(width: 100, height: 100)
                    .clipShape(Circle())
                Spacer().frame(height: 16)
                Text(user.name).foregroundStyle(theme.textColor).font(.largeTitle)
                Spacer().frame(height: 5)
                Text(user.bio).padding(leading: 15, trailing: 15).foregroundStyle(theme.textColor).font(.caption)
                Spacer().frame(height: 16)
                HStack {
                    switch user.mode {
                    case 0 : Button(action: onAddFriendClicked) {
                        Text("Add Friend").foregroundStyle(Color.white)
                    }.padding().background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                    case -2 : Button(action: onAcceptFriendClicked) {
                        Text("Accept Friend").foregroundStyle(Color.white)
                    }.padding().background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                    case -1 : Button(action: onCancelFriendClicked) {
                        Text("Cancel Request").foregroundStyle(Color.white)
                    }.padding().background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                    case 2 : Button(action: {}) {
                        Text("Edit Profile").foregroundStyle(Color.white)
                    }.padding().background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                    default:
                        Spacer(minLength: 0)
                    }
                    if (user.mode != 2) {
                        Spacer().frame(width: 10)
                        Button(action: onMessageClicked) {
                            Text("Message").foregroundStyle(Color.black)
                        }.padding().background(Color.green).cornerRadius(15)
                        Spacer(minLength: 0)
                    }
                }
            }.padding()
        }
    }
}
