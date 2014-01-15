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
 * MBean respons�vel por opera�es relativas 
 * aos cart�es de benef�cio discente.
 * 
 * @author geyson
 */
@Scope(value = "session")
@Component(value = "cartaoBeneficio")
public class CartaoBeneficioDiscenteMBean extends SigaaAbstractController<CartaoBeneficioDiscente> {

	/** Op��o de busca por c�digo cart�o */
	private boolean checkBuscaDiscente;
	/** Op��o de busca por c�digo de barra do cart�o */
	private boolean checkBuscaMatricula;
	
	/** Valor selecionado para busca por matricula */
	private Long buscaMatricula;
	/** Valor selecionado para busca por nome do discente */
	private String buscaNomeDiscente;
	/** Id discente */
	private Integer idDiscente;
	/** Valor selecionado para busca por ano */
	private Integer ano = CalendarUtils.getAnoAtual();
	/** Valor selecionado para busca por per�odo */
	private  Integer periodo = getPeriodoAtual();
	/** Bolsa Aux�lio */
	private  BolsaAuxilio bolsa;
	/** Cartao Beneficio discente */
	private CartaoBeneficioDiscente cartaoDiscente;
	/** Cartao Alimenta��o */
	private CartaoBolsaAlimentacao cartaoAlimentacao;
	
	/** Binding */
	private UIData uiData = new HtmlDataTable();
	
	/** Cole��o de cart�es de acesso ao restaurante universit�rio */
	private Collection<CartaoBeneficioDiscente> cartoes = new ArrayList<CartaoBeneficioDiscente>();
	
	/** Cole��o de discentes econtrados com bolsa alimenta��o */
	private Collection<BolsaAuxilio> bolsas = new ArrayList<BolsaAuxilio>();
	
	/** Construtor */
	public CartaoBeneficioDiscenteMBean() {
		obj = new CartaoBeneficioDiscente();
		cartaoAlimentacao = new CartaoBolsaAlimentacao();
	}
	
	/**
	 * Inicia a busca a discente com bolsa alimenta��o.
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
	 * Retorna para tela de busca de cart�es
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
	 * Inicia a bloqueio do cart�o alimenta��o.
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
	 * Busca discentes contemplados com bolsa alimenta��o.
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
			    ValidatorUtil.validateRequired(matricula, "Matr�cula", lista);
			}
			
			if (checkBuscaDiscente) {
				idDiscente = this.idDiscente;
			    ValidatorUtil.validateRequired(idDiscente, "Discente", lista);
			}
			
			ano = this.ano;
			periodo = this.periodo;
			if(ano == null || periodo == null){
				lista.addErro("Per�odo: Campo obrigat�rio n�o informado.");
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
	 * Busca discentes que possuem cart�o benef�cio.
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
			    ValidatorUtil.validateRequired(matricula, "Matr�cula", lista);
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
	 * Inicia opera��o de associar ou substituir pessoa ao cart�o de acesso ao ru
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
	 * Inicia opera��o de bloqueio do cart�o
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
            	addMensagemWarning(" H� outro(s) discente(s) vinculado(s) a este cart�o. O bloqueio ser� feito para todos os discente vinculados a este cart�o.");
			}
			
			setOperacaoAtiva(SigaaListaComando.BLOQUEAR_CARTAO_BENEFICIO.getId());
			setConfirmButton("Bloquear Cart�o");
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
		
		ValidatorUtil.validateRequired(cartaoAlimentacao.getCodigo(), "C�digo do Cart�o", lista);
		ValidatorUtil.validateRequired(cartaoAlimentacao.getCodBarras(), "C�digo de Barras", lista);
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		 
		try{
		//boolean existeCartaoCodBarras = dao.verificaCartaoExistente(cartaoAlimentacao.getCodBarras());
		//boolean existeCartaoCodigo = dao.verificaCartaoExistente(cartaoAlimentacao.getCodigo());
		
		
		if(!dao.verificaCartaoExistente(cartaoAlimentacao.getCodBarras(), cartaoAlimentacao.getCodigo())){
			addMensagemErro("Cart�o n�o encontrado.");
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
			addMensagemErro("N�o � poss�vel associar o discente a um cart�o bloqueado.");
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
			addMensagemErro(" Opera��o j� realizada. ");
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
			lista.addErro(" Motivo do Bloqueio: campo obrigat�rio n�o informado. ");
		}
		
		CartaoBeneficioDiscente cartao = getGenericDAO().findByPrimaryKey(cartaoDiscente.getId(), CartaoBeneficioDiscente.class);
		
		if(cartao == null){
			lista.addErro(" Cart�o n�o identificado ou n�o encontrado. ");
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
			addMensagemErro(" Opera��o j� realizada. ");
			return null;
		}
		return null;
	}
	
	
	/**
	 * Retorna o total de cart�es localizadas.
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
	 * Retorna o total de cart�es localizadas.
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
