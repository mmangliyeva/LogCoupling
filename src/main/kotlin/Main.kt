package org.example


import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.set

data class Developer(val id: String, val name: String)
data class FileContributions(val file: String, val developers: List<Developer>)

val fileContributionsMap: MutableMap<String, MutableList<Developer>> = mutableMapOf()



fun main(args: Array<String>) {

    if (args.size != 3) {
        println("Usage: kotlin MainKt <owner> <repository> <token>")
        return
    }

    val repoOwner = args[0]
    val repoName = args[1]
    val token = args[2]

    val filesList = mutableListOf<String>()
    val client = OkHttpClient()
    // Fetch commit data
    val commitsUrl = "https://api.github.com/repos/$repoOwner/$repoName/commits"
    // Make a GET request to the GitHub API
    val request = Request.Builder()
        .url(commitsUrl)
        .apply {
            // Add authorization header if token is provided
            if (token.isNotEmpty()) {
                addHeader("Authorization", "token $token")
            }
        }
        .build()

    val response = client.newCall(request).execute()
    val commitsJson = response.body?.string() ?: ""
    val commits = JSONArray(commitsJson)
    print(commits.length())
    for (i in 0 until commits.length()) {
        val commit = commits.getJSONObject(i)
        val commitUrl = commit.getString("url")
        println(commitUrl)
        val commitRequest = Request.Builder()
            .url(commitUrl)
            .header("Authorization", "token $token")
            .build()

        val commitResponse = client.newCall(commitRequest).execute()
        val commitJson = commitResponse.body?.string() ?: continue
        val comm=JSONObject(commitJson)
        val files = comm.getJSONArray("files")
        val committerName = comm.getJSONObject("commit").getJSONObject("author").getString("name")
        val committerId = comm.getJSONObject("commit").getJSONObject("author").getString("email")
        val developer = Developer(committerId, committerName)
        println(developer)

        for (j in 0 until files.length()) {
            val filename = files.getJSONObject(j).getString("filename")
            filesList.add(filename)
            val contributions = fileContributionsMap.getOrDefault(filename, mutableListOf())
            contributions.add(developer)
            fileContributionsMap[filename] = contributions
        }
    }

    val developerPairsCount = calculateLogicalCoupling(filesList)
    val topPairs = developerPairsCount.entries.sortedByDescending { it.value }.take(10)


    topPairs.forEachIndexed { index, pair ->
        val developer1 = pair.key.first
        val developer2 = pair.key.second
        val couplingCount = pair.value
        println("${index + 1}. Developers ${developer1.name} and ${developer2.name} have a coupling count of $couplingCount")
    }


}
fun calculateLogicalCoupling(files: List<String>): Map<Pair<Developer, Developer>, Int> {
    val developerPairsCount = mutableMapOf<Pair<Developer, Developer>, Int>()

    for (file in files) {
        val developers = fileContributionsMap[file] ?: continue

        for (i in 0 until developers.size - 1) {
            for (j in i + 1 until developers.size) {
                val developer1 = developers[i]
                val developer2 = developers[j]

                // Exclude self-matching pairs
                if (developer1 != developer2) {
                    val pair = Pair(developer1, developer2)
                    developerPairsCount[pair] = developerPairsCount.getOrDefault(pair, 0) + 1
                }
            }
        }
    }

    return developerPairsCount
}

