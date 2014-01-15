/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Controller responsável pelo cadastro de permissões para extrapolar a carga
 * horária máxima por semestre na matrícula em disciplinas por discente.
 * 
 * @author Henrique André
 * 
 */
@Component("extrapolarCredito")
@Scope("session")
public class ExtrapolarCreditoMBean extends SigaaAbstractController<ExtrapolarCredito> implements OperadorDiscente {

	/** Coleção de permissões para extrapolar a carga horária dadas ao discente selecionado. */
	private Collection<ExtrapolarCredito> permissoes;

	/** Redireciona o usuário para o formulário de cadastro de permissões. 
	 * @return
	 */
	public String telaForm() {
		return forward("/graduacao/matricula/restricoes/form.jsp");
	}
	
	/** Redireciona o usuário para a busca por discente 
	 * @return
	 */
	public String telaBuscaDiscente() {
		return forward("/graduacao/busca_discente.jsp");
	}

	/** Inicia o processo de cadastro de permissões para extrapolar a carga horária na mátricula do discente.
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

	/** Método invocado quando o discente é selecionado.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		if (obj.getDiscente() == null) {
			addMensagemErro("Não foi possível carregar dados do discente selecionado");
			return null;
		}
		return telaForm();
	}

	/** Seta o discente selecionado pelo usuário.
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

	/** Cadastra a permissão para extrapolar a carga horária no ano-semestre.
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
					"Operacao de extrapolar créditos feita com sucesso para o aluno: "
							+ obj.getDiscente().getNome(),
					TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, e.getMessage());
		}
		redirectJSF(getSubSistema().getLink());
		return cancelar();
	}

	/** Valida os dados para o cadastro de permissão para extrapolar os créditos.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		try {
			if (obj.getAno() == null || obj.getAno() < CalendarUtils.getAnoAtual()) {
				mensagens.addErro("Ano inválido");
			}
			if (obj.getPeriodo() == null || obj.getPeriodo() > 2 || obj.getPeriodo() < 1) {
				mensagens.addErro("Periodo inválido");
			}
			if ( obj.getDiscente().getStatus() != StatusDiscente.ATIVO && obj.getDiscente().getStatus() != StatusDiscente.FORMANDO ) {
				mensagens.addErro("Apenas alunos ativos podem realizar matrículas.");
			}
			if ((obj.getCrMaximoExtrapolado() == null && obj.getCrMinimoExtrapolado() == null) || (obj.getCrMaximoExtrapolado() == 0 && obj.getCrMinimoExtrapolado() == 0)) {
				mensagens.addErro("Informe um valor máximo e um valor mínimo para o número de créditos extrapolados.");
			} else {
				// compara valores máximos e mínimo com os do currículo do aluno
				Curriculo curriculo = obj.getDiscente().getCurriculo();
				validateMinValue(obj.getCrMaximoExtrapolado(), curriculo.getCrMaximoSemestre(), "Créditos Máximos", mensagens);
				validateMaxValue(obj.getCrMinimoExtrapolado(), curriculo.getCrMinimoSemestre(), "Créditos Mínimos", mensagens);
			}
			ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class);
			ExtrapolarCredito permissao = dao.findPermissaoAtivo(obj.getDiscente().getDiscente(), obj.getAno(), obj.getPeriodo());
			if (permissao != null) {
				mensagens.addErro("Este Aluno já possui permissão para extrapolar créditos neste ano-periodo.");
			}
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return !mensagens.isEmpty();
	}

	/** Exclui a permissão para extrapolar a carga horária no ano-semestre.
	 * @return
	 * @throws ArqException
	 */
	public String excluir() throws ArqException {
		prepareMovimento(SigaaListaComando.MATRICULA_CACELAR_EXTRAPOLAR_CREDITO);
		
		int id = getParameterInt("id");
		
		ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class);
		ExtrapolarCredito extrapolarCredito = dao.findByPrimaryKey(id, ExtrapolarCredito.class);
		
		if (extrapolarCredito == null || !extrapolarCredito.isAtivo()) {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "permissão", "removida");
			return null;
		}
		MovimentoExtrapolarCredito mov = new MovimentoExtrapolarCredito();
		mov.setExtrapolarCredito(extrapolarCredito);
		mov.setAcao(MovimentoExtrapolarCredito.EXCLUIR);
		mov.setCodMovimento(SigaaListaComando.MATRICULA_CACELAR_EXTRAPOLAR_CREDITO);
		
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage(
					"Operacao de excluir créditos feita com sucesso para o aluno: "
							+ obj.getDiscente().getNome(),
					TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		permissoes = dao.findByDiscenteAtivo(extrapolarCredito.getDiscente());
		return telaForm();
	}
	
	/** Retorna a coleção de permissões para extrapolar a carga horária dadas ao discente selecionado.
	 * @return
	 */
	public Collection<ExtrapolarCredito> getPermissoes() {
		return permissoes;
	}

	/** Seta a coleção de permissões para extrapolar a carga horária dadas ao discente selecionado.
	 * @param permissoes
	 */
	public void setPermissoes(Collection<ExtrapolarCredito> permissoes) {
		this.permissoes = permissoes;
	}

}