/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ValidacaoProducao;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * Processador de validação da produção de um usuário
 *
 * @author Gleydson
 *
 */
@Deprecated
public class ProcessadorValidacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		ValidacaoProducao validacaoProducao = (ValidacaoProducao) mov;

		Producao prod = new Producao();
		GenericDAO dao = getDAO(mov);

		validacaoProducao.setDataValidacao(new Date());

		prod = dao.findByPrimaryKey(validacaoProducao.getProducao().getId(), prod.getClass());
		validacaoProducao.setProducao(prod);

		if (validacaoProducao.getValidado() != null && !validacaoProducao.getValidado().equals(prod.getValidado())) {

			prod.setValidado(validacaoProducao.getValidado());
			prod.setDataValidacao(validacaoProducao.getDataValidacao());
			dao.update(prod);
			dao.create(validacaoProducao);
		} else {
			String valorValidacao = (validacaoProducao.getValidado() != null && validacaoProducao.getValidado() ? "Validada" : "Invalidada");
			NegocioException ne = new NegocioException();
			ne.addErro("N&atilde;o &eacute; poss&iacute;vel atualizar a valida&ccedil;&atilde;o.<br>" + "A produ&ccedil;&atilde;o j&aacute; se encontra "
					+ valorValidacao);
			throw ne;
		}

		dao.close();

		return prod;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		ServidorDao dao = getDAO(ServidorDao.class, mov);
		Usuario usr = (Usuario) mov.getUsuarioLogado();
		Collection<Designacao> designacoes = dao.findDesignacoesAtivas(usr.getServidor());
		boolean pode = false;

		if ( mov.getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO)) {
			pode = true;
		}

		for (Designacao d : designacoes) {
			if (AtividadeServidor.CHEFE_DEPARTAMENTO.contains(d.getAtividade().getCodigoRH()) ||
					AtividadeServidor.DIRETOR_CENTRO.contains(d.getAtividade().getCodigoRH())) {
				pode = true;
			}
		}
		if (!pode) {
			NegocioException e = new NegocioException();
			e.addErro("Usuário não é autorizado a fazer validações de produções intelectuais");
			throw e;
		}

	}
}
