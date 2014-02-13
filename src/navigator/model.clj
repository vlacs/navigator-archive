(ns ^{:doc "A library for keeping and updating browser state"}
  navigator.model)

(def browser-model (atom {}))

(defn update-in!
  "Pass a fn to update a path within 'browser-model.
   Use this whenever you need to update the value of a path within 'browser-model.
   Example: (update-in! [:ui :user2comp 1234 :current_score] (fn [old-val] (inc old-val)))
   Example: (update-in! [:ui :user2comp 1234] update-iscomplete requirements-spec)"
  [path fn & args]
  (apply swap! browser-model update-in path fn args))

(defn get-in!
  "Retrieve the value of a path within 'browser-model.
   DO NOT retrieve the value, do some processing, and then assoc-in! a new value.
   Instead, see update-in! to avoid overwriting changes made between your calls to get-in! a subsequent assoc-in!.
   Example: (get-in! [:ui :comps 73 :name])"
  [path]
  (get-in @browser-model path))

(defn assoc-in!
  "Set the value for a path within 'browser-model.
   DO NOT use assoc-in! to overwrite an existing value within
   'browser-model, unless overwriting is explicitly what you intend.
   Instead, see update-in!.
   Example: (assoc-in! [:ui :comps 73] {:id 73 :name \"Some Competency\" :version \"v3\"})"
  [path val]
  (swap! browser-model assoc-in path val))

(defn- dissoc-in
  "ClojureScript doesn't have dissoc-in, so this is borrowed from
  Clojure's core.incubator.

   Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn dissoc-in!
  "Remove the value of a path within 'browser-model.
   Example: (dissoc-in! [:ui :comps 73])"
  [path]
  (swap! browser-model dissoc-in path))

