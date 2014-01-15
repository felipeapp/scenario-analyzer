/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/10/2009
 *
 */
package br.ufrn.sigaa.pid.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dominio.AtividadeComplementarPID;
import br.ufrn.sigaa.pid.dominio.AtividadesEspecificasDocente;
import br.ufrn.sigaa.pid.dominio.CargaHorariaAtividadesComplementares;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;
import br.ufrn.sigaa.pid.dominio.TipoOrientacaoPID;

/**
 * DAO responsável por gerenciar o acesso a dados do Plano Individual do Docente
 * 
 * @author agostinho campos
 */
 
public class PlanoIndividualDocenteDao extends GenericSigaaDAO {

	public PlanoIndividualDocenteDao() {
		super();
	}
	
	/**
	 * Buscar as atividades complementares de acordo com o tipo da entidade TipoAtividadeComplementarPID.
	 * 
	 * Podem ser do tipo: Ensino, Pesquisa e Produção Técnico-Científica, 
	 * Extensão e outras atividades técnicas e Administração. 
	 * 
	 * @param idTipoAtividade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings(value="unchecked")
	public List<AtividadeComplementarPID> findAtividadeComplementarPIDByTipo(int idTipoAtividade) throws HibernateException, DAOException {
		return getSession().createQuery("select ativ from AtividadeComplementarPID ativ where ativ.tipoAtividadeComplementar = ? order by ativ.denominacao").setInteger(0, idTipoAtividade).list();
	}
	
	/**
	 * Localiza o tipo de orientação da CH de Orientação - Graduação ou Pós
	 * 
	 * @param idTipoOrientacaoPID
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public TipoOrientacaoPID findTipoOrientacaoPIDByTipo(int idTipoOrientacaoPID) throws HibernateException, DAOException {
		return (TipoOrientacaoPID) getSession().createQuery("select tipo from TipoOrientacaoPID tipo where tipo.id = ?").setInteger(0, idTipoOrientacaoPID).uniqueResult();
	}
	

	/**
	 * Localiza o PID do servidor de acordo com o Ano/Período informado
	 * 
	 * @param servidor
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public PlanoIndividualDocente findPIDByServidorAnoPeriodo(Servidor servidor, int ano,int periodo) throws HibernateException, DAOException {
		String hqlAnoPeriodo = "select pid " +
								"from PlanoIndividualDocente as pid " +
								"where pid.servidor.id = ? and pid.ano = ? and pid.periodo = ?";
		return (PlanoIndividualDocente) getSession().createQuery(hqlAnoPeriodo).setInteger(0, servidor.getId()).setInteger(1, ano).setInteger(2, periodo).uniqueResult();
	}
	
	/**
	 * Tornao PID dos servidores de acordo com o Ano/Período informado
	 * 
	 * @param servidor
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<PlanoIndividualDocente> findPIDByServidoresAnoPeriodo(Collection<Servidor> servidores, int ano,int periodo) throws HibernateException, DAOException {
		if (isEmpty(servidores))
			return null;
		Collection<Integer> ids = new ArrayList<Integer>(1);
		for (Servidor s : servidores)
			ids.add(s.getId());
		Criteria c = getSession().createCriteria(PlanoIndividualDocente.class)
			.add(Restrictions.eq("ano",ano)).add(Restrictions.eq("periodo", periodo))
			.createCriteria("servidor").add(Restrictions.in("id", ids));
		@SuppressWarnings("unchecked")
		Collection<PlanoIndividualDocente> lista = c.list();
		return lista;
	}
	
	/**
	 * Busca todas as atividades especificas do plano individual passado como parâmetro
	 * @param pid
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings(value="unchecked")
	public List<AtividadesEspecificasDocente> findAtividadesEspecificasDocenteByPID(PlanoIndividualDocente pid) throws HibernateException, DAOException {
		String hqlAnoPeriodo = "select ativ from AtividadesEspecificasDocente as ativ where ativ.planoIndividualDocente = ?";
		return getSession().createQuery(hqlAnoPeriodo).setInteger(0, pid.getId()).list();
	}
	
	/**
	 * Busca todos os PIDs do servidor
	 * @param servidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings(value="unchecked")
	public List<PlanoIndividualDocente> findAllPIDDocente(Servidor servidor) throws HibernateException, DAOException {
		String hqlAllServiodr = "select pid from PlanoIndividualDocente as pid where pid.servidor.id = ? order by pid.ano, pid.periodo";
		return getSession().createQuery(hqlAllServiodr).setInteger(0, servidor.getId()).list();
	}
		
	/**
	 * Busca todos os PIDs da Unidade do Chefe do Departamento que estejam 
	 * homologados ou tenham sido enviados para a Chefia do Departamento.
	 *
	 * @param ano
	 * @param periodo
	 * @param servidor
	 * @param unidade
	 * @param todosStatus
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<PlanoIndividualDocente> findPIDByAnoPeriodoServidorUnidadeStatus(Integer ano, Integer periodo, Servidor docente, Unidade unidade, boolean todosStatus , int ... status  ) throws HibernateException, DAOException {
			
		String hql = "select pid from PlanoIndividualDocente as pid where  pid.servidor.unidade.id = " + unidade.getId();
		
		if (ano != null && ano > 0) { hql += " and pid.ano = " + ano; }
		if (periodo != null && periodo > 0) { hql += " and pid.periodo = " + periodo; }
		if(!isEmpty(docente)) { hql += " and pid.servidor.id = " + docente.getId();	}
		if ( !todosStatus &&  status != null )  hql += " and pid.status in " + UFRNUtils.gerarStringIn(status);
				
		hql += " order by pid.status , pid.servidor.pessoa.nome";

	    Query q = getSession().createQuery(hql);		
		List<PlanoIndividualDocente> lista = q.list();
		
		return lista;
	}
	
	/**
	 * Localiza as atividades complementares que foram setadas para um PID  
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings(value="unchecked")
	public List<CargaHorariaAtividadesComplementares>findCargaHorariaAtividadesComplementaresByPID(PlanoIndividualDocente pid) throws HibernateException, DAOException {
		return getSession().createQuery("select ch from CargaHorariaAtividadesComplementares ch where ch.planoIndividualDocente.id = ?").setInteger(0, pid.getId()).list();
	}

	/**
	 * Busca todos os planos individuais cadastrados de acordo com os critérios de busca informados
	 * 
	 * @param ano
	 * @param periodo
	 * @param docente
	 * @param unidade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoIndividualDocente> find(Integer ano, Integer periodo, Servidor docente, Unidade unidade) throws DAOException {
		String projecao = "id, ano, periodo, status, observacao, servidor.regimeTrabalho, servidor.pessoa.nome, servidor.unidade.sigla, servidor.classeFuncional.denominacao, "+
		" servidor.dedicacaoExclusiva, chProjeto.chPesquisa, chProjeto.chExtensao, chTotalAdministracao, totalGrupoEnsino, chOutrasAtividades.chSemanalOutrasAtividades, "+
		" totalGrupoOutrasAtividades, chOutrasAtividades.chSemanalOutrasAtividadesEnsino  ";
		StringBuilder hql = new StringBuilder("select " + projecao + " from PlanoIndividualDocente pid where status <> " + PlanoIndividualDocente.CADASTRADO);
		
		if (ano != null) { hql.append(" and pid.ano = " + ano); }
		if (periodo != null) { hql.append(" and pid.periodo = " + periodo); }
		if(!isEmpty(docente)) { hql.append(" and pid.servidor.id = " + docente.getId());	}
		if(!isEmpty(unidade)) { hql.append(" and pid.servidor.unidade.id = " + unidade.getId());	}

		hql.append(" order by ano, periodo, servidor.pessoa.nome");
		return HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, PlanoIndividualDocente.class);
	}

	/**
	 * Busca todos os PIDs homologados do servidor informado
	 * @param docente
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<PlanoIndividualDocente> findAllPIDsHomologadosByServidor(Servidor docente) throws HibernateException, DAOException {
		return getSession().createQuery("select pid from PlanoIndividualDocente pid where servidor.id = ? and status = ? order by cast(ano as text) desc, cast(periodo as text) desc").setInteger(0, docente.getId()).setInteger(1, PlanoIndividualDocente.HOMOLOGADO).list();
	}
	
	
	/** Busca por PIDs em determinado ano-período e retorna um CSV com os dados.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws ArqException
	 */
	public String findByAnoPeriodo(Integer ano, Integer periodo) throws ArqException {
		
			Connection con = null;
			StringBuilder dados = new StringBuilder();
			try {
				con = Database.getInstance().getSigaaConnection();		
			
				// dados do candidato
				String sqlCandidato = "SELECT pid.ano "+
					", pid.periodo" +
					", s.siape" +
					", p.nome_ascii AS docente" +
					", u.nome_ascii AS departamento"+
					", round(che.ch_atendimento_aluno::numeric, 2) AS ch_atendimento_aluno" +
					", round(che.ch_atendimento_aluno_grad::numeric, 2) AS orientacao_grad"+
					", round(che.ch_atendimento_aluno_pos_grad::numeric, 2) AS orientacao_pos"+
					", round((pid.ch_total_grupo_ensino - che.ch_atendimento_aluno - che.ch_atendimento_aluno_grad - che.ch_atendimento_aluno_pos_grad)::numeric,2) AS ch_sala_aula"+
					", round(pid.ch_total_grupo_ensino::numeric, 2) AS ch_total_grupo_ensino" +
					", round(pid.ch_total_administracao::numeric, 2) AS ch_total_administracao" +
					", round(chp.ch_pesquisa::numeric, 2) AS ch_pesquisa"+
					", round(chp.ch_extensao::numeric, 2) AS ch_extensao" +
					", round(choa.ch_outras_ativ_ensino::numeric, 2) AS ch_outras_ativ_ensino" +
					", round(choa.ch_outras_ativ::numeric, 2) AS ch_ativ_consepe "+
					", round(pid.ch_total_grupo_outras_ativ::numeric, 2) AS ch_total_outra_atividades" +
					", pid.data_homologacao "+
					", pid.obs_chefe_departamento " +
					"FROM pid.plano_individual_docente pid " +
					"JOIN pid.carga_horaria_projeto chp ON chp.id_carga_horaria_projeto = pid.id_carga_horaria_projeto " +
					"JOIN pid.carga_horaria_outras_atividades choa ON choa.id_carga_horaria_outras_atividades = pid.id_carga_horaria_outras_atividades " +
					"JOIN pid.carga_horaria_ensino che ON che.id_carga_horaria_ensino = pid.id_carga_horaria_ensino " +
					"JOIN rh.servidor s ON pid.id_servidor = s.id_servidor " +
					"JOIN comum.pessoa p ON p.id_pessoa = s.id_pessoa " +
					"JOIN comum.unidade u ON u.id_unidade = s.id_unidade " +
					"where ano = " + ano + " and periodo = " + periodo +
					" order by u.nome, p.nome";
				
				PreparedStatement st = con.prepareStatement(sqlCandidato);
				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					dados.append(BDUtils.resultSetToCSV(rs));
					return dados.toString();
				} else {
					return null;
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				throw new ArqException(e);
			} finally {
				Database.getInstance().close(con);
			}
	}
	
	
}