@echo off

cd ./script

set fname=LocalGit

javac %fname%.java

jar cmf ../manifest.txt ../%fname%.jar ./*.class

pause