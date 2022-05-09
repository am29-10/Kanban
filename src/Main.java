import manager.Manager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Утренняя пробежка", "Бег на стадионе", TaskStatus.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Сходить в магазин", "Покупка продуктов", TaskStatus.NEW);
        manager.createTask(task2);

        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, 1);
        manager.createSubTask(subTaskForEpic1);
        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Конспект по теореме Пифагора", TaskStatus.NEW, 1);
        manager.createSubTask(subTaskForEpic2);

        Epic epic1 = new Epic("Прочитать книжку", "Чтение в библиотеке", TaskStatus.NEW);
        manager.createEpic(epic1);
        SubTask subTaskForEpic11 = new SubTask("Прочитать Достоевского", "Преступление и наказание", TaskStatus.NEW, 2);
        manager.createSubTask(subTaskForEpic11);

        System.out.println();
        System.out.println(manager.getAllTask());
        System.out.println("-------------------------");
        System.out.println(manager.getAllEpic());
        System.out.println("-------------------------");
        System.out.println(manager.getAllSubTask());
        System.out.println("-------------------------");
        System.out.println("-------------------------");

        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println(task1);
        System.out.println("-------------------------");

        task2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task2);
        System.out.println(task2);
        System.out.println("-------------------------");

        subTaskForEpic1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTaskForEpic1);
        System.out.println(subTaskForEpic1);
        System.out.println("-------------------------");

        subTaskForEpic2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTaskForEpic2);
        System.out.println(subTaskForEpic2);
        System.out.println("-------------------------");

        System.out.println(epic);
        System.out.println("-------------------------");
        System.out.println("-------------------------");

        manager.removeTask(1);
        System.out.println(manager.getAllTask());
        System.out.println("-------------------------");

        manager.removeEpic(2);
        System.out.println(manager.getAllEpic());
        System.out.println("-------------------------");

    }
}
