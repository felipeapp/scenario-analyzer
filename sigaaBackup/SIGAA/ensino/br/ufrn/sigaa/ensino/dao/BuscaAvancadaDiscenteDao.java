package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoENADE;
import br.ufrn.sigaa.ensino.util.ParametrosBusca;
import br.ufrn.sigaa.ensino.util.RestricoesBusca;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;

/**
 * Dao utilizado pela busca avançada de discentes.
 * 
 * @author wendell
 *
 */
public class BuscaAvancadaDiscenteDao extends GenericSigaaDAO {

	/**
	 * Busca os discentes com base nas restrições definidas pelo usuário.
	 * As restrições indicam quais parâmetros de busca foram passados, como matrícula, nome, nível, entre outros. Ver mais restrições em {@link RestricoesBusca}
	 * 
	 * @see RestricoesBusca, ParametrosBusca
	 * @param niveis
	 * @param restricoes
	 * @param parametros
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> buscaAvancadaDiscente(char[] niveis, RestricoesBusca restricoes, ParametrosBusca parametros, boolean relatorio) throws DAOException {

		String count = "select count(*) ";
		String hql = "select d.id, d.matricula, d.pessoa.nome, d.status, c.id, c.nome, c.nivel ";

		StringBuilder sb = criarCorpoConsulta(niveis, restricoes, parametros);
		
		Long total = (Long) getHibernateTemplate().uniqueResult(count + sb);

		if (total > 5000 && !relatorio) {
			throw new LimiteResultadosException("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
		}

		Query q = getSession().createQuery( hql.toString() + sb.toString() + " order by d.pessoa.nome asc" );
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		
		ArrayList<Discente> discentes = new ArrayList<Discente>();
		int i = 0;
		for( Object[] linha : resultado ){
			i = 0;
			Discente d = new Discente();
			d.setId( (Integer) linha[i++] );
			d.setMatricula( (Long) linha[i++] );
			d.setPessoa(new Pessoa());
			d.getPessoa().setNome((String) linha[i++]);
			d.setStatus((Integer) linha[i++]);
			d.setCurso(new Curso());
			Integer idCurso = (Integer) linha[i++];
			if( idCurso != null ){
				d.getCurso().setNome((String) linha[i++]);
				d.getCurso().setNivel((Character) linha[i++]);
			}
			discentes.add(d);
		}
		
		return discentes;
	}
	
	/**
	 * Exporta os dados da busca para o formato CSV
	 *  
	 * @see RestricoesBusca, ParametrosBusca
	 * @param niveis
	 * @param restricoes
	 * @param parametros
	 * @return
	 * @throws ArqException 
	 */
	public String exportarDadosBusca(char[] niveis, RestricoesBusca restricoes, ParametrosBusca parametros, boolean relatorio) throws ArqException {
		
			String count = "select count(*) ";
			// Ao alterar a projeção precisa modificar o método criarCSV para tratar o status do discente
			String projecao =  "select d.matricula as Matrícula, d.pessoa.nome as Nome, c.nome as Curso, d.status as Status ";
	
			StringBuilder sb = criarCorpoConsulta(niveis, restricoes, parametros);

			Long total = (Long) getHibernateTemplate().uniqueResult(count + sb);
	
			if (total > 5000 && !relatorio) {
				throw new LimiteResultadosException("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
			}
			
			Query q = getSession().createQuery( projecao.toString() + sb.toString() + " order by d.pessoa.nome asc" );
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			String csv = criarCSV(projecao, resultado);
			return csv;
	}
	
	/**
	 * Cria o CSV a partir de uma string com a projeção da consulta e a lista de objetos resultantes da consulta.
	 * O cabeçalho do CSV gerado a partir da projeção considera tanto alias como apenas '.'.
	 * Este método não pode ir pra arquitetura pois possui uma regra específica para tratar a situação dos discentes.
	 *  
	 * @param projecao
	 * @param resultado
	 * @return
	 */
	private String criarCSV(String projecao, List<Object[]> resultado) {

		StringBuffer csv = new StringBuffer();
		
		if (!isEmpty(projecao)){
			// Monta o cabeçalho CSV considerando Alias
			String[] cabecalho = projecao.split(",");
			for (int i = 0; i < cabecalho.length; i++ ){
				
				String campo = cabecalho[i];
				campo = campo.replace("."," ");
				
				int ultEsp = StringUtils.buscaUltimoEspacoEmBranco(campo);
				
				// Trata espaços em branco no final do campo
				while (ultEsp == campo.length()-1){
					if (campo.length()==0)
						break;
					campo = campo.substring(0, campo.length()-1);
					ultEsp = StringUtils.buscaUltimoEspacoEmBranco(campo);
				}
				
				campo = campo.substring(ultEsp+1, campo.length());
				csv.append(campo + ";");
			}
			csv.append("\n");
		}
		
		for (Object[] linha : resultado){
			for (int i = 0; i < linha.length; i++ ){	
				
				Object ob = linha[i];
				String campo = "";		
					
				if (ob != null && ob instanceof Date){
					campo = Formatador.getInstance().formatarData((Date) ob);
				}else if (ob != null)
					campo = ob.toString();
				
				// Esta comparação é específica para a consulta.
				if (i==3){
					int status = (Integer) ob;
					campo = StatusDiscente.getDescricao(status);
				}
				
				if (campo != null) {
					campo = campo.replace(";", " ");
					campo = campo.replace("\n", " ");
					campo = campo.replace("\r", " ");
				} else {
					campo = "";
				}
				csv.append(campo + ";");
			}
			csv.append("\n");
		}
		
		return csv.toString();
	}

	/**
	 * Criar o corpo do hql da consulta de busca.
	 * @see RestricoesBusca, ParametrosBusca
	 * @param niveis
	 * @param restricoes
	 * @param parametros
	 * @return
	 * @throws DAOException
	 */
	private StringBuilder criarCorpoConsulta(char[] niveis,
			RestricoesBusca restricoes, ParametrosBusca parametros)
			throws DAOException {
		StringBuilder sb = new StringBuilder("from Discente d left join d.curso c ");

		if (restricoes.isBuscaNivel()) {

			if (parametros.getNivel() == NivelEnsino.GRADUACAO) {
				sb.append(", DiscenteGraduacao dg where d.id = dg.id and d.nivel = 'G' ");

				if (restricoes.isBuscaCentro()) {
					sb.append(" and c.unidade.id = " + parametros.getCentro().getId());
				}
				if (restricoes.isBuscaCursoGraduacao()) {
					sb.append(" and c.id = " + parametros.getCursoGraduacao().getId());
				}
				if (restricoes.isBuscaMatriz()) {
					sb.append("  and dg.matrizCurricular.id = " + parametros.getMatrizCurricular().getId());
				}
				if (restricoes.isBuscaCurriculo()) {
					sb.append("  and dg.discente.curriculo.codigo = '" + parametros.getCurriculo().getCodigo() + "'");
				}
				if (restricoes.isBuscaTurno()) {
					sb.append(" and dg.matrizCurricular.turno.id = " + parametros.getTurno().getId());
				}
				if (restricoes.isBuscaModalidade()) {
					sb.append(" and c.modalidadeEducacao.id = " + parametros.getModalidade().getId());
				}
				if (restricoes.isBuscaPrazoMaximo()) {
					sb.append(" and d.prazoConclusao = " + parametros.getPrazoMaximoAno() + parametros.getPrazoMaximoPeriodo());
				}
				if (restricoes.isBuscaPolo()) {
					sb.append(" and dg.polo.id = " + parametros.getPolo().getId());
				}
				if (restricoes.isBuscaParticipacaoEnade()) {
					ParticipacaoEnade participacao = findAndFetch(parametros.getParticipacaoEnade().getId(), ParticipacaoEnade.class, "tipoEnade");
					if (participacao.getTipoEnade() == TipoENADE.INGRESSANTE)
						sb.append(" and dg.participacaoEnadeIngressante.id = " + parametros.getParticipacaoEnade().getId());
					else
						sb.append(" and dg.participacaoEnadeConcluinte.id = " + parametros.getParticipacaoEnade().getId());
				}
				if (restricoes.isBuscaNoPeriodo()) {
					sb.append(" and d.periodoAtual = " + parametros.getNoPeriodo());
				}
				
				if (restricoes.isBuscaConvenio()) {
					if ( isEmpty(parametros.getConvenio()) )
						sb.append(" and c.convenio.id is null ");
					else
						sb.append(" and c.convenio.id = " + parametros.getConvenio().getId());
				}
				
				if (restricoes.isBuscaDesconsiderarApostilamentos()) {
					sb.append(" and not exists(select ma from MovimentacaoAluno ma where ma.apostilamento = trueValue() and ma.discente.id = dg.id and ma.ativo = trueValue()) ");
				}
				
				if (restricoes.isBuscaOpcaoAprovacao() && parametros.getOpcaoAprovacao() != null) {
					sb.append(" and dg.id in ("
						+ " SELECT sd.id"
						+ " FROM ConvocacaoProcessoSeletivoDiscente cpsd"
						+ " INNER JOIN cpsd.discente sd"
						+ " INNER JOIN cpsd.inscricaoVestibular iv"
						+ " INNER JOIN iv.processoSeletivo ps"
						+ " WHERE sd.discente.anoIngresso = ps.anoEntrada");
					if (parametros.getOpcaoAprovacao() == 1)
						sb.append(" AND sd.matrizCurricular.id = iv.opcoesCurso[0].id");
					else {
						sb.append(" AND sd.matrizCurricular.id != iv.opcoesCurso[0].id");
						sb.append(" AND sd.matrizCurricular.id = iv.opcoesCurso["+(parametros.getOpcaoAprovacao() - 1)+"].id");
					}
					sb.append(" AND cpsd.id NOT IN ("
						+ "    SELECT cc.convocacao.id"
						+ "      FROM CancelamentoConvocacao cc)"
						+ " AND cpsd.id NOT IN ("
						+ "    SELECT cpsd.id"
						+ "      FROM ConvocacaoProcessoSeletivoDiscente cpsd"
						+ "     WHERE tipo = "+TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE.ordinal()+")"
						+ ")");
				}


			} else if (parametros.getNivel() == NivelEnsino.TECNICO) {
				sb.append(", DiscenteTecnico dt where d.id = dt.id and d.nivel = 'T' ");

				if (restricoes.isBuscaEscola()) {
					sb.append(" and c.unidade.id = " + parametros.getEscola().getId());
				}
				if (restricoes.isBuscaCursoTecnico()) {
					sb.append(" and c.id = " + parametros.getCursoTecnico().getId());
				}
				if (restricoes.isBuscaTurmaEntrada()) {
					sb.append(" and dt.turmaEntradaTecnico.id = " + parametros.getTurmaEntrada().getId());
				}
				if (restricoes.isBuscaEspecialidade()) {
					sb.append(" and dt.turmaEntradaTecnico.especializacao.id = " + parametros.getEspecialidade().getId());
				}

			} else if (parametros.getNivel() == NivelEnsino.LATO) {
				sb.append(", DiscenteLato dl where d.id = dl.id and d.nivel = 'L' ");

				if (restricoes.isBuscaCursoLato()) {
					sb.append(" and c.id = " + parametros.getCursoLato().getId());
				}

			} else if (NivelEnsino.isAlgumNivelStricto(parametros.getNivel())) {
				if (parametros.getNivel() == NivelEnsino.STRICTO) {
					sb.append(", DiscenteStricto ds where d.id = ds.id and d.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()));
				} else {
					sb.append(", DiscenteStricto ds where d.id = ds.id and d.nivel = '" + parametros.getNivel() + "' ");
				}

				if (restricoes.isBuscaCurriculoStricto()) {
					sb.append("  and ds.discente.curriculo.id = '" + parametros.getCurriculo().getId() + "'");
				}				
				
				if (restricoes.isBuscaPrograma()) {
					sb.append(" and d.gestoraAcademica.id = " + parametros.getPrograma().getId());
				}
				if (restricoes.isBuscaPrevisaoQualificacao()) {
					sb.append(" and exists (select mc from MatriculaComponente mc where mc.componente.tipoAtividade.id = "
									+ TipoAtividade.QUALIFICACAO
									+ " and mc.discente.id = d.id and mc.situacaoMatricula.id = 2)");
				}
				if (restricoes.isBuscaPrevisaoDefesa()) {
					sb.append(" and exists (select mc from MatriculaComponente mc where mc.componente.tipoAtividade.id = "
									+ TipoAtividade.TESE
									+ " and mc.discente.id = d.id and mc.situacaoMatricula.id = 2)");
				}
				if (restricoes.isBuscaPrazoEsgotado()) {
					sb.append(" and d.status = 1 and ds.prazoMaximoConclusao < now()");
				}
				if (restricoes.isBuscaPrazoASerEsgotado()) {
					sb.append(" and d.status = 1 and ds.prazoMaximoConclusao <= '"
									+ new SimpleDateFormat("yyyy-MM-dd").format(parametros.getPrazoEsgotadoAte()) + "'");
				}
				if (restricoes.isBuscaPrazoProrrogado()) {
					sb.append(" and exists(select ma from MovimentacaoAluno ma where ma.discente.id = ds.id and ma.tipoMovimentacaoAluno.grupo = 'PR' and ma.ativo=trueValue()) ");
				}
				if (restricoes.isBuscaDiscentesHomologados()) {
					sb.append(" and d.status = 11");
				}

				if (restricoes.isBuscaDiscentesDefenderamNaoHomologados()) {
					sb.append(" and d.status not in (3, 11) and exists (select bp from BancaPos bp where bp.dadosDefesa.discente.id = ds.id and bp.tipoBanca = 2) ");
				}

			} else {
				sb.append(" where 1 = 1 ");
			}

		} else {
			sb.append(" where 1 = 1 ");
		}

		if (niveis != null) {
			sb.append(" and d.nivel in " + UFRNUtils.gerarStringIn(niveis));
		}

		if (restricoes.isBuscaTipo()) {
			sb.append(" and d.tipo = " + parametros.getTipo());
		}
		if (restricoes.isBuscaMatricula()) {
			sb.append(" and d.matricula = " + parametros.getMatricula());
		}
		if (restricoes.isBuscaNome()) {
			sb.append(" and "
					+ UFRNUtils.toAsciiUpperUTF8("d.pessoa.nomeAscii")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8("'" + UFRNUtils.trataAspasSimples(parametros.getNome()) + "%'"));
		}
		if (restricoes.isBuscaStatus()){
			
			if(parametros.getStatus() == StatusDiscente.ATIVO) {
				sb.append(" and d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()));
			} else {
				sb.append(" and d.status = " + parametros.getStatus());
			}
			
		} else {
			sb.append(" and d.status != " + StatusDiscente.EXCLUIDO);
		}
		
		prepararRestricoesIndiceAcademico(restricoes, parametros, sb);
		
		if (restricoes.isBuscaFormaIngresso()) {
			sb.append(" and d.formaIngresso.id = " + parametros.getFormaIngresso().getId());
		}
		if (restricoes.isBuscaIdade()) {
			sb.append(" and d.pessoa.dataNascimento >= '" + parametros.getNascimentoDe() + "' and d.pessoa.dataNascimento <= '" + parametros.getNascimentoAte() + "'");
		}
		if (restricoes.isBuscaSexo() && !isEmpty(parametros.getSexo())) {
			sb.append(" and d.pessoa.sexo = '" + parametros.getSexo() + "'");
		}
		if (restricoes.isBuscaAnoIngresso()) {
			sb.append(" and d.anoIngresso = " + parametros.getAnoIngresso());
		}
		if (restricoes.isBuscaPeriodoIngresso()) {
			sb.append(" and d.periodoIngresso = " + parametros.getPeriodoIngresso());
		}

		prepararRestricoesSaida(restricoes, parametros, sb);
		
		if (restricoes.isBuscaMatriculadosEm()) {
			
				sb.append(" and ( exists (SELECT mc FROM MatriculaComponente mc " 
						+ " WHERE " 
						+ " mc.ano = " + parametros.getMatriculadoEmAno() 
						+ (parametros.getMatriculadoEmPeriodo() != null ? " and mc.periodo = " + parametros.getMatriculadoEmPeriodo() : "")
						+ " and mc.discente.id = d.id and mc.situacaoMatricula.id in (2, 3, 4, 5, 6, 7, 21, 22, 23)) ");	
				
				sb.append(" OR exists (select rap from RenovacaoAtividadePos rap " 
						+ " LEFT JOIN rap.matricula matRenovacao LEFT JOIN rap.solicitacaoMatricula solRenovacao LEFT JOIN solRenovacao.matriculaGerada solMatGerada " 
						+ " WHERE "
						+ (parametros.getMatriculadoEmAno() != null ? " rap.ano = " + parametros.getMatriculadoEmAno() +" and": "") 
						+ (parametros.getMatriculadoEmPeriodo() != null ? " rap.periodo = " + parametros.getMatriculadoEmPeriodo() +" and": "") 			
						+ " ((matRenovacao.discente.id = d.id "
							+ " and matRenovacao.situacaoMatricula.id in " + gerarStringIn(new int[] {2, 3, 4, 5, 6, 7}) + ")"
						+ " or (solRenovacao.matriculaGerada.discente.id = d.id"
							+ " and solMatGerada.situacaoMatricula.id in " + gerarStringIn(new int[] {2, 3, 4, 5, 6, 7}) + ")))  )");	
				
		}
		
		if (restricoes.isBuscaNaoMatriculadosEm()) {
			sb.append(" and not exists (select mc from MatriculaComponente mc " 
					  		+ " where mc.discente.id = d.id" 
							+ (parametros.getNaoMatriculadoEmAno() != null ? " and mc.ano = " + parametros.getNaoMatriculadoEmAno() : "") 
							+ (parametros.getNaoMatriculadoEmPeriodo() != null ? " and mc.periodo = " + parametros.getNaoMatriculadoEmPeriodo() : "") 			
					  		+ " and mc.situacaoMatricula.id in (2, 3, 4, 5, 6, 7, 21, 22, 23))");
			sb.append(" and not exists (select rap from RenovacaoAtividadePos rap where "
							+ (parametros.getNaoMatriculadoEmAno() != null ? " rap.ano = " + parametros.getNaoMatriculadoEmAno() +" and": "") 
							+ (parametros.getNaoMatriculadoEmPeriodo() != null ? " rap.periodo = " + parametros.getNaoMatriculadoEmPeriodo() +" and": "") 			
							+ " ((rap.matricula.discente.id = d.id "
								+ " and rap.matricula.situacaoMatricula.id in " + gerarStringIn(new int[] {2, 3, 4, 5, 6, 7}) + ")"
							+ " or (rap.solicitacaoMatricula.matriculaGerada.discente.id = d.id"
								+ " and rap.solicitacaoMatricula.matriculaGerada.situacaoMatricula.id in " + gerarStringIn(new int[] {2, 3, 4, 5, 6, 7}) + ")))");			
		}

		if (restricoes.isBuscaTrancadosNoPeriodo()) {
			sb.append(" and exists (select ma from MovimentacaoAluno ma"
							+ " where ma.discente.id = d.id"
							+ (parametros.getTrancadoNoAno() != null ? " and ma.anoReferencia = " + parametros.getTrancadoNoAno() : "") 
							+ (parametros.getTrancadoNoPeriodo() != null ? " and ma.periodoReferencia = " + parametros.getTrancadoNoPeriodo() : "") 					
							+ " and ma.ativo = trueValue() and ma.tipoMovimentacaoAluno.id = "+TipoMovimentacaoAluno.TRANCAMENTO+")");
		}

		if (restricoes.isBuscaCidade()) {
			sb.append(" and d.pessoa.municipio.id = " + parametros.getCidade().getId());
		}
		if (restricoes.isBuscaEstado()) {
			sb.append(" and d.pessoa.municipio.unidadeFederativa.id = " + parametros.getEstado().getId());
		}
		
		if (restricoes.isBuscaTipoNecessidadeEspecial()) {
			
			if (ValidatorUtil.isEmpty(parametros.getTipoNecessidadeEspecial())) {

				sb.append(" and d.pessoa.tipoNecessidadeEspecial.id in (select tne.id from TipoNecessidadeEspecial tne)");
				
			} else {
				sb.append(" and d.pessoa.tipoNecessidadeEspecial.id = " + parametros.getTipoNecessidadeEspecial().getId());
			}
		}
		return sb;
	}

	/**
	 * Este método prepara os critérios de busca quando o filtro selecionado é por índice academico.
	 * @param restricoes
	 * @param parametros
	 * @param sb
	 */
	private void prepararRestricoesIndiceAcademico(RestricoesBusca restricoes, ParametrosBusca parametros, StringBuilder sb) {
		if (restricoes.isBuscaIndiceAcademico()) {
			StringBuilder expressao = new StringBuilder();
			for (int i = 0; i < parametros.getIndicesAcademicos().length(); i++) {
				if (parametros.getIndicesAcademicos().charAt(i) == '(') {
					expressao.append(" " + parametros.getIndicesAcademicos().charAt(i) + " ");
				} else if (parametros.getIndicesAcademicos().charAt(i) == ')') {
					expressao.append(" " + parametros.getIndicesAcademicos().charAt(i) + " ");
				} else if ((parametros.getIndicesAcademicos().charAt(i) == '>' || parametros.getIndicesAcademicos().charAt(i) == '<') && parametros.getIndicesAcademicos().charAt(i+1) != '=') {
					expressao.append(" " + parametros.getIndicesAcademicos().charAt(i) + " ");
				} else if ((parametros.getIndicesAcademicos().charAt(i) == '>' || parametros.getIndicesAcademicos().charAt(i) == '<') && parametros.getIndicesAcademicos().charAt(i+1) == '=') {
					expressao.append(" " + parametros.getIndicesAcademicos().charAt(i));
				} else if (parametros.getIndicesAcademicos().charAt(i) == '=' && (parametros.getIndicesAcademicos().charAt(i-1) == '<' || parametros.getIndicesAcademicos().charAt(i-1) == '>')) {
					expressao.append(parametros.getIndicesAcademicos().charAt(i) + " ");
				} else if (parametros.getIndicesAcademicos().charAt(i) == '=' && (parametros.getIndicesAcademicos().charAt(i-1) != '<' && parametros.getIndicesAcademicos().charAt(i-1) != '>')) {
					expressao.append(" " + parametros.getIndicesAcademicos().charAt(i) + " ");
				} else {
					expressao.append(parametros.getIndicesAcademicos().charAt(i));
				}
			}
			
			String expressaoCorrigida = StringUtils.removeEspacosRepetidos(expressao.toString());
			StringBuilder indices = new StringBuilder();
			String[] partes = expressaoCorrigida.split(" ");
			for (String parte : partes) {
				if ( parte.equals("(") || parte.equals(")") || parte.equals(">")|| parte.equals("=") || parte.equals("<") || parte.equals(">=") || parte.equals("<=") ) {
					indices.append(" " + parte +" ");
				} else if ( "E".equalsIgnoreCase(parte) ) {
					indices.append(" INTERSECT ");
				} else if ( "OU".equalsIgnoreCase(parte) ) {
					indices.append(" UNION ");
				} else if ( StringUtils.isAlpha(parte) ) {
					indices.append(
							"(select iad.id_discente from ensino.indice_academico_discente iad " +
							"   join ensino.indice_academico ia on (iad.id_indice_academico = ia.id) " +
							"   join discente d using (id_discente) " +
							"   left join curso c on (c.id_curso = d.id_curso) " +
							" where upper(ia.sigla) = upper('" + parte +"') ");
					if (restricoes.isBuscaCursoGraduacao()) 
						indices.append("   and c.id_curso = " + parametros.getCursoGraduacao().getId());
					if (restricoes.isBuscaNivel())
						indices.append("   and d.nivel = '" + parametros.getNivel() + "'");
					if (restricoes.isBuscaStatus())
						indices.append("   and d.status = " + parametros.getStatus());
					
					indices.append(" 	and iad.valor ");					
				} else if (StringUtils.isDouble(parte.replace(",", "."))) {
					indices.append(" " + parte.replace(",", ".") + " )");
				}
			}
			
			@SuppressWarnings("unchecked")
			List<Integer> ids = getJdbcTemplate().queryForList(indices.toString(), Integer.class);
			if (!isEmpty(ids)) {
				sb.append(" and (d.id in " + UFRNUtils.gerarStringIn(ids) + ") ");
			} else {
				sb.append(" and ( d.id = 0 ) ");
			}
		}
	}

	/**
	 * Adiciona de forma agrupada as restrições de saída selecionadas.
	 * 
	 * @param restricoes
	 * @param parametros
	 * @param sb
	 */
	private void prepararRestricoesSaida(RestricoesBusca restricoes, ParametrosBusca parametros, StringBuilder sb) {
		if ( restricoes.isBuscaTipoSaida() || restricoes.isBuscaAnoSaida() || restricoes.isBuscaPeriodoSaida() ) {
			sb.append(" and exists (select ma.id FROM MovimentacaoAluno ma " +
					" WHERE ma.discente.id = d.id " +
					" AND ma.ativo = trueValue() " +
					" AND ma.dataRetorno is null " +
					" AND ma.tipoMovimentacaoAluno.grupo = '" + GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE + "'" +
					" AND ma.id not in (select ema.movimentacao.id from EstornoMovimentacaoAluno ema) ");
			
			if (restricoes.isBuscaTipoSaida()) {
				sb.append(" AND ma.tipoMovimentacaoAluno.id = " + parametros.getTipoSaida().getId() );
			}
			if (restricoes.isBuscaAnoSaida()) {
				sb.append(" AND ma.anoReferencia = " + parametros.getAnoSaida());
			}
			if (restricoes.isBuscaPeriodoSaida()) {
				sb.append(" AND ma.periodoReferencia = " + parametros.getPeriodoSaida());
			}
			
			sb.append(" )");
		}
		
	}
}