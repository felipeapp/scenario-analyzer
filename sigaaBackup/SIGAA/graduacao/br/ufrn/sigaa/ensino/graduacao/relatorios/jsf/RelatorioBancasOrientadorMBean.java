package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioBancasOrientadorDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
/**
 * MBean responsável por gerar relatório de bancas por orientador.
 * 
 * @author geyson
 */
@Component("relatorioBancasOrientador") @Scope("request")
public class RelatorioBancasOrientadorMBean extends AbstractRelatorioGraduacaoMBean {

	/**
	 * Lista de dados do relatório.
	 */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	private Servidor membroBanca;
	private Date dataInicio;
	private Date dataFim;

	
	private final String CONTEXTO ="/graduacao/relatorios/bancas_orientador/";
	private final String JSP_SELECIONA_BANCAS_ORIENTADOR = "seleciona_bancas_orientador";
	private final String JSP_REL_BANCAS_ORIENTADOR = "rel_bancas_orientador";
	
	/** Construtor padrão. */
	public RelatorioBancasOrientadorMBean(){
		setMembroBanca(new Servidor());
	}
	
	/**
	 * Redireciona para a página de preenchimento dos filtros de busca.
	 * JSP: sigga/graduacao/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioBancasOrientador() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO+JSP_SELECIONA_BANCAS_ORIENTADOR); 
	}
	
	
	/**
	 * Gera relatório passando como entrada o id do orientador. 
	 * JSP: /graduacao/relatorios/bancas_orientador/seleciona_bancas_orientador.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioBancasOrientador() throws DAOException{
		
		RelatorioBancasOrientadorDao dao = getDAO(RelatorioBancasOrientadorDao.class);
		validaCampos();
		
		if(!hasErrors()){
			lista = dao.relatorioBancasOrientador(getMembroBanca().getId(), getDataInicio(), getDataFim());
			if (lista.size() > 0) {
				return forward(JSP_REL_BANCAS_ORIENTADOR);
			}
			else
				addMensagemWarning("Não foi encontrado nenhum registro com o(s) parâmetro(s) informado(s).");
			return null;
		}
		return null;
	}
	
	/**
	 * Valida Campos de Busca.
	 * JSP: Não Invocado por JSP.
	 */
	public void validaCampos(){
		
		ValidatorUtil.validateRequired(getMembroBanca(), "Orientador", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data Inicial", erros);
		
		if(getDataFim() != null){
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Data Fim", erros);
			ValidatorUtil.validateRequired(dataFim, "Data Final", erros);
		}
		
		if(!erros.isEmpty()){
			addMensagens(erros);
		}
	}
	
	
	

	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public Servidor getMembroBanca() {
		return membroBanca;
	}

	public void setMembroBanca(Servidor membroBanca) {
		this.membroBanca = membroBanca;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}


	
	
	
	
	
	
}
