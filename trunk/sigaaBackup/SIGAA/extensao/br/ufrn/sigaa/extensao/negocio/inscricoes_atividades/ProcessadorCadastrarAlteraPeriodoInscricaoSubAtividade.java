/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 *
 * <p>Processador EXCLUSIVO  para cadatrar ou alterar um período de inscrição para sub ativiade de cursos e eventos de extensão </p>
 *
 * <p>Contém todas as regras de negócio para abrir e alterar um período de inscrição.</p>
 *
 * <p> <i> Não colocar aqui nada de atividade por favor</i> </p>
 * 
 * 
 * @author jadson
 *
 */
public class ProcessadorCadastrarAlteraPeriodoInscricaoSubAtividade  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade movimento = ( MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade) mov;
		InscricaoAtividade inscricao = movimento.getInscricao();
		
		GenericDAO dao = null;
		
		boolean isAlterando = inscricao.getId() > 0;
		
		try{
			
			dao = getGenericDAO(movimento);
			
			// Se o usuário não informar pode pagar a GRU até o início da mini atividade
			if(inscricao.isCobrancaTaxaMatricula()){
				if(inscricao.getDataVencimentoGRU() == null){
					inscricao.setDataVencimentoGRU( dao.findByPrimaryKey(inscricao.getSubAtividade().getId(), SubAtividadeExtensao.class, "inicio").getInicio() );
				}
			}
			
			if(inscricao.getQuestionario() != null && inscricao.getQuestionario().getId() <=0){
				inscricao.setQuestionario(null);
			}
			
			if(! movimento.isAlterado()) {
				inscricao.setSequencia(dao.getNextSeq("extensao", "inscricao_sequence"));
			}
			
			dao.createOrUpdate(inscricao);
			
			// Atualiza o relacionamento com a modalidade de participante //
			if(inscricao.isCobrancaTaxaMatricula() ){
				
				List<Integer> idsModalidadesAtividas = new ArrayList<Integer>();
				
				for(ModalidadeParticipantePeriodoInscricaoAtividade modalidade : inscricao.getModalidadesParticipantes()){
					dao.createOrUpdate(modalidade);
				}
				
				for(ModalidadeParticipantePeriodoInscricaoAtividade modalidade : inscricao.getModalidadesParticipantes()){
					if(modalidade.getId() > 0 ){
						idsModalidadesAtividas.add(modalidade.getId());
					}
				}
				if(isAlterando)
					desativaModalidadesRemovidas(inscricao, idsModalidadesAtividas, dao);
			}
			
		}finally{	
			if(dao != null) dao.close();
		}

		
		return null;
	}

	/**
	 * Desativa as modalidades removiadas pelo usuári.
	 *  
	 * @param inscricao
	 * @param dao
	 * @throws DAOException
	 */
	private void desativaModalidadesRemovidas(InscricaoAtividade inscricao, List<Integer> idsModalidadesAtividas, GenericDAO dao) throws DAOException {
		
		
		if(inscricao.getId() > 0 ){
		
			if(idsModalidadesAtividas.size() > 0){
				// Desativa aqueles removidades pelo coordenador, ou seja, que não estão mais da lista //
				dao.update("  UPDATE extensao.modalidade_participante_periodo_inscricao_atividade " +
				" SET ativo = FALSE WHERE id_inscricao_atividade =  "+inscricao.getId() +
				" AND id_modalidade_participante_periodo_inscricao_atividade NOT IN "+UFRNUtils.gerarStringIn(idsModalidadesAtividas)+" " );
			}else{
				// Desativa aqueles removidades pelo coordenador, ou seja, que não estão mais da lista //
				dao.update("  UPDATE extensao.modalidade_participante_periodo_inscricao_atividade " +
						" SET ativo = FALSE WHERE id_inscricao_atividade =  "+inscricao.getId());
			}
		}
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		
		MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade movimento = ( MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade) mov;
		InscricaoAtividade inscricao = movimento.getInscricao();
		AtividadeExtensao atividade = movimento.getAtividade();
		
		if( ! movimento.isCoordenadorExtensao()){
			lista.addErro("O vínculo utilizando atualmente não é um vínculo de coordenador de extensão.");
		}
		
		lista.addAll(inscricao.validate());
		
		if(inscricao.getSubAtividade() == null){
			lista.addErro("A inscrição não está associada com uma mini atividade");
		}
		
		if(inscricao.getAtividade() != null){
			lista.addErro("A inscrição não pode está associada com uma atividade");
		}
		
		checkValidation(lista); // se tiver retorna logo daqui.
		
		InscricaoAtividadeExtensaoDao daoInscricaoAtividade = null;
		InscricaoSubAtividadeExtensaoDao daoInscricaoSubAtividade  = null;
		InscricaoParticipanteAtividadeExtensaoDao daoInscricaoParticipante = null;
		
		try{
			
			daoInscricaoAtividade = getDAO(InscricaoAtividadeExtensaoDao.class, movimento);
			daoInscricaoSubAtividade = getDAO(InscricaoSubAtividadeExtensaoDao.class, movimento);
			
			daoInscricaoParticipante = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class, movimento);
			
			// Valida o período de termino das inscrições, tem que ser até o fim da atividade 
			if( inscricao.getPeriodoFim() != null 
					&& CalendarUtils.configuraTempoDaData(inscricao.getPeriodoFim(), 0, 0, 0, 0).after(
							CalendarUtils.configuraTempoDaData( atividade.getDataFim(), 0, 0, 0, 0) ) ){
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				lista.addErro("Esse projeto finaliza dia: "+format.format(atividade.getDataFim()));
				lista.addErro("Não é possível abrir um período de inscrição após o término do projeto.");
			}
			
			
			/*
			 *  Nova regra de negócio 
			 *  
			 *  Verifica se o novo período de inscrição não está dentro de algum período já existente.
			 *  
			 *  Se não existe um com o mesmo período pode abrir uma nova inscrição. 
			 *  A idéia é ter vários períodos de inscrição com datas diferentes e valores deferentes.
			 *  
			 *  De 01/11/2012 a  30/11/2012  R$ 100,000
			 *  De 01/12/2012 a  31/12/2012  R$ 200,000    
			 *  
			 *  Na área pública vai sempre pegar o período atual.
			 */
			List<InscricaoAtividade> inscricaoesSubAtividadeExistetnes = daoInscricaoSubAtividade.findInscricoesSubAtividades(inscricao.getSubAtividade().getId());
			
			for (InscricaoAtividade inscricaoAtividadeExistente : inscricaoesSubAtividadeExistetnes) {
				
				if( inscricaoAtividadeExistente.getId() != inscricao.getId()  // se não é ela mesma
						&& CalendarUtils.isIntervalosDeDatasConflitantes(inscricaoAtividadeExistente.getPeriodoInicio(), inscricaoAtividadeExistente.getPeriodoFim(), inscricao.getPeriodoInicio(), inscricao.getPeriodoFim())
						   ){
				
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");	
					lista.addErro("Já existe período de inscrição entre as datas: "+format.format(inscricaoAtividadeExistente.getPeriodoInicio())+" e "+format.format(inscricaoAtividadeExistente.getPeriodoFim()));
					lista.addErro("Só é possível abrir mais de um período de inscrição se as data não coincidirem.");
				}
			}
			
			
			
			/// Verifica na abertura da inscrição se o projeto já tem unidade orçamentária, para já bloquear e evitar problemas futuros  ///
			if(inscricao.isCobrancaTaxaMatricula() ){
				
				Projeto projetoBanco = daoInscricaoAtividade.findByPrimaryKey(atividade.getProjeto().getId(), Projeto.class, "unidadeOrcamentaria.id");
				
				if(projetoBanco.getUnidadeOrcamentaria() == null || projetoBanco.getUnidadeOrcamentaria().getId() <= 0 ){
					lista.addErro("Não é possível abrir um período de inscrição com cobrança de taxa de matrícula, pois o projeto ainda não possui unidade orçamentária.");
					lista.addErro("Solicite a criação de uma unidade orçamentária para o seu projeto.");
				}
				daoInscricaoAtividade.detach(projetoBanco);
			}
			
			
			
			
			// Verifica se a quantidade de vagas das sub atividades não ultrapassa a quantidade de vagas da atividade pai //
			
			int totalVagasAbertasAtividade = daoInscricaoAtividade.countQtdVagasPeriodoInscricaoAtividade(atividade.getId());
			
			if(totalVagasAbertasAtividade == 0){
				lista.addErro("Não existe período de inscrição para a atividade \""+atividade.getTitulo()
						+"\". Para abrir um período de inscrição para a mini atividade é preciso primeiro abrir inscrição para a atividade.");
			}else{
			
				if(totalVagasAbertasAtividade <  ( inscricao.getQuantidadeVagas() ) ){
					lista.addErro("A quantidade de vagas para a mini atividade ultrapassa a quantidade de vagas abertas para a sua atividade: "+totalVagasAbertasAtividade+".");
				}
				
			}
			
			///// Se está alterando /////
			if( movimento.isAlterado() ){
				
				int quantidadeInscritos = daoInscricaoParticipante.countQtdInscricoesParticipanteNoPeriodo(inscricao.getId());
				
				if(inscricao.getQuantidadeVagas() < quantidadeInscritos){
					lista.addErro("A quantidade de vagas para a mini atividade é menor que a quantidade de inscritos: "+quantidadeInscritos+" inscrições realizadas até o momento.");
				}
			}
			
		}finally{	
			if(daoInscricaoAtividade != null) daoInscricaoAtividade.close();
			if(daoInscricaoSubAtividade != null) daoInscricaoSubAtividade.close();
			if(daoInscricaoParticipante != null) daoInscricaoParticipante.close();
			
			
		}	
		
		
		
		checkValidation(lista);
		
	}

	

}
