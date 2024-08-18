//
//  Defaults.swift
//  iosApp
//
//  Created by OmAr on 14/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import shared

extension Chat {
    
    func copy(id: Int64? = nil, chartTitle: String? = nil, members: [String]? = nil, chatImage: String? = nil, users: [User]? = nil, chatPic: String? = nil, chatLabel: String? = nil, lastMessage: String? = nil, timestampLastMessage: String? = nil, numberOfLastMessages: Int32? = nil) -> Chat {
        return Chat(id: id ?? self.id, chartTitle: chartTitle ?? self.chartTitle, members: members ?? self.members, chatImage: chatImage ?? self.chatImage, users: users ?? self.users, chatPic: chatPic ?? self.chatPic, chatLabel: chatLabel ?? self.chatLabel, lastMessage: lastMessage ?? self.lastMessage, timestampLastMessage: timestampLastMessage ?? self.timestampLastMessage, numberOfLastMessages: numberOfLastMessages ?? self.numberOfLastMessages
        )
    }
}

extension Message {
    
    func copy(id: Int64? = nil, chatId: Int64? = nil, senderId: String? = nil, content: String? = nil, date: String? = nil, readersIds: [String]? = nil, isSeen: Bool? = nil, isSender: Bool? = nil, senderName: String? = nil
    ) -> Message {
        return Message(id: id ?? self.id, chatId: chatId ?? self.chatId, senderId: senderId ?? self.senderId, content: content ?? self.content, date: date ?? self.date, readersIds: readersIds ?? self.readersIds, isSeen: isSeen ?? self.isSeen, isSender: isSender ?? self.isSender, senderName: senderName ?? self.senderName)
    }
}

extension Post {
    
    func copy(
        id: Int64? = nil,
        userId: String? = nil,
        content: [PostContent]? = nil,
        postMedia: [PostMedia]? = nil,
        date: String? = nil,
        lastEdit: String? = nil
    ) -> Post {
        return Post(id: id ?? self.id, userId: userId ?? self.userId, content: content ?? self.content, postMedia: postMedia ?? self.postMedia, date: date ?? self.date, lastEdit: lastEdit ?? self.lastEdit)
    }
}

extension PostContent {
    
    func copy(font: Int32? = nil, text: String? = nil) -> PostContent {
        return PostContent(font: font ?? self.font, text: text ?? self.text)
    }
}


extension Friendship {
 
    func copy(id: Long? = nil,  userId: String? = nil, friends: Array<String>? = nil) -> Friendship {
        
        return Friendship(id: id ?? self.id, userId: userId ?? self.userId, friends: friends ?? self.friends)
    }
}

extension Comment {
    //Default
    func copy(id: Long? = nil, userId: String? = nil,postId: Long? = nil, content: String? = nil, date: String? = nil, userName: String? = nil, userImage: String? = nil) -> Comment {
        return Comment(id: id ?? self.id, userId: userId ?? self.userId, postId: postId ?? self.postId, content: content ?? self.content, date: date ?? self.date, userName: userName ?? self.userName, userImage: userImage ?? self.userImage)
    }
}

extension UserBase {
    
    func copy(id: String? = nil,  username: String? = nil, email: String? = nil, name: String? = nil, profilePicture: String? = nil) -> UserBase {
        return UserBase(id: id ?? self.id, username: username ?? self.username, email: email ?? self.email, name: name ?? self.name, profilePicture: profilePicture ?? self.profilePicture)
    }
}

extension User {
    
    func copy(id: Long? = nil, userId: String? = nil, username: String? = nil, email: String? = nil, name: String? = nil, bio: String? = nil, profilePicture: String? = nil, mode: Int32? = nil) -> User {
        return User(id: id ?? self.id, userId: userId ?? self.userId, username: username ?? self.username, email: email ?? self.email, name: name ?? self.name, bio: bio ?? self.bio, profilePicture: profilePicture ?? self.profilePicture, mode: mode ?? self.mode)
    }
}
