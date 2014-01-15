/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *    <p>Managed Bean para gerenciar a exportação de Titulos e autoridades da biblioteca. </p>
 *     
 *     <p><i>Cooperação técnica é a troca de informações entre bibliotecas. Normamente, a codificação 
 * internacional padrão é a ISO-2709. E a biblioteca realiza cooperação técnica com a FGV. </i></p>
 *
 * @author jadson
 * @since 07/07/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("cooperacaoTecnicaExportacaoMBean")
@Scope("request")
public class CooperacaoTecnicaExportacaoMBean extends SigaaAbstractController<Object> implements PesquisarAcervoBiblioteca{
	
	/** Form onde o usuário pode exportar os títulos */
	public static final String PAGINA_EXPORTA_TITULO_AUTORIDADE = "/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp";
	
	/**  Se a operação é a exportação de título */
	public static final short OPERACAO_BIBLIOGRAFICA = 1;
	
	/**  Se a operação é a exportação de autoridades */
	public static final short OPERACAO_AUTORIDADE = 2;
	
	/**  Se o usuário realizou a busca por apenas um número identificador da catalogação */
	public static final int BUSCA_INDIVIDUAL = 1;
	
	/**  Se o usuário realizou a busca por uma faixa de números que identificam as catalogações */
	public static final int BUSCA_POR_FAIXA = 2;
	
	/**
	 * informa se a importação/exportação é de autoridades ou de dados bibliográficos 
	 */
	private short tipoOperacao = OPERACAO_BIBLIOGRAFICA;
	
	/** Valor para a codificação ISO */
	public static final char ISO_2709 = 'I';
	
	/** Valor para a codificação UTF-8 */
	public static final char UTF_8 = 'U';
	
	/** Informa o tipo de codificação escolhida para ser utilizada. A codificação padrão é ISO */
	private char tipoCodificacao = ISO_2709;      

	/** O nome do arquivo que será gerado para exportar para a FGV */
	private String nomeArquivoFGV;

	/** Se for exportar para a FGV deve conter alguns campos a mais e utilizar o número de controle ( campo 001)  de lá. */
	private boolean exportarFGV = false;
	
	/** O primeiro número da faixa que o usuário digitar */
	private Integer primeiroNumeroSistema; 
	
	/** O último número da faixa que o usuário digitar */
	private Integer ultimoNumeroSistema;   
	
	/** Guarda o número se o usuário digitar manualmente os números do sistema */
	private Integer numeroDoSistema; 
	
    /** Os títulos que serão realmente exportados */
	private List<CacheEntidadesMarc> titulosExportacao = new ArrayList<CacheEntidadesMarc>();
	
	/** As Autoridades que serão realmente exportados */
	private List<CacheEntidadesMarc> autoridadesExportacao = new ArrayList<CacheEntidadesMarc>();
	
	 /** Para interagir com os títulos no formulário de exportação */
	private DataModel  dataModelTitulos;
	
	 /**  Para interagir com as autoridades no formulário de exportação */
	private DataModel  dataModelAutoridades;
	
	/** Os títulos que o usuário catalogou e o sistema salvou como pendentes de exportação */
	private List<CacheEntidadesMarc> titulosExportacaoGravadosDoUsuario = new ArrayList<CacheEntidadesMarc>();
	
	/** As autoridades que o usuário catalogou e o sistema salvou como pendentes de exportação */
	private List<CacheEntidadesMarc> autoridadesExportacaoGravadasDoUsuario = new ArrayList<CacheEntidadesMarc>();
	
	/** Os título que o usuário adicionou agora na tela da exportação. */
	private List<CacheEntidadesMarc> titulosExportacaoAdicionados = new ArrayList<CacheEntidadesMarc>();
	
	/** As autoridade que o usuário adicionou agora na tela da exportação. */
	private List<CacheEntidadesMarc> autoridadesExportacaoAdicionadas = new ArrayList<CacheEntidadesMarc>();
    
	
    /** Indica se é para selecionar todos os checkbox na tela ou não. */
	private boolean selecionaTodos = true;
	
	
	/** Se vai ser uma busca por faixa de números ou individual, setado nos combox da página. */
	private int tipoBusca = BUSCA_INDIVIDUAL;
	
	
	/** Indica que o usuário só deseja ver os titulo criados por ele sejam visualizados como pendentes de exportação */
	private boolean apenasMinhasEntidadesPedentes = true;
	
	/** Biblioteca destino das importações */
	private int biblioteca;

	/** Lista das bibliotecas vinculadas ao usuário */
	private Collection<Biblioteca> bibliotecaList;

	/** Guarda os dados do arquivo, antes dele ser enviado para o usuário */
	private ByteArrayOutputStream outputStreamArquivo;
	
	/** Guarda as informações do Título selecionado na pesquisa padrão no acervo */
	private CacheEntidadesMarc tituloSelecionado;
	
	/**
	 *    Redireciona para uma página onde o usuário vai poder buscar os título para serem exportados
	 * ou exportar aqueles que foram catalogados e salvos de forma automática pelo sistema.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
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
	 *    Redireciona para uma página de exportação de autoridades.
	 *
	 *    <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
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
//	 *   Método que adiciona título que o usuário buscar. Para o caso do usuário não saber os 
//	 *   números do sistema do títulos a serem exportados .
//	 *
//	 *   <br/><br/>
//	 *   Chamado a partir do MBean: {@link PesquisaTituloCatalograficoMBean#exportarTituloCatalografico}
//	 *   Método não chamado por nenhuma página jsp.
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
//			addMensagemErro("Título: "+cache.getNumeroDoSistema()+" já foi adicionado à lista de exportação.");
//		}
//		
//		return telaExportarTituloAutoridade();
//	}
	
	
	
	/**
	 *   Método que adiciona autoridade que o usuário buscar. Para o caso do usuário não saber os 
	 *   números do sistema da autoridade a serem exportados .
	 *
	 *   <br/><br/>
	 *   Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#exportarAutoridade()}
	 *   Método não chamado por nenhuma página jsp.
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
			addMensagemErro("Autoridade "+cache.getNumeroDoSistema()+" já foi adicionada à lista de exportação.");
		}
		
		return telaExportarTituloAutoridade();
	}	
	
	
	/**
	 * Método que chama a busca padrão para selecionar um título para exportação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *     Busca o título quando o usuário digitou individualmente o número do sistema
	 * 
	 *     <br/><br/>
	 *     Chamado a partir da página:  /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
	 *     Método não chamado por nenhuma página jsp.
	 *  
	 * @param e
	 * @throws DAOException 
	 */
	public void adicionarNumerodoSistemaTitulo(ActionEvent e) throws DAOException{
		
		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);
		
		if(primeiroNumeroSistema == null && ultimoNumeroSistema == null && numeroDoSistema == null){
			addMensagemErro("Informe o número do sistema do Título que se deseja exportar");
		}else{
		
			if(tipoBusca == BUSCA_POR_FAIXA){
				
				if( primeiroNumeroSistema == null && ultimoNumeroSistema == null ){
					
					addMensagemErro("Informe o Número do Sistema Inicial e Final do Título.");
				}else{
					if( primeiroNumeroSistema == null && ultimoNumeroSistema != null ){
						
						addMensagemErro("Informe o Número do Sistema Inicial do Título.");
						
					}else{
						if( primeiroNumeroSistema != null && ultimoNumeroSistema == null ){
						
							addMensagemErro("Informe o Número do Sistema Final do Título.");
						
						}else{
					
							if(ultimoNumeroSistema.compareTo(primeiroNumeroSistema) < 0 ){
								addMensagemErro("O Número do Sistema Final Precisa ser Maior que o Número do Sistema Inicial.");
							}else{
							
								Long qtdTitulos = dao.countByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
								
								if( qtdTitulos > 100){
									addMensagemErro("Só podem ser adicionados 100 títulos por números de sistema por vez");
								}else{
								
									List<CacheEntidadesMarc> listaCache = dao.findByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
									
									boolean possuiPeloMenosUmTitulo = false;
									
									forEntidades:
									for (CacheEntidadesMarc cacheEntidadesMarc : listaCache) {
										
										if(! cacheEntidadesMarc.isCatalogado()){
											addMensagemErro("Título: "+cacheEntidadesMarc.getNumeroDoSistema()+" "+(cacheEntidadesMarc.getAutor() != null ? cacheEntidadesMarc.getAutor(): "")
													+" "+(cacheEntidadesMarc.getTitulo() != null ? cacheEntidadesMarc.getTitulo(): "")+" não pôde ser exportado, pois sua catalogação não foi finalizada. ");
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
										addMensagemErro("Todos os Título já foram adicionados à lista de exportação.");
									}
									
									if(listaCache.size() == 0){
										addMensagemErro("Títulos com o número do sistema entre: "+primeiroNumeroSistema+" e "+ultimoNumeroSistema+" não encontrados.");
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
						addMensagemErro("Título com o número do sistema "+numeroDoSistema+" não encontrado.");
					else{
						
						if(! temp.isCatalogado()){
							addMensagemErro("Título: "+temp.getNumeroDoSistema()+" "+(temp.getAutor() != null ? temp.getAutor(): "")
									+" "+(temp.getTitulo() != null ? temp.getTitulo(): "")+" não pôde ser exportado, pois sua catalogação não foi finalizada. ");
						}else{
						
							if(! titulosExportacao.contains(temp)){
								temp.setSelecionada(true);
								adicionaTituloExportacao(temp);
								titulosExportacaoAdicionados.add(temp);
								
								primeiroNumeroSistema = null;
								ultimoNumeroSistema = null;
								numeroDoSistema = null;
								
							}else{
								addMensagemErro("Título com o número do sistema: "+numeroDoSistema+" já adicionado à lista de exportação.");
							}
						}
						
					}
				}else{
					addMensagemErro("Informe o número do sistema do Título que se deseja exportar");
				}
			}
			
		}
		
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
		
	}
	
	
	
	/**
	 *     Busca o título quando o usuário digitou individualmente o número do sistema
	 * 
	 *     <br/><br/>
	 *     Chamado a partir da página:  /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
	 *     Método não chamado por nenhuma página jsp.
	 *     
	 * @param e
	 * @throws DAOException 
	 */
	public void adicionarNumerodoSistemaAutoridade(ActionEvent e) throws DAOException{
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class);
		
		if(primeiroNumeroSistema == null && ultimoNumeroSistema == null && numeroDoSistema == null){
			addMensagemErro("Informe o Número do Sistema da Autoridade que se deseja exportar.");
		}else{
		
			if(tipoBusca == BUSCA_POR_FAIXA){
				
				if( primeiroNumeroSistema == null && ultimoNumeroSistema == null ){
					addMensagemErro("Informe o Número do Sistema Inicial e Final da Autoridade.");
				}else{
					if( primeiroNumeroSistema == null && ultimoNumeroSistema != null ){
				
						addMensagemErro("Informe o Número do Sistema Inicial da Autoridade.");
						
					}else{
						if( primeiroNumeroSistema != null && ultimoNumeroSistema == null ){
						
							addMensagemErro("Informe o Número do Sistema Final da Autoridade.");
						
						}else{
					
							if(ultimoNumeroSistema.compareTo(primeiroNumeroSistema) < 0 ){
								addMensagemErro("O Número do Sistema Final Precisa ser Maior que o Número do Sistema Inicial.");
							}else{
					
								
								Long qtdAutoridades = dao.countByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
								
								if(qtdAutoridades > 100){
									addMensagemErro("Só podem ser adicionadas 100 autoridades por números do sistema por vez");
								}else{
								
									List<CacheEntidadesMarc> listaCache = dao.findByNumerosSistema(primeiroNumeroSistema, ultimoNumeroSistema);
									
									boolean possuiPeloMenosUmaAutoridade = false;
									
									forEnitdades:
									for (CacheEntidadesMarc cacheEntidadesMarc : listaCache) {
										
										if(! cacheEntidadesMarc.isCatalogado()){
											
											if(StringUtils.notEmpty(cacheEntidadesMarc.getEntradaAutorizadaAutor())){
												addMensagemErro("Autoridade: "+ cacheEntidadesMarc.getNumeroDoSistema()+" "
														+(cacheEntidadesMarc.getEntradaAutorizadaAutor()!= null ? cacheEntidadesMarc.getEntradaAutorizadaAutor() : "")+" não pôde ser exportada, pois sua catalogação não foi finalizada. ");
											}	
											
											if(StringUtils.notEmpty(cacheEntidadesMarc.getEntradaAutorizadaAssunto())){
												addMensagemErro("Autoridade: "+ cacheEntidadesMarc.getNumeroDoSistema()+" "
														+(cacheEntidadesMarc.getEntradaAutorizadaAssunto()!= null ? cacheEntidadesMarc.getEntradaAutorizadaAssunto() : "")+" não pôde ser exportada, pois sua catalogação não foi finalizada. ");
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
										addMensagemErro("Todas as Autoridades já foram adicionados à lista de exportação.");
									}
									
									if(listaCache.size() == 0){
										addMensagemErro("Autoridades com o número do sistema entre: "+primeiroNumeroSistema+" e "+ultimoNumeroSistema+" não encontradas.");
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
						addMensagemErro("Autoridade com o número do sistema "+numeroDoSistema+" não encontrada.");
					else{
						
						if(! temp.isCatalogado()){
							
							if(StringUtils.notEmpty(temp.getEntradaAutorizadaAutor())){
								addMensagemErro("Autoridade: "+ temp.getNumeroDoSistema()+" "
										+(temp.getEntradaAutorizadaAutor()!= null ? temp.getEntradaAutorizadaAutor() : "")+" não pôde ser exportada, pois sua catalogação não foi finalizada. ");
							}	
							
							if(StringUtils.notEmpty(temp.getEntradaAutorizadaAssunto())){
								addMensagemErro("Autoridade: "+ temp.getNumeroDoSistema()+" "
										+(temp.getEntradaAutorizadaAssunto()!= null ? temp.getEntradaAutorizadaAssunto() : "")+" não pôde ser exportada, pois sua catalogação não foi finalizada. ");
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
								addMensagemErro("Autoridade com o número do sistema: "+numeroDoSistema+" já adicionada à lista de exportação.");
							}
						}
					}	
					
				}else{
					addMensagemErro("Informe o Número do Sistema da Autoridade que se deseja exportar.");
				}
			}
			
		}
		
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
		
	}
	
	
	
	/**
	 * Método que adiciona um título mantendo o sincronismo com o dado model
	 */
	private void adicionaTituloExportacao(CacheEntidadesMarc c){
		
		if(titulosExportacao == null)
			titulosExportacao = new ArrayList<CacheEntidadesMarc>();
		
		titulosExportacao.add(c);
		dataModelTitulos = new ListDataModel(titulosExportacao);
	}
	
	
	/**
	 *  Método que adiciona uma autoridade mantendo o sincronismo com o dado model
	 */
	private void adicionaAutoridadeExportacao(CacheEntidadesMarc c){
		
		if(autoridadesExportacao == null)
			autoridadesExportacao = new ArrayList<CacheEntidadesMarc>();
		
		autoridadesExportacao.add(c);
		dataModelAutoridades = new ListDataModel(autoridadesExportacao);
	}
	
	
	
	
	/**
	 *       Atualiza a lista de títulos pendentes para o usuário, apenas os criados por ele, ou 
	 *       criados por outros usuários
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
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
	 *       Configura as três listas de materias para a geração de etiquetas. A de materias pendentes
	 *   salvos no banco, a de materiais adicionados na página pelo usuário e a de materiais marcados 
	 *   para geração do código de barras.
	 */
	private void configuraListasEntidades(boolean iniciandoCasoDeUso) throws DAOException{
		configuraListasEntidades(iniciandoCasoDeUso, true);
	}
		
		
	
	/**
	 *       Configura as três listas de materias para a geração de etiquetas. A de materias pendentes
	 *   salvos no banco, a de materiais adicionados na página pelo usuário e a de materiais marcados 
	 *   para geração do código de barras.
	 */
	private void configuraListasEntidades(boolean iniciandoCasoDeUso, boolean iniciarSelecionado) throws DAOException{
	
		selecionaTodos = true;
		
		if(isCooperacaoBibliografica()){
			titulosExportacaoGravadosDoUsuario.clear();
			titulosExportacao.clear();
		
			if(apenasMinhasEntidadesPedentes){
			
				// carrega os títulos que o usuário catalogou mas ainda não exportou.
				List<EntidadesMarcadasParaExportacao> entidades = getDAO(TituloCatalograficoDao.class)
						.encontraTitulosPendentesExportacaoByUsuario(getUsuarioLogado().getId());
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
						titulosExportacaoGravadosDoUsuario.add( BibliotecaUtil.obtemDadosTituloCache(entidade.getIdTituloCatalografico()));
				}
				
			}else{
				
				// carrega todos os títulos catalogados mas ainda não exportou.
				List<EntidadesMarcadasParaExportacao> entidades = getDAO(TituloCatalograficoDao.class)
						.encontraTitulosPendentesExportacao();
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
					titulosExportacaoGravadosDoUsuario.add( BibliotecaUtil.obtemDadosTituloCache(entidade.getIdTituloCatalografico()));	
				}
				
			}
			
			// inicialmente todos gravados são selecionados para geração etiqueta
			titulosExportacao.addAll(titulosExportacaoGravadosDoUsuario);
			
			for (CacheEntidadesMarc c : titulosExportacao) {
				c.setSelecionada(iniciarSelecionado);
			}
			
			if(! iniciandoCasoDeUso){ // chamado depois que o usuário pode ter adicionado algum material à lista
				
				titulosExportacao.addAll(titulosExportacaoAdicionados);
				
				
			}
			
			dataModelTitulos = new ListDataModel(titulosExportacao);
			
		}
		
		
		
		if(isCooperacaoAutoridades()){
			
			autoridadesExportacaoGravadasDoUsuario.clear();
			autoridadesExportacao.clear();
		
			if(apenasMinhasEntidadesPedentes){
			
				// carrega as autoridades que o usuário catalogou mas inda não exportou.
				List<EntidadesMarcadasParaExportacao> entidades = getDAO(AutoridadeDao.class)
					.encontraAutoridadesPendentesExportacaoByUsuario(getUsuarioLogado().getId());
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
						autoridadesExportacaoGravadasDoUsuario.add( BibliotecaUtil.obtemDadosAutoridadeEmCache(entidade.getIdAutoridade()));
				}
				
			}else{
				
				// carrega todos os títulos catalogados mas ainda não exportou.
				List<EntidadesMarcadasParaExportacao> entidades =  getDAO(AutoridadeDao.class)
						.encontraAutoridadesPendentesExportacao();
			
				for (EntidadesMarcadasParaExportacao entidade: entidades) {
					autoridadesExportacaoGravadasDoUsuario.add( BibliotecaUtil.obtemDadosAutoridadeEmCache(entidade.getIdAutoridade()));
				}
				
			}
			
			// inicialmente todos gravados são selecionados para geração etiqueta
			autoridadesExportacao.addAll(autoridadesExportacaoGravadasDoUsuario);
			
			for (CacheEntidadesMarc c : autoridadesExportacao) {
				c.setSelecionada(iniciarSelecionado);
			}
			
			if(! iniciandoCasoDeUso){ // chamado depois que o usuário ter adicionado algum material à lista
				autoridadesExportacao.addAll(autoridadesExportacaoAdicionadas);
			}
			
			dataModelAutoridades = new ListDataModel(autoridadesExportacao);
			
		}
		
	}
	
	/**
	 *  Método chamado para selecionar ou deselecionar todas as entidades marcadas para exportação.
	 *
	 *  <br/><br/>
	 *  Chamado a partir da página:  /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formExportarTituloAutoridade.jsp
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
	 *   Apaga a entidade da lista de exportação. Se foi uma entidade adicionado pelo usuário, só apaga
	 * da lista, se foi uma entidade que o sistema tinha salvo, remove-se a entidade que o sistema salvou
	 * do estado de pendente. 
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerEntidadesExportacao() throws ArqException {
		
		if(isCooperacaoBibliografica()){
			
			if(dataModelTitulos != null){
				
				CacheEntidadesMarc tituloRemovido = (CacheEntidadesMarc) dataModelTitulos.getRowData();
				
				if(titulosExportacaoAdicionados.contains(tituloRemovido)){ // só apaga na memória
					
					titulosExportacaoAdicionados.remove(dataModelTitulos.getRowData());
					titulosExportacao.remove(dataModelTitulos.getRowData());
					
					addMensagemInformation("Título "+tituloRemovido.getNumeroDoSistema()+" removido da lista com sucesso.");
					
				}else{
					if(titulosExportacaoGravadosDoUsuario.contains(tituloRemovido)){ // precisa apagar os que o sistema salvou
						
						apagaEntidadeGravadaSistema(tituloRemovido);
						titulosExportacao.remove(dataModelTitulos.getRowData());
						
						addMensagemInformation("Título "+tituloRemovido.getNumeroDoSistema()+" removido da lista com sucesso.");
					}
				}
				
				
			}
			
		}
		
		if(isCooperacaoAutoridades()){
			if(dataModelAutoridades != null){
				
				CacheEntidadesMarc autoridadeRemovida = (CacheEntidadesMarc) dataModelAutoridades.getRowData();
				
				if(autoridadesExportacaoAdicionadas.contains(autoridadeRemovida)){ // só apaga na memória
					
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
	 * Remove todas os títulos selecionadas
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
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
			
			addMensagemInformation("Títulos removidos da lista com sucesso.");
		
		}else{
			addMensagemErro("Não existem Títulos Selecionados.");
		}
		
		return telaExportarTituloAutoridade();
	}
	
	
	/**
	 *    Remove todas as autoridades selecionadas 
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
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
			addMensagemErro("Não existem Autoridades Selecionadas.");
		}
		
		return telaExportarTituloAutoridade();
	}
	
	
	
	/**
	 * Apaga as entidades que o sistema tinha gravado como pendentes de importação porque o usuário 
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
				addMensagemErro("Erro ao apagar os títulos marcados para exportação");
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
				addMensagemErro("Erro ao apagar as autoridades marcadas para exportação");
			}
			
		}
	
		forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
		
	}
	
	
	
	
	/**
	 * Habilita e desabilita a digitação do nome do arquivo
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
	 * 
	 * @param evt
	 */
	public void abilitarExportacaoFGV(ActionEvent evt){
		tipoCodificacao = ISO_2709;
	}
	
	
	/**
	 *    Retorna a quantidade de entidades pendentes de exportação buscadas
	 *<br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
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
	 *    Método que realiza a exportação dos títulos ou autoridades marcadas.
	 *    Busca o Título no banco, converte-o para o formato do MARC4J e manda o MARC4J gerar 
	 * um arquivo na codificação desejada.
	 * <br/>
	 *    Entidades marcadas como pendentes de exportação pelo sistema quando exportadas é 
	 *  removida essa pendência.
	 *
	 *    <br/><br/>
	 *	  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/formExportaTituloAutoridade.jsp
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
		
		// pega os marcados pelo usuário
		List<CacheEntidadesMarc> titulosMarcados = new ArrayList<CacheEntidadesMarc>();
		List<CacheEntidadesMarc> autoridadesMarcadas = new ArrayList<CacheEntidadesMarc>();
		
		
		if(isCooperacaoBibliografica()){
			
			
			for (CacheEntidadesMarc cacheEntidadesMarc : titulosExportacao) {
				
				if(cacheEntidadesMarc.isSelecionada())
					titulosMarcados.add(cacheEntidadesMarc);
				
			}
			
			if(titulosMarcados.size() == 0){
				addMensagemErro("Não existem títulos selecionados para exportação.");
				return null;
			}
		}
		
		if(isCooperacaoAutoridades()){
			
			for (CacheEntidadesMarc cacheEntidadesMarc : autoridadesExportacao) {
				if(cacheEntidadesMarc.isSelecionada())
					autoridadesMarcadas.add(cacheEntidadesMarc);
			}
			
			if(autoridadesMarcadas.size() == 0){
				addMensagemErro("Não existem autoridades selecionadas para exportação.");
				return null;
			}
		}
		
		try{
			
			dao = getDAO(TituloCatalograficoDao.class);
		
			Biblioteca bibliotecaUsuarioLogado = dao.findByPrimaryKey(biblioteca, Biblioteca.class);
			
			if(bibliotecaUsuarioLogado == null || bibliotecaUsuarioLogado.getId() <=0 ){
				addMensagemErro("Selecione uma biblioteca para a exportação.");
				return null;
			}
			
			
			//HttpServletResponse response = getResponse();
			
			/* *************************************************************************************
			 *    Streams temporários onde os dados de cada registro MARC seão salvos temporariamente.
			 *    É preciso guardar cada um separadamente, caso seja exportação para a FGV, porque é
			 * necessário contar o tamanho de cada registro MARC para gerar o arquivo PCS.
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
						writer = new MarcStreamWriter(outputStreamTemp); // codifição ISO 2709
					else
						writer = new MarcStreamWriter(outputStreamTemp,"UTF-8");
					
					c = dao.refresh(c);
					
					TituloCatalografico t = dao.findByPrimaryKey(c.getIdTituloCatalografico(), TituloCatalografico.class);
					
					Record record = null;
					
					if(isExportarFGV()){
						
						proximoNumeroDisponivelExportacao = BibliotecaUtil.encontraProximoNumeroDisponivelExportacaoFGV(arquivosCarregados);
						
						// Não existem mais 
						// gera o arquivo de erro. não tem como mostrar uma mensagem para o usuário porque o controle da tela já foi perdido.
						if(proximoNumeroDisponivelExportacao == null){
							addMensagemErro("Não existem mais números de controle para a exportação para a FGV, "
									+"por favor carregue um novo arquivo com os números de controle do catálogo coletivo."); 
							return null;
						}else{
							
							/*
							 *  Tem que gerar um arquivo com o nome RN00000001.CC ou  RN00000001.AC
							 *  
							 *  No caso de várias exportações no mesmo aquivo, vai conter o número do último
							 *  
							 *  Conversa com o analista para FGV. 05/05/2011 as 10:03 <br/>
							 *  <p>
							 *  Jadson diz:
							 *	Essa sequencia: 000001, 0000002, 0000xxxx,   tem alguma importancia para o envio dos arquivos para o catálogo coletivo ?
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
				
			} // Cooperacao Bibliotegárica
			
			if(isCooperacaoAutoridades()){
				
				
				// retira os repetidos
				Set<CacheEntidadesMarc> autoridadesNaoRepetidas = new HashSet<CacheEntidadesMarc>(autoridadesMarcadas);
				
				MarcStreamWriter writer;
				
				ByteArrayOutputStream outputStreamTemp = new ByteArrayOutputStream(); 
				
				if (exportarFGV || tipoCodificacao == ISO_2709)
					writer = new MarcStreamWriter(outputStreamTemp); // codifição ISO 2709
				else
					writer = new MarcStreamWriter(outputStreamTemp,"UTF-8");
				
				for (CacheEntidadesMarc c : autoridadesNaoRepetidas) {
					
					c = dao.refresh(c);
							
					Record record = null;
					
					if(isExportarFGV()){
						
						proximoNumeroDisponivelExportacao = BibliotecaUtil.encontraProximoNumeroDisponivelExportacaoFGV(arquivosCarregados);
						
						// Não existem mais números disponíveis
						// gera o arquivo de erro. não tem como mostrar uma mensagem para o usuário porque o controle da tela já foi perdido.
						if(proximoNumeroDisponivelExportacao == null){
							addMensagemErro("Não existem mais números de controle para a exportação para a FGV, "
									+"por favor carregue um novo arquivo com os números de controle do catálogo coletivo.");
							return null;
						}else{
							
							/*
							 *  Tem que gerar um arquivo com o nome RN00000001.CC ou  RN00000001.AC
							 *  
							 *  No caso de várias exportações no mesmo aquivo, vai conter o número do último.
							 *  
							 *  Conversa com o analista para FGV. 05/05/2011 as 10:03 <br/>
							 *  <p>
							 *  Jadson diz:
							 *	Essa sequencia: 000001, 0000002, 0000xxxx,   tem alguma importancia para o envio dos arquivos para o catálogo coletivo ?
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
				
			}  /// Cooperação de autoridades 

			
			
			

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
				
				// Escreve os bytes de cada registro MARC na saída
				for (ByteArrayOutputStream byteArrayOut : outputSteams) {
					dos.write(byteArrayOut.toByteArray());	
				}
			}
			
			prepareMovimento(SigaaListaComando.ACOES_AUXILIARES_EXPORTA_TITULO_AUTORIDADE);
			
			
			//////////////////////////////////////////////////////////////////////////////////
			//////////////       remove os títulos gravados que foram exportados /////////////
			
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
					
					// usuário exportou um título que estava gravado
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
	 *   Metódo chamado para exportar um título na parte pública da biblioteca. 
	 *   Aqui o usuário não vai escolher. Ele vai sempre ser exportado no formato ISO2709
	 *
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
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
			
			// Escreve o registro na saída
			MarcStreamWriter writer = new MarcStreamWriter(response.getOutputStream()); // codifição ISO 2709
			
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
		
		return null; // fica na mesma página da pesquisa pública
	}
	
	
	/**
	 *   Metódo chamado para exportar um título na parte pública da biblioteca. 
	 *   Aqui o usuário não vai escolher. Ele vai sempre ser exportado no formato ISO2709
	 *
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
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
			
			// Escreve o registro na saída
			MarcStreamWriter writer = new MarcStreamWriter(response.getOutputStream()); // codifição ISO 2709
			
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
		
		return null; // fica na mesma página da pesquisa pública
	}
	
	
	
	public boolean isCooperacaoAutoridades(){
		return tipoOperacao  == OPERACAO_AUTORIDADE;
	}
	
	public boolean isCooperacaoBibliografica(){
		return tipoOperacao  == OPERACAO_BIBLIOGRAFICA;
	}
	
	
	
	
	/**
	 * pega os títulos do banco e os transforma em títulos do MARC4J
	 * @param gerarArquivoSaidaEmAscii
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	@SuppressWarnings("unchecked")
	private Record montaInfoMARC4J(Object o, boolean gerarArquivoSaidaEmAscii, Integer proximoNumeroDisponivelExportacaoFGV) throws DAOException, NegocioException{
		
		// Prepara a fábrica marc e o registro marc
		MarcFactory marcFactory = MarcFactory.newInstance();
		Record record = marcFactory.newRecord();

		// Lê os campos de controle do título catalográfico
		
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
				
				// Se o campo de controle é o LDR, coloca-o como Leader
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
		
		// Lê todos os campos de dados do Título e adiciona ao registro de exportação
		if(camposDados != null){
			
			for (CampoDados cd : camposDados){
				
				Etiqueta etiqueta = cd.getEtiqueta();
				
				// etiqueta locais não são exportadas, mas o 090 é para a FGV //
				if(! etiqueta.isEquetaLocal() || ( exportarFGV && etiqueta.equals(Etiqueta.NUMERO_CHAMADA ) ) ){ 
				
					char indicador1 = cd.getIndicador1();
					char indicador2 = cd.getIndicador2();
					
					DataField df = marcFactory.newDataField(etiqueta.getTag(), indicador1, indicador2);
		
					Collection <SubCampo> subCampos = cd.getSubCampos();
					
					// Lê todos os dados dos subcampos do campo sem a pontuação AACR2
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
		// gera os campos específicos do catálogo da FGV
		// Na exportação para a FGV além do formato do arquivo ser diferente, ele possui alguns campos expecíficos ou com informações específicas
		// são eles: (001, 005, 040, 997, 998, 999)
		/////////////////////////////////////////////////////////////////////////////
		if(exportarFGV){
			
			for (CampoDados cd : camposDados){
				
				Etiqueta etiqueta = cd.getEtiqueta();
				
				if(etiqueta.equals(Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA) || etiqueta.equals(Etiqueta.FONTE_CATALOGACAO_AUTORIDADES) ){
					
					possuiCampo040 = true; // se possui o campo 040, só altera seus dados para ficar com o código da FGV
					
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
			
			
			// se na base não tiver, gera na hora de exportar com os valores 
			// da última data de alteração do histórico
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
			
			// alguns títulos e autoridades na base não possuem o campo 040, mas a FGV obriga ter, 
			// nesses casos gera na hora da exportação. 
			if(! possuiCampo040 ){
				
				String tag = "";
				
				if(isCooperacaoBibliografica())	
					tag = Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA.getTag();
				if(isCooperacaoAutoridades())	
					tag = Etiqueta.FONTE_CATALOGACAO_AUTORIDADES.getTag();
					
				BibliotecaUtil.geraDadosCampo040FGV(marcFactory, record, tag);
				
			}else{ // O Título ou autoridade já possui o campo 040, faltava só um sub campo
				
				String tag = "";
				if(isCooperacaoBibliografica())	
					tag = Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA.getTag();
				if(isCooperacaoAutoridades())	
					tag = Etiqueta.FONTE_CATALOGACAO_AUTORIDADES.getTag();
				
				BibliotecaUtil.geraSubCamposDadosCampo040FGV(marcFactory, record, tag, possuiSubCampoACampo040, possuiSubCampoBCampo040);
				
				
			}	
				
			if(isCooperacaoBibliografica()){	
				
				// Gera os dados do campo 001 e dos campos 900 específicos da FGV //
				BibliotecaUtil.geraDadosFGVCamposBibliograficos(
					marcFactory, record, nomeArquivoFGV, getUsuarioLogado().getNome(), 
					proximoNumeroDisponivelExportacaoFGV, codigoBibliotecasCooperantes, OPERACAO_BIBLIOGRAFICA);
			}
			
			
			if(isCooperacaoAutoridades()){
				// Gera os dados do campo 001 e dos campos 900 específicos da FGV //
				BibliotecaUtil.geraDadosFGVCamposBibliograficos(
						marcFactory, record, nomeArquivoFGV, getUsuarioLogado().getNome(), 
						proximoNumeroDisponivelExportacaoFGV, codigoBibliotecasCooperantes, OPERACAO_AUTORIDADE);
				
			}
			
		}
		
		
		return record;
	}
	
	
	/**
	 * Em caso de exeção gera um arquivo de erro padrão
	 * 
	 * @param mensagemErro
	 * @throws IOException
	 */
	private void geraArquivoDeErro(String mensagemErro) throws IOException{
		// Prepara a saida
		HttpServletResponse response = getResponse();

		DataOutputStream writer  = new DataOutputStream(response.getOutputStream());
		
		writer.writeBytes("   Desculpe, existem erros nos dados MARC e o registro não pôde ser exportado.");
		writer.writeBytes(mensagemErro);
		
		response.setContentType("Content-Type: text/html; charset=utf-8");
		response.addHeader("Content-Disposition", "attachment; filename=erro.txt");
		
	}
	
	/**
	 * 
	 * Método que envia o arquivo pdf gerado no passo anterior para a saída do usuário.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formExportarTituloAutoridade.jsp</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
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
					if (tipoCodificacao == ISO_2709){ // usuario escolheu a codificação ISO 2709
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
					if (tipoCodificacao == ISO_2709){ // usuario escolheu a codificação ISO 2709
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
	
	
	
	
	///////////////// telas de navegação///////////////////////
	
	/**
	 * <p>Redireciona para a tela de exportação de títulos ou autoridades </p>
	 * 
	 * <p>Método não chamado de jsp. </p>
	 * 
	 */
	public String telaExportarTituloAutoridade(){
		return forward(PAGINA_EXPORTA_TITULO_AUTORIDADE);
	}
	
	
	
	
	
	// sets e gets
	
	/**
	 * <p>Método que retorna as bibliotecas do sistema em forma de SelectItem's.</p>
	 * <ul>Método chamado pela(s) seguinte(s) JSP(s):
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

	//////////////////Métodos da interface de busca no acervo  ///////////////////////

	/**
	 * Configura as informações do Título selecionado na pesquisa do acervo.<br/>
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
	 * Adiciona o Título selecionado na pesquisa do acervo, na listagem dos títulos que vão ser exportados. <br/>
	 *
	 *  <p>Método não chamado por página jsp.</p>
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
			addMensagemErro("Título: "+tituloSelecionado.getNumeroDoSistema()+" já foi adicionado à lista de exportação.");
		}
		
		return telaExportarTituloAutoridade();
	}



	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}
	
	//////////////////Fim dos Métodos da interface de busca no acervo  ///////////////////////
	
}
