/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 14/12/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.PessoaInscricao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * MBean responsável pelo controle de operações sobre PessoaInscricao
 * 
 * @author Ricardo Wendell
 *
 */
@Component("pessoaInscricao")
@Scope("session")
public class PessoaInscricaoMBean extends SigaaAbstractController<PessoaInscricao> {

	// Lista dos municípios da UF atual
	private Collection<SelectItem> municipiosNaturalidade = new ArrayList<SelectItem>(0);
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	// Utilizado para a renderização de campos estado e município para outros países
	private boolean brasil = true;
	
	public PessoaInscricaoMBean() {
		initObj();
	}

	public void initObj() {
		obj = new PessoaInscricao();
		brasil = true;
		obj.clear();
	}
	
	public void iniciarInscricao() throws DAOException {
		initObj();
		popularMunicipios();
	}
	
	/**
	 * Popular campos de municípios que serão utilizados no formulário
	 * 
	 * @throws DAOException
	 */
	protected void popularMunicipios() throws DAOException {
		// Popular municípios para campo de naturalidade
		int uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getUnidadeFederativa() != null && obj.getUnidadeFederativa().getId() > 0)
			uf = obj.getUnidadeFederativa().getId();
		carregarMunicipiosNaturalidade(uf);	
		
		//Popular municípios para campo de endereço
		uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getEnderecoResidencial() != null 
				&& obj.getEnderecoResidencial().getUnidadeFederativa() != null 
				&& obj.getEnderecoResidencial().getUnidadeFederativa().getId() > 0) {
			uf = obj.getEnderecoResidencial().getUnidadeFederativa().getId();
		}
		carregarMunicipiosEndereco(uf);
		
		if (obj.getIdentidade() != null && obj.getIdentidade().getUnidadeFederativa() != null &&
			obj.getIdentidade().getUnidadeFederativa().getId() == 0) {
			obj.getIdentidade().getUnidadeFederativa().setId(uf);
		}

		if (obj.getTituloEleitor() != null && obj.getTituloEleitor().getUnidadeFederativa() != null &&
			obj.getTituloEleitor().getUnidadeFederativa().getId() == 0) {
			obj.getTituloEleitor().getUnidadeFederativa().setId(uf);
		} 
	}
	
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			if (selectId.toLowerCase().contains("natur")) {
				carregarMunicipiosNaturalidade(ufId);
			} else if (selectId.toLowerCase().contains("end")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}
	
	public void carregarMunicipiosNaturalidade(Integer idUf) throws DAOException {
		if ( idUf == null ) {
			idUf = obj.getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosNaturalidade = new ArrayList<SelectItem>(0);
		if (!ValidatorUtil.isEmpty(uf)) {
			municipiosNaturalidade.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
			municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));
			obj.setUnidadeFederativa(new UnidadeFederativa());
			obj.getUnidadeFederativa().setId(uf.getId());
		}
	}

	public void carregarMunicipiosEndereco() throws DAOException {
		carregarMunicipiosEndereco(null);
	}
	
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		if ( idUf == null ) {
			idUf = obj.getEnderecoResidencial().getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		if (!ValidatorUtil.isEmpty(uf)) {
			municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
			municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
			obj.getEnderecoResidencial().setUnidadeFederativa(new UnidadeFederativa());
			obj.getEnderecoResidencial().getUnidadeFederativa().setId(uf.getId());
		}
		
	}

	public void alterarPais(ValueChangeEvent e) throws DAOException {
		Integer idPais = (Integer) e.getNewValue();
		brasil = (idPais == Pais.BRASIL);
		if (brasil && obj.getMunicipio() == null) {
			obj.setMunicipio(new Municipio());
			obj.setUnidadeFederativa( new UnidadeFederativa() );
		}
	}
	
	public Collection<SelectItem> getMunicipiosNaturalidade() {
		return this.municipiosNaturalidade;
	}

	public void setMunicipiosNaturalidade(
			Collection<SelectItem> municipiosNaturalidade) {
		this.municipiosNaturalidade = municipiosNaturalidade;
	}

	public Collection<SelectItem> getMunicipiosEndereco() {
		return this.municipiosEndereco;
	}

	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	public boolean isBrasil() {
		return this.brasil;
	}

	public void setBrasil(boolean brasil) {
		this.brasil = brasil;
	}	
	
}
