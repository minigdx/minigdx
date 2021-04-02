package com.github.dwursteisen.minigdx.ecs.components.position

sealed class SimulationResult(val result: Any?) {

    abstract fun execute(simulation: InternalSimulation)

    class Commit(result: Any?) : SimulationResult(result) {

        override fun execute(simulation: InternalSimulation) = Unit
    }

    class Rollback(result: Any?) : SimulationResult(result) {

        override fun execute(simulation: InternalSimulation) = simulation.rollbackMe()
    }
}
