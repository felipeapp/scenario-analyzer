package exemplo2;

import org.junit.Test;

public class Classe1 {

	
	@Test
	public void chamaClasse3() {
		Classe2 c2 = new Classe2();
		c2.metodo();
		
		for (int i=0; i < 3; ++i){
			for (int j=0; j < 3; ++j){
				System.out.println(i + j);
			}
		}
	}
}
