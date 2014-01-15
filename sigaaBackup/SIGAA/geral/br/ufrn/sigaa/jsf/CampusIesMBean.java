/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/12/2009
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/** Controller responsável pelas operações de cadastros de {@link CampusIes campus}.<br>
 * Gerado pelo CrudBuilder.
 * @author Édipo Elder F. Melo
 */
@Component("campusIes")
@Scope("request")
public class CampusIesMBean extends SigaaAbstractController<CampusIes> {

	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	/** Construtor padrão. 
	 * @throws DAOException */
	public CampusIesMBean() throws DAOException {
		initObj();
	}

	/**
     * Inicializa objetos necessários e define valores-padrão para alguns deles
	 * @throws DAOException 
     *  
     */
	private void initObj() throws DAOException {
		if(obj == null || obj.getId() == 0){
			obj = new CampusIes();
			obj.setTipoCampusUnidade(null);
		}
		if (obj.getEndereco() == null || obj.getEndereco().getId() == 0) {
            obj.setEndereco(new Endereco());
            obj.getEndereco().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
            int cepPadrao =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.CEP_PADRAO );
            obj.getEndereco().setCep(cepPadrao + "");
            obj.getEndereco().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
            obj.getEndereco().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
        }
        
        if (obj.getEndereco() != null && obj.getEndereco().getUnidadeFederativa() == null) {
            obj.getEndereco().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
        }
        
        if (obj.getEndereco() != null && obj.getEndereco().getMunicipio() == null) {
            obj.getEndereco().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
        }
        //Popular municípios para campo de endereço
        int uf = UnidadeFederativa.ID_UF_PADRAO;
        if (obj.getEndereco() != null 
                && obj.getEndereco().getUnidadeFederativa() != null 
                && obj.getEndereco().getUnidadeFederativa().getId() > 0) {
            uf = obj.getEndereco().getUnidadeFederativa().getId();
        }
        carregarMunicipiosEndereco(uf);
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/camposIes/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		return super.preCadastrar();
	}
	
	/**
	 * <br>
	 * JSP: Não invocado por JSP.
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		initObj();
	}
	
	/** Método invocado após o cadastro.
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/** Retorna o link para o formulário de cadastro.
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/geral/campusIes/form.jsf";
	}

	/** Retorna o link para a lista de campus.
	 * <br>
	 * JSP: Não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/geral/campusIes/lista.jsf";
	}
	
	/** Retorna uma coleção de SelectItem de campus da instituição.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCampusCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findByExactField(CampusIes.class,"instituicao.id",InstituicoesEnsino.UFRN), "id", "nome");
	}

	/**
     * Decide como carregar as opções de municípios no formulário de acordo com os métodos
     * presentes aqui.
     * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/DiscenteInfantil/form.jsp</li>
	 *	</ul>
     * @param e
     * @throws DAOException
     */
    public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
        String selectId = e.getComponent().getId();
        if (selectId != null && e.getNewValue() != null) {
            Integer ufId = (Integer) e.getNewValue();
            if (selectId.toLowerCase().contains("end")) {
                carregarMunicipiosEndereco(ufId);
            }
        }
    }
    
    /**
     * Carrega as opções de municípios no formulário de endereço de acordo com a
     * unidade federativa selecionada.
     * <br>
     * JSP: Não invocado por JSP.
     * @param idUf
     * @throws DAOException
     */
    public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
        if ( idUf == null ) {
            idUf = obj.getEndereco().getUnidadeFederativa().getId();
        }

        MunicipioDao dao = getDAO(MunicipioDao.class);
        UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
        Collection<Municipio> municipios = dao.findByUF(idUf);
        municipiosEndereco = new ArrayList<SelectItem>(0);
        municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
        municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
    }

	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}
}
