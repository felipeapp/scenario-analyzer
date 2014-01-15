package br.ufrn.sigaa.arq.dao;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class TesteDAO {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//DAOFactory.getInstance().getSfSigaa();
		Database.setDirectMode();
		GenericDAOImpl genericDAO = new GenericDAOImpl(Sistema.SIGAA);
		
		
		try{
//			ArrayList<Pessoa> pessoas = (ArrayList<Pessoa>)
//						genericDAO.findAll(Pessoa.class);
//			for(Pessoa p : pessoas)
//				System.out.println(p);
			
			Discente d = new Discente();
			
			if (genericDAO != null)
				d = genericDAO.findByPrimaryKey(26320531,Discente.class);
				System.out.println(d.getMatriculaNome());

		} catch (DAOException e) {
			e.printStackTrace();
		}finally{
			genericDAO.close();
		}

	}

}
