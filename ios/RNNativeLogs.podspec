
Pod::Spec.new do |s|
  s.name         = "RNNativeLogs"
  s.version      = "1.0.0"
  s.summary      = "RNNativeLogs"
  s.description  = <<-DESC
                  RNNativeLogs
                   DESC
  s.homepage     = "https://github.com/V1pi/native-logs#readme"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "viniciusspicanco@gmail.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNNativeLogs.git", :tag => "master" }
  s.source_files  = "**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  
