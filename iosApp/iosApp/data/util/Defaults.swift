//
//  Defaults.swift
//  iosApp
//
//  Created by OmAr on 14/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import shared

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
