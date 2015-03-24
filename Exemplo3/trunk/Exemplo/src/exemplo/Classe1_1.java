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
package exemplo;

import org.junit.Assert;
import org.junit.Test;

public class Classe1_2 {
	
	@Test
	public void chamaMethodClasse2_2() {
		Classe2_3 c2_3 = new Classe2_3();
		c2_3.chamarMetodoClasse3();
		Assert.assertTrue(true);
	}

}
package exemplo;

public class Classe2_1 {

	public void chamarMetodoClasse3() {
		Classe3_1 c3_1 = new Classe3_1();
		c3_1.chamarMetodoClasse4();
	}

}
package exemplo;

public class Classe4 {

	public void metodoClasse4() {
		System.out.println("Método Classe 4");
	}
	
}
package exemplo;

import org.junit.Assert;
import org.junit.Test;

public class Classe1_1 {
	
	@Test
	public void chamaMethodClasse2_1() {
		Classe2_1 c2_1 = new Classe2_1();
		c2_1.chamarMetodoClasse3();
		Assert.assertTrue(true);
	}
	
}
