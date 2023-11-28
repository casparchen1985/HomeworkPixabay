#  About Project
- Full Kotlin programming
- Apply
  - MVVM structure
  - Sensitive information isolated
- Used Components
  - Android
    - LiveData
    - Coroutine
    - Navigation
    - Safe Args
  - Third-party
    - Retrofit
    - OkHttp
    - Hilt
    - Realm(Kotlin)
    - Glide
    - Firebase
    - Mockk 
- Unit Test
  - Repository
    - ImagesRepositoryTest
- Additional User Story
  - Search image by type (All, Photo, Illustration, Vector)
  - Sort history by time descending
  - Change staggered grid layout setting (Orientation/Span Count) by Firebase remote config
  - Save searching result layout (Grid/List) after changing manually, and app will ignore the setting from remote config
  - Show high resolution image in DialogFragment after clicking the search result

# Class Diagram
![Class Diagram](https://drive.google.com/uc?id=19uloJ-CmlN3bKvppNPwqKDa4jHNV4Pqf)

# Before Building
- Add "key.properties" file at the root of project
- copy the key config and paste it into property file
  ```kotlin
  PIXABAY_KEY={YOUR_PIXABAY_KEY_WITHOUT_QUOTATION_MARK}
  ```

# TODO List
- Increase unit test coverage
- Handle more AutoCompleteTextView click scenario
- UI Test
