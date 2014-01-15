package br.ufrn.sigaa.arq.dao.vestibular;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.LinhaImpressaoDocumentosConvocados;
import br.ufrn.sigaa.vestibular.dominio.ResultadoArgumento;
import br.ufrn.sigaa.vestibular.dominio.ResultadoClassificacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;
import br.ufrn.sigaa.vestibular.jsf.DocumentosDiscentesConvocadosMBean;

/**
 * Classe responsável por consultas especializadas aos Documentos dos discentes convocados.
 * 
 * @author guerethes
 */
public class DocumentosDiscentesConvocadosDao extends GenericSigaaDAO {

	/**
	 * Busca os convocados levando em conta o processo seletivo, a matriz Curricular e a convocação.
	 * @param idsMatrizCurricular
	 * @param idDiscente
	 * @param idProcessoSeletivo
	 * @param idConvocacao
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaImpressaoDocumentosConvocados> findAllCandidatosConvocados(List<Integer> idsMatrizCurricular, Integer idDiscente, int idProcessoSeletivo, int idConvocacao, int idTipoRelatorio) throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT c.nome as curso, ha.nome as habilitacao, ga.descricao, t.sigla as sigla, d.matricula as matricula, iv.numero_inscricao as numeroInscricao, pv.nome as nome,"+  
					 " en.logradouro as endereco, en.numero as enderecoNumero, en.complemento as enderecoComplemento, en.bairro as bairro,"+  
					 " en.cep as cep, m.nome as cidade, uf.sigla as estado, pv.telefone_fixo as tel, pv.email as email, pv.telefone_celular,"+  
					 " pv.data_nascimento as dataNascimento, mNascimento.nome as cidadeNascimento, uf2.sigla as EstadoNascimento, p.nome as paisNascimento,"+  
					 " pv.sexo as sexo, ec.descricao as estadocivil, pv.nome_pai as nomePai, pv.nome_mae as nomeMae, pv.cpf_cnpj as cpf, pv.numero_identidade as rg,"+ 
					 " pv.orgao_expedicao_identidade as orgaorg, uf3.sigla as estadorg, pv.numero_titulo_eleitor as tituloeleitornumero,"+  
					 " pv.secao_titulo_eleitor as tituloeleitorsecao,  pv.zona_titulo_eleitor as tituloeleitorzona, uf4.sigla as tituloeleitorestado,"+  
					 " pv.segundograuescola as nomeescola, pv.segundograuanoconclusao as anoconclusao, d.periodo_ingresso as semestreaprovacao,"+  
					 " m2.nome as municipiocurso, ps.nome as concurso, iv.id_inscricao_vestibular as idcandidato, roc.argumento_final as argumentofinal,"+
					 " cps.descricao as listagem, cast (curr.semestre_conclusao_maximo as integer) as semestremaximoconclusao,"+
					 " roc.classificacao as classificacaocandidato, mc.id_matriz_curricular as matrizCurricular, cpsd.tipo as tipoconvocacao," +
					 " gcvc.descricao||' - '||gcvc.descricao_detalhada as grupo_cota, cpsd.dentro_numero_vagas, gcvci.descricao as grupo_cota_inscricao");
					 
		sql.append(" from vestibular.convocacao_processo_seletivo_discente cpsd" +
					" inner join vestibular.convocacao_processo_seletivo cps using (id_convocacao_processo_seletivo) " +
					" inner join vestibular.processo_seletivo ps using (id_processo_seletivo) " +
					" inner join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
					" left join ensino.grupo_cota_vaga_curso gcvci using (egresso_escola_publica, pertence_grupo_etnico, baixa_renda_familiar) " +
					" inner join vestibular.resultado_classificacao_candidato rcc ON (iv.id_inscricao_vestibular = rcc.id_inscricao_vestibular) " +
					" inner join vestibular.resultado_opcao_curso roc ON  (rcc.id_resultado_classificacao_candidato = roc.id_resultado_classificacao_candidato )" +
					" inner join discente d ON (cpsd.id_discente = d.id_discente) " +
					" inner join graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
					" inner join graduacao.curriculo curr ON (d.id_curriculo = curr.id_curriculo) " +
					" inner join graduacao.matriz_curricular mc on (dg.id_matriz_curricular = mc.id_matriz_curricular) " +
					" inner join curso c ON (mc.id_curso = c.id_curso) " +
					" inner join ensino.turno t ON (mc.id_turno = t.id_turno)" +
					" left join ensino.grupo_cota_vaga_curso gcvc on (gcvc.id_grupo_cota_vaga_curso = cpsd.id_grupo_cota_vaga_curso) " +
					" left join ensino.grau_academico ga ON (mc.id_grau_academico = ga.id_grau_academico) " +
					" left join graduacao.habilitacao ha ON (mc.id_habilitacao = ha.id_habilitacao) " +
					" left join comum.pessoa pv ON (d.id_pessoa = pv.id_pessoa) " +
					" left join comum.endereco en ON (en.id_endereco = pv.id_endereco_contato) " +
					" left join comum.municipio m ON (en.id_municipio = m.id_municipio) " +
					" left join comum.municipio mNascimento ON (pv.id_municipio_naturalidade = mNascimento.id_municipio) " +
					" left join comum.unidade_federativa uf ON (uf.id_unidade_federativa = m.id_unidade_federativa) " +
					" left join comum.unidade_federativa uf2 ON (uf2.id_unidade_federativa = pv.id_uf_naturalidade) " +
					" left join comum.pais p ON (uf2.id_pais = p.id_pais) " +
					" left join comum.estado_civil ec ON (pv.id_estado_civil = ec.id_estado_civil) " +
					" left join comum.unidade_federativa uf3 ON (uf3.id_unidade_federativa = pv.id_uf_identidade) " +
					" left join comum.unidade_federativa uf4 ON (uf4.id_unidade_federativa = pv.id_uf_titulo_eleitor) " +
					" left join comum.municipio m2 ON (c.id_municipio = m2.id_municipio) ");
		if (idTipoRelatorio == DocumentosDiscentesConvocadosMBean.LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO)
			sql.append(" left join graduacao.mudanca_curricular mud ON (mud.id_discente = d.id_discente) ");
		
		sql.append(" WHERE cps.id_convocacao_processo_seletivo = :convocacaoProcessoSeletivo"+
	  				 ( !ValidatorUtil.isEmpty( idsMatrizCurricular) ? " AND mc.id_matriz_curricular IN " + gerarStringIn(idsMatrizCurricular) : "") +
	  				 " AND ps.id_processo_seletivo = :processoSeletivo");
	  	if (idTipoRelatorio == DocumentosDiscentesConvocadosMBean.DOCUMENTO_RECONVOCACAO_MUDANCA_SEMESTRE) 
	  		sql.append( " AND cpsd.tipo = :tipoConvocacaoMudancaSemestre");
	  	else if (idTipoRelatorio == DocumentosDiscentesConvocadosMBean.LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO)
	  		sql.append( " AND (cpsd.tipo = :tipoConvocacaoMudancaSemestre or (cpsd.tipo = :tipoConvocacaoPrimeiraOpcao and mud.id_matriz_antiga is not null))");
	  	else
	  		sql.append( " AND (cpsd.tipo is null or cpsd.tipo <> :tipoConvocacaoMudancaSemestre)");
	  	if (idDiscente != null)
	  		sql.append(" AND d.id_discente = :idDiscente");
	  	sql.append( " order by m2.nome, c.nome, ha.nome, ga.descricao, t.sigla, d.periodo_ingresso, gcvc.descricao, pv.nome");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("processoSeletivo", idProcessoSeletivo);
		q.setInteger("convocacaoProcessoSeletivo", idConvocacao);
		q.setInteger("tipoConvocacaoMudancaSemestre", TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE.ordinal());
		if (idTipoRelatorio == DocumentosDiscentesConvocadosMBean.LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO)
			q.setInteger("tipoConvocacaoPrimeiraOpcao", TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal());
		if (idDiscente != null)
			q.setInteger("idDiscente", idDiscente);
		@SuppressWarnings("unchecked")
		Collection<Object[]> bulk = q.list();
		Collection<LinhaImpressaoDocumentosConvocados> lista = new ArrayList<LinhaImpressaoDocumentosConvocados>();
		Map<String, Collection<ResultadoArgumento>> mapListaResultadoArgumento = findResultadoArgumentoCandidatosConvocados(idsMatrizCurricular, idDiscente, idProcessoSeletivo, idConvocacao);
		if (bulk != null) {
			for (Object[] obj : bulk){
				int k = 0;
				LinhaImpressaoDocumentosConvocados convocacao = new LinhaImpressaoDocumentosConvocados();
				convocacao.setCurso((String) obj[k++]);
				convocacao.setHabilitacao((String) obj[k++]);
				convocacao.setDescricao((String) obj[k++]);
				convocacao.setSigla((String) obj[k++]);
				convocacao.setMatricula((BigInteger) obj[k++]);
				convocacao.setNumeroInscricao((Integer) obj[k++]);
				convocacao.setNome((String) obj[k++]);
				convocacao.setEndereco((String) obj[k++]);
				convocacao.setEnderecoNumero((String) obj[k++]);
				convocacao.setEnderecoComplemento((String) obj[k++]);
				convocacao.setEnderecoBairro((String) obj[k++]);
				convocacao.setCep((String) obj[k++]);
				convocacao.setCidade((String) obj[k++]);
				convocacao.setEstado((String) obj[k++]);
				convocacao.setTel((String) obj[k++]);
				convocacao.setEmail((String) obj[k++]);
				convocacao.setCelular((String) obj[k++]);
				convocacao.setDataNascimento((Date) obj[k++]);
				convocacao.setCidadeNascimento((String) obj[k++]);
				convocacao.setEstadoNascimento((String) obj[k++]);
				convocacao.setPaisNascimento((String) obj[k++]);
				convocacao.setSexo((Character) obj[k++]);
				convocacao.setEstadoCivil((String) obj[k++]);
				convocacao.setNomePai((String) obj[k++]);
				convocacao.setNomeMae((String) obj[k++]);
				convocacao.setCpf((BigInteger) obj[k++]);
				convocacao.setRg((String) obj[k++]);
				convocacao.setOrgaoRG((String) obj[k++]);
				convocacao.setEstadoRG((String) obj[k++]);
				convocacao.setTituloEleitorNumero((String) obj[k++]);
				convocacao.setTituloEleitorSecao((String) obj[k++]);
				convocacao.setTituloEleitoZona((String) obj[k++]);
				convocacao.setTituloEleitoEstado((String) obj[k++]);
				convocacao.setNomeEscola((String) obj[k++]);
				convocacao.setAnoConclusao((String) obj[k++]);
				convocacao.setSemestreAprocacao((Integer) obj[k++]);
				convocacao.setMunicipioCurso((String) obj[k++]);
				convocacao.setConcurso((String) obj[k++]);
				convocacao.setIdCandidato((Integer) obj[k++]);
				convocacao.setArgumentoFinal((Double) obj[k++]);
				convocacao.setListagem((String) obj[k++]);
				convocacao.setSemestreMaximoConclusao((Integer) obj[k++]);
				convocacao.setClassificacaoCandidato((Integer) obj[k++]);
				convocacao.setMatrizCurricular((Integer) obj[k++]);
				convocacao.setTipoConvocacao((Integer) obj[k++]);
				convocacao.setGrupoCota((String) obj[k++]);
				convocacao.setDentroNumeroVagas((Boolean) obj[k++]);
				convocacao.setGrupoCotaInscricao((String) obj[k++]);
				if (mapListaResultadoArgumento.get(convocacao.getClassificacaoCandidato()+"_"+1) != null)
					convocacao.getResultado1Fase().addAll( mapListaResultadoArgumento.get(convocacao.getClassificacaoCandidato()+"_"+1) );//findResultadosArgumentoByResultadoClassificacaoAndFase(listaResultadoArgumento, convocacao.getClassificacaoCandidato(), 1));
				if (mapListaResultadoArgumento.get(convocacao.getClassificacaoCandidato()+"_"+2) != null)
					convocacao.getResultado2Fase().addAll( mapListaResultadoArgumento.get(convocacao.getClassificacaoCandidato()+"_"+2) );//findResultadosArgumentoByResultadoClassificacaoAndFase(listaResultadoArgumento, convocacao.getClassificacaoCandidato(), 2));
				lista.add(convocacao);
			}
		}
		return lista;
	}
	
	
	/** Retorna os dados para o preenchimento do relatório de efetivação de cadastro.
	 * @param idsMatrizCurricular
	 * @param idDiscente
	 * @param idProcessoSeletivo
	 * @param idEfetivacaoCadastro
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaImpressaoDocumentosConvocados> findAllEfetivacaoCadastro(List<Integer> idsMatrizCurricular, int idProcessoSeletivo, int idEfetivacaoCadastro) throws DAOException {
		String projecao = "c.nome as curso,"
				+ " ha.nome as habilitacao,"
				+ " ga.descricao as descricao,"
				+ " t.sigla as sigla,"
				+ " d.matricula as matricula,"
				+ " d.periodo_ingresso as semestreAprocacao,"
				+ " iv.numero_inscricao as numeroInscricao,"
				+ " pv.nome as nome,"
				+ " m2.nome as municipioCurso,"
				+ " ps.nome as concurso,"
				+ " iv.id_inscricao_vestibular as idCandidato,"
				+ " roc.classificacao as classificacaoCandidato,"
				+ " mc.id_matriz_curricular as matrizCurricular,"
				+ " cpsd.tipo as tipoConvocacao,"
				+ " gcvc.descricao||' - '||gcvc.descricao_detalhada as grupoCota,"
				+ " cpsd.dentro_numero_vagas as dentroNumeroVagas,"
				+ " gcvci.descricao as grupoCotaInscricao,"
				+ " roc.classificacao as classificacaoCandidato";
		StringBuilder sql = new StringBuilder("SELECT ").append(projecao)
				.append(" from vestibular.convocacao_processo_seletivo_discente cpsd" +
					" inner join vestibular.convocacao_efetivada_cadastramento_reserva cecr using (id_convocacao_processo_seletivo_discente)" +
					" inner join vestibular.convocacao_processo_seletivo cps using (id_convocacao_processo_seletivo) " +
					" inner join vestibular.processo_seletivo ps using (id_processo_seletivo) " +
					" inner join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
					" left join ensino.grupo_cota_vaga_curso gcvci using (egresso_escola_publica, pertence_grupo_etnico, baixa_renda_familiar) " +
					" inner join vestibular.resultado_classificacao_candidato rcc ON (iv.id_inscricao_vestibular = rcc.id_inscricao_vestibular) " +
					" inner join vestibular.resultado_opcao_curso roc ON  (rcc.id_resultado_classificacao_candidato = roc.id_resultado_classificacao_candidato )" +
					" inner join discente d ON (cpsd.id_discente = d.id_discente) " +
					" inner join graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
					" inner join graduacao.matriz_curricular mc on (dg.id_matriz_curricular = mc.id_matriz_curricular) " +
					" inner join curso c ON (mc.id_curso = c.id_curso) " +
					" inner join ensino.turno t ON (mc.id_turno = t.id_turno)" +
					" left join ensino.grupo_cota_vaga_curso gcvc on (gcvc.id_grupo_cota_vaga_curso = cpsd.id_grupo_cota_vaga_curso) " +
					" left join ensino.grau_academico ga ON (mc.id_grau_academico = ga.id_grau_academico) " +
					" left join graduacao.habilitacao ha ON (mc.id_habilitacao = ha.id_habilitacao) " +
					" left join comum.pessoa pv ON (d.id_pessoa = pv.id_pessoa) " +
					" left join comum.municipio m2 ON (c.id_municipio = m2.id_municipio) ");
		sql.append(" WHERE cecr.id_efetivacao_cadastramento_reserva = :idEfetivacaoCadastro"+
	  				 ( !ValidatorUtil.isEmpty( idsMatrizCurricular) ? " AND mc.id_matriz_curricular IN " + gerarStringIn(idsMatrizCurricular) : "") +
	  				 " AND ps.id_processo_seletivo = :processoSeletivo");
	  	sql.append( " order by municipioCurso, curso, habilitacao, descricao, sigla, grupoCota, dentroNumeroVagas desc, nome");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("processoSeletivo", idProcessoSeletivo);
		q.setInteger("idEfetivacaoCadastro", idEfetivacaoCadastro);
		@SuppressWarnings("unchecked")
		Collection<LinhaImpressaoDocumentosConvocados> lista = HibernateUtils.parseTo(q.list(), projecao, LinhaImpressaoDocumentosConvocados.class);
		return lista;
	}
	
	/** Retorna os dados para o preenchimento do relatório de efetivação de cadastro.
	 * @param idsMatrizCurricular
	 * @param idDiscente
	 * @param idProcessoSeletivo
	 * @param idEfetivacaoCadastro
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaImpressaoDocumentosConvocados> findAllCadastroReserva(List<Integer> idsMatrizCurricular, int idProcessoSeletivo) throws DAOException {
		String projecao = "c.nome as curso,"
				+ " ha.nome as habilitacao,"
				+ " ga.descricao as descricao,"
				+ " t.sigla as sigla,"
				+ " d.matricula as matricula,"
				+ " d.periodo_ingresso as semestreAprocacao,"
				+ " iv.numero_inscricao as numeroInscricao,"
				+ " pv.nome as nome,"
				+ " m2.nome as municipioCurso,"
				+ " ps.nome as concurso,"
				+ " iv.id_inscricao_vestibular as idCandidato,"
				+ " roc.classificacao as classificacaoCandidato,"
				+ " mc.id_matriz_curricular as matrizCurricular,"
				+ " cpsd.tipo as tipoConvocacao,"
				+ " gcvc.descricao||' - '||gcvc.descricao_detalhada as grupoCota,"
				+ " cpsd.dentro_numero_vagas as dentroNumeroVagas,"
				+ " gcvci.descricao as grupoCotaInscricao,"
				+ " roc.classificacao as classificacaoCandidato";
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(projecao)
			.append(" from vestibular.convocacao_processo_seletivo_discente cpsd" +
				" inner join vestibular.convocacao_processo_seletivo cps using (id_convocacao_processo_seletivo) " +
				" inner join vestibular.processo_seletivo ps using (id_processo_seletivo) " +
				" inner join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
				" left join ensino.grupo_cota_vaga_curso gcvci using (egresso_escola_publica, pertence_grupo_etnico, baixa_renda_familiar) " +
				" inner join vestibular.resultado_classificacao_candidato rcc ON (iv.id_inscricao_vestibular = rcc.id_inscricao_vestibular) " +
				" inner join vestibular.resultado_opcao_curso roc ON  (rcc.id_resultado_classificacao_candidato = roc.id_resultado_classificacao_candidato )" +
				" inner join discente d ON (cpsd.id_discente = d.id_discente) " +
				" inner join graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
				" inner join graduacao.matriz_curricular mc on (dg.id_matriz_curricular = mc.id_matriz_curricular) " +
				" inner join curso c ON (mc.id_curso = c.id_curso) " +
				" inner join ensino.turno t ON (mc.id_turno = t.id_turno)" +
				" left join ensino.grupo_cota_vaga_curso gcvc on (gcvc.id_grupo_cota_vaga_curso = cpsd.id_grupo_cota_vaga_curso) " +
				" left join ensino.grau_academico ga ON (mc.id_grau_academico = ga.id_grau_academico) " +
				" left join graduacao.habilitacao ha ON (mc.id_habilitacao = ha.id_habilitacao) " +
				" left join comum.pessoa pv ON (d.id_pessoa = pv.id_pessoa) " +
				" left join comum.municipio m2 ON (c.id_municipio = m2.id_municipio) "+
				" WHERE d.status = ").append(StatusDiscente.PRE_CADASTRADO)
				.append(
  				 ( !ValidatorUtil.isEmpty( idsMatrizCurricular) ? " AND mc.id_matriz_curricular IN " + gerarStringIn(idsMatrizCurricular) : "") +
  				 " AND ps.id_processo_seletivo = :processoSeletivo");
	  	sql.append( " order by municipioCurso, curso, habilitacao, descricao, sigla, grupoCota, dentroNumeroVagas desc, nome");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("processoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		Collection<LinhaImpressaoDocumentosConvocados> lista = HibernateUtils.parseTo(q.list(), projecao, LinhaImpressaoDocumentosConvocados.class);
		return lista;
	}
	
	/** Retorna os dados para o preenchimento do relatório de efetivação de cadastro.
	 * @param idsMatrizCurricular
	 * @param idDiscente
	 * @param idProcessoSeletivo
	 * @param idEfetivacaoCadastro
	 * @param idTipoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaImpressaoDocumentosConvocados> findAllDiscentesCadastrados(List<Integer> idsMatrizCurricular, int idProcessoSeletivo, int idConvocacao) throws DAOException {
		String projecao = "c.nome as curso,"
				+ " ha.nome as habilitacao,"
				+ " ga.descricao as descricao,"
				+ " t.sigla as sigla,"
				+ " d.matricula as matricula,"
				+ " d.periodo_ingresso as semestreAprocacao,"
				+ " iv.numero_inscricao as numeroInscricao,"
				+ " pv.nome as nome,"
				+ " m2.nome as municipioCurso,"
				+ " ps.nome as concurso,"
				+ " iv.id_inscricao_vestibular as idCandidato,"
				+ " roc.classificacao as classificacaoCandidato,"
				+ " mc.id_matriz_curricular as matrizCurricular,"
				+ " cpsd.tipo as tipoConvocacao,"
				+ " gcvc.descricao||' - '||gcvc.descricao_detalhada as grupoCota,"
				+ " cpsd.dentro_numero_vagas as dentroNumeroVagas,"
				+ " gcvci.descricao as grupoCotaInscricao,"
				+ " roc.classificacao as classificacaoCandidato";
		StringBuilder sql = new StringBuilder("SELECT ").append(projecao)
				.append(" from vestibular.convocacao_processo_seletivo_discente cpsd" +
				" inner join vestibular.convocacao_processo_seletivo cps using (id_convocacao_processo_seletivo) " +
				" inner join vestibular.processo_seletivo ps using (id_processo_seletivo) " +
				" inner join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
				" left join ensino.grupo_cota_vaga_curso gcvci using (egresso_escola_publica, pertence_grupo_etnico, baixa_renda_familiar) " +
				" inner join vestibular.resultado_classificacao_candidato rcc ON (iv.id_inscricao_vestibular = rcc.id_inscricao_vestibular) " +
				" inner join vestibular.resultado_opcao_curso roc ON  (rcc.id_resultado_classificacao_candidato = roc.id_resultado_classificacao_candidato )" +
				" inner join discente d ON (cpsd.id_discente = d.id_discente) " +
				" inner join graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
				" inner join graduacao.matriz_curricular mc on (dg.id_matriz_curricular = mc.id_matriz_curricular) " +
				" inner join curso c ON (mc.id_curso = c.id_curso) " +
				" inner join ensino.turno t ON (mc.id_turno = t.id_turno)" +
				" left join ensino.grupo_cota_vaga_curso gcvc on (gcvc.id_grupo_cota_vaga_curso = cpsd.id_grupo_cota_vaga_curso) " +
				" left join ensino.grau_academico ga ON (mc.id_grau_academico = ga.id_grau_academico) " +
				" left join graduacao.habilitacao ha ON (mc.id_habilitacao = ha.id_habilitacao) " +
				" left join comum.pessoa pv ON (d.id_pessoa = pv.id_pessoa) " +
				" left join comum.municipio m2 ON (c.id_municipio = m2.id_municipio) "+
				" WHERE d.status in ").append(UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo()))
				.append(
  				 ( !ValidatorUtil.isEmpty( idsMatrizCurricular) ? " AND mc.id_matriz_curricular IN " + gerarStringIn(idsMatrizCurricular) : "") +
  				 " AND ps.id_processo_seletivo = :processoSeletivo"+
  				 " AND cps.id_convocacao_processo_seletivo = :idConvocacao" +
  				 " AND cpsd.dentro_numero_vagas = true");
	  	sql.append( " order by municipioCurso, curso, habilitacao, descricao, sigla, grupoCota, dentroNumeroVagas desc, nome");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("processoSeletivo", idProcessoSeletivo);
		q.setInteger("idConvocacao", idConvocacao);
		@SuppressWarnings("unchecked")
		Collection<LinhaImpressaoDocumentosConvocados> lista = HibernateUtils.parseTo(q.list(), projecao, LinhaImpressaoDocumentosConvocados.class);
		return lista;
	}
	
	/**
	 * Carrega as provas e os argumentos do discente convocado.
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResultadoArgumento> findByArgumentoCandidatoFase(int idCandidato, int fase) throws DAOException {
		try {
			String hql = "select new ResultadoArgumento(ra.id, ra.descricaoProva, ra.argumento, ra.fase) " +
					" from ResultadoArgumento ra " +
					" where ra.resultadoClassificacaoCandidato.inscricaoVestibular.id = :idCandidato" +
					" and  ra.fase = :fase";
			Query q = getSession().createQuery(hql);
			q.setInteger("idCandidato", idCandidato);
			q.setInteger("fase", fase);

			return q.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/** Carrega uma coleção com todos os resultados de argumento dos candidatos convocados, para ser inserida ao objeto de convocação. 
	 * @param idsMatrizCurricular
	 * @param idDiscente
	 * @param idProcessoSeletivo
	 * @param idConvocacao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<String, Collection<ResultadoArgumento>> findResultadoArgumentoCandidatosConvocados(List<Integer> idsMatrizCurricular, Integer idDiscente, int idProcessoSeletivo, int idConvocacao) throws HibernateException, DAOException{
		StringBuilder sql = new StringBuilder("select rcc.id_resultado_classificacao_candidato, " +
				" ra.id_resultado_argumento, ra.descricao_prova, ra.argumento, ra.fase " +
				" from vestibular.resultado_argumento ra" +
				" inner join vestibular.resultado_classificacao_candidato rcc using (id_resultado_classificacao_candidato)" +
				" inner join vestibular.inscricao_vestibular iv using(id_inscricao_vestibular)" +
				" inner join vestibular.convocacao_processo_seletivo_discente cpsd using(id_inscricao_vestibular)" +
				" inner join vestibular.convocacao_processo_seletivo cps Using(id_convocacao_processo_seletivo)" +
				" inner join discente d using(id_discente)" +
				" inner join graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
				" WHERE cps.id_convocacao_processo_seletivo = :convocacaoProcessoSeletivo"+
				( !ValidatorUtil.isEmpty( idsMatrizCurricular) ? " AND dg.id_matriz_curricular IN " + gerarStringIn(idsMatrizCurricular)  : "") +
				( idDiscente != null ? " AND d.id_discente = " + idDiscente : "") +
  				" AND cps.id_processo_seletivo = :processoSeletivo"+
				" order by iv.id_inscricao_vestibular, rcc.id_resultado_classificacao_candidato, ra.fase, ra.descricao_prova");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setInteger("processoSeletivo", idProcessoSeletivo);
		q.setInteger("convocacaoProcessoSeletivo", idConvocacao);
		@SuppressWarnings("unchecked")
		Collection<Object[]> bulk = q.list();
		Collection<ResultadoArgumento> lista = new ArrayList<ResultadoArgumento>();
		
		if (bulk != null) {
			for (Object[] obj : bulk){
				ResultadoArgumento resultadoArgumento = new ResultadoArgumento();
				resultadoArgumento.setResultadoClassificacaoCandidato( new ResultadoClassificacaoCandidato((Integer) obj[0]) );
				resultadoArgumento.setId((Integer) obj[1]);
				resultadoArgumento.setDescricaoProva((String) obj[2]);
				resultadoArgumento.setArgumento((Double) obj[3]);
				resultadoArgumento.setFase((Integer) obj[4]);
				lista.add(resultadoArgumento);
			}
		}	
		
		Map<String, Collection<ResultadoArgumento>> map = new HashMap<String, Collection<ResultadoArgumento>>(); 
		Integer idResultadoClassificacaoCandidato = 0;
		Integer fase = 0;
		Collection<ResultadoArgumento> listaResultado = new ArrayList<ResultadoArgumento>();
		int listaSize = lista.size();
		int k = 0;
		for (Iterator<ResultadoArgumento> it = lista.iterator(); it.hasNext();) {
			ResultadoArgumento ra = it.next();
			if ( fase != ra.getFase() ){
				if ( listaResultado.size() > 0 )
					map.put(idResultadoClassificacaoCandidato+"_"+fase, listaResultado);
				fase = ra.getFase();
				listaResultado = new ArrayList<ResultadoArgumento>();
			}	
			if ( idResultadoClassificacaoCandidato != ra.getResultadoClassificacaoCandidato().getId() ){
				idResultadoClassificacaoCandidato = ra.getResultadoClassificacaoCandidato().getId();
			}	
			listaResultado.add(ra);
			if( listaResultado.contains(ra) )
				it.remove();
			k++;
			if ( k == listaSize ){
				map.put(idResultadoClassificacaoCandidato+"_"+fase, listaResultado);
			}
		}
		return map;
	}

}