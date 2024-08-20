//
//  PostCreatorScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct PostCreatorScreen : View {
    
    let userBase: UserBase
    let backPress: @MainActor () -> Unit

    @Inject
    private var theme: Theme
    
    @StateObject private var obs: PostCreatorObserve = PostCreatorObserve()
    @State private var toast: Toast? = nil

    var body: some View {
        let state = obs.state
        ZStack {
            VStack {
                Spacer().frame(height: 60)
                BasicsViewPostCreator(post: state.postCreate, isFontDialogVisible: state.isFontDialogVisible, makeFontDialogVisible: obs.makeFontDialogVisible, addAbout: obs.addAbout, changeAbout: obs.changeAbout, removeAboutIndex: obs.removeAboutIndex)
                Spacer().frame(height: 16)
                HStack {
                    Button(action: {
                        obs.onMediaSelected(mediaType: 1, mediaURL: "https://example.com/media.jpg")
                    }, label: {
                        Text("Add Image")
                            .foregroundColor(theme.textForPrimaryColor)
                    }).padding()
                        .background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                    Button(action: {
                        obs.onMediaSelected(mediaType: 2, mediaURL: "https://example.com/media.jpg")
                    }, label: {
                        Text("Add Video")
                            .foregroundColor(theme.textForPrimaryColor)
                    }).padding()
                        .background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                }.padding()
                VStack {
                    Button(action: {
                        obs.createPost(userId: userBase.id, invoke: { _ in
                            backPress()
                        }) {
                            toast = Toast(style: .error, message: "Failed")
                        }
                    }, label: {
                        HStack {
                            Spacer()
                            Text("Post")
                                .foregroundColor(theme.textForPrimaryColor)
                            Spacer()
                        }
                    }).padding()
                        .background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                }.padding()
            }
            BackButton(action: backPress).onTop().onStart()
            LoadingScreen(isLoading: state.isProcess)
        }.background(theme.background)
            .toolbar(.hidden)
            .toastView(toast: $toast, backColor: theme.backDark)
    }
}

struct BasicsViewPostCreator : View {
    
    let post: Post
    let isFontDialogVisible: Bool
    let makeFontDialogVisible: @MainActor () -> Unit
    let addAbout: @MainActor (Int32) -> Unit
    let changeAbout: @MainActor (String, Int) -> Unit
    let removeAboutIndex: @MainActor (Int) -> Unit
        
    @Inject
    private var theme: Theme
    @State var scrollTo: Int = 0

    var body: some View {
        ScrollView {
            LazyVStack {
                ForEach(Array(post.content.enumerated()), id: \.offset) { index, data in
                    let content = data as PostContent
                    let isHeadline = content.font == ConstKt.HEADLINE_FONT
                    HStack(alignment: .center) {
                        OutlinedTextField(text: content.text, onChange: { it in
                            changeAbout(it, index)
                        }, hint: isHeadline ? "Enter Headline" : "Enter Details", isError: false, errorMsg: "Shouldn't be Empty", theme: theme, cornerRadius: 12, lineLimit: nil, keyboardType: .default)
                        Button(action: {
                            if (index == post.content.count - 1) {
                                makeFontDialogVisible()
                                scrollTo = post.content.count + 4
                            } else {
                                removeAboutIndex(index)
                            }
                        }, label: {
                            VStack {
                                VStack {
                                    ImageAsset(
                                        icon: index == post.content.count - 1 ? "plus" : "delete",
                                        tint: theme.textColor
                                    )
                                }.padding(7).background(
                                    theme.background.margeWithPrimary(0.3)
                                )
                            }.clipShape(Circle())
                        }).frame(width: 40, height: 40)
                    }.padding(top: 5, leading: 20, bottom: 5, trailing: 20)
                }
            }.padding(all: 16)
            if (isFontDialogVisible) {
                PostContentCreator { it in
                    addAbout(it)
                }
            }
        }
    }
}

struct PostContentCreator : View {
    
    let addContentFont: (Int32) -> Unit
    
    @Inject
    private var theme: Theme

    var body: some View {
        VStack(alignment: .center) {
            HStack(alignment: .center) {
                Button(action: {
                    addContentFont(ConstKt.DETAILS_FONT)
                }, label: {
                    Text("Small Font").foregroundStyle(theme.textColor)
                }).padding(5)
                Divider().frame(width: 1, height: 30).foregroundStyle(Color.gray)
                Button(action: {
                    addContentFont(ConstKt.HEADLINE_FONT)
                }, label: {
                    Text("Big Font").foregroundStyle(theme.textColor)
                }).padding(5)
            }.padding(5).frame(maxHeight: 300).background(theme.backDarkSec)
        }.clipShape(RoundedRectangle(cornerRadius: 20)).shadow(radius: 2)
    }
}
