(ns leiningen.semver
  (:use [leiningen.core :only (apply-task read-project task-not-found)]
        [clojure.java.io]
        [clojure.java.shell :only (sh)]
        [leiningen.help :only (help-for)]))

(defn load-git-hook []
  (-> (.getContextClassLoader (Thread/currentThread))
      (.getResourceAsStream "pre-commit")
      (java.io.InputStreamReader.)
      (slurp)))

(defn write-file [filename content]
  (spit (file filename) content))

(defn initialize-semver [hook-file]
  (let [template (load-git-hook)]
    (println "Initializing semver pre-commit hook at " hook-file)
    (write-file hook-file template)
    (sh "chmod" "+x" hook-file)
    (println "Finished initializing semver.")))

(defn semver
  ([project]
     (let [root (:root project)
           git-dir (str root "/.git")
           git-pre-commit-file (str git-dir "/hooks/pre-commit")]
       (cond
        (not (.exists (file git-dir)))
        (println "Git directory does not exist, is this a git repository?")
        (.exists (file git-pre-commit-file))
        (println "Git pre-commit hook already exists, not doing anything.")
        :else (initialize-semver git-pre-commit-file)))))