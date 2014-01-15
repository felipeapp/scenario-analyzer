/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/10/2011
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoCancelamentoAutomatico;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador utilizado no jubilamento de discentes.
 *
 * @author Igor Linnik
 *
 */
public class ProcessadorJubilamento extends AbstractProcessador {
	
	
	/** Executa o cancelamento do discente
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		MovimentoCancelamentoAutomatico movCanc = (MovimentoCancelamentoAutomatico) mov;
		for ( Discente d : movCanc.getDiscentes() ) {
			
			CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(d);
			MovimentacaoAluno afastamento = new MovimentacaoAluno();
			afastamento.setDataOcorrencia(new Date());
			afastamento.setAtivo(true);
			afastamento.setDiscente(d);
			afastamento.setTipoMovimentacaoAluno(movCanc.getTipoMovimentacao());
			afastamento.setAnoReferencia(movCanc.getAno());
			afastamento.setPeriodoReferencia(movCanc.getPeriodo());
			if (ValidatorUtil.isEmpty(movCanc.getObservacoes()))
				afastamento.setObservacao("Cancelamento coletivo realizado em " + Formatador.getInstance().formatarData(new Date()));
			else
				afastamento.setObservacao(movCanc.getObservacoes());
			afastamento.setAnoOcorrencia(calendario.getAno());
			afastamento.setPeriodoOcorrencia(calendario.getPeriodo());	
			
			MovimentoMovimentacaoAluno movProc = new MovimentoMovimentacaoAluno();
			movProc.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
			movProc.setObjMovimentado(afastamento);
			movProc.setApplicationContext(movCanc.getApplicationContext());
			movProc.setSistema(movCanc.getSistema());
			movProc.setUsuarioLogado(movCanc.getUsuarioLogado());
			
			ProcessadorMovimentacaoAluno procMov = new ProcessadorMovimentacaoAluno();
			procMov.execute(movProc);
			if (movCanc.getTipoMovimentacao().getId() == TipoMovimentacaoAluno.NAO_CONFIRMACAO_VINCULO)
				DiscenteHelper.alterarStatusDiscente(d, StatusDiscente.EXCLUIDO, mov, getGenericDAO(mov));
		}
		
		return null;
	}

	
	/** Valida os dados do cancelamento do discente
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoCancelamentoAutomatico movCancelamento = (MovimentoCancelamentoAutomatico) movimento;
		StringBuffer discentesPendentes = new StringBuffer();
		
		if( !movCancelamento.isIgnorarPendencias() ){
			for ( Discente discente : movCancelamento.getDiscentes() ) {
				
				ListaMensagens erros =  VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(discente);
				
				if(erros.isErrorPresent())
					for (MensagemAviso mensagem : erros.getErrorMessages()) {
						discentesPendentes.append(mensagem.getMensagem());
					}
					
			}
		}
		
		if (discentesPendentes.length() > 0) throw new NegocioException(discentesPendentes.toString());
	}

}
