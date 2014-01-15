package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.sae.CartaoBeneficioDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.CartaoBeneficioDiscente;
import br.ufrn.sigaa.assistencia.dominio.CartaoBolsaAlimentacao;
import br.ufrn.sigaa.assistencia.dominio.StatusCartaoBeneficio;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por operaões relativas 
 * aos cartões de benefício discente.
 * 
 * @author geyson
 */
@Scope(value = "session")
@Component(value = "cartaoBeneficio")
public class CartaoBeneficioDiscenteMBean extends SigaaAbstractController<CartaoBeneficioDiscente> {

	/** Opção de busca por código cartão */
	private boolean checkBuscaDiscente;
	/** Opção de busca por código de barra do cartão */
	private boolean checkBuscaMatricula;
	
	/** Valor selecionado para busca por matricula */
	private Long buscaMatricula;
	/** Valor selecionado para busca por nome do discente */
	private String buscaNomeDiscente;
	/** Id discente */
	private Integer idDiscente;
	/** Valor selecionado para busca por ano */
	private Integer ano = CalendarUtils.getAnoAtual();
	/** Valor selecionado para busca por período */
	private  Integer periodo = getPeriodoAtual();
	/** Bolsa Auxílio */
	private  BolsaAuxilio bolsa;
	/** Cartao Beneficio discente */
	private CartaoBeneficioDiscente cartaoDiscente;
	/** Cartao Alimentação */
	private CartaoBolsaAlimentacao cartaoAlimentacao;
	
	/** Binding */
	private UIData uiData = new HtmlDataTable();
	
	/** Coleção de cartões de acesso ao restaurante universitário */
	private Collection<CartaoBeneficioDiscente> cartoes = new ArrayList<CartaoBeneficioDiscente>();
	
	/** Coleção de discentes econtrados com bolsa alimentação */
	private Collection<BolsaAuxilio> bolsas = new ArrayList<BolsaAuxilio>();
	
	/** Construtor */
	public CartaoBeneficioDiscenteMBean() {
		obj = new CartaoBeneficioDiscente();
		cartaoAlimentacao = new CartaoBolsaAlimentacao();
	}
	
	/**
	 * Inicia a busca a discente com bolsa alimentação.
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/menu.jsp</li>
     *  </ul>
     *  
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarBusca() throws SegurancaException {
		checkRole(SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO);
		bolsas = new ArrayList<BolsaAuxilio>();
		uiData = new HtmlDataTable();
		cartoes = new ArrayList<CartaoBeneficioDiscente>();
		obj = new CartaoBeneficioDiscente();
		return forward(ConstantesNavegacaoSae.DISCENTE_CARTAO_BUSCA);
	}
	
	@Override
	public String getListPage() {
		return forward(ConstantesNavegacaoSae.DISCENTE_CARTAO_BUSCA);
	}
	
	/**
	 * Retorna para tela de busca de cartões
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/form_bloquear.jsp</li>
     *  </ul>
	 * @return
	 */
	public String voltaListaCartoes() {
		return forward(ConstantesNavegacaoSae.BLOQUEIO_CARTAO_BUSCA);
	}
	
	/**
	 * Inicia a bloqueio do cartão alimentação.
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/menu.jsp</li>
     *  </ul>
     *  
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarBuscaBloqueio() throws SegurancaException {
		checkRole(SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO);
		bolsas = new ArrayList<BolsaAuxilio>();
		cartoes = new ArrayList<CartaoBeneficioDiscente>();
		uiData = new HtmlDataTable();
		obj = new CartaoBeneficioDiscente();
		return forward(ConstantesNavegacaoSae.BLOQUEIO_CARTAO_BUSCA);
	}
	
	/** 
	 * Busca discentes contemplados com bolsa alimentação.
	 * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/buscar.jsp</li>
     *  </ul>
	 * 
	 * @throws SegurancaException
	 * @throws DAOException 
	 * @throws HibernateException 
	 */	
	public String buscar() throws SegurancaException, HibernateException, DAOException {
		
		Long matricula = null;
		Integer idDiscente = null;
		Integer ano = null;
		Integer periodo = null;
		ListaMensagens lista = new ListaMensagens();			
		bolsas = new ArrayList<BolsaAuxilio>();
		uiData = new HtmlDataTable();
		CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
		
		try {

			if (checkBuscaMatricula) {
				matricula = buscaMatricula;
			    ValidatorUtil.validateRequired(matricula, "Matrícula", lista);
			}
			
			if (checkBuscaDiscente) {
				idDiscente = this.idDiscente;
			    ValidatorUtil.validateRequired(idDiscente, "Discente", lista);
			}
			
			ano = this.ano;
			periodo = this.periodo;
			if(ano == null || periodo == null){
				lista.addErro("Período: Campo obrigatório não informado.");
			}
			
			if (lista.isEmpty()) {
			    bolsas = dao.bolsasByDiscente( idDiscente, matricula, ano, periodo );
			}else {
			    addMensagens(lista);
			}

		} finally {
			dao.close();
		}
		return forward(ConstantesNavegacaoSae.DISCENTE_CARTAO_BUSCA);
	}
	
	/** 
	 * Busca discentes que possuem cartão benefício.
	 * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/buscar_cartoes.jsp</li>
     *  </ul>
	 * 
	 * @throws SegurancaException
	 * @throws DAOException 
	 * @throws HibernateException 
	 */	
	public String buscarCartoes() throws SegurancaException, HibernateException, DAOException {
		
		Long matricula = null;
		Integer idDiscente = null;
		ListaMensagens lista = new ListaMensagens();			
		cartoes = new ArrayList<CartaoBeneficioDiscente>();
		uiData = new HtmlDataTable();
		CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
		
		try {
			if (checkBuscaMatricula) {
				matricula = buscaMatricula;
			    ValidatorUtil.validateRequired(matricula, "Matrícula", lista);
			}
			if (checkBuscaDiscente) {
				idDiscente = this.idDiscente;
			    ValidatorUtil.validateRequired(idDiscente, "Discente", lista);
			}
			
			if (lista.isEmpty()) {
				cartoes = dao.findCartoesDesbloqueados(idDiscente, matricula);
			}else {
			    addMensagens(lista);
			}

		} finally {
			dao.close();
		}
		return forward(ConstantesNavegacaoSae.BLOQUEIO_CARTAO_BUSCA);
	}
	
	/**
	 * Inicia operação de associar ou substituir pessoa ao cartão de acesso ao ru
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/buscar.jsp</li>
     *  </ul>
	 * 
	 * @return
	 */
	public String iniciarAssociarDiscente(){
		CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
		try{
		bolsa = (BolsaAuxilio) uiData.getRowData();
		
		if( bolsa.getDiscente().getId() > 0 ){
			setOperacaoAtiva(SigaaListaComando.ASSOCIAR_PESSOA_CARTAO_RU.getId());
			setConfirmButton("Associar Discente");
		}
				
		}finally{
			dao.close();
		}
		return forward(ConstantesNavegacaoSae.ASSOCIA_DISCENTE_CARTAO);
	}
	
	/**
	 * Inicia operação de bloqueio do cartão
	 * 
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/buscar_cartoes.jsp</li>
     *  </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarBloqueio() throws HibernateException, DAOException{
		CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
		try{
		cartaoDiscente = (CartaoBeneficioDiscente) uiData.getRowData();
		
		if( cartaoDiscente.getId() > 0 ){
			
			Collection<CartaoBeneficioDiscente> cartoesAli = new ArrayList<CartaoBeneficioDiscente>();
			cartoesAli = dao.cartoesBeneficioDiscente(cartaoDiscente.getCartaoBolsaAlimentacao().getId());
			if(cartoesAli.size() > 1){
            	addMensagemWarning(" Há outro(s) discente(s) vinculado(s) a este cartão. O bloqueio será feito para todos os discente vinculados a este cartão.");
			}
			
			setOperacaoAtiva(SigaaListaComando.BLOQUEAR_CARTAO_BENEFICIO.getId());
			setConfirmButton("Bloquear Cartão");
		}
				
		}finally{
			dao.close();
		}
		return forward(ConstantesNavegacaoSae.BLOQUEIO_CARTAO_FORM);
	}
	
	
	/** Associar/Substituir pessoa 
	 * 
	 *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/form_associar.jsp</li>
     *  </ul>
     *  
	 * @throws ArqException 
	 * @throws NegocioException */
	public String associarDiscente() throws ArqException, NegocioException{
		ListaMensagens lista = new ListaMensagens();
		CartaoBolsaAlimentacao cartaoAli = new CartaoBolsaAlimentacao();
		CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
		
		ValidatorUtil.validateRequired(cartaoAlimentacao.getCodigo(), "Código do Cartão", lista);
		ValidatorUtil.validateRequired(cartaoAlimentacao.getCodBarras(), "Código de Barras", lista);
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		 
		try{
		//boolean existeCartaoCodBarras = dao.verificaCartaoExistente(cartaoAlimentacao.getCodBarras());
		//boolean existeCartaoCodigo = dao.verificaCartaoExistente(cartaoAlimentacao.getCodigo());
		
		
		if(!dao.verificaCartaoExistente(cartaoAlimentacao.getCodBarras(), cartaoAlimentacao.getCodigo())){
			addMensagemErro("Cartão não encontrado.");
		}
		
		
		if(hasErrors()){
			return null;
		}
		
		cartaoAli = dao.findByExactField(CartaoBolsaAlimentacao.class, "codBarras", cartaoAlimentacao.getCodBarras()).iterator().next();
		cartaoAlimentacao = cartaoAli;
		if(dao.cartoesBeneficioDiscente(cartaoAlimentacao.getId()).isEmpty()){
			cartaoAlimentacao.setBloqueado(false);
		}
		
		if(cartaoAlimentacao.isBloqueado()){
			addMensagemErro("Não é possível associar o discente a um cartão bloqueado.");
			return null;
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		if(isOperacaoAtiva(SigaaListaComando.ASSOCIAR_PESSOA_CARTAO_RU.getId())){
			prepareMovimento(SigaaListaComando.ASSOCIAR_PESSOA_CARTAO_RU);
			mov.setCodMovimento(SigaaListaComando.ASSOCIAR_PESSOA_CARTAO_RU);
			obj.setAtivo(true);
			obj.setData(new Date());
			obj.setDiscente(getGenericDAO().findByPrimaryKey(bolsa.getDiscente().getId(), Discente.class));
			obj.setRegistroAtribuicao(getUsuarioLogado().getRegistroEntrada().getId());
			obj.setCartaoBolsaAlimentacao(cartaoAlimentacao);
			StatusCartaoBeneficio status = getGenericDAO().findByPrimaryKey(StatusCartaoBeneficio.ATIVO, StatusCartaoBeneficio.class); 
			obj.setStatusCartaoBeneficio(status) ;
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			this.setConfirmButton("");
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			resetBean();
			return forward(ConstantesNavegacaoSae.DISCENTE_CARTAO_BUSCA);
		}else{
			addMensagemErro(" Operação já realizada. ");
			resetBean();
			return forward(ConstantesNavegacaoSae.DISCENTE_CARTAO_BUSCA);
		}
		}finally{
			dao.close();
		}

	}
	
	/** bloquear cartao 
	 * 
	 *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/form_bloquear.jsp</li>
     *  </ul>
	 * 
	 * @throws ArqException 
	 * @throws NegocioException */
	public String bloquearCartao() throws ArqException, NegocioException{
		ListaMensagens lista = new ListaMensagens();

		if(cartaoDiscente.getCartaoBolsaAlimentacao().getMotivoBloqueio().isEmpty()){
			lista.addErro(" Motivo do Bloqueio: campo obrigatório não informado. ");
		}
		
		CartaoBeneficioDiscente cartao = getGenericDAO().findByPrimaryKey(cartaoDiscente.getId(), CartaoBeneficioDiscente.class);
		
		if(cartao == null){
			lista.addErro(" Cartão não identificado ou não encontrado. ");
			return null;
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		if(isOperacaoAtiva(SigaaListaComando.BLOQUEAR_CARTAO_BENEFICIO.getId())){

			if (lista.isEmpty()) {
					cartao.getCartaoBolsaAlimentacao().setMotivoBloqueio(cartaoDiscente.getCartaoBolsaAlimentacao().getMotivoBloqueio());
					cartao.setStatusCartaoBeneficio(getGenericDAO().findByPrimaryKey(StatusCartaoBeneficio.PERDIDO, StatusCartaoBeneficio.class));
					prepareMovimento(SigaaListaComando.BLOQUEAR_CARTAO_BENEFICIO);
					mov.setCodMovimento(SigaaListaComando.BLOQUEAR_CARTAO_BENEFICIO);
					mov.setObjMovimentado(cartao);
					execute(mov, getCurrentRequest());
					this.setConfirmButton("");
					addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
					removeOperacaoAtiva();
					resetBean();
					return forward(ConstantesNavegacaoSae.BLOQUEIO_CARTAO_BUSCA);
			}
			else{
				addMensagens(lista);
			}

		}else{
			addMensagemErro(" Operação já realizada. ");
			return null;
		}
		return null;
	}
	
	
	/**
	 * Retorna o total de cartões localizadas.
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/buscar.jsp</li>
     *  </ul>
	 * 
	 * @return
	 */
	public int getTotalCartoesLocalizadas() {
		return bolsas.size();
	}
	
	/**
	 * Retorna o total de cartões localizadas.
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/CartaoBeneficioDiscente/buscar.jsp</li>
     *  </ul>
	 * 
	 * @return
	 */
	public int getTotalCartoesBloqueioLocalizadas() {
		return cartoes.size();
	}

	public boolean isCheckBuscaDiscente() {
		return checkBuscaDiscente;
	}

	public void setCheckBuscaDiscente(boolean checkBuscaDiscente) {
		this.checkBuscaDiscente = checkBuscaDiscente;
	}

	public boolean isCheckBuscaMatricula() {
		return checkBuscaMatricula;
	}

	public void setCheckBuscaMatricula(boolean checkBuscaMatricula) {
		this.checkBuscaMatricula = checkBuscaMatricula;
	}

	public Long getBuscaMatricula() {
		return buscaMatricula;
	}

	public void setBuscaMatricula(Long buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public String getBuscaNomeDiscente() {
		return buscaNomeDiscente;
	}

	public void setBuscaNomeDiscente(String buscaNomeDiscente) {
		this.buscaNomeDiscente = buscaNomeDiscente;
	}

	public Integer getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(Integer idDiscente) {
		this.idDiscente = idDiscente;
	}

	public UIData getUiData() {
		return uiData;
	}

	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

	public Collection<CartaoBeneficioDiscente> getCartoes() {
		return cartoes;
	}

	public void setCartoes(Collection<CartaoBeneficioDiscente> cartoes) {
		this.cartoes = cartoes;
	}

	public Collection<BolsaAuxilio> getBolsas() {
		return bolsas;
	}

	public void setBolsas(Collection<BolsaAuxilio> bolsas) {
		this.bolsas = bolsas;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public BolsaAuxilio getBolsa() {
		return bolsa;
	}

	public void setBolsa(BolsaAuxilio bolsa) {
		this.bolsa = bolsa;
	}

	public CartaoBolsaAlimentacao getCartaoAlimentacao() {
		return cartaoAlimentacao;
	}

	public void setCartaoAlimentacao(CartaoBolsaAlimentacao cartaoAlimentacao) {
		this.cartaoAlimentacao = cartaoAlimentacao;
	}

	public CartaoBeneficioDiscente getCartaoDiscente() {
		return cartaoDiscente;
	}

	public void setCartaoDiscente(CartaoBeneficioDiscente cartaoDiscente) {
		this.cartaoDiscente = cartaoDiscente;
	}
	
	
}
