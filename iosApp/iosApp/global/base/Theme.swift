import SwiftUI

struct Theme {
    let isDarkMode: Bool
    let isDarkStatusBarText: Bool
    let primary: Color
    let secondary: Color
    let background: Color
    let surface: Color
    let backDark: Color
    let backDarkSec: Color
    let backGreyTrans: Color
    let textColor: Color
    let textForPrimaryColor: Color
    let textGrayColor: Color
    let error: Color
    let textHintColor: Color
    let textHintAlpha: Color
    let backDarkAlpha: Color
    let primaryAlpha: Color
    let backgroundPrimary: Color
    let pri: Color
    let priAlpha: Color

    init(isDarkMode: Bool) {
        self.isDarkMode = isDarkMode
        if (isDarkMode) {
            self.isDarkStatusBarText = false
            self.primary = Color(red: 109 / 255, green: 157 / 255, blue: 241 / 255)
            self.secondary = Color(red: 65 / 255, green: 130 / 255, blue: 237 / 255)
            self.background = Color(red: 31 / 255, green: 31 / 255, blue: 31)
            self.surface =  Color(red: 31 / 255, green: 31 / 255, blue: 31)
            self.backgroundPrimary = Color(red: 31 / 255, green: 31 / 255, blue: 31).margeWithPrimary
            self.backDark = Color(red: 25 / 255, green: 25 / 255, blue: 25 / 255)
            self.backDarkSec = Color(red: 143 / 255, green: 143 / 255, blue: 143 / 255)
            self.backGreyTrans = UIColor(red: 85 / 255, green: 85 / 255, blue: 85 / 255, alpha: 85 / 255).toC
            self.textColor = Color.white
            self.textForPrimaryColor = Color.black
            self.textGrayColor = Color(red: 143 / 255, green: 143 / 255, blue: 143 / 255)
            self.error = Color(red: 1, green: 21 / 255, blue: 21 / 255)
            self.textHintColor = Color(red: 211 / 255, green: 211 / 255, blue: 211 / 255)
            self.pri = Color(red: 102 / 255, green: 158 / 255, blue: 1)
            self.textHintAlpha = UIColor(_colorLiteralRed: 175 / 255, green: 175 / 255, blue: 175 / 255, alpha: 0.5).toC
            self.backDarkAlpha = UIColor(_colorLiteralRed: 31 / 255, green: 31 / 255, blue: 31 / 255, alpha: 0.5).toC
            self.primaryAlpha = UIColor(_colorLiteralRed: 208 / 255, green: 188 / 255, blue: 1, alpha: 0.5).toC
            self.priAlpha = UIColor(red: 109 / 255, green: 157 / 255, blue: 241 / 255, alpha: 0.14).toC
        } else {
            self.isDarkStatusBarText = true
            self.primary = Color(red: 109 / 255, green: 157 / 255, blue: 241 / 255)
            self.secondary = Color(red: 65 / 255, green: 130 / 255, blue: 237 / 255)
            self.background = Color.white
            self.surface =  Color.white
            self.backgroundPrimary = Color.white.margeWithPrimary
            self.backDark = Color(red: 230 / 255, green: 230 / 255, blue: 230 / 255)
            self.backDarkSec = Color(red: 112 / 255, green:112 / 255, blue: 112 / 255)
            self.backGreyTrans = UIColor(red: 170 / 255, green: 170 / 255, blue: 170 / 255, alpha: 85 / 255).toC
            self.textColor = Color.black
            self.textForPrimaryColor = Color.black
            self.textGrayColor = Color(red: 112 / 255, green: 112 / 255, blue: 112 / 255)
            self.error = Color(red: 155, green: 0, blue: 0)
            self.textHintColor = Color(red: 31 / 255, green: 31 / 255, blue: 31)
            self.pri = Color(red: 102 / 255, green: 158 / 255, blue: 1)
            self.textHintAlpha = UIColor(_colorLiteralRed: 80 / 255, green: 80 / 255, blue: 80 / 255, alpha: 0.5).toC
            self.backDarkAlpha = UIColor(_colorLiteralRed: 241 / 255, green: 241 / 255, blue: 241 / 255, alpha: 0.5).toC
            self.primaryAlpha = UIColor(_colorLiteralRed: 102 / 255, green: 80 / 255, blue: 164 / 255, alpha: 0.5).toC
            self.priAlpha = UIColor(red: 1, green: 1, blue: 1, alpha: 0.14).toC
        }
    }
    
    /*func colorScheme(screen: Screen) -> ColorScheme {
        return screen == .SPLASH_SCREEN_ROUTE ? (isDarkMode ? ColorScheme.dark : ColorScheme.light) : (isDarkStatusBarText ? ColorScheme.light : ColorScheme.dark)
    }*/
    
    func textFieldColor(isError: Bool, isEmpty: Bool) -> Color {
        if (isError) {
            return error
        } else {
            return isEmpty ? Color.black.opacity(0.7) : Color.black
        }
    }
}
