/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/08/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * <p>Dao exclusivos para a consultas que envolvem sub atividades de extensão. </p>
 * 
 * @author jadson
 *
 */
public class SubAtividadeExtensaoDao extends GenericSigaaDAO{
	
	
	/**
	 * Retorna as informações da sub atividade de extensão que podem ser alteradas.
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public SubAtividadeExtensao findInformacoesAlteracaoSubAtividadeExtensao(int idSubAtividade)throws DAOException {
		
		String projecao = " subAtv.id, subAtv.titulo, subAtv.descricao, subAtv.local, subAtv.inicio, subAtv.fim, " +
				" subAtv.horario, subAtv.cargaHoraria, subAtv.numeroVagas, subAtv.tipoSubAtividadeExtensao.id, subAtv.tipoSubAtividadeExtensao.descricao, " +
				" atv.id, atv.tipoAtividadeExtensao, atv.sequencia," +
				" projeto.id, projeto.ano, projeto.titulo, projeto.dataInicio, projeto.dataFim, " +
				" pessoa.id, pessoa.nome as nomeCoordenador, coordenador.funcaoMembro.id, coordenador.ativo, coordenador.dataInicio, coordenador.dataFim ";

		
		String hql =
		" SELECT " + projecao + "" +
		" FROM SubAtividadeExtensao subAtv " +
		" INNER JOIN subAtv.atividade atv "+
		" INNER JOIN atv.projeto projeto "+
		" INNER JOIN projeto.coordenador coordenador "+      // Para verifiar se o usuário pode alterar a sub atividade, só se ele for coordenador
		" INNER JOIN coordenador.pessoa pessoa "+
		" WHERE subAtv.ativo = :true AND atv.ativo = :true "+
		" AND subAtv.id = :idSubAtividade ";			
				
			
		Query query = getSession().createQuery(hql);
		query.setInteger("idSubAtividade", idSubAtividade);
		query.setBoolean("true", true);
		

		Object[] dadosSubAtividade = (Object[]) query.uniqueResult();
			
		SubAtividadeExtensao retorno = new SubAtividadeExtensao();
		
		if(dadosSubAtividade != null) {
		
			int col = 0;
			
			retorno.setId((Integer) dadosSubAtividade[col++]);
			retorno.setTitulo((String) dadosSubAtividade[col++]);
			retorno.setDescricao((String) dadosSubAtividade[col++]);
			retorno.setLocal((String) dadosSubAtividade[col++]);
			retorno.setInicio((Date) dadosSubAtividade[col++]);
			retorno.setFim((Date) dadosSubAtividade[col++]);
			retorno.setHorario((String) dadosSubAtividade[col++]);
			retorno.setCargaHoraria((Integer) dadosSubAtividade[col++]);
			retorno.setNumeroVagas((Integer) dadosSubAtividade[col++]);
			retorno.setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao((Integer) dadosSubAtividade[col++], (String) dadosSubAtividade[col++]));
			retorno.setAtivo(true);
			
			retorno.setAtividade(new AtividadeExtensao((Integer) dadosSubAtividade[col++] )  );
			retorno.getAtividade().setTipoAtividadeExtensao((TipoAtividadeExtensao) dadosSubAtividade[col++] );
			retorno.getAtividade().setSequencia((Integer) dadosSubAtividade[col++] );
			
			retorno.getAtividade().setProjeto( new Projeto( (Integer) dadosSubAtividade[col++] )  );
			retorno.getAtividade().getProjeto().setAno((Integer) dadosSubAtividade[col++] );
			retorno.getAtividade().getProjeto().setTitulo((String) dadosSubAtividade[col++] );
			retorno.getAtividade().getProjeto().setDataInicio((Date) dadosSubAtividade[col++] );
			retorno.getAtividade().getProjeto().setDataFim((Date) dadosSubAtividade[col++] );
			
			retorno.getAtividade().getProjeto().setCoordenador( new MembroProjeto());
			
			// Dados para saber se o coordenador pode alterar a mini atividade //
			retorno.getAtividade().getProjeto().getCoordenador().setPessoa( new Pessoa((Integer) dadosSubAtividade[col++], (String) dadosSubAtividade[col++]));
			retorno.getAtividade().getProjeto().getCoordenador().setFuncaoMembro( new FuncaoMembro((Integer) dadosSubAtividade[col++]));
	
			retorno.getAtividade().getProjeto().getCoordenador().setAtivo((Boolean) dadosSubAtividade[col++]);
			retorno.getAtividade().getProjeto().getCoordenador().setDataInicio((Date) dadosSubAtividade[col++] );
			retorno.getAtividade().getProjeto().getCoordenador().setDataFim( (Date) dadosSubAtividade[col++]);
			
			retorno.getAtividade().getProjeto().getCoordenador().setProjeto(retorno.getAtividade().getProjeto());
		
		}
		
		return retorno;
		
	}
	
	
	
	
	/**
	 * Retorna as inscrições em sub atividades de extensão. Essas inscrições precisam ser desativadas se a mini atividade for removida.
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public List<InscricaoAtividadeParticipante> findInscritosSubAtividadeExtensao(int idSubAtividade)throws DAOException {
		
		String projecao = " participante.id, cadastroParticipante.nome, cadastroParticipante.email, cadastroParticipante.dataNascimento," +
				" inscricaoAtividade.id, " +
				" participante.statusInscricao.id, participante.statusInscricao.descricao ";

		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  InscricaoAtividadeParticipante participante " +
				" INNER JOIN  participante.inscricaoAtividade inscricaoAtividade " +
				" INNER JOIN  participante.cadastroParticipante cadastroParticipante " +
				" INNER JOIN  inscricaoAtividade.subAtividade subAtividade " +
				" INNER JOIN  subAtividade.atividade atividade " +
				" WHERE subAtividade.ativo = :true AND atividade.ativo = :true AND cadastroParticipante.ativo = :true "+
				" AND subAtividade.id = :idSubAtividade ";
			
		Query query = getSession().createQuery(hql);
		query.setInteger("idSubAtividade", idSubAtividade);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<InscricaoAtividadeParticipante> lista = new ArrayList<InscricaoAtividadeParticipante>(
					HibernateUtils.parseTo(query.list(), projecao, InscricaoAtividadeParticipante.class, "participante")
				);
		
		return lista;
		
	}
	
	
}
