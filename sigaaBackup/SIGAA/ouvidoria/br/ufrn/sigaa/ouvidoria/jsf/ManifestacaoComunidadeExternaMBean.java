package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * MBean responsável por realizar as operações de {@link Manifestacao} do Portal Público do sistema.
 * 
 * @author bernardo
 *
 */
@Component(value="manifestacaoComunidadeExterna") @Scope(value="request")
public class ManifestacaoComunidadeExternaMBean extends	ManifestacaoAbstractController {
    
	/** Link para a JSP do comprovante de cadastro da manifestação. */
    public static final String JSP_COMPROVANTE_MANIFESTACAO = "/public/ouvidoria/Manifestacao/comprovante.jsf";
    
    /** Link para a JSP do comprovante de cadastro da manifestação, na versão para impressão. */
    public static final String JSP_IMPRESSAO_COMPROVANTE = "/public/ouvidoria/Manifestacao/comprovante_impressao.jsf";

    /** Armazena os dados do manifestante. */
    private InteressadoNaoAutenticado interessadoNaoAutenticado;
    
    /** Combo de municípios carregados a partir da UF selecionada. */
    private Collection<SelectItem> municipiosCombo;
    
    /** Código de área informado para o telefone. */
    private String codArea;
	
    public ManifestacaoComunidadeExternaMBean() {
    	init();
    }

    /**
     * Inicia os dados necessários do MBean
     */
    protected void init() {
		obj = new Manifestacao();
		municipiosCombo = new ArrayList<SelectItem>();
    }
    
    /**
     * Inicia o cadastro da manifestação.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/public/ouvidoria/Manifestacao/form.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     * @throws NegocioException
     */
    public String getIniciarCadastro() throws ArqException, NegocioException {
		boolean operacaoAtiva = isOperacaoAtiva(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA.getId());
		
		if(!operacaoAtiva || interessadoNaoAutenticado == null) {
		    obj = new Manifestacao(CategoriaSolicitante.COMUNIDADE_EXTERNA, OrigemManifestacao.ONLINE, StatusManifestacao.SOLICITADA, null);
		    obj.setTipoManifestacao(TipoManifestacao.getTipoManifestacao(TipoManifestacao.CRITICA));
		    interessadoNaoAutenticado = new InteressadoNaoAutenticado();
		    interessadoNaoAutenticado.setEndereco(new Endereco());
		    
		    setOperacaoAtiva(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA.getId());
		    prepareMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA);
		}
		
		return "";
    }
    
    /**
     * Recupera a UF selecionada e carrega os municípios associados.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/public/ouvidoria/Manifestacao/form.jsp</li>
     * </ul>
     * 
     * @param e
     * @throws DAOException
     */
    public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer idUf = (Integer) e.getNewValue();
			carregarMunicipios(idUf);
		}
    }
    
    /**
     * Carrega o combo de municípios de acordo com a UF passada.<br />
     * Método não invocado por JSPs.
     * 
     * @param idUf
     * @throws DAOException
     */
    public void carregarMunicipios(Integer idUf) throws DAOException {
		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		if(interessadoNaoAutenticado.getEndereco() != null) {
		    interessadoNaoAutenticado.getEndereco().setUnidadeFederativa(isNotEmpty(uf) ? uf : new UnidadeFederativa());
		    interessadoNaoAutenticado.getEndereco().setMunicipio(isNotEmpty(uf) ? uf.getCapital() : new Municipio());
		}
		municipiosCombo = new ArrayList<SelectItem>();
		municipiosCombo.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosCombo.addAll(toSelectItems(municipios, "id", "nome"));
    }
    
    /**
     * Seta o valor do município do interessado de acoro com o escolhido no formulário.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/public/ouvidoria/Manifestacao/form.jsp</li>
     * </ul>
     * 
     * @param e
     * @throws DAOException
     */
    public void setarMunicipio(ValueChangeEvent e) throws DAOException {
		if(e.getNewValue() != null) {
		    GenericDAO dao = getGenericDAO();
		    try {
				int idMunicipio = (Integer) e.getNewValue();
				Municipio municipio = dao.findByPrimaryKey(idMunicipio, Municipio.class, "id", "nome");
				
				interessadoNaoAutenticado.getEndereco().setMunicipio(municipio);
		    } finally {
		    	dao.close();
		    }
		}
    }
    
    @Override
    public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		montarObj();
		validaFormatoArquivo(arquivo);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(arquivo);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA);
		
		try {
		    execute(mov);
		    removeOperacaoAtiva();
		    
		    obj = getGenericDAO().findAndFetch(obj.getId(), Manifestacao.class, "tipoManifestacao", "assuntoManifestacao");
		    if(isNotEmpty(interessadoNaoAutenticado.getEndereco()) && isNotEmpty(interessadoNaoAutenticado.getEndereco().getTipoLogradouro())) {
				GenericDAO dao = getGenericDAO();
				
				interessadoNaoAutenticado.getEndereco().setTipoLogradouro(dao.findByPrimaryKey(interessadoNaoAutenticado.getEndereco().getTipoLogradouro().getId(), TipoLogradouro.class));
			}
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return paginaComprovante();
    }

    /**
     * Monta o objeto trabalhado para cadastro.
     */
    private void montarObj() {
		if(isNotEmpty(codArea))
		    interessadoNaoAutenticado.setCodigoAreaTelefone(Short.valueOf(codArea));
		
		interessadoNaoAutenticado.setNome(StringUtils.upperCase(interessadoNaoAutenticado.getNome()));
		interessadoNaoAutenticado.setNomeAscii(StringUtils.toAscii(interessadoNaoAutenticado.getNome()));
		
		obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().setInteressadoNaoAutenticado(interessadoNaoAutenticado);
    }
    
    @Override
    public String paginaComprovante() {
    	return forward(JSP_COMPROVANTE_MANIFESTACAO);
    }
    
    @Override
    public String imprimirComprovante() {
    	return forward(JSP_IMPRESSAO_COMPROVANTE);
    }

    public InteressadoNaoAutenticado getInteressadoNaoAutenticado() {
        return interessadoNaoAutenticado;
    }

    public void setInteressadoNaoAutenticado(
    	InteressadoNaoAutenticado interessadoNaoAutenticado) {
        this.interessadoNaoAutenticado = interessadoNaoAutenticado;
    }

    public Collection<SelectItem> getMunicipiosCombo() {
        return municipiosCombo;
    }

    public void setMunicipiosCombo(Collection<SelectItem> municipiosCombo) {
        this.municipiosCombo = municipiosCombo;
    }

    public String getCodArea() {
        return codArea;
    }

    public void setCodArea(String codArea) {
        this.codArea = codArea;
    }


}
