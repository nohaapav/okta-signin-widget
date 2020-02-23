(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[cljsjs/boot-cljsjs "0.10.5" :scope "test"]
                 [cljsjs/react "16.8.6-0"]
                 [cljsjs/react-dom "16.8.6-0"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "3.7.3")
(def +version+ (str +lib-version+ "-0"))

(task-options!
 pom {:project     'cljsjs/okta-signin-widget
      :version     +version+
      :description "Okta SignIn widget"
      :url         "http://www.okta.com/"
      :scm         {:url "https://github.com/nohaapav/okta-signin-widget"}
      :license     {"Okta Sign-In Widget SDK License" "https://github.com/okta/okta-signin-widget/blob/master/LICENSE"}})

(deftask package []
  (comp
   (run-commands :commands [["npm" "install" "--include-dev"]
                            ["npm" "run" "build:dev"]
                            ["npm" "run" "build:prod"]
                            ["rm" "-rf" "./node_modules"]])
   (sift :move {#".*okta-signin-widget.inc.js"     "cljsjs/okta-signin-widget/development/okta-signin-widget.inc.js"
                #".*okta-signin-widget.min.inc.js" "cljsjs/okta-signin-widget/production/okta-signin-widget.min.inc.js"})
   (sift :include #{#"^cljsjs"})
   (deps-cljs :foreign-libs [{:file           #"okta-signin-widget.inc.js"
                              :file-min       #"okta-signin-widget.min.inc.js"
                              :provides       ["@okta/okta-signin-widget"]
                              :global-exports '{"@okta/okta-signin-widget" OktaSignIn}
                              :requires       ["react" "react-dom"]}]
              :externs [#"okta-signin-widget.ext.js"])
   (pom)
   (jar)
   (validate-checksums)))
