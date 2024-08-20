//
//  PostScreen.swift
//  iosApp
//
//  Created by OmAr on 16/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared
import Zoomable

struct PostScreen : View {
    
    let screenConfig: (Screen) -> (any ScreenConfig)?
    let backPress: @MainActor () -> Unit
    
    @State private var postMedia: [String] = []
    @State private var pos: Int = 0
    @State private var isVisible: Bool = true

    @Inject
    private var theme: Theme

    var body: some View {
        ZStack {
            GeometryReader { proxy in
                PageView(list: postMedia, theme: theme, size: proxy.size) {
                    isVisible.toggle()
                }
                if isVisible {
                    VStack {
                        HStack {
                            BackPressButton(action: backPress).onStart()
                        }.onTop().padding(leading: 5, trailing: 5).frame(height: 60).background(
                            LinearGradient(gradient: Gradient(colors: [theme.backDarkAlpha, Color.clear]), startPoint: .top, endPoint: .bottom)
                        )
                    }.frame(height: 60)
                }
            }
        }.background(theme.background).toolbar(.hidden).padding(0).onAppear {
            guard let args = screenConfig(.POST_SCREEN_ROUTE) as? PostRoute else {
                return
            }
            let list = args.postMedia.map { it in // For Now Only Images
                it.mediaURL
            }
            withAnimation {
                pos = args.pos
                postMedia = list
            }
        }
    }
}

struct PageView : View {
    
    let list: [String]
    let theme: Theme
    let size: CGSize
    let onClick: () -> Unit

    @State var current: Int = 0
    var body: some View {
        TabView(selection: $current) {
            ForEach(0..<list.count, id: \.self) { idx in
                ImageCacheView(list[idx], contentMode: .fit)
                    .frame(width: size.width).tag(idx)
                    .zoomable(
                        minZoomScale: 0.5,        // Default value: 1
                        doubleTapZoomScale: 2,    // Default value: 3
                        outOfBoundsColor: theme.background  // Default value: .clear
                    ).onTapGesture {
                        onClick()
                    }
            }
        }.tabViewStyle(.page(indexDisplayMode: .automatic))
            .frame(width: size.width).onCenter().background(theme.background)
    }
}
