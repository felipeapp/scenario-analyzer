/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 28/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoTransfereExemplaresEntreBibliotecas;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais;

/**
 *      Mbean que gerencia a parte de transfer�ncia de exemplares entre bibliotecas.
 *
 * @author jadson
 * @since 28/10/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("transfereExemplaresEntreBibliotecasMBean")
@Scope("request")
public class TransfereExemplaresEntreBibliotecasMBean  extends SigaaAbstractController<Exemplar> implements PesquisarAcervoMateriaisInformacionais{

	
	/** Constante utilizada para o controle de redirecionamento da p�gina */
	public static final String PAGINA_TRANSFERENCIA_EXEMPLARES = "/biblioteca/processos_tecnicos/outras_operacoes/paginaTransferenciaExemplares.jsp";
	
	/** Guarda os exemplares que v�o ser transferidos */
	private List<Exemplar> exemplares = new ArrayList<Exemplar>(); 
	
	/** 
	 * Guarda os materiais selecionados no caso de uso de pesquisa materiais no acervo temporariamente
	 */
	private List<MaterialInformacional> materiaisTemp = new ArrayList<MaterialInformacional>();
	
	
	
	/** A biblioteca onde os exemplares v�o */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1); 
	
	// C�digos de barras dos exemplares que o usu�rio pode pesquisar na p�gina
	/** C�digos de barras definido pelo usu�rio na busca individual */
	private String codigoBarras;
	/** C�digos de barras inicial definido pelo usu�rio na busca por faixa */
	private String codigoBarrasInicial;
	/** C�digos de barras final definido pelo usu�rio na busca por faixa */
	private String codigoBarrasFinal;
	
	
	/** guarda os dados do cambobox para n�o ficar buscando toda vida que reenderizar a p�gina */
	private Collection<Biblioteca> bibliotecasAtivas;
	
	
	// A busca que o usu�rio realiza da p�gina
	/** C�digo da opera��o de busca c�digo de barras individual */
	public static final int BUSCA_INDIVIDUAL = 1;
	/** C�digo da opera��o de busca c�digo de barras por faixa */
	public static final int BUSCA_POR_FAIXA = 2;
	
	
	/** Se vai ser uma busca por faixa de c�digos ou individual, setado no combobox da p�gina. */
	private int tipoBusca = BUSCA_INDIVIDUAL;
	
	
	/** 
	 * Indica se o chamado patrim�nial vai ser gerado ou n�o. <br/> 
	 * 
	 * J� que ocorrem muitos erros de inclu�rem materiais em outras bibliotecas, o bibliotec�rio vai 
	 * ter a op��o de desativar a gera��o do chamado patrim�nial, para n�o ficar gerando muitos 
	 * chamados patrim�niais que na verdade
	 * 
	 */
	private boolean gerarChamadoPatrimonial = true;
	
	/**
	 * Armazena a sigla do nome do sistema que gerencia as informa��es do patr�nimo da institui��o.
	 */
	private String nomeSistemaPatrimonio;
	
	/**
	 *    Inicia o caso de uso de tranfer�ncia dos fasc�culos redirecionando o usu�rio para a tela onde 
	 *  ele ir� escolher os exemplares para serem transferidos.
	 * 
	 *   <br/><br/>
	 *     Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 *     Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/informacao_referencia.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarTransferencia() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.TRANSFERE_EXEMPLARES_ENTRE_BIBLIOTECAS);
		
		return telaTransferenciaExemplares();
	}
	
	
	
	
	/**
	 *      M�todo utilizado caso o usu�rio n�o queira busca os exemplares pelo c�digo de barras mas a partir do 
	 *   T�tulo dos Exemplares
	 *
	 *     <br/><br/>
	 *      Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaTransferenciaExemplares.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String buscarExemplaresPorTitulo() throws ArqException{
		
		PesquisaTituloCatalograficoMBean beanPesquisa = getMBean("pesquisaTituloCatalograficoMBean");
		return beanPesquisa.iniciarPesquisaSelecionaVariosMateriaisDoTitulo(this, true);
		
	}
	
	
	
	
	/**
	 *   Adiciona os exemplares inseridos usando o c�digo de barras pela pr�pria p�gina.
	 *
	 *  <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaTransferenciaExemplares.jsp
	 *
	 * @param exemplares
	 * @return
	 * @throws ArqException 
	 */
	public String  adicionarCodigoBarras() throws ArqException{
		
		if( StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal) && StringUtils.isEmpty(codigoBarras)){
			addMensagemWarning("Informe o C�digo de Barras do Exemplar.");
			return null;
		}
		
		ExemplarDao dao = getDAO(ExemplarDao.class);
		

		if( tipoBusca == BUSCA_POR_FAIXA){
			
			if( StringUtils.isNotEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal)){
				
				addMensagemWarning("Informe o C�digo de Barras Final do Exemplar.");
				
			}else{
				if(StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isNotEmpty(codigoBarrasFinal) ){
				
					addMensagemWarning("Informe o C�digo de Barras Inicial do Exemplar.");
				
				}else{
				
					if(codigoBarrasFinal.compareTo(codigoBarrasInicial) < 0){
						
						addMensagemErro("O C�digo de Barras Final deve ser maior que o C�digo de Barras Inicial.");
					
					}else{
						
						Integer quantidadeMateriais = dao.countExemplaresAtivosByCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
						
						if(quantidadeMateriais >  100){
							addMensagemErro("S� podem ser adicionados 100 C�digos de Barras por vez");
						}else{
						
							List<Exemplar> exs = dao.findExemplaresAtivosByCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
							
							if (exs != null){
								for (Exemplar e : exs) {
									if(!exemplares.contains(e)){
										exemplares.add(e);
									} else {
										addMensagemErro("Exemplar com o c�digo de barras "+e.getCodigoBarras()+" j� se encontra na lista.");
									}
								}
								
								if(exs.size() == 0){
									addMensagemErro("Exemplares com o c�digo de barras entre: "+codigoBarrasInicial+" e "+codigoBarrasFinal+" n�o encontrados.");
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
				
				Exemplar e = dao.findExemplarAtivoByCodigoBarras(codigoBarras);
				
				if(e == null)
					addMensagemErro("Exemplar com o c�digo de barras "+codigoBarras+" n�o encontrado.");
				else{
					
					if (!exemplares.contains(e)){
						
						exemplares.add(e);
						
						codigoBarrasInicial = null;
						codigoBarrasFinal = null;
						codigoBarras = null;
						
					} else
						addMensagemErro("Exemplar com o c�digo de barras "+codigoBarras+" j� se encontra na lista.");
				}
				
			}else{
				addMensagemWarning("Informe o C�digo de Barras do Exemplar.");
			}
		}
		
		return telaTransferenciaExemplares();
	}
	
	
	
	
	
	
//	/**
//	 *   Adiciona os exemplares selecionados na busca de t�tulo.
//	 *
//	 *  <br/><br/>
//	 * Chamado {@link DetalhesMateriaisDeUmTituloMBean#verificaExemplaresSeleiconados()}
//	 * M�todo n�o chamado por nenhuma p�gina jsp.
//	 *
//	 * @param exemplares
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String adicionaExemplaresSelecionados(List<Exemplar> exemplaresSelecionados) throws ArqException{
//		
//		for (Exemplar exemplarSelecionado : exemplaresSelecionados) {
//			if(! exemplares.contains(exemplarSelecionado))
//				exemplares.add(exemplarSelecionado);
//		}
//		
//		return telaTransferenciaExemplares();
//	}
	
	
	
	/**
	 * 
	 * Remove algum exemplar que n�o deveria estar na lista de exemplares que v�o ser transferidos
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaTransferenciaExemplares.jsp
	 *
	 * @return
	 */
	public String removerExemplarDaLista(){
		
		int idExemplarRemocao = getParameterInt("idExemplarRemocao", 0);
		
				
		for (Iterator<Exemplar> iterator = exemplares.iterator(); iterator.hasNext();) {
			Exemplar e = iterator.next();
			if(e.getId() == idExemplarRemocao){
				iterator.remove();
				addMensagemInformation("Exemplar removido da lista.");
			}
			
		}
		return telaTransferenciaExemplares();
	}
	
	
	
	/**
	 *   Remove todos os exemplares da lista
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaTransferenciaExemplares.jsp
	 *
	 * @return
	 */
	public String removerTodosExemplaresDaLista(){
		if(exemplares != null) exemplares.clear();
		
		addMensagemInformation("Exemplares removidos da lista.");
		
		return telaTransferenciaExemplares();
	}
	
	
	
	
	/**
	 * 
	 * M�todo que realiza a transfer�ncia dos exemplares escolhidos pelo usu�rio.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaTransferenciaExemplares.jsp
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	
	public String transfeExemplares() throws ArqException{
	
		MovimentoTransfereExemplaresEntreBibliotecas mov 
				= new MovimentoTransfereExemplaresEntreBibliotecas(exemplares, bibliotecaDestino, gerarChamadoPatrimonial);
		
		mov.setCodMovimento(SigaaListaComando.TRANSFERE_EXEMPLARES_ENTRE_BIBLIOTECAS);
		
		try {
		
			Object[] retorno = execute(mov);
			String numeroChamadoPatrimonial = (String) retorno[0];
			@SuppressWarnings("unchecked")
			List<String> codigosDeBarraDosExemplaresAbertoChamado = (List<String>) retorno[1];
			
			StringBuilder mensagemUsuario = new StringBuilder();
			
			if(codigosDeBarraDosExemplaresAbertoChamado.size() > 0){
			
				for (int i= 0; i < codigosDeBarraDosExemplaresAbertoChamado.size() ; i++) {
					
					if(i != 0 && i % 10 == 0)
						mensagemUsuario.append("<br/>"); // quebra linha a cada 10 impressos
					
					if(i == 0)
						mensagemUsuario.append(" "+codigosDeBarraDosExemplaresAbertoChamado.get(i)+" ");
					else
						if(i == codigosDeBarraDosExemplaresAbertoChamado.size()-1)
							mensagemUsuario.append(" e "+codigosDeBarraDosExemplaresAbertoChamado.get(i)+". ");
						else
							mensagemUsuario.append(" ,"+codigosDeBarraDosExemplaresAbertoChamado.get(i)+" ");
				}
			
			}
			
			addMensagemInformation(mensagemUsuario.toString());
			
			addMensagemInformation("Um chamado patrimonial de n�mero: "+numeroChamadoPatrimonial+" foi gerado para os exemplares: ");
			
			addMensagemInformation("Transfer�ncia realizada com sucesso. ");
			
			return cancelar();
			
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			
			// Zera os anteriores.
			for (Exemplar exemplar : exemplares) {
				exemplar.setSelecionado(false);
			}
			
			// Verifica quais deram erro para mostrar ao usu�rio //
			for (Exemplar exemplarErro : mov.getExemplaresComErroTransferencia()) {
				for (Exemplar exemplar : exemplares) {
					if(  exemplar.getCodigoBarras().equalsIgnoreCase(exemplarErro.getCodigoBarras())  ){
						exemplar.setSelecionado(true);
					}
				}
			}
			
			return null;
		} catch(RemoteAccessException rae){
			rae.printStackTrace();
			notifyError(rae);
			addMensagemErro("Erro ao abrir um chamado patromonial no  "+RepositorioDadosInstitucionais.get("siglaSipac")+". ");
			return null;
		}
		
	}
	
	
	
	/**
	 * 
	 * Retorna todas as Biblioteca internas do sistema para mostrar no formul�rio da busca
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternasAtivas() throws DAOException{
		if ( bibliotecasAtivas == null )
			bibliotecasAtivas = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas(); 
		return toSelectItems(bibliotecasAtivas, "id", "descricaoCompleta");
	}
	
	/**
	 * M�todo que retorna a sigla do nome do sistema que gerencia as informa��es do patrim�nio da 
	 * institui��o.
	 * 
	 * M�todo utilizado em nenhuma JSP
	 * 
	 * @return A sigla do nome do sistema que gerencia as informa��es do patrim�nio
	 */
	public String getNomeSistemaPatrimonio(){
		
		if(nomeSistemaPatrimonio == null)
			nomeSistemaPatrimonio = RepositorioDadosInstitucionais.get("siglaSipac");
		
		return  nomeSistemaPatrimonio;
	}
	
	
	////////////////////////////////// Tela de navega��o /////////////////////////////////////
	
	/**
	 * Direciona o fluxo de navega��o para a p�gina principal de transfer�ncia de exemplares.
	 * 
	 * M�todo utilizado em nenhuma JSP
	 */
	public String telaTransferenciaExemplares() {
		return forward(PAGINA_TRANSFERENCIA_EXEMPLARES);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	public List<Exemplar> getExemplares() {
		return exemplares;
	}
	
	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
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

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public void setBibliotecaDestino(Biblioteca bibliotecaDestino) {
		this.bibliotecaDestino = bibliotecaDestino;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public boolean isGerarChamadoPatrimonial() {
		return gerarChamadoPatrimonial;
	}

	public void setGerarChamadoPatrimonial(boolean gerarChamadoPatrimonial) {
		this.gerarChamadoPatrimonial = gerarChamadoPatrimonial;
	}


	///////////////////////  M�todo da interface de Busca ///////////////////////////


	/**
	 * M�todo utilizado em nenhuma JSP
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#getOperacao()
	 */
	
	@Override
	public String getDescricaoOperacaoUtilizandoBuscaAcervoMateriais() {
		return "Traferir Exemplares Selecionados";
	}


	/**
	 * M�todo utilizado em nenhuma JSP
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#voltarBusca()
	 */
	@Override
	public String voltarBuscaPesquisarAcervoMateriais() throws ArqException {
		return iniciarTransferencia();
	}

	
	/**
	 * M�todo utilizado em nenhuma JSP
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#isUtilizaBotaoVoltarBusca()
	 */
	@Override
	public boolean isUtilizaBotaoVoltarBuscaPesquisarAcervoMateriais() {
		return true;
	}

	/**
	 * M�todo utilizado em nenhuma JSP
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#selecionouMateriaisRetornadosDaPesquisaAcervo()
	 */
	@Override
	public String selecionouMateriaisRetornadosDaPesquisaAcervo() throws ArqException {
		
		for (MaterialInformacional materialSelecionadoTemp : materiaisTemp) {
			
			if( materialSelecionadoTemp instanceof Exemplar){
				if(! exemplares.contains( materialSelecionadoTemp))
					exemplares.add((Exemplar)  materialSelecionadoTemp);
			}else{
				addMensagemErro("O material "+materialSelecionadoTemp.getCodigoBarras()+" n�o � um exemplar ");
				return null;
			}		
			
		}
		
		return telaTransferenciaExemplares();
		
	}



	@Override
	/**
	 * M�todo utilizado em nenhuma JSP
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#setMateriaisRetornadosDaPesquisaAcervo()
	 */
	public void setMateriaisRetornadosDaPesquisaAcervo(List<MaterialInformacional> materiaisSelecionados) throws ArqException {
		materiaisTemp = materiaisSelecionados;
	}




	
	
	///////////////////////////////////////////////////////////////////////////////
	
}
