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
    let navigateToUser: () -> Unit
    let navigateToImage: (Int) -> Unit
    let onLikeClicked: () -> Unit
    let onCommentClicked: () -> Unit
    let onShareClicked: () -> Unit

    var body: some View {
        let contentPadding = EdgeInsets(top: 5, leading: 15, bottom: 5, trailing: 15)
        HStack {
            Spacer()
            VStack {
                HStack {
                    ImageCacheView(meme.user.profilePicture, isVideoPreview: false, contentMode: .fit)
                        .clipShape(Circle())
                        //.cornerRadius(20)
                        .frame(width: 40, height: 40)
                    Spacer().frame(width: 8)
                    Text(meme.user.name)
                        .lineLimit(1)
                        .truncationMode(.tail)//overflow
                        .foregroundColor(theme.textColor)
                }.padding().onTapGesture(perform: navigateToUser)
                Spacer().frame(height: 8)
                PostContentScrollable(about: meme.post.content, theme: theme)
                if (meme.post.isHaveMedia) {
                    ScrollView(.horizontal) {
                        LazyHStack {
                            ForEach(Array(meme.post.postMedia.enumerated()), id: \.offset) { index, date in
                                let media = date as PostMedia
                                ImageCacheView(media.mediaURL, isVideoPreview: false, contentMode: .fit)
                                    .clipShape(Circle())
                                    .frame(height: 200).onTapGesture {
                                        navigateToImage(index)
                                    }
                            }
                        }
                    }.padding(leading: 10, trailing: 10).frame(height: 200).cornerRadius(20)
                }
                Spacer().frame(height: 8)
                HStack {
                    Text(String(meme.likes.count) + " likes").foregroundStyle(theme.textHintColor).font(.caption2)
                    Spacer().frame(width: 16)
                    Text(String(meme.comments.count) + " comments").foregroundStyle(theme.textHintColor).font(.caption2)
                }.onStart()
                HStack {
                    Spacer(minLength: 0)
                    Button(action: onLikeClicked) {
                        HStack {
                            ImageAsset(icon: "favorite", tint: meme.isLiked ? Color.red : Color.red.margeColors(Color(red: 68 / 255, green: 68 / 255, blue: 68 / 255), 0.6))
                            Spacer().frame(width: 4)
                            Text("Like").foregroundStyle(theme.textColor).font(.system(size: 13)).lineLimit(1)
                        }
                    }.padding(contentPadding)
                    Spacer(minLength: 0)
                    Button(action: onCommentClicked) {
                        HStack {
                            ImageAsset(icon: "comment", tint: Color(red: 162 / 255, green: 104 / 255, blue: 18 / 255))
                            Spacer().frame(width: 4)
                            Text("Comment").foregroundStyle(theme.textColor).font(.system(size: 13)).lineLimit(1)
                        }
                    }.padding(contentPadding)
                    Spacer(minLength: 0)
                    Button(action: onShareClicked) {
                        HStack {
                            ImageAsset(icon: "share", tint: Color.green)
                            Spacer().frame(width: 4)
                            Text("Share").foregroundStyle(theme.textColor).font(.system(size: 13)).lineLimit(1)
                        }
                    }.padding(contentPadding)
                    Spacer(minLength: 0)
                }
            }
            
            Spacer()
        }.onStart().padding()
            .background(theme.backgroundPrimary)
            .cornerRadius(7)
            .shadow(radius: 4)
    }
}

struct CommentBottomSheet : View, KeyboardReadable {
    
    let memeLord: MemeLord?
    let commentText: String
    let onValueComment: (String) -> Unit
    let hide: () -> Unit
    let onComment: (MemeLord) -> Unit

    @Inject
    private var theme: Theme
    @FocusState private var isFoucesed: Bool
    private var invisibleTopViewId: String = "TOP_ID"

    var body: some View {
        /*ScrollViewReader { value in
        }*/
        sheet(isPresented: Binding(get: {
            memeLord != nil
        }, set: { it in
            if !it {
                hide()
            }
        })) {
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
                                Text("Question?")
                                    .foregroundColor(theme.textHintColor)
                            }).focused($isFoucesed).multilineTextAlignment(.leading).foregroundColor(theme.textColor).frame(alignment: .leading)
                                .onTapGesture {
                                isFoucesed = true
                            }
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
                        .clipShape(.rect(topLeadingRadius: 8, topTrailingRadius: 8))
                    Spacer().frame(height: 16)
                    ScrollViewReader { value in
                        ScrollView {
                            Color.clear
                                .frame(height: 0)
                                .id(invisibleTopViewId)
                            LazyVStack {
                                if let memeLord = self.memeLord {
                                    ForEach(Array(memeLord.comments.enumerated()), id: \.offset) { index, date in
                                        let comment = date as Comment
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
            }.presentationDetents([.medium, .custom(MyCustomDetent.self)])
        }.background(theme.backDark)
    }
}

struct CommentItem : View {
    
    let comment: Comment
    let theme: Theme
    
    var body: some View {
        HStack {
            ImageCacheView(comment.userImage, isVideoPreview: false, contentMode: .fit)
                .clipShape(Circle())
                //.cornerRadius(20)
                .frame(width: 40, height: 40)
            Spacer().frame(height: 8)
            VStack {
                Text(comment.userName).font(.title).foregroundStyle(theme.textColor)
                Text(comment.content).font(.body).foregroundStyle(theme.textColor)
                Text(comment.timestamp).font(.caption2).foregroundStyle(theme.textHintColor)
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

struct MyCustomDetent: CustomPresentationDetent {
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
            }
        }
    }
}
