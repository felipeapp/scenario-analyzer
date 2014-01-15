package br.ufrn.sigaa.arq.test;

import java.sql.SQLException;
import java.util.HashMap;

import junit.framework.TestCase;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.comum.dominio.Sistema;

public class SigaaTestCase extends TestCase {

	private static HashMap<String, String> massaTestes;

	private static GenericDAO genDao;

	@Override
	protected void setUp() throws Exception {
		Database.setDirectMode();
//		if (massaTestes == null) {
//			loadMassaTestes();
//		}
		if (genDao == null)
			genDao = new GenericDAOImpl(Sistema.SIGAA);
	}

	public GenericDAO getGenericDAO() {
		return genDao;
	}

	// carrega a massa de testes da base de dados
	public static void loadMassaTestes() throws SQLException {

		massaTestes = new HashMap<String, String>();
//		try {
//			Class.forName("org.postgresql.Driver");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		Connection con = DriverManager.getConnection("jdbc:postgresql://testes.info.ufrn.br/massa_testes","comum_user","comum_user");
//		Statement st = con.createStatement();
//		ResultSet rs = st.executeQuery("SELECT * FROM MASSA_TESTE");
//		while (rs.next()) {
//			massaTestes.put(rs.getString("chave"), rs.getString("valor"));
//		}
//
//		rs.close();
//		con.close();

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		genDao.close();
	}

	public int getIntMassa(String id) {
		return new Integer(massaTestes.get(id));
	}

	public String getParamMassa(String id) {
		return massaTestes.get(id);
	}

}
