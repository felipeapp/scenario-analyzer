package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.apedagogica.dao.ParticipanteAtividadeAPDAO;
import br.ufrn.sigaa.apedagogica.dao.RegistroParticipacaoAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.RegistroParticipacaoAtualizacaoPedagogica;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe responsável em definir as atividades do pap para um determinado docente.
 * @author mario
 *
 */
public class MapperProgramaAtualizacaoPedagogica extends AbstractMapperAtividade {

	/** Define o periodo inicial da consulta das atividades */
	private static final int PERIODO_INICIAL = 1;
	/** Define o periodo final da consulta das atividades */
	private static final int PERIODO_FINAL = 2;
	
	/**
	 * Busca todas atividades do PAP para o docente em determinado ano.
	 */
	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade)
			throws DAOException {
		
		ParticipanteAtividadeAPDAO participanteDAO = new ParticipanteAtividadeAPDAO();
		
		RegistroParticipacaoAtualizacaoPedagogicaDAO registroDAO = 
			new RegistroParticipacaoAtualizacaoPedagogicaDAO();
		
		Collection<ParticipanteAtividadeAtualizacaoPedagogica> participacoesDocente =
			new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>();
		try{
			participacoesDocente.addAll( participanteDAO.findByAnoDocente(ano, docente.getId() ) );
			Collection<RegistroParticipacaoAtualizacaoPedagogica> registrosDocente =  
			 	registroDAO.findGeral(docente.getId(), null, ano, PERIODO_INICIAL, ano, PERIODO_FINAL);
		
			for (RegistroParticipacaoAtualizacaoPedagogica r : registrosDocente) {
				 participacoesDocente.add( r.toParticipacao() );
			}
			
			return participacoesDocente;
			
		}finally{
			participanteDAO.close();
			registroDAO.close();
		}
	}
	
}