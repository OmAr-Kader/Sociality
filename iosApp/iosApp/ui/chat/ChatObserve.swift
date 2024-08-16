//
//  ChatObserve.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class ChatObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    func loadChat(userId: String, chatId: Long, chat: Chat?) {
        if let chat = chat {
            if (chatId == 0) {
                scope.launchBack {
                    if let cloudChat = try? await self.project.chat.getChatOnUsers(userIds: chat.members) {
                        if let users = try? await self.project.profile.getAllProfilesOnUserIds(ids: cloudChat.members) {
                            let chatInjected = cloudChat.injectChatForChatScreenIos(userId: userId, users: users)
                            self.scope.launchMain {
                                self.state = self.state.copy(chat: chatInjected, isProcess: false)
                                self.fetchRealTimeMessages(chatId: chatInjected.id, userId: userId)
                            }
                        }
                    } else {
                        if let users = try? await self.project.profile.getAllProfilesOnUserIds(ids: chat.members) {
                            let chatInjected = chat.injectChatForChatScreenIos(userId: userId, users: users)
                            self.scope.launchMain {
                                self.state = self.state.copy(chat: chatInjected, isProcess: false)
                                self.fetchRealTimeMessages(chatId: chatInjected.id, userId: userId)
                            }
                        }
                    }
                }
            } else {
                self.scope.launchMain {
                    self.state = self.state.copy(chat: chat, isProcess: false)
                    self.fetchRealTimeMessages(chatId: chat.id, userId: userId)
                }
            }
        } else {
            scope.launchBack {
                if let cloudChat = try? await self.project.chat.getChatOnId(id: chatId) {
                    if let users = try? await self.project.profile.getAllProfilesOnUserIds(ids:cloudChat.members) {
                        let chatInjected = cloudChat.injectChatForChatScreenIos(userId: userId, users: users)
                        self.scope.launchMain {
                            self.state = self.state.copy(chat: chatInjected, isProcess: false)
                            self.fetchRealTimeMessages(chatId: chatInjected.id, userId: userId)
                        }
                    }
                } else {
                    self.setProcess(false)
                }
            }
        }
    }
    
    /**
     fun loadChat(userId: String, chatRoute: Screen.ChatRoute) {
             if (chatRoute.chat != null) {
                 
             } else {
                 launchBack {
                     project.chat.getChatOnId(chatRoute.chatId)?.also { chat ->
                         project.profile.getAllProfilesOnUserIds(chat.members.toList()).also { users ->
                             chat.injectChatForChatScreen(userId, users).also { chatInjected ->
                                 _uiState.update { state ->
                                     state.copy(chat = chatInjected, isProcess = false)
                                 }
                                 fetchRealTimeMessages(chatId = chatInjected.id, userId = userId)
                             }
                         }
                     } ?: setProcess(false)
                 }
             }
         }
     func loadChat(userId: String, chatRoute: Screen.ChatRoute) {
         if let chat = chatRoute.chat {
             if chatRoute.chatId == 0 {
                 launchBack {
                     if let chat = project.chat.getChatOnUsers(chatRoute.chat.members) {
                         project.profile.getAllProfilesOnUserIds(chat.members) { users in
                             let chatInjected = chat.injectChatForChatScreen(userId: userId, users: users)
                             _uiState.update { state in
                                 state.copy(chat: chatInjected, isProcess: false)
                             }
                             fetchRealTimeMessages(chatId: chatInjected.id, userId: userId)
                         }
                     } else {
                         project.profile.getAllProfilesOnUserIds(chatRoute.chat.members) { users in
                             let chatInjected = chatRoute.chat.injectChatForChatScreen(userId: userId, users: users)
                             _uiState.update { state in
                                 state.copy(chat: chatInjected, isProcess: false)
                             }
                             fetchRealTimeMessages(chatId: chatInjected.id, userId: userId)
                         }
                     }
                 }
             } else {
                 _uiState.update { state in
                     state.copy(chat: chatRoute.chat, isProcess: false)
                 }
                 fetchRealTimeMessages(chatId: chatRoute.chat.id, userId: userId)
             }
         } else {
             launchBack {
                 if let chat = project.chat.getChatOnId(chatRoute.chatId) {
                     project.profile.getAllProfilesOnUserIds(chat.members) { users in
                         let chatInjected = chat.injectChatForChatScreen(userId: userId, users: users)
                         _uiState.update { state in
                             state.copy(chat: chatInjected, isProcess: false)
                         }
                         fetchRealTimeMessages(chatId: chatInjected.id, userId: userId)
                     }
                 } else {
                     setProcess(false)
                 }
             }
         }
     }

     
     */
    
    @MainActor
    private func fetchRealTimeMessages(chatId: Long, userId: String) {
        let state = self.state
        scope.launchBack {
            try? await self.project.message.fetchRealTimeMessages(chatId: chatId) { messages in
                if (state.messages.isEmpty) {
                    self.markRead(messages: messages, userId: userId)
                }
                let chatMessages = ConverterKt.injectChatMessagesNormal(messages, chat: state.chat, userId: userId)
                self.scope.launchMain {
                    self.state = self.state.copy(messages: chatMessages, dummy: state.dummy + 1, isProcess: false)
                }
            }
        }
    }

    
    @MainActor
    func onSend(senderId: String) {
        let state = self.state
        scope.launchBack {
            if (state.chat.id == 0) {
                if let chat = try? await self.project.chat.addNewChat(item: state.chat) {
                    let message = Message().copy(
                        chatId: chat.id,
                        senderId: senderId,
                        content: state.chatText,
                        date: DateKt.dateNow,
                        readersIds: [senderId]
                    )
                    
                    if let firstMessage = try? await self.project.message.addNewMessage(item: message) {
                        let chatInjected = chat.injectChatForChatScreenIos(userId: senderId, users: chat.users)
                        
                        if let chatMessages = try? await ConverterKt.injectChatMessages([firstMessage], chat: chat, userId: senderId) {
                            self.scope.launchMain {
                                self.state = self.state.copy(chat: chatInjected, messages: chatMessages, chatText: "", dummy: state.dummy + 1, isProcess: false)
                                self.fetchRealTimeMessages(chatId: chatInjected.id, userId: senderId)
                            }
                        }
                    }
                }
                
            } else {
                let message = Message().copy(
                    chatId: state.chat.id,
                    senderId: senderId,
                    content: state.chatText,
                    date: DateKt.dateNow,
                    readersIds: [senderId]
                )
                if let pushedMessage = try? await self.project.message.addNewMessage(item: message) {
                    if let chatMessages = try? await ConverterKt.injectChatMessages(state.messages + [pushedMessage], chat: state.chat, userId: senderId) {
                        self.scope.launchMain {
                            self.state = self.state.copy(messages: chatMessages, chatText: "", dummy: state.dummy + 1 , isProcess: false)
                        }
                    }
                }
            }
        }
    }
    
    private func markRead(messages: [Message], userId: String) {
        scope.launchBack {
            let unreadMessages = messages.filter { !$0.readersIds.contains(userId) }
            if !unreadMessages.isEmpty {
                let messages = unreadMessages.map { it in it.copy(readersIds: it.readersIds + [userId]) }
                let _ = try? await self.project.message.editMessages(item: messages)
            }
        }
    }

    @MainActor
    func onTextChanged(text: String) {
        self.state = self.state.copy(chatText: text)
    }

    
    private func setProcess(_ isProcess: Bool) {
        self.scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor private func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        private (set) var chat: Chat = Chat()
        private(set) var messages: [Message] = []
        private(set) var chatText: String = ""
        private(set) var dummy: Int = 0
        private(set) var isProcess: Bool = true

        @MainActor
        mutating func copy(
            chat: Chat? = nil,
            messages: [Message]? = nil,
            chatText: String? = nil,
            dummy: Int? = nil,
            isProcess: Bool? = nil
        ) -> Self {
            self.chat = chat ?? self.chat
            self.messages = messages ?? self.messages
            self.chatText = chatText ?? self.chatText
            self.dummy = dummy ?? self.dummy
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }

}
