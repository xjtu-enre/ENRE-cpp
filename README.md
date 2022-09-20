# ENRE-cpp

> ENRE-cpp is a tool extracting entities and relations for C++ based on [Eclipse/CDT](https://github.com/eclipse-cdt).

## Features
* 🆕 Designed for the latest C++ standard
* 👨‍💻 Friendly and easy to use
* 💪 Input file source code compilation error tolerance, strong robustness
* ⚙️ The tool part supports C language, with strong scalability

## Supported Language

|  Language  | Maximum Version |
|:----------:|:---------------:|
|    C++     |       14        |


## Getting Started

> ENRE-cpp has been tested to be worked with [Java 17]((https://docs.oracle.com/javase/7/docs/webnotes/install/)).

## Usage

Append `-h` or `--help` without any other arguments to see list
of options:
```text
Usage: ENRE-CPP [-hv] [-d=<dirs>]... [-p=<program_environment>]... directory
                projectName
      directory      The directory to be analyzed
      projectName    A short alias name of the anayzed source code project
  -d, --dir=<dirs>   other directory need to analysis.
  -h, --help         display help for command
  -p, --program_environment=<program_environment>
                     the program environment.
  -v, --version      print version information and exit
```

### Examples

Use ENRE to analyze a demo project "**electron**" written in *C++*: 

```cmd
java -jar ENRE-CPP.jar  demo-projects/electron electron -p electron/src 
```

## Documentation
Specifications on which kinds of entities and relations can be
captured and any other details can be found in [documents](docs/README.md)

## References
- [cpp reference](https://en.cppreference.com/w/)
- [C++ docs | Microsoft Docs](https://learn.microsoft.com/en-us/cpp/cpp/?view=msvc-170)
- [IBM XL C/C++ for AIX documentation](https://www.ibm.com/docs/en/xl-c-and-cpp-aix)
- [LLVM](https://github.com/llvm/llvm-project)