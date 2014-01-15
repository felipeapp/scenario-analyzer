/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 *
 * Created on 30/01/2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.AlteracaoStatusDiscenteMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean respons�vel por realizar altera��o de status da situa��o do discente no seu curr�culo.
 * @author Victor Hugo
 *
 */
@Component("alteracaoStatusDiscente")
@Scope("session")
public class AlteracaoStatusDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente{

	/** Novo status que o discente ter�. */
	private StatusDiscente status = new StatusDiscente();

	/** Observa��o para o motivo desta altera��o de status manual. */
	private String observacao;

	/** Este atributo diz se � para ignorar as pend�ncias na biblioteca do discente que est� tendo o status alterado */
	private boolean ignorarPendencias = false;
	
	/** Construtor padr�o. */
	public AlteracaoStatusDiscenteMBean() {
		setConfirmButton("Alterar Status do Aluno");
		ignorarPendencias = false;
		obj = new Discente();
	}

	/** Inicia a opera��o de altera��o de status do discente.<br><br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):<ul>
	 *  <li> /sigaa.war/graduacao/menus/administracao.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/discente.jsp</li></ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_LATO ,SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, 
				SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		prepareMovimento(SigaaListaComando.ALTERACAO_STATUS_DISCENTE);
		setOperacaoAtiva(SigaaListaComando.ALTERACAO_STATUS_DISCENTE.getId());

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao( OperacaoDiscente.ALTERACAO_STATUS_DISCENTE );
		setConfirmButton("Alterar Status do Aluno");
		ignorarPendencias = false;
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Chama o processador para persistir a altera��o do status do discente.<br><br>
	 * M�todo chamado pela JSP: /sigaa.war/ensino/alteracao_status_discente/form.jsp
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String chamaModelo() throws ArqException, NegocioException{
		if (!checkOperacaoAtiva(SigaaListaComando.ALTERACAO_STATUS_DISCENTE.getId()))
			return cancelar();
		
		if( isEmpty( observacao ) )
			addMensagemErro("Entre com a justificativa para a altera��o do status do discente.");
		
		if( isEmpty(status) || status.getId() == 0 )
			addMensagemErro("Selecione um status para realizar a altera��o.");

		if( !isEmpty(status) && obj.getStatus() == status.getId() )
			addMensagemErro("Voc� n�o pode alterar o status do discente para o mesmo status que ele possui.");
		
		MatriculaComponenteDao mcDao = getDAO(MatriculaComponenteDao.class);
		int numeroMatriculas = mcDao.countMatriculasByDiscente(obj, SituacaoMatricula.MATRICULADO,SituacaoMatricula.EM_ESPERA);
		if( (obj.getDiscente().isGraduacao() || obj.getDiscente().isTecnico() || obj.getDiscente().isFormacaoComplementar()) 
				&& !isEmpty(status) 
				&& numeroMatriculas > 0
				&& (StatusDiscente.isAfastamento(status.getId()) || StatusDiscente.isAfastamentoPermanente(status.getId())) )
			addMensagemErro("N�o � poss�vel alterar o status do discente para status de afastamento, quando este discente possui registros de matr�culas.");
		
		if( hasErrors() )
			return null;
		
		/**
		 * Se for um status de afastamento deve verificar se o discente tem pendencia na biblioteca.
		 * caso ele tenha apenas ADMINISTRADOR_DAE E ADMINISTRADOR_STRICTO podem realizar a altera��o de vinculo do discente
		 */
		if( StatusDiscente.isAfastamentoPermanente(status.getId()) && VerificaSituacaoUsuarioBibliotecaUtil.temPendencia(obj.getDiscente()) && !ignorarPendencias ){
			
			if( (obj.isGraduacao() && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) || ( obj.isStricto() && isUserInRole(SigaaPapeis.ADMINISTRADOR_STRICTO) ) ){
				
				addMensagemWarning("Aten��o Usu�rio, o discente selecionado possue empr�stimos ativos na biblioteca. " +
						"Com o v�nculo encerrado, o aluno poder� tomar posse indevidamente de um bem da institui��o. " +
						"Deseja realmente cancelar o v�nculo desse discente?");
				setConfirmButton("Confirmar altera��o de status");
				ignorarPendencias = true;
				return forward( getFormPage() );
			}else{
				addMensagemWarning("Caro Usu�rio, o discente selecionado possue empr�stimos ativos na biblioteca e por isso n�o pode ter seu status alterado.");
				ignorarPendencias = false;
				setConfirmButton("Alterar Status do Aluno");
				return null;
			}
			
		}
		
		
		if( hasErrors() )
			return null;

		if( !confirmaSenha() )
			return null;

		AlteracaoStatusDiscenteMov mov = new AlteracaoStatusDiscenteMov();
		mov.setCodMovimento( SigaaListaComando.ALTERACAO_STATUS_DISCENTE );
		mov.setDiscente( obj.getDiscente() );
		mov.setStatus(status);
		mov.setObservacao(observacao);

		try {
			execute(mov);
			addMessage("Altera��o de Status do Discente Realizada com Sucesso!", TipoMensagemUFRN.INFORMATION);
			ignorarPendencias = false;
			setConfirmButton("Alterar Status do Aluno");
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		}catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		return cancelar();

	}

	/** 
	 * M�todo chamado ap�s a sele��o do discente na busca por discente.<br><br>
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() {
		return forward( getFormPage() );
	}

	/** Seta o discente selecionado na busca por discentes.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) {
		obj = discente;
	}

	/** Retorna o diret�rio base dos formul�rios utilizados neste controller.<br><br>
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
	 */
	public String getDirBase() {
		return "/ensino/alteracao_status_discente";
	}

	/** Retorna o novo status que o discente ter�.
	 * @return
	 */
	public StatusDiscente getStatus() {
		return status;
	}

	/** Seta o novo status que o discente ter�. 
	 * @param status
	 */
	public void setStatus(StatusDiscente status) {
		this.status = status;
	}

	/** Retorna a observa��o para o motivo desta altera��o de status manual. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta a observa��o para o motivo desta altera��o de status manual.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isIgnorarPendencias() {
		return ignorarPendencias;
	}

	public void setIgnorarPendencias(boolean ignorarPendencias) {
		this.ignorarPendencias = ignorarPendencias;
	}

}
