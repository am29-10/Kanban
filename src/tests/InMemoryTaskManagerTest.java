package tests;

import manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

}