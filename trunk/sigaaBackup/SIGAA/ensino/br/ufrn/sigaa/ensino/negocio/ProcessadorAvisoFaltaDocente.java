/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '30/04/2010'
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ResponsavelUnidadeDAO;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.DadosAvisoFaltaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocente;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Este processador é responsável por inserir e notificar os responsáveis pela falta de um docente.
 * 
 * @author Henrique André
 *
 */
public class ProcessadorAvisoFaltaDocente extends AbstractProcessador {

	/**
	 * Chamado pela arquitetura, executa a lógica de negócio presente neste processador
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException {

		validate(mov);
		
		MovimentoAvisoFaltaDocente movAvisoFalta = (MovimentoAvisoFaltaDocente) mov;
		AvisoFaltaDocente avisoFalta = movAvisoFalta.getAvisoFalta();

		if (movAvisoFalta.getCodMovimento().equals(SigaaListaComando.AVISAR_FALTA_DOCENTE)) {

			DadosAvisoFaltaDao dao = getDAO(DadosAvisoFaltaDao.class, mov);
			DadosAvisoFalta dadosAvisoFalta = dao.findByDocenteTurmaAula(avisoFalta.getDadosAvisoFalta().getIdProfessor(), 
					avisoFalta.getDadosAvisoFalta().getTurma().getId(),
					avisoFalta.getDadosAvisoFalta().getDataAula(),
					avisoFalta.getDadosAvisoFalta().isServidor());
			
			if (dadosAvisoFalta != null) {
				avisoFalta.setDadosAvisoFalta(dadosAvisoFalta);
			}
			else {
				dao.create(avisoFalta.getDadosAvisoFalta());
				notificarResponsaveis(movAvisoFalta);				
			}
			
			dao.create(avisoFalta);
			
		}

		return movAvisoFalta;
	}

	/**
	 * Busca os usuários responsáveis pelo controle do docente
	 *  
	 * @param movAvisoFalta
	 * @throws DAOException
	 */
	private void notificarResponsaveis(MovimentoAvisoFaltaDocente movAvisoFalta) throws DAOException {
		
		Turma turma = getGenericDAO(movAvisoFalta).findByPrimaryKey(movAvisoFalta.getAvisoFalta().getDadosAvisoFalta().getTurma().getId(), Turma.class);
		movAvisoFalta.getAvisoFalta().getDadosAvisoFalta().setTurma(turma);
		
		notificarDocente(movAvisoFalta);
		notificarChefes(movAvisoFalta);
	}

	/**
	 * Localiza e informa os chefes sobre o a falta de um docente.
	 */
	private void notificarChefes(MovimentoAvisoFaltaDocente movAvisoFalta) throws DAOException {
		
		List<UsuarioGeral> destinatarios = new ArrayList<UsuarioGeral>();
		
		Usuario chefeDpto = findChefeDepartamento(movAvisoFalta);
		if (chefeDpto != null){
			destinatarios.add(chefeDpto);
		}else{
		List<Usuario> chefeCentro = findDiretorCentro(movAvisoFalta);
		if( isNotEmpty(chefeCentro) )
			destinatarios.addAll(chefeCentro);
		}
		Mensagem msg = criarMensagem(movAvisoFalta);
		notificar(movAvisoFalta, destinatarios, msg);
	}
	
	/**
	 * Localiza e notifica o docente sobre sua falta.
	 */
	private void notificarDocente(MovimentoAvisoFaltaDocente movAvisoFalta) throws DAOException {
		
		List<UsuarioGeral> docente = findDocente(movAvisoFalta);
		
		if (ValidatorUtil.isEmpty(docente))
			return;
		
		Mensagem msg = criarMensagemDocente(movAvisoFalta);
		notificar(movAvisoFalta, docente, msg);
	}
	
	/**
	 * Notifica os responsáveis pela falta do professor
	 * OBS.: caso aconteça um erro no envio e a mensagem não seja enviada
	 * não vai aparecer nenhum erro ou stacktrace.
	 * 
	 * @param mov
	 * @param destinatarios
	 */
	private void notificar(MovimentoAvisoFaltaDocente mov, List<UsuarioGeral> destinatarios, Mensagem mensagem) {		
		
		if (mov == null || destinatarios == null) {
			throw new IllegalArgumentException();
		}
		
		UsuarioGeral destinatario = new UsuarioGeral();
		destinatario = destinatarios.get(0);
		
		String msgFoot = "<br/><br/>"
			+ "Mensagem Gerada pelo SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas";
		mensagem.setTitulo("[SIGAA] " + mensagem.getTitulo());
		mensagem.setMensagem(mensagem.getMensagem() + msgFoot);
		 
		
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		
		// Definir email
		mail.setFromName( RepositorioDadosInstitucionais.get("siglaSigaa") );
		mail.setEmail( destinatario.getEmail() );
		mail.setAssunto( mensagem.getTitulo() );
		mail.setMensagem( mensagem.getMensagem() );
		mail.setNome(destinatario.getPessoa().getNome());
		
		Mail.send(mail);	
		
		
		
	}

	/**
	 * Retorna o chefe de departamento ao qual a turma pertence
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Usuario findChefeDepartamento(MovimentoAvisoFaltaDocente mov) throws DAOException {

		Turma turma = mov.getAvisoFalta().getDadosAvisoFalta().getTurma();
		int idUnidade = turma.getDisciplina().getUnidade().getId();

		ServidorDao dao = getDAO(ServidorDao.class, mov);
		Servidor chefe = dao.findChefeByDepartamento(idUnidade);

		Usuario usuario = null;
		if (chefe != null) {
			UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
			usuario = uDao.findByServidor(chefe);
			
			if ( chefe.getPessoa().getId() == 0 )
				chefe = dao.findByPrimaryKey(chefe.getId(), Servidor.class, "id","siape","pessoa");
			
			
		}

		return usuario;
	}
	
	/**
	 * Retorna o diretor do centro ao qual a turma pertence
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private List<Usuario> findDiretorCentro(MovimentoAvisoFaltaDocente mov) throws DAOException {

		Turma turma = mov.getAvisoFalta().getDadosAvisoFalta().getTurma();
		int idUnidade = turma.getDisciplina().getUnidade().getUnidadeResponsavel().getId();

		ServidorDao dao = getDAO(ServidorDao.class, mov);
		Servidor diretor = dao.findDiretorByCentro(idUnidade);

		List<Usuario> usuarios = new ArrayList<Usuario>();
		if (diretor != null) {
			UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
			Usuario usuario = uDao.findByServidor(diretor);
			usuarios.add(usuario);
		}

		ResponsavelUnidadeDAO responsavelUnidadeDAO = getDAO(ResponsavelUnidadeDAO.class, mov);
		Responsavel responsabilidades = responsavelUnidadeDAO.findResponsavelAtualByUnidade(idUnidade, NivelResponsabilidade.CHEFE);
		
		if (responsabilidades != null) {
			
			PessoaGeral pessoaGeral = responsabilidades.getServidor().getPessoa();
			
			UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
			List<Usuario> usuarioDaMesmaPessoa = uDao.findByPessoa(new Pessoa(pessoaGeral.getId()));
			usuarios.addAll(usuarioDaMesmaPessoa);
		}
		
		return usuarios;
	}	
	
	/**
	 * Recupera o docente que leciona na turma.
	 * 
	 * @param movAvisoFalta
	 * @return
	 * @throws DAOException
	 */
	private List<UsuarioGeral> findDocente(MovimentoAvisoFaltaDocente movAvisoFalta) throws DAOException {
		UsuarioDao usuario = getDAO(UsuarioDao.class, movAvisoFalta);
		Servidor docenteUFRN = movAvisoFalta.getAvisoFalta().getDadosAvisoFalta().getDocente();
		DocenteExterno docenteExterno = movAvisoFalta.getAvisoFalta().getDadosAvisoFalta().getDocenteExterno();
		Usuario docente = null;
		
		
		List<UsuarioGeral> resultado = new ArrayList<UsuarioGeral>();
		if(docenteExterno != null)
			docente = usuario.findByPessoaParaEmail(docenteExterno.getPessoa());
		
		if(docenteUFRN != null)
			docente = usuario.findByPessoaParaEmail(docenteUFRN.getPessoa());
		
		resultado.add(docente);
		
		return resultado;
	}
	

	/**
	 * Cria a mensagem que será enviada aos responsáveis
	 * 
	 * @param mov
	 * @return
	 */
	private Mensagem criarMensagem(MovimentoAvisoFaltaDocente mov) {

		Servidor docenteUFRN = mov.getAvisoFalta().getDadosAvisoFalta().getDocente();
		DocenteExterno docenteExteno = mov.getAvisoFalta().getDadosAvisoFalta().getDocenteExterno();
		Turma turma = mov.getAvisoFalta().getDadosAvisoFalta().getTurma();
		Date dataAula = mov.getAvisoFalta().getDadosAvisoFalta().getDataAula();

		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEEE, dd 'de' MMMM 'de' yyyy");

		String docenteNome = ( docenteUFRN == null ? docenteExteno.getNome() : docenteUFRN.getNome() ); 
		
		String titulo = docenteNome + " não compareceu a aula";
		String conteudo = "Caro Chefe,<br/>" +
				"Um aluno da Turma "
				+ turma.getDisciplina().getNome()
				+ " informa que o docente "
				+ docenteNome
				+ " , responsável por ministrar essa disciplina, não compareceu à aula no(a) "
				+ sdf.format(dataAula) + "." +
				"<br/><br/>" +
				"Obs.: a homologação ou negação do aviso de falta pode ser realizada pelo Chefe de Departamento através da opção Sigaa -> Portal do Docente -> Chefia -> Docentes -> Listar/Homologar Avisos de Falta";

		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo.toUpperCase());
		mensagem.setMensagem(conteudo);
		mensagem.setSistema(Sistema.SIGAA);
		
		return mensagem;
	}
	
	/**
	 * Cria a mensagem que será enviada ao docente da turma.
	 * 
	 * @param mov
	 * @return
	 */
	private Mensagem criarMensagemDocente(MovimentoAvisoFaltaDocente mov) {

		Turma turma = mov.getAvisoFalta().getDadosAvisoFalta().getTurma();
		Date dataAula = mov.getAvisoFalta().getDadosAvisoFalta().getDataAula();

		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy");

		String titulo = "Comunicado de não comparecimento em sala de aula";
		
		String conteudo = "Caro docente,"
				+" <br/>"
				+ "Um aluno da Turma " + turma.getDescricaoSemDocente() + " (" + turma.getDescricaoHorario() + ") , informa que o(a) Sr.(a), enquanto docente desta disciplina, não compareceu a aula na data " + sdf.format(dataAula) + "."
				+" <br/>"
				+ "O ato do aluno não implicará em nenhuma consequência antes da averiguação e homologação por parte da chefia da Unidade Acadêmica . No caso da Chefia da Unidade Acadêmica homologar a não realização da aula, isto também não necessariamente implicará em uma falta, pois, o docente poderá apresentar eletronicamente um plano de reposição desta aula."
				+" <br/>"
				+ "A consequência da falta só ocorrerá frente a não apresentação de uma forma de reposição e do lançamento efetivo desta ausência no Sistema de Recursos Humanos da Instituição (SIGRH).";
		
		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo.toUpperCase());
		mensagem.setMensagem(conteudo);
		mensagem.setSistema(Sistema.SIGAA);

		return mensagem;
	}

	/**
	 * Valida o aviso de falta antes de persistir
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoAvisoFaltaDocente movAvisoFalta = (MovimentoAvisoFaltaDocente) mov;
		AvisoFaltaDocente avisoFalta = movAvisoFalta.getAvisoFalta();
		
		checkValidation( avisoFalta.validate() );
	}

}