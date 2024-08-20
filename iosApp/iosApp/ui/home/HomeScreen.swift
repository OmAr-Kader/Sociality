//
//  HomeScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct HomeScreen : View {
    
    let userBase: UserBase
    let navigateTo: @MainActor (Screen) -> Unit
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let navigateHome: @MainActor (Screen) -> Unit
    
    @Inject
    private var theme: Theme
    
    @StateObject private var obs: HomeObserve = HomeObserve()
    @State private var toast: Toast? = nil
    @State var isOpen = false
    
    private var listOfFloatingButtons: [FabItem] {
        [FabItem(icon: "chat", title: "Chat", color: theme.primary), FabItem(icon: "plus", title: "Post Creator", color: theme.secondary)]
    }

    var body: some View {
        let state = obs.state
        ZStack {
            DrawerView(isOpen: $isOpen, overlayColor: shadowColor) {
                VStack {
                    BarMainScreen(profilePicture: userBase.profilePicture) {
                        isOpen.toggle()
                    } onSearch: {
                        navigateTo(.SEARCH_SCREEN_ROUTE)
                    } onProfile: {
                        navigateToScreen(ProfileRoute(userId: userBase.id), .PROFILE_SCREEN_ROUTE)
                    }
                    ScrollView {
                        LazyVStack {
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
                }.sheet(isPresented: Binding(get: {
                    state.isComment
                }, set: { it in
                    logger("----", String(it))
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
                    .background(theme.background).overlay {
                        MultipleFloatingButton(listOfFabs: listOfFloatingButtons) { it in
                            navigateTo(it == 0 ? .MESSENGER_SCREEN_ROUTE : .POST_CREATOR_ROUTE)
                        }
                    }
            } drawer: {
                DrawerContainer {
                    DrawerText(
                        itemColor: theme.primary,
                        text: "Curso",
                        textColor: theme.textForPrimaryColor
                    ) {
                        withAnimation {
                            isOpen.toggle()
                        }
                    }
                    DrawerItem(
                        itemColor: theme.backDark,
                        icon: "exit",
                        text: "Sign out",
                        textColor: theme.textColor
                    ) {
                        obs.signOut {
                            exit(0)
                        } failed: {
                            toast = Toast(style: .error, message: "Failed")
                        }
                    }
                }
            }
            LoadingBar(isLoading: state.isProcess)
        }.background(theme.background).toastView(toast: $toast, backColor: theme.backDark).onAppear {
            obs.loadMemes(userId: userBase.id) { memeLords in
                withAnimation {
                    obs.updateMemes(memeLords: memeLords)
                }
            }
        }
    }
}

struct BarMainScreen : View {
    
    let profilePicture: String
    let openDrawer: () -> Unit
    let onSearch: () -> Unit
    let onProfile: () -> Unit
    
    @Inject
    private var theme: Theme
    
    var body: some View {
        HStack {
            HStack {
                HStack {
                    Button(action: openDrawer, label: {
                        ImageAsset(icon: "menu", tint: theme.textColor).frame(width: 35, height: 35).padding(5)
                    }).padding()
                    Spacer().frame(width: 5)
                    Image(
                        uiImage: UIImage(
                            named: "sociality"
                        ) ?? UIImage()
                    ).resizable()
                        .renderingMode(.original)
                        .imageScale(.medium)
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 40, height: 40)
                }
                Spacer()
                HStack {
                    ImageAsset(icon: "search", tint: theme.textColor)
                        .onTapGesture(perform: onSearch)
                        .padding(all: 8)
                        .frame(width: 40, height: 40)
                        .background(Circle().fill(theme.backDark))
                    Spacer().frame(width: 10)
                    ImageCacheView(profilePicture, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                        .frame(width: 40, height: 40)
                        .clipShape(Circle())
                        .onTapGesture(perform: onProfile)
                    Spacer().frame(width: 10)
                }
            }.padding(leading: 5, trailing: 15).frame(height: 60).background(theme.background)
        }.frame(height: 60).clipShape(
            .rect(
                topLeadingRadius: 0,
                bottomLeadingRadius: 15,
                bottomTrailingRadius: 15,
                topTrailingRadius: 0
            )
        ).background(
            theme.background
                .shadow(color: theme.primary, radius: 10, x: 0, y: 0)
                .mask(Rectangle().padding(.bottom, -20))
        )
    }
}
