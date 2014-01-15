package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dao.AssuntoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Controller genérico para operações comuns aos cadastros de manifestações.
 * 
 * @author Bernardo
 *
 */
public class ManifestacaoAbstractController extends SigaaAbstractController<Manifestacao> {

	/** Arquivo anexado ao cadastrar-se uma nova manifestação */
	public UploadedFile arquivo;
	
	/** Tamanho maximo em bytes que um arquivo anexado pode ter '50Mb'. */
    public static final int TAM_MAX_ARQUIVO = 52428800;
    
	/**
	 * Inicia os dados necessários ao MBean.
	 */
	protected void init() {
		obj = new Manifestacao();
	}
	
	/**
	 * Direciona o fluxo para a tela do comprovante de manifestação.
	 * 
	 * @return
	 */
	public String paginaComprovante() {
		return forward(getDirBase() + "/comprovante.jsp");
	}
	
	/**
	 * Abre a página do comprovante na sua versão de impressão.
	 * 
	 * @return
	 */
	public String imprimirComprovante() {
    	return forward(getDirBase() + "/comprovante_impressao.jsp");
    }

	/**
	 * Retorna todos os assuntos associados à categoria de assunto escolhida.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AssuntoManifestacao> getAllAssuntosByCategoria() throws DAOException {
        AssuntoManifestacaoDao dao = getDAO(AssuntoManifestacaoDao.class);
        Collection<AssuntoManifestacao> assuntos = new ArrayList<AssuntoManifestacao>();
        
        try {
            if(obj != null && obj.getAssuntoManifestacao() != null
        	    && isNotEmpty(obj.getAssuntoManifestacao().getCategoriaAssuntoManifestacao()))
        	assuntos = dao.getAllAssuntosAtivosByCategoria(obj.getAssuntoManifestacao().getCategoriaAssuntoManifestacao());
        } finally {
            dao.close();
        }
        
        return assuntos;
    }

	/**
	 * Retorna todos os assuntos associados à categoria de assunto escolhida na forma de combo.
	 * 
	 * @return
	 * @throws DAOException
	 */
    public Collection<SelectItem> getAllAssuntosByCategoriaCombo() throws DAOException {
        return toSelectItems(getAllAssuntosByCategoria(), "id", "descricao");
    }
    
    /**
   	 * Valida o arquivo que o anexou a manifestação.
   	 *
   	 * <p>Este método é chamado pelas seguintes JSPs:
   	 * <ul><li>nenhuma JSP</li></ul>
        * @throws NegocioException 
   	 * 
   	 */
   	public void validaFormatoArquivo(UploadedFile arquivo) throws NegocioException  {
   		if( arquivo != null ) {
    			if(arquivo.getSize() > TAM_MAX_ARQUIVO){
   				arquivo = null;
   				throw new NegocioException("O arquivo anexado a manifestação precisa ter tamanho inferior a 50Mb.");
   			}
   			
   			if(arquivo != null && arquivo.getName().length() > 100){
   				arquivo = null;
   				throw new NegocioException("O tamanho máximo permito para o nome de arquivo é de 100 caracteres.");
   			}
   			
   		}
   	}
   	
   	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}
}
