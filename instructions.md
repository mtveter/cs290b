# cs290b

How to change to java 8:

in terminal:
  vi ~/.profile
  
this file should contain the following two lines:
  export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.31.x86_64
  export PATH=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.31.x86_64/bin:$PATH
  
to apply the changes from .profile: 
  source ~/.profile

to check:
  java -version
