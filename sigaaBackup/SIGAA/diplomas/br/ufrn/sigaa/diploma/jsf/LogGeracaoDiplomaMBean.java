/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/08/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.LogGeracaoDiploma;

/** Controller responsável pela auditoria na geração dos arquivos de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("logGeracaoDiploma")
@Scope("request")
public class LogGeracaoDiplomaMBean extends
		SigaaAbstractController<LogGeracaoDiploma> {

	/** Indica se a busca deverá filtrar pela matrícula do discente. */
	private boolean buscaMatricula;
	/** Indica se a busca deverá filtrar pelo nome do discente. */
	private boolean buscaNomeDiscente;
	/** Indica se a busca deverá filtrar pelo número do registro do diploma. */
	private boolean buscaRegistro;
	/** Indica se a busca deverá filtrar pelo nome do usuário. */
	private boolean buscaUsuario;
	/** Indica se a busca deverá filtrar pela data de geração do diploma. */
	private boolean buscaData;
	
	/** Matrícula do discente usado na busca. */
	private int matricula;
	/** Nome do discente usado na busca.*/
	private String nomeDiscente;
	/** Número do registro do diploma usado na busca.*/
	private int numeroRegistro;
	/** Nome do usuário que gerou o diploma. */
	private String nomeUsuario;
	/** Data que o diploma foi gerado.*/
	private Date data;
	/** Nível de ensino específico da bsuca. */
	private char nivelEnsinoEspecifico;
	
	/** Lista de logs de geração de diploma encontrados. */
	private Collection<LogGeracaoDiploma> listaLogGeracaoDiplomas;

	/** Construtor padrão. */
	public LogGeracaoDiplomaMBean() {
	}

	/**
	 * Inicia a auditoria de impressão de diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/diplomas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String auditarImpressao() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO);
		initController();
		return forward("/diplomas/log_registro_diploma/log_impressao.jsp");
	}

	/**
	 * Busca por logs de geração de diplomas de acordo com os atributos setados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/log_registro_diploma/log_impressao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String buscarImpressao() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO);
		int matricula = 0;
		String nomeDiscente = null;
		int numeroRegistro = 0;
		String nomeUsuario = null;
		Date data = null;
		if (buscaData) {
			data = this.data;
		}
		if (buscaMatricula) {
			matricula = this.matricula;
		}
		if (buscaNomeDiscente) {
			nomeDiscente = this.nomeDiscente;
		}
		if (buscaRegistro) {
			numeroRegistro = this.numeroRegistro;
		}
		if (buscaUsuario) {
			nomeUsuario = this.nomeUsuario;
		}
		if (getNiveisHabilitados().length == 1){
			nivelEnsinoEspecifico = getNiveisHabilitados()[0];
		} else if (nivelEnsinoEspecifico == '0') {
			addMensagemErro("Selecione um Nível de Ensino válido.");
		}
		if (matricula == 0
				&& (nomeDiscente == null || nomeDiscente.isEmpty())
				&& numeroRegistro == 0
				&& (nomeUsuario == null || nomeUsuario.isEmpty())
				&& data == null) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		} 
		if (hasErrors()) return null;
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
		listaLogGeracaoDiplomas = dao.findLogGeracaoDiploma(matricula, nomeDiscente, numeroRegistro, nomeUsuario, data, nivelEnsinoEspecifico);
		if (listaLogGeracaoDiplomas == null || listaLogGeracaoDiplomas.isEmpty()) 
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return null;
	}
	
	/**
	 * Inicializa os atributos do controller.
	 */
	private void initController() {
		this.obj = new LogGeracaoDiploma();
	}

	/** Indica se a busca deverá filtrar pela matricula do discente. 
	 * @return
	 */
	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	/** Seta se a busca deverá filtrar pela matricula do discente. 
	 * @param buscaMatricula
	 */
	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	/** Indica se a busca deverá filtrar pelo nome do discente. 
	 * @return
	 */
	public boolean isBuscaNomeDiscente() {
		return buscaNomeDiscente;
	}

	/** Seta se a busca deverá filtrar pelo nome do discente. 
	 * @param buscaNomeDiscente
	 */
	public void setBuscaNomeDiscente(boolean buscaNomeDiscente) {
		this.buscaNomeDiscente = buscaNomeDiscente;
	}

	/** Indica se a busca deverá filtrar pelo número do registro do diploma. 
	 * @return
	 */
	public boolean isBuscaRegistro() {
		return buscaRegistro;
	}

	/** Seta se a busca deverá filtrar pelo número do registro do diploma. 
	 * @param buscaRegistro
	 */
	public void setBuscaRegistro(boolean buscaRegistro) {
		this.buscaRegistro = buscaRegistro;
	}

	/** Indica se a busca deverá filtrar pelo nome do usuário. 
	 * @return
	 */
	public boolean isBuscaUsuario() {
		return buscaUsuario;
	}

	/** Seta se a busca deverá filtrar pelo nome do usuário. 
	 * @param buscaUsuario
	 */
	public void setBuscaUsuario(boolean buscaUsuario) {
		this.buscaUsuario = buscaUsuario;
	}

	/** Indica se a busca deverá filtrar pela data de geração do diploma. 
	 * @return
	 */
	public boolean isBuscaData() {
		return buscaData;
	}

	/** Seta se a busca deverá filtrar pela data de geração do diploma. 
	 * @param buscaData
	 */
	public void setBuscaData(boolean buscaData) {
		this.buscaData = buscaData;
	}

	/** Retorna a matrícula do discente usado na busca. 
	 * @return Matrícula do discente usado na busca. 
	 */
	public int getMatricula() {
		return matricula;
	}

	/** Seta a matrícula do discente usado na busca. 
	 * @param matricula Matrícula do discente usado na busca. 
	 */
	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}

	/** Retorna o nome do discente usado na busca.
	 * @return Nome do discente usado na busca.
	 */
	public String getNomeDiscente() {
		return nomeDiscente;
	}

	/** Seta o nome do discente usado na busca.
	 * @param nomeDiscente Nome do discente usado na busca.
	 */
	public void setNomeDiscente(String nomeDiscente) {
		this.nomeDiscente = nomeDiscente;
	}

	/** Retorna o número do registro do diploma usado na busca.
	 * @return Número do registro do diploma usado na busca.
	 */
	public int getNumeroRegistro() {
		return numeroRegistro;
	}

	/** Seta o número do registro do diploma usado na busca.
	 * @param numeroRegistro Número do registro do diploma usado na busca.
	 */
	public void setNumeroRegistro(int numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	/** Retorna o nome do usuário que gerou o diploma.
	 * @return Nome do usuário que gerou o diploma. 
	 */
	public String getNomeUsuario() {
		return nomeUsuario;
	}

	/** Seta o nome do usuário que gerou o diploma. 
	 * @param nomeUsuario Nome do usuário que gerou o diploma. 
	 */
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	/** Retorna a data que o diploma foi gerado.
	 * @return Data que o diploma foi gerado.
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data que o diploma foi gerado.
	 * @param data Data que o diploma foi gerado.
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna a lista de logs de geração de diploma encontrados. 
	 * @return Lista de logs de geração de diploma encontrados. 
	 */
	public Collection<LogGeracaoDiploma> getListaLogGeracaoDiplomas() {
		return listaLogGeracaoDiplomas;
	}

	/** Seta a lista de logs de geração de diploma encontrados. 
	 * @param listaLogGeracaoDiplomas Lista de logs de geração de diploma encontrados. 
	 */
	public void setListaLogGeracaoDiplomas(
			Collection<LogGeracaoDiploma> listaLogGeracaoDiplomas) {
		this.listaLogGeracaoDiplomas = listaLogGeracaoDiplomas;
	}

	public char getNivelEnsinoEspecifico() {
		return nivelEnsinoEspecifico;
	}

	public void setNivelEnsinoEspecifico(char nivelEnsinoEspecifico) {
		this.nivelEnsinoEspecifico = nivelEnsinoEspecifico;
	}
	
}
