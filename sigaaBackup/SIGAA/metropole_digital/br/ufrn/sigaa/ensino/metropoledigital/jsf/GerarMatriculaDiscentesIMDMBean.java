package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.metropoledigital.dao.GerarMatriculaDiscentesIMDDao;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;

/***
 * 
 * Bean responsável por gerar a matrícula dos discentes do IMD que não possuem matrícula
 * 
 * @author Rafael Barros
 *
 */

@Component("gerarMatriculaDiscentesIMD")
@Scope("request")
public class GerarMatriculaDiscentesIMDMBean extends SigaaAbstractController {

	/**ID do Objeto que representa o processo seletivo a ser filtrado da listagem de discentes sem matrícula*/
	private Integer idProcessoSeletivo;
	
	/**ID do Objeto que representa a opção polo grupo a ser filtrada da listagem de discentes sem matrícula*/
	private Integer idOpcao;
	
	/**Coleção de itens dos processos seletivos que serão utilizados em um combobox*/
	private Collection<SelectItem> processosCombo;
	
	/**Coleção de itens das opções pólo grupo que serão utilizadas em um combobox*/
	private Collection<SelectItem> opcoesCombo;
	
	/**Coleção de discentes sem matrícula que serão listados na tela de listagem */
	private Collection<DiscenteTecnico> listagemDiscentes;
	
	/**Coleção de discentes sem matrícula que serão listados na tela de listagem */
	private Collection<DiscenteTecnico> discentesSelecionados;
	
	/**Construtor padrão do MBean*/
	public GerarMatriculaDiscentesIMDMBean(){
		this.idProcessoSeletivo = null;
		this.idOpcao = null;
		this.processosCombo = new ArrayList<SelectItem>();
		this.opcoesCombo = new ArrayList<SelectItem>();
		this.listagemDiscentes = new ArrayList<DiscenteTecnico>();
		this.discentesSelecionados = new ArrayList<DiscenteTecnico>();
	}

	public Integer getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	public void setIdProcessoSeletivo(Integer idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	public Integer getIdOpcao() {
		return idOpcao;
	}

	public void setIdOpcao(Integer idOpcao) {
		this.idOpcao = idOpcao;
	}

	/** Método responsável por retornar o atributo processosCombo, caso o atributo esteja vazio, o método efetua o preenchimento da lista
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/gerar_matricula/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return processosCombo
	 * @throws DAOException
	 */
	public Collection<SelectItem> getProcessosCombo() throws DAOException {
		if(processosCombo.isEmpty()){
			processosCombo = toSelectItems(getGenericDAO().findAll(ProcessoSeletivoTecnico.class), "id", "nome");
		}
		return processosCombo;
	}

	public void setProcessosCombo(Collection<SelectItem> processosCombo) {
		this.processosCombo = processosCombo;
	}

	/** Método responsável por retornar o atributo opcoesCombo, caso o atributo esteja vazio, o método efetua o preenchimento da lista
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/gerar_matricula/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return opcoesCombo
	 * @throws DAOException
	 */
	public Collection<SelectItem> getOpcoesCombo() throws DAOException {
		if(opcoesCombo.isEmpty()){
			opcoesCombo = toSelectItems(getGenericDAO().findAll(OpcaoPoloGrupo.class), "id", "descricao");
		}
		return opcoesCombo;
	}

	public void setOpcoesCombo(Collection<SelectItem> opcoesCombo) {
		this.opcoesCombo = opcoesCombo;
	}

	public Collection<DiscenteTecnico> getListagemDiscentes() {
		return listagemDiscentes;
	}

	public void setListagemDiscentes(Collection<DiscenteTecnico> listagemDiscentes) {
		this.listagemDiscentes = listagemDiscentes;
	}
	
	public Collection<DiscenteTecnico> getDiscentesSelecionados() {
		return discentesSelecionados;
	}

	public void setDiscentesSelecionados(Collection<DiscenteTecnico> discentesSelecionados) {
		this.discentesSelecionados = discentesSelecionados;
	}

	public String buscarDiscentes() throws DAOException{
		
		GerarMatriculaDiscentesIMDDao dao = new GerarMatriculaDiscentesIMDDao();
		
		try {
			
			if(idProcessoSeletivo == null || idProcessoSeletivo == 0) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Processo Seletivo");
			} else {
				listagemDiscentes = dao.findDiscentesSemMatricula(idProcessoSeletivo, idOpcao);
				if(listagemDiscentes.isEmpty()){
					addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
				}
			}
			
		} finally {
			dao.close();
		}
		
		return forward("/metropole_digital/gerar_matricula/lista.jsf");
	}
	
	public String redirecionarGerarMatricula(){
		return forward("/metropole_digital/gerar_matricula/lista.jsf");
	}
	
	
	/**
	 * Responsável por submeter os discentes selecionados que a matrícula será gerada
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/gerar_matricula/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws 
	 */
	public String submeterDiscentes() throws DAOException {
		
		if (hasOnlyErrors()){
			return null;
		}
		
		discentesSelecionados = new ArrayList<DiscenteTecnico>();
		for (DiscenteTecnico d : listagemDiscentes) {
			if ( d.getDiscente().isSelecionado()  ){
				discentesSelecionados.add(d);
			}
		}
		
		if ( discentesSelecionados.isEmpty() ) {
			addMensagemErro("É necessário informar pelo menos um discente.");
			return null;
		} 	
	
		return redirecionarListagemSelecionados();
	}
	
	/**
	 * Responsável por submeter os discentes selecionados que a matrícula será gerada
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/gerar_matricula/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws 
	 */
	public String gerarMatriculaDiscentesSelecionados() throws ArqException{
		
		try {
			if(discentesSelecionados.isEmpty()) {
				addMensagemErro("É necessário informar pelo menos um discente.");
				return null;
			} else {
				ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
				
				for(DiscenteTecnico discenteTec: discentesSelecionados){
					processadorDiscente.gerarMatricula(discenteTec.getDiscente(), getGenericDAO());
					
					System.out.println("MAT: " + discenteTec.getDiscente().getMatricula() + " - NOME: " + discenteTec.getPessoa().getNome());
				}
				
				idProcessoSeletivo = 0;
				idOpcao = 0;
				listagemDiscentes = new ArrayList<DiscenteTecnico>();
				discentesSelecionados = new ArrayList<DiscenteTecnico>();
				
				addMessage("Matrícula(s) gerada(s) com sucesso.", TipoMensagemUFRN.INFORMATION);
				return forward("/metropole_digital/gerar_matricula/lista.jsf");
			}
			
			
		} finally{
			
		}
		
	}
	
	/**
	 * Responsável por submeter os discentes selecionados que a matrícula será gerada
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/gerar_matricula/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws 
	 */
	public String redirecionarListagemSelecionados(){
		return forward("/metropole_digital/gerar_matricula/lista_selecionados.jsf");
	}
	
}
