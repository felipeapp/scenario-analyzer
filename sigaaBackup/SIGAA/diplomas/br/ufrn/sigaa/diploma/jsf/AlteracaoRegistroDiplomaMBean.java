/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 23/02/2012
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ProcessoDAO;
import br.ufrn.comum.dao.ProcessoDAOImpl;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dominio.AlteracaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.mensagens.MensagensGerais;

/**
 * Controler responsável pelo registro de mudanças em registros de diplomas.
 * @author Édipo Elder F. de Melo
 *
 */
@Component("alteracaoRegistroDiplomaMBean")
@Scope("request")
public class AlteracaoRegistroDiplomaMBean extends SigaaAbstractController<AlteracaoRegistroDiploma> {

	/** Registro de Diploma a ser alterado. **/
	private RegistroDiploma registroAntigo;
	/** Registro de Diploma com alterações. **/
	private RegistroDiploma registroNovo;

	/** Cadastra a alteração do Registro de Diploma<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/alterar_registro_diploma.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DIPLOMAS);
		validacaoDados(erros);
		if (hasErrors())
			return null;
		obj = new AlteracaoRegistroDiploma(registroAntigo, registroNovo);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(registroNovo);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_REGISTRO_DIPLOMA);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return forward("/diplomas/registro_diplomas/alterar_registro_diploma.jsp");
		}
		return cancelar();
	}

	/** Inicia a alteração de um registo de diplomas.
	 * Este método deve ser chamado a partir de outro controler (ex.: busca de registro de diplomas).
	 * <br/>Método não invocado por JSP´s.
	 * @param registro
	 * @return
	 * @throws ArqException
	 */
	public String inciarAlteracaoRegistroDiploma(RegistroDiploma registro) throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DIPLOMAS);
		if (isEmpty(registro)) {
			addMensagemErro("Selecione um registro de diplomas válido.");
			return null;
		}
		this.registroAntigo = registro;
		try {
			registroNovo = new RegistroDiploma();
			BeanUtils.copyProperties(registroNovo, registro);
		} catch (Exception e) {
			throw new ArqException(e);
		}
		setOperacaoAtiva(SigaaListaComando.ALTERAR_REGISTRO_DIPLOMA.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_REGISTRO_DIPLOMA);
		return forward("/diplomas/registro_diplomas/alterar_registro_diploma.jsp");
	}
	
	/** Valida os dados para o cadastro de diploma: Ano/Período, datas de coleção, expedição e registro, e número do processo.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		ValidatorUtil.validateRequired(registroNovo.getDataColacao(), "Data de Colação", lista);
		ValidatorUtil.validateRequired(registroNovo.getDataExpedicao(), "Data de Expedição", lista);
		ValidatorUtil.validateRequired(registroNovo.getDataRegistro(), "Data de Registro", lista);
		ValidatorUtil.validateRequired(registroNovo.getProcesso(), "Nº do Processo", erros);
		if(!confirmaProcesso()) {
			lista.addMensagem(MensagensGerais.NUMERO_PROCESSO_NAO_ENCONTRADO);
		}
		return hasErrors();
	}
	
	/** Verifica se o processo existe no banco.
	 * @return
	 */
	private boolean confirmaProcesso() {
		ProcessoDTO processo = null;
		String numeroProtocolo = registroNovo.getProcesso();
		try {
			String digitoAno = numeroProtocolo.substring(numeroProtocolo.length() - 7, numeroProtocolo.length());
			int numero = Integer.parseInt(numeroProtocolo.substring(6, numeroProtocolo.length()-8));
			int ano = Integer.parseInt(digitoAno.substring(0, digitoAno.length()-3));
			ProcessoDAO procDao = getDAO(ProcessoDAOImpl.class);
			processo = procDao.findByIdentificador(numero, ano, -1);
			if(processo != null) {
				return true;
			} else {
				return false;
			}
		}
		catch (StringIndexOutOfBoundsException e) { 
			return false;
		}
		catch (NumberFormatException e) {
			return false;
		}
		catch (DAOException e) {
			addMensagemErroPadrao();
			notifyError(e);
			return false;
		} 
	}

	public RegistroDiploma getRegistroNovo() {
		return registroNovo;
	}

	public void setRegistroNovo(RegistroDiploma registroNovo) {
		this.registroNovo = registroNovo;
	}

	public RegistroDiploma getRegistroAntigo() {
		return registroAntigo;
	}

	public void setRegistroAntigo(RegistroDiploma registroAntigo) {
		this.registroAntigo = registroAntigo;
	}
}
