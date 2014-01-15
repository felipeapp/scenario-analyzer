/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/04/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

/*******************************************************************************
 * MBean utilizado para colocar as buscas de atividades e avaliações de extensão
 * de maneira que possam ser recuperados em escopo session.
 * 
 * @author Gleydson
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope(value = "session")
@Component(value = "filtroAtividades")
public class FiltroAtividadesMBean extends SigaaAbstractController<AtividadeExtensao> {

	/** Constate utilizada nas definições da barra de filtragem */
	public static final int FILTRO_DISTRIBUIR_ATIVIDADES_MANUAL = 1;
	/** Constate utilizada nas definições da barra de filtragem */
	public static final int FILTRO_DISTRIBUIR_ATIVIDADES_AUTO = 2;
	/** Constate utilizada nas definições da barra de filtragem */
	public static final int FILTRO_AVALIAR_ATIVIDADES = 3;


	/** Usado para armazenar informações retornadas por uma busca */
	private Collection<AtividadeExtensao> resultadosBusca;

	/** Usado para armazenar informação inserida na tela de busca */
	private Integer buscaEdital = null;
	
	/** Usado para armazenar informação inserida na tela de busca */
	private String buscaTitulo = null;
	
	/** Usado para armazenar informação inserida na tela de busca */
	private Integer buscaAno = null;

	/** Usado como auxiliar de tela de busca. */
	private Boolean buscaFinanciamentoInterno = null;

	/** Usado como auxiliar de tela de busca. */
	private Boolean buscaFinanciamentoExterno = null;

	/** Usado como auxiliar de tela de busca. */
	private Boolean buscaAutoFinanciamento = null;

	/** Usado como auxiliar de tela de busca. */
	private Boolean buscaConvenioFunpec = null;

	/** Usado como auxiliar de tela de busca. */
	private Integer buscaAreaTematicaPrincipal = null;


	/** Usado como auxiliar de tela de busca. */
	private Boolean checkEdital = false;

	/** Usado como auxiliar de tela de busca. */
	private Boolean checkFinanciamento = false;
	
	/** Usado como auxiliar de tela de busca. */
	private Boolean checkTitulo = false;
	
	/** Usado como auxiliar de tela de busca. */
	private Boolean checkAno = false;
	
	/** Usado como auxiliar de tela de busca. */
	private Boolean checkAreaTematicaPrincipal = false;
	
	/** Usado como auxiliar de tela de busca. */
	private Boolean checkAguardandoAvaliacaoFinal = false;


	/** Usado para informar o tipo do filtro */
	private int tipoFiltro = 0;

	/**
	 * Redireciona o usuário para tela de lista de projetos disponíveis para
	 * distribuição manual para avaliação do comitê Ad Hoc
	 * 
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String irTelaDistribuirManualComiteAdHoc() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		// habilitando método filtro() para distribuição manual de atividades...
		setTipoFiltro(FILTRO_DISTRIBUIR_ATIVIDADES_MANUAL);
		checkAguardandoAvaliacaoFinal = true;
		resultadosBusca = null;
		return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_LISTA);
	}

	/**
	 * Redireciona o usuário para tela de lista de projetos disponíveis para
	 * distribuição automática para avaliação do comitê Ad Hoc
	 * 
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String irTelaDistribuirAutomaticaComiteAdHoc() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		// habilitando método filtro() para distribuição automática de atividades...
		setTipoFiltro(FILTRO_DISTRIBUIR_ATIVIDADES_AUTO);
		resultadosBusca = null;
		checkAguardandoAvaliacaoFinal = false;
		return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_AUTO_LISTA);
	}

	
	/**
	 * Redireciona o usuário para tela de lista de projetos disponíveis para
	 * distribuir para avaliação do comitê
	 * 
	 * 
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String irTelaDistribuirComiteExtensao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		// Habilitando método filtro() para distribuição de atividades...
		setTipoFiltro(FILTRO_DISTRIBUIR_ATIVIDADES_MANUAL);
		resultadosBusca = null;
		checkAguardandoAvaliacaoFinal = true;
		return forward(ConstantesNavegacao.DISTRIBUICAOCOMITE_LISTA);
	}

	/**
	 * Redireciona o usuário para tela de lista de projetos disponíveis para
	 * avaliação do presidente do comitê
	 * 
	 * sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String irTelaAvaliarPresidenteComite() throws SegurancaException {
		checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
		setTipoFiltro(FILTRO_AVALIAR_ATIVIDADES);
		// Atualizando lista de ações para distribuição.
		resultadosBusca = null;
		checkAguardandoAvaliacaoFinal = true;
		return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_LISTA_PRESIDENTE);
	}

	/**
	 * Filtra atividades segundo a variável tipoFiltro que deve ser setada antes
	 * da chamada deste método
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @throws SegurancaException
	 */
	public void filtrar() throws SegurancaException {
		try {

			resultadosBusca = null;
			
			Integer idEdital = null;
			String titulo = null;
			Integer ano = null;
			Integer idArea = null;
			Boolean financiamentoInterno = null;
			Boolean financiamentoExterno = null;
			Boolean autofinanciamento = null;
			Boolean convenioFunpec = null;
			
			ListaMensagens lista = new ListaMensagens();

			if (checkTitulo) {
				ValidatorUtil.validateRequired(buscaTitulo, "Título", lista);
				titulo = buscaTitulo;
			}

			if (checkAno) {
				ValidatorUtil.validateRequired(buscaAno, "Ano", lista);
				ano = buscaAno;
			}
			
			if (checkEdital) {
				ValidatorUtil.validateRequired(buscaEdital, "Edital", lista);
				idEdital = buscaEdital;
			}

			if (checkAreaTematicaPrincipal) {
				ValidatorUtil.validateRequired(buscaAreaTematicaPrincipal, "Área Temática", lista);
				idArea = buscaAreaTematicaPrincipal;
			}

			if (checkFinanciamento) {
				financiamentoInterno = buscaFinanciamentoInterno;
				financiamentoExterno = buscaFinanciamentoExterno;
				autofinanciamento = buscaAutoFinanciamento;
				convenioFunpec = buscaConvenioFunpec;
			}
			
			if (!checkTitulo && !checkAno && !checkAreaTematicaPrincipal && !checkEdital && !checkFinanciamento && !checkAguardandoAvaliacaoFinal){
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			}
			
			if (!lista.isEmpty() || hasErrors()) {
				addMensagens(lista);
				resultadosBusca = null;
				
			} else {
				
				AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
	
				/**
				 * Retorna todas as atividades pendentes de distribuição para os
				 * avaliadores ad-hoc ou do comite de extensão avaliarem
				 * 
				 * lista somente propostas que estão 'AGUARDANDO AVALIACAO' ou com situação 'SUBMETIDA'
				 */
				if (isTipoFiltroDistribuicaoManual()){				
					resultadosBusca = dao.findPendenteDistribuicao(titulo, ano, idEdital, financiamentoInterno, financiamentoExterno, autofinanciamento, convenioFunpec, idArea);
	
				} 
				
				/**
				 * Retorna todas as atividades pendentes de distribuição para os
				 * avaliadores ad-hoc ou do comite de extensão avaliarem
				 * 
				 * lista somente propostas que estão 'AGUARDANDO AVALIACAO' ou com situação 'SUBMETIDA'
				 */
				else if (isTipoFiltroDistribuicaoAutomatica()){
					if ( !checkAreaTematicaPrincipal ){
						addMensagemErro("Área Temática deve ser informada para distribuição automática das ações.");
						resultadosBusca = null;
					}else {
						resultadosBusca = dao.findPendenteDistribuicao(titulo, ano, idEdital, financiamentoInterno, financiamentoExterno, autofinanciamento, convenioFunpec, idArea);
					}
				}
	
				/**
				 * Retorna todas as atividades com avaliação em andamento. método
				 * utilizado na avaliação do presitente do comitê de extensão.
				 * 
				 */
				else if (isTipoFiltroAvaliacao()) {
					checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
					resultadosBusca = dao.findAguardandoAvaliacaoFinal(titulo, ano, idEdital, financiamentoInterno, financiamentoExterno, autofinanciamento, convenioFunpec, idArea);
				} else {
	
					addMensagemErro("Tipo de filtro não foi definido.");
					resultadosBusca = null;
	
				}
	
				if (ValidatorUtil.isEmpty(resultadosBusca)) {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				}
				
			}


		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}

	public boolean isExibir(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosExtensao.EXIBIR_AVALIACAO_FINAL_PROPOSTA);
	}
	
	public Collection<AtividadeExtensao> getResultadosBusca() {
		return resultadosBusca;
	}

	public void setResultadosBusca(Collection<AtividadeExtensao> resultadosBusca) {
		this.resultadosBusca = resultadosBusca;
	}

	public Integer getBuscaEdital() {
		return buscaEdital;
	}

	public void setBuscaEdital(Integer buscaEdital) {
		this.buscaEdital = buscaEdital;
	}

	public Boolean getBuscaFinanciamentoInterno() {
		return buscaFinanciamentoInterno;
	}

	public void setBuscaFinanciamentoInterno(Boolean buscaFinanciamentoInterno) {
		this.buscaFinanciamentoInterno = buscaFinanciamentoInterno;
	}

	public Boolean getBuscaFinanciamentoExterno() {
		return buscaFinanciamentoExterno;
	}

	public void setBuscaFinanciamentoExterno(Boolean buscaFinanciamentoExterno) {
		this.buscaFinanciamentoExterno = buscaFinanciamentoExterno;
	}

	public Boolean getBuscaAutoFinanciamento() {
		return buscaAutoFinanciamento;
	}

	public void setBuscaAutoFinanciamento(Boolean buscaAutoFinanciamento) {
		this.buscaAutoFinanciamento = buscaAutoFinanciamento;
	}

	public Boolean getBuscaConvenioFunpec() {
		return buscaConvenioFunpec;
	}

	public void setBuscaConvenioFunpec(Boolean buscaConvenioFunpec) {
		this.buscaConvenioFunpec = buscaConvenioFunpec;
	}

	public Boolean getCheckEdital() {
		return checkEdital;
	}

	public void setCheckEdital(Boolean checkEdital) {
		this.checkEdital = checkEdital;
	}

	public Boolean getCheckFinanciamento() {
		return checkFinanciamento;
	}

	public void setCheckFinanciamento(Boolean checkFinanciamento) {
		this.checkFinanciamento = checkFinanciamento;
	}

	public int getTipoFiltro() {
		return tipoFiltro;
	}

	public void setTipoFiltro(int tipoFiltro) {
		this.tipoFiltro = tipoFiltro;
	}

	public String getBuscaTitulo() {
	    return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
	    this.buscaTitulo = buscaTitulo;
	}

	public Boolean getCheckTitulo() {
	    return checkTitulo;
	}

	public void setCheckTitulo(Boolean checkTitulo) {
	    this.checkTitulo = checkTitulo;
	}

	public Boolean getCheckAno() {
	    return checkAno;
	}

	public void setCheckAno(Boolean checkAno) {
	    this.checkAno = checkAno;
	}

	public Integer getBuscaAno() {
	    return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
	    this.buscaAno = buscaAno;
	}

	public Boolean getCheckAreaTematicaPrincipal() {
		return checkAreaTematicaPrincipal;
	}

	public void setCheckAreaTematicaPrincipal(Boolean checkAreaTematicaPrincipal) {
		this.checkAreaTematicaPrincipal = checkAreaTematicaPrincipal;
	}

	public Integer getBuscaAreaTematicaPrincipal() {
		return buscaAreaTematicaPrincipal;
	}

	public void setBuscaAreaTematicaPrincipal(Integer buscaAreaTematicaPrincipal) {
		this.buscaAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
	}

	/** Determina se o tipo de filtro está configurado para distribuição manual. */
	public boolean isTipoFiltroDistribuicaoManual(){
		return getTipoFiltro() == FILTRO_DISTRIBUIR_ATIVIDADES_MANUAL;
	}

	/** Determina se o tipo de filtro está configurado para distribuição automática. */
	public boolean isTipoFiltroDistribuicaoAutomatica(){
		return getTipoFiltro() == FILTRO_DISTRIBUIR_ATIVIDADES_AUTO;
	}

	/** Determina se o tipo de filtro está configurado avaliação do presidente do comitê de extensão. */
	public boolean isTipoFiltroAvaliacao(){
		return getTipoFiltro() == FILTRO_AVALIAR_ATIVIDADES;
	}

	public Boolean getCheckAguardandoAvaliacaoFinal() {
		return checkAguardandoAvaliacaoFinal;
	}

	public void setCheckAguardandoAvaliacaoFinal(
			Boolean checkAguardandoAvaliacaoFinal) {
		this.checkAguardandoAvaliacaoFinal = checkAguardandoAvaliacaoFinal;
	}

}
