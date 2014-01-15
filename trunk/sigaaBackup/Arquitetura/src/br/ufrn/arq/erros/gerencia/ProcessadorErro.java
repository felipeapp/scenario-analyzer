/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/12/2008
 */
package br.ufrn.arq.erros.gerencia;

import java.rmi.RemoteException;
import java.util.Date;

import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.ErroDao;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Processa um erro ocorrido no sistema.Verifica se ele já ocorreu e caso sim
 * registra a ocorrência sem registrar um novo Erro. A cada deploy de produção o
 * sistema refaz o registro dos erros
 * 
 * @author Gleydson Lima
 * @author David Pereira
 * 
 */
public class ProcessadorErro extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoErro movErro = (MovimentoErro) mov;
		ErroDao erroDao = getDAO(ErroDao.class, movErro);

		String traceCausador = "";
		
		if(movErro.getErro().getStackTrace().length > 0)
			traceCausador = movErro.getErro().getStackTrace()[0].toString();
		
		String descricaoErro = movErro.getErro().toString();
		
		// procura por uma ocorrência deste erro desde o último deploy de produçao
		JdbcTemplate template =  erroDao.getJdbcTemplate();
		Integer idErro = null;
		try {
			idErro = template
					.queryForInt(" select id_erro from infra.erro where trace_gerador = '"
							+ StringUtils.escapeBackSlash(traceCausador).trim()
							+ "' and excecao = '" + StringUtils.escapeBackSlash(descricaoErro).trim() + "' "
							+ "and data >= (select max(data) from iproject.deploy_producao  where id_sistema = "
							+ mov.getSistema() + ") order by data desc " + BDUtils.limit(1));
		} catch (EmptyResultDataAccessException e) {

		}

		try {
			Erro erro = new Erro();
			if (idErro == null) {
				erro.setData(new Date());
				erro.setTraceGerador(traceCausador);
				erro.setTraceCompleto(movErro.getDetails());
				erro.setSistema(new Sistema(mov.getSistema()));
				erro.setSubSistema(new SubSistema(mov.getSubsistema()));
				erro.setExcecao(descricaoErro);

				erro.setDeployProducao(buscarUltimoDeployProducao(mov.getSistema(), template));

				UFRNUtils.clearTransientObjects(erro);
				erroDao.create(erro);
			} else {
				erro.setId(idErro);
			}
			erroDao.create(criarOcorrencia(erro, mov));
		} finally {
			erroDao.close();
		}
		
		return null;
	}

	/**
	 * Retorna o id do último deploy feito para producao do sistema informado como parametro.
	 * 
	 * @param mov
	 * @param template
	 * @return
	 */
	private Integer buscarUltimoDeployProducao(Integer idSistema, JdbcTemplate template) {
		Integer idDeployProducao = template
				.queryForInt("select max(id_deploy_producao) from iproject.deploy_producao dp join iproject.sistema sis on " +
							"(dp.id_sistema = sis.id) where id_sistema_arq = " + idSistema );
		return idDeployProducao;
	}

	/**
	 * Cria uma ocorrência interna
	 * 
	 * @param erro
	 * @param mov
	 * @return
	 */
	private ErroOcorrencia criarOcorrencia(Erro erro, Movimento mov) {
		ErroOcorrencia ocorrencia = new ErroOcorrencia();
		ocorrencia.setErro(erro);
		ocorrencia.setData(new Date());

		UsuarioGeral usuarioLogado = mov.getUsuarioLogado();
		if (usuarioLogado != null) {
			ocorrencia.setIdUsuario(usuarioLogado.getId());
			ocorrencia.setIdRegistroEntrada(usuarioLogado.getRegistroEntrada() != null ? usuarioLogado.getRegistroEntrada().getId() : null);
		}
		return ocorrencia;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
