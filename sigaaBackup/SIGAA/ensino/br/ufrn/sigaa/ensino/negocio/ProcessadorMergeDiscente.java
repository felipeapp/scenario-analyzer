/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 19/04/2012
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/** Processador responsável por unificar os dados pessoais de dois discentes
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorMergeDiscente extends AbstractProcessador {

	/** Executa a unificação de dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		DiscenteAdapter unificado = null;
		@SuppressWarnings("unchecked")
		Collection<Discente> discentes = (Collection<Discente>) movimento.getColObjMovimentado();
		GenericDAO dao = getGenericDAO(movimento);
		try {
			// recupera os dados do banco para garantir que todos dados foram carregados corretamente
			for (Discente d : discentes) {
				if (d.isSelecionado()) {unificado = d; break;}
			}
			unificado = dao.refresh(unificado);
			for (Discente outro : discentes) {
				if (outro.getId() == unificado.getId()) continue;
				outro = dao.refresh(outro);
				int idPessoaAntigo = outro.getPessoa().getId();
				// invalida os dados pessoais antigos
				dao.updateField(Pessoa.class, outro.getPessoa().getId(), "valido", false);
				// unifica os dados dos discentes
				outro.setPessoa(unificado.getPessoa());
				// unifica os dados do usuário
				Collection<Usuario> usuarios = dao.findByExactField(Usuario.class, "pessoa.id", idPessoaAntigo);
				if (usuarios != null) {
					for (Usuario usuario : usuarios) {
						// invalida os dados pessoais antigos
						dao.updateField(Pessoa.class, usuario.getPessoa().getId(), "valido", false);
						usuario.setPessoa(unificado.getPessoa());
						dao.update(usuario);
					}
						
				}
				// unifica os dados do servidor, se houver
				Collection<Servidor> servidores = dao.findByExactField(Servidor.class, "pessoa.id", idPessoaAntigo);
				if (usuarios != null) {
					for (Servidor servidor : servidores) {
						// invalida os dados pessoais antigos
						dao.updateField(Pessoa.class, servidor.getPessoa().getId(), "valido", false);
						servidor.setPessoa(unificado.getPessoa());
						dao.update(servidor);
					}
						
				}
				// unifica os dados de outros discentes associados à pessoa, se houver
				Collection<Discente> outrosDiscentes = dao.findByExactField(Discente.class, "pessoa.id", idPessoaAntigo);
				if (outrosDiscentes != null) {
					for (Discente discente : outrosDiscentes) {
						// invalida os dados pessoais antigos
						dao.updateField(Pessoa.class, discente.getPessoa().getId(), "valido", false);
						discente.setPessoa(unificado.getPessoa());
						dao.update(discente);
					}
				}
				// anota a observação que foi migrado;
				Date data = new Date();
				String obs = unificado.getObservacao();
				if (obs == null) obs = "";
				obs += ". Dados unificados com o discente " + outro.getMatricula() +" em " + Formatador.getInstance().formatarData(data);
				unificado.setObservacao(obs);
				obs = outro.getObservacao();
				if (obs == null) obs = "";
				obs += "Dados unificados com o discente " + unificado.getMatricula() +" em " + Formatador.getInstance().formatarData(data);
				outro.setObservacao(obs);
				dao.update(unificado);
				dao.update(outro);
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/** Valida os dados a serem persistidos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		Discente principal = movimento.getObjMovimentado();
		Discente outro = (Discente) movimento.getObjAuxiliar();
		ListaMensagens erros = new ListaMensagens();
		validateRequired(principal, "Discente Unificado", erros);
		validateRequired(outro, "Discente a unificar", erros);
		checkValidation(erros);
	}

}
