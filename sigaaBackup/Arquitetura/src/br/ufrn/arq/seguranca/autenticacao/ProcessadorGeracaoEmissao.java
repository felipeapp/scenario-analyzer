/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2008
 */
package br.ufrn.arq.seguranca.autenticacao;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.EmissaoDocumentoAutenticadoDao;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;

/**
 * Processador responsável pela geração da emissão de documentos autenticados,
 * como históricos, atestados de matrícula, etc.
 * 
 * @author Gleydson Lima
 * 
 */
public class ProcessadorGeracaoEmissao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		EmissaoDocumentoAutenticadoDao dao = getDAO(
				EmissaoDocumentoAutenticadoDao.class, mov);

		EmissaoDocumentoAutenticado emissao = ((MovimentoCadastro) mov)
				.getObjMovimentado();

		if ( emissao.isEmissaoDocumentoComNumero() ) {
			/* 
			 * Se tiver número de documento, significa que o documento 
			 * já deve ter tido uma emissão anterior, então busca a emissão para 
			 * reaproveitá-la e gerar um novo registro
			 */ 
			
			// buscando emissão antiga
			EmissaoDocumentoAutenticado emissaoDocumento = dao.findByEmissaoSql(emissao
					.getIdentificador(), emissao.getCodigoSeguranca(), emissao
					.getTipoDocumento(), emissao.getSubTipoDocumento());
			
			if ( emissaoDocumento == null ) {
				emissao.setNumeroDocumento(dao.getNextSeq("numero_documento_emissao_seq") + "");
				dao.create(emissao);
				emissaoDocumento = emissao;
			}
			
			emissaoDocumento.setCodigoSeguranca(emissao.getCodigoSeguranca());
			RegistroEmissaoDocumentoAutenticado registro = criaRegistro(emissaoDocumento, mov);
			dao.create(registro);
			
			emissao = emissaoDocumento;
			
		} else {
			
			// é uma nova emissão e terá também um novo registro
			dao.create(emissao);
			RegistroEmissaoDocumentoAutenticado registro = criaRegistro(emissao, mov);
			dao.create(registro);
			
		}
		
		dao.close();
		
		return emissao;

	}
	
	/*
	 * Cria um registro de emissão de documento autenticado, para indicar
	 * que um determinado documento que já foi emitido está sendo emitido novamente.
	 */
	private RegistroEmissaoDocumentoAutenticado criaRegistro(EmissaoDocumentoAutenticado emissao, Movimento mov) {
		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		
		// cria novo registro de emissão
		RegistroEmissaoDocumentoAutenticado registro = new RegistroEmissaoDocumentoAutenticado();
		registro.setData(new Date());
		registro.setEmissao(emissao);
		
		if (mov.getUsuarioLogado() != null) {
			registro.setIdRegistroEntrada(cmov.getRegistroEntrada().getId());
		} else {
			registro.setIdRegistroAcessoPublico(cmov.getRegistroAcessoPublico().getId());
		}

		registro.setSistema(mov.getSistema());
		registro.setUsuario(mov.getUsuarioLogado());
		registro.setSistema(mov.getSistema());
		
		return registro;
		
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
