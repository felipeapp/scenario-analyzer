package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino_rede.dao.SelecionaCampusIesDao;

@SuppressWarnings("serial")
@Component("selecionaCampusRedeMBean") @Scope("session")
public class SelecionaCampusIesMBean extends EnsinoRedeAbstractController<CampusIes> {
	
	private static final String LISTA_CAMPUS = "/ensino_rede/busca_geral/campus_ies/lista_campus.jsp";
	
	private SelecionaCampus requisitor;
	
	private ParametrosSelecionaCampus parametros;
	private ValoresSelecionaCampus valores;
	
	public SelecionaCampusIesMBean() {
		clear();
	}

	private void clear() {
		obj = new CampusIes();
		obj.setInstituicao(new InstituicoesEnsino());
		parametros = new ParametrosSelecionaCampus();
		valores = new ValoresSelecionaCampus();
		resultadosBusca = new ArrayList<CampusIes>();
	}
	
	public String iniciar() {
		clear();
		
		return forward(LISTA_CAMPUS);
	}

	public void buscar(ActionEvent evt) throws DAOException {
		SelecionaCampusIesDao dao = getDAO(SelecionaCampusIesDao.class);
		
		List<CampusIes> resultado = dao.buscar(getProgramaRede(), parametros, valores);
		
		setResultadosBusca(resultado);
	}
	
	public void escolheCampus(ActionEvent evt) throws ArqException, NegocioException {

		Integer id = (Integer) evt.getComponent().getAttributes().get("idCampus");
		escolheCampus(id);
		
	}
	
	public Collection<SelectItem> getIfesCombo() throws DAOException {
		SelecionaCampusIesDao dao = getDAO(SelecionaCampusIesDao.class);
		List<InstituicoesEnsino> ifes = dao.findIesByPrograma(getProgramaRede());
		
		return toSelectItems(ifes, "id", "sigla");
	}
	
	private void escolheCampus(Integer id) throws ArqException, NegocioException {
		SelecionaCampusIesDao dao = getDAO(SelecionaCampusIesDao.class);
		obj = dao.findByPrimaryKey(id, CampusIes.class);
		
		requisitor.setCampus(obj);
		requisitor.selecionaCampus();
		
	}

	public SelecionaCampus getRequisitor() {
		return requisitor;
	}

	public void setRequisitor(SelecionaCampus requisitor) {
		this.requisitor = requisitor;
	}

	public ParametrosSelecionaCampus getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosSelecionaCampus parametros) {
		this.parametros = parametros;
	}

	public ValoresSelecionaCampus getValores() {
		return valores;
	}

	public void setValores(ValoresSelecionaCampus valores) {
		this.valores = valores;
	}

}
