package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoNotasDisciplinaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RetornoNotaAEIntegracaoMoodle;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCadastroNotasAE;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCadastroNotasDisciplina;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Classe respons�vel por gerenciar o lan�amento de notas das disciplinas
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */
@Scope("session")
@Component("lancamentoNotasDisciplina")
public class LancamentoNotasDisciplinaMBean extends SigaaAbstractController{
	
	//URLS DE NAVEGA��O
	/** Vari�vel est�tica com o link para a p�gina inicial do portal */
	public final static String JSP_PRINCIPAL = "/metropole_digital/principal.jsf";
	
	/** Vari�vel est�tica com o link para a p�gina de lan�amento de notas por disciplina*/
	public final static String JSP_LANCAR_NOTAS = "/metropole_digital/lancar_notas_disciplina/form.jsf";
	
	/** Vari�vel est�tica com o link para o relat�rio de notas por disciplina*/
	public final static String JSP_VISUALIZAR_NOTAS = "/metropole_digital/lancar_notas_disciplina/view.jsf";
	
	/** Vari�vel est�tica com o link para a p�gina com as listas das disciplinas vinculadas a turma*/
	public final static String JSP_DISCIPLINAS_TURMA = "/metropole_digital/lancar_notas_disciplina/disciplinas_turma.jsf";
	
	/** Entidade que armazena a turma de entrada selecionada na p�gina inicial do portal na qual as notas ser�o informadas **/
	private TurmaEntradaTecnico turmaEntrada;
	
	/** Entidade que armazena a disciplina selecionada para as notas serem informadas **/
	private Turma disciplina;
	
	/** Entidade que armazena uma cole��o de Matriculas que corresponde aos discentes matriculados em uma determinada disciplina **/
	private List<MatriculaComponente> listaMatriculaDiscentes; 
	
	/** Entidade que armazena uma cole��o de disciplinas que pertencem a turma selecionada**/
	private List<Turma> listaDisciplinas;
	
	/** Entidade que armazena uma cole��o de matr�culas em uma disciplina**/
	private List<MatriculaTurma> matriculasTurma = new ArrayList<MatriculaTurma>();
	
	/** Vari�vel respons�vel por armazenar a String de retorno das notas AE do MOODLE **/
	String retornoAEMooodle;
	
	private List<NotaIMD>listaNotasDiscentes = new ArrayList<NotaIMD>();
	
	
	/** Construtor da classe **/
	public LancamentoNotasDisciplinaMBean() {
		retornoAEMooodle = "";
		turmaEntrada = new TurmaEntradaTecnico();
		disciplina = new Turma();
		listaMatriculaDiscentes = new ArrayList<MatriculaComponente>();
		listaMatriculaDiscentes = Collections.emptyList();
		listaDisciplinas = new ArrayList<Turma>();
		listaDisciplinas = Collections.emptyList();
	}
	
	
	/**
	 * Redireciona para a p�gina respons�vel por listar as disciplinas vinculadas a turma selecionada.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/lancar_notas_disciplina/disciplinas_turma.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws DAOException 
	 */
	public String listarDisciplinasTurma() throws DAOException{
		LancamentoNotasDisciplinaDao dao = getDAO(LancamentoNotasDisciplinaDao.class);
		try {
			turmaEntrada = dao.findByPrimaryKey((Integer)getParameterInt("id"), TurmaEntradaTecnico.class);
			listaDisciplinas = dao.findDisciplinasByEspecializacao(turmaEntrada.getEspecializacao().getId());	
			if (listaDisciplinas.isEmpty()) {
				addMessage("Esta turma n�o possui disciplinas abertas para o lan�amento de notas.", TipoMensagemUFRN.ERROR);
				return redirect(JSP_PRINCIPAL);
			}
		} finally {
			dao.close();
		}
		return forward(JSP_DISCIPLINAS_TURMA);
	}
	
	
	/**
	 * Redireciona para a p�gina de lan�amento de Notas
	 * 
 	 *	M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/lancar_notas_disciplina/disciplinas_turma.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws DAOException 
	 */
	public String lancarNotas() throws DAOException{
		LancamentoNotasDisciplinaDao dao = getDAO(LancamentoNotasDisciplinaDao.class);
		Integer idDisciplina = getParameterInt("idDisciplina");
		disciplina = getDAO(TurmaDao.class).findByPrimaryKey(idDisciplina);
		TurmaEntradaTecnico t = getDAO(TurmaEntradaTecnicoDao.class).findTurmaEntradaByEspecializacao(disciplina.getEspecializacao().getId());
		
		listaNotasDiscentes = dao.findNotasAlunos(t.getId(), disciplina);
		
		return forward(JSP_LANCAR_NOTAS);
	}
	
	/**
	 * Retorna para a p�gina de sele��o das disciplinas
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * 
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltar(){
		return forward(JSP_DISCIPLINAS_TURMA);
	}
	
	/**
	 * Salva as notas informadas na tela de lan�amento de notas
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws ArqException 
	 * @throws NegocioException, ArqException
	 */
	public String salvar() throws ArqException {		
		MovimentoCadastroNotasDisciplina mov = new MovimentoCadastroNotasDisciplina();		
		try {
			mov.setNotasIMD(listaNotasDiscentes);		
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTAS_DISCIPLINA);
			prepareMovimento(SigaaListaComando.CADASTRAR_NOTAS_DISCIPLINA);
			execute(mov);
			removeOperacaoAtiva();
			addMessage("Opera��o Realizada com Sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}					
		return forward(JSP_LANCAR_NOTAS);
	}
	
	
	/**
	 * Redireciona para a p�gina de cadastro das notas  
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws
	 */
	public String visualizarNotas(){
		
		return forward(JSP_VISUALIZAR_NOTAS);
	}
	
	
	//GETTERS AND SETTERS
	public List<Turma> getListaDisciplinas() {
		return listaDisciplinas;
	}


	public void setListaDisciplinas(List<Turma> listaDisciplinas) {
		this.listaDisciplinas = listaDisciplinas;
	}


	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}


	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}


	public List<MatriculaComponente> getListaMatriculaDiscentes() {
		return listaMatriculaDiscentes;
	}


	public void setListaMatriculaDiscentes(
			List<MatriculaComponente> listaMatriculaDiscentes) {
		this.listaMatriculaDiscentes = listaMatriculaDiscentes;
	}


	public Turma getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Turma disciplina) {
		this.disciplina = disciplina;
	}


	public String getRetornoAEMooodle() {
		return retornoAEMooodle;
	}


	public void setRetornoAEMooodle(String retornoAEMooodle) {
		this.retornoAEMooodle = retornoAEMooodle;
	}
	
	
	public List<MatriculaTurma> getMatriculasTurma() {
		return matriculasTurma;
	}


	public void setMatriculasTurma(List<MatriculaTurma> matriculasTurma) {
		this.matriculasTurma = matriculasTurma;
	}
	
	public List<NotaIMD> getListaNotasDiscentes() {
		return listaNotasDiscentes;
	}


	public void setListaNotasDiscentes(List<NotaIMD> listaNotasDiscentes) {
		this.listaNotasDiscentes = listaNotasDiscentes;
	}


	/*FUN��ES RESPONS�VEL PELA INTEGRA��O ENTRE SIGAA E MOODLE*/
	/** 
	 * Fun��o que carrega a integra��o das notas AE do moodle
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws Exception 
	 */
	public String carregarIntegracaoAeMoodle() throws Exception{
		GenericSigaaDAO dao = getDAO(GenericSigaaDAO.class);
		try {
			
			retornoAEMooodle = "";
			
			importarAEMoodle();
			
			// Adicionando ao String de retorno o caracter delimitador do fim do texto
			retornoAEMooodle += "^";
			
			int qtdNotasSincronizadas = 0;
			
			List<RetornoNotaAEIntegracaoMoodle> listaRetorno = new ArrayList<RetornoNotaAEIntegracaoMoodle>();
			listaRetorno =  (List<RetornoNotaAEIntegracaoMoodle>) lerRegistrosRetornoNotaAEIntegracaoMoodle(retornoAEMooodle);
			
			if(! listaRetorno.isEmpty()) {
				MovimentoCadastroNotasAE mov = new MovimentoCadastroNotasAE();		
				try {
					mov.setListaNotasIMD(listaNotasDiscentes);		
					mov.setListaRetornoMoodle(listaRetorno);
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTAS_AE);
					prepareMovimento(SigaaListaComando.CADASTRAR_NOTAS_AE);
					qtdNotasSincronizadas = execute(mov);
					removeOperacaoAtiva();
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					return null;
				}					
				
				if(qtdNotasSincronizadas > 0){
					addMessage("Sincroniza��o efetuada com sucesso. ", TipoMensagemUFRN.INFORMATION);
				}
				else {
					addMessage("Todas as notas j� haviam sido sincronizadas." , TipoMensagemUFRN.INFORMATION);
				}
			} 
			else {
				addMessage("Ocorreu um problema na leitura das informa��es junto ao Moodle. As notas n�o foram sincronizadas." , TipoMensagemUFRN.ERROR);
			}
			
			return forward(JSP_LANCAR_NOTAS);
		}
		finally {
			dao.close();
		}
	}


	/** 
	 * Fun��o repons�vel por disparar a requisi��o de conex�o HTTP a uma p�gina externa.
	 * Nessa fun��o ser�o criados e informados os par�metros e a URL a ser requisitada
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return
	 * @throws Exception 
	 */
	public void importarAEMoodle () throws Exception{
		try {
			// a string da url  
			String urlString = "http://moodle.imd.ufrn.br/blocks/genome/notas_ae_sigaa.php";  
			  
			// os parametros a serem enviados  
			Properties parameters = new Properties();  
			
			parameters.setProperty("curso", turmaEntrada.getDadosTurmaIMD().getCodigoIntegracao());
			parameters.setProperty("disciplina", "" + disciplina.getDisciplina().getCodigo());
			
			
			this.retornoAEMooodle = conexaoHttp(urlString, parameters);
			
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	
	/** 
	 * Fun��o respons�vel por realizar a requisi��o de conex�o HTTP a uma p�gina externa
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param urlString: URL da p�gina externa a ser requisitada; parameters: Par�metros passados na requisi��o via m�todo GET
	 * @throws Exception 
	 * @return String de retorno da integra��o com Moodle
	 */
	public String conexaoHttp(String urlString, Properties parameters) throws IOException{
		try {
  
			// o iterador, para criar a URL  
			Iterator i = parameters.keySet().iterator();  
			// o contador  
			int counter = 0;  
			  
			// enquanto ainda existir parametros  
			while (i.hasNext()) {  
			  
			    // pega o nome  
			    String name = (String) i.next();  
			    // pega o valor  
			    String value = parameters.getProperty(name);  
			  
			    // adiciona com um conector (? ou &)  
			    // o primeiro � ?, depois s�o &  
			    urlString += (++counter == 1 ? "?" : "&")  
			        + name  
			        + "="  
			        + value;  
			  
			}  
			
			// cria o objeto url  
			URL url = new URL(urlString); 
			
			// cria o objeto httpurlconnection  
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			
			// seta o metodo  
			connection.setRequestProperty("Request-Method", "GET"); 
			
			
			// seta a variavel para ler o resultado  
			connection.setDoInput(true);  
			connection.setDoOutput(false);  
			
			
			// conecta com a url destino  
			connection.connect();  
			
			// abre a conex�o pra input  
			BufferedReader br =   new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			
			
			// le ate o final  
			StringBuffer newData = new StringBuffer(10000);  
			String s = "";  
			while (null != ((s = br.readLine()))) {  
			    newData.append(s);  
			}  
			br.close();
			
			
			// imprime o codigo resultante  
			return new String(newData);
		}
		catch(Exception e) {
			return null;
		}
	}
	
	
	/** 
	 * Fun��o respons�vel pelo tratamento da String de retorno da integra��o com o Moodle,
	 * Essa fun��o ir� separar os campos e seus determinados valores
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @throws Exception 
	 * @return Cole��o de objetos do tipo RetornoNotaAEIntegracaoMoodle que representar�o as informa��es da integra��o j� organizadas
	 */
	public Collection<RetornoNotaAEIntegracaoMoodle> lerRegistrosRetornoNotaAEIntegracaoMoodle(String retorno){
		try {
			ArrayList<RetornoNotaAEIntegracaoMoodle> itens = new ArrayList<RetornoNotaAEIntegracaoMoodle>();
			
			char caracterLido = retorno.charAt(0);
			String valor = "";
			
			String matricula = "";
			String codigoIntegracaoDisciplina = "";
			Double notaAe = 0.0;
			
			
			int contadorCampos = 0;
			
			for (int i = 0; caracterLido != '^'; i++) {
				
				caracterLido = retorno.charAt(i);
				
				if(caracterLido != '#'){
					if(caracterLido != '@'){
						valor += (char) caracterLido;
					} else {
						
						if(contadorCampos > 0){
							 if(contadorCampos == 1){
		 						matricula = valor;
		 					} else if(contadorCampos == 2){
		 						codigoIntegracaoDisciplina = valor;
		 					} else if(contadorCampos == 3){
		 						if(valor == "") {
		 							notaAe = null;
		 						}
		 						else {
		 							notaAe = Double.parseDouble(valor);
		 						}
		 					} 
							
						}
						valor = "";
						contadorCampos++;
					}
					
				} else {
					if(contadorCampos == 3){
						if(valor == "") {
							notaAe = null;
						}
						else {
							notaAe = Double.parseDouble(valor);
						}
					}
					
					if (valor != "") {
		    			RetornoNotaAEIntegracaoMoodle objRetorno = new RetornoNotaAEIntegracaoMoodle(matricula, codigoIntegracaoDisciplina, notaAe);
		    			itens.add(objRetorno);
					}
					
					valor = "";
					contadorCampos = 0;
					
				}
				
			}
			if(contadorCampos == 3){
				if(valor == "") {
					notaAe = null;
				}
				else {
					valor = valor.substring(0, valor.length()-1);
					notaAe = Double.parseDouble(valor);
				}
			}
			
			if (valor != "") {
				RetornoNotaAEIntegracaoMoodle objRetorno = new RetornoNotaAEIntegracaoMoodle(matricula, codigoIntegracaoDisciplina, notaAe);
				itens.add(objRetorno);
			}
			
			valor = "";
			contadorCampos = 0;

			return itens;
		
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
