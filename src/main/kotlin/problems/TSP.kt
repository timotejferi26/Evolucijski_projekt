package problems

import utility.RandomUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.sqrt

class TSP(path: String, maxEvaluations: Int) {
    internal enum class DistanceType {
        EUCLIDEAN,
        WEIGHTED
    }

    internal enum class EdgeWeightType {
        EXPLICIT,
        EUC_2D
    }

    inner class City {
        var index = 0
        var x = 0.0
        var y = 0.0
    }

    inner class Tour {
        var distance: Double
        var dimension: Int
        var path: Array<City?>

        constructor(tour: Tour) {
            distance = tour.distance
            dimension = tour.dimension
            path = tour.path.clone()
        }

        constructor(dimension: Int) {
            this.dimension = dimension
            path = arrayOfNulls(dimension)
            distance = Double.MAX_VALUE
        }

        fun clone(): Tour {
            return Tour(this)
        }

//        fun getPath(): Array<City?> {
//            return path
//        }
//
//        fun setPath(path: Array<City?>) {
//            this.path = path.clone()
//        }

        fun setCity(index: Int, city: City?) {
            path[index] = city
            distance = Double.MAX_VALUE
        }
    }

    var name: String? = null
    var start: City? = null
    var cities: List<City> = ArrayList()
    var numberOfCities = 0
    var weights: Array<DoubleArray> = emptyArray()
    private var distanceType = DistanceType.EUCLIDEAN
    var numberOfEvaluations: Int
    var maxEvaluations: Int
    private var edgeWeightType: EdgeWeightType? = null

    init {
        loadData(path)
        numberOfEvaluations = 0
        this.maxEvaluations = maxEvaluations
    }

    fun evaluate(tour: Tour) {
        var distance = 0.0
        distance += calculateDistance(start, tour.path[0])
        for (index in 0 until numberOfCities) {
            distance += if (index + 1 < numberOfCities) calculateDistance(
                tour.path[index],
                tour.path[index + 1]
            ) else calculateDistance(tour.path[index], start)
        }
        tour.distance = distance
        numberOfEvaluations++
    }

    private fun calculateDistance(from: City?, to: City?): Double {
        // TODO implement
        return when (distanceType) {
            DistanceType.EUCLIDEAN -> {
                // Implement Euclidean distance calculation
                if (from == null || to == null) return Double.MAX_VALUE
                val deltaX = to.x - from.x
                val deltaY = to.y - from.y
                sqrt(deltaX * deltaX + deltaY * deltaY)
            }
            DistanceType.WEIGHTED -> {
                if (from == null || to == null) return Double.MAX_VALUE
                weights[from.index][to.index]
            }
            else -> Double.MAX_VALUE
        }
    }

    fun generateTour(): Tour? {
        //TODO generate random tour, use RandomUtils

        // Assuming `cities` is the list of all cities in your TSP problem
        // Shuffling the cities list using RandomUtils (or Kotlin's standard shuffle function)
        val shuffledCities = cities.shuffled(RandomUtils.getRand()) // Replace with RandomUtils method if available

        // Create a new Tour with the dimension equal to the number of cities
        val tour = Tour(cities.size)

        // Assign each shuffled city to the tour
        for ((index, city) in shuffledCities.withIndex()) {
            tour.setCity(index, city)
        }

        return tour
    }

    private fun loadData(path: String) {
        val inputStream = TSP::class.java.classLoader.getResourceAsStream(path)
        if (inputStream == null) {
            System.err.println("File $path not found!")
            return
        }

        val lines: MutableList<String> = ArrayList()
        var parsingSection = false

        try {
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                var line = br.readLine()
                while (line != null) {
                    when {
                        line.startsWith("EDGE_WEIGHT_TYPE : EXPLICIT") -> {
                            edgeWeightType = EdgeWeightType.EXPLICIT
                            distanceType = DistanceType.WEIGHTED // set if needed
                        }
                        line.startsWith("EDGE_WEIGHT_TYPE : EUC_2D") -> {
                            edgeWeightType = EdgeWeightType.EUC_2D
                            distanceType = DistanceType.EUCLIDEAN // set if needed
                        }
                        line.startsWith("NODE_COORD_SECTION") || line.startsWith("EDGE_WEIGHT_SECTION") -> {
                            parsingSection = true
                            line = br.readLine()
                            continue
                        }
                        parsingSection && (line.startsWith("EOF") || line.startsWith("DISPLAY_DATA_SECTION")) -> break
                        parsingSection -> lines.add(line)
                    }
                    line = br.readLine()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (edgeWeightType) {
            EdgeWeightType.EUC_2D -> parseEUC2D(lines)
            EdgeWeightType.EXPLICIT -> parseExplicitFullMatrix(lines)
            else -> System.err.println("Unsupported edge weight type")
        }
        numberOfCities = cities.size
        start = cities[0] // Assuming the start city is the first one
    }

    private fun parseEUC2D(lines: List<String>) {
        for (line in lines) {
            val parts = line.trim().split("\\s+".toRegex())
            val city = City()
            city.index = parts[0].toInt() - 1 // Assuming index starts from 1
            city.x = parts[1].toDouble()
            city.y = parts[2].toDouble()
            cities += city
        }
    }

    private fun parseExplicitFullMatrix(lines: List<String>) {
        // Assuming numberOfCities is already set
        numberOfCities = lines.size
        weights = Array(numberOfCities) { DoubleArray(numberOfCities) }

        for (i in 0 until numberOfCities) {
            val city = City()
            city.index = i
            cities += city
        }

        for ((rowIndex, line) in lines.withIndex()) {
            val distances = line.trim().split("\\s+".toRegex()).map { it.toDouble() }
            for ((colIndex, distance) in distances.withIndex()) {
                weights[rowIndex][colIndex] = distance
            }
        }
    }

}
