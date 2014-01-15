/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */
package br.ufrn.sigaa.estagio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import org.apache.commons.validator.EmailValidator;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.estagio.ConcedenteEstagioDao;
import br.ufrn.sigaa.arq.dao.estagio.EstagiarioDao;
import br.ufrn.sigaa.arq.dao.estagio.InteresseOfertaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.estagio.dominio.ConcedentePessoaFuncao;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.InteresseOferta;
import br.ufrn.sigaa.estagio.dominio.ParametrosEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pela a persistência dos dados relacionados 
 * a Ofertas de Estágio.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorInteresseOfertaEstagio extends AbstractProcessador {

	/** Persiste o interesse do discente na oferta de estágio
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoCadastro movimentoCadastro = (MovimentoCadastro) mov;
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_INTERESSE_OFERTA_ESTAGIO)){
			cadastrarInteresse(movimentoCadastro);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.SELECIONAR_INTERESSADO)){
			selecionarInteressado(movimentoCadastro);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.CANCELAR_INTERESSE_OFERTA_ESTAGIO)){
			cancelarInteresse(movimentoCadastro);
		}
		return null;
	}


	/** Cancela o interesse do discente na oferta de estágio
	 * @param movimentoCadastro
	 * @throws DAOException
	 */
	private void cancelarInteresse(MovimentoCadastro movimentoCadastro)
			throws DAOException {
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class, movimentoCadastro);
		try {
			InteresseOferta interesse = movimentoCadastro.getObjMovimentado();
			dao.updateField(InteresseOferta.class, interesse.getId(), "ativo", false);
		} finally {
			if (dao != null)
				dao.close();
		}
	}


	/** Cadastra o interesse do discente na oferta de estágio
	 * @param movimentoCadastro
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void cadastrarInteresse(MovimentoCadastro movimentoCadastro) throws NegocioException, ArqException, DAOException {
		validate(movimentoCadastro);
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class, movimentoCadastro);
		try {
			InteresseOferta interesse = movimentoCadastro.getObjMovimentado();
		
			/* Atualiza o Currículo lattes */
			String lattes = (String) movimentoCadastro.getObjAuxiliar();
			if (!isEmpty(lattes)){
				if (lattes.toLowerCase().indexOf("http://") < 0 && lattes.toLowerCase().indexOf("https://") < 0)
					lattes = "http://"+lattes;									
				PerfilPessoaDAO.getDao().updateLattes(lattes, interesse.getDiscente().getId());	
			}
			
			gravarArquivoCurriculo(movimentoCadastro);
			
			interesse.setAtivo(true);
			if (interesse.getId() == 0)				
				dao.create(interesse);
			else
				dao.update(interesse);
		} finally {
			if (dao != null)
				dao.close();
		}
	}


	/** Seleciona um discente para o estágio.
	 * @param movimentoCadastro
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void selecionarInteressado(MovimentoCadastro movimentoCadastro)
			throws NegocioException, ArqException, DAOException {
		validate(movimentoCadastro);
		InteresseOferta interesse = movimentoCadastro.getObjMovimentado();
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class, movimentoCadastro);
		PessoaDao daoPessoa = getDAO(PessoaDao.class, movimentoCadastro);
		ConcedenteEstagioDao daoConcedente = getDAO(ConcedenteEstagioDao.class, movimentoCadastro);
		try {
			Pessoa supervisor = (Pessoa) movimentoCadastro.getObjAuxiliar();					
			/* Cadastra o Supervisor caso não existir */
			Pessoa p = daoPessoa.findByCpf(supervisor.getCpf_cnpj());
			if (p == null){
				int idPessoa = SincronizadorPessoas.getNextIdPessoa();
				p = new Pessoa();
				p.setCpf_cnpj(supervisor.getCpf_cnpj());
				p.setNome(supervisor.getNome());
				p.setId(idPessoa);
				p.setTipo(Pessoa.PESSOA_FISICA);
				ProcessadorPessoa.anularTransientObjects(p);
				
				/**   Sincroniza com o banco comum */
				SincronizadorPessoas.usandoDataSource(Database.getInstance().getComumDs()).sincronizarPessoa(p);						
				
				dao.create(p);															
			} else {
				EstagiarioDao estagioDao = getDAO(EstagiarioDao.class, movimentoCadastro);
				try {
					if (estagioDao.countEstagiosBySupervisor(p) > ParametroHelper.getInstance().getParametroInt(
							ParametrosEstagio.MAXIMO_ESTAGIARIOS_POR_SUPERVISOR) ){
						throw new NegocioException("Número máximo de Estagiários excedido para o Supervisor informado.");
					}
				} finally {
					if (estagioDao != null)
						estagioDao.close();
				}						
			}
			/* Verifica se já existe o Supervisor cadastrado */
			ConcedenteEstagioPessoa concedentePessoa = daoConcedente.findPessoaByConcedenteFuncao(p, 
					interesse.getOferta().getConcedente(), ConcedentePessoaFuncao.SUPERVISOR);					

			if (ValidatorUtil.isEmpty(concedentePessoa)){
				/* Cria a Pessoa do Supervisor do Estágio caso não existir */
				concedentePessoa = new ConcedenteEstagioPessoa();
				concedentePessoa.setPessoa(p);
				concedentePessoa.setFuncao(new ConcedentePessoaFuncao(ConcedentePessoaFuncao.SUPERVISOR));
				concedentePessoa.setConcedente(interesse.getOferta().getConcedente());
				
				dao.create(concedentePessoa);												
			}
			
			/* Atribui os dados do estagiário e cria o estágio para análise */
			Estagiario e = new Estagiario();
			e.setInteresseOferta(interesse);
			e.setSupervisor(p);
			e.setDiscente(interesse.getDiscente());
			e.setConcedente(interesse.getOferta().getConcedente());
			e.setStatus(new StatusEstagio(StatusEstagio.EM_ANALISE));
			e.setValorBolsa(interesse.getOferta().getValorBolsa());
			e.setValorAuxTransporte(interesse.getOferta().getValorAuxTransporte());
			e.setDescricaoAtividades(interesse.getDescricaoAtividades());
			
			dao.create(e);
							
			/** Notifica o Coordenador do Curso do Discente
			 *  informando que foi selecionado no Estágio */
			notificarCoordenador(interesse, movimentoCadastro);					
			
			interesse.setSelecionado(true);
			dao.update(interesse);
		} finally {
			if (dao != null)
				dao.close();
			if (daoPessoa != null)
				daoPessoa.close();
			if (daoConcedente != null)
				daoConcedente.close();
		}
	}
	

	/** Persiste o arquivo pdf que o discente enviou.
	 * @param movCadastro
	 * @throws ArqException
	 */
	private void gravarArquivoCurriculo(MovimentoCadastro movCadastro) throws ArqException {
		InteresseOferta interesse = movCadastro.getObjMovimentado();
		UploadedFile arquivo = interesse.getArquivoCurriculo();
		if (arquivo != null ) {
			try {
				// remove o anterior
				if (interesse.getIdArquivoCurriculo() != null) {
					EnvioArquivoHelper.removeArquivo(interesse.getIdArquivoCurriculo());
				}
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo,
						arquivo.getBytes(),
						arquivo.getContentType(),
						arquivo.getName());
				interesse.setIdArquivoCurriculo(idArquivo);
			} catch (IOException e) {
				throw new ArqException(e);
			}
		}
		
	}


	/**
	 * Notifica o Coordenador informando da Seleção do Estágio.
	 * @param r
	 * @throws DAOException 
	 */
	private void notificarCoordenador(InteresseOferta i, Movimento mov) throws DAOException{
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class, mov);
		try {
			CoordenacaoCurso coordenacao = dao.findAtivoByData(new Date(), i.getDiscente().getCurso());

			if (coordenacao != null && !isEmpty(coordenacao.getServidor().getPessoa().getEmail())){
				StringBuffer mensagem = new StringBuffer();
				mensagem.append("Prezado(a) ");
				mensagem.append(coordenacao.getServidor().getNome()+", <br/><br/>");
				mensagem.append("Foi selecionado(a) o(a) aluno(a) "+i.getDiscente().getNome()+", matrícula "+i.getDiscente().getMatricula());
				mensagem.append(", do curso "+i.getDiscente().getCurso().getDescricao()+", para ocupar a vaga de estágio oferecida pela concedente ");
				mensagem.append(i.getOferta().getConcedente().getPessoa().getNome()+".");
							
				mensagem.append("<br/><br/>Este estágio está sujeito a Aprovação para que seja Consolidado.");
				
				mensagem.append("<br/><br/>Por favor, não responder este email.");
				
				enviarMensagem(coordenacao.getServidor().getPessoa(), mensagem.toString());				
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}		
	
	/**
	 * Método responsável pela construção e envio do email.
	 * @param p
	 * @param assunto
	 * @param conteudo
	 * @param complemento
	 */
	private void enviarMensagem(Pessoa p, String mensagem) {
		if (!ValidatorUtil.isEmpty(p.getEmail())){
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setAssunto("SIGAA - Seleção de Estagiários - MENSAGEM AUTOMÁTICA");
			mail.setMensagem(mensagem.toString());
			mail.setEmail(p.getEmail());
			mail.setNome(p.getNome());
			Mail.send(mail);
		}
	}		

	/** Valida os dados a serem persistidos
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (mov.getCodMovimento().equals(SigaaListaComando.SELECIONAR_INTERESSADO)){
			Pessoa supervisor = (Pessoa) ((MovimentoCadastro)mov).getObjAuxiliar();			
			InteresseOferta interesse = ((MovimentoCadastro)mov).getObjMovimentado();
			
			if (supervisor == null)
				throw new NegocioException("É necessário informar o Supervisor do Estágio.");
			
			if (supervisor.getCpf_cnpj() == null || supervisor.getCpf_cnpj() == 0 || !ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(supervisor.getCpf_cnpj()))
				throw new NegocioException("CPF do Supervisor Inválido.");
			
			if (ValidatorUtil.isEmpty(supervisor.getNome()))
				throw new NegocioException("Informe o nome do Supervisor.");

			if (ValidatorUtil.isEmpty(supervisor.getEmail()) || !EmailValidator.getInstance().isValid(supervisor.getEmail()))
				throw new NegocioException("Informe um Email Válido para o Supervisor.");			
			
			if (ValidatorUtil.isEmpty(interesse.getDescricaoAtividades()))
				throw new NegocioException("Informe a Descrição das Atividades.");
		}
			
	}

}
