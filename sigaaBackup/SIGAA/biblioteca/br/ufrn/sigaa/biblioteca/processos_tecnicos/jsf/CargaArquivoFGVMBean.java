/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/09/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArquivoDeCargaNumeroControleFGVDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *    MBean que faz a carga dos arquivos que geram os n�meros de controle usados no campo 001
 * para a coopera��o t�cnica com a FGV. Tanto para T�tulos quanto para Autoridades.<br/><br/>
 * 
 * 
 * 	  <strong>OBS.: Os arquivos s�o lidos e apenas os seus conte�dos s�o salvos no banco.</strong>	
 *
 * @author jadson
 * @since 15/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("cargaArquivoFGVMBean")
@Scope("request")
public class CargaArquivoFGVMBean extends SigaaAbstractController<ArquivoDeCargaNumeroControleFGV>{

	public static final short OPERACAO_BIBLIOGRAFICA = 1; // vai importar o arquivo com os n�meros de controle de T�tulos
	public static final short OPERACAO_AUTORIDADE = 2;     // vai importar o arquivo com os n�meros de controle de Autoridades
	
	public static final String PAGINA_CARREGA_ARQUIVO_TITULOS = "/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp";
	public static final String PAGINA_CARREGA_ARQUIVO_AUTORIDADES = "/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp";
	
	
	private short tipoOperacao = OPERACAO_BIBLIOGRAFICA;
	
	
	private UploadedFile arquivo;       // aguarda o arquivo que o usu�rio escolheu
	
	
	// Se o usu�rio preferir digitar a sequ�ncia no lugar de usar o arquivo.
	private String numeroInicial;
	private String numeroFinal;
	
	
	// Guarda os arquivos j� carregados no sistema para mostrar ao usu�rio, para o usu�rio saber caso
	// exista algum arquivo carregado anteriormente com n�meros ainda n�o usados.
	private List<ArquivoDeCargaNumeroControleFGV> 
		arquivosCarregadosAtivos = new ArrayList<ArquivoDeCargaNumeroControleFGV>();
	
	
	private static String codigoDaBiblioteca;
	
	
	/**
	 * Inicia a caso de uso que carrega os n�meros de controle da FGV para T�tulos
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCargaArquivoTitulo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		tipoOperacao = OPERACAO_BIBLIOGRAFICA;
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		codigoDaBiblioteca = ParametroHelper.getInstance().getParametro(
				ParametrosBiblioteca.CODIGO_NUMERO_CONTROLE_BIBLIOGRAFICO);
		
		arquivosCarregadosAtivos = getDAO(ArquivoDeCargaNumeroControleFGVDao.class)
			.findAllArquivosTituloAtivosOrderByDataCarga(); 
		
		if(arquivosCarregadosAtivos.size() == 0)
			addMensagemWarning("N�o existem n�mero do controle dispon�veis para exporta��o de T�tulos para a FGV.");
		
		return telaCarregaArquivoTitulo();
	}
	
	
	/**
	 * 
	 * Inicia a caso de uso que carrega os n�meros de controle da FGV para Autoridades
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCargaArquivoAutoridades() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		tipoOperacao = OPERACAO_AUTORIDADE;
	
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		codigoDaBiblioteca = ParametroHelper.getInstance().getParametro(
				ParametrosBiblioteca.CODIGO_NUMERO_CONTROLE_AUTORIDADES);
		
		arquivosCarregadosAtivos = getDAO(ArquivoDeCargaNumeroControleFGVDao.class)
			.findAllArquivosAutoridadeAtivosOrderByDataCarga(); 
	
		if(arquivosCarregadosAtivos.size() == 0)
			addMensagemWarning("N�o existem n�mero do controle dispon�veis para exporta��o de Autoridades para a FGV.");
		
		return telaCarregaArquivoAutoridades();
	}
	
	
	
	/**
	 *     M�todo que submete o arquivo com os n�meros de controle da FGV pelo usu�rio.
	 * 
	 * Chamado a partir da p�gina:  /biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp
	 * 							   	/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp
	 * @throws  
	 * 
	 * @throws IOException 
	 * 
	 */
	public String submeterArquivo() throws ArqException{
	
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		if (arquivo == null ){
			
			if(StringUtils.isEmpty(numeroInicial) || StringUtils.isEmpty(numeroFinal)){
				addMensagemErro("Escolha o arquivo com os n�meros de controle da FGV ou digite o n�mero inicial e final da sequ�ncia.");
				return null;
			}
		}
		
		
		if (arquivo != null ){
			
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
			
				String linha = r.readLine(); // O arquivo sempe tem uma linha.
			
				if(StringUtils.isEmpty(linha)){
					addMensagemErro("O arquivo importado n�o possui dados");
					return null;
				}
				
				// dadosDoArquivo[0] = n� inicial da sequ�ncia
				// dadosDoArquivo[1] = n� final da sequ�ncia
				// dadosDoArquivo[2] = check sum do arquivo
				String[] dadosDoArquivo = linha.split("\\s+"); // separa a cada 1 ou mais ocorr�ncias de espa�os
				
				
				ArquivoDeCargaNumeroControleFGV arquivoDeCarga = null;
				
				if(isCarregamentoArquivoTitulo()){
					arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_BIBLIOGRAFICO );
				}
				
				if(isCarregamentoArquivoAutoridade()){
					arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES );
				}
				
				if(dadosDoArquivo.length != 3){
					addMensagemErro("O arquivo importado n�o est� no padr�o do arquivo de carga do n�mero de controle da FGV");
					return null;
				}
				
				if(StringUtils.isEmpty(dadosDoArquivo[0])){
					addMensagemErro("O arquivo importado n�o possui o n�mero inicial da sequ�ncia");
					return null;
				}
				
				if(StringUtils.isEmpty(dadosDoArquivo[1])){
					addMensagemErro("O arquivo importado n�o possui o n�mero final da sequ�ncia");
					return null;
				}
				
				if(arquivoDeCarga != null){
				
					try{
						// retira o "RN" do n�mero inicial da sequ�ncia			
						arquivoDeCarga.setNumeroInicialSequencia(Integer.parseInt( dadosDoArquivo[0].substring(2, dadosDoArquivo[0].length())));
						arquivoDeCarga.setNumeroAtualSequencia(Integer.parseInt( dadosDoArquivo[0].substring(2, dadosDoArquivo[0].length())));
						arquivoDeCarga.setNumeroFinalSequencia(Integer.parseInt( dadosDoArquivo[1].substring(2, dadosDoArquivo[1].length())));
					}catch(NumberFormatException nfe){
						addMensagemErro("O arquivo importado n�o est� no padr�o do arquivo de carga do n�mero de controle da FGV");
						return null;
					}
				
					
					if( existeSequenciaConflitanteNoSistema(arquivoDeCarga) ){
						addMensagemErro("J� existe n�meros de controle salvos no sistema cujo intervalo entra em conflito com os n�meros presentes no arquivo  ");
						return null;
					}
					
					ListaMensagens lista = salvaNumeroDeControleNaBase(arquivoDeCarga);
					
					if (lista != null && !lista.isEmpty()) {
						addMensagens(lista);
						return null;                     
					}
		
				}
				
				if(isCarregamentoArquivoTitulo()){
					return iniciarCargaArquivoTitulo();
				}
				
				if(isCarregamentoArquivoAutoridade()){
					return iniciarCargaArquivoAutoridades();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				addMensagemErro("O sistema n�o conseguiu ler o arquivo.");
			}
	
		}else{
			
			ArquivoDeCargaNumeroControleFGV arquivoDeCarga = null;
			
			if(isCarregamentoArquivoTitulo()){
				arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_BIBLIOGRAFICO );
			}
			
			if(isCarregamentoArquivoAutoridade()){
				arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES );
			}
			
			if(arquivoDeCarga != null){
				
				try{
					arquivoDeCarga.setNumeroInicialSequencia(Integer.parseInt( numeroInicial.substring(2, numeroInicial.length())));
					arquivoDeCarga.setNumeroAtualSequencia(arquivoDeCarga.getNumeroInicialSequencia());
					arquivoDeCarga.setNumeroFinalSequencia(Integer.parseInt( numeroFinal.substring(2, numeroFinal.length())));
				}catch(NumberFormatException nfe){
					addMensagemErro("O n�mero inicial e final da sequ�ncia n�o est�o no formato dos n�meros do controle da FGV.");
					return null;
				}catch(StringIndexOutOfBoundsException sobex){
					addMensagemErro("O n�mero inicial e final da sequ�ncia n�o est�o no formato dos n�meros do controle da FGV.");
					return null;
				}
				
				
				if( existeSequenciaConflitanteNoSistema(arquivoDeCarga) ){
					addMensagemErro("J� existe n�meros de controle salvos no sistema cujo intervalo entra em conflito com o intervalo digitado. ");
					return null;
				}
				
				ListaMensagens lista = salvaNumeroDeControleNaBase(arquivoDeCarga);
				
				if (lista != null && !lista.isEmpty()) {
					addMensagens(lista);
					return null;                       
				}
				
			}
			
			if(isCarregamentoArquivoTitulo()){
				return iniciarCargaArquivoTitulo();
			}
			
			if(isCarregamentoArquivoAutoridade()){
				return iniciarCargaArquivoAutoridades();
			}
			
		}
		
		return null; // s� chega aqui se der algum erro no carregamento.
		
	}
	
	
	

	/**
	 *   M�todo que verifica se j� existe outra sequ�ncia no sistema em conflito que a sequ�ncia 
	 *   que se est� desejando importar.
	 * @throws DAOException 
	 */
	private boolean existeSequenciaConflitanteNoSistema(ArquivoDeCargaNumeroControleFGV arquivoDeCarga) throws DAOException{
		
		List<ArquivoDeCargaNumeroControleFGV> arquivosCarregados = new ArrayList<ArquivoDeCargaNumeroControleFGV>();
		
		if(isCarregamentoArquivoTitulo()){
			arquivosCarregados  = getDAO(ArquivoDeCargaNumeroControleFGVDao.class).findAllArquivosTituloOrderByDataCarga(); 
		}
		
		if(isCarregamentoArquivoAutoridade()){
			arquivosCarregados  = getDAO(ArquivoDeCargaNumeroControleFGVDao.class).findAllArquivosAutoridadeOrderByDataCarga(); 
		}
		
		for (ArquivoDeCargaNumeroControleFGV arquivoSalvo : arquivosCarregados) {
			
			/*     Verifica se j� tem um arquivo com a mesmo intervalo de n�meros salvo no sistema
			 * Se a sequ�ncia inicial ou final est� entre a sequ�ncia inicial e final de um arquivo j� salvo
			 * e vice-versa 
			 */
			if(arquivoDeCarga.getNumeroInicialSequencia() >= arquivoSalvo.getNumeroInicialSequencia()
					&& arquivoDeCarga.getNumeroInicialSequencia() <= arquivoSalvo.getNumeroFinalSequencia()
					||
					(arquivoDeCarga.getNumeroFinalSequencia() >= arquivoSalvo.getNumeroInicialSequencia()
							&& arquivoDeCarga.getNumeroFinalSequencia() <= arquivoSalvo.getNumeroFinalSequencia()) 	
			        ||
			        (arquivoSalvo.getNumeroInicialSequencia() >= arquivoDeCarga.getNumeroInicialSequencia()
							&& arquivoSalvo.getNumeroInicialSequencia() <= arquivoDeCarga.getNumeroFinalSequencia()
							||
							(arquivoSalvo.getNumeroFinalSequencia() >= arquivoDeCarga.getNumeroInicialSequencia()
									&& arquivoSalvo.getNumeroFinalSequencia() <= arquivoDeCarga.getNumeroFinalSequencia()) ) ){
				
				return true;
				
			}
			
		}
		
		return false;
	}
	
	
	
	
	/**
	 *  Salva os n�meros de controle na base
	 */
	private ListaMensagens salvaNumeroDeControleNaBase(ArquivoDeCargaNumeroControleFGV arquivoDeCarga) throws ArqException{
		
		
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setCodMovimento(ArqListaComando.CADASTRAR);
		movimento.setObjMovimentado(arquivoDeCarga);
		
		ListaMensagens lista = arquivoDeCarga.validate();
		
		if (lista == null || lista.isEmpty()) {
		
			try{
				
				execute(movimento);
			
				String mensagemUsuario = " Faixa de N�meros de Controle de: "
					+codigoDaBiblioteca+arquivoDeCarga.getNumeroInicialSequenciaFormatado()+" at�: "
					+codigoDaBiblioteca+arquivoDeCarga.getNumeroFinalSequenciaFormatado()+" carregados e dispon�veis para serem usados ";
				
				if(isCarregamentoArquivoTitulo()){
					mensagemUsuario = mensagemUsuario + "para a coopera��o de T�tulos.";
				}
				
				if(isCarregamentoArquivoAutoridade()){
					arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES );
					mensagemUsuario = mensagemUsuario + "para a coopera��o de Autoridades.";
				}
				
				addMensagemInformation(mensagemUsuario);
				
			
			}catch(NegocioException ne){
				if(lista == null)
					lista = new ListaMensagens();
				
				lista.addAll(ne.getListaMensagens());
			}
		
		}
		
		return lista;
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * Retorna o c�digo da biblioteca no cat�logo coletivo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp
	 *                             /biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp
	 *
	 * @return
	 */
	public String getCodigoBibliotecaCatalogaColetivo(){
		return codigoDaBiblioteca;
	}
	
	
	////////////////////////////  Telas de Navega��o   //////////////////////////////////
	
	/**
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp
	 */
	public String telaCarregaArquivoTitulo(){
		return forward(PAGINA_CARREGA_ARQUIVO_TITULOS);
	}
	
	/**
	 * 
	 *Chamado a partir da p�gina:  /biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp
	 *
	 * @return
	 */
	public String telaCarregaArquivoAutoridades(){
		return forward(PAGINA_CARREGA_ARQUIVO_AUTORIDADES);
	}

	///////////////////////////////////////////////////////////////////////////////////////
	

	public short getTipoOperacao() {
		return tipoOperacao;
	}


	public void setTipoOperacao(short tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}


	public UploadedFile getArquivo() {
		return arquivo;
	}


	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}
	
	
	/**
	 *    Diz o tipo de opera��o que est� sendo feita
	 *
	 * @return
	 */
	public boolean isCarregamentoArquivoTitulo(){
		if(tipoOperacao == OPERACAO_BIBLIOGRAFICA)
			return true;
		else
			return false;
	}
	
	
	/**
	 *    Diz o tipo de opera��o que est� sendo feita
	 *
	 * @return
	 */
	public boolean isCarregamentoArquivoAutoridade(){
		if(tipoOperacao == OPERACAO_AUTORIDADE)
			return true;
		else
			return false;
	}


	public List<ArquivoDeCargaNumeroControleFGV> getArquivosCarregadosAtivos() {
		return arquivosCarregadosAtivos;
	}


	public void setArquivosCarregadosAtivos(List<ArquivoDeCargaNumeroControleFGV> arquivosCarregadosAtivos) {
		this.arquivosCarregadosAtivos = arquivosCarregadosAtivos;
	}


	public String getNumeroInicial() {
		return numeroInicial;
	}


	public void setNumeroInicial(String numeroInicial) {
		this.numeroInicial = numeroInicial;
	}


	public String getNumeroFinal() {
		return numeroFinal;
	}


	public void setNumeroFinal(String numeroFinal) {
		this.numeroFinal = numeroFinal;
	}
	
	
	
	
}
