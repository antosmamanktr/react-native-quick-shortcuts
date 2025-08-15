Pod::Spec.new do |s|
  s.name         = "react-native-quick-shortcuts"
  s.version      = "1.0.0"
  s.summary      = "React Native package for iOS & Android Quick Shortcuts"
  s.description  = <<-DESC
                   Quick shortcuts API for React Native apps.
                   Supports dynamic shortcuts and event listeners.
                   DESC
  s.homepage     = "https://github.com/antosmamanktr/react-native-quick-shortcuts"
  s.license      = { :type => "MIT" }
  s.author       = { "Your Name" => "you@example.com" }
  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/antosmamanktr/react-native-quick-shortcuts.git", :tag => s.version.to_s }
  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React-Core"
end
