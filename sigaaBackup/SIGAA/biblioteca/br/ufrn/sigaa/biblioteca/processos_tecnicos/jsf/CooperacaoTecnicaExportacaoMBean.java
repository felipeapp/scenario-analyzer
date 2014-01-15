/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/07/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArquivoDeCargaNumeroControleFGVDao;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroExportacaoCooperacaoTecnica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAcoesAuxiliaresExportaTituloAutoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 *    <p>Managed Bean para gerenciar a exporta��o de Titulos e autoridades da biblioteca. </p>
 *     
 *     <p><i>Coopera��o t�cnica � a troca de informa��es entre bibliotecas. Normamente, a codifica��o 
 * internacional padr�o � a ISO-2709. E a biblioteca realiza coopera��o t�cnica com a FGV. </i></p>
 *
 * @author jadson
 * @since 07/07/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("cooperacaoTecnicaExportacaoMBean")
@Scope("request")
public class CooperacaoTecnicaExportacaoMBean extends SigaaAbstractController<Object> implements PesquisarAcervoBiblioteca{
	
	/** Form onde o usu�rio pode exportar os t�tulos */
	public static final String PAGINA_EXPORTA_TITULO_AUTORIDADE = "/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp";
	
	/**  Se a opera��o � a exporta��o de t�tulo */
	public static final short OPERACAO_BIBLIOGRAFICA = 1;
	
	/**  Se a opera��o � a exporta��o de autoridades */
	public static final short OPERACAO_AUTORIDADE = 2;
	
	/**  Se o usu�rio realizou a busca por apenas um n�mero identificador da cataloga��o */
	public static final int BUSCA_INDIVIDUAL = 1;
	
	/**  Se o usu�rio realizou a busca por uma faixa de n�meros que identificam as cataloga��es */
	public static final int BUSCA_POR_FAIXA = 2;
	
	/**
	 * informa se a importa��o/exporta��o � de autoridades ou de dados bibliogr�ficos 
	 */
	private short tipoOperacao = OPERACAO_BIBLIOGRAFICA;
	
	/** Valor para a codifica��o ISO */
	public static final char ISO_2709 = 'I';
	
	/** Valor para a codifica��o UTF-8 */
	public static final char UTF_8 = 'U';
	
	/** Informa o tipo de codifica��o escolhida para ser utilizada. A codifica��o padr�o � ISO */
	private char tipoCodificacao = ISO_2709;      

	/** O nome do arquivo que ser� gerado para exportar para a FGV */
	private String nomeArquivoFGV;

	/** Se for exportar para a FGV deve conter alguns campos a mais e utilizar o n�mero de controle ( campo 001)  de l�. */
	private boolean exportarFGV = false;
	
	/** O primeiro n�mero da faixa que o usu�rio digitar */
	private Integer primeiroNumeroSistema; 
	
	/** O �ltimo n�mero da faixa que o usu�rio digitar */
	private Integer ultimoNumeroSistema;   
	
	/** Guarda o n�mero se o usu�rio digitar manualmente os n�meros do sistema */
	private Integer numeroDoSistema; 
	
    /** Os t�tulos que ser�o realmente exportados */
	private List<CacheEntidadesMarc> titulosExportacao = new ArrayList<CacheEntidadesMarc>();
	
	/** As Autoridades que ser�o realmente exportados */
	private List<CacheEntidadesMarc> autoridadesExportacao = new ArrayList<CacheEntidadesMarc>();
	
	 /** Para interagir com os t�tulos no formul�rio de exporta��o */
	private DataModel  dataModelTitulos;
	
	 /**  Para interagir com as autoridades no formul�rio de exporta��o */
	private DataModel  dataModelAutoridades;
	
	/** Os t�tulos que o usu�rio catalogou e o sistema salvou como pendentes de exporta��o */
	private List<CacheEntidadesMarc> titulosExportacaoGravadosDoUsuario = new ArrayList<CacheEntidadesMarc>();
	
	/** As autoridades que o usu�rio catalogou e o sistema salvou como pendentes de exporta��o */
	private List<CacheEntidadesMarc> autoridadesExportacaoGravadasDoUsuario = new ArrayList<CacheEntidadesMarc>();
	
	/** Os t�tulo que o usu�rio adicionou agora na tela da exporta��o. */
	private List<CacheEntidadesMarc> titulosExportacaoAdicionados = new ArrayList<CacheEntidadesMarc>();
	
	/** As autoridade que o usu�rio adicionou agora na tela da exporta��o. */
	private List<CacheEntidadesMarc> autoridadesExportacaoAdicionadas = new ArrayList<CacheEntidadesMarc>();
    
	
    /** Indica se � para selecionar todos os checkbox na tela ou n�o. */
	private boolean selecionaTodos = true;
	
	
	/** Se vai ser uma busca por faixa de n�meros ou individual, setado nos combox da p�gina. */
	private int tipoBusca = BUSCA_INDIVIDUAL;
	
	
	/** Indica que o usu�rio s� deseja ver os titulo criados por ele sejam visualizados como pendentes de exporta��o */
	private boolean apenasMinhasEntidadesPedentes = true;
	
	/** Biblioteca destino das importa��es */
	private int biblioteca;

	/** Lista das bibliotecas vinculadas ao usu�rio */
	private Collection<Biblioteca> bibliotecaList;

	/** Guarda os dados do arquivo, antes dele ser enviado para o usu�rio */
	private ByteArrayOutputStream outputStreamArquivo;
	
	/** Guarda as informa��es do T�tulo selecionado na pesquisa padr�o no acervo */
	private CacheEntidadesMarc tituloSelecionado;
	
	/**
	 *    Redireciona para uma p�gina onde o usu�rio vai poder buscar os t�tulo para serem exportados
	 * ou exportar aqueles que foram catalogados e salvos de forma autom�tica pelo sistema.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarExportacaoBibliografica()  throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		tipoOperacao = OPERACAO_BIBLIOGRAFICA;
		
		configuraListasEntidades(true);
		
		return telaExportarTituloAutoridade();
	}
	
	
	
	/**
	 *    Redireciona para uma p�gina de exporta��o de autoridades.
	 *
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarExportacaoAutoridades()  throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		tipoOperacao = OPERACAO_AUTORIDADE;
		
		configuraListasEntidades(true);
		
		return telaExportarTituloAutoridade();
		
	}
	
//	/**
//	 *   M�todo que adiciona t�tulo que o usu�rio buscar. Para o caso do usu�rio n�o saber os 
//	 *   n�meros do sistema do t�tulos a serem exportados .
//	 *
//	 *   <br/><br/>
//	 *   Chamado a partir do MBean: {@link PesquisaTituloCatalograficoMBean#exportarTituloCatalografico}
//	 *   M�todo n�o chamado por nenhuma p�gina jsp.
//	 * 
//	 * @param idTitulo
//	 * @return
//	 * @throws DAOException
//	 */
//	public String adicionarTituloPesquisado(int idTitulo) throws DAOException{
//		
//		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);
//		
//		CacheEntidadesMarc cache = dao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", idTitulo, true);
//		
//		if(! titulosExportacao.contains(cache)){
//			cache.setSelecionada(true);
//			adicionaTituloExportacao(cache);
//			titulosExportacaoAdicionados.add(cache);
//		}else{
//			addMensagemErro("T�tulo: "+cache.getNumeroDoSistema()+" j� foi adicionado � lista de exporta��o.");
//		}
//		
//		return telaExportarTituloAutoridade();
//	}
	
	
	
	/**
	 *   M�todo que adiciona autoridade que o usu�rio buscar. Para o caso do usu�rio n�o saber os 
	 *   n�meros do sistema da autoridade a serem exportados .
	 *
	 *   <br/><br/>
	 *   Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#exportarAutoridade()}
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 * 
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public String adicionarAutoridadePesquisada(int idAutoridade) throws DAOException{
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class);
		
		CacheEntidadesMarc cache = dao.findByExactField(CacheEntidadesMarc.class, "idAutoridade", idAutoridade, true);
		
		if(! autoridadesExportacao.contains(cache)){
			cache.setSelecionada(true);
			adicionaAutoridadeExportacao(cache);
			autoridadesExportacaoAdicionadas.add(cache);
		}else{
			addMensagemErro("Autoridade "+cache.getNumeroDoSistema()+" j� foi adicionada � lista de exporta��o.");
		}
		
		return telaExportarTituloAutoridade();
	}	
	
	
	/**
	 * M�todo que chama a busca padr�o para selecionar um t�tulo para exporta��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPesquisaExportacao() throws SegurancaException {
		
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		bean.limpaResultadoPesquisa();
		return bean.iniciarPesquisaSelecionarTitulo(this);
	}
	
	
	
	
	
	/**
	 *     Busca o t�tulo quando o usu�rio digitou individualmente o n�mero do sistema
	 * 
	 *     <br/><br/>
	 *     Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
	 *     M�todo n�o chamado por nenhuma p�gina jsp.
	 *  
	 * @param e
	 * @throws DAOException 
	 */
	public void adicionarNumerodoSistemaTitulo(ActionEvent e) throws DAOException{
		
		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);
		
		if(primeiroNumeroSistema == null && ultimoNumeroSistema == null && numeroDoSistema == null){
			addMensagemErro("Informe o n�mero do sistema do T�tulo que se deseja exportar");
		}else{
		
			if(tipoBusca == BUSCA_POR_FAIXA){
				
				if( primeiroNumeroSistema == null && ultimoNumeroSistema == null ){
					
					addMensagemErro("Informe o N�mero do Sistema Inicial e Final do T�tulo.");
				}else{
					if( primeiroNumeroSistema == null && ultimoNumeroSistema != null ){
						
						addMensagemErro("Informe o N�mero do Sistema Inicial do T�tulo.");
						
					}else{
						if( primeiroNumeroSistema != null && ultimoNumeroSistema == null ){
						
							addMensagemErro("Informe o N�mero do Sistema Final do T�tulo.");
						
						}else{
					
							if(ultimoNumeroSistema.compareTo(primeiroNumeroSistema) < 0 ){
								addMensagemErro("O N�mero do Sistema Final Precisa ser Maior que o N�mero do Sistema Inicial.");
							}else{
							
								Long qtdTitulos = dao.countByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
								
								if( qtdTitulos > 100){
									addMensagemErro("S� podem ser adicionados 100 t�tulos por n�meros de sistema por vez");
								}else{
								
									List<CacheEntidadesMarc> listaCache = dao.findByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
									
									boolean possuiPeloMenosUmTitulo = false;
									
									forEntidades:
									for (CacheEntidadesMarc cacheEntidadesMarc : listaCache) {
										
										if(! cacheEntidadesMarc.isCatalogado()){
											addMensagemErro("T�tulo: "+cacheEntidadesMarc.getNumeroDoSistema()+" "+(cacheEntidadesMarc.getAutor() != null ? cacheEntidadesMarc.getAutor(): "")
													+" "+(cacheEntidadesMarc.getTitulo() != null ? cacheEntidadesMarc.getTitulo(): "")+" n�o p�de ser exportado, pois sua cataloga��o n�o foi finalizada. ");
											continue forEntidades;
										}
										
										if(! titulosExportacao.contains(cacheEntidadesMarc)){
											cacheEntidadesMarc.setSelecionada(true);
											adicionaTituloExportacao(cacheEntidadesMarc);
											titulosExportacaoAdicionados.add(cacheEntidadesMarc);
											possuiPeloMenosUmTitulo = true;
											
											primeiroNumeroSistema = null;
											ultimoNumeroSistema = null;
											numeroDoSistema = null;
										}
									}
									
									if(! possuiPeloMenosUmTitulo && listaCache.size() > 0){
										addMensagemErro("Todos os T�tulo j� foram adicionados � lista de exporta��o.");
									}
									
									if(listaCache.size() == 0){
										addMensagemErro("T�tulos com o n�mero do sistema entre: "+primeiroNumeroSistema+" e "+ultimoNumeroSistema+" n�o encontrados.");
									}
								}
							}
						}
					}
				}
			}else{
				
				if(numeroDoSistema != null){
					CacheEntidadesMarc temp = dao.findByNumeroSistema(numeroDoSistema);
					
					if(temp == null)
						addMensagemErro("T�tulo com o n�mero do sistema "+numeroDoSistema+" n�o encontrado.");
					else{
						
						if(! temp.isCatalogado()){
							addMensagemErro("T�tulo: "+temp.getNumeroDoSistema()+" "+(temp.getAutor() != null ? temp.getAutor(): "")
									+" "+(temp.getTitulo() != null ? temp.getTitulo(): "")+" n�o p�de ser exportado, pois sua cataloga��o n�o foi finalizada. ");
						}else{
						
							if(! titulosExportacao.contains(temp)){
								temp.setSelecionada(true);
								adicionaTituloExportacao(temp);
								titulosExportacaoAdicionados.add(temp);
								
								primeiroNumeroSistema = null;
								ultimoNumeroSistema = null;
								numeroDoSistema = null;
								
							}else{
								addMensagemErro("T�tulo com o n�mero do sistema: "+numeroDoSistema+" j� adicionado � lista de exporta��o.");
							}
						}
						
					}
				}else{
					addMensagemErro("Informe o n�mero do sistema do T�tulo que se deseja exportar");
				}
			}
			
		}
		
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
		
	}
	
	
	
	/**
	 *     Busca o t�tulo quando o usu�rio digitou individualmente o n�mero do sistema
	 * 
	 *     <br/><br/>
	 *     Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
	 *     M�todo n�o chamado por nenhuma p�gina jsp.
	 *     
	 * @param e
	 * @throws DAOException 
	 */
	public void adicionarNumerodoSistemaAutoridade(ActionEvent e) throws DAOException{
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class);
		
		if(primeiroNumeroSistema == null && ultimoNumeroSistema == null && numeroDoSistema == null){
			addMensagemErro("Informe o N�mero do Sistema da Autoridade que se deseja exportar.");
		}else{
		
			if(tipoBusca == BUSCA_POR_FAIXA){
				
				if( primeiroNumeroSistema == null && ultimoNumeroSistema == null ){
					addMensagemErro("Informe o N�mero do Sistema Inicial e Final da Autoridade.");
				}else{
					if( primeiroNumeroSistema == null && ultimoNumeroSistema != null ){
				
						addMensagemErro("Informe o N�mero do Sistema Inicial da Autoridade.");
						
					}else{
						if( primeiroNumeroSistema != null && ultimoNumeroSistema == null ){
						
							addMensagemErro("Informe o N�mero do Sistema Final da Autoridade.");
						
						}else{
					
							if(ultimoNumeroSistema.compareTo(primeiroNumeroSistema) < 0 ){
								addMensagemErro("O N�mero do Sistema Final Precisa ser Maior que o N�mero do Sistema Inicial.");
							}else{
					
								
								Long qtdAutoridades = dao.countByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
								
								if(qtdAutoridades > 100){
									addMensagemErro("S� podem ser adicionadas 100 autoridades por n�meros do sistema por vez");
								}else{
								
									List<CacheEntidadesMarc> listaCache = dao.findByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
									
									boolean possuiPeloMenosUmaAutoridade = false;
									
									forEnitdades:
									for (CacheEntidadesMarc cacheEntidadesMarc : listaCache) {
										
										if(! cacheEntidadesMarc.isCatalogado()){
											
											if(StringUtils.notEmpty(cacheEntidadesMarc.getEntradaAutorizadaAutor())){
												addMensagemErro("Autoridade: "+ cacheEntidadesMarc.getNumeroDoSistema()+" "
														+(cacheEntidadesMarc.getEntradaAutorizadaAutor()!= null ? cacheEntidadesMarc.getEntradaAutorizadaAutor() : "")+" n�o p�de ser exportada, pois sua cataloga��o n�o foi finalizada. ");
											}	
											
											if(StringUtils.notEmpty(cacheEntidadesMarc.getEntradaAutorizadaAssunto())){
												addMensagemErro("Autoridade: "+ cacheEntidadesMarc.getNumeroDoSistema()+" "
														+(cacheEntidadesMarc.getEntradaAutorizadaAssunto()!= null ? cacheEntidadesMarc.getEntradaAutorizadaAssunto() : "")+" n�o p�de ser exportada, pois sua cataloga��o n�o foi finalizada. ");
											}	
											
											continue forEnitdades;
										}
										
										
										if(! autoridadesExportacao.contains(cacheEntidadesMarc)){
											cacheEntidadesMarc.setSelecionada(true);
											adicionaAutoridadeExportacao(cacheEntidadesMarc);
											autoridadesExportacaoAdicionadas.add(cacheEntidadesMarc);
											possuiPeloMenosUmaAutoridade = true;
											
											primeiroNumeroSistema = null;
											ultimoNumeroSistema = null;
											numeroDoSistema = null;
										}
									}
									
				
									if(! possuiPeloMenosUmaAutoridade && listaCache.size() > 0){
										addMensagemErro("Todas as Autoridades j� foram adicionados � lista de exporta��o.");
									}
									
									if(listaCache.size() == 0){
										addMensagemErro("Autoridades com o n�mero do sistema entre: "+primeiroNumeroSistema+" e "+ultimoNumeroSistema+" n�o encontradas.");
									}
								
								}
								
							}
						}
					}
				}
				
			}else{
				
			
				if(numeroDoSistema != null){
				
					CacheEntidadesMarc temp = dao.findByNumeroSistema(numeroDoSistema);
					
					if(temp == null)
						addMensagemErro("Autoridade com o n�mero do sistema "+numeroDoSistema+" n�o encontrada.");
					else{
						
						if(! temp.isCatalogado()){
							
							if(StringUtils.notEmpty(temp.getEntradaAutorizadaAutor())){
								addMensagemErro("Autoridade: "+ temp.getNumeroDoSistema()+" "
										+(temp.getEntradaAutorizadaAutor()!= null ? temp.getEntradaAutorizadaAutor() : "")+" n�o p�de ser exportada, pois sua cataloga��o n�o foi finalizada. ");
							}	
							
							if(StringUtils.notEmpty(temp.getEntradaAutorizadaAssunto())){
								addMensagemErro("Autoridade: "+ temp.getNumeroDoSistema()+" "
										+(temp.getEntradaAutorizadaAssunto()!= null ? temp.getEntradaAutorizadaAssunto() : "")+" n�o p�de ser exportada, pois sua cataloga��o n�o foi finalizada. ");
							}
							
						}else{
							if(! autoridadesExportacao.contains(temp)){
								temp.setSelecionada(true);
								adicionaAutoridadeExportacao(temp);
								autoridadesExportacaoAdicionadas.add(temp);
								
								primeiroNumeroSistema = null;
								ultimoNumeroSistema = null;
								numeroDoSistema = null;
								
							}else{
								addMensagemErro("Autoridade com o n�mero do sistema: "+numeroDoSistema+" j� adicionada � lista de exporta��o.");
							}
						}
					}	
					
				}else{
					addMensagemErro("Informe o N�mero do Sistema da Autoridade que se deseja exportar.");
				}
			}
			
		}
		
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
		
	}
	
	
	
	/**
	 * M�todo que adiciona um t�tulo mantendo o sincronismo com o dado model
	 */
	private void adicionaTituloExportacao(CacheEntidadesMarc c){
		
		if(titulosExportacao == null)
			titulosExportacao = new ArrayList<CacheEntidadesMarc>();
		
		titulosExportacao.add(c);
		dataModelTitulos = new ListDataModel(titulosExportacao);
	}
	
	
	/**
	 *  M�todo que adiciona uma autoridade mantendo o sincronismo com o dado model
	 */
	private void adicionaAutoridadeExportacao(CacheEntidadesMarc c){
		
		if(autoridadesExportacao == null)
			autoridadesExportacao = new ArrayList<CacheEntidadesMarc>();
		
		autoridadesExportacao.add(c);
		dataModelAutoridades = new ListDataModel(autoridadesExportacao);
	}
	
	
	
	
	/**
	 *       Atualiza a lista de t�tulos pendentes para o usu�rio, apenas os criados por ele, ou 
	 *       criados por outros usu�rios
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
	 *
	 * @param evt
	 * @throws DAOException 
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public void atualizaEntidadesPendentes(ValueChangeEvent evt) throws SegurancaException, DAOException {
		
		apenasMinhasEntidadesPedentes = ( Boolean ) evt.getNewValue();
		
		configuraListasEntidades(false);
		
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
	}
	
	
	
	/**
	 *       Configura as tr�s listas de materias para a gera��o de etiquetas. A de materias pendentes
	 *   salvos no banco, a de materiais adicionados na p�gina pelo usu�rio e a de materiais marcados 
	 *   para gera��o do c�digo de barras.
	 */
	private void configuraListasEntidades(boolean iniciandoCasoDeUso) throws DAOException{
		configuraListasEntidades(iniciandoCasoDeUso, true);
	}
		
		
	
	/**
	 *       Configura as tr�s listas de materias para a gera��o de etiquetas. A de materias pendentes
	 *   salvos no banco, a de materiais adicionados na p�gina pelo usu�rio e a de materiais marcados 
	 *   para gera��o do c�digo de barras.
	 */
	private void configuraListasEntidades(boolean iniciandoCasoDeUso, boolean iniciarSelecionado) throws DAOException{
	
		selecionaTodos = true;
		
		if(isCooperacaoBibliografica()){
			titulosExportacaoGravadosDoUsuario.clear();
			titulosExportacao.clear();
		
			if(apenasMinhasEntidadesPedentes){
			
				// carrega os t�tulos que o usu�rio catalogou mas ainda n�o exportou.
				List<EntidadesMarcadasParaExportacao> entidades = getDAO(TituloCatalograficoDao.class)
						.encontraTitulosPendentesExportacaoByUsuario(getUsuarioLogado().getId());
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
						titulosExportacaoGravadosDoUsuario.add( BibliotecaUtil.obtemDadosTituloCache(entidade.getIdTituloCatalografico()));
				}
				
			}else{
				
				// carrega todos os t�tulos catalogados mas ainda n�o exportou.
				List<EntidadesMarcadasParaExportacao> entidades = getDAO(TituloCatalograficoDao.class)
						.encontraTitulosPendentesExportacao();
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
					titulosExportacaoGravadosDoUsuario.add( BibliotecaUtil.obtemDadosTituloCache(entidade.getIdTituloCatalografico()));	
				}
				
			}
			
			// inicialmente todos gravados s�o selecionados para gera��o etiqueta
			titulosExportacao.addAll(titulosExportacaoGravadosDoUsuario);
			
			for (CacheEntidadesMarc c : titulosExportacao) {
				c.setSelecionada(iniciarSelecionado);
			}
			
			if(! iniciandoCasoDeUso){ // chamado depois que o usu�rio pode ter adicionado algum material � lista
				
				titulosExportacao.addAll(titulosExportacaoAdicionados);
				
				
			}
			
			dataModelTitulos = new ListDataModel(titulosExportacao);
			
		}
		
		
		
		if(isCooperacaoAutoridades()){
			
			autoridadesExportacaoGravadasDoUsuario.clear();
			autoridadesExportacao.clear();
		
			if(apenasMinhasEntidadesPedentes){
			
				// carrega as autoridades que o usu�rio catalogou mas inda n�o exportou.
				List<EntidadesMarcadasParaExportacao> entidades = getDAO(AutoridadeDao.class)
					.encontraAutoridadesPendentesExportacaoByUsuario(getUsuarioLogado().getId());
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
						autoridadesExportacaoGravadasDoUsuario.add( BibliotecaUtil.obtemDadosAutoridadeEmCache(entidade.getIdAutoridade()));
				}
				
			}else{
				
				// carrega todos os t�tulos catalogados mas ainda n�o exportou.
				List<EntidadesMarcadasParaExportacao> entidades =  getDAO(AutoridadeDao.class)
						.encontraAutoridadesPendentesExportacao();
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
					autoridadesExportacaoGravadasDoUsuario.add( BibliotecaUtil.obtemDadosAutoridadeEmCache(entidade.getIdAutoridade()));
				}
				
			}
			
			// inicialmente todos gravados s�o selecionados para gera��o etiqueta
			autoridadesExportacao.addAll(autoridadesExportacaoGravadasDoUsuario);
			
			for (CacheEntidadesMarc c : autoridadesExportacao) {
				c.setSelecionada(iniciarSelecionado);
			}
			
			if(! iniciandoCasoDeUso){ // chamado depois que o usu�rio ter adicionado algum material � lista
				autoridadesExportacao.addAll(autoridadesExportacaoAdicionadas);
			}
			
			dataModelAutoridades = new ListDataModel(autoridadesExportacao);
			
		}
		
	}
	
	/**
	 *  M�todo chamado para selecionar ou deselecionar todas as entidades marcadas para exporta��o.
	 *
	 *  <br/><br/>
	 *  Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
	 *  
	 *  
	 * @param evt
	 */
	public void selecionarDeselecionarTodos(ActionEvent evt){
		
		if(isCooperacaoBibliografica()){
			
			if(selecionaTodos){
			
				for (CacheEntidadesMarc cache : titulosExportacao) {
					cache.setSelecionada(true);
				}
			
			}else{
				for (CacheEntidadesMarc cache : titulosExportacao) {
					cache.setSelecionada(false);
				}
			}
		}
		
		if(isCooperacaoAutoridades()){
			
			if(selecionaTodos){
				for (CacheEntidadesMarc cache : autoridadesExportacao) {
					cache.setSelecionada(true);
				}
			}else{
				for (CacheEntidadesMarc cache : autoridadesExportacao) {
					cache.setSelecionada(false);
				}
			}
		}
	}
	
	
	/**
	 * 
	 *   Apaga a entidade da lista de exporta��o. Se foi uma entidade adicionado pelo usu�rio, s� apaga
	 * da lista, se foi uma entidade que o sistema tinha salvo, remove-se a entidade que o sistema salvou
	 * do estado de pendente. 
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerEntidadesExportacao() throws ArqException {
		
		if(isCooperacaoBibliografica()){
			
			if(dataModelTitulos != null){
				
				CacheEntidadesMarc tituloRemovido = (CacheEntidadesMarc) dataModelTitulos.getRowData();
				
				if(titulosExportacaoAdicionados.contains(tituloRemovido)){ // s� apaga na mem�ria
					
					titulosExportacaoAdicionados.remove(dataModelTitulos.getRowData());
					titulosExportacao.remove(dataModelTitulos.getRowData());
					
					addMensagemInformation("T�tulo "+tituloRemovido.getNumeroDoSistema()+" removido da lista com sucesso.");
					
				}else{
					if(titulosExportacaoGravadosDoUsuario.contains(tituloRemovido)){ // precisa apagar os que o sistema salvou
						
						apagaEntidadeGravadaSistema(tituloRemovido);
						titulosExportacao.remove(dataModelTitulos.getRowData());
						
						addMensagemInformation("T�tulo "+tituloRemovido.getNumeroDoSistema()+" removido da lista com sucesso.");
					}
				}
				
				
			}
			
		}
		
		if(isCooperacaoAutoridades()){
			if(dataModelAutoridades != null){
				
				CacheEntidadesMarc autoridadeRemovida = (CacheEntidadesMarc) dataModelAutoridades.getRowData();
				
				if(autoridadesExportacaoAdicionadas.contains(autoridadeRemovida)){ // s� apaga na mem�ria
					
					autoridadesExportacaoAdicionadas.remove(dataModelAutoridades.getRowData());
					autoridadesExportacao.remove(dataModelAutoridades.getRowData());
					
					addMensagemInformation("Autoridade "+autoridadeRemovida.getNumeroDoSistema()+" removida da lista com sucesso.");
					
				}else{
					if(autoridadesExportacaoGravadasDoUsuario.contains(autoridadeRemovida)){ // precisa pagar os que o sistema salvou
						
						apagaEntidadeGravadaSistema(autoridadeRemovida);
						autoridadesExportacao.remove(dataModelAutoridades.getRowData());
						
						addMensagemInformation("Autoridade "+autoridadeRemovida.getNumeroDoSistema()+" removida da lista com sucesso.");
					}
				}
			}
		}
		
		
		return telaExportarTituloAutoridade();
	}
	
	
	/**
	 * 
	 * Remove todas os t�tulos selecionadas
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String removerTitulosSelecionados() throws ArqException{
		
		List<CacheEntidadesMarc> listaTempRemocao = new ArrayList<CacheEntidadesMarc>();
		
		for (CacheEntidadesMarc cache : titulosExportacao) {
			
			if(cache.isSelecionada()){
				listaTempRemocao.add(cache);
			}
			
		}
		
		if( listaTempRemocao.size() > 0 ){
		
			for (CacheEntidadesMarc cache : listaTempRemocao) {
				
				if(titulosExportacaoAdicionados.contains(cache)){
					titulosExportacaoAdicionados.remove(cache);
					titulosExportacao.remove(cache);
					
				}else{
					if(titulosExportacaoGravadosDoUsuario.contains(cache)){ // precisa pagar os que o sistema salvou
						apagaEntidadeGravadaSistema(cache);
						titulosExportacao.remove(cache);
					}
				}
			}
			
			addMensagemInformation("T�tulos removidos da lista com sucesso.");
		
		}else{
			addMensagemErro("N�o existem T�tulos Selecionados.");
		}
		
		return telaExportarTituloAutoridade();
	}
	
	
	/**
	 *    Remove todas as autoridades selecionadas 
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String removerAutoridadesSelecionadas() throws ArqException{
		
		List<CacheEntidadesMarc> listaTempRemocao = new ArrayList<CacheEntidadesMarc>();
		
		for (CacheEntidadesMarc cache : autoridadesExportacao) {
			
			if(cache.isSelecionada()){
				listaTempRemocao.add(cache);
			}
		}
		
		
		if( listaTempRemocao.size() > 0 ){
			
		
			for (CacheEntidadesMarc cache : listaTempRemocao) {
				
				if(autoridadesExportacaoAdicionadas.contains(cache)){
					autoridadesExportacaoAdicionadas.remove(cache);
					autoridadesExportacao.remove(cache);
					
				}else{
					if(autoridadesExportacaoGravadasDoUsuario.contains(cache)){ // precisa apagar do cache os registros que o sistema salvou
						apagaEntidadeGravadaSistema(cache);
						autoridadesExportacao.remove(cache);
						
					}
				}
			}
			
			addMensagemInformation("Autoridades removidas da lista com sucesso.");
			
		}else{
			addMensagemErro("N�o existem Autoridades Selecionadas.");
		}
		
		return telaExportarTituloAutoridade();
	}
	
	
	
	/**
	 * Apaga as entidades que o sistema tinha gravado como pendentes de importa��o porque o usu�rio 
	 * escolheu apagar.
	 */
	private void apagaEntidadeGravadaSistema(CacheEntidadesMarc cacheGravada) throws ArqException{
		
		GenericDAO dao = getGenericDAO();
		
		if(isCooperacaoBibliografica()){
		
			EntidadesMarcadasParaExportacao entidade =  dao.findByExactField(EntidadesMarcadasParaExportacao.class, "idTituloCatalografico", 
					 cacheGravada.getIdTituloCatalografico(), true);
	
			prepareMovimento(ArqListaComando.REMOVER);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(ArqListaComando.REMOVER);
			mov.setObjMovimentado(entidade);
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagemErro("Erro ao apagar os t�tulos marcados para exporta��o");
			}
		}
		
		if(isCooperacaoAutoridades()){
			
			EntidadesMarcadasParaExportacao entidade =  dao.findByExactField(EntidadesMarcadasParaExportacao.class, "idAutoridade", 
					 cacheGravada.getIdAutoridade(), true);

			prepareMovimento(ArqListaComando.REMOVER);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(ArqListaComando.REMOVER);
			mov.setObjMovimentado(entidade);
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagemErro("Erro ao apagar as autoridades marcadas para exporta��o");
			}
			
		}
	
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
		
	}
	
	
	
	
	/**
	 * Habilita e desabilita a digita��o do nome do arquivo
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 * 
	 * @param evt
	 */
	public void abilitarExportacaoFGV(ActionEvent evt){
		tipoCodificacao = ISO_2709;
	}
	
	
	/**
	 *    Retorna a quantidade de entidades pendentes de exporta��o buscadas
	 *<br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 *
	 * @return
	 */
	public int getQuantidadeEntidadesPendentes(){
		
		if(isCooperacaoBibliografica()){
			return  titulosExportacao.size();
		}
		
		if(isCooperacaoAutoridades()){
			return  autoridadesExportacao.size();
		}
		
		return 0;
	}
	
	
	
	/**
	 *    M�todo que realiza a exporta��o dos t�tulos ou autoridades marcadas.
	 *    Busca o T�tulo no banco, converte-o para o formato do MARC4J e manda o MARC4J gerar 
	 * um arquivo na codifica��o desejada.
	 * <br/>
	 *    Entidades marcadas como pendentes de exporta��o pelo sistema quando exportadas � 
	 *  removida essa pend�ncia.
	 *
	 *    <br/><br/>
	 *	  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 *
	 * @param idTitulo
	 * @throws IOException
	 * @throws ArqException 
	 */
	public String exportarArquivo(ActionEvent evt) throws IOException, ArqException{
		
		List<RegistroExportacaoCooperacaoTecnica> listaRegistros 
				= new ArrayList<RegistroExportacaoCooperacaoTecnica>();
		
		TituloCatalograficoDao dao  = null;
		ArquivoDeCargaNumeroControleFGVDao arquivoDao = null;
		
		// pega os marcados pelo usu�rio
		List<CacheEntidadesMarc> titulosMarcados = new ArrayList<CacheEntidadesMarc>();
		List<CacheEntidadesMarc> autoridadesMarcadas = new ArrayList<CacheEntidadesMarc>();
		
		
		if(isCooperacaoBibliografica()){
			
			
			for (CacheEntidadesMarc cacheEntidadesMarc : titulosExportacao) {
				
				if(cacheEntidadesMarc.isSelecionada())
					titulosMarcados.add(cacheEntidadesMarc);
				
			}
			
			if(titulosMarcados.size() == 0){
				addMensagemErro("N�o existem t�tulos selecionados para exporta��o.");
				return null;
			}
		}
		
		if(isCooperacaoAutoridades()){
			
			for (CacheEntidadesMarc cacheEntidadesMarc : autoridadesExportacao) {
				if(cacheEntidadesMarc.isSelecionada())
					autoridadesMarcadas.add(cacheEntidadesMarc);
			}
			
			if(autoridadesMarcadas.size() == 0){
				addMensagemErro("N�o existem autoridades selecionadas para exporta��o.");
				return null;
			}
		}
		
		try{
			
			dao = getDAO(TituloCatalograficoDao.class);
		
			Biblioteca bibliotecaUsuarioLogado = dao.findByPrimaryKey(biblioteca, Biblioteca.class);
			
			if(bibliotecaUsuarioLogado == null || bibliotecaUsuarioLogado.getId() <=0 ){
				addMensagemErro("Selecione uma biblioteca para a exporta��o.");
				return null;
			}
			
			
			//HttpServletResponse response = getResponse();
			
			/* *************************************************************************************
			 *    Streams tempor�rios onde os dados de cada registro MARC se�o salvos temporariamente.
			 *    � preciso guardar cada um separadamente, caso seja exporta��o para a FGV, porque �
			 * necess�rio contar o tamanho de cada registro MARC para gerar o arquivo PCS.
			 * *************************************************************************************/
			List<ByteArrayOutputStream> outputSteams = new ArrayList<ByteArrayOutputStream>();
			
			
			List<ArquivoDeCargaNumeroControleFGV> arquivosCarregados 
												= new ArrayList<ArquivoDeCargaNumeroControleFGV>();
			
			
			Integer proximoNumeroDisponivelExportacao = null;
			
			if(isExportarFGV()){
				arquivoDao = getDAO(ArquivoDeCargaNumeroControleFGVDao.class);
				
				if(isCooperacaoBibliografica())
					arquivosCarregados = arquivoDao.findAllArquivosTituloAtivosOrderByDataCarga();
			
				if(isCooperacaoAutoridades())
					arquivosCarregados = arquivoDao.findAllArquivosAutoridadeAtivosOrderByDataCarga();
				
			}
			
			
			if(isCooperacaoBibliografica()){
				
				// retira os repetidos
				Set<CacheEntidadesMarc> titulosNaoRepetidos = new HashSet<CacheEntidadesMarc>(titulosMarcados);
				
				for (CacheEntidadesMarc c : titulosNaoRepetidos) {
				
					MarcStreamWriter writer;
					
					ByteArrayOutputStream outputStreamTemp = new ByteArrayOutputStream(); 
					
					if (exportarFGV || tipoCodificacao == ISO_2709)
						writer = new MarcStreamWriter(outputStreamTemp); // codifi��o ISO 2709
					else
						writer = new MarcStreamWriter(outputStreamTemp,"UTF-8");
					
					c = dao.refresh(c);
					
					TituloCatalografico t = dao.findByPrimaryKey(c.getIdTituloCatalografico(), TituloCatalografico.class);
					
					Record record = null;
					
					if(isExportarFGV()){
						
						proximoNumeroDisponivelExportacao = BibliotecaUtil.encontraProximoNumeroDisponivelExportacaoFGV(arquivosCarregados);
						
						// N�o existem mais 
						// gera o arquivo de erro. n�o tem como mostrar uma mensagem para o usu�rio porque o controle da tela j� foi perdido.
						if(proximoNumeroDisponivelExportacao == null){
							addMensagemErro("N�o existem mais n�meros de controle para a exporta��o para a FGV, "
									+"por favor carregue um novo arquivo com os n�meros de controle do cat�logo coletivo."); 
							return null;
						}else{
							
							/*
							 *  Tem que gerar um arquivo com o nome RN00000001.CC ou  RN00000001.AC
							 *  
							 *  No caso de v�rias exporta��es no mesmo aquivo, vai conter o n�mero do �ltimo
							 *  
							 *  Conversa com o analista para FGV. 05/05/2011 as 10:03 <br/>
							 *  <p>
							 *  Jadson diz:
							 *	Essa sequencia: 000001, 0000002, 0000xxxx,   tem alguma importancia para o envio dos arquivos para o cat�logo coletivo ?
                             *  </p>
							 *  
							 *  <p>
							 *  Robert Bessa diz:
 							 *	nao tem importancia pro catalogo
                             *  </p>
							 *  
							 */
							nomeArquivoFGV = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_GERACAO_NOME_ARQUIVO_FGV)
														+proximoNumeroDisponivelExportacao;
						}	
							
					}
					
					try{
					
						if (exportarFGV || tipoCodificacao == ISO_2709)
							record= montaInfoMARC4J(t, true, proximoNumeroDisponivelExportacao);
						else
							record = montaInfoMARC4J(t, false, proximoNumeroDisponivelExportacao);
					
					}catch(NegocioException ne){
						addMensagens(ne.getListaMensagens()); 
						return null;
					}
						
					writer.write(record);
					
					outputStreamTemp.flush();
					outputStreamTemp.close();
					
					outputSteams.add(outputStreamTemp);
					
					if(exportarFGV){
						listaRegistros.add(new RegistroExportacaoCooperacaoTecnica(t, new Date(), 
							bibliotecaUsuarioLogado, proximoNumeroDisponivelExportacao) );
					}else{
						listaRegistros.add(new RegistroExportacaoCooperacaoTecnica(t, new Date(), 
								bibliotecaUsuarioLogado) );
					}
					
				}
				
			} // Cooperacao Biblioteg�rica
			
			if(isCooperacaoAutoridades()){
				
				
				// retira os repetidos
				Set<CacheEntidadesMarc> autoridadesNaoRepetidas = new HashSet<CacheEntidadesMarc>(autoridadesMarcadas);
				
				MarcStreamWriter writer;
				
				ByteArrayOutputStream outputStreamTemp = new ByteArrayOutputStream(); 
				
				if (exportarFGV || tipoCodificacao == ISO_2709)
					writer = new MarcStreamWriter(outputStreamTemp); // codifi��o ISO 2709
				else
					writer = new MarcStreamWriter(outputStreamTemp,"UTF-8");
				
				for (CacheEntidadesMarc c : autoridadesNaoRepetidas) {
					
					c = dao.refresh(c);
							
					Record record = null;
					
					if(isExportarFGV()){
						
						proximoNumeroDisponivelExportacao = BibliotecaUtil.encontraProximoNumeroDisponivelExportacaoFGV(arquivosCarregados);
						
						// N�o existem mais n�meros dispon�veis
						// gera o arquivo de erro. n�o tem como mostrar uma mensagem para o usu�rio porque o controle da tela j� foi perdido.
						if(proximoNumeroDisponivelExportacao == null){
							addMensagemErro("N�o existem mais n�meros de controle para a exporta��o para a FGV, "
									+"por favor carregue um novo arquivo com os n�meros de controle do cat�logo coletivo.");
							return null;
						}else{
							
							/*
							 *  Tem que gerar um arquivo com o nome RN00000001.CC ou  RN00000001.AC
							 *  
							 *  No caso de v�rias exporta��es no mesmo aquivo, vai conter o n�mero do �ltimo.
							 *  
							 *  Conversa com o analista para FGV. 05/05/2011 as 10:03 <br/>
							 *  <p>
							 *  Jadson diz:
							 *	Essa sequencia: 000001, 0000002, 0000xxxx,   tem alguma importancia para o envio dos arquivos para o cat�logo coletivo ?
                             *  </p>
							 *  
							 *  <p>
							 *  Robert Bessa diz:
 							 *	nao tem importancia pro catalogo
                             *  </p>
                             *  
							 */
							nomeArquivoFGV = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_GERACAO_NOME_ARQUIVO_FGV)
														+proximoNumeroDisponivelExportacao;
						}
					}
					
					try{
					
					if (exportarFGV || tipoCodificacao == ISO_2709)
						record= montaInfoMARC4J(dao.findByPrimaryKey(c.getIdAutoridade(), Autoridade.class), true, proximoNumeroDisponivelExportacao);
					else
						record = montaInfoMARC4J(dao.findByPrimaryKey(c.getIdAutoridade(), Autoridade.class), false, proximoNumeroDisponivelExportacao);
					
					}catch(NegocioException ne){
						addMensagens(ne.getListaMensagens()); 
						return null;
					}
					
					writer.write(record);
				
					outputStreamTemp.flush();
					outputStreamTemp.close();
					
					outputSteams.add(outputStreamTemp);
					
					if(exportarFGV){
						listaRegistros.add(new RegistroExportacaoCooperacaoTecnica(dao.findByPrimaryKey(c.getIdAutoridade(), Autoridade.class), new Date(), 
								bibliotecaUsuarioLogado, proximoNumeroDisponivelExportacao ) );
					}else{
						listaRegistros.add(new RegistroExportacaoCooperacaoTecnica(dao.findByPrimaryKey(c.getIdAutoridade(), Autoridade.class), new Date(), 
								bibliotecaUsuarioLogado ) );
					}
					
				}	
				
			}  /// Coopera��o de autoridades 

			
			
			

			outputStreamArquivo = new ByteArrayOutputStream();
			
			DataOutputStream dos  = new DataOutputStream(outputStreamArquivo); // Resposta definitiva
			
			if(isExportarFGV()){
				
				
				dos.write( BibliotecaUtil.formataArquivoBibliotegraficoExportacaoParaPCS(outputSteams));
				
				/*
				 *  Para testes, quando quizer ler o arquivo gerado descomente essa linha e comente a de cima.
				 *  
				for (ByteArrayOutputStream byteArrayOutputStream : outputSteams) {
					dos.write( byteArrayOutputStream.toByteArray());
				}
				*/
				
			}else{
				
				// Escreve os bytes de cada registro MARC na sa�da
				for (ByteArrayOutputStream byteArrayOut : outputSteams) {
					dos.write(byteArrayOut.toByteArray());	
				}
			}
			
			prepareMovimento(SigaaListaComando.ACOES_AUXILIARES_EXPORTA_TITULO_AUTORIDADE);
			
			
			//////////////////////////////////////////////////////////////////////////////////
			//////////////       remove os t�tulos gravados que foram exportados /////////////
			
			List<CacheEntidadesMarc> entidadesTemp = new ArrayList<CacheEntidadesMarc>();
			
			if(isCooperacaoBibliografica()){
				
				for (CacheEntidadesMarc cacheEntidadesMarc : titulosMarcados) {
					if ( titulosExportacaoGravadosDoUsuario.contains(cacheEntidadesMarc) ) {
						entidadesTemp.add(cacheEntidadesMarc);
					}
				}
			}
			
			if(isCooperacaoAutoridades()){
				for (int i = 0; i < autoridadesMarcadas.size(); i++) {
					
					// usu�rio exportou um t�tulo que estava gravado
					if ( autoridadesExportacaoGravadasDoUsuario.contains(autoridadesMarcadas.get(i))){
						entidadesTemp.add(autoridadesMarcadas.get(i));
					}
				}
			}
					
			MovimentoAcoesAuxiliaresExportaTituloAutoridade movimento 
				= new MovimentoAcoesAuxiliaresExportaTituloAutoridade(listaRegistros
						, entidadesTemp
						, isCooperacaoBibliografica(), arquivosCarregados);
			
			movimento.setCodMovimento(SigaaListaComando.ACOES_AUXILIARES_EXPORTA_TITULO_AUTORIDADE);
			
			try {
				execute(movimento);
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens()); 
			}
			
			//////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////

			configuraListasEntidades(false);
			
			return telaExportarTituloAutoridade();
			
		} finally {
			if (dao != null) dao.close();
			if(arquivoDao != null) arquivoDao.close();
		}
	
	}
	
	
	
	
	/**
	 *   Met�do chamado para exportar um t�tulo na parte p�blica da biblioteca. 
	 *   Aqui o usu�rio n�o vai escolher. Ele vai sempre ser exportado no formato ISO2709
	 *
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 *    
	 * @return
	 * @throws IOException
	 */
	public String exportarArquivoPublico() throws IOException{
		
		int idTituloExportacao = getParameterInt("idTituloParaExportacao", 0);
		
		this.tipoOperacao = OPERACAO_BIBLIOGRAFICA;
		
		TituloCatalograficoDao dao  = null;
		
		try{
			
			dao = getDAO(TituloCatalograficoDao.class);
		
			TituloCatalografico tituloExportacao = dao.findByPrimaryKey(idTituloExportacao, TituloCatalografico.class);
			
			HttpServletResponse response = getResponse();
			
			// Escreve o registro na sa�da
			MarcStreamWriter writer = new MarcStreamWriter(response.getOutputStream()); // codifi��o ISO 2709
			
			Record record= montaInfoMARC4J(tituloExportacao, true, null);
			
			writer.write(record);
			
			response.setContentType("Content-Type: text/html; charset=ISO2709");
			response.addHeader("Content-Disposition", "attachment; filename=titulo_ISO2709.txt");
			
		} catch (Exception e){
			e.printStackTrace();
			geraArquivoDeErro(e.getMessage());
		} finally {
			
			finalizaResposta();
			
			if (dao != null){
				dao.close();
			}
		}
		
		return null; // fica na mesma p�gina da pesquisa p�blica
	}
	
	
	/**
	 *   Met�do chamado para exportar um t�tulo na parte p�blica da biblioteca. 
	 *   Aqui o usu�rio n�o vai escolher. Ele vai sempre ser exportado no formato ISO2709
	 *
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 *    
	 * @return
	 * @throws IOException
	 */
	public String exportarArquivoAutoridadesPublico() throws IOException{
		
		int idAutoridadeExportacao = getParameterInt("idAutoridadeParaExportacao", 0);
		
		this.tipoOperacao = OPERACAO_AUTORIDADE;
		
		AutoridadeDao dao  = null;
		
		try{
			
			dao = getDAO(AutoridadeDao.class);
		
			Autoridade autoridadeExportacao = dao.findByPrimaryKey(idAutoridadeExportacao, Autoridade.class);
			
			HttpServletResponse response = getResponse();
			
			// Escreve o registro na sa�da
			MarcStreamWriter writer = new MarcStreamWriter(response.getOutputStream()); // codifi��o ISO 2709
			
			Record record= montaInfoMARC4J(autoridadeExportacao, true, null);
			
			writer.write(record);
			
			response.setContentType("Content-Type: text/html; charset=ISO2709");
			response.addHeader("Content-Disposition", "attachment; filename=autoridade_ISO2709.txt");
			
		} catch (Exception e){
			e.printStackTrace();
			geraArquivoDeErro(e.getMessage());
		} finally {
			
			finalizaResposta();
			
			if (dao != null){
				dao.close();
			}
		}
		
		return null; // fica na mesma p�gina da pesquisa p�blica
	}
	
	
	
	public boolean isCooperacaoAutoridades(){
		return tipoOperacao  == OPERACAO_AUTORIDADE;
	}
	
	public boolean isCooperacaoBibliografica(){
		return tipoOperacao  == OPERACAO_BIBLIOGRAFICA;
	}
	
	
	
	
	/**
	 * pega os t�tulos do banco e os transforma em t�tulos do MARC4J
	 * @param gerarArquivoSaidaEmAscii
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	@SuppressWarnings("unchecked")
	private Record montaInfoMARC4J(Object o, boolean gerarArquivoSaidaEmAscii, Integer proximoNumeroDisponivelExportacaoFGV) throws DAOException, NegocioException{
		
		// Prepara a f�brica marc e o registro marc
		MarcFactory marcFactory = MarcFactory.newInstance();
		Record record = marcFactory.newRecord();

		// L� os campos de controle do t�tulo catalogr�fico
		
		Collection <CampoControle> camposControle = null;
		
		if(isCooperacaoBibliografica())
			camposControle =  ((TituloCatalografico)o).getCamposControleOrdenadosByEtiqueta();
		if(isCooperacaoAutoridades())
			camposControle =  ((Autoridade)o).getCamposControleOrdenadosByEtiqueta();
		
		boolean possuiCampo005 = false;
		
		if(camposControle != null){
		
			for (CampoControle cc : camposControle){
	
				if(cc.getEtiqueta().equals(Etiqueta.CAMPO_005_BIBLIOGRAFICO) || cc.getEtiqueta().equals(Etiqueta.CAMPO_005_AUTORIDADE) )
					possuiCampo005 = true;
				
				// Se o campo de controle � o LDR, coloca-o como Leader
				if (cc.getEtiqueta().getTag().equalsIgnoreCase(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag()) 
						|| cc.getEtiqueta().getTag().equalsIgnoreCase(Etiqueta.CAMPO_LIDER_AUTORIDADE.getTag()) ){
					Leader leader = null;
					
					if(gerarArquivoSaidaEmAscii){
						leader = marcFactory.newLeader( StringUtils.toAscii(  cc.getDado() ) );
					}else{
						leader = marcFactory.newLeader(cc.getDado());
					}
					
					record.setLeader(leader);
				} else {
					Etiqueta e = cc.getEtiqueta();
					
					ControlField controlField = null;
					
					if(gerarArquivoSaidaEmAscii){
						controlField = marcFactory.newControlField(e.getTag(), StringUtils.toAscii(cc.getDado()));
					}else{
						controlField = marcFactory.newControlField(e.getTag(), cc.getDado());
					}
					
					record.getControlFields().add(controlField);
				}
	
			}
		}
		
		Collection <CampoDados> camposDados = null;
		
		if(isCooperacaoBibliografica())
			camposDados =  ((TituloCatalografico)o).getCamposDadosOrdenadosByEtiqueta();
		if(isCooperacaoAutoridades())
			camposDados =  ((Autoridade)o).getCamposDadosOrdenadosByEtiqueta();
		

		String codigoBibliotecasCooperantes = "";
		
		boolean possuiCampo040 = false;
		boolean possuiSubCampoACampo040 = false;
		boolean possuiSubCampoBCampo040 = false;
		
		// L� todos os campos de dados do T�tulo e adiciona ao registro de exporta��o
		if(camposDados != null){
			
			for (CampoDados cd : camposDados){
				
				Etiqueta etiqueta = cd.getEtiqueta();
				
				// etiqueta locais n�o s�o exportadas, mas o 090 � para a FGV //
				if(! etiqueta.isEquetaLocal() || ( exportarFGV && etiqueta.equals(Etiqueta.NUMERO_CHAMADA ) ) ){ 
				
					char indicador1 = cd.getIndicador1();
					char indicador2 = cd.getIndicador2();
					
					DataField df = marcFactory.newDataField(etiqueta.getTag(), indicador1, indicador2);
		
					Collection <SubCampo> subCampos = cd.getSubCampos();
					
					// L� todos os dados dos subcampos do campo sem a pontua��o AACR2
					for (SubCampo s : subCampos){
						
						if(gerarArquivoSaidaEmAscii){
							df.addSubfield(marcFactory.newSubfield(s.getCodigo(), StringUtils.toAscii( s.getDadoSemFormatacaoAACR2()) ));
						}else{
							df.addSubfield(marcFactory.newSubfield(s.getCodigo(), s.getDadoSemFormatacaoAACR2()));
						}
					}	
					record.getDataFields().add(df);
				}
				
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////
		// gera os campos espec�ficos do cat�logo da FGV
		// Na exporta��o para a FGV al�m do formato do arquivo ser diferente, ele possui alguns campos expec�ficos ou com informa��es espec�ficas
		// s�o eles: (001, 005, 040, 997, 998, 999)
		/////////////////////////////////////////////////////////////////////////////
		if(exportarFGV){
			
			for (CampoDados cd : camposDados){
				
				Etiqueta etiqueta = cd.getEtiqueta();
				
				if(etiqueta.equals(Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA) || etiqueta.equals(Etiqueta.FONTE_CATALOGACAO_AUTORIDADES) ){
					
					possuiCampo040 = true; // se possui o campo 040, s� altera seus dados para ficar com o c�digo da FGV
					
					for (SubCampo sub : cd.getSubCampos()) {
						
						if(sub.isSubCampoA()){
							possuiSubCampoACampo040 = true;
							sub.setDado(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_INSTITUICAO_CATALOGACAO));
						}
						
						if(sub.isSubCampoB()){
							possuiSubCampoBCampo040 = true;
							sub.setDado(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.IDIOMA_CATALOGACAO));
						}
					} 
				}
				
				if(isCooperacaoBibliografica()){
					if( etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES)){ // 998
						
						for (SubCampo sub : cd.getSubCampos()) {
							if(sub.isSubCampoA())
								codigoBibliotecasCooperantes = sub.getDado();
						} 
						
						
					}
				}
				
				if(isCooperacaoAutoridades()){
					if( etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES)){ // 998
						for (SubCampo sub : cd.getSubCampos()) {
							if(sub.isSubCampoA())
								codigoBibliotecasCooperantes = sub.getDado();
						} 
					}
				}
				
			}
			
			
			// se na base n�o tiver, gera na hora de exportar com os valores 
			// da �ltima data de altera��o do hist�rico
			if(! possuiCampo005 ){ 
				String tag = "";
				
				if(isCooperacaoBibliografica()){	
					tag = Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag();
					BibliotecaUtil.geraDadosCampo005FGVTitulo(marcFactory, record, tag, ((TituloCatalografico)o).getId());
				}
				
				if(isCooperacaoAutoridades()){	
					tag = Etiqueta.CAMPO_005_AUTORIDADE.getTag();
					BibliotecaUtil.geraDadosCampo005FGVAutoridade(marcFactory, record, tag, ((Autoridade)o).getId());
				}
			}
			
			// alguns t�tulos e autoridades na base n�o possuem o campo 040, mas a FGV obriga ter, 
			// nesses casos gera na hora da exporta��o. 
			if(! possuiCampo040 ){
				
				String tag = "";
				
				if(isCooperacaoBibliografica())	
					tag = Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA.getTag();
				if(isCooperacaoAutoridades())	
					tag = Etiqueta.FONTE_CATALOGACAO_AUTORIDADES.getTag();
					
				BibliotecaUtil.geraDadosCampo040FGV(marcFactory, record, tag);
				
			}else{ // O T�tulo ou autoridade j� possui o campo 040, faltava s� um sub campo
				
				String tag = "";
				if(isCooperacaoBibliografica())	
					tag = Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA.getTag();
				if(isCooperacaoAutoridades())	
					tag = Etiqueta.FONTE_CATALOGACAO_AUTORIDADES.getTag();
				
				BibliotecaUtil.geraSubCamposDadosCampo040FGV(marcFactory, record, tag, possuiSubCampoACampo040, possuiSubCampoBCampo040);
				
				
			}	
				
			if(isCooperacaoBibliografica()){	
				
				// Gera os dados do campo 001 e dos campos 900 espec�ficos da FGV //
				BibliotecaUtil.geraDadosFGVCamposBibliograficos(
					marcFactory, record, nomeArquivoFGV, getUsuarioLogado().getNome(), 
					proximoNumeroDisponivelExportacaoFGV, codigoBibliotecasCooperantes, OPERACAO_BIBLIOGRAFICA);
			}
			
			
			if(isCooperacaoAutoridades()){
				// Gera os dados do campo 001 e dos campos 900 espec�ficos da FGV //
				BibliotecaUtil.geraDadosFGVCamposBibliograficos(
						marcFactory, record, nomeArquivoFGV, getUsuarioLogado().getNome(), 
						proximoNumeroDisponivelExportacaoFGV, codigoBibliotecasCooperantes, OPERACAO_AUTORIDADE);
				
			}
			
		}
		
		
		return record;
	}
	
	
	/**
	 * Em caso de exe��o gera um arquivo de erro padr�o
	 * 
	 * @param mensagemErro
	 * @throws IOException
	 */
	private void geraArquivoDeErro(String mensagemErro) throws IOException{
		// Prepara a saida
		HttpServletResponse response = getResponse();

		DataOutputStream writer  = new DataOutputStream(response.getOutputStream());
		
		writer.writeBytes("   Desculpe, existem erros nos dados MARC e o registro n�o p�de ser exportado.");
		writer.writeBytes(mensagemErro);
		
		response.setContentType("Content-Type: text/html; charset=utf-8");
		response.addHeader("Content-Disposition", "attachment; filename=erro.txt");
		
	}
	
	/**
	 * 
	 * M�todo que envia o arquivo pdf gerado no passo anterior para a sa�da do usu�rio.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formExportarTituloAutoridade.jsp</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public void visualizarArquivo(
			ActionEvent evt)
			throws DAOException, IOException {
		
		if(outputStreamArquivo != null){
			
			HttpServletResponse response = getResponse();
		
			DataOutputStream dos  = new DataOutputStream(response.getOutputStream());
			dos.write(outputStreamArquivo.toByteArray());
			
			// Tem que ser aqui no final depois que tudo deu certo.
			
			if(isCooperacaoBibliografica()){
				if (exportarFGV){
					response.setContentType("Content-Type: text/html; charset=ISO2709");
					response.addHeader("Content-Disposition", "attachment; filename="+nomeArquivoFGV+".CC");
				}else
					if (tipoCodificacao == ISO_2709){ // usuario escolheu a codifica��o ISO 2709
						response.setContentType("Content-Type: text/html; charset=ISO2709");
						response.addHeader("Content-Disposition", "attachment; filename=titulos_ISO2709.txt");
					}else{
						getCurrentResponse().setContentType("Content-Type: text/html; charset=utf-8");
						getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=titulos_UTF-8.txt");
					}
			}
			
			if(isCooperacaoAutoridades()){
				if (exportarFGV){
					response.setContentType("Content-Type: text/html; charset=ISO2709");
					response.addHeader("Content-Disposition", "attachment; filename="+nomeArquivoFGV+".AC");
				}else
					if (tipoCodificacao == ISO_2709){ // usuario escolheu a codifica��o ISO 2709
						response.setContentType("Content-Type: text/html; charset=ISO2709");
						response.addHeader("Content-Disposition", "attachment; filename=autoridades_ISO2709.txt");
					}else{
						getCurrentResponse().setContentType("Content-Type: text/html; charset=utf-8");
						getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=autoridades_UTF-8.txt");
					}
			}
			
			outputStreamArquivo = null;
			
			finalizaResposta();
			
		}
	}
	
	
	
	/**
	 * pega o response para jogar os dados do arquivo
	 * @return
	 * @throws IOException
	 */
	private HttpServletResponse getResponse(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return (HttpServletResponse) facesContext.getExternalContext().getResponse();
	}
	
	
	/**
	 * envia a resposta para o cliente.
	 */
	private void finalizaResposta(){
		 FacesContext.getCurrentInstance().responseComplete();
	}
	
	
	
	
	///////////////// telas de navega��o///////////////////////
	
	/**
	 * <p>Redireciona para a tela de exporta��o de t�tulos ou autoridades </p>
	 * 
	 * <p>M�todo n�o chamado de jsp. </p>
	 * 
	 */
	public String telaExportarTituloAutoridade(){
		return forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
	}
	
	
	
	
	
	// sets e gets
	
	/**
	 * <p>M�todo que retorna as bibliotecas do sistema em forma de SelectItem's.</p>
	 * <ul>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <li>/sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getBibliotecaList() throws DAOException {
		if(bibliotecaList == null) {
			BibliotecaDao dao = null;
			
			try{
				dao = getDAO(BibliotecaDao.class);
				if (!isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) {
					bibliotecaList = dao.findAllBibliotecasInternasAtivasPorUnidade(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO));
				}else {
					bibliotecaList = dao.findAllBibliotecasInternasAtivas();
				}
			} finally {
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(bibliotecaList, "id", "descricao");
	}

	public short getTipoOperacao() {
		return tipoOperacao;
	}


	public void setTipoOperacao(short tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}


	public char getTipoCodificacao() {
		return tipoCodificacao;
	}


	public void setTipoCodificacao(char tipoCodificacao) {
		this.tipoCodificacao = tipoCodificacao;
	}


	public String getNomeArquivoFGV() {
		return nomeArquivoFGV;
	}


	public void setNomeArquivoFGV(String nomeArquivoFGV) {
		this.nomeArquivoFGV = nomeArquivoFGV;
	}


	public boolean isExportarFGV() {
		return exportarFGV;
	}


	public void setExportarFGV(boolean exportarFGV) {
		this.exportarFGV = exportarFGV;
	}


	public Integer getPrimeiroNumeroSistema() {
		return primeiroNumeroSistema;
	}


	public void setPrimeiroNumeroSistema(Integer primeiroNumeroSistema) {
		this.primeiroNumeroSistema = primeiroNumeroSistema;
	}


	public Integer getUltimoNumeroSistema() {
		return ultimoNumeroSistema;
	}


	public void setUltimoNumeroSistema(Integer ultimoNumeroSistema) {
		this.ultimoNumeroSistema = ultimoNumeroSistema;
	}


	public Integer getNumeroDoSistema() {
		return numeroDoSistema;
	}


	public void setNumeroDoSistema(Integer numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}


	public List<CacheEntidadesMarc> getTitulosExportacao() {
		return titulosExportacao;
	}


	public void setTitulosExportacao(List<CacheEntidadesMarc> titulosExportacao) {
		this.titulosExportacao = titulosExportacao;
	}


	public List<CacheEntidadesMarc> getAutoridadesExportacao() {
		return autoridadesExportacao;
	}


	public void setAutoridadesExportacao(List<CacheEntidadesMarc> autoridadesExportacao) {
		this.autoridadesExportacao = autoridadesExportacao;
	}


	public List<CacheEntidadesMarc> getTitulosExportacaoGravadosDoUsuario() {
		return titulosExportacaoGravadosDoUsuario;
	}


	public void setTitulosExportacaoGravadosDoUsuario(List<CacheEntidadesMarc> titulosExportacaoGravadosDoUsuario) {
		this.titulosExportacaoGravadosDoUsuario = titulosExportacaoGravadosDoUsuario;
	}


	public List<CacheEntidadesMarc> getAutoridadesExportacaoGravadasDoUsuario() {
		return autoridadesExportacaoGravadasDoUsuario;
	}


	public void setAutoridadesExportacaoGravadasDoUsuario(List<CacheEntidadesMarc> autoridadesExportacaoGravadasDoUsuario) {
		this.autoridadesExportacaoGravadasDoUsuario = autoridadesExportacaoGravadasDoUsuario;
	}



	public DataModel getDataModelTitulos() {
		return dataModelTitulos;
	}



	public void setDataModelTitulos(DataModel dataModelTitulos) {
		this.dataModelTitulos = dataModelTitulos;
	}



	public DataModel getDataModelAutoridades() {
		return dataModelAutoridades;
	}



	public void setDataModelAutoridades(DataModel dataModelAutoridades) {
		this.dataModelAutoridades = dataModelAutoridades;
	}



	public List<CacheEntidadesMarc> getTitulosExportacaoAdicionados() {
		return titulosExportacaoAdicionados;
	}



	public void setTitulosExportacaoAdicionados(List<CacheEntidadesMarc> titulosExportacaoAdicionados) {
		this.titulosExportacaoAdicionados = titulosExportacaoAdicionados;
	}



	public List<CacheEntidadesMarc> getAutoridadesExportacaoAdicionadas() {
		return autoridadesExportacaoAdicionadas;
	}

	public void setAutoridadesExportacaoAdicionadas(List<CacheEntidadesMarc> autoridadesExportacaoAdicionadas) {
		this.autoridadesExportacaoAdicionadas = autoridadesExportacaoAdicionadas;
	}

	public boolean isSelecionaTodos() {
		return selecionaTodos;
	}

	public void setSelecionaTodos(boolean selecionaTodos) {
		this.selecionaTodos = selecionaTodos;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public boolean isApenasMinhasEntidadesPedentes() {
		return apenasMinhasEntidadesPedentes;
	}

	public void setApenasMinhasEntidadesPedentes(boolean apenasMinhasEntidadesPedentes) {
		this.apenasMinhasEntidadesPedentes = apenasMinhasEntidadesPedentes;
	}


	public int getBiblioteca() {
		return biblioteca;
	}


	public void setBiblioteca(int biblioteca) {
		this.biblioteca = biblioteca;
	}



	public ByteArrayOutputStream getOutputStreamArquivo() {
		return outputStreamArquivo;
	}

	//////////////////M�todos da interface de busca no acervo  ///////////////////////

	/**
	 * Configura as informa��es do T�tulo selecionado na pesquisa do acervo.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	
	@Override
	public void setTitulo(CacheEntidadesMarc cache) throws ArqException {
		TituloCatalograficoDao dao = null;
		try{
			dao = getDAO(TituloCatalograficoDao.class);
			tituloSelecionado = dao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", cache.getIdTituloCatalografico(), true);
		}finally{
			if( dao != null) dao.close();
		}
			
	}

	

	/**
	 * Adiciona o T�tulo selecionado na pesquisa do acervo, na listagem dos t�tulos que v�o ser exportados. <br/>
	 *
	 *  <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	
	@Override
	public String selecionaTitulo() throws ArqException {
		
		if(! titulosExportacao.contains(tituloSelecionado)){
			tituloSelecionado.setSelecionada(true);
			adicionaTituloExportacao(tituloSelecionado);
			titulosExportacaoAdicionados.add(tituloSelecionado);
		}else{
			addMensagemErro("T�tulo: "+tituloSelecionado.getNumeroDoSistema()+" j� foi adicionado � lista de exporta��o.");
		}
		
		return telaExportarTituloAutoridade();
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		return telaExportarTituloAutoridade();
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}
	
	//////////////////Fim dos M�todos da interface de busca no acervo  ///////////////////////
	
}
