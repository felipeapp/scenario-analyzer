package br.ufrn.sigaa.ouvidoria.jsf;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoManifestacao;

/**
 * MBean responsável por realizar as operações de {@link Manifestacao} do Portal do Docente.
 * 
 * @author bernardo
 *
 */
@Component(value="manifestacaoDocente") @Scope(value="request")
public class ManifestacaoDocenteMBean extends ManifestacaoAbstractController {
    
    public ManifestacaoDocenteMBean() {
    	init();
    }

    @Override
    public String preCadastrar() throws ArqException, NegocioException {
    	CategoriaSolicitante categoria = getGenericDAO().findByPrimaryKey(CategoriaSolicitante.DOCENTE, CategoriaSolicitante.class);
    	
		obj = new Manifestacao(CategoriaSolicitante.DOCENTE, OrigemManifestacao.ONLINE, StatusManifestacao.SOLICITADA, getUsuarioLogado().getPessoa());
		obj.setTipoManifestacao(TipoManifestacao.getTipoManifestacao(TipoManifestacao.CRITICA));
		obj.getInteressadoManifestacao().setCategoriaSolicitante(categoria);
		
		prepareMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_DOCENTE);
		
		return forward(getFormPage());
    }
    
    @Override
    public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		validaFormatoArquivo(arquivo);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(arquivo);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_DOCENTE);
		
		try {
		    execute(mov);
		    
		    obj = getGenericDAO().findAndFetch(obj.getId(), Manifestacao.class, "tipoManifestacao", "assuntoManifestacao");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return paginaComprovante();
    }

    @Override
    public String getDirBase() {
    	String dirBase = super.getDirBase();
    	
    	if(getSubSistema().getId() != SigaaSubsistemas.OUVIDORIA.getId()) {
    		dirBase += "/ouvidoria/Manifestacao";
    	}
    	dirBase += "/docente";
    	
		return dirBase;
    }


}
