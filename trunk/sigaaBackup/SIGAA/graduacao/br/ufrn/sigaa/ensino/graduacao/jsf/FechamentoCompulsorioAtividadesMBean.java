/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.ensino.FechamentoCompulsorioDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;

/**
 * MBean usado para fechar compulsoriamente tividade
 * @author Diego Jácome
 */
@Component("fechamentoCompulsorioAtividades")
@Scope("request")
public class FechamentoCompulsorioAtividadesMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {

	/** Serão buscadas matrículas abertas de atividades individuais inferiores ou iguais a este ano. */
	private Integer ano;
	/** Serão buscadas matrículas abertas de atividades individuais inferiores ou iguais a este período. */
	private Integer periodo;
	/** Lista de matrículas que podem ser canceladas. */
	private ArrayList<MatriculaComponente> matriculas;
	/** Lista de matrículas que escolhidas para ser canceladas. */
	private ArrayList<MatriculaComponente> matriculasEscolhidas;
	/**Utilizados como parâmetro da busca a ser realizada.*/
	private Boolean ead = false;
	
	/**
	 * Inicia o caso de uso<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/administracao.jsp</li>
     * </ul>
	 * @throws ArqException 
	 */
	public String iniciar () throws ArqException {
		return forward("/graduacao/fechamento_compulsorio/listar_matriculas.jsp");
	}
	
	/**
	 * Reinicia o caso de uso após carregar o ano e período<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/fechamento_compulsorio/listar_matriculas.jsp</li>
     * </ul>
	 */
	public String filtrar () throws ArqException {
		
		if (ano == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (periodo == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		if (ead == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Modalidade");
		
		if (hasErrors())
			return null;
		
		FechamentoCompulsorioDao fdao = null;

		try {
			fdao = getDAO(FechamentoCompulsorioDao.class);
			matriculas = fdao.findMatriculasFechamentoByAnoPeriodo(ano,periodo,ead,null);
		} finally {
			if (fdao!=null)
				fdao.close();
		}

		if (isEmpty(matriculas)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return getListPage();
	}
		
	/**
	 * Prepara o movimento e lista todas as matrículas a serem canceladas. <br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/fechamento_compulsorio/listar_matriculas.jsp</li>
     * </ul>
	 */
	public String fechar () throws ArqException{
		prepareMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_MATRICULA.getId());
		FechamentoCompulsorioDao fdao = null;
		try {
			
			matriculasEscolhidas = new ArrayList<MatriculaComponente>();
			
			for (MatriculaComponente m : matriculas){
				if (m.getDiscente().isSelecionado())
					matriculasEscolhidas.add(m);
			}
			
			if (isEmpty(matriculas) || isEmpty(matriculasEscolhidas)){
				addMensagemErro("Nenhuma matrícula selecionada.");
				return null;
			}
		} finally {
			if (fdao!=null)
				fdao.close();
		}
		return forward("/graduacao/fechamento_compulsorio/confirmar.jsp");
	}
	
	/**
	 * Chama o processador para cancelar as matrículas.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/fechamento_compulsorio/confirmar.jsp</li>
     * </ul>
	 */
	public String confirmar () throws NegocioException, ArqException{
		
		if ( isOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_MATRICULA.getId()) ){

			validarConfirmacao();
	
			if (hasErrors())
				return null;
				
			MatriculaComponenteDao mdao = null;
			try {
				mdao = getDAO(MatriculaComponenteDao.class);
				
				MovimentoOperacaoMatricula movMatricula = new MovimentoOperacaoMatricula();
				// seta o discente e as matrículas a serem alteradas.
				movMatricula.setNovaSituacao(SituacaoMatricula.CANCELADO);
				movMatricula.setMatriculas(matriculasEscolhidas);
				// prepara e executa o processador para alterar o status da matrícula
				movMatricula.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
				movMatricula.setSistema(getSistema());
				movMatricula.setUsuarioLogado(getUsuarioLogado());
				movMatricula.setAutomatico(false);
				execute(movMatricula);
				
				matriculas = null;
				addMessage("Matrículas canceladas com sucesso.", TipoMensagemUFRN.INFORMATION);
				setOperacaoAtiva(null);
				
			} finally {
				if (mdao!=null)
					mdao.close();
			} 
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
		}
		
		return getListPage();
	}

	/**
	 * Valida a confirmação da operação de fechamento.<br />
     * Método não invocado por JSP(s):
	 */
	private void validarConfirmacao() throws ArqException {
		if (ano == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (periodo == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		if (ead == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Aluno");
		if (isEmpty(matriculasEscolhidas))
			addMensagemErro("Nenhuma matrícula selecionada.");
		confirmaSenha();
	}
	
	public String getListPage(){
		return forward("/graduacao/fechamento_compulsorio/listar_matriculas.jsp");
	}
	
	/**
	 * Limpa a filtragem de discentes e chama o método de paginação para trocar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/fechamento_compulsorio/listar_matriculas.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void changePage(ValueChangeEvent e) {
		PagingInformation paging = getMBean("paginacao");
		matriculas = null;		
		paging.changePage(e);
	}
	
	/**
	 * Limpa a filtragem de discentes e chama o método de paginação para avançar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/fechamento_compulsorio/listar_matriculas.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void nextPage(ActionEvent e) {
		PagingInformation paging = getMBean("paginacao");
		matriculas = null;		
		paging.nextPage(e);
	}

	/**
	 * Limpa a filtragem de discentes e chama o método de paginação para voltar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/graduacao/fechamento_compulsorio/listar_matriculas.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void previousPage(ActionEvent e) {
		PagingInformation paging = getMBean("paginacao");
		matriculas = null;				
		paging.previousPage(e);
	}
	
	@Override
	public String selecionaDiscente() throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		// TODO Auto-generated method stub
		
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getAno() {
		return ano;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setMatriculas(ArrayList<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public ArrayList<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setEad(Boolean ead) {
		this.ead = ead;
	}

	public Boolean getEad() {
		return ead;
	}

	public void setMatriculasEscolhidas(ArrayList<MatriculaComponente> matriculasEscolhidas) {
		this.matriculasEscolhidas = matriculasEscolhidas;
	}

	public ArrayList<MatriculaComponente> getMatriculasEscolhidas() {
		return matriculasEscolhidas;
	}


}
