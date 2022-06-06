import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic("Сделать уроки", "Написать конспекты", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTaskForEpic1 = new SubTask("Сделать алгебру", "Конспект по логарифмам", TaskStatus.IN_PROGRESS, 1);
        manager.createSubTask(subTaskForEpic1);
        SubTask subTaskForEpic2 = new SubTask("Сделать геометрию", "Конспект по теореме Пифагора", TaskStatus.NEW, 1);
        manager.createSubTask(subTaskForEpic2);
        SubTask subTaskForEpic3 = new SubTask("Сделать физику", "Конспект по закону Ома", TaskStatus.NEW, 1);
        manager.createSubTask(subTaskForEpic3);

        Epic epic1 = new Epic("Прочитать книжку", "Чтение в библиотеке", TaskStatus.NEW);
        manager.createEpic(epic1);

        System.out.println("Получаем задачи:");
        System.out.println(manager.getEpic(1));
        System.out.println(manager.getEpic(5));
        System.out.println(manager.getSubTask(2));
        System.out.println(manager.getSubTask(3));
        System.out.println(manager.getSubTask(4));

        System.out.println("Получаем имеющиеся задачи (проверка обновления истории):");
        System.out.println(manager.getEpic(1));
        System.out.println(manager.getSubTask(2));

        System.out.println("Вывод на экран списка истории: ");
        System.out.println(manager.getHistory());
        System.out.println(manager.getHistory().size());

        System.out.println("Удаляем задачу (проверка обновления истории):");
        manager.removeSubTask(2);
        System.out.println(manager.getHistory());
        System.out.println(manager.getHistory().size());

        System.out.println("Удаляем эпик в котором есть подзадачи (проверка удаления как самого эпика, так и его " +
                "подзадач из истории):");
        manager.removeEpic(1);
        System.out.println(manager.getHistory());
        System.out.println(manager.getHistory().size());
    }
}
