APP=app
SERVER=amr.server.core
JAR=target/amr.jar

jar: clean cljs-compile uberjar

server:
	@clojure -A:server

cljs-watch:
	@clojure -A:cljs watch app

cljs-compile:
	@npm install
	@clojure -A:cljs release app

uberjar:
	@clojure -Spom
	@clojure -M:depstar -m hf.depstar.uberjar $(JAR) -C -m $(SERVER)
	@rm pom.xml

runjar:
	@java -jar $(JAR)

clean:
	@rm -rf target/ -q
	@rm -rf resources/public/js -q

.PHONY: server
