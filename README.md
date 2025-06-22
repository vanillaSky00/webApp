## Frontend
```
src/
└── frontend/
    ├── node_modules/             # Installed dependencies (auto-created)
    ├── public/                   # Static assets served as-is
    │   └── index.html            # HTML entry point
    ├── src/                      # Source code
    │   ├── assets/               # Images, global styles, etc.
    │   ├── components/           # Reusable Vue components
    │   ├── App.vue               # Root component
    │   └── main.js               # App entry point
    ├── .gitignore                # Files to ignore in Git
    ├── babel.config.js           # Babel transpilation config
    ├── jsconfig.json             # JS tooling support (optional)
    ├── package.json              # Project metadata & dependencies
    ├── package-lock.json         # Exact versions of npm dependencies
    └── README.md                 # Project info
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
