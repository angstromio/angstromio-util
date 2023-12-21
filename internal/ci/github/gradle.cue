package github

workflows: gradle: {
	// This workflow uses actions that are not certified by GitHub.
	// They are provided by a third-party and are governed by
	// separate terms of service, privacy policy, and support
	// documentation.
	// This workflow will build a Java project with Gradle and cache/restore any dependencies to improve the workflow execution time
	// For more information see: https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-gradle

	name: "Java CI with Gradle"

	on: {
		push: branches: ["main"]
		pull_request: branches: ["main"]
	}

	permissions: contents: "read"

	jobs: build: {

		"runs-on": "ubuntu-latest"

		steps: [{
			uses: "actions/checkout@v3"
		}, {
			name: "Set up JDK 17"
			uses: "actions/setup-java@v3"
			with: {
				"java-version": "17"
				distribution:   "temurin"
				cache:          "gradle"
			}
		}, {
			name: "Build with Gradle"
			uses: "gradle/gradle-build-action@bd5760595778326ba7f1441bcf7e88b49de61a25" // v2.6.0
			with: {
				arguments: "clean jacocoTestReport --no-daemon"
			}
		}, {
			name: "Upload coverage reports to Codecov with GitHub Action"
			uses: "codecov/codecov-action@v3"
			env: CODECOV_TOKEN: "${{ secrets.CODECOV_TOKEN }}"
		}]
	}
}
