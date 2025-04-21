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
src/main/java/com/vanillasky/imageuploader/
├── controller/              --> C in MVC
│   └── ImageController.java
│   └── PageController.java
│   └── UserController.java
├── service/                 --> business logic (optional, but good)
│   └── StorageService.java
│   └── UserService.java
├── model/                   --> M in MVC (domain/data objects)
│   └── ImageUtils
├── repository/
│   └── FileDataRepository.java
│   └── ImageDataRepository.java
├── config/                  --> config classes (e.g., file upload size limits)
│   
└── ImageUploaderApplication.java

src/main/resources/
├── static/                  --> where processed images can be served
├── templates/               --> V in MVC (Thymeleaf views)
│   └── uploadForm.html
└── application.properties
```
