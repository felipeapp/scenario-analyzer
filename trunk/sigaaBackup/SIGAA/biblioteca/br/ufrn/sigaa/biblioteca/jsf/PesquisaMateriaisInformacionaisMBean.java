/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais;

/**
 *
 * <p>MBean que gerencia a página de busca padrão de materiais informacionais (Exemplares ou Fascículos) no acervo da biblioteca.  </p>
 *
 * <p> <i> A busca é realizada por faixa de código de barras ou a partir da seleção do material na busca do acervo da biblioteca. 
 *   Para busca no acervo implementa a interface {@linkPesquisarAcervoMateriaisInformacionais} que também é implementada em outros caso de uso fora da busca padrão. </i> </p>
 * 
 * <p><strong>Qualquer parte do sistema que quiser utilizar essa pesquisa de materiais da biblioteca, deve 
 *     implentar a interface {@link PesquisarMateriaisInformacionais}<strong></p>
 * 
 * @author jadson
 *
 */
@Component("pesquisaMateriaisInformacionaisMBean")
@Scope("request")
public class PesquisaMateriaisInformacionaisMBean extends SigaaAbstractController<MaterialInformacional> implements PesquisarAcervoMateriaisInformacionais{
	
	/**
	 * A página de busca padrão e seleção de vários materiais no acervo que pode ser chamada de qualquer caso de uso do sistema.
	 */
	public static final String PAGINA_PESQUISA_PADRAO_MATERIAIS_INFORMACIONAIS = "/biblioteca/pesquisaPadraoMaterialInformacional.jsp";
	
	
	/** O Mbean que chamou esse cado de uso, e para onde o fluxo deve retornar quando o usuário selecionar o material */
	private PesquisarMateriaisInformacionais mbeanChamador;

	/** O título da operação mostrado na página, passado como parâmetro do Mbean que chamou esse caso de uso. */
	private String tituloOperacao;
	/** A descrição mostrada ao usuário na página, passado como parâmetro do Mbean que chamou esse caso de uso. */
	private String descricaoOperacao;
	
	/** Indica se a pesquisa em questão é apenas sobre os materiais baixados (situação = baixado). */
	private boolean isPesquisaMateriaisBaixados;
	
	/** A busca que o usuário realiza da página */
	public static final int BUSCA_INDIVIDUAL = 1;
	/** A busca que o usuário realiza da página */
	public static final int BUSCA_POR_FAIXA = 2;
	
	
	/** Se vai ser uma busca por faixa de códigos ou individual, setado no combobox da página. */
	private int tipoBusca = BUSCA_INDIVIDUAL;	
	
	/**
	 * Guarda a lista de materiais pesquisados pelo usuário.
	 */
	private List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
	
	
	/**
	 * Utilizado na pesquisa por código de barras.
	 */
	private String codigoBarras;
	
	/**
	 * Utilizado na pesquisa por faixa de códigos de barras.
	 */
	private String codigoBarrasInicial, codigoBarrasFinal;
	

	
	/**
	 *   <p>Inicia a pesquisa interna no acervo, quando esse pesquisa é chamada de outro caso de uso </p>
	 *
	 *   <p> <strong>Método não chamado por nenhuma página jsp.</strong></p>
	 *   
	 * @return
	 */
	public String iniciarBusca(PesquisarMateriaisInformacionais mbeanChamador
			, String tituloOperacao, String descricaoOperacao, String tituloAcao){
		
		return iniciarBusca(mbeanChamador, tituloOperacao, descricaoOperacao, tituloAcao, false);
		
	}
	
	/**
	 *   <p>Inicia a pesquisa interna no acervo, quando esse pesquisa é chamada de outro caso de uso </p>
	 *
	 *   <p> <strong>Método não chamado por nenhuma página jsp.</strong></p>
	 *   
	 * @return
	 */
	public String iniciarBusca(PesquisarMateriaisInformacionais mbeanChamador
			, String tituloOperacao, String descricaoOperacao, String tituloAcao, boolean isPesquisaMateriaisBaixados){
		
		super.setConfirmButton(tituloAcao); // Nome da ação que vai ser executado no botão
		
		this.mbeanChamador = mbeanChamador;
		
		this.tituloOperacao = tituloOperacao;
		this.descricaoOperacao = descricaoOperacao;
		this.isPesquisaMateriaisBaixados = isPesquisaMateriaisBaixados;
		
		return telaPesquisaPadraoMateriaisInformacionais();
		
	}

	
	
	/**
	 * 
	 * Método que realiza a pesquisa dos materiais por código de barras ou faixa de código de barras 
	 * digitado pelo usuário na página
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/pesquisaPadraoMaterialInformacional.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String pesquisarMaterial() throws DAOException{
		
		
		if( StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal) && StringUtils.isEmpty(codigoBarras)){
			addMensagemWarning("Informe o Código de Barras do Exemplar.");
			return null;
		}
		
		MaterialInformacionalDao dao = null;
		
		try{
			dao = getDAO(MaterialInformacionalDao.class);
			
			if( tipoBusca == BUSCA_POR_FAIXA){
				
				if( StringUtils.isNotEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal)){
					
					addMensagemWarning("Informe o Código de Barras Final do Material.");
					
				}else{
					if(StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isNotEmpty(codigoBarrasFinal) ){
					
						addMensagemWarning("Informe o Código de Barras Inicial do Material.");
					
					}else{
					
						if(codigoBarrasFinal.compareTo(codigoBarrasInicial) < 0){
							
							addMensagemErro("O Código de Barras Final deve ser maior que o Código de Barras Inicial.");
						
						}else{
							
							Integer quantidadeMateriais = null; 
							
							if (isPesquisaMateriaisBaixados){
								quantidadeMateriais = dao.countMateriaisBaixadosByCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
							} else {
								quantidadeMateriais = dao.countMateriaisAtivosByCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
							}
							
							if(quantidadeMateriais >  100){
								addMensagemErro("Só podem ser adicionados 100 Códigos de Barras por vez");
							}else{
							
								List<MaterialInformacional> materiaisTemp = null; 
								
								if (isPesquisaMateriaisBaixados) {
									materiaisTemp = dao.findMateriaisBaixadosByCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
								} else {
									materiaisTemp = dao.findMateriaisAtivosByCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
								}
								
								if (materiaisTemp != null){
									for (MaterialInformacional matInf : materiaisTemp) {
										if(! materiais.contains(matInf)){
											materiais.add(matInf);
										}
									}
									
									if(materiais.size() == 0){
										if (isPesquisaMateriaisBaixados){
											addMensagemErro("Materiais baixados com o código de barras entre: "+codigoBarrasInicial+" e "+codigoBarrasFinal+" não encontrados.");
										} else {
											addMensagemErro("Materiais com o código de barras entre: "+codigoBarrasInicial+" e "+codigoBarrasFinal+" não encontrados.");
										}
									}
									
									codigoBarrasInicial = null;
									codigoBarrasFinal = null;
									codigoBarras = null;
								}
							}
							
						}
					}
				}
				
			}else{
			
			
				if( ! StringUtils.isEmpty(codigoBarras)){

					MaterialInformacional mat = null;
					
					if (isPesquisaMateriaisBaixados){
						mat = dao.findMaterialBaixadoByCodigoBarras(codigoBarras);
					} else {
						mat = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
					}
					
					if(mat == null) {
						if (isPesquisaMateriaisBaixados) {
							addMensagemErro("Material baixado com o código de barras "+codigoBarras+" não encontrado.");
						} else {
							addMensagemErro("Material com o código de barras "+codigoBarras+" não encontrado.");
						}
					} else {
						
						if (!materiais.contains(mat)){
							
							materiais.add(mat);
							
							codigoBarrasInicial = null;
							codigoBarrasFinal = null;
							codigoBarras = null;
							
						} else
							addMensagemErro("Material com o código de barras "+codigoBarras+" já se encontra na lista.");
					}
					
				}else{
					addMensagemWarning("Informe o Código de Barras do Material.");
				}
			}
		
		}finally{
			if(dao != null) dao.close();
		}
			
		return telaPesquisaPadraoMateriaisInformacionais();
	}
	
	
	
	
	/**
	 *      Método utilizado caso o usuário não queira busca os materiais pelo código de barras 
	 *   mas a partir dos Títulos no acervo
	 *   
	 *  <br/><br/>
	 *  Chamado a partir da página: /sigaa.war/biblioteca/pesquisaPadraoMaterialInformacional.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String buscarExemplaresPorTitulo() throws ArqException{
		
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		return bean.iniciarPesquisaSelecionaVariosMateriaisDoTitulo(this, true);
		
	}
	
	
	
	
	/**
	 * 
	 * Remove algum material que tenha sido adicionado a lista
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/pesquisaPadraoMaterialInformacional.jsp
	 *
	 * @return
	 */
	public String removerMaterialSelecionadoDaLista(){
		
		int idMaterialRemocao = getParameterInt("idMaterialRemocao", 0);
		
				
		for (Iterator<MaterialInformacional> iterator = materiais.iterator(); iterator.hasNext();) {
			MaterialInformacional m = iterator.next();
			if(m.getId() == idMaterialRemocao)
				iterator.remove();
			
		}
		return telaPesquisaPadraoMateriaisInformacionais();
	}

	
	/**
	 *   Remove todos os materiais que tinham sido adicionados à lista de buscas
	 *
	 * <br/><br/>
	 * Chamado a partir da página:  /sigaa.war/biblioteca/pesquisaPadraoMaterialInformacional.jsp
	 *
	 * @return
	 */
	public String removerTodosMateriaisDaLista(){
		if(materiais != null) materiais.clear();
		
		codigoBarrasInicial = null;
		codigoBarrasFinal = null;
		codigoBarras = null;
		
		return telaPesquisaPadraoMateriaisInformacionais();
	}


	
	/**
	 * <p>Método que retorna o fluxo do caso de uso para o Mbean que o chamou, com os materiais selecionados 
	 * na busca.</p>
	 * <p>O que vai ser feito com esses materais vai depender de quem está chamando. </p>
	 *
	 *
	 * <br/><br/>
	 * Chamado a partir da página:  /sigaa.war//biblioteca/pesquisaPadraoMaterialInformacional.jsp
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	
	public String realizarAcao() throws ArqException{
		mbeanChamador.setMateriaisPesquisaPadraoMateriais(materiais);
		return mbeanChamador.selecionouMateriaisPesquisaPadraoMateriais();
	}
	
	
	
	
	
	
	
	/**
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaPesquisaPadraoMateriaisInformacionais() {
		return forward(PAGINA_PESQUISA_PADRAO_MATERIAIS_INFORMACIONAIS);
	}

	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}

	public String getDescricaoOperacao() {
		return descricaoOperacao;
	}

	public void setDescricaoOperacao(String descricaoOperacao) {
		this.descricaoOperacao = descricaoOperacao;
	}

	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<MaterialInformacional> materiais) {
		this.materiais = materiais;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getCodigoBarrasInicial() {
		return codigoBarrasInicial;
	}

	public void setCodigoBarrasInicial(String codigoBarrasInicial) {
		this.codigoBarrasInicial = codigoBarrasInicial;
	}

	public String getCodigoBarrasFinal() {
		return codigoBarrasFinal;
	}

	public void setCodigoBarrasFinal(String codigoBarrasFinal) {
		this.codigoBarrasFinal = codigoBarrasFinal;
	}


	///////////////////////  Métodos da interface de pesquisa no acervo de materiais ///////////////
	

	/**
	 * Ver comentário no método pai.
	 * 
	 * <p>Método não chamado por página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#setMateriaisRetornadosDaPesquisaAcervo(java.util.List)
	 */
	@Override
	public void setMateriaisRetornadosDaPesquisaAcervo(List<MaterialInformacional> materiais) throws ArqException {
		
		if(this.materiais == null)
			this.materiais = new ArrayList<MaterialInformacional>();
		
		this.materiais.addAll(materiais); // Adiciona a os materiais dessa listagem, os materiais selecionado no acervo
		
	}
	
	
	/**
	 * Ver comentário no método pai.
	 * 
	 * <p>Método não chamado por página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#selecionouMateriaisRetornadosDaPesquisaAcervo()
	 */
	@Override
	public String selecionouMateriaisRetornadosDaPesquisaAcervo() throws ArqException {
		return telaPesquisaPadraoMateriaisInformacionais();
	}



	/**
	 * Ver comentário na classe pai.
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *   <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *   <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 * </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#voltarBuscaPesquisarAcervoMateriais()
	 */
	@Override
	public String voltarBuscaPesquisarAcervoMateriais() throws ArqException {
		return telaPesquisaPadraoMateriaisInformacionais();
	}
	
	
	/**
	 * Ver comentário no método pai.
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *  <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *  <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 * </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#isUtilizaBotaoVoltarBuscaPesquisarAcervoMateriais()
	 */
	@Override
	public boolean isUtilizaBotaoVoltarBuscaPesquisarAcervoMateriais() {
		return true;
	}
	


	/**
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li> /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 *   
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#getOperacao()
	 */
	@Override
	public String getDescricaoOperacaoUtilizandoBuscaAcervoMateriais() {
		return "Selecionar Materiais";
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	public PesquisarMateriaisInformacionais getMbeanChamador() {
		return mbeanChamador;
	}



	public void setMbeanChamador(PesquisarMateriaisInformacionais mbeanChamador) {
		this.mbeanChamador = mbeanChamador;
	}
	
	public boolean isPermitirUtilizarPesquisa() {
		return !isPesquisaMateriaisBaixados;
	}
	
	
}
