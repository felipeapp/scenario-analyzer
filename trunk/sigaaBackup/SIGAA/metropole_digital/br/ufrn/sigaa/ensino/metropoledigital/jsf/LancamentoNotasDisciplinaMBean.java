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
 * Classe responsável por gerenciar o lançamento de notas das disciplinas
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */
@Scope("session")
@Component("lancamentoNotasDisciplina")
public class LancamentoNotasDisciplinaMBean extends SigaaAbstractController{
	
	//URLS DE NAVEGAÇÃO
	/** Variável estática com o link para a página inicial do portal */
	public final static String JSP_PRINCIPAL = "/metropole_digital/principal.jsf";
	
	/** Variável estática com o link para a página de lançamento de notas por disciplina*/
	public final static String JSP_LANCAR_NOTAS = "/metropole_digital/lancar_notas_disciplina/form.jsf";
	
	/** Variável estática com o link para o relatório de notas por disciplina*/
	public final static String JSP_VISUALIZAR_NOTAS = "/metropole_digital/lancar_notas_disciplina/view.jsf";
	
	/** Variável estática com o link para a página com as listas das disciplinas vinculadas a turma*/
	public final static String JSP_DISCIPLINAS_TURMA = "/metropole_digital/lancar_notas_disciplina/disciplinas_turma.jsf";
	
	/** Entidade que armazena a turma de entrada selecionada na página inicial do portal na qual as notas serão informadas **/
	private TurmaEntradaTecnico turmaEntrada;
	
	/** Entidade que armazena a disciplina selecionada para as notas serem informadas **/
	private Turma disciplina;
	
	/** Entidade que armazena uma coleção de Matriculas que corresponde aos discentes matriculados em uma determinada disciplina **/
	private List<MatriculaComponente> listaMatriculaDiscentes; 
	
	/** Entidade que armazena uma coleção de disciplinas que pertencem a turma selecionada**/
	private List<Turma> listaDisciplinas;
	
	/** Entidade que armazena uma coleção de matrículas em uma disciplina**/
	private List<MatriculaTurma> matriculasTurma = new ArrayList<MatriculaTurma>();
	
	/** Variável responsável por armazenar a String de retorno das notas AE do MOODLE **/
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
	 * Redireciona para a página responsável por listar as disciplinas vinculadas a turma selecionada.
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/lancar_notas_disciplina/disciplinas_turma.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws DAOException 
	 */
	public String listarDisciplinasTurma() throws DAOException{
		LancamentoNotasDisciplinaDao dao = getDAO(LancamentoNotasDisciplinaDao.class);
		try {
			turmaEntrada = dao.findByPrimaryKey((Integer)getParameterInt("id"), TurmaEntradaTecnico.class);
			listaDisciplinas = dao.findDisciplinasByEspecializacao(turmaEntrada.getEspecializacao().getId());	
			if (listaDisciplinas.isEmpty()) {
				addMessage("Esta turma não possui disciplinas abertas para o lançamento de notas.", TipoMensagemUFRN.ERROR);
				return redirect(JSP_PRINCIPAL);
			}
		} finally {
			dao.close();
		}
		return forward(JSP_DISCIPLINAS_TURMA);
	}
	
	
	/**
	 * Redireciona para a página de lançamento de Notas
	 * 
 	 *	Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/lancar_notas_disciplina/disciplinas_turma.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
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
	 * Retorna para a página de seleção das disciplinas
	 * 
	 * Método é chamado nas seguintes JSP's:
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
	 * Salva as notas informadas na tela de lançamento de notas
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return JSP na qual a página será redirecionada
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
			addMessage("Operação Realizada com Sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}					
		return forward(JSP_LANCAR_NOTAS);
	}
	
	
	/**
	 * Redireciona para a página de cadastro das notas  
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return JSP na qual a página será redirecionada
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


	/*FUNÇÕES RESPONSÁVEL PELA INTEGRAÇÃO ENTRE SIGAA E MOODLE*/
	/** 
	 * Função que carrega a integração das notas AE do moodle
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return JSP na qual a página será redirecionada
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
					addMessage("Sincronização efetuada com sucesso. ", TipoMensagemUFRN.INFORMATION);
				}
				else {
					addMessage("Todas as notas já haviam sido sincronizadas." , TipoMensagemUFRN.INFORMATION);
				}
			} 
			else {
				addMessage("Ocorreu um problema na leitura das informações junto ao Moodle. As notas não foram sincronizadas." , TipoMensagemUFRN.ERROR);
			}
			
			return forward(JSP_LANCAR_NOTAS);
		}
		finally {
			dao.close();
		}
	}


	/** 
	 * Função reponsável por disparar a requisição de conexão HTTP a uma página externa.
	 * Nessa função serão criados e informados os parâmetros e a URL a ser requisitada
	 * 
	 * Método é chamado nas seguintes JSP's:
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
	 * Função responsável por realizar a requisição de conexão HTTP a uma página externa
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param urlString: URL da página externa a ser requisitada; parameters: Parâmetros passados na requisição via método GET
	 * @throws Exception 
	 * @return String de retorno da integração com Moodle
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
			    // o primeiro é ?, depois são &  
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
			
			// abre a conexão pra input  
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
	 * Função responsável pelo tratamento da String de retorno da integração com o Moodle,
	 * Essa função irá separar os campos e seus determinados valores
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @throws Exception 
	 * @return Coleção de objetos do tipo RetornoNotaAEIntegracaoMoodle que representarão as informações da integração já organizadas
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
