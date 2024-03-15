# HW2
 Software Development. RestaurantApp
БПИ224
Лебедев Илья

1. Для работы приложения нужно:
  1) Открыть проект в IntellIJ IDEA
  2) Нажать правой кнопкой мыши на проект!
  3) Выбрать пункт "Open Module Settings"!
  4) Выбрать пункт "Dependencies"!
  5) Нажать на +!
  6) Выбрать "Jars or ..."!
  7) Выбрать rdbc драйвер(называется sqlite-jdbc-3.43.0.0.jar)!
  8) Нажать OK!

Работ с приложением: 
Для начала нужно авторизоваться. Авторизация для админа и пользователей происходит через одну форму. 
Для админа:
login: admin
password: admin

Админ может: 
1. - Добавить позицию в меню
2. - Удалить позицию из меню
3. - Редактировать позицию: все её поля: наименование, цену, время ожидания, кол-во доступных блюд, принадлежность к разделу меню
4. - Посмотреть отзывы на блюда
5. - Посмотреть статистику: Выручку заведения, так же отсортированные по популярности блюда
   Выйти из своего профиля.

Пользователи могут: 
1. Войти или зарегистрироваться
2. Создать новый заказ
3. Просмотреть созданные заказы
4. Добавлять блюда в созданный заказ, до тез пор, пока он не будет готов
5. Отменять заказ до тех пор, пока он не будет готов
6. Оплатить готовый заказ
7. Дать оценку бюду из заказа

Программа: 
1. Эмулирует готовку бюда в многопоточном режиме
2. Заботится о пользователе и разделяет меню на разделы и не выводит в меню недоступных блюд
3. Оповещает о готовности блюд
4. Сохраняет данные в базу данных
