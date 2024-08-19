//
//  ProfileObserve.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class ProfileObserve : ObservableObject {
    
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    @MainActor
    func loadData(userBase: UserBase, userId: String, invoke: @MainActor @escaping (User, Friendship, [FriendshipRequest]) -> Unit, invokeMems: @MainActor @escaping ([MemeLord]) -> Unit) {
        scope.launchBack {
            if let user = try? await self.project.profile.getProfileOnUserId(userId: userId) { // Get User
                await self.fetchMyProfileInfo(myId: userBase.id, userId: userId) { mode, friendShip, requests in
                    self.scope.launchMain {
                        invoke(user.copy(mode: Int32(mode)), friendShip, requests)
                    }
                    self.loadMemes(userBase: userBase, userId: userId, invokeMems: invokeMems)
                }
            } else {
                self.setProcess(false)
            }
        }
    }

    private func loadMemes(userBase: UserBase, userId: String, invokeMems: @MainActor @escaping ([MemeLord]) -> Unit) {
        scope.launchBack {
            do {
                if let friends = try? await self.project.friendship.getFriendshipOnId(userId: userId) {
                    let profiles = try? await self.project.profile.getAllProfilesOnUserIds(ids: friends.friends + [userId])
                    try await self.project.post.getAllPostsOnUser(users: profiles ?? [], userId: userBase.id, profileId: userId) { memeLords in
                        self.scope.launchMain {
                            invokeMems(memeLords)
                        }
                    }
                } else {
                    self.setProcess(false)
                }
            } catch {
                self.setProcess(false)
            }
        }
    }
    
    @MainActor
    func updateUserFrindShipAndRequests(user: User, friendShip: Friendship, requests: [FriendshipRequest]) {
        self.state = self.state.copy(user: user, friendShip: friendShip, requests: requests, isProcess: false)
    }
    
    @MainActor
    func updateMemes(memeLords: [MemeLord]) {
        self.state = self.state.copy(memes: memeLords, isProcess: false)
    }

    private func fetchMyProfileInfo(myId: String, userId: String, invoke: @escaping (Int, Friendship, [FriendshipRequest]) -> Void) async {
        do {
            try await self.project.profile.getProfileInfosOnUserId(userId: myId) { status, friendShip, requests in
                guard let friendShip else {
                    self.setProcess(false)
                    return
                }
                let mode = ConverterKt.getProfileMode(requests, myId: myId, userId: userId, friendShip: friendShip)
                invoke(Int(mode), friendShip, requests)
            }
        } catch {
            setProcess(false)
        }
    }

    @MainActor
    func onAddFriendClicked(fromMe: String, to: String) {
        setMainProcess(true)
        let state = self.state
        scope.launchBack {
            do {
                if let requests = try await self.project.friendRequest.doAddFriendRequest(requests: state.requests, fromMe: fromMe, to: to) {
                    let mode = ConverterKt.getProfileMode(requests, myId: fromMe, userId: to, friendShip: state.friendShip)
                    self.scope.launchMain {
                        self.state = self.state.copy(user: state.user.copy(mode: mode), requests: requests, isProcess: false)
                    }
                } else {
                    self.setProcess(false)
                }
            } catch {
                self.setProcess(false)
            }
        }
    }


    @MainActor
    func onAcceptFriendClicked(userBase: UserBase, userId: String) {
        setMainProcess(true)
        let state = self.state
        scope.launchBack {
            await self.editFriendships(state: state, userBase: userBase, userId: userId) {
                self.scope.launchBack {
                    if let result = try? await self.project.friendRequest.acceptFriendRequest(requests: state.requests, myId: userBase.id, userId: userId), result.int32Value == RealmKt.REALM_SUCCESS  {
                        await self.fetchMyProfileInfo(myId: userBase.id, userId: userId) { mode, friendShip, requests in
                            self.scope.launchMain {
                                self.state = self.state.copy(user: state.user.copy(mode: Int32(mode)), friendShip: friendShip, requests: requests, isProcess: false)
                            }
                        }
                    } else {
                        self.setProcess(false)
                    }
                }
            }
        }
    }
    
    private func editFriendships(state: State, userBase: UserBase, userId: String, invoke: @escaping () -> Void) async {
        do {
            try await project.friendship.doEditFriendships(friendship: state.friendShip, userBase: userBase, userId: userId) { isSuccess in
                if isSuccess.boolValue {
                    invoke()
                } else {
                    self.setProcess(false)
                }
            }
            
        } catch {
            self.setProcess(false)
        }
    }
    
   
    @MainActor
    func onCancelFriendClicked(userBase: UserBase, userId: String) {
        let state = self.state
        if state.isLocked {
            logger("isLocked", "true")
            return
        }
        self.setLock(true)
        scope.launchBack {
            do {
                try await self.project.friendRequest.doCancelFriendRequest(requests: state.requests, userBase: userBase, userId: userId) { newRequests, isSuccess in
                    if isSuccess.boolValue {
                        self.scope.launchMain {
                            self.state = self.state.copy(user: state.user.copy(mode: 0), requests: newRequests, isLocked: false)
                        }
                    } else {
                        self.setLock(false)
                    }
                }
                
            } catch {
                self.setLock(true)
            }
        }
    }

    @MainActor
    func onLikeClicked(userId: String, postId: Int64, isLiked: Bool) {
        let state = self.state
        if state.isLocked {
            logger("isLocked", "true")
            return
        }
        self.setLock(true)
        scope.launchBack {
            do {
                try await self.project.like.liker(memes: state.memes, userId: userId, postId: postId, isLiked: isLiked) { newMemes, isSuccess in
                    if isSuccess.boolValue {
                        self.scope.launchMain {
                            self.state = self.state.copy(memes: newMemes, dummy: state.dummy + 1, isLocked: false)
                        }
                    } else {
                        self.setLock(false)
                    }
                }
            } catch {
                self.setLock(false)
            }
        }
    }

    @MainActor
    func onComment(userBase: UserBase, postId: Int64, invoke: @MainActor @escaping () -> Void) {
        let state = self.state
        if state.isLocked || state.commentText.isEmpty {
            logger("isLocked", "\(state.isLocked)")
            return
        }
        setLock(true)
        scope.launchBack {
            do {
                try await self.project.comment.commenter(memes: state.memes, userBase: userBase, postId: postId, commentText: state.commentText) { newMemes, item in
                    if item != nil {
                        self.scope.launchMain {
                            self.state = self.state.copy(memes: newMemes, commentMeme: item, commentText: "", dummy: state.dummy + 1, isLocked: false)
                            invoke()
                        }
                    } else {
                        self.setLock(false)
                    }
                }
            } catch {
                self.setLock(false)
            }
        }
    }

    @MainActor
    func onCommentClicked(memeLord: MemeLord) {
        self.state = self.state.copy(commentMeme: memeLord, isComment: true, commentText: "")
    }

    @MainActor
    func onShareClicked() {

    }

    @MainActor
    func hide() {
        self.state = self.state.copy(commentMeme: nil, isComment: false, commentText: "")
    }

    @MainActor
    func onValueComment(commentText: String) {
        self.state = self.state.copy(commentText: commentText)
    }
    
    private func setLock(_ isLocked: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isLocked: isLocked)
        }
    }
    
    private func setProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor private func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        private(set) var user: User = User()
        private(set) var friendShip: Friendship = Friendship()
        private(set) var requests: [FriendshipRequest] = []
        private(set) var memes: [MemeLord] = []
        private(set) var commentMeme: MemeLord? = nil
        private(set) var isComment: Bool = false
        private(set) var commentText: String = ""
        private(set) var dummy: Int = 0
        private(set) var isProcess: Bool = true
        private(set) var isLocked: Bool = false

        @MainActor
        mutating func copy(
            user: User? = nil,
            friendShip: Friendship? = nil,
            requests: [FriendshipRequest]? = nil,
            memes: [MemeLord]? = nil,
            commentMeme: MemeLord? = nil,
            isComment: Bool? = nil,
            commentText: String? = nil,
            dummy: Int? = nil,
            isProcess: Bool? = nil,
            isLocked: Bool? = nil
        ) -> Self {
            self.user = user ?? self.user
            self.friendShip = friendShip ?? self.friendShip
            self.requests = requests ?? self.requests
            self.memes = memes ?? self.memes
            self.commentMeme = commentMeme ?? self.commentMeme
            self.isComment = isComment ?? self.isComment
            self.commentText = commentText ?? self.commentText
            self.dummy = dummy ?? self.dummy
            self.isProcess = isProcess ?? self.isProcess
            self.isLocked = isLocked ?? self.isLocked
            return self
        }
    }

}
