/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/**
 * Processador responsável pelo cadastro de dados pessoais e foto do candidato.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorPessoaVestibular extends AbstractProcessador {

	/** Persiste os dados pessoais do candidato.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoPessoaVestibular movPV = (MovimentoPessoaVestibular) mov;
		PessoaVestibular pessoaVestibular = movPV.getPessoaVestibular();
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ATUALIZAR_EMAIL_CANDIDATO)) {
			atualizarEmail(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.RECUPERAR_SENHA_VESTIBULAR)) {
			recuperarSenha(mov);
		} else {
			GenericDAO dao = getGenericDAO(mov);
			InscricaoVestibularDao ivDao = getDAO(InscricaoVestibularDao.class, mov);
			PessoaVestibularDao pvDao = getDAO(PessoaVestibularDao.class, mov);
			try {
				// Persistir arquivo com a foto
				if (movPV.getFoto() != null ) {
					// remove a foto anterior
					if (pessoaVestibular.getIdFoto() != null) {
						EnvioArquivoHelper.removeArquivo(pessoaVestibular.getIdFoto());
					}
					int idFoto = EnvioArquivoHelper.getNextIdArquivo();
					EnvioArquivoHelper.inserirArquivo(idFoto,
							movPV.getFoto().getBytes(),
							movPV.getFoto().getContentType(),
							movPV.getFoto().getName());
					pessoaVestibular.setIdFoto( idFoto );
					// quando uma foto é inserida, o status da foto é definida como não analisada.
					pessoaVestibular.setStatusFoto(new StatusFoto(StatusFoto.NAO_ANALISADA));
				}
				if (movPV.isEnviaEmail())
					enviaEmailCadastro(pessoaVestibular, movPV.getSenhaAberta());
				pessoaVestibular.setValidadeRecuperacaoSenha(null);
				pessoaVestibular.setChaveRecuperacaoSenha(null);
				// verifica se há dados pessoais que foram migrados
				PessoaVestibular pessoaDB = pvDao.findByCPF(pessoaVestibular.getCpf_cnpj());
				Collection<InscricaoVestibular> inscricoes = ivDao.findByCpf(pessoaVestibular.getCpf_cnpj(), null);
				PessoaVestibular pessoaMigrada = null;
				if (pessoaDB != null && pessoaDB.isMigrada() && pessoaDB.getId() != pessoaVestibular.getId()) {
					pessoaMigrada  = pessoaDB;
				}
				if (pessoaMigrada != null) {
					// seta o CPF antigo para nulo / restrição de CPF único
					pessoaMigrada.setCpf_cnpj(null);
					dao.update(pessoaMigrada);
					// cadastra os dados atuais
					pessoaVestibular.setDadosAnteriores(pessoaMigrada);
					dao.create(pessoaVestibular);
					// atualiza as inscrições anteriores que foram migradas p/ a referência nova
					if (!isEmpty(inscricoes)) {
						for (InscricaoVestibular inscricao : inscricoes) {
							inscricao.setPessoa(pessoaVestibular);
							dao.update(inscricao);
						}
					}
				} else {
					pessoaVestibular.setMigrada(false);
					dao.createOrUpdate(pessoaVestibular);
				}
			} catch (IOException e) {
				throw new ArqException(e);
			} finally {
				dao.close();
				ivDao.close();
			}
		}
		return pessoaVestibular;
	}
	
	/**
	 * Atualiza apenas o email do candidato.
	 * @param mov
	 * @throws DAOException
	 */
	private void atualizarEmail(Movimento mov) throws DAOException {
		MovimentoPessoaVestibular movPV = (MovimentoPessoaVestibular) mov;
		PessoaVestibular pessoaVestibular = movPV.getPessoaVestibular();
		GenericDAO dao = getGenericDAO(mov);
		dao.updateField(PessoaVestibular.class, pessoaVestibular.getId(), "email", pessoaVestibular.getEmail());
	}

	/** Envia um e-mail para o candidato com instruções de como recuperar a senha.
	 * @param mov
	 * @throws DAOException
	 */
	private void recuperarSenha(Movimento mov) throws DAOException{
		MovimentoPessoaVestibular movPV = (MovimentoPessoaVestibular) mov;
		PessoaVestibular pessoaVestibular = movPV.getPessoaVestibular();
		// define a chave e data de validade da recuperação de senha
		GenericDAO dao = getGenericDAO(mov);
		Date data = new Date();
		dao.updateField(PessoaVestibular.class, pessoaVestibular.getId(), "validadeRecuperacaoSenha", CalendarUtils.adicionaDias(data, 1));
		String hash = UFRNUtils.toMD5(pessoaVestibular.getCpf_cnpjString() + data.toString());
		dao.updateField(PessoaVestibular.class, pessoaVestibular.getId(), "chaveRecuperacaoSenha", hash);
		// envia o e-mail
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setEmail( pessoaVestibular.getEmail() );
		mail.setNome( pessoaVestibular.getNome() );
		mail.setAssunto("Vestibular "+RepositorioDadosInstitucionais.get("siglaInstituicao")+": recuperação de senha.");
		String saudacao;
		if (pessoaVestibular.getSexo() == Pessoa.SEXO_MASCULINO)
			saudacao = "Caro " + pessoaVestibular.getNome() +", ";
		else 
			saudacao = "Cara " + pessoaVestibular.getNome() +", ";
		mail.setMensagem("<html><body>" + saudacao + "<br/><br/>" + 
					"Foi solicitado em " + Formatador.getInstance().formatarData(data) +
					" a alteração de sua senha de acesso à Área Pessoal de Inscrição para o Vestibular.<br/><br/>" +
					"Para alterar sua senha atual, clique no link abaixo: <br/><br/>" +
					"<a href=\""+RepositorioDadosInstitucionais.getLinkSigaa()+"/sigaa/public/vestibular/recuperaSenha?id=" + pessoaVestibular.getId() +
					"&chave=" + hash + "\">Redefinir Minha Senha</a><br/><br/>" +
					"Este pedido de recuperação de senha terá validade de 24 horas. Caso não tenha sido você quem o solicitou, " +
					"por favor ignore esta mensagem.<br/><br/>" +
					"Atenciosamente,<br/><br/>" +
					"COMPERVE<br/>" +
					"Comissão Permanente do Vestibular</body></html>");
		mail.setReplyTo( "comperve@comperve.ufrn.br");
		Mail.send(mail);
	}

	/** Envia um e-mail informando ao candidato que o cadastro foi realizado com sucesso.
	 * @param pessoaVestibular
	 * @param senha
	 */
	public void enviaEmailCadastro(PessoaVestibular pessoaVestibular, String senha) {
		//enviando email para candidato com os dados cadastrados
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setEmail( pessoaVestibular.getEmail() );
		mail.setNome( pessoaVestibular.getNome() );
		mail.setAssunto("Vestibular "+RepositorioDadosInstitucionais.get("siglaInstituicao")+": confirmação de cadastro no SIGAA");
		String saudacao = "";
		if (pessoaVestibular.getSexo() == Pessoa.SEXO_MASCULINO)
			saudacao = "Caro " + pessoaVestibular.getNome() +", ";
		else 
			saudacao = "Cara " + pessoaVestibular.getNome() +", ";
		if (ValidatorUtil.isEmpty(senha))
			senha = "sua senha";
		else
			senha = "sua senha: <b>" + senha+"</b>";
		mail.setMensagem("<html><body>"+saudacao +
				"<p>Seus dados pessoais foram cadastrados com sucesso no SIGAA.</p>"  +
				"<p>Para ter acesso à sua área exclusiva, quando solicitado, utilize o seu CPF e a "+senha+".<p>" +
				"<p>Na sua área exclusiva, você poderá:</p>" +
				"<ul>" +
				"<li> Realizar uma nova Inscrição para o Vestibular</li>" +
				"<li> Consultar a validação de sua inscrição</li>" +
				"<li> Emitir uma segunda via da GRU</li>" +
				"<li> Consultar seu extrato de desempenho</li>" +
				"<li> Além de outras opções</li>" +
				"</ul>" +
				"<p>Caso tenha dúvidas, sinta-se a vontade para contactar-nos pelo e-mail <a href=\"mailto: comperve@comperve.ufrn.br\">comperve@comperve.ufrn.br</a> ou pelo telefone (84) 3211-9203.</p>" +
				"<br/>" +
				"<p>A COMPERVE agradece.</p></body></html>");
		//usuário que esta enviando e email para resposta.
		mail.setFromName("SIGAA - VESTIBULAR");
		mail.setReplyTo( "comperve@comperve.ufrn.br");
		Mail.send(mail);
	}
	
	
	/** Envia um e-mail informando ao candidato que o cadastro foi realizado com sucesso.
	 * @param pessoaVestibular
	 * @param senha
	 */
	public void enviaEmailInscricao(PessoaVestibular pessoaVestibular, String senha) {
		//enviando email para candidato com os dados cadastrados
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setEmail( pessoaVestibular.getEmail() );
		mail.setNome( pessoaVestibular.getNome() );
		mail.setAssunto("Vestibular "+RepositorioDadosInstitucionais.get("siglaInstituicao")+": confirmação de inscrição no SIGAA");
		String saudacao = "";
		if (pessoaVestibular.getSexo() == Pessoa.SEXO_MASCULINO)
			saudacao = "Caro " + pessoaVestibular.getNome() +", ";
		else 
			saudacao = "Cara " + pessoaVestibular.getNome() +", ";
		if (ValidatorUtil.isEmpty(senha))
			senha = "sua senha";
		else
			senha = "sua senha: <b>" + senha+"</b>";
		mail.setMensagem("<html><body>"+saudacao +
				"<p>Sua inscrição foi cadastrada com sucesso no SIGAA.</p>"   +
				"<p>Para ter acesso à sua área exclusiva, quando solicitado, utilize o seu CPF e a "+senha+".<p>" +
				"<p>Na sua área exclusiva, você poderá:</p>" +
				"<ul>" +
				"<li> Realizar uma nova Inscrição para o Vestibular</li>" +
				"<li> Consultar a validação de sua inscrição</li>" +
				"<li> Emitir uma segunda via da GRU</li>" +
				"<li> Consultar seu extrato de desempenho</li>" +
				"<li> Além de outras opções</li>" +
				"</ul>" +
				"<p>Caso tenha dúvidas, sinta-se a vontade para contactar-nos pelo e-mail <a href=\"mailto: comperve@comperve.ufrn.br\">comperve@comperve.ufrn.br</a> ou pelo telefone (84) 3211-9203.</p>" +
				"<br/>" +
				"<p>A COMPERVE agradece.</p></body></html>");
		//usuário que esta enviando e email para resposta.
		mail.setFromName("SIGAA - VESTIBULAR");
		mail.setReplyTo( "comperve@comperve.ufrn.br");
		Mail.send(mail);
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
