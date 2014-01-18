/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2008
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dao.AndamentoObjetivoDao;
import br.ufrn.sigaa.extensao.dominio.AndamentoAtividade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.relatorio.dominio.ArquivoRelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioCursoEvento;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProdutoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProgramaExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProjetoExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Processador para realizar cadastro de relatórios finais e parciais de
 * extensão faz processamento de relatórios de projetos, cursos e eventos de
 * extensão
 * 
 * 
 * @author ilueny santos
 * 
 */
public class ProcessadorRelatorioExtensao extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_RELATORIO_ACAO_EXTENSAO)) {
			remover(mov);
		}

		if (mov.getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR_EXTENSAO)){
			devolverParaCoordenacao(mov);
		}

		if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO)) {
			salvar(mov);
		}

		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_EXTENSAO)
				|| mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_CURSO_EVENTO_EXTENSAO)
				|| mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PRODUTO_EXTENSAO)
				|| mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PROGRAMA_EXTENSAO)) {

			salvar(mov);
			enviar(mov);
		}

		if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_DEPARTAMENTO)) {
			validarDepartamento(mov);
		}

		if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_PROEX)) {
			validarProReitoria(mov);
		}

		return relatorio;
	}

	/**
	 * Avaliações realizadas pela chefia do departamento da unidade proponente do 
	 * projeto e pela pró-reitoria de extensão.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void validarProReitoria(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {

		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);

		try {	
			relatorio.setDataValidacaoProex(new Date());
			relatorio.setRegistroEntradaProex(mov.getUsuarioLogado().getRegistroEntrada());
			dao.update(relatorio);

			/* @negocio: Ações não realizadas devem passar para situação de 'cancelada' para que não aja cobrança de relatórios ou penalidades sejam aplicadas. */
			if (relatorio.isAcaoNaoRealizada()) {

				//Cancelando a ação de extensão, membros da equipe, etc.
				AtividadeExtensao atividade = dao.findByPrimaryKey(relatorio.getAtividade().getId(), AtividadeExtensao.class);
				atividade.setProjeto(getGenericDAO(mov).findByPrimaryKey(atividade.getProjeto().getId(), Projeto.class));

				ProcessadorAtividadeExtensao processador = new ProcessadorAtividadeExtensao();
				CadastroExtensaoMov mov2 = new CadastroExtensaoMov();
				mov2.setCodMovimento(SigaaListaComando.NAO_EXECUTAR_ATIVIDADE_EXTENSAO);
				mov2.setUsuarioLogado(mov.getUsuarioLogado());
				mov2.setSistema(mov.getSistema());
				mov2.setAtividade(atividade);
				processador.execute(mov2);

			/* @negocio: Torna o relatório editável novamente. Permite que o que o coordenador faça as alterações sugeridas e reenvie o relatório. */
			} else if (relatorio.isReprovadoProex() || relatorio.isAprovadoComRecomendacoesProex()) {
				dao.updateField(RelatorioAcaoExtensao.class, relatorio.getId(), "dataEnvio", null);


			} else if (relatorio.isAprovadoProex()) {

				/* @negocio: Se é relatório final e todas as instâncias o aprovaram, então a ação deverá ser concluída. */
				if (relatorio.isRelatorioFinal() && relatorio.isAprovadoDepartamento()) {

					//Evitar erro de lazy
					AtividadeExtensao atv = new AtividadeExtensao();
					atv = dao.findByPrimaryKey(relatorio.getAtividade().getId(), AtividadeExtensao.class);
					atv.getMembrosEquipe().iterator();
					atv.getPlanosTrabalho().iterator();
					atv.getDiscentesSelecionados().iterator();

					// tem que retirar da seção antes de chamar o outro processador, senão dá problema de dois objetos na sessão 
					// quando a atividade tem discentes de extensão e dois discentes possuem o mesmo plano de trabalho
					dao.detach(atv);
					
					//concluindo a ação de extensão, finalizando os membros da equipe, etc.
					ProcessadorAtividadeExtensao procAtividade = new ProcessadorAtividadeExtensao();
					CadastroExtensaoMov movEx = new CadastroExtensaoMov();				
					movEx.setCodMovimento(SigaaListaComando.CONCLUIR_ATIVIDADE_EXTENSAO);
					movEx.setUsuarioLogado(mov.getUsuarioLogado());
					movEx.setAtividade(atv);
					movEx.setSistema( mov.getSistema() );
					procAtividade.execute(movEx);
				}

			}
		}finally {
			dao.close();
		}		
	}

	
	/**
	 * Validação realizada pela chefia do departamento da unidade proponente da ação de extensão.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void validarDepartamento(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		
		try {		
			relatorio.setDataValidacaoDepartamento(new Date());
			relatorio.setRegistroEntradaDepartamento(mov.getRegistroEntrada());
	
			/* @negocio: torna o relatório editável novamente permitindo que o que o coordenador altere conforme as recomendações e reenvie o relatório. */				
			if (relatorio.isReprovadoDepartamento() || relatorio.isAprovadoComRecomendacoesDepartamento()) {
				relatorio.setDataEnvio(null);
			}			

			dao.update(relatorio);
			
		}finally {
			dao.close();
		}		
	}

	
	/**
	 * Salva o relatório.
	 * 
	 * @param mov
	 * @param dao
	 * @param relatorio
	 * @throws DAOException
	 */
	private void salvar(MovimentoCadastro mov) throws NegocioException, ArqException {
		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);

		try {		
			
			dao.createOrUpdate(relatorio);
			
			Date dateLimite = ParametroHelper.getInstance()
					.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
			
			// Arquivos
			if (relatorio.getArquivos() != null) {
				for (ArquivoRelatorioAcaoExtensao ar : relatorio.getArquivos()) {
					dao.createOrUpdate(ar);
				}
			}
			
			if ( relatorio.getAtividade().getProjeto().getDataCadastro().before(dateLimite) ) {
				// Detalhamento do recursos
				if (relatorio.getDetalhamentoRecursos() != null) {
					for (DetalhamentoRecursos det : relatorio.getDetalhamentoRecursos()) {
						dao.createOrUpdate(det);
					}
				}		
	
			} else {
				if ( relatorio.getAndamento() != null ) {
					for (AndamentoAtividade andamento : relatorio.getAndamento()) {
						if (andamento != null && ValidatorUtil.isNotEmpty(andamento.getAtividade())) {
							dao.createOrUpdate(andamento);
							if ( andamento.getAtividade().getObjetivo().getObservacaoExecucao() != null &&
									!andamento.getAtividade().getObjetivo().getObservacaoExecucao().equals("")) {
								dao.updateField(Objetivo.class, andamento.getAtividade().getObjetivo().getId(), 
										"observacaoExecucao", andamento.getAtividade().getObjetivo().getObservacaoExecucao());
							}
						}
					}
				}
			}
			
		}finally {
			dao.close();
		}
	}
		

	/**
	 * Quando envia o relatório atualiza os dados na ação de extensão...
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */	
	private void enviar(MovimentoCadastro mov) throws NegocioException, ArqException {
		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);

		try {		

			AtividadeExtensao atividade = dao.findByPrimaryKey(relatorio.getAtividade().getId(), AtividadeExtensao.class);

			// atualizando orçamento utilizado no projeto....
			for (OrcamentoConsolidado orc : atividade.getOrcamentosConsolidados()) {
				if (  relatorio.getDetalhamentoRecursos() != null ) {
					for (DetalhamentoRecursos recursoUtilizado : relatorio.getDetalhamentoRecursos()) {
						if (recursoUtilizado.getElemento().getId() == orc.getElementoDespesa().getId()) {
							dao.updateFields(OrcamentoConsolidado.class, orc.getId(), 
									new String[] {"fundacaoUtilizado", "fundoUtilizado", "outrosUtilizado"}, 
									new Object[] {recursoUtilizado.getFunpec(), recursoUtilizado.getFaex(), recursoUtilizado.getOutros()});
	
							// gravando o total gasto no detalhamento da despesa...
							dao.createOrUpdate(recursoUtilizado);						
							break;
						}
					}
				}
			}

			// atualizando o publico atendido da ação de extensão
			dao.updateField(AtividadeExtensao.class, atividade.getId(),	"publicoAtendido", relatorio.getPublicoRealAtingido());

			//atualizando total de concluíntes do curso ou evento
			if(atividade.isTipoCurso() || atividade.isTipoEvento()) {				
				dao.updateField(CursoEventoExtensao.class, atividade.getCursoEventoExtensao().getId(), 
						"numeroConcluintes", ((RelatorioCursoEvento)relatorio).getNumeroConcluintes());
			}
			
			//Ajustes no relatório para que possa ser re-validado pelo chefe e pela pró-reitoria.
			relatorio.setDataValidacaoDepartamento(null);
			relatorio.setParecerDepartamento(null);
			relatorio.setTipoParecerDepartamento(null);
			relatorio.setRegistroEntradaDepartamento(null);

			relatorio.setDataValidacaoProex(null);
			relatorio.setTipoParecerProex(null);
			relatorio.setParecerProex(null);
			relatorio.setRegistroEntradaProex(null);
			relatorio.setAtivo(true);
			
			dao.update(relatorio);			    			
			

		}finally {
			dao.close();
		}
	}

	/**
	 * O relatório do projeto pode ser devolvido para que o coordenador altere e reenvie em caso de erro.
	 * 	
	 * @param dao
	 * @param relatorio
	 * @throws DAOException
	 */
	private void devolverParaCoordenacao(MovimentoCadastro mov) throws NegocioException, ArqException {
		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {		
			relatorio.setDataValidacaoDepartamento(null);
			relatorio.setParecerDepartamento(null);
			relatorio.setTipoParecerDepartamento(null);
			relatorio.setRegistroEntradaDepartamento(null);

			relatorio.setDataValidacaoProex(null);
			relatorio.setTipoParecerProex(null);
			relatorio.setParecerProex(null);
			relatorio.setRegistroEntradaProex(null);

			relatorio.setDataEnvio(null);
			relatorio.setAtivo(true);
			
			dao.update(relatorio);			    
		}finally {
			dao.close();
		}
	}

	/**
	 * Remove um relatório de ação de extensão.
	 */
	private void remover(MovimentoCadastro mov) throws NegocioException, ArqException {		
		RelatorioAcaoExtensao relatorio = (RelatorioAcaoExtensao) mov.getObjMovimentado();
		AndamentoObjetivoDao dao = getDAO(AndamentoObjetivoDao.class, mov);
	
		try {
			
			relatorio = dao.findByPrimaryKey(relatorio.getId(), RelatorioAcaoExtensao.class);
			
			dao.updateField(RelatorioAcaoExtensao.class, relatorio.getId(), "ativo", false);

			// Arquivos
			if (relatorio.getArquivos() != null) {
				for (ArquivoRelatorioAcaoExtensao ar : relatorio.getArquivos()) {
					if (ar.getId() != 0) {
						dao.updateField(ArquivoRelatorioAcaoExtensao.class, ar.getId(), "ativo", false);
					}
				}
			}
			
			relatorio.setAndamento( dao.findAndamentoAtividades(relatorio.getAtividade().getId(), relatorio.getTipoRelatorio().getId(), false) );
			if ( relatorio.getAndamento() != null ) {
				for (AndamentoAtividade andamento : relatorio.getAndamento()) {
					dao.updateField(AndamentoAtividade.class, andamento.getId(), "ativo", false);
				}
			}
			
		}finally {
			dao.close();
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro aMov = (MovimentoCadastro) mov;
		
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
		}
		
		ListaMensagens lista = new ListaMensagens();
		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_EXTENSAO)) {
			RelatorioProjetoExtensao relatorio = aMov.getObjMovimentado();
			RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(relatorio, lista);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_CURSO_EVENTO_EXTENSAO)) {
			RelatorioCursoEvento relatorio = aMov.getObjMovimentado();
			RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(relatorio, lista);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PRODUTO_EXTENSAO)) {
			RelatorioProdutoExtensao relatorio = aMov.getObjMovimentado();
			RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(relatorio, lista);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PROGRAMA_EXTENSAO)) {
			RelatorioProgramaExtensao relatorio = aMov.getObjMovimentado();
			RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(relatorio, lista);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_DEPARTAMENTO)) {
			RelatorioAcaoExtensao relatorio = aMov.getObjMovimentado();
			ValidatorUtil.validateRequired(relatorio.getTipoParecerDepartamento(), "Parecer", lista);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_PROEX)) {
			RelatorioAcaoExtensao relatorio = aMov.getObjMovimentado();
			ValidatorUtil.validateRequired(relatorio.getTipoParecerProex(), "Parecer", lista);
		}

		checkValidation(lista);
	}

}