/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/10/2011
 *
 */	
package br.ufrm.sigaa.nee.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.OrientacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/** DAO responsável por consultas específicas à OrientacaoAtividade de alunos com NEE.
 * @author Rafael Gomes
 *
 */
public class OrientacaoAtividadeNeeDao extends GenericSigaaDAO {
	
	/** Construtor padrão. */
	public OrientacaoAtividadeNeeDao() {
		
	}

	/** Busca orientações de atividades baseado nos critérios informados.
	 * @param idOrientador ID do servidor/orientador do discente.  Caso null, é ignorado na busca.
	 * @param nomeDiscente nome do discente. Caso null, é ignorado na busca.
	 * @param idComponente ID do componente curricular. Caso null, é ignorado na busca.
	 * @param ano ano da matrícula no componente curricular. Caso null, é ignorado na busca.
	 * @param periodo período da matrícula no componente curricular. Caso null, é ignorado na busca.
	 * @param situacoesMatricula lista de IDs de situações de matrículas no componente.  Caso null, é ignorado na busca.
	 * @return lista de atividades encontradas de acordo com os critérios informados.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OrientacaoAtividade> findByOrientadorDiscenteComponente(
			Integer idOrientador, String nomeDiscente, Integer idComponente,
			Integer ano, Integer periodo, Collection<Integer> situacoesMatricula, Integer idUnidade,
			boolean isBuscaRegAtivEspecificas, Character nivelEnsino,Integer idCurso)
			throws HibernateException, DAOException {
		String tipoRelacionamentoBusca = isBuscaRegAtivEspecificas ? "left" : "inner";
		StringBuilder sql = new StringBuilder("select oa.id_orientacao_atividade as idOrientacaoAtividade,"
				+ " ra.id_registro_atividade as idRegistroAtividade,"
				+ " mc.id_matricula_componente as idMatriculaComponente,"
				+ " mc.ano as anoMatriculaComponente,"
				+ " mc.periodo as periodoMatriculaComponente,"
				+ " sm.id_situacao_matricula as idSituacaoMatricula,"
				+ " sm.descricao as descricaoSituacaoMatricula,"
				+ " ccd.codigo as codigoComponente,"
				+ " cc.id_disciplina as idComponenteCurricular,"
				+ " cc.id_tipo_atividade as idTipoAtividade,"
				+ " ccd.id_componente_detalhes as idDetalhes,"
				+ " ccd.nome as nomeComponente,"
				+ " ccd.ch_total as chTotal,"
				+ " ccd.ch_dedicada_docente as chDedicadaDocente,"
				+ " po.nome as nomeOrientador,"
				+ " pd.nome as nomeDiscente,"
				+ " s.id_servidor as idServidor,"
				+ " s.siape as siape,"
				+ " po.id_pessoa as idPessoaOrientador,"
				+ " po.cpf_cnpj as cpf,"
				+ " de.id_docente_externo as idDocenteExterno,"
				+ " pde.cpf_cnpj as cpfDocenteExterno,"
				+ " pde.nome as nomeDocenteExterno,"
				+ " pd.id_pessoa as idPessoaDiscente,"
				+ " d.matricula as matriculaDiscente,"
				+ " d.id_discente as idDiscente,"
				+ " u.id_usuario as idUsuario,"
				+ " u.login as loginUsuario,"
				+ " snee.denominacao as statusAtendimentoNee"
				+ " from ensino.registro_atividade ra "
				+ " "+tipoRelacionamentoBusca+" join ensino.orientacao_atividade oa using (id_registro_atividade)"
				+ " inner join ensino.matricula_componente mc using (id_matricula_componente)"
				+ " inner join ensino.situacao_matricula sm on (" +
						" sm.id_situacao_matricula = mc.id_situacao_matricula" +
						(!isEmpty(situacoesMatricula) ? " and mc.id_situacao_matricula in (:idSituacoesMatricula)":"") +
						(!isEmpty(ano) && !isEmpty(periodo) ?  " and mc.ano = :ano and mc.periodo = :periodo" : "")+
					")"
				+ " inner join ensino.componente_curricular cc on (id_componente_curricular = id_disciplina)"
				+ " inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
				+ " inner join discente d on (mc.id_discente = d.id_discente)"
				+ " inner join comum.pessoa pd on (d.id_pessoa = pd.id_pessoa)"
				+ " inner join nee.solicitacao_apoio_nee nee on (nee.id_discente = d.id_discente)"
				+ " inner join nee.status_atendimento snee on (snee.id_status_atendimento = nee.id_status_atendimento)"
				+ " left join rh.servidor s using (id_servidor)"
				+ " "+tipoRelacionamentoBusca+" join comum.pessoa po on (po.id_pessoa = s.id_pessoa)"
				+ " left join comum.usuario u on (u.id_pessoa = pd.id_pessoa)"
				+ " left join ensino.docente_externo de on (oa.id_orientador_externo = de.id_docente_externo)"
				+ " left join comum.pessoa pde on (pde.id_pessoa = de.id_pessoa)"
				+ " where cc.ativo = trueValue()");

		if (!isEmpty(nivelEnsino))
			sql.append(" and d.nivel = :nivelEnsino" );
		if (!isEmpty(idUnidade))
			sql.append(" and (s.id_unidade=:idUnidade or de.id_unidade=:idUnidade)");
		if (!isEmpty(idOrientador))
			sql.append(" and (s.id_servidor=:idOrientador or de.id_servidor=:idOrientador)");
		if (!isEmpty(nomeDiscente))
			sql.append(" AND " + UFRNUtils.convertUtf8UpperLatin9("pd.nome_ascii") + "like '%" 
					+ UFRNUtils.trataAspasSimples(StringUtils.toAscii(nomeDiscente.toUpperCase())) + "%'");
		if (!isEmpty(idCurso))
			sql.append(" and d.id_curso =:idCurso");
		if (!isEmpty(idComponente))
			sql.append(" and mc.id_componente_curricular = :idComponente");
		sql.append(" order by mc.ano, mc.periodo, ccd.codigo, pd.nome_ascii");
		
		Query q = getSession().createSQLQuery(sql.toString());
		if (!isEmpty(nivelEnsino))
			q.setCharacter("nivelEnsino", nivelEnsino);
		if (!isEmpty(idUnidade))
			q.setInteger("idUnidade", idUnidade);		
		if (!isEmpty(idOrientador))
			q.setInteger("idOrientador", idOrientador);
		if (!isEmpty(idCurso))
			q.setInteger("idCurso", idCurso);
		if (!isEmpty(idComponente))
			q.setInteger("idComponente", idComponente);
		if (!isEmpty(ano) && !isEmpty(periodo)) {
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		if (!isEmpty(situacoesMatricula))
			q.setParameterList("idSituacoesMatricula", situacoesMatricula);
		
		List<Object[]> bulkResult = q.list();
		ArrayList<OrientacaoAtividade> lista = new ArrayList<OrientacaoAtividade>();
		if (bulkResult != null) {
			for (Object[] obj : bulkResult) {
				int i = 0;
				Integer idOrientacaoAtividade        = (Integer) obj[i++];
				Integer idRegistroAtividade          = (Integer) obj[i++];
				Integer idMatriculaComponente        = (Integer) obj[i++];
				Short   anoMatriculaComponente       = (Short  ) obj[i++];
				Short   periodoMatriculaComponente   = (Short  ) obj[i++];
				Integer idSituacaoMatricula          = (Integer) obj[i++];
				String  descricaoSituacaoMatricula   = (String ) obj[i++];
				String  codigoComponente             = (String ) obj[i++];
				Integer idComponenteCurricular       = (Integer) obj[i++];
				Integer idTipoAtividade 		 	 = (Integer) obj[i++];
				Integer idDetalhes                   = (Integer) obj[i++];
				String  nomeComponente               = (String ) obj[i++];
				Short   chTotal                      = (Short  ) obj[i++];
				Integer chDedicadaDocente            = (Integer) obj[i++];
				String  nomePessoaOrientador         = (String ) obj[i++];
				String  nomePessoaDiscente           = (String ) obj[i++];
				Integer idServidor                   = (Integer) obj[i++];
				Integer siape                        = (Integer) obj[i++];
				Integer idPessoaOrientador           = (Integer) obj[i++];
				BigInteger cpf                       = (BigInteger) obj[i++];
				Integer idDocenteExterno             = (Integer) obj[i++];
				BigInteger cpfDocenteExterno         = (BigInteger) obj[i++];
				String nomeDocenteExterno            = (String) obj[i++];
				Integer idPessoaDiscente             = (Integer) obj[i++];
				BigInteger matriculaDiscente         = (BigInteger) obj[i++];
				Integer idDiscente                   = (Integer) obj[i++];
				Integer idUsuario                    = (Integer) obj[i++];
				String  loginUsuario                 = (String ) obj[i++];
				String  statusAtendimentoNee         = (String ) obj[i++];
				// construindo os objetos

				if ( idOrientacaoAtividade != null ){
					if ( !lista.contains(new OrientacaoAtividade(idOrientacaoAtividade))){
						
						if (cpf == null) cpf = new BigInteger("0");
						if (idDocenteExterno == null) idDocenteExterno = new Integer(0);
						if (cpfDocenteExterno == null) cpfDocenteExterno = new BigInteger("0");
						
						Usuario usuario = null;
						if (idUsuario != null) {
							usuario = new Usuario(idUsuario);
							usuario.setLogin(loginUsuario);
						}
						
						Discente discente = new Discente(idDiscente, matriculaDiscente.longValue(), nomePessoaDiscente);
						discente.setUsuario(usuario);
						discente.getPessoa().setId(idPessoaDiscente);
						discente.setObservacao(statusAtendimentoNee); // Preenchendo o status do Atendimento de NEE na Observação do discente.
						
						SituacaoMatricula situacao = new SituacaoMatricula(idSituacaoMatricula, descricaoSituacaoMatricula);
						
						Servidor orientador = new Servidor();
						if ( idServidor != null  ){
							orientador = new Servidor(idServidor, nomePessoaOrientador, siape);
							orientador.getPessoa().setId(idPessoaOrientador);
						}
						
						DocenteExterno orientadorExterno = new DocenteExterno(idDocenteExterno, cpfDocenteExterno.longValue(), nomeDocenteExterno);
						ComponenteCurricular atividade = new ComponenteCurricular(idComponenteCurricular, codigoComponente, nomeComponente);
						if( !isEmpty(idTipoAtividade) )
							atividade.setTipoAtividade( new TipoAtividade(idTipoAtividade) );
							
						ComponenteDetalhes detalhes = new ComponenteDetalhes(idDetalhes);
						detalhes.setNome(nomeComponente);

						detalhes.setChTotal(chTotal);
						detalhes.setChDedicadaDocente(chDedicadaDocente);
						atividade.setDetalhes(detalhes);
						
						MatriculaComponente matricula = new MatriculaComponente(idMatriculaComponente);
						matricula.setAno(anoMatriculaComponente);
						matricula.setPeriodo(Byte.valueOf(periodoMatriculaComponente.toString()));
						matricula.setComponente(atividade);
						matricula.setSituacaoMatricula(situacao);
						matricula.setDiscente(discente);
						
						RegistroAtividade registroAtividade = new RegistroAtividade(idRegistroAtividade);
						registroAtividade.setMatricula(matricula);
						registroAtividade.setCoordenador(orientador);
						
						OrientacaoAtividade orientacao = new OrientacaoAtividade(idOrientacaoAtividade);
						orientacao.setOrientador(orientador);
						orientacao.setOrientadorExterno(orientadorExterno);
						orientacao.setRegistroAtividade(registroAtividade);
						lista.add(orientacao);
					} 
				} else if( !lista.contains(new MatriculaComponente(idMatriculaComponente)) ){
					if (cpf == null) cpf = new BigInteger("0");
					if (idDocenteExterno == null) idDocenteExterno = new Integer(0);
										
					Usuario usuario = null;
					if (idUsuario != null) {
						usuario = new Usuario(idUsuario);
						usuario.setLogin(loginUsuario);
					}
					
					Discente discente = new Discente(idDiscente, matriculaDiscente.longValue(), nomePessoaDiscente);
					discente.setUsuario(usuario);
					discente.getPessoa().setId(idPessoaDiscente);
					
					SituacaoMatricula situacao = new SituacaoMatricula(idSituacaoMatricula, descricaoSituacaoMatricula);
					
					if (idPessoaOrientador != null){
						Servidor orientador = new Servidor(idServidor, nomePessoaOrientador, siape);
						orientador.getPessoa().setId(idPessoaOrientador);
					}
					
					DocenteExterno orientadorExterno = new DocenteExterno(idDocenteExterno, cpf.longValue(), nomePessoaOrientador);
					ComponenteCurricular atividade = new ComponenteCurricular(idComponenteCurricular, codigoComponente, nomeComponente);
					
					MatriculaComponente matricula = new MatriculaComponente(idMatriculaComponente);
					matricula.setAno(anoMatriculaComponente);
					matricula.setPeriodo(Byte.valueOf(periodoMatriculaComponente.toString()));
					matricula.setComponente(atividade);
					matricula.setSituacaoMatricula(situacao);
					matricula.setDiscente(discente);
					
					RegistroAtividade registroAtividade = new RegistroAtividade(idRegistroAtividade);
					registroAtividade.setMatricula(matricula);
				//	registroAtividade.setCoordenador(orientador);
				
					if( idOrientacaoAtividade == null ) idOrientacaoAtividade = 0;
					OrientacaoAtividade orientacao = new OrientacaoAtividade(idOrientacaoAtividade);
					//orientacao.setOrientador(orientador);
					orientacao.setOrientadorExterno(orientadorExterno);
					orientacao.setRegistroAtividade(registroAtividade);
					lista.add(orientacao);
				}
			}
		}
		return lista;
	}
	
}
