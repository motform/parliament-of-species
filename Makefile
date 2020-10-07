server:
	@clojure -A:server

cljs:
	@clojure -A:cljs watch app

.PHONY: server
