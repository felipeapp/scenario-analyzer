/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 09/07/2008
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean respons�vel pela busca geral de bolsas aux�lios
 * @author agostinho
 * @author Jean Guerethes
 */
@Component()  @Scope("session")
public class DefinirDiasAlimentacaoMBean extends SigaaAbstractController<BolsaAuxilio> {

	/** Par�metros da busca: n�vel de ensino. */
	private char nivel;
	
	/** Indica��o do filtro das pontua��es . */
	private HashMap<Integer, String> hashCores = new HashMap<Integer, String>();
	
	/** Lista de bolsas aux�lios . */
	private List<BolsaAuxilio> listaBolsaAuxilio;
	
	public DefinirDiasAlimentacaoMBean() {
	}

	/**
	 * M�todo respons�vel por instanciar o objeto de BolsaAuxilio.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> sigaa.war/sae/menu.jsp </li>
	 * 		  <li> sigaa.war/sae/DiasAlimentacao/view.jsp </li> </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String instanciar() throws DAOException {
		obj = new BolsaAuxilio();
		ConsultaBolsaAuxilioMBean mBean = getMBean("consultaBolsaAuxilioMBean");
		mBean.clear();
		return forward("/sae/busca_bolsa_auxilio.jsp");
	}
	
	/**
	 * Retorna Resid�ncias como SelectItem
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> N�o invocado por JSP </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllResidenciasCombo() throws DAOException{
		return getAll(ResidenciaUniversitaria.class, "id", "localizacao");
	}
	
	/**
	 * Inicia o cadastro de dias de alimenta��o que esse aluno ter� direito/acesso ao RU.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> sigaa.war/sae/busca_bolsa_auxilio.jsp </ul>
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void iniciarCadastroDiasAlimentacao(ActionEvent evt) throws ArqException {
		Integer idDiscente = (Integer) evt.getComponent().getAttributes().get("idDiscente");
		Integer idBolsa = (Integer) evt.getComponent().getAttributes().get("idBolsa");
		BolsaAuxilioDao buscaBolsaAuxilioDao = getDAO(BolsaAuxilioDao.class);
		
		Discente discente  = buscaBolsaAuxilioDao.findByPrimaryKey(idDiscente, Discente.class);
		BolsaAuxilio bolsa  = buscaBolsaAuxilioDao.findByPrimaryKey(idBolsa, BolsaAuxilio.class);
		
		DiasAlimentacaoMBean diasAlimentacaoMBean = getMBean("diasAlimentacaoMBean");
		diasAlimentacaoMBean.iniciarCadastroDiasAlimentacao(discente, bolsa);
	}
	
	/**
	 * Retorna para o menu principal do SAE
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> sigaa.war/sae/busca_bolsa_auxilio.jsp </ul>
	 */
	public String cancelar() {
		return forward ("/sae/menu.jsf");
	}
	
	/**
	 * Realiza a busca de Bolsa Aux�lio de acordo com os crit�rios
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> sigaa.war/sae/busca_bolsa_auxilio.jsp </ul>
	 * @throws Exception 
	 */
	public String buscar() throws Exception {
		ConsultaBolsaAuxilioMBean mBean = getMBean("consultaBolsaAuxilioMBean");
		listaBolsaAuxilio = mBean.buscarBolsaAuxilio();
		if (listaBolsaAuxilio.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}

		return null;
	}
	
	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public List<BolsaAuxilio> getListaBolsaAuxilio() {
		return listaBolsaAuxilio;
	}

	public void setListaBolsaAuxilio(List<BolsaAuxilio> listaBolsaAuxilio) {
		this.listaBolsaAuxilio = listaBolsaAuxilio;
	}

	public HashMap<Integer, String> getHashCores() {
		return hashCores;
	}

	public void setHashCores(HashMap<Integer, String> hashCores) {
		this.hashCores = hashCores;
	}

}