/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/09/2011
 *
 */
package br.ufrn.sigaa.pesquisa.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe responsável pelas consultas referente a submissão da proposta para a criação do grupo de pesquisa.
 * 
 * @author Leonardo Campos
 * @author Jean Guerethes
 */
public class PropostaGrupoPesquisaDao extends GenericSigaaDAO {

	/***
	 * Serve para recuperar pegar o Endereço Lattes do docente adicionado
	 * @param servidor
	 * @return
	 */
	public String getEnderecoLattes(Servidor servidor) {
		try {
			return (String) getJdbcTemplate(Sistema.COMUM)
			.queryForObject(
					"select endereco_lattes from comum.perfil_pessoa where id_servidor = ? order by endereco_lattes desc " + BDUtils.limit(1),
					new Object[] { servidor.getId() }, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Busca um docente pelo nome, para que possa ser adicionado como membro permanente. 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByMembroPermanente( String nome ) throws DAOException {
		try {
			String hql = "select s.id, s.pessoa.id, s.pessoa.nome, s.siape, s.ativo.descricao from Servidor s where s.categoria = :categoria " ;
			
				hql += " and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nomeAscii") +
					   " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%' ");
			
			hql += " and ( (s.ativo = " + Ativo.SERVIDOR_ATIVO + ") ) ";

			hql += "order by s.pessoa.nomeAscii";

			Query q = getSession()
					.createQuery(hql);
			
			q.setInteger("categoria", Categoria.DOCENTE);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = new ArrayList<Servidor>();
			
			List<Object[]> l = q.list();
			
			return servidoresEncontrados(lista, l);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca um docente pelo nome, para que possa ser adicionado como membro associado.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByMembroDocenteAssociado( String nome ) throws DAOException {
		try {
			List<Integer> status = statusAssociados();
			
			String hql = "select s.id, s.pessoa.id, s.pessoa.nome, s.siape, s.ativo.descricao from Servidor s where s.categoria = :categoria " ;
			
				hql += " and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nomeAscii") +
					   " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%' "); 
			
			hql += " and ( s.ativo = " + Ativo.APOSENTADO + " or s.cargo.id in " + UFRNUtils.gerarStringIn( status ) + " ) ";

			hql += "order by s.pessoa.nomeAscii";

			Query q = getSession()
					.createQuery(hql);
			
			q.setInteger("categoria", Categoria.DOCENTE);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = new ArrayList<Servidor>();
			
			List<Object[]> l = q.list();
			
			return servidoresEncontrados(lista, l);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca um servidor técnico pelo nome, para que possa ser adicionado como membro associado.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findByMembroTecnicoAssociado( String nome ) throws DAOException {
		try {
			String hql = "select s.id, s.pessoa.id, s.pessoa.nome, s.siape, s.ativo.descricao from Servidor s where s.categoria = :categoria " ;
			
				hql += " and " + UFRNUtils.toAsciiUpperUTF8("s.pessoa.nomeAscii") +
					   " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%' ");
			
			hql += " and ( ( s.formacao in " + UFRNUtils.gerarStringIn( new int[] { Formacao.MESTRE, Formacao.DOUTOR }) + " ) )";

			hql += "order by s.pessoa.nomeAscii";

			Query q = getSession()
					.createQuery(hql);
			
			q.setInteger("categoria", Categoria.TECNICO_ADMINISTRATIVO);

			@SuppressWarnings("unchecked")
			Collection<Servidor> lista = new ArrayList<Servidor>();
			
			List<Object[]> l = q.list();
			
			return servidoresEncontrados(lista, l);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca um discente pelo nome, para que possa ser adicionado como membro associado.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findByMembroDiscenteAssociado( String nome ) throws DAOException {
		try {
			String hql = "SELECT d.id, d.pessoa.id, d.pessoa.nome" +
					     " from Discente d " +
					     " WHERE d.nivel in " + UFRNUtils.gerarStringIn( new char[] { NivelEnsino.GRADUACAO, NivelEnsino.LATO, NivelEnsino.STRICTO, NivelEnsino.DOUTORADO, NivelEnsino.MESTRADO }) +
			             " AND d.status in " + UFRNUtils.gerarStringIn( new int[] { StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.GRADUANDO }) +
			             " AND " + UFRNUtils.toAsciiUpperUTF8("d.pessoa.nomeAscii") +
			             " like " + UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%' ") +
						 " order by d.pessoa.nomeAscii";

			Query q = getSession()
					.createQuery(hql);
			
			Collection<Discente> lista = new ArrayList<Discente>();
			
			List<Object[]> l = q.list();
			
			for(Object[] obj : l){
				int col = 0;
				
				Discente d = new Discente();
				d.setId((Integer) obj[col++]);
				d.getPessoa().setId((Integer) obj[col++]);
				d.getPessoa().setNome((String) obj[col++]);
				
				lista.add(d);
			}
			
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/** Carrega os status possiveis dos associados */
	private List<Integer> statusAssociados() {
		List<Integer> status = new ArrayList<Integer>();
		status.addAll( Cargo.DOCENTE_SUBSTITUTO );
		status.add( Cargo.DOCENTE_SUPERIOR_VISITANTE );
		return status;
	}

	/**
	 * Realiza a projeção dos servidos encontrados.
	 * @param lista
	 * @param l
	 * @return
	 */
	private Collection<Servidor> servidoresEncontrados(
			Collection<Servidor> lista, List<Object[]> l) {
		for(Object[] obj : l){
			int col = 0;
			
			Servidor s = new Servidor();
			s.setId((Integer) obj[col++]);
			s.getPessoa().setId((Integer) obj[col++]);
			s.getPessoa().setNome((String) obj[col++]);
			s.setSiape((Integer) obj[col++]);
			
			Ativo a = new Ativo();
			a.setDescricao((String) obj[col++]);
			s.setAtivo(a);
			
			lista.add(s);
		}
		
		return lista;
	}

	/**
	 * Realiza a busca dos projetos em que os docentes da proposta fazem parte.
	 * @param nome
	 * @param membros
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByProjetoPesquisa( String nome, Collection<MembroGrupoPesquisa> membros ) throws DAOException {
		Collection<Integer> membrosGrupoPesq = carregarIdMembros(membros);

		try {
			String sql = "select distinct p.titulo, pp.id_projeto_pesquisa, pp.cod_ano, pp.cod_prefixo, pp.cod_numero" +
					" FROM pesquisa.projeto_pesquisa pp" +
					" JOIN projetos.membro_projeto mp on ( pp.id_projeto = mp.id_projeto )" +
					" JOIN projetos.projeto p on ( p.id_projeto = pp.id_projeto )" +
					" WHERE mp.ativo = true" +
					" AND (p.titulo ilike '"+ nome +"%' or (pp.cod_prefixo || pp.cod_numero || '-' || pp.cod_ano like '"+ nome +"%'))" +  
					" AND mp.id_pessoa in " + UFRNUtils.gerarStringIn( membrosGrupoPesq ) +
					" AND p.id_tipo_situacao_projeto in  " + UFRNUtils.gerarStringIn( TipoSituacaoProjeto.PESQ_GRUPO_VALIDO ) +
					" GROUP BY p.titulo, pp.id_projeto_pesquisa, pp.cod_ano, pp.cod_prefixo, pp.cod_numero";

			Query q = getSession().createSQLQuery(sql);
			
			Collection<ProjetoPesquisa> lista = new ArrayList<ProjetoPesquisa>();
			
			List<Object[]> l = q.list();
			
			for(Object[] obj : l){
				int col = 0;
				
				ProjetoPesquisa pp = new ProjetoPesquisa();
				pp.setTitulo( (String) obj[col++] );
				pp.setId( (Integer) obj[col++] );
				
				CodigoProjetoPesquisa cpp = new CodigoProjetoPesquisa();
				cpp.setAno( ((Short) obj[col++]).intValue() );
				cpp.setPrefixo( (String) obj[col++] );
				cpp.setNumero( (Integer) obj[col++] );
				pp.setCodigo(cpp);
				
				lista.add(pp);
			}
			
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Carrega os id's dos membros para ser utilizada na consulta para retornar os projeto que os docentes fazem parte. */
	private Collection<Integer> carregarIdMembros(
			Collection<MembroGrupoPesquisa> membros) {
		Collection<Integer> membrosGrupoPesq = new ArrayList<Integer>();
		for (MembroGrupoPesquisa linha : membros )
			membrosGrupoPesq.add(linha.getPessoa().getId());
		return membrosGrupoPesq;
	}

}