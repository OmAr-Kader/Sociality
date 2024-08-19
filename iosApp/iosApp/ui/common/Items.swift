//
//  Items.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Combine
import UIKit
import shared

struct PostItem : View {
    
    let meme: MemeLord
    let theme: Theme
    let navigateToUser: @MainActor () -> Unit
    let navigateToImage: @MainActor (Int) -> Unit
    let onLikeClicked: @MainActor () -> Unit
    let onCommentClicked: @MainActor () -> Unit
    let onShareClicked: @MainActor () -> Unit

    var body: some View {
        let contentPadding = EdgeInsets(top: 5, leading: 15, bottom: 5, trailing: 15)
        HStack {
            VStack {
                VStack {
                    HStack {
                        ImageCacheView(meme.user.profilePicture, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                            .frame(width: 40, height: 40)
                            .clipShape(Circle())
                        Spacer().frame(width: 8)
                        Text(meme.user.name)
                            .lineLimit(1)
                            .truncationMode(.tail)//overflow
                            .foregroundColor(theme.textColor)
                    }.padding().onStart().onTapGesture(perform: navigateToUser)
                    Spacer().frame(height: 8)
                    PostContentScrollable(about: meme.post.content, theme: theme)
                    if (meme.post.isHaveMedia) {
                        ScrollView(.horizontal) {
                            LazyHStack {
                                ForEach(Array(meme.post.postMedia.enumerated()), id: \.offset) { index, data in
                                    let media = data as PostMedia
                                    ImageCacheView(media.mediaURL, isVideoPreview: false, contentMode: .fit)
                                        .frame(height: 200).onTapGesture {
                                            navigateToImage(index)
                                        }
                                }
                            }.clipShape(RoundedRectangle(cornerRadius: 20))
                        }.padding(leading: 10, trailing: 10).frame(height: 200)
                    }
                    Spacer().frame(height: 8)
                    HStack {
                        Spacer().frame(width: 5)
                        Text(String(meme.likes.count) + " likes").foregroundStyle(theme.textHintColor).font(.caption2)
                        Spacer().frame(width: 16)
                        Text(String(meme.comments.count) + " comments").foregroundStyle(theme.textHintColor).font(.caption2)
                    }.onStart()
                    HStack {
                        Spacer(minLength: 0)
                        Button(action: onLikeClicked) {
                            HStack {
                                ImageAsset(icon: "favorite", tint: meme.isLiked ? Color.red : Color.red.margeColors(Color(red: 68 / 255, green: 68 / 255, blue: 68 / 255), 0.6)).frame(width: 25, height: 25)
                                Spacer().frame(width: 4)
                                Text("Like").foregroundStyle(theme.textColor).font(.system(size: 13)).lineLimit(1)
                            }
                        }.padding(contentPadding)
                        Spacer(minLength: 0)
                        Button(action: onCommentClicked) {
                            HStack {
                                ImageAsset(icon: "comment", tint: Color(red: 162 / 255, green: 104 / 255, blue: 18 / 255)).frame(width: 25, height: 25)
                                Spacer().frame(width: 4)
                                Text("Comment").foregroundStyle(theme.textColor).font(.system(size: 13)).lineLimit(1)
                            }
                        }.padding(contentPadding)
                        Spacer(minLength: 0)
                        Button(action: onShareClicked) {
                            HStack {
                                ImageAsset(icon: "share", tint: Color.green).frame(width: 25, height: 25)
                                Spacer().frame(width: 4)
                                Text("Share").foregroundStyle(theme.textColor).font(.system(size: 13)).lineLimit(1)
                            }
                        }.padding(contentPadding)
                        Spacer(minLength: 0)
                    }
                }
            }.background(RoundedRectangle(cornerRadius: 7).fill(theme.backgroundPrimary))
                .shadow(radius: 4)
            Spacer()
        }.onStart().padding(10)
    }
}

struct CommentBottomSheet : View, KeyboardReadable {
    
    let memeLord: MemeLord?
    let commentText: String
    let onValueComment: (String) -> Unit
    let onComment: (MemeLord) -> Unit
    
    @Inject
    private var theme: Theme
    @FocusState private var isFoucesed: Bool
    private let invisibleTopViewId: String = "TOP_ID"
    
    var body: some View {
        NavigationStack {
            VStack {
                HStack {
                    HStack {
                        TextField("", text: Binding(get: {
                            commentText
                        }, set: onValueComment), axis: Axis.vertical
                        ).onReceive(keyboardPublisher) { isKeyboardVisible in
                            print("Is keyboard visible? ", isKeyboardVisible)
                        }.placeholder(when: commentText.isEmpty, alignment: .leading, placeholder: {
                            Text("Write Comment...")
                                .foregroundColor(theme.textHintColor)
                        }).focused($isFoucesed).multilineTextAlignment(.leading).foregroundColor(theme.textColor).frame(alignment: .leading)
                            .onTapGesture {
                                isFoucesed = true
                            }
                            .environment(\.layoutDirection, textDirection(for: commentText))// TEMP Solution

                        Button(action: {
                            isFoucesed = false
                            if let memeLord = self.memeLord {
                                onComment(memeLord)
                            }
                        }, label: {
                            ImageAsset(icon: "send", tint: theme.textGrayColor)
                                .frame(width: 30, height: 30)
                        }).frame(width: 50, height: 50, alignment: .center)
                    }.padding(leading: 10, trailing: 5)
                }.shadow(radius: 3).background(theme.background.margeWithPrimary(0.3))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                Spacer().frame(height: 16)
                ScrollViewReader { value in
                    ScrollView {
                        Color.clear
                            .frame(height: 0)
                            .id(invisibleTopViewId)
                        LazyVStack {
                            if let memeLord = self.memeLord {
                                ForEach(Array(memeLord.comments.enumerated()), id: \.offset) { index, data in
                                    let comment = data as Comment
                                    CommentItem(comment: comment, theme: theme).id(String(comment.id) + comment.userId + String(comment.postId))
                                    Divider().padding(leading: 8, trailing: 8)
                                }
                            }
                        }
                    }.onChange(memeLord?.comments.count ?? 0) { _ in
                        value.scrollTo(invisibleTopViewId)
                    }
                }
            }.navigationTitle("Comments")
        }
    }
}

struct CommentItem : View {
    
    let comment: Comment
    let theme: Theme
    
    var body: some View {
        HStack {
            ImageCacheView(comment.userImage, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                .clipShape(Circle())
                .frame(width: 40, height: 40).onTop()
            HStack {
                Spacer().frame(width: 8).background(.red)
                VStack {
                    Text(comment.userName).foregroundStyle(theme.textColor).onStart()
                    Text(comment.content).font(.caption).foregroundStyle(theme.textColor).onStart()
                    Text(comment.timestamp).font(.caption2).foregroundStyle(theme.textHintColor).onStart()
                }.onStart()
            }
        }.padding()
    }
}


/// Publisher to read keyboard changes.
protocol KeyboardReadable {
    var keyboardPublisher: AnyPublisher<Bool, Never> { get }
}

extension KeyboardReadable {
    var keyboardPublisher: AnyPublisher<Bool, Never> {
        Publishers.Merge(
            NotificationCenter.default
                .publisher(for: UIResponder.keyboardWillShowNotification)
                .map { _ in true },
            
            NotificationCenter.default
                .publisher(for: UIResponder.keyboardWillHideNotification)
                .map { _ in false }
        )
        .eraseToAnyPublisher()
    }
}

struct CommentSheetDetent: CustomPresentationDetent {
    static func height(in context: Context) -> CGFloat? {
        if context.dynamicTypeSize.isAccessibilitySize {
            return context.maxDetentValue
        } else {
            return context.maxDetentValue * 0.8
        }
    }
}


struct PostContentScrollable : View {
   
    let about: [PostContent]
    let theme: Theme

    var body: some View {
        VStack(alignment: .leading) {
            ForEach(0..<about.count, id:\.self) { idx in
                let post = about[idx]
                Text(post.text)
                    .multilineTextAlignment(.leading)
                    .foregroundStyle(theme.textColor)
                    .font(.system(size: CGFloat(post.font)))
                    .padding(leading: 20, trailing: 20)
                    .lineLimit(nil).onStart()
                    .environment(\.layoutDirection, textDirection(for: post.text))// TEMP Solution
            }
        }
    }
}
