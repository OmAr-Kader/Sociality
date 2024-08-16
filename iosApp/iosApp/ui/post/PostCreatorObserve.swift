//
//  PostCreatorObserve.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class PostCreatorObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    @MainActor
    func createPost(userId: String, invoke: @MainActor @escaping (Post) -> Void, failed: @MainActor @escaping () -> Void) {
        let post = state.postCreate
        if post.content.first == nil || post.content.first!.text.isEmpty {
            self.state = self.state.copy(isErrorPressed: false)
            return
        }
        let currentDate = DateKt.dateNow
        let newPost = post.copy(userId: userId, date: currentDate, lastEdit: currentDate)
        doCreatePost(post: newPost, invoke: invoke, failed: failed)
    }

    private func doCreatePost(post: Post, invoke: @MainActor @escaping (Post) -> Void, failed: @MainActor @escaping () -> Void) {
        scope.launchBack {
            if let newPost = try? await self.project.post.addNewPost(item: post) {
                self.scope.launchMain {
                    invoke(newPost)
                }
            } else {
                self.scope.launchMain {
                    failed()
                }
            }
        }
    }

    @MainActor
    func onMediaSelected(mediaType: Int32, mediaURL: String) {
        state = state.copy(
            postCreate: state.postCreate.copy(
                postMedia: state.postCreate.postMedia + [PostMedia(mediaType: mediaType, mediaURL: mediaURL)]
            )
        )
    }
    
    @MainActor
    func makeFontDialogVisible() {
        self.state = self.state.copy(isFontDialogVisible: true)
    }

    
    @MainActor
    func addAbout(font: Int32) {
        var list = state.postCreate.content
        list.append(PostContent(font: font, text: ""))
        self.state = self.state.copy(postCreate: state.postCreate.copy(content: list), isFontDialogVisible: false, isErrorPressed: false)
    }

    @MainActor
    func removeAboutIndex(index: Int) {
        var list = state.postCreate.content
        list.remove(at: index)
        self.state = self.state.copy(postCreate: state.postCreate.copy(content: list), dummy: state.dummy + 1)
    }

    @MainActor
    func changeAbout(text: String, index: Int) {
        var list = state.postCreate.content
        list[index] = list[index].copy(text: text)
        self.state = self.state.copy(postCreate: state.postCreate.copy(content: list), dummy: state.dummy + 1)
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
        
        private(set) var postCreate: Post = Post().copy(content: [PostContent(font: ConstKt.HEADLINE_FONT, text: "")])
        private(set) var dummy: Int = 0
        private(set) var isFontDialogVisible: Bool = false
        private(set) var isErrorPressed: Bool = false
        private(set) var isProcess: Bool = false

        @MainActor
        mutating func copy(
            postCreate: Post? = nil,
            dummy: Int? = nil,
            isFontDialogVisible: Bool? = nil,
            isErrorPressed: Bool? = nil,
            isProcess: Bool? = nil
        ) -> Self {
            self.postCreate = postCreate ?? self.postCreate
            self.dummy = dummy ?? self.dummy
            self.isFontDialogVisible = isFontDialogVisible ?? self.isFontDialogVisible
            self.isErrorPressed = isErrorPressed ?? self.isErrorPressed
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }
    
}
