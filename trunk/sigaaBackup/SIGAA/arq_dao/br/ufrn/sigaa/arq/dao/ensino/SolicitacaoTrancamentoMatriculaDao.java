/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 31/05/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamento;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao responsável por gerenciar as solicitações de trancamento de matrícula
 *
 * @author Victor Hugo
 *
 */
public class SolicitacaoTrancamentoMatriculaDao extends GenericSigaaDAO {

	static StringBuffer construtorProjecao = new StringBuffer();

	static {
		construtorProjecao.append( "SELECT DISTINCT new SolicitacaoTrancamentoMatricula(stm.id, matriculaComponente.id, " +
				" componente.id, detalhes.nome, " +
				" componente.codigo, tipoComponente.id, " +
				" stm.situacao, stm.dataCadastro, stm.dataAtendimento, motivo.descricao, stm.replica, turma.ano, " +
				" turma.periodo, turma.id, turma.codigo,  " +
				" discente.id, discente.matricula, " +
				" pessoaDiscente.nome, discente.nivel, " +
				" gestoraAcademica.id, gestoraAcademica.tipoAcademica, stm.justificativa, pessoaAtendedor.nome, discente.curso.id ) " +
				" FROM SolicitacaoTrancamentoMatricula stm" +
				" inner join stm.motivo motivo" +
				" inner join stm.matriculaComponente matriculaComponente" +
				" inner join matriculaComponente.componente componente" +
				" inner join componente.detalhes detalhes" +
				" inner join componente.tipoComponente tipoComponente" +
				" inner join matriculaComponente.turma turma" +
				" inner join matriculaComponente.discente discente" +
				" inner join discente.pessoa pessoaDiscente" +
				" inner join discente.gestoraAcademica gestoraAcademica" +
				" inner join stm.motivo" +
				// registroAtentedor pode ser null, portando usa-se left join
				" left  join stm.registroAtendendor registroAtendendor" +
				" left  join registroAtendendor.usuario usuario" +
				" left  join usuario.pessoa pessoaAtendedor ");
	}

	public SolicitacaoTrancamentoMatriculaDao() {
	}

	/**
	 * Retorna as solicitações de trancamento do discente informado com a situação passada por parâmetro
	 * caso a situação seja null considera qualquer uma.
	 * 
	 * @param idDiscente
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findByDiscente(int idDiscente, int... situacao) throws DAOException{

		StringBuffer hql = new StringBuffer();

		hql.append( construtorProjecao );
		hql.append( " WHERE stm.matriculaComponente.discente.id = :idDiscente " );

		//if( situacao != null )
		//	hql.append( " AND stm.situacao = :situacao " );
		if( situacao != null )
			hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn( situacao ) );

		Query q = getSession().createQuery( hql.toString() );

		q.setInteger("idDiscente", idDiscente);
		//if( situacao != null )
		//	q.setInteger("situacao", situacao);

		return q.list();

	}

	/**
	 * Retorna quantidade de discentes que solicitaram trancamento e ainda não foram orientados pelo
	 * coordenador do curso.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Long getNumeroSolicitacoesTrancamentoMatricula(Curso curso, Integer ano, Integer periodo) throws DAOException {
		try {
			String hql = "select count(distinct d.id) from SolicitacaoTrancamentoMatricula s join s.matriculaComponente.discente d" +
					" where s.matriculaComponente.ano = :ano and s.matriculaComponente.periodo = :periodo and s.situacao = :situacao and " +
					"s.matriculaComponente.discente.curso.id = :curso ";
			Query q = getSession().createQuery(hql);
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("curso", curso.getId());
			q.setInteger("situacao", SolicitacaoTrancamentoMatricula.SOLICITADO);
			return (Long) q.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException( e);
		}
	}

	/**
	 * Retorna quantidade de solicitações de trancamentos ainda não aprovados pelo
	 * coordenador do curso.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Long getNumeroSolicitacoesTrancamentoMatricula(Unidade unidade, Integer ano, Integer periodo) throws DAOException {
		try {
			String hql = "select count(distinct d.id) from SolicitacaoTrancamentoMatricula s join s.matriculaComponente.discente d" +
					" where s.matriculaComponente.ano = :ano and s.matriculaComponente.periodo = :periodo and s.situacao = :situacao and " +
					"s.matriculaComponente.discente.gestoraAcademica.id = :unidade ";
			Query q = getSession().createQuery(hql);
			q.setInteger("periodo", periodo);
			q.setInteger("ano", ano);
			q.setInteger("unidade", unidade.getId());
			q.setInteger("situacao", SolicitacaoTrancamentoMatricula.SOLICITADO);
			return (Long) q.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException( e);
		}
	}

	/**
	 * Retorna a quantidade de solicitações de trancamento de matrículas de um discente com as situações informadas em um determinado ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public int countSolicitacaoTrancamentoByDiscente(DiscenteAdapter discente, int ano, int periodo, Collection<SituacaoMatricula> situacoesMatricula, int... situacoesTrancamento) throws DAOException {
       
		StringBuilder hqlMatriculas = new StringBuilder();
		hqlMatriculas.append("SELECT count(id) FROM MatriculaComponente WHERE discente.id="+discente.getId());
        if (ano > 0)
        	hqlMatriculas.append(" and ano="+ano);
        if (periodo > 0)
        	hqlMatriculas.append(" and periodo="+periodo);
        if( !isEmpty(situacoesMatricula) )
        	hqlMatriculas.append(" and situacaoMatricula.id in "+gerarStringIn(situacoesMatricula));

        int qtdeMatriculas = ((Long)getSession().createQuery(hqlMatriculas.toString()).uniqueResult()).intValue();
		
		StringBuilder hql = new StringBuilder();
        hql.append("select count(distinct s.id) " +
        		"	from SolicitacaoTrancamentoMatricula s join s.matriculaComponente.discente d" +
				"	where d.id = " + discente.getId() );
		if (ano > 0)
        	hql.append(" and s.matriculaComponente.ano="+ano);
        if (periodo > 0)
        	hql.append(" and s.matriculaComponente.periodo="+periodo);
        if( !isEmpty(situacoesMatricula) )
        	hql.append(" and s.matriculaComponente.situacaoMatricula.id in "+gerarStringIn(situacoesMatricula));
        if( !isEmpty(situacoesTrancamento) )
        	hql.append(" and s.situacao in "+gerarStringIn(situacoesTrancamento));

        int qtdeSolicitacoesTrancamento = ((Long)getSession().createQuery(hql.toString()).uniqueResult()).intValue(); 
        
        return qtdeMatriculas - qtdeSolicitacoesTrancamento;
	}
	

	/**
	 * Retorna as solicitações de trancamento ativas ou trancadas do discente, ano, período informados.
	 * 
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findByDiscenteAnoPeriodo(int idDiscente, int ano, int periodo, int... situacao) throws DAOException{
		StringBuffer hql = new StringBuffer();
		hql.append( construtorProjecao );
		hql.append( " WHERE stm.matriculaComponente.discente.id = :idDiscente " );
		hql.append( " AND stm.matriculaComponente.ano = :ano AND stm.matriculaComponente.periodo = :periodo" );

		if( situacao != null )
			hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn( situacao ) );
		//hql.append( " AND stm.situacao != " + SolicitacaoTrancamentoMatricula.CANCELADO );

		Query q = getSession().createQuery( hql.toString() );

		q.setInteger("idDiscente", idDiscente);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		return q.list();
	}

	/**
	 * Retorna a coleção de solicitações com o discente, componente, situação informados
	 * 
	 * @param idDiscente
	 * @param componente
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findByDiscenteComponenteSituacao(int idDiscente, int idComponente, int... situacao) throws DAOException{
		StringBuffer hql = new StringBuffer();
		hql.append( construtorProjecao );
		hql.append( " WHERE stm.matriculaComponente.discente.id = :idDiscente " );
		hql.append( " AND stm.matriculaComponente.componente.id = :idComponente" );

		if( situacao != null )
			hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn( situacao ) );

		Query q = getSession().createQuery( hql.toString() );

		q.setInteger("idDiscente", idDiscente);
		q.setInteger("idComponente", idComponente);

		return q.list();
	}

	/**
	 * Retorna as MatriculaComponente das solicitações de trancamento realizadas na data informada com as situações informadas
	 * 
	 * @param data
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findByData(Date data, Character nivel, int modalidadeEducacao, int... situacao) throws DAOException{

		StringBuffer hql = new StringBuffer();
		hql.append( construtorProjecao );
		hql.append( " left join stm.matriculaComponente mc " );
		hql.append( " left join mc.discente.curso curso WHERE 1 = 1" );

		if( data != null ) {
			hql.append( " AND stm.dataCadastro <= :dataCadastro " );
		}
		if (nivel != null) {
			if (nivel == NivelEnsino.STRICTO) {
				hql.append( " AND stm.matriculaComponente.discente.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()) );
			} else {
				hql.append( " AND stm.matriculaComponente.discente.nivel = '" + nivel + "'" );
			}
		}

		if( situacao != null ) {
			hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn( situacao ) );
		}
		
		hql.append( " AND ( curso.modalidadeEducacao.id = " + modalidadeEducacao + " OR curso.id IS NULL ) " );
		
		Query q = getSession().createQuery( hql.toString() );
		//q.setMaxResults(200);

		if( data != null ) {
			q.setDate("dataCadastro", data);
		}

		return q.list();
	}

	/**
	 * Retorna os discentes com o tipo solicitação especificada de um orientador acadêmico
	 * 
	 * @param idServidor O id do servidor que é orientador acadêmico
	 * @param situacao Tipo da situação para ser buscadas
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findDiscenteByOrientadorAcademicoSituacao(Servidor servidor, DocenteExterno docenteExterno, Character nivel, int... situacao) throws DAOException {

		StringBuffer hql = new StringBuffer(construtorProjecao);

		hql.append( " , OrientacaoAcademica orientacao" );
		hql.append( " WHERE orientacao.discente.id = stm.matriculaComponente.discente.id " );
		hql.append( " AND stm.matriculaComponente.discente.id = stm.matriculaComponente.discente.id " );
		hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn(situacao));
		hql.append( " AND orientacao.dataFinalizacao is null ");
		if( nivel == null ){
		} else if( NivelEnsino.isAlgumNivelStricto(nivel) ){
			hql.append( " AND orientacao.tipoOrientacao in " + gerarStringIn( new char[] { OrientacaoAcademica.ORIENTADOR } )	);
			hql.append(" and orientacao.cancelado = falseValue() ");
		} else if( nivel.equals( NivelEnsino.GRADUACAO ) ){
			hql.append( " AND orientacao.tipoOrientacao = '" + OrientacaoAcademica.ACADEMICO + "'" );
		}

		if( servidor != null )
			hql.append( " AND orientacao.servidor.id = :idServidor " );
		else if( docenteExterno != null )
			hql.append( " AND orientacao.docenteExterno.id = :idDocenteExterno " );
		else
			throw new IllegalArgumentException();

		hql.append(" ORDER BY stm.id, matriculaComponente.id, componente.id, detalhes.nome, componente.codigo, tipoComponente.id, " +
				" stm.situacao, stm.dataCadastro, stm.dataAtendimento, motivo.descricao, stm.replica, turma.ano, " +
				" turma.periodo, turma.id,  " +
				" discente.id, discente.matricula, " +
				" pessoaDiscente.nome, discente.nivel, " +
				" gestoraAcademica.id, stm.justificativa, pessoaAtendedor.nome");
		
//		hql.append( " ORDER BY stm.matriculaComponente.discente.nivel, stm.matriculaComponente.discente.pessoa.nome");

		Query q = getSession().createQuery( hql.toString() );
		if( servidor != null )
			q.setInteger("idServidor", servidor.getId());
		else if( docenteExterno != null )
			q.setInteger("idDocenteExterno", docenteExterno.getId());

		return q.list();
	}


	/**
	 * Retorna todas as solicitações de trancamento de matrícula, informando o orientador acadêmico e o tipo da solicitação
	 * 
	 * @param idServidor O id do servidor que é orientador acadêmico
	 * @param situacao Tipo da situação para ser buscadas
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findAllSolicitacoesByOrientadorAcademicoSituacao(Servidor servidor,
			DocenteExterno docenteExterno, int ano, int periodo, Character nivel,int... situacao) throws DAOException {

		StringBuffer hql = new StringBuffer(construtorProjecao);

		hql.append( " , OrientacaoAcademica orientacao" );
		if( servidor != null ){
			hql.append( " WHERE orientacao.servidor.id = :idServidor " );
		} else if( docenteExterno != null ){
			hql.append( " WHERE orientacao.docenteExterno.id = :idDocenteExterno " );
		} else
			throw new IllegalArgumentException();
		hql.append( " AND orientacao.dataFinalizacao is null ");
		hql.append( " AND orientacao.discente.id = stm.matriculaComponente.discente.id " );
		hql.append( " AND stm.matriculaComponente.discente.id = stm.matriculaComponente.discente.id" );
		hql.append( " AND stm.matriculaComponente.ano  = :ano " );
		hql.append( " AND stm.matriculaComponente.periodo = :periodo " );
		hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn(situacao));

		if( nivel == null ){
		} else if( NivelEnsino.isAlgumNivelStricto(nivel) ){
			hql.append( " AND orientacao.tipoOrientacao in " + gerarStringIn( new char[] { OrientacaoAcademica.ORIENTADOR, OrientacaoAcademica.CoORIENTADOR } ) );
		} else if( nivel.equals( NivelEnsino.GRADUACAO ) ){
			hql.append( " AND orientacao.tipoOrientacao = '" + OrientacaoAcademica.ACADEMICO + "'" );
		}

		hql.append(" ORDER BY stm.id, matriculaComponente.id, componente.id, detalhes.nome, componente.codigo, tipoComponente.id, " +
				" stm.situacao, stm.dataCadastro, stm.dataAtendimento, motivo.descricao, stm.replica, turma.ano, " +
				" turma.periodo, turma.id,  " +
				" discente.id, discente.matricula, " +
				" pessoaDiscente.nome, discente.nivel, " +
				" gestoraAcademica.id, stm.justificativa, pessoaAtendedor.nome");

		
//		hql.append( " ORDER BY stm.matriculaComponente.discente.nivel, stm.matriculaComponente.discente.pessoa.nome, " +
//				"stm.matriculaComponente.turma.ano, stm.matriculaComponente.turma.periodo, stm.matriculaComponente.componente.detalhes.nome");



		Query q = getSession().createQuery( hql.toString() );
		if( servidor != null ){
			q.setInteger("idServidor", servidor.getId());
		} else if( docenteExterno != null ){
			q.setInteger("idDocenteExterno", docenteExterno.getId());
		}
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);

		return q.list();
	}

	/**
	 * Retorna todas as solicitações de trancamento de matrícula, informando o curso e o tipo da solicitação
	 * 
	 * @param idCurso
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findByCursoSituacao(Curso curso, Unidade programa, int ano, int periodo, int... situacao) throws DAOException {

		StringBuffer hql = new StringBuffer(construtorProjecao);

		hql.append( " WHERE 1 = 1 " );
		hql.append( " AND stm.matriculaComponente.ano = :ano" );
		hql.append( " AND stm.matriculaComponente.periodo = :periodo" );
		hql.append( " AND stm.situacao in " + UFRNUtils.gerarStringIn(situacao));
		
		if( curso != null )
			hql.append( " AND stm.matriculaComponente.discente.curso.id = :idCurso " );
		else if( programa != null )
			hql.append( " AND stm.matriculaComponente.discente.gestoraAcademica.id = :idPrograma " );
	
		hql.append(" ORDER BY stm.id, matriculaComponente.id, componente.id, detalhes.nome, componente.codigo, tipoComponente.id," +
				" stm.situacao, stm.dataCadastro, stm.dataAtendimento, motivo.descricao, stm.replica, turma.ano," +
				" turma.periodo, turma.id, discente.id, discente.matricula, pessoaDiscente.nome, discente.nivel, " +
				"gestoraAcademica.id, stm.justificativa, pessoaAtendedor.nome");
		
//		hql.append( " ORDER BY stm.matriculaComponente.discente.nivel, stm.matriculaComponente.discente.pessoa.nome, " +
//				" stm.matriculaComponente.turma.ano, stm.matriculaComponente.turma.periodo, stm.matriculaComponente.componente.detalhes.nome");

		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		
		if( curso != null )
			q.setInteger("idCurso", curso.getId());
		else if( programa != null )
			q.setInteger("idPrograma", programa.getId());

		return q.list();
	}

	/**
	 * Busca todos os discentes do EAD que solicitaram trancamento
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findAllEad() throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append( " select distinct d from SolicitacaoTrancamentoMatricula stm1 left join stm1.matriculaComponente mc1 left join mc1.discente d, " );
		hql.append( " SolicitacaoTrancamentoMatricula stm2 left join stm2.matriculaComponente mc2 left join mc2.turma t WHERE (t.polo is not null OR t.distancia = trueValue()) " );
		hql.append( " AND stm1.id = stm2.id and stm1.situacao in " + UFRNUtils.gerarStringIn( new int[] { SolicitacaoTrancamentoMatricula.SOLICITADO } ) );

		Query q = getSession().createQuery( hql.toString() );
		return q.list();
	}
	
	/**
	 * Retorna todas as solicitações de trancamento de matrícula, informando o curso e o tipo da solicitação
	 * 
	 * @param idCurso
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findProcessadasEAD(int ano, int periodo) throws DAOException {

		StringBuffer hql = new StringBuffer(construtorProjecao);

		hql.append( " WHERE matriculaComponente.ano = :ano" );
		hql.append( " AND matriculaComponente.periodo = :periodo" );
		hql.append( " AND (turma.polo is not null OR turma.distancia = trueValue()) " );
		hql.append( " AND stm.situacao in (" + SolicitacaoTrancamentoMatricula.CANCELADO + "," +
				SolicitacaoTrancamentoMatricula.RECUSADO + "," +
				SolicitacaoTrancamentoMatricula.TRANCADO + "," +
				SolicitacaoTrancamentoMatricula.VISTO + ")");
		
	
		hql.append(" ORDER BY stm.id, matriculaComponente.id, componente.id, detalhes.nome, componente.codigo, tipoComponente.id," +
				" stm.situacao, stm.dataCadastro, stm.dataAtendimento, motivo.descricao, stm.replica, turma.ano," +
				" turma.periodo, turma.id, discente.id, discente.matricula, pessoaDiscente.nome, discente.nivel, " +
				"gestoraAcademica.id, stm.justificativa, pessoaAtendedor.nome");
		
		Query q = getSession().createQuery( hql.toString() );
		
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		
		return q.list();
	}

	/**
	 * Retorna todas as solicitações de trancamentos ativas do discente informado das subunidades do bloco informado
	 * 
	 * @param bloco
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoTrancamentoMatricula> findPendetesByBlocoDiscente(ComponenteCurricular bloco, DiscenteAdapter discente) throws DAOException{

		StringBuffer hql = new StringBuffer( " SELECT stm FROM SolicitacaoTrancamentoMatricula stm " );
		hql.append( " WHERE stm.matriculaComponente.discente.id = :idDiscente " );
		hql.append( " AND stm.matriculaComponente.componente.blocoSubUnidade.id = :idBloco " );
		hql.append( " AND stm.situacao in " + gerarStringIn( new int[] {SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO} ) );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idDiscente", discente.getId());
		q.setInteger("idBloco", bloco.getId());

		return q.list();
	}

	/**
	 * Realiza a consulta para geração do relatório analítico de trancamento de
	 * componete curricular por motivo.
	 * 
	 * @param unidade
	 * @param curso
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List relatorioTrancamentoTurmaPorMotivo(
			Integer ano, Integer periodo, ComponenteCurricular componenteCurricular, Unidade unidade, Curso curso) throws DAOException{
		//sql de consulta
		Collection<MotivoTrancamento> motivos = findAll(MotivoTrancamento.class, "id", "asc");
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct unidade.nome as unidade,");
		sql.append(" turma.ano,");
		sql.append(" turma.periodo,");
		sql.append(" turma.codigo as codigo_turma,");
		sql.append(" componente_curricular.codigo as codigo_componente,");
		sql.append(" componente_curricular_detalhes.nome as componente,");
		// conta o valor de cada motivo
		for (MotivoTrancamento motivo : motivos) {
			sql.append("'"+motivo.getDescricao()+"' as motivo_descricao"+motivo.getId()+",");
			sql.append(" sum(case when matricula_componente.id_situacao_matricula = 5 and solicitacao_trancamento_matricula.id_motivo_trancamento = "+motivo.getId()+" then 1 else 0 end) as quant_trancamento"+motivo.getId()+",");
		}
		sql.append(" sum(case when matricula_componente.id_situacao_matricula = 5 then 1 else 0 end) as trancamentos,");
		sql.append(" round(100 * sum(case when matricula_componente.id_situacao_matricula = 5 then 1 else 0 end) :: numeric / count(matricula_componente.id_discente), 2) as percentual_trancamento,");
		sql.append(" count(matricula_componente.id_discente) as total_discentes");
		sql.append(" from ensino.turma");
		sql.append(" inner join ensino.componente_curricular using (id_disciplina)");
		sql.append(" inner join ensino.componente_curricular_detalhes on (id_detalhe = id_componente_detalhes)");
		sql.append(" inner join comum.unidade using (id_unidade)");
		sql.append(" inner join ensino.matricula_componente using (id_turma, ano, periodo)");
		sql.append(" inner join discente on( discente.id_discente = matricula_componente.id_discente ) ");
		sql.append(" left join ensino.solicitacao_trancamento_matricula using (id_matricula_componente)");
		
		
		// situações que configuram um aluno na turma:
		sql.append(" where matricula_componente.id_situacao_matricula not in (1, 10, 11, 12, 21, 22, 23)");

		
		if(curso != null)
			sql.append(" AND discente.id_curso =  " + curso.getId());
		if(ano != null)
			sql.append(" and turma.ano = " + ano + " and matricula_componente.ano = " + ano);
		if (periodo != null)
			sql.append(" and turma.periodo = " + periodo + " and matricula_componente.periodo = " + periodo);
		if (componenteCurricular != null)
			sql.append(" and id_componente_curricular = " + componenteCurricular.getId());
		if (unidade != null)
			sql.append(" and id_unidade = " + unidade.getId());
		
			
		
		sql.append(" group by unidade.nome, turma.codigo, turma.ano, turma.periodo, componente_curricular.codigo, componente_curricular_detalhes.nome");
		sql.append(" order by turma.ano, turma.periodo, componente_curricular_detalhes.nome, turma.codigo");

		return getJdbcTemplate().queryForList(sql.toString());
	}
	
	/**
	 * Retorna os discente do curso ou programa informado que possuem solicitação de
	 * trancamento de matrícula pendentes.
	 *
	 * @param curso 
	 * @param programa
	 * @param portal - informa se a consulta é destinada ao portal do coordenador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findBySolicitacoesTrancamentoPendentes(Curso curso, Unidade programa, int ano, int periodo, boolean portal) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer();
			Collection<Discente> lista = new ArrayList<Discente>();
			hql	.append(" SELECT DISTINCT new Discente(matriculaComponente.discente.id,matriculaComponente.discente.matricula,"
				+ " matriculaComponente.discente.pessoa.nome, matriculaComponente.discente.status, matriculaComponente.discente.nivel) "
				+ " FROM SolicitacaoTrancamentoMatricula stm ");
			hql.append(" WHERE stm.situacao = :situacao ");
			
			if( ano > 0 ) {
				hql.append(" AND stm.matriculaComponente.ano = :ano ");
			}
			if( periodo > 0 ) {
				hql.append(" AND stm.matriculaComponente.periodo = :periodo ");
			}
			
			if( curso != null ){
				hql.append(" AND stm.matriculaComponente.discente.curso.id = :idCurso ");
			} else if( programa != null ){
				hql.append(" AND stm.matriculaComponente.discente.gestoraAcademica.id = :idUnidade ");
			}
			hql.append(" GROUP BY matriculaComponente.discente.nivel, matriculaComponente.discente.pessoa.nome," +
					   " matriculaComponente.discente.id, matriculaComponente.discente.matricula, matriculaComponente.discente.status");
			hql.append(" ORDER BY matriculaComponente.discente.nivel, matriculaComponente.discente.pessoa.nome ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("situacao", SolicitacaoTrancamentoMatricula.SOLICITADO);
			
			if( curso != null ){
				q.setInteger("idCurso", curso.getId());
			} else if( programa != null ){
				q.setInteger("idUnidade", programa.getId());
			} else
				return lista;
			
			if( ano > 0 ) {
				q.setInteger("ano", ano);
			}
			if( periodo > 0 ) {
				q.setInteger("periodo", periodo);
			}
			// Se a consulta é destinada ao portal do coordenador, restringe o
			// número de resultados
			if (portal) {
				q.setMaxResults(5);
			}
	
			lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	
	}
}
