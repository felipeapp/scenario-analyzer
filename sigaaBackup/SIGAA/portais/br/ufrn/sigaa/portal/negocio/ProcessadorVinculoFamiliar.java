/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Processador respons�vel pelo cadastro de v�nculos de discente ao familiar
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
	 * Realiza a valida��o dos dados informados
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
				throw new NegocioException("N�o foi encontrado nenhum discente com a matr�cula informada.");
			
			if (discente.getNivel() != NivelEnsino.MEDIO)
				throw new NegocioException("Somente ser� poss�vel criar usu�rios para familiares de discente do n�vel m�dio.");
			
			List<UsuarioFamiliar> u = (List<UsuarioFamiliar>) dao.findByExactField(UsuarioFamiliar.class, "discenteMedio.id", discente.getId());
			if (ValidatorUtil.isNotEmpty(u))
				throw new NegocioException("Voc� j� possui vinculo com o discente informado ("+discente.getMatriculaNome() +").");			

			if (!StringUtils.compareInAscii(discente.getPessoa().getNome().toUpperCase().trim(), uf.getDiscenteMedio().getPessoa().getNome().toUpperCase().trim()))
				erros.addErro("Nome n�o confere com o registrado no sistema");
			
			if ( !internacional ) {
				ValidatorUtil.validateCPF_CNPJ(uf.getDiscenteMedio().getPessoa().getCpf_cnpj(), "CPF", erros);
				
				if (!uf.getDiscenteMedio().getPessoa().getCpf_cnpj().equals(discente.getPessoa().getCpf_cnpj()))
					erros.addErro("CPF n�o confere com a registrada no sistema");
				
				if (isEmpty(uf.getDiscenteMedio().getPessoa().getIdentidade()) || isEmpty(uf.getDiscenteMedio().getPessoa().getIdentidade().getNumero())) {
					erros.addErro("Informe o n�mero da identidade.");
				} else if (!isEmpty(discente.getPessoa().getIdentidade()) && !isEmpty(discente.getPessoa().getIdentidade().getNumero())) {
					String identDigitada = StringUtils.extractLong(uf.getDiscenteMedio().getPessoa().getIdentidade().getNumero()).toString();
					String identDiscente = StringUtils.extractLong(discente.getPessoa().getIdentidade().getNumero()).toString();
					if (!identDigitada.equals(identDiscente)) {
						erros.addErro("Identidade n�o confere com a registrada no sistema");
					}
				}
			} else {
				validateRequired(uf.getDiscenteMedio().getPessoa().getPassaporte(), "Passaporte", erros);
				
				if (!uf.getDiscenteMedio().getPessoa().getPassaporte().equals(discente.getPessoa().getPassaporte())) {
					erros.addErro("Passaporte n�o confere com o registrado no sistema");
				}
			}

			if (!uf.getDiscenteMedio().getDiscente().getAnoIngresso().equals(discente.getAnoIngresso())) 
				erros.addErro("Ano de Ingresso inv�lido para este aluno");

			if (discente.getPessoa().getDataNascimento() != null) {
				if (CalendarUtils.compareTo(discente.getPessoa().getDataNascimento(), uf.getDiscenteMedio().getPessoa().getDataNascimento()) != 0)
					erros.addErro("Data de Nascimento n�o confere");
			} else {
				erros.addErro("Data de Nascimento n�o informada.");
			}

			if (!erros.isEmpty())
				throw new NegocioException(erros);
			
			if(discente != null && (discente.getStatus() != StatusDiscente.ATIVO 
					&& discente.getStatus() != StatusDiscente.ATIVO_DEPENDENCIA))
				throw new NegocioException("N�o foi poss�vel realizar o cadastro, pois o aluno informado ainda n�o est� com matr�cula ativa no sistema.");
			
			Curso curso = null;

			if (discente.getCurso() != null && discente.getCurso().getId() != 0)
				curso = dao.findByPrimaryKey(discente.getCurso().getId(), Curso.class);

			if (curso == null)
				throw new NegocioException("N�o foi poss�vel realizar o cadastro, pois o aluno voc� n�o est� associado a nenhum curso. Por favor, entre em contato com o suporte.");
			if (curso != null && curso.getUnidade() == null)
				throw new NegocioException("N�o foi poss�vel realizar o cadastro, pois n�o existe uma unidade associada ao curso do aluno informado. Por favor, entre em contato com o suporte.");
			
			uf.setDiscenteMedio(new DiscenteMedio(discente.getId()));
			uf.getDiscenteMedio().setDiscente(discente);

		} finally {
			dao.close();
			usrDao.close();
		}	
		
	}

}
