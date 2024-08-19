//
//  ChatScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared
import Combine
import UIKit

struct ChatScreen : View, KeyboardReadable {
    
    let userBase: UserBase
    let screenConfig: (Screen) -> (any ScreenConfig)?
    let backPress: @MainActor () -> Unit
    
    @Inject
    private var theme: Theme

    @StateObject private var obs: ChatObserve = ChatObserve()
    @FocusState private var isFoucesed: Bool

    private let invisibleTopViewId: String = "BOTTOM_ID"
    private let seenColor = Color(red: 79, green: 195, blue: 247)
    
    var body: some View {
        let state = obs.state
        ZStack {
            VStack {
                HStack {
                    HStack {
                        BackPressButton(action: backPress)
                        ImageCacheView(state.chat.chatPic, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                            .frame(width: 40, height: 40)
                            .clipShape(Circle())
                        Spacer().frame(width: 4)
                        Text(state.chat.chatLabel)
                            .lineLimit(1)
                            .truncationMode(.tail)//overflow
                            .foregroundColor(theme.textColor)
                        Spacer()
                        Button {
                            
                        } label: {
                            ImageAsset(icon: "more", tint: theme.textColor).padding(5).frame(width: 30, height: 30)
                        }.padding()
                    }.onStart().frame(height: 60).background(theme.background)
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
                ScrollViewReader { value in
                    ScrollView {
                        ForEach(Array(state.messages.enumerated()), id: \.offset) { index, data in
                            let message = data as Message
                            ChatBubble(message: message, theme: theme, showSenderName: state.chat.showSenderName, seenColor: seenColor)
                        }
                        Color.clear
                            .frame(height: 0)
                            .id(invisibleTopViewId)
                    }.onChange(state.messages.count) { it in
                        value.scrollTo(invisibleTopViewId)
                    }
                }
                Spacer()
                HStack {
                    HStack {
                        TextField("", text: Binding(get: {
                            state.chatText
                        }, set: { it in
                            obs.onTextChanged(text: it)
                        }), axis: Axis.vertical
                        ).onReceive(keyboardPublisher) { isKeyboardVisible in
                            print("Is keyboard visible? ", isKeyboardVisible)
                        }.placeholder(when: state.chatText.isEmpty, alignment: .leading, placeholder: {
                            Text("Message...")
                                .foregroundColor(theme.textHintColor)
                        }).focused($isFoucesed).multilineTextAlignment(.leading).foregroundColor(theme.textColor).frame(alignment: .leading)
                            .onTapGesture {
                                isFoucesed = true
                            }
                        Button(action: {
                            isFoucesed = false
                            obs.onSend(senderId: userBase.id)
                        }, label: {
                            ImageAsset(icon: "send", tint: theme.textGrayColor)
                                .frame(width: 30, height: 30)
                        }).frame(width: 50, height: 50, alignment: .center)
                    }.padding(leading: 10, trailing: 5)
                }.shadow(radius: 3).background(theme.background.margeWithPrimary(0.3))
                    .clipShape(.rect(topLeadingRadius: 8, topTrailingRadius: 8))
            }
        }.background(theme.background)
            .toolbar(.hidden)
            .onAppear {
                guard let screenConfig = screenConfig(.CHAT_SCREEN_ROUTE) as? ChatRoute else {
                    return
                }
                obs.loadChat(userId: userBase.id, chatId: screenConfig.chatId, chat: screenConfig.chat)
            }
    }
}

struct ChatBubble : View {
    
    let message: Message
    let theme: Theme
    let showSenderName: Bool
    let seenColor: Color

    var body: some View {
        VStack {
            VStack {
                if showSenderName && !message.isSender {
                    Text(message.senderName).font(.title2).foregroundStyle(theme.textHintColor)
                }
                VStack {
                    VStack {
                        VStack {
                            Text(message.content).foregroundStyle(theme.textColor)
                        }.onStart()
                        VStack {
                            HStack {
                                Spacer()
                                Text(message.timestamp).font(.system(size: 13)).foregroundStyle(theme.textGrayColor)
                                Spacer().frame(width: 4)
                                if message.isSender {
                                    ImageAsset(icon: "done_all", tint: message.isSeen ? Color.blue : theme.textGrayColor).frame(width: 17, height: 17)
                                }
                            }.onEnd()
                        }
                    }.padding(all: 4)
                }.background(RoundedRectangle(cornerRadius: 8).fill(message.isSender ? theme.backgroundPrimary : theme.background.margeWithPrimary(0.3))).padding(all: 8)
            }.frame(minWidth: 200).padding(leading: message.isSender ? 40 : 0, trailing: message.isSender ? 0 : 40)
        }.onStart()
    }
}
