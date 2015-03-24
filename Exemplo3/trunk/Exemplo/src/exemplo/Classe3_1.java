package exemplo;

public class Classe2_3 {

	public void chamarMetodoClasse3() {
		Classe3_2 c3_2 = new Classe3_2();
		c3_2.chamarMetodoClasse4();
	}
	
}
package exemplo;

public class Classe3_2 {

	public void chamarMetodoClasse4() {
		Classe4 c4 = new Classe4();
		c4.metodoClasse4();
	}

}
package exemplo;

public class Classe2_2 {

	public void chamarMetodoClasse3() {
		Classe3_2 c3_2 = new Classe3_2();
		c3_2.chamarMetodoClasse4();
	}

}
package exemplo;

public class Classe3_1 {

	public void chamarMetodoClasse4() {
		Classe4 c4 = new Classe4();
		c4.metodoClasse4();
	}

}
