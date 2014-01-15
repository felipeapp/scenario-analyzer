/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *    MBean que faz a carga dos arquivos que geram os números de controle usados no campo 001
 * para a cooperação técnica com a FGV. Tanto para Títulos quanto para Autoridades.<br/><br/>
 * 
 * 
 * 	  <strong>OBS.: Os arquivos são lidos e apenas os seus conteúdos são salvos no banco.</strong>	
 *
 * @author jadson
 * @since 15/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("cargaArquivoFGVMBean")
@Scope("request")
public class CargaArquivoFGVMBean extends SigaaAbstractController<ArquivoDeCargaNumeroControleFGV>{

	public static final short OPERACAO_BIBLIOGRAFICA = 1; // vai importar o arquivo com os números de controle de Títulos
	public static final short OPERACAO_AUTORIDADE = 2;     // vai importar o arquivo com os números de controle de Autoridades
	
	public static final String PAGINA_CARREGA_ARQUIVO_TITULOS = "/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp";
	public static final String PAGINA_CARREGA_ARQUIVO_AUTORIDADES = "/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp";
	
	
	private short tipoOperacao = OPERACAO_BIBLIOGRAFICA;
	
	
	private UploadedFile arquivo;       // aguarda o arquivo que o usuário escolheu
	
	
	// Se o usuário preferir digitar a sequência no lugar de usar o arquivo.
	private String numeroInicial;
	private String numeroFinal;
	
	
	// Guarda os arquivos já carregados no sistema para mostrar ao usuário, para o usuário saber caso
	// exista algum arquivo carregado anteriormente com números ainda não usados.
	private List<ArquivoDeCargaNumeroControleFGV> 
		arquivosCarregadosAtivos = new ArrayList<ArquivoDeCargaNumeroControleFGV>();
	
	
	private static String codigoDaBiblioteca;
	
	
	/**
	 * Inicia a caso de uso que carrega os números de controle da FGV para Títulos
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
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
			addMensagemWarning("Não existem número do controle disponíveis para exportação de Títulos para a FGV.");
		
		return telaCarregaArquivoTitulo();
	}
	
	
	/**
	 * 
	 * Inicia a caso de uso que carrega os números de controle da FGV para Autoridades
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
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
			addMensagemWarning("Não existem número do controle disponíveis para exportação de Autoridades para a FGV.");
		
		return telaCarregaArquivoAutoridades();
	}
	
	
	
	/**
	 *     Método que submete o arquivo com os números de controle da FGV pelo usuário.
	 * 
	 * Chamado a partir da página:  /biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp
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
				addMensagemErro("Escolha o arquivo com os números de controle da FGV ou digite o número inicial e final da sequência.");
				return null;
			}
		}
		
		
		if (arquivo != null ){
			
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
			
				String linha = r.readLine(); // O arquivo sempe tem uma linha.
			
				if(StringUtils.isEmpty(linha)){
					addMensagemErro("O arquivo importado não possui dados");
					return null;
				}
				
				// dadosDoArquivo[0] = nº inicial da sequência
				// dadosDoArquivo[1] = nº final da sequência
				// dadosDoArquivo[2] = check sum do arquivo
				String[] dadosDoArquivo = linha.split("\\s+"); // separa a cada 1 ou mais ocorrências de espaços
				
				
				ArquivoDeCargaNumeroControleFGV arquivoDeCarga = null;
				
				if(isCarregamentoArquivoTitulo()){
					arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_BIBLIOGRAFICO );
				}
				
				if(isCarregamentoArquivoAutoridade()){
					arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES );
				}
				
				if(dadosDoArquivo.length != 3){
					addMensagemErro("O arquivo importado não está no padrão do arquivo de carga do número de controle da FGV");
					return null;
				}
				
				if(StringUtils.isEmpty(dadosDoArquivo[0])){
					addMensagemErro("O arquivo importado não possui o número inicial da sequência");
					return null;
				}
				
				if(StringUtils.isEmpty(dadosDoArquivo[1])){
					addMensagemErro("O arquivo importado não possui o número final da sequência");
					return null;
				}
				
				if(arquivoDeCarga != null){
				
					try{
						// retira o "RN" do número inicial da sequência			
						arquivoDeCarga.setNumeroInicialSequencia(Integer.parseInt( dadosDoArquivo[0].substring(2, dadosDoArquivo[0].length())));
						arquivoDeCarga.setNumeroAtualSequencia(Integer.parseInt( dadosDoArquivo[0].substring(2, dadosDoArquivo[0].length())));
						arquivoDeCarga.setNumeroFinalSequencia(Integer.parseInt( dadosDoArquivo[1].substring(2, dadosDoArquivo[1].length())));
					}catch(NumberFormatException nfe){
						addMensagemErro("O arquivo importado não está no padrão do arquivo de carga do número de controle da FGV");
						return null;
					}
				
					
					if( existeSequenciaConflitanteNoSistema(arquivoDeCarga) ){
						addMensagemErro("Já existe números de controle salvos no sistema cujo intervalo entra em conflito com os números presentes no arquivo  ");
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
				addMensagemErro("O sistema não conseguiu ler o arquivo.");
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
					addMensagemErro("O número inicial e final da sequência não estão no formato dos números do controle da FGV.");
					return null;
				}catch(StringIndexOutOfBoundsException sobex){
					addMensagemErro("O número inicial e final da sequência não estão no formato dos números do controle da FGV.");
					return null;
				}
				
				
				if( existeSequenciaConflitanteNoSistema(arquivoDeCarga) ){
					addMensagemErro("Já existe números de controle salvos no sistema cujo intervalo entra em conflito com o intervalo digitado. ");
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
		
		return null; // só chega aqui se der algum erro no carregamento.
		
	}
	
	
	

	/**
	 *   Método que verifica se já existe outra sequência no sistema em conflito que a sequência 
	 *   que se está desejando importar.
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
			
			/*     Verifica se já tem um arquivo com a mesmo intervalo de números salvo no sistema
			 * Se a sequência inicial ou final está entre a sequência inicial e final de um arquivo já salvo
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
	 *  Salva os números de controle na base
	 */
	private ListaMensagens salvaNumeroDeControleNaBase(ArquivoDeCargaNumeroControleFGV arquivoDeCarga) throws ArqException{
		
		
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setCodMovimento(ArqListaComando.CADASTRAR);
		movimento.setObjMovimentado(arquivoDeCarga);
		
		ListaMensagens lista = arquivoDeCarga.validate();
		
		if (lista == null || lista.isEmpty()) {
		
			try{
				
				execute(movimento);
			
				String mensagemUsuario = " Faixa de Números de Controle de: "
					+codigoDaBiblioteca+arquivoDeCarga.getNumeroInicialSequenciaFormatado()+" até: "
					+codigoDaBiblioteca+arquivoDeCarga.getNumeroFinalSequenciaFormatado()+" carregados e disponíveis para serem usados ";
				
				if(isCarregamentoArquivoTitulo()){
					mensagemUsuario = mensagemUsuario + "para a cooperação de Títulos.";
				}
				
				if(isCarregamentoArquivoAutoridade()){
					arquivoDeCarga = new ArquivoDeCargaNumeroControleFGV(ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES );
					mensagemUsuario = mensagemUsuario + "para a cooperação de Autoridades.";
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
	 * Retorna o código da biblioteca no catálogo coletivo.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp
	 *                             /biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp
	 *
	 * @return
	 */
	public String getCodigoBibliotecaCatalogaColetivo(){
		return codigoDaBiblioteca;
	}
	
	
	////////////////////////////  Telas de Navegação   //////////////////////////////////
	
	/**
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoTitulos.jsp
	 */
	public String telaCarregaArquivoTitulo(){
		return forward(PAGINA_CARREGA_ARQUIVO_TITULOS);
	}
	
	/**
	 * 
	 *Chamado a partir da página:  /biblioteca/processos_tecnicos/cooperacao_tecnica/paginaCarregaArquivoAutoridades.jsp
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
	 *    Diz o tipo de operação que está sendo feita
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
	 *    Diz o tipo de operação que está sendo feita
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
