package github

workflows: release: {
	name: "Release"
	env: {
		ORG_GRADLE_PROJECT_signingKey:      "${{ secrets.GPG_SIGNING_KEY }}"
		ORG_GRADLE_PROJECT_signingPassword: "${{ secrets.GPG_SIGNING_PASSWORD }}"
	}
	defaults: run: shell: "bash"
	on: workflow_dispatch: inputs: next_release_version: {
		description: "Next development version"
		required:    true
		type:        "string"
	}

	permissions: contents: "write"

	jobs: release: {

		"runs-on": "ubuntu-latest"

		if: "${{ inputs.next_release_version }}"

		steps: [{
			uses: "actions/checkout@v2.3.4"
		}, {
			name: "Set up JDK 17"
			uses: "actions/setup-java@v3"
			with: {
				"java-version": "17"
				distribution:   "temurin"
				cache:          "gradle"
			}
		}, {
			name: "Import GPG"
			uses: "crazy-max/ghaction-import-gpg@v6"
			with: {
				gpg_private_key: "${{ secrets.GPG_SIGNING_KEY }}"
				passphrase:      "${{ secrets.GPG_SIGNING_PASSWORD }}"
			}
		}, {
			name: "Set up Git Config"
			run: """
				git config user.name \"GitHub Actions Bot\"
				git config user.email \"<>\"

				"""
		}, {
			name: "Update Release Version"
			run: """
				version=$(sed -r -e \"s/io\\.angstrom\\.version=([^-]*)-SNAPSHOT/\\1/\" gradle.properties)
				echo \"io.angstrom.version=$version\" > gradle.properties
				git commit -am \"[Angstromio Release] - pre tag commit: '$version'\"
				git push origin main

				"""
		}, {
			name: "Create Release Branch"
			run: """
				git checkout -b release

				"""
		}, {
			name: "Publish Release with Gradle"
			uses: "gradle/gradle-build-action@bd5760595778326ba7f1441bcf7e88b49de61a25" // v2.6.0
			with: {
				arguments: "-PSonatypeUsername=${{ secrets.SONATYPE_USERNAME }} -PSonatypePassword=${{ secrets.SONATYPE_PASSWORD }} clean publish --no-daemon"
			}
		}, {
			name: "Create Git Tag"
			run: """
				version=$(sed -r -e \"s/io\\.angstrom\\.version=([^-]*)/\\1/\" gradle.properties)
				git tag \"v$version\" -a -m \"Release v$version\"
				git push origin --tags

				"""
		}, {
			name: "Generate API ACCESS Token"
			id:   "generate_token"
			uses: "actions/create-github-app-token@9d97a4282b2c51a2f4f0465b9326399f53c890d4" // v1.5.0
			with: {
				app_id:      "${{ secrets.API_ACCESS_APP_ID }}"
				private_key: "${{ secrets.API_ACCESS_APP_KEY }}"
			}
		}, {
			name: "Create GitHub Release"
			env: GITHUB_TOKEN: "${{ steps.generate_token.outputs.token }}"
			run: """
				version=$(sed -r -e \"s/io\\.angstrom\\.version=([^-]*)/\\1/\" gradle.properties)
				gh release create \"v$version\" --notes-from-tag --title \"Release v$version\"

				"""
		}, {
			name: "Update to Next Release Version"
			run: """
				git checkout main
				version=\"${{ inputs.next_release_version }}-SNAPSHOT\"
				echo \"io.angstrom.version=$version\" > gradle.properties
				git commit -am \"[Angstromio Release] - new version commit: '$version'\"
				git push origin main
				"""
		}]
	}
}
