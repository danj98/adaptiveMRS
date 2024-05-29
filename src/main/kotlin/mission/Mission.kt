package adaptiveMRS.mission


class Mission(
    val id: Int,
    val tasks: List<Task>,
) {
    companion object {
        @Volatile private var INSTANCE: Mission? = null

        fun getInstance(
            id: Int,
            tasks: List<Task>
        ) : Mission {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Mission(id, tasks).also { INSTANCE = it }
            }
        }

        fun deepCopy(mission: Mission): Mission {
            val tasks = mission.tasks.map { Task.deepCopy(it) }
            return Mission(mission.id, tasks)
        }
    }

    fun deepCopy(): Mission {
        return deepCopy(this)
    }
}