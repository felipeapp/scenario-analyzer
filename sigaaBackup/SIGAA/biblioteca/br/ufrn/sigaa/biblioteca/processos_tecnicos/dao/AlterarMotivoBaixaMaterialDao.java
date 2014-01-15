package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * <p>Dao espec�fico para as consultas de altera��o do motivo da baixa. </p>
 * 
 * @author jadson
 *
 */
public class AlterarMotivoBaixaMaterialDao extends GenericSigaaDAO {

	/**
	 *   <p>Retorna o �ltimo usu�rio que alterou o material.</p>
	 * 
	 * @param idMaterial o id do material.
	 * @throws DAOException
	 */
	public Usuario findUsuarioAlterouUltimaVezMaterial(int idMaterial) throws DAOException {
		
		String hql = " SELECT m.registroUltimaAtualizacao.usuario.id "
			+" FROM MaterialInformacional m "
			+" WHERE m.id = :idMaterial ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		Integer idUsuario = (Integer) q.uniqueResult();
		
		if(idUsuario != null)
			return new Usuario(idUsuario);
		else
			return null;
	}

	
	/**
	 * Metodo para atualizar o motivo da baixa dos materiais via sql para n�o gerar altera��o na data de atualiza��o que est� com a nota��o @AtualizadaoEm 
	 * 
	 * A data da baixa e as demais informa��es devem permancer as mesmas!
	 * 
	 * @param mat
	 * @throws DAOException
	 */
	public void atualizaMotivoBaixaMaterial(MaterialInformacional mat) throws DAOException{
		
		Query q = getSession().createSQLQuery("UPDATE biblioteca.material_informacional SET motivo_baixa = :motivoBaixa WHERE id_material_informacional = :idMaterial ");
		q.setInteger("idMaterial", mat.getId());
		q.setString("motivoBaixa", mat.getMotivoBaixa());
		if (q.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao atualizar o motimo da baixa dos materiais.");
	}
	
	
	
}
