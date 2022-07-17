package manager;

import tasks.Task;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> tail;
    private Node<Task> head;
    private final Map<Integer, Node<Task>> history;
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
            history.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public void removeHistory(int id) {
        Node<Task> nodeRemove = history.get(id);
        if (nodeRemove != null) {
            removeNode(nodeRemove);
            history.remove(id);
        }
    }


    public Node<Task> linkLast(Task element) {
        Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node(element, null, oldTail);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else {
            oldTail.setNext(newNode);
        }
        return newNode;
    }

    public void removeNode(Node<Task> taskNode) {
        if (taskNode != null) {
            final Node<Task> prev = taskNode.getPrev();
            final Node<Task> next = taskNode.getNext();

            if (prev == null) {
                head = next;
            } else {
                prev.setNext(next);
                head.setPrev(null);
            }

            if (next == null) {
                tail = prev;
            } else {
                next.setPrev(prev);
                tail.setNext(null);
            }
            taskNode.setTask(null);
        }
    }

    public List<Task> getTasks() {
        historyTask = new ArrayList<>();
        Node<Task> temp = head;
        while (temp != null) {
            historyTask.add(temp.getTask());
            temp = temp.getNext();
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
