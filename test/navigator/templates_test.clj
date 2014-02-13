(ns navigator.templates-test
  (:require [net.cgrand.enlive-html :as en]
            [navigator.templates :refer :all]
            [clojure.test :refer :all]))

;; TODO: en/emit* and beautify the HTML in each test for human readability here?
(deftest onboarding-templates-test
  (testing "onboarding"
    (testing "snippets"
      (testing "common-header"
        (is (= (common-header 1 "a" "b")
               '({:tag :nav, :attrs {:id "top-bar"}, :content ["\n        " {:tag :header, :attrs {:id "logo"}, :content ["\n          " {:tag :h1, :attrs nil, :content ["\n            Logo\n          "]} "\n        "]} "\n        " {:tag :a, :attrs {:id "side-nav-control", :href "#", :data-active "false"}, :content ["Menu"]} {:tag :a, :attrs {:id "search-control", :href "#", :data-active "false"}, :content ["Search"]} "\n        " {:tag :ul, :attrs nil, :content ["\n          " {:tag :li, :attrs {:class "alerts"}, :content ["\n            " {:tag :a, :attrs {:href "/alerts"}, :content ["Alerts " {:tag :span, :attrs {:class "alert-count"}, :content ["1"]}]} "\n          "]} "\n          " {:tag :li, :attrs {:class "profile"}, :content ["\n            " {:tag :img, :attrs {:src "", :alt ""}, :content []} {:tag :a, :attrs {:href "a"}, :content ["b"]} "\n          "]} "\n          " {:tag :li, :attrs {:class "logout"}, :content ["\n            " {:tag :a, :attrs {:href "/logout"}, :content ["Logout"]} "\n          "]} "\n        "]} "\n      "]}))))

      (testing "onboarding-intro"
        (is (= (onboarding-intro "a" "b")
               '({:tag :div, :attrs {:class "intro"}, :content ["\n        " {:tag :h2, :attrs nil, :content ["a"]} "\n        " {:tag :p, :attrs nil, :content ("b")} "\n      "]})
               )))

      (testing "onboarding-step"
        (is (= (onboarding-step 1 "desc")
               '({:tag :li, :attrs {:class "step-1"}, :content ["\n          " {:tag :strong, :attrs nil, :content ["Step 1"]} " " {:tag :span, :attrs nil, :content ("desc")} "\n        "]})))))

    (testing "templates"
      (testing "onboarding"
        (is (= (onboarding "header" "greeting" "greeting message" ["step1" "step2" "step3"])
               "<!DOCTYPE html>\n<!--[if lt IE 7]>\n<html class=\"no-js lt-ie9 lt-ie8 lt-ie7\" lang=\"en\"></html>\n<![endif]--><!--[if IE 7]>\n<html class=\"no-js lt-ie9 lt-ie8\" lang=\"en\"></html>\n<![endif]--><!--[if IE 8]>\n<html class=\"no-js lt-ie9\" lang=\"en\"></html>\n<![endif]--><!--[if gt IE 8]>\n<html class=\"no-js\" lang=\"en\"></html>\n<![endif]--><html><body id=\"onboarding\" data-search-active=\"true\">\n    <main role=\"main\" id=\"content\"><div class=\"intro\">\n        <h2>greeting</h2>\n        <p>greeting message</p>\n      </div>\n      <ol class=\"onboarding-steps\"><li class=\"step-1\">\n          <strong>Step 1</strong> <span>step1</span>\n        </li><li class=\"step-1\">\n          <strong>Step 2</strong> <span>step2</span>\n        </li><li class=\"step-1\">\n          <strong>Step 3</strong> <span>step3</span>\n        </li></ol>\n      \n    </main>\n    <form id=\"search\" data-active=\"true\" action=\"/search-results-alt/\">\n      <h2>\n        Search for classes &amp; competencies\n      </h2>\n      <input type=\"search\" placeholder=\"Search...\" /><button type=\"submit\" class=\"button-large\">Search</button>\n    </form>\n    \n    <footer role=\"contentinfo\"></footer>\n    <script type=\"text/javascript\" src=\"assets/javascripts/libs/jquery.js\"></script>\n    <script type=\"text/javascript\" src=\"assets/javascripts/global.js\"></script>\n  </body>\n</html>"))

        ;; admin and rendered admin2 should be identical.
        (is (= (admin "header" "greeting" "greeting message" "steps")
               "<!DOCTYPE html>\n<!--[if lt IE 7]>\n<html class=\"no-js lt-ie9 lt-ie8 lt-ie7\" lang=\"en\"></html>\n<![endif]--><!--[if IE 7]>\n<html class=\"no-js lt-ie9 lt-ie8\" lang=\"en\"></html>\n<![endif]--><!--[if IE 8]>\n<html class=\"no-js lt-ie9\" lang=\"en\"></html>\n<![endif]--><!--[if gt IE 8]>\n<html class=\"no-js\" lang=\"en\"></html>\n<![endif]--><html><body id=\"onboarding\" data-search-active=\"false\">\n    <main role=\"main\" id=\"content\"><h1>Navigator Administration</h1>\n<div class=\"config-onboarding\">\n  <h2>Configure Page: Onboarding</h2>\n  <form method=\"post\" id=\"config-onboarding\" action=\"/config/page/onboarding\">\n    <h3>\n      Greeting text:\n    </h3>\n    <input value=\"greeting\" type=\"text\" placeholder=\"Welcome\" name=\"greeting\" id=\"greeting\" /><br /><br />\n    <h3>\n      Greeting subtext:\n    </h3>\n    <input value=\"greeting message\" type=\"text\" placeholder=\"Come learn with us.\" name=\"greeting-msg\" id=\"greeting-msg\" /><br /><br />\n    <h3>\n      Onboarding steps:\n    </h3>\n    <textarea style=\"width:50%; height:30em;\" placeholder=\"Enter onboarding steps here. Each paragraph counts as a separate step; do not number them.\" name=\"steps\" id=\"steps\">steps</textarea><br /><br /><button type=\"submit\">Submit</button>\n  </form>\n</div>\n      \n    </main>\n    <form id=\"search\" data-active=\"false\" action=\"/search-results-alt/\">\n      <h2>\n        Search for classes &amp; competencies\n      </h2>\n      <input type=\"search\" placeholder=\"Search...\" /><button type=\"submit\" class=\"button-large\">Search</button>\n    </form>\n    \n    <footer role=\"contentinfo\"></footer>\n    <script type=\"text/javascript\" src=\"assets/javascripts/libs/jquery.js\"></script>\n    <script type=\"text/javascript\" src=\"assets/javascripts/global.js\"></script>\n  </body>\n</html>"))))))

