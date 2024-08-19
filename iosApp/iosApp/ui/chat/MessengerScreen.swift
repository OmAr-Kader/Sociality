//
//  MessengerScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MessengerScreen : View {

    let userBase: UserBase
    let navigateToScreen: @MainActor (any ScreenConfig, Screen) -> Unit
    let backPress: @MainActor () -> Unit
    
    @Inject
    private var theme: Theme
    
    @StateObject
    private var obs: MessengerObserve = MessengerObserve()
    
    var body: some View {
        let state = obs.state
        ZStack {
            VStack {
                HStack {
                    HStack {
                        BackPressButton(action: backPress)
                        Spacer().frame(width: 10)
                        Text("Messenger")
                            .lineLimit(1)
                            .foregroundColor(theme.textColor)
                        Spacer()
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
                ScrollView {
                    ForEach(Array(state.chats.enumerated()), id: \.offset) { index, data in
                        let chat = data as Chat
                        ChatItemView(chatItem: chat, theme: theme) {
                            navigateToScreen(ChatRoute(chatId: chat.id, chat: chat), .CHAT_SCREEN_ROUTE)
                        }
                    }
                }
            }
        }.background(theme.background)
            .toolbar(.hidden).onAppear {
                obs.loadChat(userId: userBase.id)
            }
    }
}

struct ChatItemView : View {

    let chatItem: Chat
    let theme: Theme
    let onClick: () -> Unit
    
    var body: some View {
        HStack {
            ImageCacheView(chatItem.chatPic, isVideoPreview: false, contentMode: .fill, errorImage: UIImage(named: "profile")?.withTintColor(UIColor(theme.textColor)))
                .clipShape(Circle())
                .frame(width: 40, height: 40)
            Spacer().frame(width: 8)
            ZStack {
                Text(chatItem.chatLabel)
                    .lineLimit(1)
                    .truncationMode(.tail)//overflow
                    .foregroundColor(theme.textColor).onStart().onTop()
                VStack {
                    Spacer().frame(height: 24)
                    HStack {
                        Text(chatItem.lastMessageLineLess)
                            .foregroundStyle(theme.textHintColor)
                            .lineLimit(1)
                            .font(chatItem.numberOfLastMessages == 0 ? .system(size: 16) : .system(size: 16).bold())
                            .truncationMode(.tail).onStart()
                        Spacer().frame(width: 8)
                        if(chatItem.numberOfLastMessages != 0) {
                            Text(
                                String(chatItem.numberOfLastMessages)
                            ).frame(width: 20, height: 20)
                                .background(Circle().fill(Color(red: 155 / 255, green: 0, blue: 0)))
                                .foregroundStyle(Color.white)
                                .lineLimit(1)
                                .font(.system(size: 11))
                                .truncationMode(.tail).onStart()
                        }
                    }
                    Spacer().frame(height: 4)
                    Text(chatItem.timestampLastMessage)
                        .lineLimit(1)
                        .font(.system(size: 13))
                        .truncationMode(.tail)//overflow
                        .foregroundColor(theme.textColor).onStart()
                }
            }
            Spacer()
        }.frame(height: 60)
            .onStart()
            .padding(all: 8)
            .onTapGesture(perform: onClick)
    }
}
