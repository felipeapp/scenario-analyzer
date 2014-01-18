/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.siged.dto.ArquivoDocumento;
import br.ufrn.integracao.siged.service.IntegracaoSigedService;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.ResponsavelAssinaturaDiplomasDao;
import br.ufrn.sigaa.diploma.dominio.AlteracaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/** Processador respons�vel por opera��es de registro de diploma individual.
 * @author �dipo Elder F. Melo
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
			if (!registro.getLivroRegistroDiploma().isLivroAntigo()) {
				// Assinaturas no diploma 
				registro.setAssinaturaDiploma(assinaturaDao.findAtivo(registro.getDiscente().getNivel()));
				// Se o registro do diploma n�o for antigo, seta o n�mero.
				int numeroRegistro = registroDao.requisitaNumeroRegistro(registro.getLivroRegistroDiploma().isRegistroExterno(), registro.getLivroRegistroDiploma().getNivel());
				registro.setNumeroRegistro(numeroRegistro);
			} else {
				// o n�mero n�o pode ter sido utilizado anteriormente
				Collection<RegistroDiploma> registros = registroDao.findByDiscenteNumeroRegistro(null, registro.getNumeroRegistro(), registro.getLivroRegistroDiploma().getNivel());
				if (registros != null && registros.size() > 0)
					throw new NegocioException("O n�mero do registro de diploma j� foi utilizado. Por favor, verifique se o valor est� correto.");
				// o n�mero antigo n�o pode ser maior que o �ltimo n�mero (registro novo)
				int inicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.NUMERO_INICIAL_REGISTRO_DIPLOMA);
				if (inicioRegistro != 0 && inicioRegistro <= registro.getNumeroRegistro())
					throw new NegocioException("O n�mero do registro de diploma n�o pode ser maior que " + inicioRegistro );
				// inclui o n�mero do registro no controle de n�meros
				registroDao.anotaNumeroRegistroAntigo(registro.getNumeroRegistro(), registro.getLivroRegistroDiploma().isRegistroExterno(), registro.getLivroRegistroDiploma().getNivel());
			}
			if (registro.getFolha().getId() == 0) {
				// atualiza o livro
				registroDao.createOrUpdate(registro.getFolha());
			}
			// seta o coordenador do curso, caso n�o seja registro antigo
			if (!registro.getLivroRegistroDiploma().isLivroAntigo()) {
				CoordenacaoCurso coordenador = coordenacaoDao.findAtivoByData(registro.getDataColacao(), registro.getDiscente().getCurso());
				// caso n�o haja coordena��o ativa, pega o �ltimo coordenador
				if (coordenador == null) {
					Collection<CoordenacaoCurso> coordenadores = coordenacaoDao.findByCurso(
							registro.getDiscente().getCurso().getId(), 0, 
							registro.getDiscente().getCurso().getNivel(), null,
							CargoAcademico.COORDENACAO);
					if (!isEmpty(coordenadores))
						coordenador = coordenadores.iterator().next(); 
				}
				registro.setCoordenadorCurso(coordenador);
			}
			// se registro antigo e a lista de assinaturas � nova, persiste a lista de assinaturas
			if (registro.getLivroRegistroDiploma().isLivroAntigo() && registro.getAssinaturaDiploma().getId() == 0)
				registroDao.create(registro.getAssinaturaDiploma());
			// persiste o registro
			registroDao.createOrUpdate(registro);
			// persite as observa��es
			if (!isEmpty(registro.getObservacoes()))
				for (ObservacaoRegistroDiploma obs : registro.getObservacoes())
					registroDao.createOrUpdate(obs);
			// se h� diploma digitalizado anexado, cadastra-o no SIGED
			UploadedFile uploadedFile = (UploadedFile) ((MovimentoCadastro) mov).getObjAuxiliar();
			if (uploadedFile != null) {
				
				if (!Sistema.isSigedAtivo())
					throw new NegocioException("N�o foi poss�vel enviar o diploma para o SIGED, pois o mesmo n�o encontra-se ativo.");
				
				ArquivoDocumento arquivo = new ArquivoDocumento(uploadedFile);
				IntegracaoSigedService siged = getBean("integracaoSigedInvoker", mov);			
				try {
					DiplomaHelper.inserirDiplomaSIGED(arquivo, registro, mov.getUsuarioLogado(), siged);
				} catch (Exception e) {					
					throw new NegocioException("N�o foi poss�vel enviar o diploma para o SIGED.");
				}
				
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
