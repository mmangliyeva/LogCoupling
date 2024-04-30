import khttp.get
import org.json.JSONArray

fun main() {
    val repoOwner = "owner"
    val repoName = "repository"
    val token = "your_github_token"

    val commitsUrl = "https://api.github.com/repos/$repoOwner/$repoName/commits"
    val commitsResponse = get(commitsUrl, headers = mapOf("Authorization" to "token $token"))
    val commits = commitsResponse.jsonArray

    // Map to store files/modules and their associated contributors
    val fileContributorsMap = mutableMapOf<String, MutableSet<String>>()

    // Iterate over commits
    for (commit in commits) {
        val commitUrl = commit["url"] as String
        val commitResponse = get(commitUrl, headers = mapOf("Authorization" to "token $token"))
        val files = (commitResponse.jsonObject["files"] as JSONArray)

        val contributor = (commitResponse.jsonObject["author"] as Map<String, Any?>)["login"] as String

        // Update fileContributorsMap for each modified file/module
        for (file in files) {
            val filename = (file as Map<String, Any?>)["filename"] as String

            if (!fileContributorsMap.containsKey(filename)) {
                fileContributorsMap[filename] = mutableSetOf(contributor)
            } else {
                fileContributorsMap[filename]?.add(contributor)
            }
        }
    }

    // Calculate frequency of contribution by pairs of developers to the same files/modules
    val developerPairsFrequency = mutableMapOf<Pair<String, String>, Int>()

    for ((_, contributors) in fileContributorsMap) {
        // Create pairs of contributors for each file/module
        val contributorList = contributors.toList()
        for (i in 0 until contributorList.size - 1) {
            for (j in i + 1 until contributorList.size) {
                val pair = Pair(contributorList[i], contributorList[j])
                developerPairsFrequency[pair] = developerPairsFrequency.getOrDefault(pair, 0) + 1
            }
        }
    }

    // Display frequency of contribution by pairs of developers to the same files/modules
    println("Frequency of contribution by pairs of developers to the same files/modules:")
    developerPairsFrequency.forEach { (pair, frequency) ->
        println("$pair: $frequency times")
    }
}
