/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/09/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.estagio.dominio.ConcedentePessoaFuncao;
import br.ufrn.sigaa.estagio.dominio.ConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoConvenio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável por gerenciar o acesso a dados de Convênios de Estágio
 * 
 * @author Arlindo Rodrigues
 */
 
public class ConvenioEstagioDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a lista de Convênios de Estágios conforme os Parâmetros informados.
	 * @param curso
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvenioEstagio> findConvenioGeral(String empresa,
			String responsavel, Usuario usuarioCriacao,
			StatusConvenioEstagio status,
			boolean apenasPermiteCadastroDiretoCoordenacao,
			Collection<Integer> tipoOfertaVagas,
			Long cpfCnpj,
			String numeroConvenio) throws HibernateException,
			DAOException {
		Collection<StatusConvenioEstagio> colStatus = null;
		if (!isEmpty(status)) {
			colStatus = new LinkedList<StatusConvenioEstagio>();
			colStatus.add(status);
		}
		return findConvenioGeral(empresa, responsavel, usuarioCriacao, colStatus, apenasPermiteCadastroDiretoCoordenacao, tipoOfertaVagas, cpfCnpj, numeroConvenio);
	}
	/**
	 * Retorna a lista de Convênios de Estágios conforme os Parâmetros informados.
	 * @param curso
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvenioEstagio> findConvenioGeral(String empresa,
			String responsavel, Usuario usuarioCriacao,
			Collection<StatusConvenioEstagio> status,
			boolean apenasPermiteCadastroDiretoCoordenacao,
			Collection<Integer> tipoOfertaVagas,
			Long cpfCnpj,
			String numeroConvenio) throws HibernateException,
			DAOException {
		String projecao = " ce.id, ce.idArquivoTermoConvenio, ce.concedente.id, ce.concedente.pessoa.cpf_cnpj, ce.concedente.pessoa.nome, " +
		" ce.tipoConvenio.descricao, ce.dataCadastro, ce.status.id, ce.status.descricao," +
		" ce.registroCadastro.id, ce.registroCadastro.usuario.id, ce.registroCadastro.usuario.login, ce.registroCadastro.usuario.ramal," +
		" ce.registroCadastro.usuario.pessoa.nome, pep ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select "+projecao+" from ConvenioEstagio ce ");
		hql.append("inner join ce.concedente ");
		hql.append("inner join ce.concedente.pessoa ");
		hql.append("inner join ce.concedente.concedenteEstagioPessoa as pep ");
		hql.append("inner join ce.tipoConvenio ");
		hql.append("inner join ce.status ");
		hql.append("where 1 = 1 ");
		
		if (!ValidatorUtil.isEmpty(empresa))
			hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("ce.concedente.pessoa.nomeAscii") + " like :empresa ");
		if (cpfCnpj != null && cpfCnpj > 0)
			hql.append(" and ce.concedente.pessoa.cpf_cnpj = :cpfCnpj ");
		
		Long cpfReponsavel = null;
		if (!ValidatorUtil.isEmpty(responsavel)){
			cpfReponsavel = StringUtils.extractLong(responsavel);
			hql.append(" and ce.concedente.id in ( " +
						" select p.id from ConcedenteEstagioPessoa pe " +
						" inner join pe.concedente p " +
						" where pe.funcao.id = "+ConcedentePessoaFuncao.ADMINISTRADOR);
			if (cpfReponsavel != null)
				hql.append(" and pe.pessoa.cpf_cnpj = :responsavel ) ");
			else
				hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("pe.pessoa.nomeAscii") + " like :responsavel )");
		}
					
		if (usuarioCriacao != null && usuarioCriacao.getId() > 0)
			hql.append(" and ce.registroCadastro.usuario.id = "+usuarioCriacao.getId());		
		
		if (!isEmpty(status))
			hql.append(" and ce.status.id in "+UFRNUtils.gerarStringIn(status));
		
		if (apenasPermiteCadastroDiretoCoordenacao)
			hql.append(" and ce.tipoConvenio.permiteCadastroDiretoCoordenacao = "+SQLDialect.TRUE);
		
		if (!isEmpty(tipoOfertaVagas))
			hql.append(" and ce.tipoOfertaVaga in ").append(UFRNUtils.gerarStringIn(tipoOfertaVagas));
		if (!isEmpty(numeroConvenio))
			hql.append(" and ce.numeroConvenio = :numeroConvenio ");
		hql.append(" order by ce.dataCadastro desc, ce.concedente.pessoa.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (!ValidatorUtil.isEmpty(empresa))
			q.setString("empresa",  "%"+ StringUtils.toAscii(empresa.trim().toUpperCase()) + "%");
		if (cpfCnpj != null && cpfCnpj > 0)
			q.setLong("cpfCnpj", cpfCnpj);
		if (!ValidatorUtil.isEmpty(responsavel)){
			if (cpfReponsavel != null)
				q.setLong("responsavel", cpfReponsavel);
			else
				q.setString("responsavel", "%"+StringUtils.toAscii(responsavel.trim().toUpperCase()) + "%");
		}
		if (!isEmpty(numeroConvenio))
			q.setString("numeroConvenio", numeroConvenio);			
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		List<ConvenioEstagio> convenios = new ArrayList<ConvenioEstagio>();
		for (Object[] linha : resultado) {
			ConvenioEstagio c = new ConvenioEstagio();
			int a = 0;
			int linhaPessoa = 0;
			c.setId( (Integer) linha[a++] );		
			if (!convenios.contains(c)) {
				c.setIdArquivoTermoConvenio((Integer) linha[a++]);
				c.setConcedente(new ConcedenteEstagio());
				c.getConcedente().setId((Integer) linha[a++]);
				c.getConcedente().setPessoa(new Pessoa());
				c.getConcedente().getPessoa().setCpf_cnpj( (Long) linha[a++]);
				c.getConcedente().getPessoa().setNome((String) linha[a++]);
				c.getConcedente().setConvenioEstagio(c);
				c.setTipoConvenio(new TipoConvenio());
				c.getTipoConvenio().setDescricao((String) linha[a++]);
				c.setDataCadastro((Date) linha[a++]);
				c.setStatus(new StatusConvenioEstagio((Integer) linha[a++]));
				c.getStatus().setDescricao((String) linha[a++]);
				c.setRegistroCadastro(new RegistroEntrada());
				c.getRegistroCadastro().setId((Integer) linha[a++]);
				c.getRegistroCadastro().setUsuario(new Usuario((Integer) linha[a++]));
				c.getRegistroCadastro().getUsuario().setLogin((String) linha[a++]);
				c.getRegistroCadastro().getUsuario().setRamal((String) linha[a++]);
				c.getRegistroCadastro().getUsuario().setNome((String) linha[a++]);				

				linhaPessoa = a;
				if(linha[linhaPessoa] != null)
					c.getConcedente().setConcedenteEstagioPessoa(new ArrayList<ConcedenteEstagioPessoa>());
				convenios.add(c);
			} else {
				linhaPessoa = 14;
				c = convenios.get(convenios.indexOf(c));
			}			
			if(linha[linhaPessoa] != null){
				ConcedenteEstagioPessoa pep = (ConcedenteEstagioPessoa) linha[linhaPessoa];
				c.getConcedente().getConcedenteEstagioPessoa().add(pep);
				if (pep.getFuncao().getId() == ConcedentePessoaFuncao.ADMINISTRADOR)
					c.getConcedente().setResponsavel(pep);
				else if (pep.getFuncao().getId() == ConcedentePessoaFuncao.SUPERVISOR)
					c.getConcedente().setSupervisor(pep);
			}
		}
		return convenios;
	}

	/**
	 * Retorna os Tipos de Convenio que permitem realizar solicitações de Convênio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<TipoConvenio> findAllTipoConvenioSolicitacao() throws HibernateException, DAOException{
		String hql = "select t from TipoConvenio t where t.permiteSolicitacao = trueValue() ";
		@SuppressWarnings("unchecked")
		Collection<TipoConvenio> lista = getSession().createQuery(hql).list(); 
		return lista;
	}
	
	
	/**
	 * Busca Convênios de Estágio do Concedente e Tipo informados
	 * @param id
	 * @param tipo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvenioEstagio> findConvenioAtivobyConcedenteTipo(Pessoa p, int tipo) throws HibernateException, DAOException{
		String hql = "select c from ConvenioEstagio c " +
				" where c.concedente.pessoa.id = "+p.getId() +
				 "  and c.tipoConvenio.id = "+tipo+
				 "  and c.status.id in ("+StatusConvenioEstagio.APROVADO+","+
				 						StatusConvenioEstagio.EM_ANALISE+","+
				 						StatusConvenioEstagio.SUBMETIDO+ ")";
		@SuppressWarnings("unchecked")
		Collection<ConvenioEstagio> lista = getSession().createQuery(hql).list(); 
		return lista;		
	}

}
