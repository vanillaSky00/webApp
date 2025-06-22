## Frontend
```
src/image_conversion_micro_service/frontend/src
├── App.vue                   # Root component
├── main.ts                   # App entry point
├── shims-vue.d.ts            # Vue type declarations for TypeScript
├── assets/                   # Global styles and static assets
│   └── animation.css
│   └── base.css
│   └── main.css
│   └── css/
│   └── material/                 # default library
│ 
├── components/               # Reusable UI components
│   └── DirectoryUploadBlock_Simplified.vue
│   └── Header.vue
│   └── HeaderNav.vue
│   └── ImageSourceSelector.vue
│   └── MosaicGenerateButton.vue
│   └── MosaicResultDisplay.vue
│   └── PageStepper.vue
│   └── ParallaxContainer.vue
│   └── PredefinedThemeModal.vue
│   └── SplitLayoutSection.vue
│   └── StyleImageUploader.vue
│   └── Typewriter.vue
│ 
├── views/                    # Page-level components
│   ├── HomeView.vue
│   ├── OpenApiViewer.vue
│   └── UseNowView.vue
```

## Backend
```
src/main/java/com/blurnest/imageuploader/
├── config/                  --> System configs
│   └── CORSConfig.java
│   └── DownloadResourceConfig.java
│   └── EndpointDumpConfig.java
│   └── OpenApiConfig.java
├── controller/              --> REST API endpoints
│   └── CustomApiDocsController.java
│   └── ImageController.java
│   └── UiForwardController.java
├── service/                 --> Business logic layer
│   └── ImageProcessingService.java
│   └── StorageService.java
│   └── UserService.java
├── model/                   --> Domain objects and image processing utilities
│   └── engine/
│       └── FileDataRepository.java
│       └── mosaicUtils/
│           └── ColorUtils.java
│           └── ImageConverter.java
│           └── ImageLoader.java
│           └── ImageMatcher.java
│           └── ImageSplitter.java
│           └── MosaicBuilder.java
│           └── ColorUtils.java
│   └── processor/
│       └── ImageProcessor.java
│       └── MosaicProcessor.java
<<<<<<< HEAD
├── repository/              --> Domain objects and image processing utilities
=======
├── repository/              
>>>>>>> 9f5b235e3ad28d9a03775ebbe1f6c99767662787
│   └── FileDataRepository.java
│   └── StorageRepository.java
│ 
└── ImageUploaderApplication.java
<<<<<<< HEAD

src/main/resources/
├── static/                  --> Exposed static files (e.g., processed images)
├── templates/               --> V in MVC (Thymeleaf views)
│   └── uploadForm.html
└── application.properties
=======
>>>>>>> 9f5b235e3ad28d9a03775ebbe1f6c99767662787
```