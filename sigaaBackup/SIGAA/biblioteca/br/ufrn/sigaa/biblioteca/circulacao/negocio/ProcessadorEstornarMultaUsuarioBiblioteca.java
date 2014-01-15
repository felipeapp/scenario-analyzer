/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.MultaUsuariosBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca.StatusMulta;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 * <p> Processador com as regras para estornas as multas do sistema. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorEstornarMultaUsuarioBiblioteca extends AbstractProcessador{

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
	
		MovimentoEstornarMultaUsuarioBiblioteca movi = (MovimentoEstornarMultaUsuarioBiblioteca) mov;
		
		validate(movi);
		
		UsuarioBibliotecaDao dao = null;
		
		try{
			dao = getDAO(UsuarioBibliotecaDao.class, mov);
		
			MultaUsuarioBiblioteca multaASerEstornada = movi.getMulta();
			
			Date dataEstorno = new Date();
			
			dao.updateFields(MultaUsuarioBiblioteca.class, multaASerEstornada.getId(), new String[]{"ativo", "usuarioEstorno", "dataEstorno", "motivoEstorno"}, 
					new Object[]{false, movi.getUsuarioLogado(),  dataEstorno, multaASerEstornada.getMotivoEstorno()} );
			
			
			/* *****************************************************************************************
			 * Retira a referência para todas as multa que possuiam a mesma GRU da multa estornada
			 * Isso quando foi gerado uma única GRU para várias multas e uma deles foi estornada
			 * Será preciso reimprimir a GRU gerada anteriormente porque uma multa presente na GRU 
			 * anterior não existe mais, então o valor deve ser menor 
			 * *******************************************************************************************/
			
			dao.update(" UPDATE biblioteca.multa_usuario_biblioteca multa set id_gru_quitacao = NULL " +
					  " WHERE multa.id_multa_usuario_biblioteca <> ? " +
					  " AND id_gru_quitacao = ? AND multa.ativo = ? AND multa.status = ? "
					  , new Object[]{multaASerEstornada.getId(), multaASerEstornada.getIdGRUQuitacao(), true, new Integer(StatusMulta.EM_ABERTO.toString()) });
			
			enviaEmailAvisoEstornoMulta(dao, multaASerEstornada.getId(), multaASerEstornada.getValorFormatado(), multaASerEstornada.getMotivoEstorno()
					,  movi.getUsuarioDaMulta(), multaASerEstornada.getInfoIdentificacaoMultaGerada(), dataEstorno);
	
		}finally{
			if(dao != null ) dao.close();
		}
		
		
		return null;
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoEstornarMultaUsuarioBiblioteca movi = (MovimentoEstornarMultaUsuarioBiblioteca) mov;
		
		MultaUsuariosBibliotecaDao dao = null;
		
		try{
			
			dao = getDAO(MultaUsuariosBibliotecaDao.class, movi);
		
			MultaUsuarioBiblioteca multaBanco = dao.findByPrimaryKey(movi.getMulta().getId(), MultaUsuarioBiblioteca.class, new String[]{"status", "ativo"});
		
			if( ! multaBanco.isAtivo() ){
				lista.addErro("A multa já foi estornada.");
			}
			
			if(multaBanco.getStatus() == StatusMulta.QUITADA_MANUALMENTE ||
					multaBanco.getStatus() == StatusMulta.QUITADA_AUTOMATICAMENTE){
				lista.addErro("A multa não pode ser estornada porque ela já foi quitada.");
			}
			
			// Erros do programador //
			if(movi.getMulta() == null){
				throw new IllegalArgumentException("A multa a ser paga não foi informada");
			}
			
			if(StringUtils.isEmpty(movi.getMulta().getMotivoEstorno() )  ){
				lista.addErro("O motivo do estorno não foi informado ");
			}
			
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)
					&& ! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO)){
				lista.addErro("Caro usuário(a), você não tem permissão para estornar multas no sistema");
			}
			
				
				
			// Retorna o id da unidade do material do emprestimo da suspensão para verificar a permissão do usuário
			Integer idUnidadeMulta = dao.findUnidadeDaMulta(movi.getMulta().getId() );
			
			if ( idUnidadeMulta != null ) { // não é multa manual
			
			
				if(! movi.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
					
					List<Integer> unidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(movi.getUsuarioLogado(),
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
				
					if ( ! unidades.contains(idUnidadeMulta) ) {
						NegocioException ne = new NegocioException();
						ne.getListaMensagens().addErro("O senhor(a) não tem permissão para estornar multas de empréstimos " +
								"feitos em outras bibliotecas.");
						throw ne;
					}
				}
			}
			
			dao.detach(multaBanco);
			
	
		}finally{
			
			if(dao != null ) dao.close();
			
			checkValidation(lista);
		}
	
		
	}

	
	/**
	 *   <p>Formata os dados para enviar o email de aviso de estorna da multa.</p>
	 *
	 * @param daoUsuario
	 * @param emp
	 * @param dataEstorno
	 * @param usuarioLocado
	 * @throws DAOException
	 */
	private void enviaEmailAvisoEstornoMulta(UsuarioBibliotecaDao daoUsuario, int idMulta, String valorMultaFormatado, String motivoEstorno, UsuarioBiblioteca usuarioDaMulta, String dadosIdentificacaoMulta, Date dataEstorno) throws DAOException{
		
		Object[] informacoesUsuario =  daoUsuario.findNomeEmailUsuarioBiblioteca(usuarioDaMulta);
		
		String codigoAutenticacao =  BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idMulta, dataEstorno);
		
		String assunto = " Confirmação de Estorno de Multa ";
		String titulo = " Multa Estornada ";
		String mensagemUsuario = " A multa abaixo foi estornada, não será mais necessário realizar o seu pagamento: ";
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, "Valor da Multa: "+valorMultaFormatado, dadosIdentificacaoMulta, null, null
				, "Motivo do estorno: "+motivoEstorno, null,  codigoAutenticacao, null);
	}

}
