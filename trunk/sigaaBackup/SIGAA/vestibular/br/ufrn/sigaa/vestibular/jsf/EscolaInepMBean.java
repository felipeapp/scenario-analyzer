/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/03/2009
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.EscolaInepDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.vestibular.dominio.EscolaInep;

/** Controller responsável pelas operações com escolas. 
 * @author Édipo Elder F. Melo
 *
 */
@Component("escolaInep")
@Scope("request")
public class EscolaInepMBean extends SigaaAbstractController<EscolaInep> {
	
	/** Lista de municípios da UF setada. */
	private Collection<SelectItem> municipiosCombo;
	
	/** Lista de EscolasInep */
	private Collection<EscolaInep> escolas;
	
	/** Construtor padrão. */
	public EscolaInepMBean() {
		this.obj = new EscolaInep();
		municipiosCombo = new ArrayList<SelectItem>();
		escolas = new ArrayList<EscolaInep>();
	}

	/** Retorna uma lista de SelectItem de Municípios da UF setada no objeto deste controller.
	 * @return Lista de municípios da UF setada.
	 */
	public Collection<SelectItem> getMunicipiosCombo() {
		return this.municipiosCombo;
	}
	
	/** Retorna uma lista de EscolasInep do Município e UF setados no objeto deste controller.
	 * @return
	 */
	public Collection<EscolaInep> getEscolas(){
		return this.escolas;
	}
	
	/** Listener que atualiza a lista de municípios quando o valor da UF é alterado.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/vestibular/EscolaInep/lista.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException 
	 */
	public void unidadeFederativaListener(ValueChangeEvent evt) throws DAOException {
		Integer id = (Integer) evt.getNewValue();
		obj.getEndereco().getUnidadeFederativa().setId(id.intValue());
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		municipiosCombo = toSelectItems(
				dao.findMunicipiosByUF(
						obj.getEndereco().getUnidadeFederativa().getId()),
						"id", "nome");
		this.escolas = new ArrayList<EscolaInep>();
	}
	
	/** Listener que atualiza a lista de escolas quando o valor do município é alterado.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/vestibular/EscolaInep/lista.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException 
	 */
	public void municipioListener(ValueChangeEvent evt) throws DAOException {
		Integer id = (Integer) evt.getNewValue();
		obj.getEndereco().getMunicipio().setId(id.intValue());
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		escolas = dao.findByMunicipioUF(
				obj.getEndereco().getMunicipio().getId(),
						obj.getEndereco().getUnidadeFederativa().getId());
	}
	
	/** Checa os papeis: VESTIBULAR
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}
	
	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		initObj();
		return super.listar();
	}
	
	/** Inicializa os atributos do controller */
	private void initObj (){
		this.obj = new EscolaInep();
		this.escolas = new ArrayList<EscolaInep>();
		this.municipiosCombo = new ArrayList<SelectItem>();		
	}
}
