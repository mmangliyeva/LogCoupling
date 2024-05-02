import khttp.get
import org.json.JSONArray

fun main(args: Array<String>) {

    if (args.size != 3) {
        println("Usage: kotlin MainKt <owner> <repository> <token>")
        return
    }

    val repoOwner = args[0]
    val repoName = args[1]
    val token = args[2] 

    
    val commitsUrl = "https://api.github.com/repos/$repoOwner/$repoName/commits"
    val commitsResponse = get(commitsUrl, headers = mapOf("Authorization" to "token $token"))
    val commits = commitsResponse.jsonArray

    
    var totalCommits = 0
    val uniqueContributors = mutableSetOf<String>()
    val fileModificationCount = mutableMapOf<String, Int>()

    
    for (commit in commits) {
        totalCommits++
        val commitUrl = commit["url"] as String
        val commitResponse = get(commitUrl, headers = mapOf("Authorization" to "token $token"))
        val files = (commitResponse.jsonObject["files"] as JSONArray)
        val contributor = (commitResponse.jsonObject["author"] as Map<String, Any?>)["login"] as String
        uniqueContributors.add(contributor)

        
        for (file in files) {
            val filename = (file as Map<String, Any?>)["filename"] as String
            fileModificationCount[filename] = fileModificationCount.getOrDefault(filename, 0) + 1
        }
    }

    
    val mostModifiedFiles = fileModificationCount.entries.sortedByDescending { it.value }.take(5)

    
    println("Repository Analysis for $repoOwner/$repoName:")
    println("Total Commits: $totalCommits")
    println("Unique Contributors: ${uniqueContributors.size}")
    println("Most Modified Files/Modules:")
    mostModifiedFiles.forEach { (file, count) ->
        println("$file: $count modifications")
    }

    
    val developerPairsFrequency = mutableMapOf<Pair<String, String>, Int>()
    val fileContributorsMap = mutableMapOf<String, MutableSet<String>>()

    
    for (commit in commits) {
        val commitUrl = commit["url"] as String
        val commitResponse = get(commitUrl, headers = mapOf("Authorization" to "token $token"))
        val files = (commitResponse.jsonObject["files"] as JSONArray)
        val contributors = (commitResponse.jsonObject["author"] as Map<String, Any?>)["login"] as String

    
        for (file in files) {
            val filename = (file as Map<String, Any?>)["filename"] as String

            if (!fileContributorsMap.containsKey(filename)) {
                fileContributorsMap[filename] = mutableSetOf(contributors)
            } else {
                fileContributorsMap[filename]?.add(contributors)
            }
        }
    }

    
    for ((_, contributors) in fileContributorsMap) {
        
        val contributorList = contributors.toList()
        for (i in 0 until contributorList.size - 1) {
            for (j in i + 1 until contributorList.size) {
                val pair = Pair(contributorList[i], contributorList[j])
                developerPairsFrequency[pair] = developerPairsFrequency.getOrDefault(pair, 0) + 1
            }
        }
    }
    
    println("\nFrequency of contribution by pairs of developers to the same files/modules:")
    developerPairsFrequency.forEach { (pair, frequency) ->
        println("$pair: $frequency times")
    }
}
