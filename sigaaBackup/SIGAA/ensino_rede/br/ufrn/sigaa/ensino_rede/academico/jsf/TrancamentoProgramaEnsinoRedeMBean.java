package br.ufrn.sigaa.ensino_rede.academico.jsf;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino_rede.academico.dao.MovimentacaoDiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.academico.dominio.TipoMovimentacao;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscenteMBean;

/**
 * MBean para realizar o trancamento do programa de um discente associado. 
 * 
 * @author Henrique Andr�
 */
@SuppressWarnings("serial")
@Component @Scope("session")
public class TrancamentoProgramaEnsinoRedeMBean extends EnsinoRedeAbstractController<MovimentacaoDiscenteAssociado> implements SelecionaDiscente {

	/**
	 * Inicializa vari�veis
	 */
	public void clear() {
		obj = new MovimentacaoDiscenteAssociado();
		obj.setTipo(TipoMovimentacao.TRANCAMENTO);
	}
	
	/**
	 * Inicia o processo de trancamento
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String iniciar() throws DAOException, NegocioException {
		clear();
		SelecionaDiscenteMBean mBean = getMBean("selecionaDiscenteMBean");
		mBean.filterByStatus(new ArrayList<StatusDiscenteAssociado>());
				
		mBean.setRequisitor(this);
			
		if (isCoordenadorUnidadeRede())
			return mBean.executar(getCampusIes());
		else
			return mBean.executar();
	}

	/**
	 * M�todo invocado pelo mbean generico de selecionar discente
	 */
	@Override
	public void setDiscente(DiscenteAssociado discente) {
		obj.setDiscente(discente);
	}

	/**
	 * Seleciona o discente pelo usu�rio na view.
	 * M�todo invocado pelo mbean generico de selecionar discente
	 */
	@Override
	public String selecionaDiscente() throws ArqException {
		if(obj.getDiscente().getStatus().getId() != StatusDiscenteAssociado.ATIVO ){
			addMensagemErro("O usu�rio selecionado precisa est� com o status de ATIVO para realizar o trancamento.");
			return null;
		}
		obj.getDiscente().getPessoa();
		setOperacaoAtiva(SigaaListaComando.TRANCAR_PROGRAMA_REDE.getId());
		prepareMovimento(SigaaListaComando.TRANCAR_PROGRAMA_REDE);
		return forward("/ensino_rede/trancamento_programa/confirmar.jsp");
	}
	
	/**
	 * Cadastra a movimenta��o do discente
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
	
		if( !checkOperacaoAtiva(SigaaListaComando.TRANCAR_PROGRAMA_REDE.getId() )  ){
			return cancelar();
		}
			
		validar();
		
		if (hasErrors())
			return null;
		
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.TRANCAR_PROGRAMA_REDE);
		
		obj.setAnoOcorrencia(obj.getAnoReferencia());
		obj.setPeriodoOcorrencia(obj.getPeriodoReferencia());
		
		try {
			execute(mov);
			addMensagemInformation("Trancamento realizado com sucesso!");
		} catch(NegocioException e) {
			addMensagemErro("N�o foi poss�vel trancar o discente");
			return null;
		}
		clear();
		removeOperacaoAtiva();
		return 	redirectJSF(getSubSistema().getLink());
	}

	/**
	 * valida os dados informados na view
	 * 
	 * @throws DAOException
	 */
	private void validar() throws DAOException {
		int anoPeriodoDiscente=0;
		int anoPeriodoTrancamento=0;
		if( ValidatorUtil.isEmpty( obj.getAnoReferencia() ) ||  ValidatorUtil.isEmpty( obj.getPeriodoReferencia() ) ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Ano Per�odo");
			return;
		}
				
		anoPeriodoDiscente = (obj.getDiscente().getAnoIngresso() * 10) + obj.getDiscente().getPeriodoIngresso();
		anoPeriodoTrancamento = (obj.getAnoReferencia() * 10) + obj.getPeriodoReferencia(); 
		if( obj.getDiscente().getStatus().getId() == StatusDiscenteAssociado.TRANCADO){
			addMensagemErro("O usu�rio selecionado j� est� com status de TRANCADO.");
		}
		
		if (anoPeriodoTrancamento < anoPeriodoDiscente)
			addMensagemErro("O ano-per�odo do trancamento � inferior ao ano-per�odo de ingresso do discente.");
		
		MovimentacaoDiscenteAssociadoDao dao = getDAO(MovimentacaoDiscenteAssociadoDao.class);
		MovimentacaoDiscenteAssociado ma = dao.findByDiscenteAnoPeriodo(obj.getDiscente(), obj.getAnoReferencia(), obj.getPeriodoReferencia());
		
		if (ma != null)
			addMensagemErro("O discente j� possui uma movimenta��o de trancamento para " + ma.getAnoReferencia() + "." + ma.getPeriodoReferencia());
		
	}
	
	/**
	 * Voltar para a selecao do discente.
	 * @return
	 */
	public String voltar(){
		SelecionaDiscenteMBean MBean = getMBean("selecionaDiscenteMBean");
		return MBean.voltar(); 
	}
	
}
