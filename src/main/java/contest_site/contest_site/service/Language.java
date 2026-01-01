package contest_site.contest_site.service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    CPP("my-cpp-runner:latest","Main.cpp",5){
        public String[] getCompileCommand(String containerId) {
            return new String[]{"docker", "exec", containerId, "g++", "-O2", "/app/Main.cpp", "-o", "/app/Main"};
        }
        public String[] getRunCommand(String containerId) {
            return new String[]{"docker", "exec", "-i", containerId, "/app/Main"};
        }
    },

    JAVA("eclipse-temurin:17-jdk-alpine","Main.java",2){
        public String[] getCompileCommand(String containerId) {
            return new String[]{"docker", "exec", containerId, "javac", "/app/Main.java"};
        }

        public String[] getRunCommand(String containerId) {
            return new String[]{"docker", "exec", "-i", containerId, "java", "-cp", "/app", "Main"};
        }
    },

    PYTHON("python:slim-bookworm","Main.py",3){
        public String[] getCompileCommand(String containerId) {
            return null;
        }

        public String[] getRunCommand(String containerId) {
            return new String[]{"docker", "exec", "-i", containerId, "python3", "/app/Main.py"};
        }
    };

    // to add new language support, just create a similar block as above and download that image before

    private final String dockerImage;
    private final String fileName;
    private final int poolSize;

    public abstract String[] getCompileCommand(String containerId);
    public abstract String[] getRunCommand(String containerId);
}
