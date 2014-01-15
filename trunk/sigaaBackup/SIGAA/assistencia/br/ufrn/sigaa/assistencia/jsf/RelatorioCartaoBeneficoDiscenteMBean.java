package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.sae.CartaoBeneficioDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.CartaoBeneficioDiscente;

/**
 * 
 * MBean respons�vel por relat�rios relevantes aos cart�es de benef�cio do discente.
 * @author geyson
 *
 */
@Scope(value = "request")
@Component(value = "relatorioCartaoBeneficio")
public class RelatorioCartaoBeneficoDiscenteMBean extends SigaaAbstractController<CartaoBeneficioDiscente> {
	
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
	/** Cole��o de cart�es de acesso ao restaurante universit�rio */
	private Collection<CartaoBeneficioDiscente> cartoes = new ArrayList<CartaoBeneficioDiscente>();
	
	/** Valor selecionado para busca por ano */
	private Integer ano = CalendarUtils.getAnoAtual();
	/** Valor selecionado para busca por per�odo */
	private  Integer periodo = getPeriodoAtual();
	
	/** Cole��o de discentes econtrados com bolsa alimenta��o */
	private Collection<BolsaAuxilio> bolsas = new ArrayList<BolsaAuxilio>();
	
	/** construtor */
	public RelatorioCartaoBeneficoDiscenteMBean() {
		obj = new CartaoBeneficioDiscente();
	}
	
	/**
	 * Inicia a busca para gerar relat�rios.
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/menu.jsp</li>
     *  </ul>
     *  
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciaRelatorio() throws SegurancaException{
		checkRole(SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO);
		cartoes = new ArrayList<CartaoBeneficioDiscente>();
		return forward(ConstantesNavegacaoSae.SELECIONA_DISCENTE_CARTAO);
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
	public String buscaDiscentesCartoes() throws SegurancaException, HibernateException, DAOException {
		
		Long matricula = null;
		Integer idDiscente = null;
		ListaMensagens lista = new ListaMensagens();			
		cartoes = new ArrayList<CartaoBeneficioDiscente>();
		
		if (checkBuscaMatricula) {
			matricula = buscaMatricula;
		    ValidatorUtil.validateRequired(matricula, "Matr�cula", lista);
		}
		if (checkBuscaDiscente) {
			idDiscente = this.idDiscente;
		    ValidatorUtil.validateRequired(idDiscente, "Discente", lista);
		}
		
		if (lista.isEmpty()) {
			CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
			try{
				cartoes = dao.findCartoesDiscentes(idDiscente, matricula);
			}finally{
				dao.close();
			}
		}else {
		    addMensagens(lista);
		    return null;
		}
		
		return forward(ConstantesNavegacaoSae.RELATORIO_DISCENTE_CARTAO);
	}
	
	public String iniciarBuscaAssinaturas() throws SegurancaException{
		checkRole(SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO);
		return forward(ConstantesNavegacaoSae.SELECIONA_DISCENTE_ASSINATURA);
	}
	
	/**
	 * busca discentes para gerar relat�rio de assinaturas.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String buscarDiscentesAssinaturas() throws HibernateException, DAOException{
		Integer ano = null;
		Integer periodo = null;
		
		ListaMensagens lista = new ListaMensagens();			
		bolsas = new ArrayList<BolsaAuxilio>();
			ano = this.ano;
			periodo = this.periodo;
			if(ano == null || periodo == null){
				lista.addErro("Per�odo: Campo obrigat�rio n�o informado.");
				addMensagens(lista);
				return null;
			}
			
			if (lista.isEmpty()) {
				CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class);
			    bolsas = dao.bolsasByDiscente( null, null, ano, periodo );
			}else {
			    addMensagens(lista);
			}	
		
		return forward(ConstantesNavegacaoSae.RELATORIO_DISCENTE_ASSINATURA);
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

	public Collection<CartaoBeneficioDiscente> getCartoes() {
		return cartoes;
	}

	public void setCartoes(Collection<CartaoBeneficioDiscente> cartoes) {
		this.cartoes = cartoes;
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

	public Collection<BolsaAuxilio> getBolsas() {
		return bolsas;
	}

	public void setBolsas(Collection<BolsaAuxilio> bolsas) {
		this.bolsas = bolsas;
	}

		

}
