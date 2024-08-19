//
//  HomeObserve.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class HomeObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
   
    @MainActor
    func loadMemes(userId: String, invoke: @MainActor @escaping ([MemeLord]) -> Unit) {
        scope.launchBack {
            if let friendship = try? await self.project.friendship.getFriendshipOnId(userId: userId) {
                let userIds = friendship.friends + [userId]
                if let profiles = try? await self.project.profile.getAllProfilesOnUserIds(ids: userIds) {
                    do {
                        try await self.project.post.getAllPostsOnUserFriends(users: profiles, userId: userId) { memeLords in
                            self.scope.launchMain {
                                invoke(memeLords)
                            }
                        }
                    } catch {
                        self.setProcess(false)
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
    func updateMemes(memeLords: [MemeLord]) {
        self.state = self.state.copy(memes: memeLords, isProcess: false)
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
    func onValueComment(commentText: String) {
        self.state = self.state.copy(commentText: commentText)
    }

    @MainActor
    func onShareClicked() {

    }

    @MainActor
    func hide() {
        self.state = self.state.copy(commentMeme: nil, isComment: false, commentText: "")
    }

    func signOut(invoke: @escaping () -> Void, failed: @escaping () -> Void) {
        setProcess(true)
        scope.launchBack {
            do {
                let result = try await self.project.pref.deletePrefAll()
                if (result.int32Value == RealmKt.REALM_SUCCESS) {
                    try await AuthKt.signOutAuth(invoke: {
                        self.scope.launchMain {
                            self.setMainProcess(false)
                            invoke()
                        }
                    }, failed: {
                        self.scope.launchMain {
                            self.setMainProcess(false)
                            failed()
                        }
                    })
                } else {
                    self.scope.launchMain {
                        self.setMainProcess(false)
                        failed()
                    }
                }
            } catch {
                self.scope.launchMain {
                    self.setMainProcess(false)
                    failed()
                }
            }
        }
    }

    
    private func checkDeleting(result: Int, _ invoke: () async -> Unit,_ failed: () async -> Unit) async {
        if (result == RealmKt.REALM_SUCCESS) {
            await invoke()
        } else {
            await failed()
        }
    }
    
    private func setLock(_ isLocked: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isLocked: isLocked)
        }
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
        
        private(set) var memes: [MemeLord] = []
        private(set) var commentMeme: MemeLord? = nil
        private(set) var isComment: Bool = false
        private(set) var commentText: String = ""
        private(set) var dummy: Int = 0
        private(set) var isProcess: Bool = true
        private(set) var isLocked: Bool = false

        @MainActor
        mutating func copy(
            memes: [MemeLord]? = nil,
            commentMeme: MemeLord? = nil,
            isComment: Bool? = nil,
            commentText: String? = nil,
            dummy: Int? = nil,
            isProcess: Bool? = nil,
            isLocked: Bool? = nil
        ) -> Self {
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
