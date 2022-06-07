package manager;

import tasks.Task;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> tail;
    private Node<Task> head;
    private int size;
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
            oldHead.setPrev(newNode);
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
            oldTail.setNext(newNode);
        }
        size++;
    }

    public void removeNode(Node<Task> taskNode) {
        if (taskNode != null) {
            final Node<Task> prev = taskNode.getPrev();
            final Node<Task> next = taskNode.getNext();

            if (prev == null) {
                head = next;
            } else {
                prev.setNext(taskNode.getNext());
                head.setPrev(null);
            }

            if (next == null) {
                tail = prev;
            } else {
                next.setPrev(taskNode.getPrev());
                tail.setNext(null);
            }
            size--;
        }
    }

    public List<Task> getTasks() {
        historyTask = new ArrayList<>(size);
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
