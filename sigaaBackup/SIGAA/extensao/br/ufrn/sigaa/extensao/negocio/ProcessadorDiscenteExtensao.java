/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2008
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;


/**
 * Processador responsável por realizar operações de:
 * Cadastro do discente no processo seletivo, indicar um novo discente para o plano de trabalho,
 * finalizar discente e validar relatórios dos discentes de extensão. 
 * 
 * @author Ilueny Santos
 *
 */
public class ProcessadorDiscenteExtensao extends AbstractProcessador {

	public Object execute(Movimento dmMov) throws NegocioException, ArqException,	RemoteException {

		CadastroExtensaoMov mov = (CadastroExtensaoMov) dmMov;		
		InscricaoSelecaoExtensaoDao dao = getDAO(InscricaoSelecaoExtensaoDao.class, mov);
		RegistroEntrada registroEntrada = mov.getUsuarioLogado().getRegistroEntrada();
		validate(mov);		

		try {
		
			/**
			 * Gravando o resultado da avaliação realizada pelo docente com todos os interessados
			 * em participar do projeto.
			 * Cadastrando o resultado da prova seletiva.
			 * 
			 */
			if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SELECAO_EXTENSAO) ){ 
				//Cadastrando discentes selecionados
				for( DiscenteExtensao de : mov.getDiscentesExtensao() ){
					if( de.getId() == 0 ){
						dao.create(de);
						DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, registroEntrada );			
					}
				}
				//Atualizando status das inscrições.
				for( InscricaoSelecaoExtensao insc : mov.getInscricoesSelecao() ){
					dao.update(insc);				
				}
			}
	
			/**
			 * Finalizando o discente de extensão.
			 * Retira o discente do plano de trabalho e libera o plano para comportar novo discente.
			 * 
			 */
			if( mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTE_EXTENSAO) ){
				PlanoTrabalhoExtensao pt =(PlanoTrabalhoExtensao) mov.getObjMovimentado();
				if (pt.getDiscenteExtensao() != null){
					DiscenteExtensao de = pt.getDiscenteExtensao();
					de.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.FINALIZADO));
					dao.update(de);
					DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, registroEntrada);
					
					// Depois de finalizado, o discente volta para lista de interessados permitindo que  seja selecionado novamente para o projeto.
					InscricaoSelecaoExtensao inscricao = dao.findByDiscenteAtividade(de.getDiscente().getId(), de.getAtividade().getId());
					if (ValidatorUtil.isNotEmpty(inscricao)){
						dao.updateFields(InscricaoSelecaoExtensao.class, inscricao.getId(), 
								new String[] {"tipoVinculo", "discenteExtensao", "situacaoDiscenteExtensao.id"}, 
								new Object[] {null, null, TipoSituacaoDiscenteExtensao.INSCRITO_PROCESSO_SELETIVO});
					}
					
				}
				//Removendo discente do plano de trabalho
				//OBS: O DiscenteExtensao ainda tem referência para o plano de trabalho do qual fez parte. 
				pt.setDiscenteExtensao(null);
				dao.update(pt);
			}
	
	
			/**
			 * Salva o parecer do Coordenador da ação de extensão e finaliza o discente no plano de trabalho.
			 * Liberando o plano para o próximo discente.
			 * 
			 */
			if( mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_DISCENTE_EXTENSAO) ){
	
				//Salva o parecer do Coordenador
				RelatorioBolsistaExtensao rel =(RelatorioBolsistaExtensao) mov.getObjMovimentado();			
				rel.setRegistroParecer(registroEntrada);
				rel.setDataParecer(new Date());
				dao.update(rel);
	
				//Só finaliza o discente da ação e do plano de trabalho se for relatório final
				if (rel.isRelatorioFinal()) {			    
					if (rel.getDiscenteExtensao() != null){
						DiscenteExtensao de = rel.getDiscenteExtensao();
	
						Date fimPlano = de.getPlanoTrabalhoExtensao().getDataFim();
						if (new Date().after(fimPlano)) {
							de.setDataFim(fimPlano);
						}else {
							de.setDataFim(new Date());
						}
	
						de.setSituacaoDiscenteExtensao( new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.FINALIZADO));
						dao.update(de);	
	
						DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, registroEntrada);
					}
	
					//Removendo discente do plano de trabalho
					//OBS: O DiscenteExtensao ainda tem referência ao plano de trabalho do qual fez parte.
					//mas o plano de trabalho deve estar pronto para receber um novo discente de extensão.
					dao.updateField(PlanoTrabalhoExtensao.class, rel.getDiscenteExtensao().getPlanoTrabalhoExtensao().getId(), "discenteExtensao", null);
	
				}		
			}
	
			/**
			 * Inclui um discente no plano de trabalho vazio.
			 * Caso o plano de trabalho tenha um discente este discente será finalizado e um novo discente será incluído.
			 * 
			 */
			if( mov.getCodMovimento().equals(SigaaListaComando.INDICAR_DISCENTE_EXTENSAO)){
	
				PlanoTrabalhoExtensao pt =(PlanoTrabalhoExtensao) mov.getObjMovimentado();			
	
				if (pt.getDiscenteExtensaoNovo() != null){				
					DiscenteExtensao discenteAntigo = pt.getDiscenteExtensao();
	
					if (discenteAntigo != null){
						discenteAntigo.setSituacaoDiscenteExtensao( new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.FINALIZADO));
						dao.update(discenteAntigo);
	
						DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, discenteAntigo, registroEntrada);		
					}
	
					DiscenteExtensao discenteNovo = pt.getDiscenteExtensaoNovo();
					discenteNovo.setAtivo(true);
					discenteNovo.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.ATIVO));
					discenteNovo.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
					discenteNovo.setDiscenteExtensaoAnterior(discenteAntigo);
					discenteNovo.setPlanoTrabalhoExtensao(pt);
					discenteNovo.setAtividade(pt.getAtividade());
					discenteNovo.setDataFim(pt.getDataFim()); //definindo data fim padrão para discente (data fim do plano de trabalho)
					dao.create(discenteNovo);
	
					DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, discenteNovo, registroEntrada);
					
					// Atualizando a situação da lista de interessados impedindo que o discente seja selecionado novamente para o projeto.
					InscricaoSelecaoExtensao inscricao = dao.findByDiscenteAtividade(discenteNovo.getDiscente().getId(), discenteNovo.getAtividade().getId());
					if (ValidatorUtil.isNotEmpty(inscricao)){
						dao.updateFields(InscricaoSelecaoExtensao.class, inscricao.getId(), 
								new String[] {"tipoVinculo.id", "discenteExtensao.id", "situacaoDiscenteExtensao.id"}, 
								new Object[] {discenteNovo.getTipoVinculo().getId(), discenteNovo.getId(), TipoSituacaoDiscenteExtensao.SELECIONADO});
					}
	
					//Novo discente no plano
					pt.setDiscenteExtensao(discenteNovo);
					dao.update(pt);
				}
			}
		}finally {
			dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

		CadastroExtensaoMov aMov = (CadastroExtensaoMov) mov;
		ListaMensagens lista = new ListaMensagens();

		if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SELECAO_EXTENSAO)){ 
		}
		if( mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTE_EXTENSAO) ){
			PlanoTrabalhoExtensao pt =(PlanoTrabalhoExtensao) aMov.getObjMovimentado();
			PlanoTrabalhoValidator.validaFinalizarDiscente(pt, lista);
		}
		if( mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_DISCENTE_EXTENSAO) ){
			RelatorioBolsistaExtensao rel =(RelatorioBolsistaExtensao) aMov.getObjMovimentado();
			ValidatorUtil.validateRequired(rel.getParecerOrientador(), "Parecer", lista);			
		}
		if( mov.getCodMovimento().equals(SigaaListaComando.INDICAR_DISCENTE_EXTENSAO)){			
			PlanoTrabalhoExtensao pt =(PlanoTrabalhoExtensao) aMov.getObjMovimentado();			
			PlanoTrabalhoValidator.validaIndicarDiscente(pt, lista);			
		}

		checkValidation(lista);

	}

}