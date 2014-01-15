/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 07/06/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrm.sigaa.nee.dao.NeeDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.PessoaNecessidadeEspecial;

/**
 * Processador para as operações relacionadas ao {@link PessoaNecessidadeEspecial}.
 * @author Rafael Gomes
 *
 */
public class ProcessadorPessoaNecessidadeEspecial extends ProcessadorCadastro{
	
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		DiscenteAdapter discente = new Discente();
		discente = ((MovimentoCadastro)mov).getObjMovimentado();
		@SuppressWarnings("unchecked")
		List<Object> tiposNEE = (List<Object>) ((MovimentoCadastro)mov).getObjAuxiliar();
		
		NeeDao dao = getDAO(NeeDao.class, mov);
		
		try{
			validate(mov);
			
			cadastrarNecessidadesEspeciaisAluno(discente, tiposNEE, mov);
			removerNecessidadesEspeciaisAluno(discente, tiposNEE,dao );
			
		} finally{
			dao.close();
		}
		return mov;
	}
	
	/**
	 * Método utilizado por persistir as necessidades especiais do aluno.
	 * @param solicitacaoNee
	 * @param tiposNEE
	 * @param mov
	 * @throws DAOException
	 */
	public void cadastrarNecessidadesEspeciaisAluno(DiscenteAdapter discente, List<Object> tiposNEE, Movimento mov) throws DAOException{
		NeeDao dao = getDAO(NeeDao.class, mov);
		try {
			for (Object idTipo : tiposNEE) {
				PessoaNecessidadeEspecial pNEE = new PessoaNecessidadeEspecial();
				pNEE.setPessoa(discente.getPessoa());
				pNEE.setTipoNecessidadeEspecial(new TipoNecessidadeEspecial(Integer.parseInt(idTipo.toString())));
				if ( !existeNecessidadeEspecial(discente, pNEE, mov) )
					dao.createOrUpdate(pNEE);
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Método invocado para remover as necessidades especiais do aluno,
	 * para isso a necessidade se torna inativa.
	 * @param solicitacaoNee
	 * @param tiposNEE
	 * @param mov
	 * @throws DAOException
	 */
	public void removerNecessidadesEspeciaisAluno(DiscenteAdapter discente, List<Object> tiposNEE, NeeDao dao) throws DAOException{
		
		List<Integer> neeSelecionadas = new ArrayList<Integer>();
		for (Object o : tiposNEE) {
			neeSelecionadas.add(Integer.parseInt(o.toString()));
		}
		
		Collection<PessoaNecessidadeEspecial> colNecessidadesDiscente = dao.findNecessidadesEspeciaisByDiscente(discente);
		for (Iterator<PessoaNecessidadeEspecial> iterator = colNecessidadesDiscente.iterator(); iterator.hasNext();) {
			PessoaNecessidadeEspecial pNEE = iterator.next();
			
			if ( !neeSelecionadas.contains(pNEE.getTipoNecessidadeEspecial().getId()) ){
				dao.remove(pNEE);
			}
		}
		
		if ( ValidatorUtil.isNotEmpty(discente.getPessoa().getTipoNecessidadeEspecial()) ){
			if ( !neeSelecionadas.contains(discente.getPessoa().getTipoNecessidadeEspecial().getId()) ){
				discente.getPessoa().setTipoNecessidadeEspecial(null);
				dao.updateField(Pessoa.class, discente.getPessoa().getId(), "tipoNecessidadeEspecial", null);
			}
		}
	}
	
	/**
	 * Método responsável por verificar se o aluno já possui a necessidade Especial em sua 
	 * lista de necessidades especiais cadastradas em banco.
	 * @param solicitacaoNee
	 * @param tipoNeeNova
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public boolean existeNecessidadeEspecial(DiscenteAdapter discente, PessoaNecessidadeEspecial tipoNeeNova, Movimento mov) throws DAOException{
		NeeDao dao = getDAO(NeeDao.class, mov);
		boolean existeNecessidadeEspecial = false;
		try {
			Collection<PessoaNecessidadeEspecial> colTipos = dao.findNecessidadesEspeciaisByDiscente(discente);
			for (PessoaNecessidadeEspecial pNEE : colTipos) {
				if ( pNEE .getTipoNecessidadeEspecial().getId() == tipoNeeNova.getTipoNecessidadeEspecial().getId() )
					existeNecessidadeEspecial = true;
			}
		} finally {
			dao.close();
		}
		return existeNecessidadeEspecial;
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens mensagens = new ListaMensagens();
		((MovimentoCadastro)mov).setMensagens(mensagens);
	}
	
}