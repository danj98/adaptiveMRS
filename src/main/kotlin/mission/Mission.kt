package adaptiveMRS.mission

import java.util.UUID

data class Mission (
    val id: UUID,
    val tasks: List<Task>,
    val completedTasks: List<Task>
)