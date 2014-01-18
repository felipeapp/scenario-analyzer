/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 11/06/2007 
 * 
 */
package br.ufrn.sigaa.monitoria.jsf; 

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * MBean para realizar consulta sobre Docentes do projeto (Orientadores)
 * visualização detalhada do orientador do projeto
 * exclusão de orientador do projeto
 * emissão de certificado e declaração de monitor
 *
 * @author Ilueny
 */
@Component("consultarEquipeDocente")@Scope("session")
public class ConsultarEquipeDocenteMBean extends SigaaAbstractController<EquipeDocente> {


	private String tituloProjeto = null;
		
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	private Servidor servidor = new Servidor();

	private Collection<EquipeDocente> orientadores = new ArrayList<EquipeDocente>();

	/** campos para auxiliar na busca */
	private boolean checkBuscaProjeto;

	private boolean checkBuscaOrientador;

	private boolean checkBuscaAno;
	
	private boolean checkGerarRelatorio;
	
	private EquipeDocente orientador = new EquipeDocente();
	
	private int anoReferencia;

	public ConsultarEquipeDocenteMBean() {
		obj = new EquipeDocente();
		anoReferencia = CalendarUtils.getAnoAtual();
		clear();
	}

	/**Inicializa os parametro do controller.
	 *
	 * Não invocado por JSP
	 */
	private void clear(){
		orientadores = new ArrayList<EquipeDocente>();
		servidor = new Servidor();
		tituloProjeto = null;
		checkBuscaProjeto = false;
		checkBuscaOrientador= false;
	}
	
	/**
	 *
	 * Inicializa os parametros da busca e redirecionapara tela da lista.
	 *
	 * sigaa.war/monitoria/index.jsp
	 *
	 */
	public String iniciar(){
		clear();
		return forward( ConstantesNavegacaoMonitoria.ALTERAREQUIPEDOCENTE_LISTA );
	}
	
	/**
	 *
	 * Localiza projeto na tela de situação do projeto
	 *
	 * sigaa.war/monitoria/AlterarEquipeDocente/lista.jsp
	 *
	 */
	public void localizar() {

		if (orientadores != null) {
		    orientadores.clear();
		}
	    String tituloProj = null;
	    Integer idOrientador = null;
	    Integer anoProjeto = null;
	    if(checkBuscaProjeto){
	    	ValidatorUtil.validateRequired(tituloProjeto, "Título do Projeto", erros);
	    	tituloProj = tituloProjeto;
	    }
	    if(checkBuscaOrientador){
	    	ValidatorUtil.validateRequiredId(servidor.getId(), "Nome do Docente", erros);
	    	idOrientador = servidor.getId();
	    }
	    if(checkBuscaAno){
	    	ValidatorUtil.validateRequired(anoReferencia, "Ano", erros);
	    	anoProjeto = anoReferencia;
	    }
	    if(!checkBuscaProjeto && !checkBuscaOrientador && !checkBuscaAno)
	    	addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
	    if(hasErrors())
	    	return;
	    try {
		    
		    EquipeDocenteDao dao  = getDAO( EquipeDocenteDao.class );
		    orientadores = dao.filter(tituloProj, idOrientador, anoProjeto);
		} catch (DAOException e) {
		    notifyError(e);
		}
	}


	/**
	 * exibe os dados do orientador
	 * 
	 * sigaa.war/monitoria/AlterarEquipeDocente/lista.jsp
	 * 
	 * @return
	 * @throws DAOException
     */
	public String view() throws DAOException {
		Integer id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, EquipeDocente.class);
		obj.getOrientacoes().iterator();
		return forward(ConstantesNavegacaoMonitoria.CONSULTAREQUIPEDOCENTE_VIEW);
	}



	/**
	 * exibe o certificado de participação em projeto
	 * 
	 * Não foi encontrado jsp que chame o método. 
	 * 
	 * @return
	 */
	public String certificado() {
		return null;
	}

	
	/**
	 * exibe a declaração de participação em projeto de monitoria
	 * 
	 * Não foi encontrado jsp que chame o método.
	 * 
	 * @return
	 */
	public String declaracao() {		
		return null;
	}


	@Override
	public String getDirBase() {
		return "/monitoria/ConsultarEquipeDocente";
	}

	public boolean isCheckBuscaOrientador() {
		return checkBuscaOrientador;
	}

	public void setCheckBuscaOrientador(boolean checkBuscaOrientador) {
		this.checkBuscaOrientador = checkBuscaOrientador;
	}

	public boolean isCheckBuscaProjeto() {
		return checkBuscaProjeto;
	}

	public void setCheckBuscaProjeto(boolean checkBuscaProjeto) {
		this.checkBuscaProjeto = checkBuscaProjeto;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public boolean isCheckGerarRelatorio() {
		return checkGerarRelatorio;
	}

	public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
		this.checkGerarRelatorio = checkGerarRelatorio;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public int getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(int anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public EquipeDocente getOrientador() {
		return orientador;
	}

	public void setOrientador(EquipeDocente orientador) {
		this.orientador = orientador;
	}

	public Collection<EquipeDocente> getOrientadores() {
		return orientadores;
	}

	public void setOrientadores(Collection<EquipeDocente> orientadores) {
		this.orientadores = orientadores;
	}
	
	public String getTituloProjeto() {
		return tituloProjeto;
	}

	public void setTituloProjeto(String tituloProjeto) {
		this.tituloProjeto = tituloProjeto;
	}
	
}