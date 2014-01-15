/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.InteresseOferta;
import br.ufrn.sigaa.estagio.dominio.OfertaEstagio;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável por gerenciar o acesso a dados de Interesse em Ofertas de Estágio
 * 
 * @author Arlindo Rodrigues
 */
public class InteresseOfertaDao extends GenericSigaaDAO  {
	
	/**
	 * Retorna o Interesse Cujo o discente informado foi cadastrado para a oferta informada.
	 * @param discente
	 * @param oferta
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public InteresseOferta findInteresseByDiscenteOferta(DiscenteAdapter discente, OfertaEstagio oferta, boolean ativos) throws HibernateException, DAOException {
		String hql = "select i from InteresseOferta i " +
				" inner join i.oferta oferta "+
				" inner join i.discente d "+
				" where oferta.id = "+oferta.getId()+
				"  and d.id = "+discente.getId()+
				(ativos ? "  and i.ativo = trueValue()" : "");		
		return (InteresseOferta) getSession().createQuery(hql).setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna todos os interesses ativos da oferta informada
	 * @param oferta
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<InteresseOferta> findInteresses(OfertaEstagio oferta, Curso curso, Pessoa responsavel, boolean apenasAbertos) throws HibernateException, DAOException {
		String projecao = "i.id, d.id, d.matricula, p.nome, o.id, c.id, c.nome, e.id, i.selecionado, i.descricaoPerfil, i.idArquivoCurriculo ";
		StringBuilder hql = new StringBuilder(); 
			
		hql.append("select "+projecao+" from InteresseOferta i " +
				" inner join i.discente d "+
				" inner join i.discente.pessoa p "+
				" inner join i.discente.curso c "+
				" inner join i.oferta o " +
				" inner join i.oferta.concedente " +
				" left join i.estagiario e " +
				" where i.ativo = trueValue() ");
		
		if (!ValidatorUtil.isEmpty(oferta))
			hql.append(" and i.oferta.id = "+oferta.getId());
		
		if (!ValidatorUtil.isEmpty(curso))
			hql.append(" and c.id = "+curso.getId());
		
		if (!ValidatorUtil.isEmpty(responsavel))
			hql.append(" and i.oferta.concedente in " +
					" (select concedente from ConcedenteEstagioPessoa " +
					 "  where pessoa.id = " +responsavel.getId()+")");			
			
		if (apenasAbertos)
			hql.append(" and i.selecionado = falseValue() ");
		
		hql.append(" order by i.discente.curso.nome, i.discente.pessoa.nome ");		
		
		Query q = getSession().createQuery(hql.toString());
		Collection<InteresseOferta> lista = new ArrayList<InteresseOferta>();
        @SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        if (res != null ) {
        	for (Object[] reg : res) {
        		InteresseOferta i = new InteresseOferta();
        		i.setId((Integer) reg[0]);
        		
        		i.setDiscente(new Discente());
        		i.getDiscente().getDiscente().setId((Integer) reg[1]);
        		i.getDiscente().getDiscente().setMatricula((Long) reg[2]);
        		i.getDiscente().getDiscente().setPessoa(new Pessoa());
        		i.getDiscente().getDiscente().getPessoa().setNome((String) reg[3]);
        		
        		i.setOferta(new OfertaEstagio());
        		i.getOferta().setId((Integer) reg[4]);
        		
        		i.getDiscente().getDiscente().setCurso(new Curso());
        		i.getDiscente().getDiscente().getCurso().setId((Integer) reg[5]);
        		i.getDiscente().getDiscente().getCurso().setNome((String) reg[6]);
        		
        		i.setEstagiario(new Estagiario());
        		if (reg[7] != null)
        			i.getEstagiario().setId((Integer) reg[7]);
        		
        		if (reg[8] != null)
        			i.setSelecionado((Boolean) reg[8]);
        		
        		i.setDescricaoPerfil((String) reg[9]);
        		i.setIdArquivoCurriculo((Integer) reg[10]);
        		
        		lista.add(i);
        	}
        }
		return lista;
	}	
	
}
