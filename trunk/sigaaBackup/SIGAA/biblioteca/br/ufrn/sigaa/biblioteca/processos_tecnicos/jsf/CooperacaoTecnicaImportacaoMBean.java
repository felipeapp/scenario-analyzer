/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/07/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoSalvaEntidadesImportadas;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ListaEtiquetas;
import br.ufrn.sigaa.biblioteca.util.ParseTituloCatalograficoUtil;

/**
 *     Managed Bean para gerenciar a importa��o de T�tulos e Autoridades da biblioteca.
 *     
 *     <p>Podem ser importados v�rios t�tulos e autoridade de uma �nica vez, eles v�o ser salvos no 
 *  sistema como t�tulos e autoridades n�o catalogados (ou n�o finalizados). O bibliotec�rio ter� que
 *  finalizar essas cataloga��es em momentos posteriores.</p>
 *     
 *     <p>Opera��es como exporta��o, adi��o de materiais, etc.. s� podem ser realizadas se o bibliotec�rio
 *  finalizar a cataloga��o.</p>
 *
 * @author Fred
 *
 */

@Component("cooperacaoTecnicaImportacaoMBean")
@Scope("request")
public class CooperacaoTecnicaImportacaoMBean extends SigaaAbstractController<Object> {
	
	/** Indica que est� importando um t�tulo */
	public static final short OPERACAO_BIBLIOGRAFICA = 1;
	/** Indica que est� importando um uma autoridade */
	public static final short OPERACAO_AUTORIDADE = 2;
	
	/** P�gina de importa��o de t�tulo */
	public static final String PAGINA_IMPORTA_TITULO = "/biblioteca/processos_tecnicos/cooperacao_tecnica/formImportarTituloAutoridade.jsp";
	/** P�gina de visualiza��o dos registros do arquivo */
	public static final String PAGINA_VISUALIZA_REGISTROS_DO_ARQUIVO = "/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaVisualizarRegistrosDoArquivo.jsp";
	
	
	/**
	 * Informa se a importa��o/exporta��o � de autoridades ou de dados bibliogr�ficos 
	 */
	private short tipoOperacao = OPERACAO_BIBLIOGRAFICA;
	
	

	/** Indica que o usu�rio submeteu um arquivo */
	private static final Integer IMPORTAR_ARQUIVO = 1;
	/** Indica que o usu�rio digitou os dados na tela */
	private static final Integer IMPORTAR_DADOS_DIGITADOS = 2;

	/** Se � arquivo ou dados digitados que o usu�rio selecionou na importa��o. */
	private Integer tipoImportacao = IMPORTAR_ARQUIVO;     
	/** Guarda o arquivo que o usu�rio escolheu */
	private UploadedFile arquivo;       
	/** Guarda o texto que o usu�rio digitou no lugar do arquivo */
	private String arquivoDigitado;     
	
	
	/** Guarda os dados dos t�tulos que ser�o importados.    */
	private List<TituloCatalografico> titulosImportacao;
	
	/** Guarda os dados das autoridades que ser�o importadas.   */
	private List<Autoridade> autoridadesImportacao;

	
	/** 
	 * Guarda quando o usu�rio clicar no bot�o de importar novos t�tulos, mesmo tendo 
	 * t�tulos n�o finalizados. � preciso guarda essa informa��o pois na tela de importar � preciso
	 * habilitar um bot�o para voltar a p�gina de t�tulos n�o finalizados.
	 */
	private boolean importouNovasEntidadesExistindoTituloNaoFinalizados = false;
	
	/** Guarda os dados do arquivo quando o usu�rio desejar apenas visualizar o que tem dentro do arquivo MARC */
	private String dadosDoArquivo;
	

	/** Se o sistema deve importar ou n�o os campos locais */
	private boolean importarCamposLocais = false;
	
	/** Biblioteca destino das importa��es */
	private int biblioteca;

	/** Lista das bibliotecas vinculadas ao usu�rio */
	private Collection<Biblioteca> bibliotecaList;
	
	
	/** Guarda em cache a lista de classifica��es bibliogr�fica utilizadas no sistema para n�o precisar busca sempre no banco*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>();
	
	/**
	 *    Se n�o houver t�tulos n�o finalizados, redireciona para a p�gina na qual o usu�rio vai escolher a forma da importa��o.
	 *    Se houver t�tulos n�o finalizados, redireciona para a p�gina que mostra esses t�tulos n�o finalizados.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarImportacaoBibliografica() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		carregaClassificacaoesUtilizadas();
		
		prepareMovimento(SigaaListaComando.SALVA_ENTIDADES_IMPORTADAS);
		
		titulosImportacao = new ArrayList<TituloCatalografico>();
		
		tipoOperacao = OPERACAO_BIBLIOGRAFICA;
		
		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);
		
		if( new Long(0).compareTo( dao.countAllTitulosComCatalogacaoIncompleta()) < 0 ){ // existem t�tulos n�o finalizados
			BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
			return bean.iniciarBuscaTitulosIncompletosImportacao();
		}else{
			importouNovasEntidadesExistindoTituloNaoFinalizados = false;
			return telaImportarTitulo();
		}
	}
	 
	
	/**
	 *       Chamado dentro da p�gina de t�tulos n�o finalizados, redireciona diretamente para a p�gina
	 *    de importa��o. Nesse caso o usu�rio quer importar novos t�tulos e ignorar os que ainda
	 *    n�o foram finalizados.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarImportacaoBibliograficaDiretamente() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		carregaClassificacaoesUtilizadas();
		
		prepareMovimento(SigaaListaComando.SALVA_ENTIDADES_IMPORTADAS);
		
		titulosImportacao = new ArrayList<TituloCatalografico>();
		
		tipoOperacao = OPERACAO_BIBLIOGRAFICA;
		
		importouNovasEntidadesExistindoTituloNaoFinalizados = true;
		
		return telaImportarTitulo();             
	}
	
		
	/**
	 * Redireciona para a p�gina na qual o usu�rio vai escolher a forma da importa��o para autoridades
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarImportacaoAutoridades() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.SALVA_ENTIDADES_IMPORTADAS);
		
		autoridadesImportacao = new ArrayList<Autoridade>();

		tipoOperacao = OPERACAO_AUTORIDADE;
		
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class);

		if( new Long(0).compareTo( dao.countAllAutoridadesComCatalogacaoIncompleta()) < 0 ){ // existem autoriades n�o finalizados
			BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
			return bean.iniciarBuscaAutoridadesIncompletasImportacao();
		}else{
			importouNovasEntidadesExistindoTituloNaoFinalizados = false;
			return telaImportarTitulo();
		}

	}
	
	
	
	/**
	 *       Chamado dentro da p�gina de autoridades n�o finalizadas, redireciona diretamente para a p�gina
	 *    de importa��o. Nesse caso o usu�rio quer importar novas autoridades e ignorar as que ainda
	 *    n�o foram finalizadas.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarImportacaoAutoridadesDiretamente() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.SALVA_ENTIDADES_IMPORTADAS);
		
		autoridadesImportacao = new ArrayList<Autoridade>();
		
		tipoOperacao = OPERACAO_AUTORIDADE;
		
		importouNovasEntidadesExistindoTituloNaoFinalizados = true;
		
		return telaImportarTitulo();             
	}
	
	
	
	/** Carrega as classifica��es utilizadas no sistema para configurar corretamente as informa��es dos campos de classifica��o ao salvar o T�tulo. */
	private void carregaClassificacaoesUtilizadas() throws DAOException{
		if ( classificacoesUtilizadas == null || classificacoesUtilizadas.size() == 0 ){
			ClassificacaoBibliograficaDao dao = null;
			try{
				 dao = getDAO(ClassificacaoBibliograficaDao.class);
				 classificacoesUtilizadas = dao.findAllClassificacoesParaValidacao();
			}finally{
				if(dao != null) dao.close();
			}
		} 
	}
	
	/**
	 * Faz a leitura dos dados do t�tulo, sejam eles vindos do arquivo ou do texto digitado e encaminha para 
	 * a p�gina de cataloga��o, daqui para frente tudo vai ser igual 'a cataloga��o normal
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formImportarTituloAutoridade.jsp
	 * 
	 * @throws ArqException  
	 * 
	 */
	public String realizarInterpretacaoDados() throws ArqException{
	
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO , SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			
			if (arquivo == null &&  StringUtils.isEmpty(arquivoDigitado))
				addMensagemErro("Escolha um arquivo ou digite os dados do para a importa��o. ");	
			
			if (hasErrors())
				return null;

			montaInformacoesDoTituloOUAutoridade();
			
		} catch (IOException ioe) {
			addMensagemErro("N�o foi poss�vel ler o arquivo.");
			ioe.printStackTrace();
			return null;
		} catch (MarcException mex){
			addMensagemErro("O arquivo MARC possui algum erro, impossibilitando a sua interpreta��o. ");
			mex.printStackTrace();
			return null;
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			ne.printStackTrace();
			return null;
		} catch (Exception ex){
			addMensagemErro("O arquivo ou os dados digitados cont�m algum erro, impossibilitando a sua interpreta��o.");
			ex.printStackTrace();
			return null;
		}
		
		
		if(isCooperacaoBibliografica()){
			
			TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);

			Biblioteca bibliotecaUsuarioLogado = dao.findByPrimaryKey(biblioteca, Biblioteca.class);
			
			if(bibliotecaUsuarioLogado == null || bibliotecaUsuarioLogado.getId() <=0 ){
				addMensagemErro("Selecione uma biblioteca para a importa��o.");
				return null;
			}
			
			// Sempre que o arquivo contiver 1 T�tulo o sistema vai para a p�gina de cataloga��o
			// n�o � salvo na base.
			if(titulosImportacao.size() == 1 
					/* && ( new Long(0).compareTo( dao.countAllTitulosComCatalogacaoIncompleta()) == 0) */ ){ // s� tem um t�tulo
				
				BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
				
				TituloCatalografico tituloImportacao = titulosImportacao.get(0);
				
				tituloImportacao.setImportado(true);
				tituloImportacao.setBibliotecaImportacao(bibliotecaUsuarioLogado);
				
				bean.setTituloSelecionado(tituloImportacao); // o arquivo s� tinha um t�tulo.
				return bean.iniciarCatalogacaoComApenasUmTituloImportado();
				
			}else{
				
				for (TituloCatalografico tituloImportacao : titulosImportacao) {
					tituloImportacao.setImportado(true);
					tituloImportacao.setBibliotecaImportacao(bibliotecaUsuarioLogado);
				}
				
				MovimentoSalvaEntidadesImportadas mov = new MovimentoSalvaEntidadesImportadas(titulosImportacao, classificacoesUtilizadas);
				mov.setCodMovimento(SigaaListaComando.SALVA_ENTIDADES_IMPORTADAS);
				
				try {
					execute(mov);
				} catch (NegocioException ne) {
					addMensagens(ne.getListaMensagens());
				}
				
				BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
				bean.setPesquisaImportacao(true);
				bean.setTitulosIncompletos(null); // para o bean buscar novamente
				
				addMensagemInformation(titulosImportacao.size()+" t�tulo(s) foi(foram) importado(s) e salvo(s) pelo sistema com sucesso.");
				
				return bean.telaBuscaCatalogacoesIncompletasTitulo();
			}
		}
	
		if(isCooperacaoAutoridades()){
			
			AutoridadeDao dao = getDAO(AutoridadeDao.class);
			
			Biblioteca bibliotecaUsuarioLogado = dao.findByPrimaryKey(biblioteca, Biblioteca.class, "id");
			
			if(bibliotecaUsuarioLogado == null || bibliotecaUsuarioLogado.getId() <=0 ){
				addMensagemErro("Selecione uma biblioteca para a importa��o.");
				return null;
			}
			
			// Sempre que o arquivo contiver 1 autoridade o sistema vai para a p�gina de cataloga��o
			// n�o � salvo na base porque estava gerando muita confus�o.
			if(autoridadesImportacao.size() == 1
					/* && ( new Long(0).compareTo( dao.countAllAutoridadesComCatalogacaoIncompleta()) == 0) */){ // s� tem uma autoridade
				
				CatalogacaoMBean bean = getMBean("catalogacaoMBean");
				
				Autoridade autoridadeImportacao = autoridadesImportacao.get(0);
				
				autoridadeImportacao.setImportada(true);
				autoridadeImportacao.setBiblotecaImportacao(bibliotecaUsuarioLogado);
				
				// o bean de cataloga��o trata tudo como t�tulo e s� depois na hora de salvar, salva uma autoridade.
				return bean.iniciarAutoridadesImportacao( CatalogacaoUtil.criaTituloAPartirAutoridade(autoridadeImportacao)); // inicia a cataloga��o normalmente
				
			}else{
				
				for (Autoridade autoridadeImportacao : autoridadesImportacao) {
					autoridadeImportacao.setImportada(true);
					autoridadeImportacao.setBiblotecaImportacao(bibliotecaUsuarioLogado);
				}
				
				MovimentoSalvaEntidadesImportadas mov = new MovimentoSalvaEntidadesImportadas(autoridadesImportacao, true);
				mov.setCodMovimento(SigaaListaComando.SALVA_ENTIDADES_IMPORTADAS);
				
				try {
					execute(mov);
				} catch (NegocioException ne) {
					addMensagens(ne.getListaMensagens());
				}
				
				BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
				bean.setAutoridadesIncompletas(null); // para o bean buscar novamente
				
				addMensagemInformation(autoridadesImportacao.size()+" autoridade(s) foi(foram) importada(s) e salva(s) pelo sistema com sucesso.");
				
				return bean.telaBuscaCatalogacoesIncompletasAutoridades();
			}
		}
		
		return null; // nunca deveria chegar aqui
		
	}
	
	
	
	/**
	 * 
	 *   M�todo chamado quando o usu�rio deseja apenas ver os dados que est�o no arquivo antes de import�-lo para o sistema.
	 *
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formImportarTituloAutoridade.jsp
	 *
	 * @return
	 * @throws ArqException
	 * @throws  
	 */
	public String  visualizarDadosDoArquivo() throws ArqException{
		
		if (arquivo == null ){
			addMensagemErro("Escolha um arquivo para a importa��o. ");
			return null;
		}
		
		
		
		try{
		
			BufferedReader r = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
			
			String texto = "";
			
			String linha = null;
			while ((linha  = r.readLine()) != null)
				texto += linha;
			
			MarcReader reader  = new MarcStreamReader(new ByteArrayInputStream(texto.getBytes()));
			
			Record record = null;
			
			int contador = 1;
		
			StringBuilder dadosDoArquivoTemp = new StringBuilder();
			
			while(reader.hasNext()){
				
				dadosDoArquivoTemp.append("<table class=\"subFormulario\" style=\"width: 100%; \">");
				
					dadosDoArquivoTemp.append( "<caption> Registro "+ contador++ +"</caption>");
					
					if(contador % 2 == 0)
						dadosDoArquivoTemp.append("<tr class=\"linhaPar\">");
					else
						dadosDoArquivoTemp.append("<tr class=\"linhaImpar\">");
						
						dadosDoArquivoTemp.append("<td syle=\"width: 900px;\">");
					
							record = reader.next();
							dadosDoArquivoTemp.append(" <pre style=\"margin-left: 5%; font-size: 16px;\">"+ record.toString() +"</pre>");
				
						dadosDoArquivoTemp.append("</td>");
					
					dadosDoArquivoTemp.append("</tr>");
					
				dadosDoArquivoTemp.append("</table>");
				
			}
		
			dadosDoArquivo = dadosDoArquivoTemp.toString();
			
		}catch(IOException ioe){
			ioe.printStackTrace();
			addMensagemErro("N�o foi poss�vel ler o arquivo com os dados no formato MARC ");
			return null;
		}catch (MarcException mex){
			mex.printStackTrace();
			addMensagemErro("O arquivo MARC possui algum erro, impossibilitando a sua interpreta��o. ");
			return null;
		}
		
		
		
		return telaVisualizaRegistrosDoArquivo();
	}
	
	
	
	
	
	
	



		
	/**
	 *   Realiza a leitura dos dados do t�tulo ou autoridade do arquivo ou os que foram digitados pelo usu�rio e monta o t�tulo
	 * 
	 *   OBSERVACAO: O arquivo � sempre lido como UTF8 para suportar os caracteres da l�ngua 
	 * portuguesa, apesar do padr�o ser <strong>ISO 2709</strong>, internamente o sistema vai 
	 * trabalhar com <strong>UTF8</strong>.
	 *
	 * @throws DAOException
	 * @throws IOException 
	 * @throws NegocioException 
	 */
	private void montaInformacoesDoTituloOUAutoridade() throws DAOException, IOException, NegocioException{
		
		MarcReader reader = null;
		
		if( IMPORTAR_ARQUIVO.equals(tipoImportacao)){
		
			if (arquivo != null){ // se o usu�rio escolheu um arquivo
				
				ListaEtiquetas listaEtiquetas = new ListaEtiquetas(); // objeto que mant�m as etiqueta na mem�ria
				
				try{
				
					String texto = "";
					
					BufferedReader r = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
					
					String linha = null;
					while ((linha  = r.readLine()) != null)
						texto += linha;
					
					texto = StringUtils.removeInvalidUtf8(texto);
					
					reader  = new MarcStreamReader(new ByteArrayInputStream(texto.getBytes()));
					
					Record record = null;
					
					while(reader.hasNext()){
						record = reader.next(); // pega 1 registro do arquivo (ele suporta v�rios ao mesmo tempo)
					
						TituloCatalografico tituloImportacao = new TituloCatalografico();
						Autoridade autoridadeImportacao = new Autoridade();
						
						// Basicamente copia os Field do MARC4J para os CAMPOS do Sistema
						// j� adiciona o campo de controle ao t�tulo e vice-versa
						
						if(isCooperacaoBibliografica())
							new CampoControle( montaDadosCampoLider(record.getLeader()), listaEtiquetas.getEtiqueta(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag(), TipoCatalogacao.BIBLIOGRAFICA), -1, tituloImportacao );
						if(isCooperacaoAutoridades())
							new CampoControle( montaDadosCampoLider(record.getLeader()), listaEtiquetas.getEtiqueta(Etiqueta.CAMPO_LIDER_AUTORIDADE.getTag(), TipoCatalogacao.AUTORIDADE), -1, autoridadeImportacao );
						
						for (Iterator iterator = record.getControlFields().iterator(); iterator.hasNext();) {
							ControlField c = (ControlField) iterator.next();
								
							// j� adiciona o campo de controle ao t�tulo e vice-versa
							if(isCooperacaoBibliografica()){
								Etiqueta e = listaEtiquetas.getEtiqueta(c.getTag(), TipoCatalogacao.BIBLIOGRAFICA);
								new CampoControle( CatalogacaoUtil.formataDadosCampoControle(e, c.getData()), e , -1, tituloImportacao );
							}if(isCooperacaoAutoridades()){
								Etiqueta e = listaEtiquetas.getEtiqueta(c.getTag(), TipoCatalogacao.AUTORIDADE);
								new CampoControle(CatalogacaoUtil.formataDadosCampoControle(e, c.getData()), e, -1, autoridadeImportacao );
							}
						}
						
						for (Iterator iterator = record.getDataFields().iterator(); iterator.hasNext();) {
							
							DataField c = (DataField) iterator.next();
							
							if(isCooperacaoBibliografica()){
							
								Etiqueta etiquetaQueVaiSerImportada =  new Etiqueta(c.getTag(), TipoCatalogacao.BIBLIOGRAFICA);
								
								// campos locais de outras institui��es n�o eram para ser importados. 
								// Exce��o para o campo 998 usado na coopera��o com a FGV, pois necessita guardar o hist�rico
								if (! etiquetaQueVaiSerImportada.isEquetaLocal() 
										|| etiquetaQueVaiSerImportada.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES) 
										|| ( etiquetaQueVaiSerImportada.isEquetaLocal() && importarCamposLocais )  ){ 
									
									// cria um campo de dados e j� adiciona ele ao t�tulo
									CampoDados cd  = new CampoDados( listaEtiquetas.getEtiqueta(
											c.getTag(), TipoCatalogacao.BIBLIOGRAFICA), c.getIndicator1(), 
											c.getIndicator2(), tituloImportacao, -1);
											
									for (Iterator iterator2 = c.getSubfields().iterator(); iterator2.hasNext();) {
										Subfield s = (Subfield) iterator2.next();
										new SubCampo(s.getCode(), s.getData(), cd, -1); // aqui j� adiciona o sub campo ao campo dados
									}
								}
							}
							
							if(isCooperacaoAutoridades()){
								
								Etiqueta etiquetaQueVaiSerImportada = new Etiqueta(c.getTag(), TipoCatalogacao.AUTORIDADE);
								
								// campos locais de outras institui��es n�o eram para ser importados.
								// Exce��o para o campo 998 usado na coopera��o com a FGV, pois necessita guardar o hist�rico
								if (!  etiquetaQueVaiSerImportada.isEquetaLocal()
										|| etiquetaQueVaiSerImportada.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES )
										|| ( etiquetaQueVaiSerImportada.isEquetaLocal() && importarCamposLocais)) { 
									
									// cria um campo de dados e j� adiciona ele ao t�tulo
									CampoDados cd = new CampoDados( listaEtiquetas.getEtiqueta(c.getTag(), 
											TipoCatalogacao.AUTORIDADE), c.getIndicator1(), c.getIndicator2(), autoridadeImportacao, -1 );
										
									for (Iterator iterator2 = c.getSubfields().iterator(); iterator2.hasNext();) {
										Subfield s = (Subfield) iterator2.next();
										new SubCampo(s.getCode(), s.getData(), cd, -1); // aqui j� adiciona o sub campo ao campo dados
									}
								}
							}
							
							
						}
					
						
						//TUDO OK, adiciona a lista
						
						if(isCooperacaoBibliografica())
							titulosImportacao.add(tituloImportacao);
						if(isCooperacaoAutoridades())
							autoridadesImportacao.add(autoridadeImportacao);
						
						
					}// fim do while hasNext() registros
				
				}finally{
					listaEtiquetas.fechaConexao();
				}
				
			}
		
		}else{ // o usu�rio digitou as informa��es do arquivo 

			TituloCatalografico tituloImportacao = new TituloCatalografico();
			Autoridade autoridadeImportacao = new Autoridade();
			
			if(isCooperacaoBibliografica()){
				ParseTituloCatalograficoUtil.parseTextoMARC(tituloImportacao, arquivoDigitado, tipoOperacao, importarCamposLocais);
				titulosImportacao.clear();
				titulosImportacao.add(tituloImportacao);
			}
			
			if(isCooperacaoAutoridades()){
				ParseTituloCatalograficoUtil.parseTextoMARC(autoridadeImportacao, arquivoDigitado, tipoOperacao, importarCamposLocais);
				autoridadesImportacao.clear();
				autoridadesImportacao.add(autoridadeImportacao);
			}
		}	
			
	}
	
	
	
	
	/**
	 * Converte o campo Lider que o MARC4J usa para um text para jogar no campo Lider do sistema.
	 * 
	 * @param l
	 * @return
	 */
	private String montaDadosCampoLider(Leader l){
		
		DecimalFormat df = new DecimalFormat("00000");
		
		// monta na ordem:  (Position 0-4) + (Position 5) + (Position 6) + (Position 7-8) 
		//                  + (Position 9) + (Position 10) + (Position 11) + (Position 12-16) 
		//                  + (Position 17-18)  + (Position 19-23)
		String dadosLider = df.format(l.getRecordLength())  + l.getRecordStatus() + l.getTypeOfRecord() 
		 	+ new String(l.getImplDefined1()) + l.getCharCodingScheme() + l.getIndicatorCount() + l.getSubfieldCodeLength()
		 	+ df.format(l.getBaseAddressOfData()) + new String(l.getImplDefined2()) + new String(l.getEntryMap());
		
		return dadosLider;
		
	}
	
	/**
	 * 
	 * Retorna todos os formatos de um t�tulo para um combo box
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllFormatosTitulo() throws DAOException{
		Collection <FormatoMaterial> c = getGenericDAO().findAll(FormatoMaterial.class);
		return toSelectItems(c, "id", "descricao");
	}
	
	/**
	 * Apenas para atualizar as informa��es na p�gina para o usu�rio.
	 *
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formImportarTituloAutoridade.jsp
	 *
	 * @return
	 * @throws ArqException
	 * @throws  
	 */
	public void atualizaPagina(ValueChangeEvent event){
		
	}
	
	////////////////////////////  telas de navega��o /////////////////////////
	
	/**
	 *  Chamado a partir da jsp: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaVisualizarRegistrosDoArquivo.jsp
	 */
	public String telaImportarTitulo(){
		return forward(PAGINA_IMPORTA_TITULO);
	}
	
	/**
	 * Redireciona o fluxo de navega��o para a p�gina de visualiza��o dos registros do arquivo.
	 * 
	 * @return
	 */
	private String telaVisualizaRegistrosDoArquivo() {
		return forward(PAGINA_VISUALIZA_REGISTROS_DO_ARQUIVO);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////

	
	public boolean isCooperacaoAutoridades(){
		return tipoOperacao  == OPERACAO_AUTORIDADE;
	}
	
	public boolean isCooperacaoBibliografica(){
		return tipoOperacao  == OPERACAO_BIBLIOGRAFICA;
	}
	
	
	
	
	
	// sets e gets
	
	/**
	 * <p>M�todo que retorna as bibliotecas do sistema em forma de SelectItem's.</p>
	 * <ul>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <li>/sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formImportarTituloAutoridade.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getBibliotecaList() throws DAOException {
		if(bibliotecaList == null) {
			BibliotecaDao dao = null;
			
			try{
				dao = getDAO(BibliotecaDao.class);
				
				if (!isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) {
					bibliotecaList = dao.findAllBibliotecasInternasAtivasPorUnidade(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO));
				} else {
					bibliotecaList = dao.findAllBibliotecasInternasAtivas();
				}
			} finally {
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(bibliotecaList, "id", "descricao");
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}


	public short getTipoOperacao() {
		return tipoOperacao;
	}


	public void setTipoOperacao(short tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}


	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}


	public String getArquivoDigitado() {
		return arquivoDigitado;
	}


	public void setArquivoDigitado(String arquivoDigitado) {
		this.arquivoDigitado = arquivoDigitado;
	}

	public List<TituloCatalografico> getTitulosImportacao() {
		return titulosImportacao;
	}

	public void setTitulosImportacao(List<TituloCatalografico> titulosImportacao) {
		this.titulosImportacao = titulosImportacao;
	}

	public List<Autoridade> getAutoridadesImportacao() {
		return autoridadesImportacao;
	}

	public void setAutoridadesImportacao(List<Autoridade> autoridadesImportacao) {
		this.autoridadesImportacao = autoridadesImportacao;
	}

	public Integer getTipoImportacao() {
		return tipoImportacao;
	}


	public void setTipoImportacao(Integer tipoImportacao) {
		this.tipoImportacao = tipoImportacao;
	}

	
	public boolean isImportacaoArquivo(){
		return IMPORTAR_ARQUIVO.equals(tipoImportacao);
	}
	
	
	public boolean isImportacaoDados(){
		return IMPORTAR_DADOS_DIGITADOS.equals(tipoImportacao);
	}


	public boolean isImportouNovasEntidadesExistindoTituloNaoFinalizados() {
		return importouNovasEntidadesExistindoTituloNaoFinalizados;
	}


	public void setImportouNovasEntidadesExistindoTituloNaoFinalizados(
			boolean importouNovasEntidadesExistindoTituloNaoFinalizados) {
		this.importouNovasEntidadesExistindoTituloNaoFinalizados = importouNovasEntidadesExistindoTituloNaoFinalizados;
	}


	public String getDadosDoArquivo() {
		return dadosDoArquivo;
	}


	public void setDadosDoArquivo(String dadosDoArquivo) {
		this.dadosDoArquivo = dadosDoArquivo;
	}

	
	public boolean isImportandoArquivo(){
		return tipoImportacao.equals(IMPORTAR_ARQUIVO);
	}

	public boolean isImportarCamposLocais() {
		return importarCamposLocais;
	}

	public void setImportarCamposLocais(boolean importarCamposLocais) {
		this.importarCamposLocais = importarCamposLocais;
	}


	public int getBiblioteca() {
		return biblioteca;
	}


	public void setBiblioteca(int biblioteca) {
		this.biblioteca = biblioteca;
	}
	
	
	
}