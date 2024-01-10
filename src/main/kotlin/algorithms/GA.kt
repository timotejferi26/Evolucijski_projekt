package algorithms

import problems.TSP
import problems.TSP.Tour
import utility.RandomUtils
import utility.RandomUtils.nextDouble

class GA(
    var popSize: Int, //crossover probability
    var cr: Double, //mutation probability
    var pm: Double
) {
    var population: ArrayList<Tour?>? = null
    var offspring: ArrayList<Tour?>? = null
    fun execute(problem: TSP): Tour? {
        population = ArrayList()
        offspring = ArrayList()

        for (i in 0 until popSize) {
            val newTour = problem.generateTour()
            problem.evaluate(newTour!!)
            population!!.add(newTour)
        }

        var best = population!!.minByOrNull { it!!.distance }

        while (problem.numberOfEvaluations < problem.maxEvaluations) {
            println("Evaluations: " + problem.numberOfEvaluations + " Max evaluations: " + problem.maxEvaluations)

            // elitizem - poišči najboljšega in ga dodaj v offspring in obvezno uporabi clone()
            offspring!!.add(best!!.clone())

            while (offspring!!.size < popSize) {
                val parent1 = tournamentSelection()
                var parent2 = tournamentSelection()

                // preveri, da starša nista enaka
                while (parent1 == parent2) {
                    parent2 = tournamentSelection()
                }

                if (nextDouble() < cr) {
                    val children = pmx(parent1, parent2)
                    offspring!!.add(children!![0])
                    if (offspring!!.size < popSize) offspring!!.add(children[1])
                } else {
                    offspring!!.add(parent1!!.clone())
                    if (offspring!!.size < popSize) offspring!!.add(parent2!!.clone())
                }
            }
            for (off in offspring!!) {
                if (nextDouble() < pm) {
                    swapMutation(off)
                }
            }

            //TODO ovrednoti populacijo in shrani najboljšega (best)
            for (off in offspring!!) {
                problem.evaluate(off!!)
                if (off.distance < best!!.distance) {
                    best = off
                }
            }

            //implementacijo lahko naredimo bolj učinkovito tako, da overdnotimo samo tiste, ki so se spremenili (mutirani in križani potomci)
            population = ArrayList(offspring)
            offspring!!.clear()
        }
        return best
    }

    private fun swapMutation(off: Tour?) {
        // izvedi mutacijo
        val index1 = RandomUtils.nextInt(off!!.dimension)
        val index2 = RandomUtils.nextInt(off.dimension)
        swapCities(off, off.path[index1], off.path[index2])
    }

    private fun pmx(parent1: Tour?, parent2: Tour?): Array<Tour>? {
        if (parent1 == null || parent2 == null) return null

        // Select crossover points
        val crossoverPoint1 = RandomUtils.nextInt(parent1.dimension)
        val crossoverPoint2 = RandomUtils.nextInt(crossoverPoint1, parent1.dimension)

        // Create offspring by cloning parents
        val offspring1 = parent1.clone()
        val offspring2 = parent2.clone()

        // Map the selected subsequence between parent1 and parent2
        for (i in crossoverPoint1 until crossoverPoint2) {
            val city1 = parent1.path[i]
            val city2 = parent2.path[i]

            // Swap the cities in offspring
            swapCities(offspring1, city1, city2)
            swapCities(offspring2, city2, city1)
        }

        return arrayOf(offspring1, offspring2)
    }

    private fun swapCities(tour: Tour, city1: TSP.City?, city2: TSP.City?) {
        val city1Index = tour.path.indexOf(city1)
        val city2Index = tour.path.indexOf(city2)

        tour.setCity(city1Index, city2)
        tour.setCity(city2Index, city1)
    }


    private fun tournamentSelection(): Tour? {
        // naključno izberi dva RAZLIČNA posameznika in vrni boljšega
        // Tournament size - can be a parameter of your GA class
        val tournamentSize = 2

        // Randomly select tournamentSize individuals from the population
        val selected = mutableListOf<Tour>()
        for (i in 0 until tournamentSize) {
            val randomIndex = RandomUtils.nextInt(population!!.size)
            selected.add(population!![randomIndex]!!)
        }

        // Choose the best individual among the selected
        return selected.maxByOrNull { getFitness(it) }
    }

    private fun getFitness(tour: Tour): Double {
        // Implement your fitness calculation here
        return tour.distance // Example: lower distance means higher fitness
    }
}
