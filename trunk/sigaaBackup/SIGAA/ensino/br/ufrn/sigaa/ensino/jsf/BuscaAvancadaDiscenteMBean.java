/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/03/2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.TipoMovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dao.BuscaAvancadaDiscenteDao;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.util.ParametrosBusca;
import br.ufrn.sigaa.ensino.util.RestricoesBusca;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * MBean responsável por realizar uma busca abrangente de discente, 
 * em todos os níveis de ensino
 * 
 * @author David Pereira
 *
 */
@Component
@Scope("session")
public class BuscaAvancadaDiscenteMBean extends SigaaAbstractController<Discente> {

	/** Indica os níveis da realização da busca. */
	private char[] niveis;
	/** Armazena as restrições utilizadas na busca */
	private RestricoesBusca restricoes = new RestricoesBusca();
	/** Parâmetros utilizados na busca */
	private ParametrosBusca parametros = new ParametrosBusca();
	/** Forma de geração da busca se vai ser no formato relatório */
	private boolean relatorio;
	/** Informa se geração da busca vai ser no formato CSV */
	private boolean gerarCSV;
	/** Armazena uma lista com os discente encontrados. */
	private List<Discente> discentesEncontrados;
	/** Mapa utilizado para controle das restrições e parâmetros utilizados no relatório.*/
	private LinkedHashMap<String, Object> mapRestricoes = new LinkedHashMap<String, Object>();
	
	/**
	 * Construtor padrão
	 */
	public BuscaAvancadaDiscenteMBean() {
		parametros.setAnoIngresso(CalendarUtils.getAnoAtual());
		parametros.setPeriodoIngresso(getPeriodoAtual());
		parametros.setPrazoMaximoAno(CalendarUtils.getAnoAtual());
		parametros.setPrazoMaximoPeriodo(getPeriodoAtual());
		parametros.setMatriculadoEmAno(CalendarUtils.getAnoAtual());
		parametros.setMatriculadoEmPeriodo(getPeriodoAtual());
		parametros.setTrancadoNoAno(CalendarUtils.getAnoAtual());
		parametros.setTrancadoNoPeriodo(getPeriodoAtual());
	}

	/**
	 * Inicia a busca para graduação setando os parâmetros que são particulares deste nível.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarCoordGraduacao() {
		niveis = new char[] { NivelEnsino.GRADUACAO };
		return iniciar();
	}
	
	/**
	 * Inicia a busca para stricto setando os parâmetros que são particulares deste nível.<br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarCoordStricto() {
		niveis = new char[] { NivelEnsino.STRICTO, NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO };
		return iniciar();
	}
	
	/**
	 * Inicia a busca para Nível Técnico setando os parâmetros que são particulares deste nível.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarTecnico() {
		niveis = new char[] { NivelEnsino.TECNICO };
		return iniciar();
	}
	
	/**
	 * Inicia os parâmetros comum a todos os níveis e redireciona para tela de busca.<br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/consultas.jsp</li>
	 * 	<li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciar() {
		restricoes = new RestricoesBusca();
		parametros = new ParametrosBusca();
		discentesEncontrados = new ArrayList<Discente>();
		relatorio = false;
		gerarCSV = false;
		
		if (niveis != null && niveis.length == 1) {
			restricoes.setBuscaNivel(true);
			parametros.setNivel(niveis[0]);
		}
		
		return forward("/ensino/discente/busca.jsf");
	}
	
	/**
	 * Executa a busca.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public String buscar() throws ArqException {
		BuscaAvancadaDiscenteDao dao = getDAO(BuscaAvancadaDiscenteDao.class);
		clearMensagens();
		
		discentesEncontrados = new ArrayList<Discente>();
		try {
			
			validarRestricoes();
			
			if ( hasErrors() ) return null;
			
			discentesEncontrados = dao.buscaAvancadaDiscente(niveis, restricoes, parametros, relatorio);
			if (isEmpty(discentesEncontrados))
				addMensagemWarning("Nenhum discente encontrado para os parâmetros informados.");
		} catch(BadSqlGrammarException e) {
			addMensagemErro("Expressão de índices acadêmicos inválida.");
		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
		}
		
		if (gerarCSV)
			exportarDiscentes();
			
		if (relatorio) {
			popularRestricoesRelatorio();
			return forward("/ensino/discente/relatorio.jsp");
		}	
		
		return forward("/ensino/discente/busca.jsp");
	}
	
	/**
	 * Exporta os Planos Individuais de Docentes no formado CSV.<br />
	 * Método não chamado por JSP(s):
	 * 
	 * @throws ArqException
	 */
	private void exportarDiscentes() throws ArqException {
		
		BuscaAvancadaDiscenteDao dao = getDAO(BuscaAvancadaDiscenteDao.class);
		clearMensagens();
		
		try {
					
			String dados = null;
			dados = dao.exportarDadosBusca(niveis, restricoes, parametros, relatorio);
			
			if (isEmpty(dados)){
				addMensagemWarning("Nenhum discente encontrado para os parâmetros informados.");
				return;
			}	
			
			String nomeArquivo =  "consulta_geral_discente.csv";
			getCurrentResponse().setCharacterEncoding("iso-8859-15");
			getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
			PrintWriter out = getCurrentResponse().getWriter();
			out.println(dados);
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch(BadSqlGrammarException e) {
			addMensagemErro("Expressão de índices acadêmicos inválida.");
		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
		} catch (IOException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/** Método responsável por validar as restrições solicitadas para a construção do relatório. */
	private void validarRestricoes(){
		mapRestricoes = new LinkedHashMap<String, Object>();
		
		if ( restricoes.isBuscaMatricula() 	&& isEmpty(parametros.getMatricula()) ) mapRestricoes.put( "Matrícula", parametros.getMatricula() );
		if ( restricoes.isBuscaNome() 		&& isEmpty(parametros.getNome()) ) 		mapRestricoes.put( "Nome", parametros.getNome() );
		if ( restricoes.isBuscaIdade()		&& (parametros.getIdadeDe() == null) ) 	mapRestricoes.put( "Idade de", parametros.getIdadeDe() );
		if ( restricoes.isBuscaIdade()		&& (parametros.getIdadeAte() == null) ) mapRestricoes.put( "Idade até", parametros.getIdadeAte() );
		if ( restricoes.isBuscaTipo() 		&& isEmpty(parametros.getTipo()) ) 		mapRestricoes.put( "Tipo", parametros.getTipo() );
		if ( restricoes.isBuscaNivel() 		&& parametros.getNivel() == '0' ) 		mapRestricoes.put( "Nível de Ensino", parametros.getNivelDesc() );
		if ( restricoes.isBuscaStatus() 	&& isEmpty(parametros.getStatus()) )	mapRestricoes.put( "Status", parametros.getStatusDesc() );
		if ( restricoes.isBuscaFormaIngresso() 		&& isEmpty(parametros.getFormaIngresso()) ) 	mapRestricoes.put( "Forma de Ingresso", parametros.getFormaIngresso() );
		if ( restricoes.isBuscaAnoIngresso() 		&& isEmpty(parametros.getAnoIngresso()) ) 		mapRestricoes.put( "Ano Ingresso", parametros.getAnoIngresso() );
		if ( restricoes.isBuscaPeriodoIngresso()	&& isEmpty(parametros.getPeriodoIngresso()) )  	mapRestricoes.put( "Período Ingresso", parametros.getPeriodoIngresso() );
		if ( restricoes.isBuscaTipoSaida() 			&& isEmpty(parametros.getTipoSaida()) ) 	 	mapRestricoes.put( "Tipo de Saída", parametros.getTipoSaida() );
		if ( restricoes.isBuscaAnoSaida() 			&& isEmpty(parametros.getAnoSaida()) ) 	 		mapRestricoes.put( "Ano Saída", parametros.getAnoSaida() );
		if ( restricoes.isBuscaPeriodoSaida() 		&& isEmpty(parametros.getPeriodoSaida()) ) 		mapRestricoes.put( "Período Saída", parametros.getPeriodoSaida() );
		if ( restricoes.isBuscaMatriculadosEm() 	&& isEmpty(parametros.getMatriculadoEmAno()) ) 	mapRestricoes.put( "Matriculados Em", parametros.getMatriculadoEmAno() );
		if ( restricoes.isBuscaNaoMatriculadosEm()	&& isEmpty(parametros.getNaoMatriculadoEmAno()))mapRestricoes.put( "Não Matriculados Em", parametros.getNaoMatriculadoEmAno() );
		if ( restricoes.isBuscaTrancadosNoPeriodo()	&& isEmpty(parametros.getTrancadoNoAno()) )		mapRestricoes.put( "Trancados Em", parametros.getTrancadoNoAno() );
		if ( restricoes.isBuscaEstado() 			&& isEmpty(parametros.getEstado()) ) 			mapRestricoes.put( "Estado", parametros.getEscola() );
		if ( restricoes.isBuscaCidade() 			&& isEmpty(parametros.getCidade()) ) 			mapRestricoes.put( "Cidade", parametros.getCidade() );
		
		if ( restricoes.isBuscaCentro() 		&& isEmpty(parametros.getCentro()) ) 			mapRestricoes.put( "Centro", parametros.getCentro() );
		if ( restricoes.isBuscaCursoGraduacao() && isEmpty( parametros.getCursoGraduacao()) )   mapRestricoes.put( "Curso", parametros.getCursoGraduacao() );
		if ( restricoes.isBuscaMatriz() 		&& isEmpty(parametros.getMatrizCurricular()) )	mapRestricoes.put( "Matriz Curricular", parametros.getMatrizCurricular() );
		if(NivelEnsino.isAlgumNivelStricto(parametros.getNivel()))
			if ( restricoes.isBuscaCurriculo() 	&& isEmpty(parametros.getCurriculo()) ) 	 	mapRestricoes.put( "Currículo", parametros.getCurriculo() );
		if(NivelEnsino.isGraduacao(parametros.getNivel()))
			if ( restricoes.isBuscaCurriculo() 	&& isEmpty(parametros.getCurriculo().getCodigo()) )mapRestricoes.put( "Currículo", parametros.getCurriculo() );
		if ( restricoes.isBuscaNoPeriodo() 		&& isEmpty(parametros.getNoPeriodo()) ) 		mapRestricoes.put( "No período", parametros.getNoPeriodo() );
		if ( restricoes.isBuscaTurno() 			&& isEmpty(parametros.getTurno()) ) 			mapRestricoes.put( "Turno", parametros.getTurno() );
		if ( restricoes.isBuscaModalidade() 	&& isEmpty(parametros.getModalidade()) ) 		mapRestricoes.put( "Modalidade", parametros.getModalidade() );
		if ( restricoes.isBuscaPrazoMaximo() 	&& isEmpty(parametros.getPrazoMaximoAno()) ) 	mapRestricoes.put( "Prazo Máximo de Conclusão", parametros.getPrazoMaximoAno()+ "" + (parametros.getPrazoMaximoPeriodo() != null ? "."+parametros.getPrazoMaximoPeriodo() : "") );
		// if ( restricoes.isBuscaConvenio() 		&& isEmpty(parametros.getConvenio()) ) 	 		mapRestricoes.put( "Convênio", parametros.getConvenio() );
		if ( restricoes.isBuscaPolo() 			&& isEmpty(parametros.getPolo()) ) 				mapRestricoes.put( "Pólo", parametros.getPolo() );
		if ( restricoes.isBuscaParticipacaoEnade() && isEmpty(parametros.getParticipacaoEnade()) )mapRestricoes.put( "Participacao ENADE", parametros.getParticipacaoEnade() );
		
		if ( restricoes.isBuscaEscola() 		&& isEmpty(parametros.getEscola()) ) 			mapRestricoes.put( "Escola", parametros.getEscola() );
		if ( restricoes.isBuscaCursoTecnico() 	&& isEmpty(parametros.getCursoTecnico()) ) 		mapRestricoes.put( "Curso", parametros.getCursoTecnico() );
		if ( restricoes.isBuscaTurmaEntrada() 	&& isEmpty(parametros.getTurmaEntrada()) ) 		mapRestricoes.put( "Turma de Entrada", parametros.getTurmaEntrada() );
		if ( restricoes.isBuscaEspecialidade() 	&& isEmpty(parametros.getEspecialidade()) ) 	mapRestricoes.put( "Especialidade", parametros.getEspecialidade() );
		
		if ( restricoes.isBuscaCursoLato() 		&& isEmpty(parametros.getCursoLato()) )	 		mapRestricoes.put( "Curso", parametros.getCursoLato() );
		
		if ( restricoes.isBuscaPrograma() 			&& isEmpty(parametros.getPrograma()) ) 			mapRestricoes.put( "Programa", parametros.getPrograma() );
		if ( restricoes.isBuscaPrazoASerEsgotado() 	&& isEmpty(parametros.getPrazoEsgotadoAte()) )	mapRestricoes.put( "Com prazo a ser esgotado em", "" );
		
		if ( mapRestricoes.size() > 0 ){
			for (String restricaoValidada : mapRestricoes.keySet()) {
				erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, restricaoValidada);
			}
		} 
		if ( restricoes.isBuscaIdade() && ( (parametros.getIdadeAte() != null && parametros.getIdadeDe() != null)
				&& (parametros.getIdadeAte() < parametros.getIdadeDe()) ) )  {
			erros.addErro("Idade: Não é possível realizar a consulta, pois a idade inicial é maior que a final." );
		}	
	}
	
	/** Método responsável por preencher um mapa com as restrições e parâmetros utilizados na busca avançada do discente.*/
	private void popularRestricoesRelatorio() throws DAOException{
		
		mapRestricoes = new LinkedHashMap<String, Object>();
		
		if ( restricoes.isBuscaMatricula() ) 		mapRestricoes.put( "Matrícula", parametros.getMatricula() );
		if ( restricoes.isBuscaNome() ) 	 		mapRestricoes.put( "Nome", parametros.getNome() );
		if ( restricoes.isBuscaIdade()) 	 		mapRestricoes.put( "Idade", "de "+parametros.getIdadeDe()+" até "+parametros.getIdadeAte() );
		if ( restricoes.isBuscaSexo() ) 	 		mapRestricoes.put( "Sexo", parametros.getSexo() == null ? "Ambos" : parametros.getSexo().equals("F") ? "Feminino" : "Masculino");
		if ( restricoes.isBuscaTipo() ) 	 		mapRestricoes.put( "Tipo", parametros.getTipo() == 1 ? "Regular" : "Especial" );
		if ( restricoes.isBuscaNivel() ) 	 		mapRestricoes.put( "Nível", parametros.getNivelDesc() );
		if ( restricoes.isBuscaStatus() ) 	 		mapRestricoes.put( "Status", parametros.getStatusDesc() );
		if ( restricoes.isBuscaFormaIngresso() ) 	mapRestricoes.put( "Forma de Ingresso", getFormaIngressoDesc() );
		if ( restricoes.isBuscaAnoIngresso() ) 	 	mapRestricoes.put( "Ano Ingresso", parametros.getAnoIngresso() );
		if ( restricoes.isBuscaPeriodoIngresso() )  mapRestricoes.put( "Período Ingresso", parametros.getPeriodoIngresso() );
		if ( restricoes.isBuscaTipoSaida() ) 	 	mapRestricoes.put( "Tipo de Saída", getTipoSaidaDesc() );
		if ( restricoes.isBuscaAnoSaida() ) 	 	mapRestricoes.put( "Ano Saída", parametros.getAnoSaida() );
		if ( restricoes.isBuscaPeriodoSaida() ) 	mapRestricoes.put( "Período Saída", parametros.getPeriodoSaida() );
		if ( restricoes.isBuscaMatriculadosEm() ) 	mapRestricoes.put( "Matriculados Em", parametros.getMatriculadoEmAno() + "" + (parametros.getMatriculadoEmPeriodo() != null ? "."+parametros.getMatriculadoEmPeriodo() : "") );
		if ( restricoes.isBuscaNaoMatriculadosEm()) mapRestricoes.put( "Não Matriculados Em", parametros.getNaoMatriculadoEmAno() + "" + (parametros.getNaoMatriculadoEmPeriodo() != null ? "."+parametros.getNaoMatriculadoEmPeriodo() : "") );
		if ( restricoes.isBuscaTrancadosNoPeriodo())mapRestricoes.put( "Trancados Em", parametros.getTrancadoNoAno() + "" + (parametros.getTrancadoNoPeriodo() != null ? "."+parametros.getTrancadoNoPeriodo() : "") );
		if ( restricoes.isBuscaEstado() ) 	 		mapRestricoes.put( "Estado", getEstadoDesc() );
		if ( restricoes.isBuscaCidade() ) 	 		mapRestricoes.put( "Cidade", getCidadeDesc() );
		
		if ( restricoes.isBuscaCentro() ) 	 		mapRestricoes.put( "Centro", getCentroDesc() );
		if ( restricoes.isBuscaCursoGraduacao() )   mapRestricoes.put( "Curso", getCursoGraduacaoDesc() );
		if ( restricoes.isBuscaMatriz() ) 	 		mapRestricoes.put( "Matriz Curricular", getMatrizCurricularDesc() );
		if ( restricoes.isBuscaCurriculo() ) 	 	mapRestricoes.put( "Currículo", parametros.getCurriculo() );
		if ( restricoes.isBuscaNoPeriodo() ) 		mapRestricoes.put( "No período", parametros.getNoPeriodo() );
		if ( restricoes.isBuscaTurno() ) 	 		mapRestricoes.put( "Turno", getTurnoDesc() );
		if ( restricoes.isBuscaModalidade() ) 	 	mapRestricoes.put( "Modalidade", getModalidadeDesc() );
		if ( restricoes.isBuscaPrazoMaximo() ) 	 	mapRestricoes.put( "Prazo Máximo de Conclusão", parametros.getPrazoMaximoAno()+ "" + (parametros.getPrazoMaximoPeriodo() != null ? "."+parametros.getPrazoMaximoPeriodo() : "") );
		if ( restricoes.isBuscaConvenio() ) 	 	mapRestricoes.put( "Convênio", getConvenioDesc() );
		if ( restricoes.isBuscaPolo() ) 	 		mapRestricoes.put( "Pólo", getPoloDesc() );
		if ( restricoes.isBuscaParticipacaoEnade() ) mapRestricoes.put( "Participacao ENADE", getParticipacaoEnadDesc() );
		if ( restricoes.isBuscaDesconsiderarApostilamentos() ) mapRestricoes.put( "Desconsiderar Apostilamentos", "" );
		
		if ( restricoes.isBuscaEscola() ) 	 		mapRestricoes.put( "Escola", getEscolaDesc() );
		if ( restricoes.isBuscaCursoTecnico() ) 	mapRestricoes.put( "Curso", getCursoTecnicoDesc() );
		if ( restricoes.isBuscaTurmaEntrada() ) 	mapRestricoes.put( "Turma de Entrada", getTurmaEntradaDesc() );
		if ( restricoes.isBuscaEspecialidade() ) 	mapRestricoes.put( "Especialidade", getEspecialidadeDesc() );
		
		if ( restricoes.isBuscaCursoLato() ) 	 	mapRestricoes.put( "Curso", getCursoLatoDesc() );
		
		if ( restricoes.isBuscaPrograma() ) 			mapRestricoes.put( "Programa", getProgramaDesc() );
		if ( restricoes.isBuscaPrevisaoQualificacao() ) mapRestricoes.put( "Com previsão de qualificação", "" );
		if ( restricoes.isBuscaPrevisaoDefesa() ) 	 	mapRestricoes.put( "Com previsão de defesa", "" );
		if ( restricoes.isBuscaPrazoEsgotado() ) 	 	mapRestricoes.put( "Com prazo esgotado", "" );
		if ( restricoes.isBuscaPrazoASerEsgotado() ) 	mapRestricoes.put( "Com prazo a ser esgotado em " + parametros.getPrazoEsgotadoAte(), "" );
		if ( restricoes.isBuscaPrazoProrrogado() ) 	 	mapRestricoes.put( "Com prazo prorrogado", "" );
		if ( restricoes.isBuscaDiscentesHomologados() ) mapRestricoes.put( "Discentes com diplomas homologados pela PPg e ainda não formados", "" );
		if ( restricoes.isBuscaDiscentesDefenderamNaoHomologados() ) mapRestricoes.put( "Discentes que defenderam mas não foram homologados", "" );
		
	}
	
	/**
	 * Verifica se no resultado da busca há o nome do curso, caso o nome do curso esteja presente será 
	 * exibido uma coluna com o nome do mesmo, caso contrário a coluna não será exibida.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public boolean getNomeCurso(){
		for (Discente disc : discentesEncontrados) {
			if (disc.getCurso().getNomeCursoStricto() != null && !disc.getCurso().getNomeCursoStricto().equals("")) 
				return true;
				break;
		}
		return false;
	}
	
	/**
	 * Abre o histórico do discente selecionado.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String detalhesDiscente() throws ArqException, NegocioException {
		Integer id = getParameterInt("id");
		if (id == null) throw new NegocioException("Nenhum discente foi selecionado");
		
		Discente discente = getGenericDAO().findByPrimaryKey(id, Discente.class);
		
		HistoricoMBean historico = new HistoricoMBean();
		historico.setDiscente(discente);
		return historico.selecionaDiscente();
	}
	
	public List<Discente> getDiscentesEncontrados() {
		return discentesEncontrados;
	}

	public Map<String, Object> getMapRestricoes() {
		return mapRestricoes;
	}

	public char[] getNiveis() {
		return niveis;
	}

	public void setNiveis(char[] niveis) {
		this.niveis = niveis;
	}
	
	public boolean isRelatorio() {
		return relatorio;
	}

	public void setRelatorio(boolean relatorio) {
		this.relatorio = relatorio;
	}

	/**
	 * Monta combo com os níveis que usuário logado tem acesso.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see NivelEnsino
	 * @return
	 */
	public SelectItem[] getNiveisCombo() {
		if (isEmpty(niveis)) {
			return NivelEnsino.getNiveisCombo();
		} else {
			SelectItem[] items = new SelectItem[niveis.length];
			int i = 0;
			for (char nivel : niveis) {
				items[i++] = new SelectItem(nivel, NivelEnsino.getDescricao(nivel));
			}
			return items;
		}
	}
	
	/**
	 * Monta combo com os estados do país.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see UnidadeFederativa
	 * @return
	 */
	public Collection<SelectItem> getEstados() {
		return getAll(UnidadeFederativa.class, "id", "descricao");
	}

	/**
	 * Monta combo com as cidades do estado selecionado.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see Municipio
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCidades() throws DAOException {
		if (parametros.getEstado().getId() == 0)
			return new ArrayList<SelectItem>();
		return toSelectItems(getDAO(MunicipioDao.class).findByUF(parametros.getEstado().getId()), "id", "nome");
	}
	
	/**
	 * Monta combo com os tipos de necessidade especial.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTiposNecessidadeEspecial() throws DAOException {
		return getAll(TipoNecessidadeEspecial.class, "id", "descricao");
	}

	/**
	 * Monta combo com as matrizes do curso selecionado.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see MatrizCurricular
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMatrizesCurso() throws DAOException {
		if (parametros.getCursoGraduacao().getId() == 0)
			return new ArrayList<SelectItem>();
		return toSelectItems(getGenericDAO().findByExactField(MatrizCurricular.class, "curso.id", parametros.getCursoGraduacao().getId()), "id", "descricao");
	}
	
	/**
	 * Monta combo com as turmas de entrada do técnico.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see TurmaEntradaTecnico
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTurmasEntrada() throws DAOException {
		if (parametros.getEscola().getId() == 0)
			return new ArrayList<SelectItem>();
		return toSelectItems(getGenericDAO().findByExactField(TurmaEntradaTecnico.class, "unidade.id", parametros.getEscola().getId()), "id", "descricao");
	}
	
	/**
	 * Monta combo com os tipos de especialidade.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see EspecializacaoTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getEspecialidades() throws DAOException {
		if (parametros.getEscola().getId() == 0)
			return new ArrayList<SelectItem>();
		return toSelectItems(getGenericDAO().findByExactField(EspecializacaoTurmaEntrada.class, "unidade.id", parametros.getEscola().getId()), "id", "descricao");
	}
	
	/**
	 * Monta combo com os tipos de ingresso.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see FormaIngresso
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFormasIngresso() throws DAOException {
		return toSelectItems(getGenericDAO().findByExactField(FormaIngresso.class, "ativo", true), "id", "descricao");
	}
	
	/**
	 * Monta combo com os tipos de saída.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see TipoMovimentacaoAluno
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTiposSaida() throws DAOException {
		return toSelectItems(getDAO(TipoMovimentacaoAlunoDao.class).findAtivos(true), "id", "descricao");
	}
	
	/**
	 * Monta combo com os convênios.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @see ConvenioAcademico
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getConvenios() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(ConvenioAcademico.class, "descricao", "asc"), "id", "descricao");
	}
	


	public RestricoesBusca getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(RestricoesBusca restricoes) {
		this.restricoes = restricoes;
	}



	public ParametrosBusca getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosBusca parametros) {
		this.parametros = parametros;
	}

	/**
	 * Gera descrição formatada de forma de ingresso.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getFormaIngressoDesc() throws DAOException {
		FormaIngresso forma = getGenericDAO().findByPrimaryKey(parametros.getFormaIngresso().getId(), FormaIngresso.class);
		if (forma != null)
			return forma.getDescricao();
		else
			return "";
	}
	
	/**
	 * Gera descrição formatada de tipo de saída.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getTipoSaidaDesc() throws DAOException {
		TipoMovimentacaoAluno tipo = getGenericDAO().findByPrimaryKey(parametros.getTipoSaida().getId(), TipoMovimentacaoAluno.class);
		if (tipo != null)
			return tipo.getDescricao();
		else
			return "";
	}

	/**
	 * Gera descrição formatada de estados.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getEstadoDesc() throws DAOException {
		UnidadeFederativa obj = getGenericDAO().findByPrimaryKey(parametros.getEstado().getId(), UnidadeFederativa.class);
		if (obj != null)
			return obj.getDescricao();
		else
			return "";
	}
	
	/**
	 * Gera descrição formatada de turnos.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getTurnoDesc() throws DAOException {
		Turno obj = getGenericDAO().findByPrimaryKey(parametros.getTurno().getId(), Turno.class);
		if (obj != null)
			return obj.getDescricao();
		else
			return "";
	}
	
	/**
	 * Gera descrição formatada de modalidades.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getModalidadeDesc() throws DAOException {
		ModalidadeEducacao obj = getGenericDAO().findByPrimaryKey(parametros.getModalidade().getId(), ModalidadeEducacao.class);
		if (obj != null)
			return obj.getDescricao();
		else
			return "";
	}
	
	/**
	 * Gera descrição formatada dos programas.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getProgramaDesc() throws DAOException {
		Unidade obj = getGenericDAO().findByPrimaryKey(parametros.getPrograma().getId(), Unidade.class);
		if (obj != null)
			return obj.getNome();
		else
			return "";
	}
	
	/**
	 * Gera descrição formatada de cidades.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCidadeDesc() throws DAOException {
		if (parametros.getCidade().getId() > 0) {
			Municipio obj = getGenericDAO().findByPrimaryKey(parametros.getCidade().getId(), Municipio.class);
			if (obj != null)
				return obj.getNome();
		} 
		
		return "";
	}

	/**
	 * Gera descrição formatada de Centros.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCentroDesc() throws DAOException {
		Unidade obj = getGenericDAO().findByPrimaryKey(parametros.getCentro().getId(), Unidade.class);
		if (obj != null)
			return obj.getNome();
		else
			return "";
	}
	
	/**
	 * Gera descrição formatada de cursos de graduação.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCursoGraduacaoDesc() throws DAOException {
		Curso obj = getGenericDAO().findByPrimaryKey(parametros.getCursoGraduacao().getId(), Curso.class);
		if (obj != null)
			return obj.getDescricao();
		else return "";
	}
	
	/**
	 * Gera descrição formatada de cursos de lato.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCursoLatoDesc() throws DAOException {
		Curso obj = getGenericDAO().findByPrimaryKey(parametros.getCursoLato().getId(), Curso.class);
		if (obj != null)
			return obj.getDescricao();
		else return "";
	}
	
	/**
	 * Gera descrição formatada das unidades do tipo escola.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getEscolaDesc() throws DAOException {
		Unidade obj = getGenericDAO().findByPrimaryKey(parametros.getEscola().getId(), Unidade.class);
		if (obj != null)
			return obj.getNome();
		else return "";
	}
	
	/**
	 * Gera descrição formatada de curso de técnico.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCursoTecnicoDesc() throws DAOException {
		Curso obj = getGenericDAO().findByPrimaryKey(parametros.getCursoTecnico().getId(), Curso.class);
		if (obj != null)
			return obj.getDescricao();
		else return "";
	}
	
	/**
	 * Gera descrição formatada de convênios.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getConvenioDesc() throws DAOException {
		ConvenioAcademico obj = getGenericDAO().findByPrimaryKey(parametros.getConvenio().getId(), ConvenioAcademico.class);
		if (obj != null)
			return obj.getDescricao();
		else return "";
	}
	
	/**
	 * Gera descrição formatada de participação do enad.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getParticipacaoEnadDesc() throws DAOException {
		return "todo";
	}
	
	/**
	 * Gera descrição formatada dos pólos.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getPoloDesc() throws DAOException {
		Polo obj = getGenericDAO().findByPrimaryKey(parametros.getPolo().getId(), Polo.class);
		if (obj != null)
			return obj.getDescricao();
		else return "";
	}
	
	/**
	 * Gera descrição formatada das turmas de entrada.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getTurmaEntradaDesc() throws DAOException {
		if (parametros.getTurmaEntrada().getId() > 0) {
			TurmaEntradaTecnico obj = getGenericDAO().findByPrimaryKey(parametros.getTurmaEntrada().getId(), TurmaEntradaTecnico.class);
			if (obj != null)
				return obj.getDescricao();
		}
		
		return "";
	}
	
	/**
	 * Gera descrição formatada das Especializações das turmas de entrada.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getEspecialidadeDesc() throws DAOException {
		if (parametros.getEspecialidade().getId() > 0) {
			EspecializacaoTurmaEntrada obj = getGenericDAO().findByPrimaryKey(parametros.getEspecialidade().getId(), EspecializacaoTurmaEntrada.class);
			if (obj != null)
				return obj.getDescricao();
		}
		
		return "";
	}
	
	/**
	 * Gera descrição formatada de matrizes curriculares.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getMatrizCurricularDesc() throws DAOException {
		if (parametros.getMatrizCurricular().getId() > 0) {
			MatrizCurricular obj = getGenericDAO().findByPrimaryKey(parametros.getMatrizCurricular().getId(), MatrizCurricular.class);
			if (obj != null)
				return obj.getDescricao();
		}
		
		return "";
	}
	
	/**
	 * Monta combo com os currículos strictosomenteAtivos.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/discente/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCurriculosStricto() throws DAOException {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		
		Collection<Curriculo> curriculos = null;
		
		if (parametros.getPrograma().getId() != 0)
			curriculos = dao.findCompleto(null, null, parametros.getPrograma().getId(), null);
		
		return toSelectItems(curriculos, "id", "descricaoCompleta");
	}
	
	/** Retorna uma coleção de selectItem de unidades do tipo unidade acadêmica especializada.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAllEscolaCombo() throws DAOException {
		UnidadeDao uniDao = getDAO(UnidadeDao.class);
		Collection<Unidade> allEscola = uniDao.findByTipoUnidadeAndGestoraAcademica(
				TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA, 
				getUsuarioLogado().getVinculoAtivo().getUnidade().getGestoraAcademica() != null ?
					getUsuarioLogado().getVinculoAtivo().getUnidade().getGestoraAcademica().getId() :
					getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
		Collection<SelectItem> allEscolaCombo = toSelectItems(allEscola, "id", "nome");
		if(allEscolaCombo.size() == 1)
			parametros.setEscola(allEscola.iterator().next());
		return allEscolaCombo;
	}
	
	public void setGerarCSV(boolean gerarCSV) {
		this.gerarCSV = gerarCSV;
	}

	public boolean isGerarCSV() {
		return gerarCSV;
	}
	
}

