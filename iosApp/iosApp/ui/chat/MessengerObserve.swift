//
//  MessengerObserve.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class MessengerObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    func loadChat(userId: String) {
        scope.launchBack {
            if let list = try? await self.project.chat.getChatsOnUser(userIds: [userId]) {
                let flattened = list.map { it in
                    it.members
                }.flatMap { $0 }
                if let users = try? await self.project.profile.getAllProfilesOnUserIds(ids: flattened) {
                    let chats = list.map { it in
                        it.injectChatForChatScreenIos(userId: userId, users: users)
                    }
                    self.scope.launchMain {
                        self.state = self.state.copy(chats: chats, isProcess: false)
                        self.fetchRealTimeMessages(chatIds: chats.map { it in KotlinLong(value: it.id) }, userId: userId)
                     }
                } else {
                    self.setProcess(false)
                }
            } else {
                self.setProcess(false)
            }
        }
    }

    @MainActor
    private func fetchRealTimeMessages(chatIds: [KotlinLong], userId: String) {
        let state = self.state
        scope.launchBack {
            try? await self.project.message.fetchRealTimeMessagesOfChats(chatIds: chatIds) { messages in
                self.scope.launchBack {
                    if let newChats = try? await ConverterKt.injectChats(state.chats, messages: messages, userId: userId) {
                        self.scope.launchMain {
                            self.state = self.state.copy(chats: newChats, dummy: self.state.dummy + 1)
                        }
                    }
                }
            }
        }
    }
    
    
    private func setProcess(_ isProcess: Bool) {
        self.scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    struct State {
        
        private(set) var chats: [Chat] = []
        private(set) var dummy: Int = 0
        private(set) var isProcess: Bool = true

        @MainActor
        mutating func copy(
            chats: [Chat]? = nil,
            dummy: Int? = nil,
            isProcess: Bool? = nil
        ) -> Self {
            self.chats = chats ?? self.chats
            self.dummy = dummy ?? self.dummy
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }

}
