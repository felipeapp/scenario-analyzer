package br.ufrn.sigaa.ava.administracao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.administracao.dominio.ConfiguracaoPortaArquivos;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed bean para cadastro de configurações do porta arquivos.
 *
 * @author Diego Jácome
 *
 */
@Component("configuracaoPortaArquivos") @Scope("request")
public class ConfiguracaoPortaArquivosMBean  extends SigaaAbstractController<ConfiguracaoPortaArquivos> {

	/** Tamanho de um byte */
	private static final long BYTE = 1;
	/** Tamanho de um kilobyte */
	private static final long KILO_BYTE = BYTE * 1024;
	/** Tamanho de um megabyte */
	private static final long MEGA_BYTE = KILO_BYTE * 1024;
	/** Tamanho de um gigabyte, usado para trasformar em bytes o tamanho do porta arquivos */
	private static final long GIGA_BYTE = MEGA_BYTE * 1024;
	
	/** Docente que terá seu porta-arquivos configurado. */
	private Servidor docente;
	
	/** Docente Externo que terá seu porta-arquivos configurado. */
	private DocenteExterno docenteExterno;
	
	/** Capacidade do porta-arquivos */
	private Double capacidade;
	
	/**
	 * Construtor padrão
	 * @throws Exception
	 */
	public ConfiguracaoPortaArquivosMBean() {
		initObj();
	}
	
	/**
	 * Salva uma configuração.<br/><br/>
	 * Método chamado pela seguinte JSP: /ambientes_virtuais/ConfiguracaoPortaArquivos/form.jsp
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 * 
	 */
	public String salvar() throws SegurancaException, ArqException,NegocioException {

		validarPortaArquivos();
		
		if (hasErrors())
			return null;
		
		if ( obj.getId() == 0 )
			prepareMovimento(ArqListaComando.CADASTRAR);
		else {
			setConfirmButton("Atualizar");
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
		}
		super.cadastrar();
		return iniciar();
	}

	/**
	 * Valida o porta-arquivos.<br/><br/>
	 * Método não chamado por JSPs
	 * @throws DAOException
	 * 
	 */
	private void validarPortaArquivos() throws DAOException {
		if ( isEmpty(obj.getUsuario()) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
		if ( obj.getTamanhoMaximoPortaArquivos() <= 0 )
			addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Capacidade do Porta Arquivos" , "0");	
		if ( obj.getTamanhoMaximoPortaArquivos() < 2*GIGA_BYTE )
			addMensagemErro("A capacidade mínima para o porta-arquivos é 2 gigabytes.");
		
		if ( !isEmpty(obj.getUsuario())) {
			long tamanhoOcupado = getTamanhoTotalOcupado(obj.getUsuario());
			BigDecimal capacidade =  new BigDecimal(obj.getTamanhoMaximoPortaArquivos());
			BigDecimal giga = new BigDecimal(GIGA_BYTE);
			Double capacidadeDecimal = capacidade.divide(giga).doubleValue();
			
			if ( obj.getTamanhoMaximoPortaArquivos() < tamanhoOcupado )
				addMensagemErro("O usuário já ocupa "+capacidadeDecimal+ " gigabytes do seu porta-arquivo. " +
						"Não é possível diminuir a capacidade do porta-arquivos para um tamanho menor que o atualmente ocupado");
		}
	}
	
	/**
	 * Carrega a configuração de um porta arquivos de um docente.<br/><br/>
	 * Método chamado pela seguinte JSP: /ambientes_virtuais/ConfiguracaoPortaArquivos/form.jsp
	 * @throws DAOException
	 */
	public void carregaConfiguracaoDocente() throws DAOException{
		UsuarioDao dao = null;
		try {
						
			dao = getDAO(UsuarioDao.class);
			ConfiguracaoPortaArquivos config = null;
			
			Usuario usuario = null;
			if ( docente.getId() != 0 ) {
				docente = dao.findAndFetch(docente.getId(), Servidor.class, "pessoa");
				usuario = dao.findByServidor(docente);
			}	
				
			if ( usuario != null ) {
				obj.setUsuario(usuario);
				config = dao.findByExactField(ConfiguracaoPortaArquivos.class, "usuario.id", usuario.getId(), true);
			}
			
			if ( config != null ) {
				obj = config;
				BigDecimal capacidadeLong =  new BigDecimal(obj.getTamanhoMaximoPortaArquivos());
				BigDecimal giga = new BigDecimal(GIGA_BYTE);
				capacidade = capacidadeLong.divide(giga).doubleValue();
			} 
		}finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Carrega a configuração de um porta arquivos de um docente externo.<br/><br/>
	 * Método chamado pela seguinte JSP: /ambientes_virtuais/ConfiguracaoPortaArquivos/form.jsp
	 * @throws DAOException
	 */
	public void carregaConfiguracaoDocenteExterno() throws DAOException{
		UsuarioDao dao = null;
		try {
						
			dao = getDAO(UsuarioDao.class);
			ConfiguracaoPortaArquivos config = null;
			
			Usuario usuario = null;
			if ( docenteExterno.getId() != 0 ) 
				usuario = dao.findByDocenteExterno(docenteExterno.getId());
				
			if ( usuario != null ) {
				obj.setUsuario(usuario);
				config = dao.findByExactField(ConfiguracaoPortaArquivos.class, "usuario.id", usuario.getId(), true);
			}
			
			if ( config != null ) {
				obj = config;
				BigDecimal capacidadeLong =  new BigDecimal(obj.getTamanhoMaximoPortaArquivos());
				BigDecimal giga = new BigDecimal(GIGA_BYTE);
				capacidade = capacidadeLong.divide(giga).doubleValue();
			} 
		}finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Inicia o caso de uso de salvar as configurações do porta arquivos.<br/><br/>
	 * Método chamado pela seguinte JSP: /ambientes_virtuais/ConfiguracaoPortaArquivos/gestor.jsp
	 * 
	 */
	public String iniciar() {
		
		initObj();
		
		return forward("/ambientes_virtuais/ConfiguracaoPortaArquivos/form.jsp");
	}
	
	/**
	 * Popula o objeto.<br/><br/>
	 * Método não chamado por JSPs
	 */
	private void initObj () {
		obj = new ConfiguracaoPortaArquivos();
		obj.setTamanhoMaximoPortaArquivos((long) 2.0*GIGA_BYTE);
		setDocente( new Servidor());
		setDocenteExterno( new DocenteExterno() );
	}
	
	@Override
	public String getDirBase() {
		return "/ambientes_virtuais/ConfiguracaoPortaArquivos";
	}
	
	@Override
	public String forwardCadastrar() {
		return "/ambientes_virtuais/ConfiguracaoPortaArquivos/form.jsp";
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Servidor getDocente() {
		return docente;
	}
	
	/**
	 * Retorna o tamanho total ocupado pelo usuário no Porta arquivos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private long getTamanhoTotalOcupado(Usuario usuario) throws DAOException {
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		return dao.findTotalOcupadoByUsuario(usuario.getId());
	}

	public void setCapacidade(Double capacidade) {
		if ( capacidade != null ) {
			long capacidadeLong = (long) (capacidade*GIGA_BYTE);
			obj.setTamanhoMaximoPortaArquivos(capacidadeLong);
		} else
			obj.setTamanhoMaximoPortaArquivos(0);
		this.capacidade = capacidade;
	}

	public Double getCapacidade() {
		BigDecimal capacidadeLong =  new BigDecimal(obj.getTamanhoMaximoPortaArquivos());
		BigDecimal giga = new BigDecimal(GIGA_BYTE);
		capacidade = capacidadeLong.divide(giga).doubleValue();
		return capacidade;
	}
	
}
