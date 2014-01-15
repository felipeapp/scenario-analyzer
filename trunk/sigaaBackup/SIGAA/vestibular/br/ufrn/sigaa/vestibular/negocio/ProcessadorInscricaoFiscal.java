/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/** Processador responsável pela efetivação da inscrição à seleção de fiscais. 
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorInscricaoFiscal extends ProcessadorCadastro {

	/** Executa a inscrição do fiscal.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		// define o PS para qual vai trabalhar
		InscricaoFiscal inscricaoFiscal = (InscricaoFiscal) ((MovimentoCadastro) mov).getObjMovimentado();
		UploadedFile foto = (UploadedFile) ((MovimentoCadastro) mov).getObjAuxiliar();
		// seta pessoa, servidor e discente
		inscricaoFiscal.setPessoa( inscricaoFiscal.getPessoa() );
		inscricaoFiscal.setServidor( inscricaoFiscal.getServidor() );
		inscricaoFiscal.setDiscente( inscricaoFiscal.getDiscente() );
		
		// determina se é recadastro ou não
		FiscalDao fiscalDao = new FiscalDao();
		inscricaoFiscal.setRecadastro(InscricaoFiscalValidator.isRecadastro(inscricaoFiscal.getPessoa()));
		inscricaoFiscal.setNumeroInscricao(fiscalDao.getNextSeq("vestibular.seq_inscricao_fiscal"));
		fiscalDao.close();
		
		// Persistir arquivo com a foto
		try {
			if (foto != null ) {
				// remove a foto anterior
				if (inscricaoFiscal.getIdFoto() != null) {
					EnvioArquivoHelper.removeArquivo(inscricaoFiscal.getIdFoto());
				}
				int idFoto = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idFoto,
						foto.getBytes(),
						foto.getContentType(),
						foto.getName());
				inscricaoFiscal.setIdFoto( idFoto );
				// quando uma foto é inserida, o status da foto é definida como não analisada.
				inscricaoFiscal.setStatusFoto(new StatusFoto(StatusFoto.NAO_ANALISADA));
				// não permite mais alterar a foto.
				inscricaoFiscal.setPermiteAlterarFoto(false);
			}
		} catch (IOException e) {
			throw new ArqException(e);
		}
		
		// cadastra
		criar((MovimentoCadastro) mov);
		return null;
	}

	/** Valida os dados necessários para a inscrição.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException {
		ListaMensagens lista = new ListaMensagens();
		UploadedFile foto = (UploadedFile) ((MovimentoCadastro) mov).getObjAuxiliar();
		InscricaoFiscal inscricaoFiscal = (InscricaoFiscal) ((MovimentoCadastro) mov).getObjMovimentado();
		if (inscricaoFiscal.getIdFoto() == null)
			validateRequired(foto, "Foto 3x4", lista);
		else
			validateRequiredId(inscricaoFiscal.getIdFoto(), "Foto 3x4", lista);
		if (ValidatorUtil.isEmpty(inscricaoFiscal.getServidor()) &&
				ValidatorUtil.isEmpty(inscricaoFiscal.getDiscente())) {
			throw new NegocioException("Impossível determinar o perfil do fiscal (discente/servidor).");
		} else if (ValidatorUtil.isEmpty(inscricaoFiscal.getDiscente())) {
			InscricaoFiscalValidator.validaInscricaoServidor(inscricaoFiscal
					.getServidor(), inscricaoFiscal
					.getProcessoSeletivoVestibular(), lista);
		} else if (ValidatorUtil.isEmpty(inscricaoFiscal.getServidor())) {
			InscricaoFiscalValidator.validaInscricaoDiscente(inscricaoFiscal
					.getDiscente().getDiscente(), inscricaoFiscal
					.getProcessoSeletivoVestibular(), lista);
		} 
		if (lista.size() > 0) {
			NegocioException e = new NegocioException();
			e.addMensagens(lista.getMensagens());
			throw e;
		}
	}
}
