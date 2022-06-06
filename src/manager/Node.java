package manager;

public class Node<Task> {
    public Task task;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Task task, Node<Task> next, Node<Task> prev) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}
