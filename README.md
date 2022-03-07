# ENRE-cpp

## Run it from command line

You could run ENRE-CPP in the following ways: ENRE-CPP.sh on Linux/Mac, ENRE-CPP.bat on Microsoft Windows, or java -jar ENRE-CPP.jar.



# Usage
###  1)Prepare the executable jar
The released jar of ENRE is named as **ENRE-CPP.jar**.
###  2) Set up Java environment 
To execute ENRE-CPP.jar, you should set up JAVA envionment. Please referer to [Set up JAVA environment](https://docs.oracle.com/javase/7/docs/webnotes/install/). 
### 3) cmd usage
Now, everthing is already prepared well. Let's use ENRE to analyze source code. 
The usage command is:
```sh
java -jar <executable> <lang> <dir> <project-name>
```

```sh
java -jar <executable> <lang> <src> <project-name> [-p=<program_environment>[,<program_environment>...]]...  [-d=<directorys>[,<directorys>...]]... 

      <executable>           The executable jar package of ENRE
      <lang>                 The language of project files: cpp
      <src>                  The directory to be analyzed
      <project-name>         A short alias name of the anayzed source

  -p, --program_environment=<program_environment>[,<program_environment>...]
                             The path of program_environment
  -d, --dirs=<dirs>
                             The project dirs
  -h, --help                 Display this help and exit

```

#### Example:
Use ENRE to analyze a demo project "**electron**" written in *C++*: 
```sh
#in linux platform 
$java -jar ENRE-CPP.jar  cpp  demo-projects/electron  electron  -p electron/src
```
```sh
#in windows platform
$java -jar ENRE-CPP.jar  cpp  demo-projects\electron  electron -p electron\src 
```



# The output Format

```json
[
  {
    "variables": [
      {
        "qualifiedName": "/Users/dzf/Desktop/code/idea/test-cpp/test.cpp",
        "id": 0,
        "entityType": "File"
      },
      {
        "qualifiedName": "/Users/dzf/Desktop/code/idea/test-cpp/test.cpp.main",
        "id": 1,
        "entityType": "Function"
      }
    ],
    "relations": [
      {
        "type": "Return",
        "src": 1,
        "dest": 2
      }
    ]
  }
]
```

# Our Tool Support

### Entity
#### File
#### Class, Struct, Union
#### Enum, Enumerator
#### Namespace
- unnamed namespace definition (doing)
    
    We extract the name of this type of namespace as [unnamed namespace]
- inline namespace definition (doing)

#### Macro
#### Function
#### Object
- lambda object: (doing)

    We recognize the 'lamdba_object' in the following expression as an object of lambda type.
    ```cpp
    auto lambda_object = [](int para){expression;};
    ```


### Dependency
#### Include
#### Use, Set
#### Extend
#### Override
#### Parameter, Return
