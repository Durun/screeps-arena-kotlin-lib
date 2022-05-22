# screeps-arena-kotlin-lib

Playing Screeps: Arena using Kotlin/Wasm

## Requirements

- Gradle

### Platform specific requirements

#### Linux

(TODO)

#### Windows

(TODO)

#### MacOS

- XCode

## How to deploy to Screeps: Arena

Deploy all scripts.

```shell
gradle deploy
```

Or, specify a script.

```shell
gradle userScripts:tutorial-simple-move:deploy
```

Then, `screeps-arena-deploy` directories are generated.

```
userScripts/
├── tutorial-final-test/
│  └── screeps-arena-deploy/
├── tutorial-simple-move/
│  └── screeps-arena-deploy/
└── ...
```

Point the `screeps-arena-deploy` directory as Script folder in game.
![](https://gyazo.com/499bbf1d6ecc002001abc805aa553451.png)
