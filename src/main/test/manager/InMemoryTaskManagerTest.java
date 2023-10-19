package manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public InMemoryTaskManagerTest() {
        manager = new InMemoryTaskManager();
    }
}