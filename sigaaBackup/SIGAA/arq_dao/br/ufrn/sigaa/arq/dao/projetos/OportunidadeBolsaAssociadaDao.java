package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.ParametroBuscaAgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.RestricoesBuscaAgregadorBolsas;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/**
 * DAO para consultas relacionadas a oportunidades de bolsas em ações associadas
 * 
 * @author geyson
 *
 */
public class OportunidadeBolsaAssociadaDao extends GenericSigaaDAO {

	/**
	 * 
	 * Retorna todas as bolsas disponíveis de acordo com as opções e
	 * restrições solicitadas e agregada nos parâmetros do método	 	  
	 * 
	 * @param restricoes
	 * @param parametros	  
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */	
	public List<Projeto> findBolsasDisponiveis(RestricoesBuscaAgregadorBolsas restricoes, ParametroBuscaAgregadorBolsas parametros)
			throws HibernateException, DAOException {

		StringBuilder hql = new StringBuilder();

		hql.append(
					" select proj.id, proj.bolsasConcedidas, proj.titulo, und.id, und.nome, und.sigla, undResponsavel.id, undResponsavel.nome, " +
					"  me.id, funcao.id, funcao.descricao, serv.id, pessoa.id, pessoa.nome, de.id, de.ativo, tipoVinculo.id, " +
				    " tipoVinculo.descricao, sitDiscentePro.id, sitDiscentePro.descricao " +
				    " FROM Projeto proj " +
				    " inner join proj.situacaoProjeto as sitProj " +
				    " inner join proj.unidade as und " +
				    " inner join und.unidadeResponsavel as undResponsavel " +
				    " left  join proj.discentesProjeto as de " +
				    " left  join de.tipoVinculo as tipoVinculo " +
				    " left  join de.situacaoDiscenteProjeto as sitDiscentePro " +
				    " inner join proj.equipe as me " +
				    " inner join me.funcaoMembro as funcao " +
				    " inner join me.servidor as serv " +
				    " inner join serv.pessoa as pessoa "+
				    " where (proj.tipoProjeto.id = :idTipoProjeto ) "
				  );		
		
		if (restricoes.isBuscaServidor())
			hql.append(" and me.servidor.id = :idDocente");
		else
			hql.append(" and 1 = 1 ");		
		
		hql.append(	" and sitProj.id in (:situacaoAtivos) " );
		 
		if (restricoes.isBuscaDepartamento())
			hql.append(" and und.id = :departamento");
		if (restricoes.isBuscaCentro())
			hql.append(" and undResponsavel.id = :centro or und.id = :centro");
		if (restricoes.isBuscaPalavraChave())
			hql.append(" and (lower(proj.resumo) like lower(:palavraChave) or lower(proj.titulo) like lower(:palavraChave))");
		if (restricoes.isBuscaAno())
			hql.append(" and proj.ano = :ano");
		
		hql.append(" order by proj.id, proj.bolsasConcedidas desc ");
		
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);
		q.setParameterList("situacaoAtivos", new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO});
		
		if (restricoes.isBuscaServidor())
			q.setInteger("idDocente", parametros.getServidor().getId());
		if (restricoes.isBuscaDepartamento())
			q.setInteger("departamento", parametros.getDepartamento());
		if (restricoes.isBuscaCentro())
			q.setInteger("centro", parametros.getCentro());
		if (restricoes.isBuscaPalavraChave())
			q.setString("palavraChave", "%" + parametros.getPalavraChave() + "%");
		if (restricoes.isBuscaAno())
			q.setInteger("ano", parametros.getAno());
		

		
		
		List<Projeto> resultado = new ArrayList<Projeto>();
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
		
		Projeto projAtual = new Projeto();
		for (int i = 0; i < lista.size(); i++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(i);

			Integer idNew = (Integer) colunas[col++]; 
			
			//nova extensão
			if (projAtual.getId() != idNew) {
				projAtual = new Projeto();
				projAtual.setCoordenador(new MembroProjeto());
				
				projAtual.setId(idNew);
				projAtual.setBolsasConcedidas((Integer) colunas[col++]);				
				projAtual.setTitulo((String) colunas[col++]);
				projAtual.getUnidade().setId((Integer) colunas[col++]);
				projAtual.getUnidade().setNome((String) colunas[col++]);
				projAtual.getUnidade().setSigla((String) colunas[col++]);
				projAtual.getUnidade().setUnidadeResponsavel(new Unidade());
				projAtual.getUnidade().getUnidadeResponsavel().setId((Integer) colunas[col++]);
				projAtual.getUnidade().getUnidadeResponsavel().setNome((String) colunas[col++]);
				
				
				resultado.add(projAtual);
			}
			
			col = 8;
			
			//adiciona os membros da equipe
			MembroProjeto mp = new MembroProjeto();
			
			mp.setId((Integer) colunas[col++]);
			mp.setFuncaoMembro(new FuncaoMembro());
			mp.getFuncaoMembro().setId((Integer) colunas[col++]);
			mp.getFuncaoMembro().setDescricao((String) colunas[col++]);			
			mp.getServidor().setId((Integer) colunas[col++]);
						
			Pessoa p = new Pessoa();
			
			p.setId((Integer) colunas[col++]);
			p.setNome((String) colunas[col++]);
			mp.getServidor().setPessoa(p);
			
			if(mp.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR){
				projAtual.setCoordenador(mp);
			}
			
			if(colunas[col] != null) {			
				
				DiscenteProjeto de = new DiscenteProjeto();
				de.setId((Integer) colunas[col++]);
				de.setAtivo((Boolean) colunas[col++]);
				de.setTipoVinculo(new TipoVinculoDiscente());
				de.getTipoVinculo().setId((Integer) colunas[col++]);
				de.getTipoVinculo().setDescricao((String) colunas[col++]);
				de.getSituacaoDiscenteProjeto().setId((Integer) colunas[col++]);
				de.getSituacaoDiscenteProjeto().setDescricao((String) colunas[col++]);
				
				if(!resultado.get(resultado.indexOf(projAtual)).getDiscentesProjeto().contains(de))			
					resultado.get(resultado.indexOf(projAtual)).getDiscentesProjeto().add(de);				
				
			}
			
			
			resultado.get(resultado.indexOf(projAtual)).getEquipe().add(mp);
			
			
		}
		
		return resultado;

	}
	
}
