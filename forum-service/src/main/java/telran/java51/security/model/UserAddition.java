package telran.java51.security.model;

import java.security.Principal;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * Мы создали этот класс, чтобы оптимизировать наше приложение.
 * 
 * На данный момент во всех фильтрах, где мы обращаемся к базе данных - у нас узкое горлышко. Потому что база данных где-то лежит на облаке, мы запрпшиваем у нее данные, это все долго
 * 
 * Чтобы это изменить, мы улучшаем наш принципал и как бы добавляем к нему роли
 * 
 * То есть этот проект будет у нас работать как принципал, только лучше. Потому что он больше, чем принципал
 */

@AllArgsConstructor
@Getter
public class UserAddition implements Principal{
	
	private String name;
	
	/*
	 * //[1] Делаем тут не енамы, а стринги, так как это более универсальное решение
	 */
	 private Set <String> roles;
	

	 /*
	  * //[1] У Principal есть метод getName(). Имлементация Principal заставляет нас его переопеределить
	  * 
	  * Но когда мы добавляем аннотацию Getter, требование исчезает, потому что поле у нас тоже называется name
	  */

}
