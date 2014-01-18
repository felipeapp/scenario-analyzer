/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino_rede.dominio.ComponenteCurricularRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.ParametrosSelecionaDiscente;
import br.ufrn.sigaa.ensino_rede.jsf.ValoresSelecionaDiscente;

/**
 * Dao responsável por consultas específicas aos discentes de ensino em rede.
 * @author Diego Jácome
 */
public class DiscenteAssociadoDao  extends GenericSigaaDAO  {
	
	/**
	 * Retorna os discentes de um campus específico
	 *
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DiscenteAssociado> findDiscenteByCampus (int idCampus, Integer ano, Integer periodo) throws HibernateException, DAOException {
		
		String hql = " select da from DiscenteAssociado da " +
					 " join fetch da.status s "+
					 " join da.dadosCurso dc " +
					 " join dc.campus c " +
					 " where c.id = " +idCampus+
					 " and s.id in "+UFRNUtils.gerarStringIn(StatusDiscenteAssociado.getStatusAtivos());
		
		if (ano != null)
			hql += " and da.anoIngresso = "+ano;
		if (periodo != null)
			hql += " and da.periodoIngresso = "+periodo;

		hql += " order by da.pessoa.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		return (ArrayList<DiscenteAssociado>) q.list();
	}

	/**
	 * Retorna os discentes que já foram aprovados no componente determinado
	 *
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> findDiscentePorSituacao (ComponenteCurricularRede c, List<DiscenteAssociado> discentes , Collection<SituacaoMatricula> situacoes) throws HibernateException, DAOException {
		
		if (discentes == null || discentes.isEmpty())
			return new ArrayList<Integer>();
		
		String sql = " select da.id_discente_associado from ensino_rede.discente_associado da "+
						" inner join ensino_rede.matricula_componente_rede m on m.id_discente = da.id_discente_associado "+
						" inner join ensino_rede.turma_rede t on t.id_turma_rede = m.id_turma "+
						" inner join ensino_rede.componente_curricular_rede c on c.id_disciplina = t.id_componente_curricular "+
						" where da.id_discente_associado in "+UFRNUtils.gerarStringIn(discentes)+" and m.id_situacao in "+UFRNUtils.gerarStringIn(situacoes)+" and c.id_disciplina = "+c.getId();

		Query q = getSession().createSQLQuery(sql);
		
		return (ArrayList<Integer>) q.list();
	}

	/**
	 * Retorna uma lista de discentes associados a um programa em rede.
	 * Opcionalmente, pode-se informar como parâmeros o campus e o cpf do
	 * discente.
	 * 
	 * @param programaRede
	 * @param parametros
	 * @param valores
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<DiscenteAssociado> findByParametros(ProgramaRede programaRede, ParametrosSelecionaDiscente parametros, ValoresSelecionaDiscente valores) throws DAOException {
		
		String projecao = "da.id, da.pessoa.nome, da.pessoa.cpf_cnpj, da.status.descricao," +
				" curso.nome as da.dadosCurso.curso.nome, instituicao.nome as da.dadosCurso.campus.instituicao.nome," +
				" instituicao.sigla as da.dadosCurso.campus.instituicao.sigla, campus.nome as da.dadosCurso.campus.nome," +
				" campus.sigla as da.dadosCurso.campus.sigla";
		
		StringBuilder sb = new StringBuilder();
		sb.append("select " + HibernateUtils.removeAliasFromProjecao(projecao) 
				+ " from DiscenteAssociado da "
				+ " join da.dadosCurso dc "
				+ " join dc.curso curso"
				+ " join da.status status "
				+ " join dc.programaRede programa "
				+ " join dc.campus campus "
				+ " join campus.instituicao instituicao"
				+ " join da.pessoa pessoa "
				+ " where programa.id = " + programaRede.getId());
		
		if (parametros.isCheckCampus())
			sb.append(" and campus.id = :idCampus");
		if (parametros.isCheckNome())
			sb.append(" and upper(pessoa.nomeAscii) like :nome");
		if (parametros.isCheckCpf())
			sb.append(" and pessoa.cpf_cnpj = :cpf");	
		if (parametros.isCheckProgramaRede())
			sb.append(" and programa.id = :idProgramaRede");
		if (parametros.isCheckStatusDiscente()){
			if( ValidatorUtil.isNotEmpty( valores.getStatus()  ) ){
				sb.append(" and status.id in " + gerarStringIn(valores.getStatus() ) );
			}	
		}	
		
		sb.append(" order by instituicao.nome, campus.nome, pessoa.nomeAscii");
		
		Query query = getSession().createQuery(sb.toString());
		
		if (parametros.isCheckCampus())
			query.setInteger("idCampus", valores.getValorIdCampus());
		if (parametros.isCheckNome())
			query.setString("nome", StringUtils.toAscii(valores.getValorNome()).toUpperCase() + "%");
		if (parametros.isCheckCpf())
			query.setLong("cpf", valores.getValorCpf());
		if (parametros.isCheckProgramaRede())
			query.setLong("idProgramaRede", valores.getValorIdProgramaRede());
		
		return (List<DiscenteAssociado>) HibernateUtils.parseTo(query.list(), projecao, DiscenteAssociado.class, "da");
	}
	
	/**
	 * Retorna um Discente Associado que possua vínculo ativo (ATIVO). Caso não
	 * exista discente associado ativo, com o número de CPF, retornará null.
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public DiscenteAssociado findVinculoAtivoByCPF(long cpf) throws DAOException {
		String projecao = "da.id, da.pessoa.nome, da.pessoa.cpf_cnpj, da.status.descricao," +
				" da.dadosCurso.campus, da.dadosCurso.curso ";
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(projecao)
		.append(" from DiscenteAssociado da "
				+ " where da.pessoa.cpf_cnpj = :cpf" +
				" and da.status.id in ")
		.append(UFRNUtils.gerarStringIn(StatusDiscenteAssociado.getStatusAtivos()));
		Query query = getSession().createQuery(sb.toString());
		query.setLong("cpf", cpf);
		Collection<DiscenteAssociado> lista = HibernateUtils.parseTo(query.list(), projecao, DiscenteAssociado.class, "da");
		if (!isEmpty(lista))
			return lista.iterator().next();
		else
			return null;
	}
	
}
