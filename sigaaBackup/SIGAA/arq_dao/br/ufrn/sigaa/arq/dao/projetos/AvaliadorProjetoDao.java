/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/10/2010
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;

public class AvaliadorProjetoDao extends GenericSigaaDAO {

    /**
     * Informa se o usuário informado já está cadastrado como avaliador de projetos.
     *
     * @param idUsuario
     * @return boolean
     * @throws DAOException
     */
    public boolean isAvaliadorCadastrado(int idUsuario) throws DAOException {
    	Criteria c = getCriteria(AvaliadorProjeto.class);
    	c.add(Expression.eq("usuario.id", idUsuario));
    	c.add(Expression.eq("ativo", true));
    	c.setMaxResults(1);
    	return c.uniqueResult() != null;
    }

    /**
     * Retorna lista com todos os Avaliadores de Projetos habilitados na área de conhecimento
     * especificada. Caso não seja informada a área retorna todos os avaliadores ativos.
     * 
     * @param idAreaConhecimento
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
    public List<AvaliadorProjeto> findByAreaConhecimento(Integer  idAreaConhecimento) throws DAOException {
	
    	StringBuffer hql = new StringBuffer(
    			"select distinct av.id, av.dataInicio, av.dataFim, ac1.id, ac1.nome, u.id, u.pessoa.id, u.pessoa.nome, u.email, " +
    			"u.unidade.nome as lotacao " +
    			"from AvaliadorProjeto av " +
    			"left join av.areaConhecimento1 ac1 " +
    			"join av.usuario u " +
    	"where av.ativo = trueValue() ");

    	if (idAreaConhecimento != null) {
    		hql.append("and (ac1.id = :idArea or ac1.grandeArea.id = :idArea)");
    	}

    	hql.append("order by u.pessoa.nome");	    
    	Query query = getSession().createQuery(hql.toString());

    	if (idAreaConhecimento != null) {
    		query.setParameter("idArea", idAreaConhecimento);
    	}

    	List<Object> lista = query.list();
    	ArrayList<AvaliadorProjeto> result = new ArrayList<AvaliadorProjeto>();
    	for (int a = 0; a < lista.size(); a++) {
    		int col = 0;
    		Object[] colunas = (Object[]) lista.get(a);
    		AvaliadorProjeto av = new AvaliadorProjeto();
    		av.setId((Integer) colunas[col++]);
    		av.setDataInicio((Date) colunas[col++]);
    		av.setDataFim((Date) colunas[col++]);
    		AreaConhecimentoCnpq acnpq1 = new AreaConhecimentoCnpq();
    		Integer idArea1 = (Integer) colunas[col++];
    		acnpq1.setId(idArea1 != null ? idArea1 : -1);
    		acnpq1.setNome((String) colunas[col++]);
    		av.setAreaConhecimento1(acnpq1);

    		Usuario user = new Usuario();
    		user.setId((Integer) colunas[col++]);
    		user.setPessoa(new Pessoa((Integer)colunas[col++]));
    		user.getPessoa().setNome((String) colunas[col++]);
    		user.setEmail((String) colunas[col++]);

    		Servidor s = new Servidor();
    		s.setPrimeiroUsuario(user);
    		s.setPessoa(user.getPessoa());		
    		s.setLotacao((String) colunas[col++]);		
    		user.setServidor(s);
    		av.setUsuario(user);

    		result.add(av);
    	}
    	return result;
    }


	/**
	 * Retorna todos os membros ativos da comissão passada. 
	 * 
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public List<AvaliadorProjeto> findUsuariosByComissao(Integer papel) throws DAOException {
	    String hql = new String(
		    "select distinct u.id, u.pessoa.id, u.pessoa.nome, u.email, " +
		    "u.unidade.nome as lotacao " +
		    " from Usuario u where " +
	    " pessoa.id in (select servidor.pessoa.id from MembroComissao where ativo = trueValue() and papel = :papel) order by u.pessoa.nome ");
	    Query query = getSession().createQuery(hql);
	    query.setInteger("papel", papel);


	    @SuppressWarnings("unchecked")
	    List<Object> lista = query.list();
	    ArrayList<AvaliadorProjeto> result = new ArrayList<AvaliadorProjeto>();
	    for (int a = 0; a < lista.size(); a++) {
		int col = 0;
		Object[] colunas = (Object[]) lista.get(a);
		Usuario user = new Usuario();
		user.setId((Integer) colunas[col++]);
		user.setPessoa(new Pessoa((Integer)colunas[col++]));
		user.getPessoa().setNome((String) colunas[col++]);
		user.setEmail((String) colunas[col++]);
		
		Servidor s = new Servidor();
		s.setPrimeiroUsuario(user);
		s.setPessoa(user.getPessoa());
		s.setLotacao((String) colunas[col++]);
		user.setServidor(s);
		AvaliadorProjeto av = new AvaliadorProjeto();
		av.setUsuario(user);
		
		result.add(av);
	    }
	    return result;
	}
    
}
