# drdff - Directories Differences Detector

## Introduction

`drdff` is a command-line program that compares two directories and reports which files are missing from each directory. The name is derived from "directories" and "differences", inspired by a drunken night of programming.

## Description

`drdff` compares the filenames of files in two directories and reports which files are missing from each directory. It does not compare file content using `md5` or `byteArray` at this time.

## Motivation

The inspiration for creating `drdff` came from the need to set up a proper backup pipeline for multiple external hard disk drives. Initially, the author tried using the `diff` utility to compare the contents of backup drive directories recursively. However, the process was slow and inefficient, and the author was too lazy to investigate how to properly use the `find` utility. Therefore, the author decided to develop their own implementation using Kotlin, OOP, and TDD.

## Installation

To run the program, you need Java 17 or higher. The executable is a single fat `jar`.

## Usage

To use `drdff`, run the following command in your terminal:

```
java -jar drdff.[version_name].jar [options]
```

The following options are available:

- `-d`: Specify the directory to search for in the second directory.
- `-i`: Specify the directory to search for files in the first directory.
- `-o`: Specify the name of the file to store the results.
- `-x`: Specify which file extensions to search for, separated by commas.
- `-s`: Specify the algorithm to use to compare two sets of `Strings`. Currently, only `Intersect` is supported.
- `-ds`: Specify how to resolve the contents of the given directories. Currently, only `TreeWalk` is supported.
- `-h`: Show help message.

## Examples

Here's an example of how to use `drdff` to find missing files on an external hard disk drive:

```
java -jar drdrff.jar -d /media/me/backup1 -i /media/me/backup2 -o /home/me/backup1MissingFromBackup2.txt
```

This command compares the contents of `/media/me/backup1` and `/media/me/backup2`, and writes the results to `/home/me/backup1MissingFromBackup2.txt`. The output contains useful information like the percentage of files missing and the paths of the missing files.

## Contributions

Contributions to `drdff` are welcome! If you encounter any bugs or have suggestions for improving the program, please open an issue or submit a pull request.

## License

`drdff` is released under the MIT License.

## Third-party licenses

- [Clikt](https://github.com/ajalt/clikt) - Apache-2.0 license
- [mockK](https://github.com/mockk/mockk) - Apache-2.0 license

## Acknowledgements

The author would like to thank the open-source community for their contributions to the Kotlin programming language, as well as the creators of the JUnit testing framework.