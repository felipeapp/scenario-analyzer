/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Processador EXCLUSIVO  para cadatrar ou alterar um per�odo de inscri��o para sub ativiade de cursos e eventos de extens�o </p>
 *
 * <p>Cont�m todas as regras de neg�cio para abrir e alterar um per�odo de inscri��o.</p>
 *
 * <p> <i> N�o colocar aqui nada de atividade por favor</i> </p>
 * 
 * 
 * @author jadson
 *
 */
public class ProcessadorCadastrarAlteraPeriodoInscricaoSubAtividade  extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
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
			
			// Se o usu�rio n�o informar pode pagar a GRU at� o in�cio da mini atividade
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
	 * Desativa as modalidades removiadas pelo usu�ri.
	 *  
	 * @param inscricao
	 * @param dao
	 * @throws DAOException
	 */
	private void desativaModalidadesRemovidas(InscricaoAtividade inscricao, List<Integer> idsModalidadesAtividas, GenericDAO dao) throws DAOException {
		
		
		if(inscricao.getId() > 0 ){
		
			if(idsModalidadesAtividas.size() > 0){
				// Desativa aqueles removidades pelo coordenador, ou seja, que n�o est�o mais da lista //
				dao.update("  UPDATE extensao.modalidade_participante_periodo_inscricao_atividade " +
				" SET ativo = FALSE WHERE id_inscricao_atividade =  "+inscricao.getId() +
				" AND id_modalidade_participante_periodo_inscricao_atividade NOT IN "+UFRNUtils.gerarStringIn(idsModalidadesAtividas)+" " );
			}else{
				// Desativa aqueles removidades pelo coordenador, ou seja, que n�o est�o mais da lista //
				dao.update("  UPDATE extensao.modalidade_participante_periodo_inscricao_atividade " +
						" SET ativo = FALSE WHERE id_inscricao_atividade =  "+inscricao.getId());
			}
		}
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
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
			lista.addErro("O v�nculo utilizando atualmente n�o � um v�nculo de coordenador de extens�o.");
		}
		
		lista.addAll(inscricao.validate());
		
		if(inscricao.getSubAtividade() == null){
			lista.addErro("A inscri��o n�o est� associada com uma mini atividade");
		}
		
		if(inscricao.getAtividade() != null){
			lista.addErro("A inscri��o n�o pode est� associada com uma atividade");
		}
		
		checkValidation(lista); // se tiver retorna logo daqui.
		
		InscricaoAtividadeExtensaoDao daoInscricaoAtividade = null;
		InscricaoSubAtividadeExtensaoDao daoInscricaoSubAtividade  = null;
		InscricaoParticipanteAtividadeExtensaoDao daoInscricaoParticipante = null;
		
		try{
			
			daoInscricaoAtividade = getDAO(InscricaoAtividadeExtensaoDao.class, movimento);
			daoInscricaoSubAtividade = getDAO(InscricaoSubAtividadeExtensaoDao.class, movimento);
			
			daoInscricaoParticipante = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class, movimento);
			
			// Valida o per�odo de termino das inscri��es, tem que ser at� o fim da atividade 
			if( inscricao.getPeriodoFim() != null 
					&& CalendarUtils.configuraTempoDaData(inscricao.getPeriodoFim(), 0, 0, 0, 0).after(
							CalendarUtils.configuraTempoDaData( atividade.getDataFim(), 0, 0, 0, 0) ) ){
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				lista.addErro("Esse projeto finaliza dia: "+format.format(atividade.getDataFim()));
				lista.addErro("N�o � poss�vel abrir um per�odo de inscri��o ap�s o t�rmino do projeto.");
			}
			
			
			/*
			 *  Nova regra de neg�cio 
			 *  
			 *  Verifica se o novo per�odo de inscri��o n�o est� dentro de algum per�odo j� existente.
			 *  
			 *  Se n�o existe um com o mesmo per�odo pode abrir uma nova inscri��o. 
			 *  A id�ia � ter v�rios per�odos de inscri��o com datas diferentes e valores deferentes.
			 *  
			 *  De 01/11/2012 a  30/11/2012  R$ 100,000
			 *  De 01/12/2012 a  31/12/2012  R$ 200,000    
			 *  
			 *  Na �rea p�blica vai sempre pegar o per�odo atual.
			 */
			List<InscricaoAtividade> inscricaoesSubAtividadeExistetnes = daoInscricaoSubAtividade.findInscricoesSubAtividades(inscricao.getSubAtividade().getId());
			
			for (InscricaoAtividade inscricaoAtividadeExistente : inscricaoesSubAtividadeExistetnes) {
				
				if( inscricaoAtividadeExistente.getId() != inscricao.getId()  // se n�o � ela mesma
						&& CalendarUtils.isIntervalosDeDatasConflitantes(inscricaoAtividadeExistente.getPeriodoInicio(), inscricaoAtividadeExistente.getPeriodoFim(), inscricao.getPeriodoInicio(), inscricao.getPeriodoFim())
						   ){
				
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");	
					lista.addErro("J� existe per�odo de inscri��o entre as datas: "+format.format(inscricaoAtividadeExistente.getPeriodoInicio())+" e "+format.format(inscricaoAtividadeExistente.getPeriodoFim()));
					lista.addErro("S� � poss�vel abrir mais de um per�odo de inscri��o se as data n�o coincidirem.");
				}
			}
			
			
			
			/// Verifica na abertura da inscri��o se o projeto j� tem unidade or�ament�ria, para j� bloquear e evitar problemas futuros  ///
			if(inscricao.isCobrancaTaxaMatricula() ){
				
				Projeto projetoBanco = daoInscricaoAtividade.findByPrimaryKey(atividade.getProjeto().getId(), Projeto.class, "unidadeOrcamentaria.id");
				
				if(projetoBanco.getUnidadeOrcamentaria() == null || projetoBanco.getUnidadeOrcamentaria().getId() <= 0 ){
					lista.addErro("N�o � poss�vel abrir um per�odo de inscri��o com cobran�a de taxa de matr�cula, pois o projeto ainda n�o possui unidade or�ament�ria.");
					lista.addErro("Solicite a cria��o de uma unidade or�ament�ria para o seu projeto.");
				}
				daoInscricaoAtividade.detach(projetoBanco);
			}
			
			
			
			
			// Verifica se a quantidade de vagas das sub atividades n�o ultrapassa a quantidade de vagas da atividade pai //
			
			int totalVagasAbertasAtividade = daoInscricaoAtividade.countQtdVagasPeriodoInscricaoAtividade(atividade.getId());
			
			if(totalVagasAbertasAtividade == 0){
				lista.addErro("N�o existe per�odo de inscri��o para a atividade \""+atividade.getTitulo()
						+"\". Para abrir um per�odo de inscri��o para a mini atividade � preciso primeiro abrir inscri��o para a atividade.");
			}else{
			
				if(totalVagasAbertasAtividade <  ( inscricao.getQuantidadeVagas() ) ){
					lista.addErro("A quantidade de vagas para a mini atividade ultrapassa a quantidade de vagas abertas para a sua atividade: "+totalVagasAbertasAtividade+".");
				}
				
			}
			
			///// Se est� alterando /////
			if( movimento.isAlterado() ){
				
				int quantidadeInscritos = daoInscricaoParticipante.countQtdInscricoesParticipanteNoPeriodo(inscricao.getId());
				
				if(inscricao.getQuantidadeVagas() < quantidadeInscritos){
					lista.addErro("A quantidade de vagas para a mini atividade � menor que a quantidade de inscritos: "+quantidadeInscritos+" inscri��es realizadas at� o momento.");
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
