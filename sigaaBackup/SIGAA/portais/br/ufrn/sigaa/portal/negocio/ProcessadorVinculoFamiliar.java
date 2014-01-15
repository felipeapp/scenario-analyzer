/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/09/2011
 *
 */
package br.ufrn.sigaa.portal.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador responsável pelo cadastro de vínculos de discente ao familiar
 *  
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorVinculoFamiliar extends AbstractProcessador  {

	/**
	 * Executa o processador
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		UsuarioFamiliar uf = (UsuarioFamiliar) cMov.getObjMovimentado();
		
		getGenericDAO(cMov).create(uf);

		return uf;
	}

	/**
	 * Realiza a validação dos dados informados
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		UsuarioFamiliar uf = (UsuarioFamiliar) cMov.getObjMovimentado();
		boolean internacional = (Boolean) cMov.getObjAuxiliar();

		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		UsuarioDao usrDao = getDAO(UsuarioDao.class, mov);

		ListaMensagens erros = new ListaMensagens();

		try {

			Discente discente = dao.findByMatricula(uf.getDiscenteMedio().getDiscente().getMatricula());
			
			if (discente == null)
				throw new NegocioException("Não foi encontrado nenhum discente com a matrícula informada.");
			
			if (discente.getNivel() != NivelEnsino.MEDIO)
				throw new NegocioException("Somente será possível criar usuários para familiares de discente do nível médio.");
			
			List<UsuarioFamiliar> u = (List<UsuarioFamiliar>) dao.findByExactField(UsuarioFamiliar.class, "discenteMedio.id", discente.getId());
			if (ValidatorUtil.isNotEmpty(u))
				throw new NegocioException("Você já possui vinculo com o discente informado ("+discente.getMatriculaNome() +").");			

			if (!StringUtils.compareInAscii(discente.getPessoa().getNome().toUpperCase().trim(), uf.getDiscenteMedio().getPessoa().getNome().toUpperCase().trim()))
				erros.addErro("Nome não confere com o registrado no sistema");
			
			if ( !internacional ) {
				ValidatorUtil.validateCPF_CNPJ(uf.getDiscenteMedio().getPessoa().getCpf_cnpj(), "CPF", erros);
				
				if (!uf.getDiscenteMedio().getPessoa().getCpf_cnpj().equals(discente.getPessoa().getCpf_cnpj()))
					erros.addErro("CPF não confere com a registrada no sistema");
				
				if (isEmpty(uf.getDiscenteMedio().getPessoa().getIdentidade()) || isEmpty(uf.getDiscenteMedio().getPessoa().getIdentidade().getNumero())) {
					erros.addErro("Informe o número da identidade.");
				} else if (!isEmpty(discente.getPessoa().getIdentidade()) && !isEmpty(discente.getPessoa().getIdentidade().getNumero())) {
					String identDigitada = StringUtils.extractLong(uf.getDiscenteMedio().getPessoa().getIdentidade().getNumero()).toString();
					String identDiscente = StringUtils.extractLong(discente.getPessoa().getIdentidade().getNumero()).toString();
					if (!identDigitada.equals(identDiscente)) {
						erros.addErro("Identidade não confere com a registrada no sistema");
					}
				}
			} else {
				validateRequired(uf.getDiscenteMedio().getPessoa().getPassaporte(), "Passaporte", erros);
				
				if (!uf.getDiscenteMedio().getPessoa().getPassaporte().equals(discente.getPessoa().getPassaporte())) {
					erros.addErro("Passaporte não confere com o registrado no sistema");
				}
			}

			if (!uf.getDiscenteMedio().getDiscente().getAnoIngresso().equals(discente.getAnoIngresso())) 
				erros.addErro("Ano de Ingresso inválido para este aluno");

			if (discente.getPessoa().getDataNascimento() != null) {
				if (CalendarUtils.compareTo(discente.getPessoa().getDataNascimento(), uf.getDiscenteMedio().getPessoa().getDataNascimento()) != 0)
					erros.addErro("Data de Nascimento não confere");
			} else {
				erros.addErro("Data de Nascimento não informada.");
			}

			if (!erros.isEmpty())
				throw new NegocioException(erros);
			
			if(discente != null && (discente.getStatus() != StatusDiscente.ATIVO 
					&& discente.getStatus() != StatusDiscente.ATIVO_DEPENDENCIA))
				throw new NegocioException("Não foi possível realizar o cadastro, pois o aluno informado ainda não está com matrícula ativa no sistema.");
			
			Curso curso = null;

			if (discente.getCurso() != null && discente.getCurso().getId() != 0)
				curso = dao.findByPrimaryKey(discente.getCurso().getId(), Curso.class);

			if (curso == null)
				throw new NegocioException("Não foi possível realizar o cadastro, pois o aluno você não está associado a nenhum curso. Por favor, entre em contato com o suporte.");
			if (curso != null && curso.getUnidade() == null)
				throw new NegocioException("Não foi possível realizar o cadastro, pois não existe uma unidade associada ao curso do aluno informado. Por favor, entre em contato com o suporte.");
			
			uf.setDiscenteMedio(new DiscenteMedio(discente.getId()));
			uf.getDiscenteMedio().setDiscente(discente);

		} finally {
			dao.close();
			usrDao.close();
		}	
		
	}

}
