/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/09/2007
 *
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** Classe responsável por consultas específicas à {@link RegistroAtividade Registro de Atividades}.
 *
 * @author Victor Hugo
 *
 */
public class RegistroAtividadeDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as atividades orientada pelo docente informado, onde o componente curricular esta com a situação em espera ou matriculado
	 * @param idOrientador
	 * @param comNota TRUE para caso seja apenas as atividades com nota, FALSE para atividades sem nota e NULL para todas
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<RegistroAtividade> findNaoConsolidadasByOrientador(int idOrientador, Boolean comNota) throws DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append( " SELECT rg FROM RegistroAtividade rg " +
				" join rg.orientacoesAtividade oa " +
				" join fetch rg.matricula mat " +
				" join fetch mat.discente d " );
		hql.append( " WHERE 1=1 " );
		hql.append( " AND oa.orientador.id = :idOrientador " );
		hql.append( " AND mat.situacaoMatricula.id in" + gerarStringIn( SituacaoMatricula.getSituacoesMatriculadas() ));
		if( comNota != null )
			hql.append( " AND mat.componente.necessitaMediaFinal = " + comNota );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idOrientador", idOrientador);

		@SuppressWarnings("unchecked")
		Collection<RegistroAtividade> lista = q.list();
		return lista;
	}	
	
	/**
	 * Retorna o registro de atividade do discente para a atividade informada.
	 * @param idDiscente
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findByDiscenteAtividade(int idDiscente, int idAtividade) throws DAOException{
		StringBuilder hql = new StringBuilder("SELECT rg.matricula FROM RegistroAtividade rg" +
				" WHERE rg.matricula.discente.id = :idDiscente" +
				" and rg.matricula.componente.id = :idAtividade");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idDiscente", idDiscente);
		q.setInteger("idAtividade", idAtividade);
		return (MatriculaComponente) q.uniqueResult();
	}
	
	/**
	 * Retorna todas as atividades orientada pelo docente informado, onde o componente curricular esta com a situação em espera ou matriculado
	 * @param idOrientador
	 * @param comNota TRUE para caso seja apenas as atividades com nota, FALSE para atividades sem nota e NULL para todas
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<RegistroAtividade> findMatriculasNaoConsolidadasByOrientador(int idOrientador) throws DAOException{

		// projeção da consulta por discentes da turma
		String projecao = 
				"  rg.id" +
				", mat.id" +
				", mat.discente.id" +
				", mat.discente.matricula" +
				", mat.discente.pessoa.id" +
				", mat.discente.pessoa.nome" +
				", mat.ano" +
				", mat.periodo " +
				", mat.componente.codigo" +
				", mat.componente.tipoAtividade.descricao " +
				", mat.componente.detalhes.nome " +
				", mat.componente.necessitaMediaFinal" +
				", u.id" +
				", u.login"; 
		
		StringBuilder hql = new StringBuilder();
		hql.append( " SELECT "+ projecao +
				" FROM RegistroAtividade rg " +
				" inner join rg.orientacoesAtividade oa " +
				" inner join rg.matricula mat " +
				" inner join mat.discente d " +
				" inner join d.pessoa p " +
				" , Usuario u " );
		hql.append( " WHERE 1=1 " );
		hql.append( " AND oa.orientador.id = :idOrientador " );
		hql.append( " AND u.pessoa.id = p.id " );
		hql.append( " AND mat.situacaoMatricula.id in" + gerarStringIn( SituacaoMatricula.getSituacoesMatriculadas() ));
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idOrientador", idOrientador);
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		List<RegistroAtividade> registrosAtividade = new ArrayList<RegistroAtividade>();

		if (result != null && !result.isEmpty()) {

			for (Object[] linha : result) {
				
				RegistroAtividade ra = new RegistroAtividade();
				ra.setId((Integer) linha[0]);
				ra.setMatricula(new MatriculaComponente((Integer) linha[1]));
				ra.getMatricula().setDiscente(new Discente((Integer) linha[2]));
				ra.getMatricula().getDiscente().setMatricula((Long) linha[3]);
				ra.getMatricula().getDiscente().setPessoa(new Pessoa((Integer) linha[4]));
				ra.getMatricula().getDiscente().getPessoa().setNome((String) linha[5]);
				ra.getMatricula().setAno((Short) linha[6]);
				ra.getMatricula().setPeriodo((Byte) linha[7]);
				ra.getMatricula().setComponente(new ComponenteCurricular());
				ra.getMatricula().getComponente().setCodigo((String) linha[8]);
				ra.getMatricula().getComponente().setTipoAtividade(new TipoAtividade());
				ra.getMatricula().getComponente().getTipoAtividade().setDescricao((String) linha[9]);
				ra.getMatricula().getComponente().setDetalhes(new ComponenteDetalhes());
				ra.getMatricula().getComponente().getDetalhes().setNome((String) linha[10]);
				ra.getMatricula().getComponente().setNecessitaMediaFinal((Boolean) linha[11]);
				ra.getMatricula().getDiscente().setUsuario(new Usuario((Integer) linha[12]));
				ra.getMatricula().getDiscente().getUsuario().setLogin((String) linha[13]);
				
				registrosAtividade.add(ra);
			}
		}
		return registrosAtividade;
	}
	
	/**
	 * Busca todas as atividaes de um discente de acordo com o ano-periodo informado e situações
	 * 
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroAtividade> findByDiscente(int idDiscente, int ano, int periodo, Collection<SituacaoMatricula> situacoes) throws DAOException {
		String hql = "select ra from RegistroAtividade ra " +
				"		 inner join fetch ra.matricula mc " +
				" where mc.discente.id = " + idDiscente + " and mc.ano = " + ano + " and mc.periodo = " + periodo + " and mc.situacaoMatricula.id in " + gerarStringIn(situacoes);
		
		return getSession().createQuery(hql).list();
		
	}
}