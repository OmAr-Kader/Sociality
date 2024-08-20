# Sociality Demo

Welcome to **Sociality**, a multi-platform social media mobile application built using Kotlin Multiplatform Mobile (KMM) and Supabase. It leverages Supabase's authentication, real-time capabilities, database, and storage to provide a seamless social experience. This project supports both Android and iOS platforms, providing a seamless social media experience where users can share posts, like, comment, interact with other users' content, and communicate through a built-in messenger.

## Features

- **User Authentication:** Secure login and registration process for users.
- **Home Feed:** Display a list of posts from various users, including text and media content.
- **Post Creation:** Users can create new posts with text and images.
- **Likes & Comments:** Users can like posts and add comments.
- **Profile Management:** Users can update their profile information including their name and profile picture.
- **Real-time Updates:** Posts, likes, and comments are updated in real-time.
- **Messenger:** Real-time chat feature that allows users to send and receive messages instantly.
- **Responsive UI:** Fully responsive design for a seamless experience across different screen sizes.
- **Multi-Platform Support:** Built using KMM, Sociality runs on both Android and iOS.

## Getting Started

### Prerequisites

- **Android Studio Bumblebee** or later
- **Xcode 12** or later for iOS
- **Kotlin 1.5** or later

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/OmAr-Kader/Sociality.git
   ```
2. **Open the project in Android Studio for Android development, or in Xcode for iOS development.**

3. **Build and run the project:**
    - For Android: Connect your Android device or use an emulator, then press the "Run" button in Android Studio.
    - For iOS: Connect your iOS device or use a simulator, then press the "Run" button in Xcode.


## 🔗 Links & Dependencies

[![Kotlin Multiplatform Mobile](https://img.shields.io/static/v1?style=for-the-badge&message=Mobile&color=7F52FF&logo=Kotlin&logoColor=FFFFFF&label=Kotlin+Multiplatform)](https://kotlinlang.org/docs/multiplatform.html)

[![Android Studio](https://img.shields.io/static/v1?style=for-the-badge&message=Android+Studio&color=222222&logo=Android+Studio&logoColor=3DDC84&label=)](https://developer.android.com/studio?gclsrc=aw.ds)

[![Xcode](https://img.shields.io/static/v1?style=for-the-badge&message=Xcode&color=147EFB&logo=Xcode&logoColor=FFFFFF&label=)](https://developer.apple.com/documentation/xcode)

[![IOS](https://img.shields.io/static/v1?style=for-the-badge&message=iOS&color=000000&logo=iOS&logoColor=FFFFFF&label=)](https://developer.apple.com/tutorials/app-dev-training)

[![SwiftUi](https://img.shields.io/static/v1?style=for-the-badge&message=Swift-ui&color=F05138&logo=Swift&logoColor=FFFFFF&label=)](https://developer.apple.com/xcode/swiftui/)

[![Compose](https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label=)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html#join-the-community)

[![koin Dependency Injection](https://img.shields.io/static/v1?style=for-the-badge&message=Dependency%20Injection&color=222222&logo=koin&logoColor=3DDC84&label=koin)](https://github.com/InsertKoinIO/koin)

[![Swinject Dependency Injection](https://img.shields.io/static/v1?style=for-the-badge&message=Dependency+Injection&color=ffa841&logo=koin&logoColor=222222&label=Swinject)](https://github.com/Swinject/Swinject)

[![Swinject Dependency Injection](https://img.shields.io/static/v1?style=for-the-badge&message=Zoomable+Swiftui&color=222222&logo=koin&logoColor=222222&label=)](https://github.com/ryohey/Zoomable)

[![Supabase](https://img.shields.io/static/v1?style=for-the-badge&message=Supabase&color=47A248&logo=Supabase&logoColor=FFFFFF&label=)](https://supabase.com/docs/reference/kotlin/installing)

## Usage

- **Create an Account:** Register with your email and set up your profile.
- **Explore the Feed:** Scroll through the home feed to see posts from other users.
- **Interact with Posts:** Like or comment on posts you find interesting.
- **Create Your Own Posts:** Share your thoughts and images with others.
- **Send Messages:** Use the messenger feature to chat with other users in real-time.

## Project Structure

```plaintext
├── shared
│   └── src/commonMain/kotlin/com/omarkader/sociality
├── androidApp        # Android-specific implementation
├── iosApp            # iOS-specific implementation
```

Screenshot
-------------

### Android
<!--suppress CheckImageSize -->
<table>
    <tr>
      <td> <img src="Screenshots/Android/1_.png"  width="300" height="667" alt="1"/> </td>
      <td> <img src="Screenshots/Android/2.png"  width="300" height="667" alt="8"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/3.png"  width="300" height="667" alt="2"/> </td>
      <td> <img src="Screenshots/Android/4.png"  width="300" height="667" alt="3"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/5.png"  width="300" height="667" alt="4"/> </td>
      <td> <img src="Screenshots/Android/6.png"  width="300" height="667" alt="5"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/7.png"  width="300" height="667" alt="6"/> </td>
    </tr>
</table>

### IOS
<table>
    <tr>
      <td> <img src="Screenshots/IOS/1_.png"  width="300" height="667" alt="1"/> </td>
      <td> <img src="Screenshots/IOS/2.png"  width="300" height="667" alt="8"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/3.png"  width="300" height="667" alt="2"/> </td>
      <td> <img src="Screenshots/IOS/4.png"  width="300" height="667" alt="3"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/5.png"  width="300" height="667" alt="4"/> </td>
      <td> <img src="Screenshots/IOS/6.png"  width="300" height="667" alt="5"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/7.png"  width="300" height="667" alt="6"/> </td>
    </tr>
</table>
