/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.graduacao.ExtrapolarCreditoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoExtrapolarCredito;

/**
 * Controller respons�vel pelo cadastro de permiss�es para extrapolar a carga
 * hor�ria m�xima por semestre na matr�cula em disciplinas por discente.
 * 
 * @author Henrique Andr�
 * 
 */
@Component("extrapolarCredito")
@Scope("session")
public class ExtrapolarCreditoMBean extends SigaaAbstractController<ExtrapolarCredito> implements OperadorDiscente {

	/** Cole��o de permiss�es para extrapolar a carga hor�ria dadas ao discente selecionado. */
	private Collection<ExtrapolarCredito> permissoes;

	/** Redireciona o usu�rio para o formul�rio de cadastro de permiss�es. 
	 * @return
	 */
	public String telaForm() {
		return forward("/graduacao/matricula/restricoes/form.jsp");
	}
	
	/** Redireciona o usu�rio para a busca por discente 
	 * @return
	 */
	public String telaBuscaDiscente() {
		return forward("/graduacao/busca_discente.jsp");
	}

	/** Inicia o processo de cadastro de permiss�es para extrapolar a carga hor�ria na m�tricula do discente.
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		obj = new ExtrapolarCredito();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MATRICULA_EXTRAPOLAR_CREDITO);
		prepareMovimento(SigaaListaComando.MATRICULA_INSERIR_EXTRAPOLAR_CREDITO);
		return buscaDiscenteMBean.popular();
	}

	/** M�todo invocado quando o discente � selecionado.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		if (obj.getDiscente() == null) {
			addMensagemErro("N�o foi poss�vel carregar dados do discente selecionado");
			return null;
		}
		return telaForm();
	}

	/** Seta o discente selecionado pelo usu�rio.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.ensino.dominio.DiscenteAdapter)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		try {
			ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class);
			obj.setDiscente((DiscenteGraduacao) dao.refresh(discente));
			permissoes = dao.findByDiscenteAtivo(discente);
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			e.printStackTrace();
		}
	}

	/** Cadastra a permiss�o para extrapolar a carga hor�ria no ano-semestre.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String cadastrar() throws ArqException {
		validacaoDados(erros);
		if (hasErrors()) return null;
		if( !confirmaSenha() )
			return null;		
		MovimentoExtrapolarCredito mov = new MovimentoExtrapolarCredito();
		mov.setExtrapolarCredito(obj);
		mov.setAcao(MovimentoExtrapolarCredito.CADASTRAR);
		mov.setCodMovimento(SigaaListaComando.MATRICULA_INSERIR_EXTRAPOLAR_CREDITO);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage(
					"Operacao de extrapolar cr�ditos feita com sucesso para o aluno: "
							+ obj.getDiscente().getNome(),
					TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, e.getMessage());
		}
		redirectJSF(getSubSistema().getLink());
		return cancelar();
	}

	/** Valida os dados para o cadastro de permiss�o para extrapolar os cr�ditos.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		try {
			if (obj.getAno() == null || obj.getAno() < CalendarUtils.getAnoAtual()) {
				mensagens.addErro("Ano inv�lido");
			}
			if (obj.getPeriodo() == null || obj.getPeriodo() > 2 || obj.getPeriodo() < 1) {
				mensagens.addErro("Periodo inv�lido");
			}
			if ( obj.getDiscente().getStatus() != StatusDiscente.ATIVO && obj.getDiscente().getStatus() != StatusDiscente.FORMANDO ) {
				mensagens.addErro("Apenas alunos ativos podem realizar matr�culas.");
			}
			if ((obj.getCrMaximoExtrapolado() == null && obj.getCrMinimoExtrapolado() == null) || (obj.getCrMaximoExtrapolado() == 0 && obj.getCrMinimoExtrapolado() == 0)) {
				mensagens.addErro("Informe um valor m�ximo e um valor m�nimo para o n�mero de cr�ditos extrapolados.");
			} else {
				// compara valores m�ximos e m�nimo com os do curr�culo do aluno
				Curriculo curriculo = obj.getDiscente().getCurriculo();
				validateMinValue(obj.getCrMaximoExtrapolado(), curriculo.getCrMaximoSemestre(), "Cr�ditos M�ximos", mensagens);
				validateMaxValue(obj.getCrMinimoExtrapolado(), curriculo.getCrMinimoSemestre(), "Cr�ditos M�nimos", mensagens);
			}
			ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class);
			ExtrapolarCredito permissao = dao.findPermissaoAtivo(obj.getDiscente().getDiscente(), obj.getAno(), obj.getPeriodo());
			if (permissao != null) {
				mensagens.addErro("Este Aluno j� possui permiss�o para extrapolar cr�ditos neste ano-periodo.");
			}
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return !mensagens.isEmpty();
	}

	/** Exclui a permiss�o para extrapolar a carga hor�ria no ano-semestre.
	 * @return
	 * @throws ArqException
	 */
	public String excluir() throws ArqException {
		prepareMovimento(SigaaListaComando.MATRICULA_CACELAR_EXTRAPOLAR_CREDITO);
		
		int id = getParameterInt("id");
		
		ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class);
		ExtrapolarCredito extrapolarCredito = dao.findByPrimaryKey(id, ExtrapolarCredito.class);
		
		if (extrapolarCredito == null || !extrapolarCredito.isAtivo()) {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "permiss�o", "removida");
			return null;
		}
		MovimentoExtrapolarCredito mov = new MovimentoExtrapolarCredito();
		mov.setExtrapolarCredito(extrapolarCredito);
		mov.setAcao(MovimentoExtrapolarCredito.EXCLUIR);
		mov.setCodMovimento(SigaaListaComando.MATRICULA_CACELAR_EXTRAPOLAR_CREDITO);
		
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage(
					"Operacao de excluir cr�ditos feita com sucesso para o aluno: "
							+ obj.getDiscente().getNome(),
					TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		permissoes = dao.findByDiscenteAtivo(extrapolarCredito.getDiscente());
		return telaForm();
	}
	
	/** Retorna a cole��o de permiss�es para extrapolar a carga hor�ria dadas ao discente selecionado.
	 * @return
	 */
	public Collection<ExtrapolarCredito> getPermissoes() {
		return permissoes;
	}

	/** Seta a cole��o de permiss�es para extrapolar a carga hor�ria dadas ao discente selecionado.
	 * @param permissoes
	 */
	public void setPermissoes(Collection<ExtrapolarCredito> permissoes) {
		this.permissoes = permissoes;
	}

}