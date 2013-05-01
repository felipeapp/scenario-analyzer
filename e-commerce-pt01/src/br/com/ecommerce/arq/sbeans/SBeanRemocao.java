package br.com.ecommerce.arq.sbeans;

import org.springframework.stereotype.Service;

import br.com.ecommerce.arq.dao.GenericDAO;
import br.com.ecommerce.arq.dao.GenericDAOImpl;
import br.com.ecommerce.arq.dominio.PersistDB;
import br.com.ecommerce.arq.erros.DAOException;

@Service
public class SBeanRemocao extends AbstractSBean{

	public void remover(PersistDB db) throws DAOException{
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		
		try{
			dao.delete(db);
		}finally{
			dao.close();
		}
	}
}
