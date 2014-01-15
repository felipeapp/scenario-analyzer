/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 25/10/2010
 *
 */
package br.ufrn.sigaa.diploma.negocio;

import java.io.IOException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.DtoUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.siged.dto.ArquivoDocumento;
import br.ufrn.integracao.siged.dto.DocumentoDTO;
import br.ufrn.integracao.siged.dto.PastaDocumentoDTO;
import br.ufrn.sigaa.diploma.dominio.ParametrosDiplomas;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.siged.integracao.SigedFacade;

/** Classe para auxiliar o cadastro de diploma digitalizado no SIGED.
 * @author Édipo Elder F. Melo
 *
 */
public class DiplomaHelper {

	/** Cadastra o arquivo digital do diploma no SIGED.
	 * @param arquivo obrigatório. Pode ser qualquer formato, sendo recomendável o formato PDF.
	 * @param registro objeto contendo os dados do registro do diploma (discente, número de registro)
	 * @param usuario 
	 * @param siged  
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws IOException
	 */
	public static DocumentoDTO inserirDiplomaSIGED(ArquivoDocumento arquivo, RegistroDiploma registro, UsuarioGeral usuario, SigedFacade siged) throws NegocioException, ArqException, IOException{
		PastaDocumentoDTO pastaRaiz = siged.buscarPasta(ParametroHelper.getInstance().getParametro(ParametrosDiplomas.PATH_DIPLOMAS), null);
		if (pastaRaiz == null)
			throw new NegocioException("A pasta para armagenzamento de diplomas não está definida ou o SIGED está desabilitado.");
		DocumentoDTO documento = new DocumentoDTO();
		documento.setTipoDocumento(ParametroHelper.getInstance().getParametroInt(ParametrosDiplomas.TIPO_DOCUMENTO_DIPLOMA));
		String subPasta = NivelEnsino.getDescricao(registro.getDiscente().getNivel());
	    PastaDocumentoDTO pasta = siged.buscarPasta(subPasta, pastaRaiz.getId());
	    if (pasta == null) {
	    	pasta = new PastaDocumentoDTO();
	    	pasta.setNome(subPasta);
	    	pasta.setPai(pastaRaiz.getId());
	    	pasta = siged.cadastrarPasta(pasta, DtoUtils.deUsuarioParaDTO(usuario));
	    }
    	documento.setLocalizacao(pasta.getId());
    	// descritores do diploma
    	// matrícula
    	documento.adicionarDescritor(ParametroHelper.getInstance().getParametroInt(ParametrosDiplomas.DESCRITOR_DIPLOMA_MATRICULA_ALUNO), registro.getDiscente().getMatricula().toString(), true);
    	// nome do discente
	    documento.adicionarDescritor(ParametroHelper.getInstance().getParametroInt(ParametrosDiplomas.DESCRITOR_DIPLOMA_NOME_ALUNO), registro.getDiscente().getNome(), true);
	    // número de registro do diploma
	    documento.adicionarDescritor(ParametroHelper.getInstance().getParametroInt(ParametrosDiplomas.DESCRITOR_DIPLOMA_NUMERO_REGISTRO), registro.getNumeroRegistro().toString(), true);
	    UsuarioDTO usuarioDTO = DtoUtils.deUsuarioParaDTO(usuario);
		siged.cadastrarDocumento(documento, arquivo, usuarioDTO);
		return documento;
	}
}
