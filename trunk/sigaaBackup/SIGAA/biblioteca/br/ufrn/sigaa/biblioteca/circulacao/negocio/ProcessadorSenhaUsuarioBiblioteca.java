/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Criado em: 06/10/2008
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dao.CadastroUsuarioBibliotecaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>Classe que insere ou atualiza a senha do usu�rio na biblioteca.</p>
 * 
 * @author Jadson
 * @version 1.0 Cria��o da classe
 * @version 1.5 24/07/2013 - Adi��o da parte do termo de ades�o nas bibliotecas.
 */
public class ProcessadorSenhaUsuarioBiblioteca extends AbstractProcessador{
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		UsuarioBibliotecaDao dao = null;
		
		MovimentoSenhaUsuarioBiblioteca movimento = (MovimentoSenhaUsuarioBiblioteca) mov;
		
		try {

			dao = getDAO(UsuarioBibliotecaDao.class, mov);

			validate(movimento);
			
			if (SigaaListaComando.CADASTRAR_SENHA_BIBLIOTECA.equals(mov.getCodMovimento())){
				dao.create(movimento.getUsuarioBiblioteca());
				
				if(isTermoAdesaoAtivo() && movimento.getTermoAdesao() != null )
					persisteTermoAdesao(movimento, movimento.getUsuarioBiblioteca());
				
			}else{
				// Depois de criado, somente a senha pode ser alterada, se mudar o v�nculo tem que quitar o anterior, para pegar o novo
				if(movimento.isAtualizaVinculo()){
					dao.updateFields(UsuarioBiblioteca.class, movimento.getUsuarioBiblioteca().getId()
						, new String[]{"senha", "vinculo", "identificacaoVinculo"}
						, new Object[]{movimento.getUsuarioBiblioteca().getSenha(), movimento.getUsuarioBiblioteca().getVinculo(), movimento.getUsuarioBiblioteca().getIdentificacaoVinculo()} );
				}else	
					dao.updateField(UsuarioBiblioteca.class, movimento.getUsuarioBiblioteca().getId(), "senha", movimento.getUsuarioBiblioteca().getSenha() );
			}

		} finally {
			if (dao != null) {dao.close();
			}
		}

		return null;
	}



	/** 
	 * Verifica se a funcionalidade de termo est� ativa no sistema.  Est� ativa se o usu�rio informou o texto do tempo, se tiver em branco � porque n�o � usado no sistema.
	 *
	 * Isso � mais uma variabilidade da linha de produto da biblioteca disponibilizada para as institui��es cooperantes usarem ou n�o.!
	 * 
	 * Para mais informa��es sobre linha de produto, verificar: {@link http://www.sei.cmu.edu/productlines/}
	 */
	private boolean isTermoAdesaoAtivo() {
		return StringUtils.notEmpty( ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.TEXTO_DE_ADESAO_AO_SISTEMA_DE_BIBLIOTECAS) );
	}



	/** Salva o termo para o usu�rio biblioteca e com as informa��es do usu�rio no momento que ele assinou, se mudar, fica o que estava no momento da assinatura ! */
	private void persisteTermoAdesao(MovimentoSenhaUsuarioBiblioteca movimento, UsuarioBiblioteca usuarioBiblioteca) throws DAOException {
		
		CadastroUsuarioBibliotecaDAO daoTermoAdesao = null;
		
		try {
			
			daoTermoAdesao = getDAO(CadastroUsuarioBibliotecaDAO.class, movimento);
			Object[] informacoesTermoAdesao = daoTermoAdesao.findInformacoesTermoAdesao(usuarioBiblioteca.getId(), usuarioBiblioteca.getVinculo());
			if (informacoesTermoAdesao[0] != null) {
				movimento.getTermoAdesao().setNomeUnidade((String) informacoesTermoAdesao[0]);
			}
			if (usuarioBiblioteca.getVinculo() != VinculoUsuarioBiblioteca.DOCENTE_EXTERNO) {
				Long matricula = Long.valueOf(informacoesTermoAdesao[1].toString());
				movimento.getTermoAdesao().setMatricula(matricula);
			}
			movimento.getTermoAdesao().geraHash();
			daoTermoAdesao.create(movimento.getTermoAdesao());
			
		} finally {
			if (daoTermoAdesao != null)
				daoTermoAdesao.close();
		}
	}
	
	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		UsuarioBibliotecaDao dao = null;
		EmprestimoDao daoEmpresimo = null;
		
		MovimentoSenhaUsuarioBiblioteca movimento = (MovimentoSenhaUsuarioBiblioteca) mov;
		
		ListaMensagens erros = new ListaMensagens();
		
		erros.addAll( movimento.getUsuarioBiblioteca().validate());
		
		try {

			dao = getDAO(UsuarioBibliotecaDao.class, mov);
			daoEmpresimo = getDAO(EmprestimoDao.class, mov);
			
			Pessoa pessoaPassada = dao.findByPrimaryKey(movimento.getUsuarioBiblioteca().getPessoa().getId(), Pessoa.class, "cpf_cnpj", "tipo", "valido", "passaporte", "internacional");
			pessoaPassada.setId(movimento.getUsuarioBiblioteca().getPessoa().getId());
			
			VerificaSituacaoUsuarioBibliotecaUtil.verificaDadosPessoaCorretosUtilizarBiblioteca(pessoaPassada);
			
			
			
			if (SigaaListaComando.CADASTRAR_SENHA_BIBLIOTECA.equals(mov.getCodMovimento())){
				
				long qtdUsuarioAtivosNaoQuitados = dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(movimento.getUsuarioBiblioteca().getPessoa().getId());
				
				if(qtdUsuarioAtivosNaoQuitados > 0)
					throw new NegocioException ("Cadastro do usu�rio duplicado. Por favor, entre em contato com o suporte.");
				
				verificaPunicaoesUsuarioOutrasContasBiblioteca(movimento);
				verificaEmprestimosUsuarioOutrasContasBiblioteca(daoEmpresimo, movimento);
				
			}else{ // ATUALIZANDO
				
				long qtdUsuarioAtivosNaoQuitados = dao.countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(movimento.getUsuarioBiblioteca().getPessoa().getId());
				
				if(qtdUsuarioAtivosNaoQuitados > 1)
					throw new NegocioException ("Cadastro do usu�rio duplicado. Por favor, entre em contato com o suporte.");
				if(qtdUsuarioAtivosNaoQuitados == 0)
					throw new NegocioException ("Cadastro do usu�rio ainda n�o realizado, por isso a senha n�o p�de ser atualizada.");
				
				if(movimento.isAtualizaVinculo()){
					if(daoEmpresimo.countEmprestimosAtivosByUsuario(movimento.getUsuarioBiblioteca()) > 0){
						throw new NegocioException ("O v�nculo atual para a utiliza��o da biblioteca n�o pode ser alterado, pois o usu�rio possui empr�stimos realizados com ele. Primeiro devolva todos os empr�stimos para depois alterar o v�nculo.");
					}
					
					verificaPunicaoesUsuarioOutrasContasBiblioteca(movimento);
					verificaEmprestimosUsuarioOutrasContasBiblioteca(daoEmpresimo, movimento);
				}
				
				UsuarioBiblioteca usuarioBibliotecaAtual =  movimento.getUsuarioBiblioteca();
				
				if(usuarioBibliotecaAtual != null && usuarioBibliotecaAtual.getVinculo() != null && usuarioBibliotecaAtual.getIdentificacaoVinculo() != null){ 	
					
					ObtemVinculoUsuarioBibliotecaStrategy estrategia = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
					
					if(! estrategia.isVinculoAtivo(usuarioBibliotecaAtual.getPessoa().getId(), usuarioBibliotecaAtual.getVinculo(), usuarioBibliotecaAtual.getIdentificacaoVinculo())){
						throw new NegocioException ("O v�nculo atualmente utilizado expirou, ser� preciso primeiro quitar seu v�nculo de "+usuarioBibliotecaAtual.getVinculo().getDescricao()
								+" para poder se cadastrar com um novo v�nculo para usar os servi�os da biblioteca");
					}
					
				}
				
			}
		
			checkValidation(erros);
			
		} finally {
			if (dao != null) dao.close();
			if (daoEmpresimo != null) daoEmpresimo.close();
		}
	}


	/**
	 * Se est� cadastrando uma novo conta na biblioteca ou alterando o v�nculo 
	 * N�o deixa o usu�rio alterar o v�nculo se ele est� suspens�o ou multado, para ele n�o escapar da puni��o pegando outro v�nculo //
	 *
	 * @void
	 */
	private void verificaPunicaoesUsuarioOutrasContasBiblioteca(MovimentoSenhaUsuarioBiblioteca movimento) throws DAOException, NegocioException {
		
		if(movimento.getContasUsuarioBiblioteca() != null && movimento.getContasUsuarioBiblioteca().size() > 0 ){
			
			UsuarioBiblioteca contaBiblioteca = movimento.getContasUsuarioBiblioteca().get(0);
			
			// S� precisa verificar para 1 conta, porque j� busca em todas //
			List<SituacaoUsuarioBiblioteca> situacoesUsuario = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
					contaBiblioteca.getIdentificadorPessoa()
					, contaBiblioteca.getIdentificadorBiblioteca());
		
			if(situacoesUsuario.size() > 0){
				String mensagem = "N�o � poss�vel cadastrar ou alterar o v�nculo pois : ";
				
				for (SituacaoUsuarioBiblioteca situacao : situacoesUsuario) {
					mensagem += situacao.getDescricaoCompleta()+"  ";
				}
				
				throw new NegocioException (mensagem);
			}
		
		}
	}
	
	
	/**
	 * Se est� cadastrando uma novo conta na biblioteca ou alterando o v�nculo 
	 * N�o deixa o usu�rio alterar o v�nculo se ele possui empr�stimos aberto em outros v�nculos, para ele n�o 
	 * criar outro v�nculo e num devolver o anterior. //
	 *
	 * @void
	 */
	private void verificaEmprestimosUsuarioOutrasContasBiblioteca(EmprestimoDao daoEmpresimo, MovimentoSenhaUsuarioBiblioteca movimento) throws DAOException, NegocioException {
		
		if(movimento.getContasUsuarioBiblioteca() != null ){
			for(UsuarioBiblioteca contaBiblioteca: movimento.getContasUsuarioBiblioteca()){
				
				if(daoEmpresimo.countEmprestimosAtivosByUsuario(contaBiblioteca) > 0){
					throw new NegocioException ("N�o � poss�vel cadastrar ou alterar o v�nculo pois o usu�rio possui empr�stimos aberto em outros v�nculos .");
				}
			}
		}
	}
	
	
}
