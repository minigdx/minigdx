
lintfix:
	./gradlew KtLintFormat

clean:
	./gradlew clean

test:
	./gradlew test

deploy:
	./gradlew publishToMavenLocal
