/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.SeqAno;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.JustificativaAusenciaCIC;
import br.ufrn.sigaa.pesquisa.dominio.OrganizacaoPaineisCIC;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para consultas a Congressos e Avaliadores de Iniciação Científica
 *
 * @author Ricardo Wendell
 *
 */
public class CongressoIniciacaoCientificaDao extends GenericSigaaDAO {

	/**
	 * Retorna o Congresso de Iniciação Científica atual
	 * @return
	 * @throws DAOException
	 */
	public CongressoIniciacaoCientifica findAtivo() throws DAOException {
        try {
        	return (CongressoIniciacaoCientifica) getSession().createCriteria(CongressoIniciacaoCientifica.class)
        		.add( Expression.eq("ativo", true))
        		.setFetchMode("restricoes", FetchMode.JOIN)
        		.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	public CongressoIniciacaoCientifica findUltimoCongresso() throws DAOException{
		try{
			Criteria c = getSession().createCriteria(CongressoIniciacaoCientifica.class);
			c.addOrder(Order.desc("inicio"));
			c.setMaxResults(1);			
			return  (CongressoIniciacaoCientifica) c.uniqueResult();
		}catch(Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<JustificativaAusenciaCIC> findAusenteByUltimo() throws DAOException{
		CongressoIniciacaoCientifica congresso = findUltimoCongresso();
		Criteria c = getSession().createCriteria(JustificativaAusenciaCIC.class);
		c.add(Expression.eq("CIC.id", congresso.getId()));
		return c.list();
	}
	
	/**
	 * Retorna a organização de apresentação de painéis mais recentemente cadastrada para o congresso informado.
	 * @param congresso
	 * @return
	 * @throws DAOException
	 */
	public OrganizacaoPaineisCIC findOrganizacaoMaisRecente(CongressoIniciacaoCientifica congresso) throws DAOException {
		try {
			return (OrganizacaoPaineisCIC) getSession().createCriteria(OrganizacaoPaineisCIC.class)
			.add( Restrictions.eq("congresso.id", congresso.getId()))
			.addOrder(Order.desc("dataCadastro"))
			.setMaxResults(1)
			.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o próximo número da sequência dos resumos de um determinado congresso
	 * @param congresso
	 * @return
	 * @throws DAOException
	 */
	public int getProximoCodigo(CongressoIniciacaoCientifica congresso) throws DAOException {
		return getNextSeq(SeqAno.SEQUENCIA_RESUMO_CIC, congresso.getAno());
	}

	/**
	 * Verifica se o docente foi cadastrado como avaliador de um determinado congresso para a área de conhecimento informada 
	 * @param congresso
	 * @param docente
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	public boolean isAvaliador(CongressoIniciacaoCientifica congresso, Servidor docente ,DiscenteAdapter discente, AreaConhecimentoCnpq area) throws DAOException {
		Criteria c = getSession().createCriteria(AvaliadorCIC.class);
		c.add(Expression.eq("congresso", congresso));
		
		if(  ValidatorUtil.isNotEmpty(  docente ) ){
			c.add(Expression.eq("docente", docente));
		}	
		
		if(  ValidatorUtil.isNotEmpty(  discente ) ){
			c.add(Expression.eq("discente.id", discente.getId()));
		}
		
		if( ValidatorUtil.isNotEmpty(  area  ) ){
			c.add(Expression.eq("area", area));
		}	
		return !c.list().isEmpty();
	}
	
	/**
	 * Realizar um busca dos avaliadores de resumo, apresentação ou ambos no congresso de iniciação Científica 
	 * selecionado, podendo também filtrar a consulta pela área de conhecimento. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findByCongresso(CongressoIniciacaoCientifica congresso, Boolean avaliadorResumo, Boolean avaliadorApresentacao, AreaConhecimentoCnpq area, boolean buscaDiscente, boolean buscaDocente) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select a.id, a.avaliadorResumo, a.avaliadorApresentacao, a.presenca , pessoaDocente.nome, pessoaDiscente.nome , area.nome ");
		hql.append(" FROM AvaliadorCIC a ");
		hql.append(" LEFT JOIN a.docente docente ");
		hql.append(" LEFT JOIN docente.pessoa pessoaDocente ");
		hql.append(" LEFT JOIN a.area area");
		hql.append(" LEFT JOIN a.discente discente ");
		hql.append(" LEFT JOIN discente.pessoa pessoaDiscente  ");
		hql.append(" where a.congresso.id = " + congresso.getId());
		if(avaliadorResumo != null && avaliadorResumo )
			hql.append(" and a.avaliadorResumo = " + SQLDialect.TRUE);
		if(avaliadorApresentacao != null && avaliadorApresentacao )
			hql.append(" and a.avaliadorApresentacao = " + SQLDialect.TRUE);
		if(area != null)
			hql.append(" and a.area.id = " + area.getId());
		
		if( buscaDiscente ){
			hql.append(" AND discente is not null " );
		}
		
		if( buscaDocente ){
			hql.append(" AND docente is not null " );
		}
		
		hql.append(" order by area.nome, pessoaDiscente.nome, pessoaDocente.nome");
		
		List lista = getSession().createQuery(hql.toString()).list();
		
		List<AvaliadorCIC> result = new ArrayList<AvaliadorCIC>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			AvaliadorCIC avaliador = new AvaliadorCIC();
			avaliador.setId((Integer) colunas[col++]);
			avaliador.setAvaliadorResumo((Boolean) colunas[col] == null ? false : (Boolean) colunas[col++]);
			avaliador.setAvaliadorApresentacao((Boolean) colunas[col] == null ? false : (Boolean) colunas[col++]);
			avaliador.setPresenca((Boolean) colunas[col++]);
			
			Pessoa pessoa = new Pessoa();
			if( colunas[col]!= null ){
				pessoa.setNome((String) colunas[col++]);
				Servidor docente = new Servidor();
				docente.setPessoa(pessoa);
				avaliador.setDocente(docente);
			}else{
				col++;
			}	
			
			if( colunas[col]!= null ){
				pessoa.setNome((String) colunas[col++]);
				DiscenteAdapter discente = new Discente();
				discente.setPessoa(pessoa);
				avaliador.setDiscente(discente);
			}else{
				col++;
			}	
			
			if(colunas[col]!=null ){
				AreaConhecimentoCnpq areaConhecimento = new AreaConhecimentoCnpq();
				areaConhecimento.setNome((String) colunas[col++]);
				avaliador.setArea(areaConhecimento);
			}
						
			result.add(avaliador);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<JustificativaAusenciaCIC> findAusentesByCongressoAtivo(Integer tipo) throws DAOException{
		CongressoIniciacaoCientifica congresso = findAtivo();
		if(congresso == null)
			return null;
		Criteria c = getSession().createCriteria(JustificativaAusenciaCIC.class);
		c.add(Expression.eq("CIC.id", congresso.getId()));
		if(tipo != 0)
			c.add(Expression.eq("tipo",tipo));
		
//		c.addOrder(Order.asc("pessoa.nome"));
		return c.list();
	}
	
	/** Realiza uma busca de usuário sobre as justificativas de ausencia do congresso ativo**/ 
	
	@SuppressWarnings("unchecked")
	public Collection<JustificativaAusenciaCIC> findAusenciaByCongressoAtivo(Usuario usuario )throws DAOException{
		CongressoIniciacaoCientifica congresso = findAtivo();
		Criteria c = getSession().createCriteria(JustificativaAusenciaCIC.class);
		c.add(Expression.eq("CIC.id", congresso.getId()));
		c.add(Expression.eq("cadastrado_por.id", usuario.getId()));
		return c.list();
	}
}