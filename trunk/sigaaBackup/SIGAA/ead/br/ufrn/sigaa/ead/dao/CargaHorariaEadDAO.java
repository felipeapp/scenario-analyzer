/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/06/2011
 *
 */
package br.ufrn.sigaa.ead.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ead.dominio.CargaHorariaEad;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Classe responsável por consultas específicas à Carga Horária dedicada pelo
 * docente no ensino à distância.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class CargaHorariaEadDAO extends GenericSigaaDAO {
	
	
	/**
	 * Retorna uma coleção de Carga Horária dedicada pelo docente no ensino à
	 * distância que poderia ser cadastrada um determinado ano-período.
	 * 
	 * @param ano
	 * @param periodo
	 * @param nomeDocente 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CargaHorariaEad> findAllTransienteByAnoPeriodo(int ano, int periodo, String nomeDocente) throws HibernateException, DAOException {
		// Construindo a consulta em SQL porque HQL não permite join condicional
		String projecao = " count(distinct id_turma) as qtdTurmas," +
				" count(distinct id_matricula_componente) as qtdDiscentes,"+
				" turma.ano as ano," +
				" turma.periodo as periodo," +
				" id_disciplina as componenteCurricular.id," +
				" componenteCurricular.codigo as componenteCurricular.codigo," +
				" detalhes.id_componente_detalhes as componenteCurricular.detalhes.id," +
				" detalhes.codigo as componenteCurricular.detalhes.codigo," +
				" detalhes.nome as componenteCurricular.detalhes.nome," +
				" detalhes.nome_ascii as componenteCurricular.detalhes.nome_ascii," +
				" detalhes.ch_aula as componenteCurricular.detalhes.chAula," +
				" detalhes.ch_nao_aula as componenteCurricular.detalhes.chNaoAula," +
				" detalhes.ch_laboratorio as componenteCurricular.detalhes.chLaboratorio," +
				" detalhes.ch_estagio as componenteCurricular.detalhes.chEstagio," +
				" detalhes.ch_ead as componenteCurricular.detalhes.chEad," +
				" detalhes.cr_aula as componenteCurricular.detalhes.crAula," +
				" detalhes.cr_laboratorio as componenteCurricular.detalhes.crLaboratorio," +
				" detalhes.cr_estagio as componenteCurricular.detalhes.crEstagio," +
				" detalhes.cr_ead as componenteCurricular.detalhes.crEad," +
				" detalhes.ch_total as componenteCurricular.detalhes.chTotal," +
				" detalhes.ch_dedicada_docente as componenteCurricular.detalhes.chDedicadaDocente,"+
				" servidor.id_servidor as servidor.id," +
				" servidor.siape as servidor.siape," +
				" servidor.regime_trabalho as servidor.regimeTrabalho," +
				" pessoaServidor.id_pessoa as servidor.pessoa.id," +
				" pessoaServidor.nome as servidor.pessoa.nome," +
				" docenteExterno.id_docente_externo as docenteExterno.id," +
				" pessoaDocenteExterno.id_pessoa as docenteExterno.pessoa.id," +
				" pessoaDocenteExterno.nome as docenteExterno.pessoa.nome";
		// POG: o hibernate traz o mesmo valor para as colunas curso.nome, habilitacao.nome, pessoa.nome, etc...
		// dai a necessidade de renomear as colunas
		StringBuilder projecaoPog = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(projecao, ",");
		int k = 1;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] split = token.split(" as ");
			projecaoPog.append(split[0]).append(" as ").append("coluna_").append(k++).append(", ");
		}
		projecaoPog.delete(projecaoPog.lastIndexOf(","), projecaoPog.length());
		StringBuilder hqlNovos = new StringBuilder("SELECT ");
		hqlNovos.append(projecaoPog).append(
				" FROM ensino.matricula_componente matriculaComponente" +
				" INNER JOIN ensino.turma turma using (id_turma)" +
				" INNER JOIN ensino.componente_curricular componenteCurricular using (id_disciplina)" +
				" INNER JOIN ensino.componente_curricular_detalhes detalhes on (componenteCurricular.id_detalhe=detalhes.id_componente_detalhes)" +
				" INNER JOIN ensino.docente_turma docenteTurma using (id_turma)" +
				" LEFT JOIN ensino.docente_externo docenteExterno using (id_docente_externo)" +
				" LEFT JOIN rh.servidor servidor on (docenteTurma.id_docente = servidor.id_servidor or docenteExterno.id_servidor = servidor.id_servidor)" +
				" LEFT JOIN comum.pessoa pessoaServidor on (servidor.id_pessoa = pessoaServidor.id_pessoa)" +
				" LEFT JOIN comum.pessoa pessoaDocenteExterno on (docenteExterno.id_pessoa = pessoaDocenteExterno.id_pessoa)" +
				" WHERE turma.ano = :ano" +
				" AND turma.periodo = :periodo" +
				" AND (turma.distancia = true or turma.id_polo is not null)" +
				" AND matriculaComponente.id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido()));
		if (!isEmpty(nomeDocente))
			hqlNovos.append(" AND (pessoaServidor.nome_ascii ilike :nomeDocente or pessoaDocenteExterno.nome_ascii ilike :nomeDocente)");
		hqlNovos.append(
				" GROUP BY " +
				" turma.ano, turma.periodo, id_disciplina, componenteCurricular.codigo," +
				" detalhes.id_componente_detalhes, detalhes.codigo, detalhes.nome," +
				" detalhes.nome_ascii, detalhes.ch_aula, detalhes.ch_nao_aula," +
				" detalhes.ch_laboratorio, detalhes.ch_estagio, detalhes.ch_ead, detalhes.cr_aula," +
				" detalhes.cr_laboratorio, detalhes.cr_estagio, detalhes.cr_ead," +
				" detalhes.ch_total, detalhes.ch_dedicada_docente, servidor.id_servidor," +
				" servidor.siape, servidor.regime_trabalho, pessoaServidor.id_pessoa," +
				" pessoaServidor.nome, docenteExterno.id_docente_externo," +
				" pessoaDocenteExterno.id_pessoa, pessoaDocenteExterno.nome");
		Query qNovos = getSession().createSQLQuery(hqlNovos.toString());
		qNovos.setInteger("ano", ano).setInteger("periodo", periodo);
		if (!isEmpty(nomeDocente))
			qNovos.setString("nomeDocente", "%" + nomeDocente + "%");
		@SuppressWarnings("unchecked")
		List<Object[]> lista = qNovos.list();
		// converte de BigInteger para long
		if (lista != null) {
			for (Object[] object : lista) {
				object[0] = ((BigInteger)object[0]).longValue();
				object[1] = ((BigInteger)object[1]).longValue();
			}
		}
		Collection<CargaHorariaEad> listaNovos = HibernateUtils.parseTo(lista, projecao, CargaHorariaEad.class);
		return listaNovos;
	}
	
	/**
	 * Retorna uma coleção de Carga Horária dedicada pelo docente no ensino à
	 * distância cadastrado para um determinado ano-período.
	 * 
	 * @param ano
	 * @param periodo
	 * @param nomeDocente 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CargaHorariaEad> findAllPersistidoByAnoPeriodo(int ano, int periodo, String nomeDocente) throws HibernateException, DAOException {
		Collection<CargaHorariaEad> lista = new ArrayList<CargaHorariaEad>();
		// servidores
		StringBuilder hqlServidores = new StringBuilder("SELECT chEad " +
				" FROM CargaHorariaEad chEad" +
				" inner join fetch chEad.componenteCurricular cc" +
				" inner join fetch cc.detalhes ccd" +
				" inner join fetch chEad.servidor s" +
				" inner join fetch s.pessoa p" +
				" WHERE chEad.ano = :ano" +
				" AND chEad.periodo = :periodo");
		if (!isEmpty(nomeDocente))
			hqlServidores.append(" AND p.nomeAscii like :nomeDocente");
		Query qAntigos = getSession().createQuery(hqlServidores.toString());
		qAntigos.setInteger("ano", ano).setInteger("periodo", periodo);
		if (!isEmpty(nomeDocente))
			qAntigos.setString("nomeDocente", "%" + nomeDocente.toUpperCase() + "%");
		@SuppressWarnings("unchecked")
		Collection<CargaHorariaEad> servidores = qAntigos.list();
		if (!isEmpty(servidores))
			lista.addAll(servidores);
		// docente externos
		StringBuilder hqlDocenteExternos = new StringBuilder("SELECT chEad " +
				" FROM CargaHorariaEad chEad" +
				" inner join fetch chEad.componenteCurricular cc" +
				" inner join fetch cc.detalhes ccd" +
				" inner join fetch chEad.docenteExterno de" +
				" inner join fetch de.pessoa p" +
				" WHERE chEad.ano = :ano" +
				" AND chEad.periodo = :periodo");
		if (!isEmpty(nomeDocente))
			hqlDocenteExternos.append(" AND p.nomeAscii like :nomeDocente");
		Query qDocenteExternos = getSession().createQuery(hqlDocenteExternos.toString());
		qDocenteExternos.setInteger("ano", ano).setInteger("periodo", periodo);
		if (!isEmpty(nomeDocente))
			qDocenteExternos.setString("nomeDocente", "%" + nomeDocente.toUpperCase() + "%");
		@SuppressWarnings("unchecked")
		Collection<CargaHorariaEad> docenteExternos = qDocenteExternos.list();
		if (!isEmpty(docenteExternos))
			lista.addAll(docenteExternos);
			
		return lista;
	}

	/** Retorna uma coleção de carga horária dedicada ao ensino de turmas à distância de um docente em um ano-período.
	 * @param idServidor
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<CargaHorariaEad> findByServidorAnoPeriodo(int idServidor, int ano, int periodo) throws HibernateException, DAOException {
		StringBuilder hql = new StringBuilder("SELECT chEad FROM CargaHorariaEad chEad" +
				" WHERE ano = :ano" +
				" AND periodo = :periodo" +
				" AND servidor.id = :idServidor");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano).setInteger("periodo", periodo).setInteger("idServidor", idServidor);
		@SuppressWarnings("unchecked")
		List<CargaHorariaEad> lista = q.list();
		return lista;
	}

}
