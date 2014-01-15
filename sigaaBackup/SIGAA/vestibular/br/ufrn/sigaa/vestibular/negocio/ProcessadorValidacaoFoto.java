/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/** Processador respons�vel por atualizar o status das fotos 3x4.
 * 
 * @author �dipo Elder F. Melo
 */
public class ProcessadorValidacaoFoto extends AbstractProcessador {

	/** Atualiza os status das fotos de uma cole��o de candidatos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		validate(mov);
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		// cache de status de fotos
		Collection<StatusFoto> allStatus = dao.findAll(StatusFoto.class);
		
		ArrayList<Object> obj = (ArrayList<Object>) movimento.getColObjMovimentado(); 
		
		try {
			if (obj.get(0) instanceof PessoaVestibular) {
				Collection<PessoaVestibular> pessoas = (Collection<PessoaVestibular>) movimento.getColObjMovimentado();
				for (PessoaVestibular pessoa : pessoas) {
					if (pessoa.isStatusFotoAlterado() && pessoa.getNovoStatusFoto().getId() > 0) {
						dao.updateField(PessoaVestibular.class, pessoa.getId(), "statusFoto", pessoa.getNovoStatusFoto());
						dao.updateField(PessoaVestibular.class, pessoa.getId(), "fotoValidadaPor", movimento.getRegistroEntrada());
						for (StatusFoto status : allStatus) {
							if (status.getId() == pessoa.getNovoStatusFoto().getId() && !status.isValida() && status.getId() != StatusFoto.NAO_ANALISADA) {
								// recupera o e-mail do candidato.
								pessoa = dao.refresh(pessoa);
								enviaEmail(pessoa);
								break;
							}
						}
					}
				}
			}
			
			if (obj.get(0) instanceof InscricaoFiscal) {
				Collection<InscricaoFiscal> fiscais = (Collection<InscricaoFiscal>) movimento.getColObjMovimentado();
				for (InscricaoFiscal fiscal : fiscais) {
					if (fiscal.isStatusFotoAlterado() && fiscal.getNovoStatusFoto().getId() > 0) {
						fiscal.setNovoStatusFoto(dao.findByPrimaryKey(fiscal.getNovoStatusFoto().getId(), StatusFoto.class));
						dao.updateField(InscricaoFiscal.class, fiscal.getId(), "statusFoto", fiscal.getNovoStatusFoto());
						for (StatusFoto status : allStatus) {
							if (status.getId() == fiscal.getNovoStatusFoto().getId() && !status.isValida() && status.getId() != StatusFoto.NAO_ANALISADA) {
								// recupera o e-mail do candidato.
								fiscal = dao.refresh(fiscal);
								enviaEmailFiscal(fiscal);
								break;
							}
						}
					}
				}
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/** Envia um e-mail ao candidato que n�o teve a foto validada, sugerindo corrigi-la.
	 * @param pessoa
	 */
	private void enviaEmail(PessoaVestibular pessoaVestibular) {
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setEmail( pessoaVestibular.getEmail() );
		mail.setNome( pessoaVestibular.getNome() );
		mail.setAssunto("Vestibular "+RepositorioDadosInstitucionais.get("siglaInstituicao")+": " + pessoaVestibular.getStatusFoto().getDescricao());
		String saudacao;
		if (pessoaVestibular.getSexo() == Pessoa.SEXO_MASCULINO)
			saudacao = "Caro " + pessoaVestibular.getNome() +", ";
		else 
			saudacao = "Cara " + pessoaVestibular.getNome() +", ";
		mail.setMensagem("<html><body>" + saudacao + "<br/><br/>" + 
					"A foto enviada no seu cadastro foi avaliada pela COMPERVE em " + Formatador.getInstance().formatarData(new Date()) +
					" e foi constatada que n�o est� adequada para a correta identifica��o do candidato.<br/><br/>" +
					(ValidatorUtil.isEmpty(pessoaVestibular.getStatusFoto().getRecomendacao()) ? "" : pessoaVestibular.getStatusFoto().getRecomendacao() +	"<br/><br/>") +
					"Solicitamos que fa�a o login em sua �rea Pessoal do Cadastro do Vestibular e troque a foto.<br/><br/>" +
					"Atenciosamente,<br/><br/>" +
					"COMPERVE<br/>" +
					"Comiss�o Permanente do Vestibular</body></html>");
		mail.setReplyTo("comperve@comperve.ufrn.br");
		Mail.send(mail);
	}

	/** Envia um e-mail ao candidato que n�o teve a foto validada, sugerindo corrigi-la.
	 * @param pessoa
	 */
	private void enviaEmailFiscal(InscricaoFiscal fiscal) {
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setEmail( fiscal.getPessoa().getEmail() );
		mail.setNome( fiscal.getPessoa().getNome() );
		mail.setAssunto("Vestibular "+RepositorioDadosInstitucionais.get("siglaInstituicao")+": " + fiscal.getStatusFoto().getDescricao());
		String saudacao;
		if (fiscal.getPessoa().getSexo() == Pessoa.SEXO_MASCULINO)
			saudacao = "Caro " + fiscal.getPessoa().getNome() +", ";
		else 
			saudacao = "Cara " + fiscal.getPessoa().getNome() +", ";
		mail.setMensagem("<html><body>" + saudacao + "<br/><br/>" + 
					"A foto enviada no seu cadastro foi avaliada pela COMPERVE em " + Formatador.getInstance().formatarData(new Date()) +
					" e foi constatada que n�o est� adequada para a correta identifica��o do fiscal.<br/><br/>" +
					(ValidatorUtil.isEmpty(fiscal.getStatusFoto().getRecomendacao()) ? "" : fiscal.getStatusFoto().getRecomendacao() +	"<br/><br/>") +
					"Solicitamos que fa�a o login no sigaa e modifique a sua foto atrav�s do caminho:<br/><br/>" +
					"Portal Discente -> Outros -> Fiscal do Vestibular -> Inscri��o <br/><br/>"+
					"Atenciosamente,<br/><br/>" +
					"COMPERVE<br/>" +
					"Comiss�o Permanente do Vestibular</body></html>");
		mail.setReplyTo("comperve@comperve.ufrn.br");
		Mail.send(mail);
	}
	
	/** Valida os dados a persistir.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
