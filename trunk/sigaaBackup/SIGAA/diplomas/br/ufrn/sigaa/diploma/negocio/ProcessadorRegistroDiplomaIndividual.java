/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/06/2009
 *
 */
package br.ufrn.sigaa.diploma.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.integracao.siged.dto.ArquivoDocumento;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.ResponsavelAssinaturaDiplomasDao;
import br.ufrn.sigaa.diploma.dominio.AlteracaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.siged.integracao.SigedFacade;

/** Processador responsável por operações de registro de diploma individual.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorRegistroDiplomaIndividual extends AbstractProcessador {

	/** Realiza o registro do diploma.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_REGISTRO_DIPLOMA) )
			return alteraRegistroDiploma(mov);
		else
			return cadastraRegistroDiploma(mov);
	}

	/** Valida os dados antes do registro.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		RegistroDiploma obj = ((MovimentoCadastro) mov).getObjMovimentado();
		obj.validate();
	}
	
	/** Altera o Registro de Diploma;
	 * @param mov
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private Object alteraRegistroDiploma(Movimento mov) throws NegocioException, ArqException {
		RegistroDiplomaDao registroDao = getDAO(RegistroDiplomaDao.class, mov);
		try {
			AlteracaoRegistroDiploma alteracaoRegistro = ((MovimentoCadastro) mov).getObjMovimentado();
			RegistroDiploma registroAlterado = (RegistroDiploma) ((MovimentoCadastro) mov).getObjAuxiliar();
			registroDao.create(alteracaoRegistro);
			registroDao.update(registroAlterado);
			return registroAlterado;
		} finally {
			registroDao.close();
		}
	}
	
	/** Cadastra o Registro de Diploma;
	 * @param mov
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private Object cadastraRegistroDiploma(Movimento mov) throws NegocioException, ArqException {
		validate(mov);
		RegistroDiplomaDao registroDao = getDAO(RegistroDiplomaDao.class, mov);
		CoordenacaoCursoDao coordenacaoDao = getDAO(CoordenacaoCursoDao.class, mov);
		ResponsavelAssinaturaDiplomasDao assinaturaDao = getDAO(ResponsavelAssinaturaDiplomasDao.class, mov);
		try {
			RegistroDiploma registro = ((MovimentoCadastro) mov).getObjMovimentado();
			registro.setLivre(false);
			// registro de entrada
			registro.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			// Assinaturas no diploma
			registro.setAssinaturaDiploma(assinaturaDao.findAtivo(registro.getDiscente().getNivel()));
			if (!registro.getLivroRegistroDiploma().isLivroAntigo()) {
				// Se o registro do diploma não for antigo, seta o número.
				int numeroRegistro = registroDao.requisitaNumeroRegistro(registro.getLivroRegistroDiploma().isRegistroExterno(), registro.getLivroRegistroDiploma().getNivel());
				registro.setNumeroRegistro(numeroRegistro);
			} else {
				// o número não pode ter sido utilizado anteriormente
				Collection<RegistroDiploma> registros = registroDao.findByDiscenteNumeroRegistro(null, registro.getNumeroRegistro(), registro.getLivroRegistroDiploma().getNivel());
				if (registros != null && registros.size() > 0)
					throw new NegocioException("O número do registro de diploma já foi utilizado. Por favor, verifique se o valor está correto.");
				// o número antigo não pode ser maior que o último número (registro novo)
				int inicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.NUMERO_INICIAL_REGISTRO_DIPLOMA);
				if (inicioRegistro != 0 && inicioRegistro <= registro.getNumeroRegistro())
					throw new NegocioException("O número do registro de diploma não pode ser maior que " + inicioRegistro );
				// inclui o número do registro no controle de números
				registroDao.anotaNumeroRegistroAntigo(registro.getNumeroRegistro(), registro.getLivroRegistroDiploma().isRegistroExterno(), registro.getLivroRegistroDiploma().getNivel());
			}
			if (registro.getFolha().getId() == 0) {
				// atualiza o livro
				registroDao.createOrUpdate(registro.getFolha());
			}
			// seta o coordenador do curso
			CoordenacaoCurso coordenador = coordenacaoDao.findAtivoByData(registro.getDataColacao(), registro.getDiscente().getCurso());
			registro.setCoordenadorCurso(coordenador);
			// persiste o registro
			registroDao.createOrUpdate(registro);
			// persite as observações
			if (!isEmpty(registro.getObservacoes()))
				for (ObservacaoRegistroDiploma obs : registro.getObservacoes())
					registroDao.createOrUpdate(obs);
			// se há diploma digitalizado anexado, cadastra-o no SIGED
			UploadedFile uploadedFile = (UploadedFile) ((MovimentoCadastro) mov).getObjAuxiliar();
			if (uploadedFile != null) {
				SigedFacade siged = getBean("sigedFacade", mov);
				ArquivoDocumento arquivo = new ArquivoDocumento(uploadedFile);
				DiplomaHelper.inserirDiplomaSIGED(arquivo, registro, mov.getUsuarioLogado(), siged);
			}
			
			return registro;
		} catch (IOException e) {
			throw new ArqException(e);
		} finally {
			registroDao.close();
			coordenacaoDao.close();
			assinaturaDao.close();
		}
	}

}
