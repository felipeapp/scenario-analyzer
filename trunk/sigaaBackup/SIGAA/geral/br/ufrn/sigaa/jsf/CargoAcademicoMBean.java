/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;

@Component("cargoAcademico") @Scope("request")
public class CargoAcademicoMBean extends AbstractControllerCadastro<CargoAcademico> {
	
	public CargoAcademicoMBean() {
		clear();
	}

	public void clear() {
		obj = new CargoAcademico();
		setLabelCombo("descricao");
	}
		
	/**
	 * retorna uma lista com o cargo coordenador e vice-coordenador
	 * @return
	 */
	public Collection<CargoAcademico> getCoordenadorEVice(){
		CargoAcademico coord = new CargoAcademico( CargoAcademico.COORDENACAO, "Coordenação" );
		CargoAcademico vice = new CargoAcademico( CargoAcademico.VICE_COORDENACAO, "Vice-Coordenação" );
		Collection<CargoAcademico> cargos = new ArrayList<CargoAcademico>();
		cargos.add(coord);
		cargos.add(vice);
		return cargos;
	}
	
	public Collection<SelectItem> getCoordenadorEViceCombo(){
		return toSelectItems( getCoordenadorEVice(), "id", "descricao" );
	}
	
	/**
	 * Para após a inserção o usuário ser redirecionado para a tela de listagem.
	 */
	@Override
	public String forwardCadastrar() {
		return "/administracao/cadastro/CargoAcademico/lista.jsf";
	}
	
	/**
	 * Para quando não foi possível remover pelo fato do mesmo está associado a outro registro, o usuário será redirecionado
	 * para a tela da listagem.
	 */
	@Override
	public String getFormPage() {
		return super.getFormPage();
	}
	
	/**
	 * Verifica se o objeto já foi removido, para evitar o nullPointer
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, CargoAcademico.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		super.remover();

		if (hasErrors()) {
			return forward(getListPage());
		}
		return null;
	}

	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
}