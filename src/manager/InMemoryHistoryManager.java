package manager;

import tasks.Task;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> tail;
    private Node<Task> head;
    private int size;
    private final Map<Integer, Node> history;
    List<Task> historyTask;
    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    @Override
    public void addHistory(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                removeHistory(task.getId());
            }
            linkLast(task);
            history.put(task.getId(), getTail());
        }
    }

    @Override
    public void removeHistory(int id) {
        Node<Task> nodeRemove = history.get(id);
        removeNode(nodeRemove);
        history.remove(id);
    }

    public void linkFirst(Task element) {
        final Node<Task> oldHead = head;
        final Node<Task> newNode = new Node(element, oldHead, null);
        head = newNode;
        if (oldHead == null)
            tail = newNode;
        else {
            oldHead.prev = newNode;
        }
        size++;
    }

    public void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node(element, null, oldTail);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else {
            oldTail.next = newNode;
        }
        size++;
    }

    public void removeNode(Node<Task> taskNode) {
        if (taskNode != null) {
            final Node<Task> prev = taskNode.prev;
            final Node<Task> next = taskNode.next;

            if (prev == null) {
                head = next;
            } else {
                prev.next = taskNode.next;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = taskNode.prev;
            }
            size--;
        }
    }

    public List<Task> getTasks() {
        historyTask = new ArrayList<>();
        Node<Task> temp = head;
        while (temp != null) {
            historyTask.add(temp.task);
            temp = temp.next;
        }
        return historyTask;
    }

    public Node<Task> getTail() {
        return tail;
    }

    public Node<Task> getHead() {
        return head;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

}
