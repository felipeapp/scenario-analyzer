/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/05/2009
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Declaração autenticada mostrando as disciplinas ministradas pelo docente
 * 
 * @author Henrique André
 */
@Component("declaracaoDisciplinasMinistradas") @Scope("request")
public class DeclaracaoDisciplinasMinistradasMBean extends SigaaAbstractController<Pessoa> implements AutValidator {

	public static final int EMITIR_DOCENTE_UFRN = 1;
	public static final int EMITIR_DOCENTE_EXTERNO = 2;
	private int acao;
	
	private Restricao restricao = new Restricao();
	private Parametro parametro = new Parametro();
	
	private EmissaoDocumentoAutenticado comprovante;
	
	private Collection<Turma> turmas;
	
	/**
	 * Iniciar a busca de docente da UFRN 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/graduacao/departamento.jsp </li></ul>
	 * 
	 * @return
	 */
	public String iniciarDocenteUFRN() {
		acao = EMITIR_DOCENTE_UFRN;
		return iniciar();
	}
	
	/**
	 * Iniciar a busca de docente externo
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/graduacao/departamento.jsp </li></ul>
	 * 
	 * @return
	 */	
	public String iniciarDocenteExterno() {
		acao = EMITIR_DOCENTE_EXTERNO;
		return iniciar();
	}	
	
	/**
	 * Redireciona para página de busca
	 * 
	 * @return
	 */
	private String iniciar() {
		return forward("/ensino/declaracao_disciplinas_ministradas/busca.jsp");	
	}
	
	/**
	 * Método responsável por gerar a declaração das disciplinas ministradas pelo docente.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/ensino/declaracao_disciplinas_ministradas/busca.jsp </li></ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String emitir() throws ArqException, NegocioException {
		if (getAcessoMenu().isDocente()){
			if (getDocenteExternoUsuario() != null){
				parametro.setDocenteExterno(getDocenteExternoUsuario());				
				acao = EMITIR_DOCENTE_EXTERNO;
			} else {
				parametro.setDocenteUFRN(getServidorUsuario());
				acao = EMITIR_DOCENTE_UFRN;			
			}
			
			if (!checarMesmaUnidade()) {
				addMensagemErro("O docente não pertence a seu departamento.");
				return null;
			}
		}
		
		validar(erros);
		
		if (hasErrors()) {
			parametro = new Parametro();
			return null;
		}
		
		turmas = buscarTurmas();
		
		if (isEmpty(turmas)) {
			addMensagemErro("O docente não possui registros de Disciplinas Ministradas..");
			return null;
		}
		
		return gerarDeclaracao();
	}

	/**
	 * Valida entrada do usuário
	 * @param lista
	 */
	private void validar(ListaMensagens lista) {
		if (acao == EMITIR_DOCENTE_UFRN) {
			
			ValidatorUtil.validateRequiredId(parametro.getDocenteUFRN().getId(), "Nome do Docente", lista);
			if (parametro.getDocenteUFRN().getId() != 0)
				ValidatorUtil.validateRequired(parametro.getDocenteUFRN().getPessoa().getNome(), "Nome do Docente", lista);
		}
		
		if (acao == EMITIR_DOCENTE_EXTERNO) {
			ValidatorUtil.validateRequiredId(parametro.getDocenteExterno().getId(), "Nome do Docente", lista);
			if (parametro.getDocenteExterno().getId() != 0)
				ValidatorUtil.validateRequired(parametro.getDocenteExterno().getPessoa().getNome(), "Nome do Docente", lista);
		}
	}

	/**
	 * Verifica se o professor é da unidade da secretária de departamento que está emitindo a declaração
	 * 
	 * @return
	 */
	private boolean checarMesmaUnidade() {

		if (acao == EMITIR_DOCENTE_EXTERNO)
			return parametro.getDocenteExterno().getUnidade().getId() == getDocenteExternoUsuario().getUnidade().getId();
		else
			return parametro.getDocenteUFRN().getUnidade().getId() == getServidorUsuario().getUnidade().getId();
	}

	/**
	 * Retorna o IDENTIFICADOR para ser usado no documento
	 * 
	 * @return
	 */
	private String getIdentificador() {

		if (acao == EMITIR_DOCENTE_UFRN)
			return String.valueOf( parametro.getDocenteUFRN().getSiape() );
		else
			return String.valueOf( parametro.getDocenteExterno().getPessoa().getCpf_cnpj()  );
	}
	
	
	/**
	 * Monta declaração com as turmas passadas
	 * 
	 * @param turmas
	 * @return
	 */
	private String gerarDeclaracao() {
		try{
			String dadosAuxiliares = gerarDadosAuxiliares(turmas);
			if (ValidatorUtil.isEmpty(comprovante)){
				comprovante = geraEmissao(TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR,
					getIdentificador(),	gerarSemente(dadosAuxiliares, getIdentificador()), 
					dadosAuxiliares, SubTipoDocumentoAutenticado.DECLARACAO_DISCIPLINA_MINISTRADA, false);	
			}
			Pessoa pessoa = null;
			String tipoIdentificador = null;
			String identificador = null;
			String cidade = null;
			
			if (acao == EMITIR_DOCENTE_UFRN) {
				pessoa = parametro.getDocenteUFRN().getPessoa();
				tipoIdentificador = "siape";
				identificador = String.valueOf(parametro.getDocenteUFRN().getSiape());				
				parametro.setDocenteUFRN( getGenericDAO().refresh(parametro.getDocenteUFRN()) );
				if(parametro.getDocenteUFRN().getUnidade().getMunicipio()!=null){				
					cidade = parametro.getDocenteUFRN().getUnidade().getMunicipio().getNome();
				}
			} else {
				pessoa = parametro.getDocenteExterno().getPessoa();
				tipoIdentificador = "cpf";
				identificador = Formatador.getInstance().formatarCPF_CNPJ(parametro.getDocenteExterno().getPessoa().getCpf_cnpj());				
				parametro.setDocenteExterno( getGenericDAO().refresh(parametro.getDocenteExterno()) );
				if(parametro.getDocenteExterno().getUnidade().getMunicipio()!=null){
					cidade = parametro.getDocenteExterno().getUnidade().getMunicipio().getNome();
				}
			}			
			
			parametro.textoDeclaracao = "Declaramos para os devidos fins que " + ( String.valueOf(pessoa.getSexo()).equals("F") ? "a Docente" : "o Docente" ) + 
			" " + pessoa.getNome()+ ", " +  ( tipoIdentificador.equals("siape") ? "Matrícula SIAPE de número " + identificador : "CPF de número " + identificador ) + 
			", ministrou nesta instituição os seguintes componentes curriculares, em seus respectivos períodos letivos:";
			
			String site = ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE);
			
			parametro.textoRodape = "Para verificar a autenticidade deste documento acesse <b>"+
			site +"</b>, informando "+( tipoIdentificador.equals("siape") ? "a Matrícula do SIAPE " : "o CPF" )+
			", data de emissão do documento e o código de verificação.";
			
			
			//Algumas unidades não tem Municipio cadastrada.
			if(cidade!=null){
				parametro.data = cidade+", "+Formatador.getInstance().formatarDataExtenso( new Date());
			}
			else {
				parametro.data = ""+Formatador.getInstance().formatarDataExtenso( new Date());
			}
			
			return forward("/ensino/declaracao_disciplinas_ministradas/relatorio.jsp");			
		} catch(Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
		}				
		return null;
	}

	/**
	 * Gera dados complementares que serão usados para gerar o hash
	 * 
	 * @param turmas
	 * @return
	 */
	private String gerarDadosAuxiliares(Collection<Turma> turmas) {
		
		List<Turma> turmasOrdenadas = new ArrayList<Turma>(turmas);
		Collections.sort(turmasOrdenadas, comparatorTurma);
		
		StringBuilder comp = new StringBuilder();
		
		comp.append("tipo=" + acao);
		comp.append(",");
		for (Turma turma : turmasOrdenadas) {
			comp.append(turma.getId() + "=" + turma.getSituacaoTurma().getId() + ",");
		}
		// Remove a última vírgula
		comp.deleteCharAt(comp.lastIndexOf(","));
		return comp.toString();
	}

	/**
	 * Busca todas as turmas consolidadas do docente
	 * 
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> buscarTurmas() throws DAOException {
		
		if (acao == EMITIR_DOCENTE_UFRN)
			return turmasDeDocentesUFRN();
		else {
			return turmasDeDocentesExterno();
		}
	}

	/**
	 * Busca todas as turmas consolidadas do docente externo
	 * 
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> turmasDeDocentesExterno() throws DAOException {
		
		if(parametro == null || isEmpty(parametro.getDocenteExterno())){ 
			throw new DAOException("Docente externo não informado ou não existe");
		}
		
		parametro.docenteExterno = getGenericDAO().findByPrimaryKey(parametro.getDocenteExterno().getId(), DocenteExterno.class);
		TurmaDao dao = getDAO(TurmaDao.class);
		Collection<Turma> tumas = dao.findByDocenteExterno(parametro.getDocenteExterno(), null, null, false, true, SituacaoTurma.CONSOLIDADA);
		return tumas;
	}

	/**
	 * Busca todas as turmas consolidadas do docente do quadro
	 * 
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> turmasDeDocentesUFRN() throws DAOException {
		parametro.docenteUFRN = getGenericDAO().findByPrimaryKey(parametro.getDocenteUFRN().getId(), Servidor.class);
		TurmaDao dao = getDAO(TurmaDao.class);
		Collection<Turma> tumas = dao.findByDocente(parametro.getDocenteUFRN(), SituacaoTurma.CONSOLIDADA, null);
		return tumas;
	}	
	
	/**
	 * Gera a semente a ser usada durante a geração do número identificador do comprovante da declaração de 
	 * disciplinas ministradas pelo docente.
	 * @param dadosComplementares 
	 * 
	 * @return
	 */
	private String gerarSemente(String dadosComplementares, String identificador) {
		StringBuilder builder = new StringBuilder();
		builder.append(identificador);
		builder.append(dadosComplementares);
		
		return builder.toString();
	}
	
	/**
	 * Mapeia os filtros do formulário
	 * 
	 * @author Henrique André
	 */
	public class Restricao {
		private boolean docenteUFRN;
		private boolean docenteExterno;
		
		public boolean isDocenteUFRN() {
			return docenteUFRN;
		}
		public void setDocenteUFRN(boolean docenteUFRN) {
			this.docenteUFRN = docenteUFRN;
		}
		public boolean isDocenteExterno() {
			return docenteExterno;
		}
		public void setDocenteExterno(boolean docenteExterno) {
			this.docenteExterno = docenteExterno;
		}
	}
	
	/**
	 * Recebe os parâmetros do formulário
	 * 
	 * @author Henrique André
	 */
	public class Parametro {
		private Servidor docenteUFRN = new Servidor();
		private DocenteExterno docenteExterno = new DocenteExterno();
		private String textoDeclaracao;
		private String textoRodape;
		private String data;		
		
		public Servidor getDocenteUFRN() {
			return docenteUFRN;
		}
		public void setDocenteUFRN(Servidor docenteUFRN) {
			this.docenteUFRN = docenteUFRN;
		}
		public DocenteExterno getDocenteExterno() {
			return docenteExterno;
		}
		public void setDocenteExterno(DocenteExterno docenteExterno) {
			this.docenteExterno = docenteExterno;
		}
		public String getTextoDeclaracao() {
			return textoDeclaracao;
		}
		public void setTextoDeclaracao(String textoDeclaracao) {
			this.textoDeclaracao = textoDeclaracao;
		}
		public String getTextoRodape() {
			return textoRodape;
		}
		public void setTextoRodape(String textoRodape) {
			this.textoRodape = textoRodape;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}			
	}

	public Restricao getRestricao() {
		return restricao;
	}

	public void setRestricao(Restricao restricao) {
		this.restricao = restricao;
	}

	public Parametro getParametro() {
		return parametro;
	}

	public void setParametro(Parametro parametro) {
		this.parametro = parametro;
	}

	public int getAcao() {
		return acao;
	}

	public void setAcao(int acao) {
		this.acao = acao;
	}

	/**
	 * Método responsável pelo início de processo de exibição e geração do documento da declaração de disciplinas do docente.
	 * Método chamado pelo ValidacaoMBean
	 * Não invocado por JSPs
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		
		this.comprovante = comprovante;
		int tipoDeclaracao = Integer.parseInt( findTipoDeclaracao(comprovante) );
		
		acao = tipoDeclaracao;
		
		if (acao == EMITIR_DOCENTE_UFRN) {
			ServidorDao servidorDao = getDAO(ServidorDao.class);
			
			try {
				parametro.docenteUFRN = servidorDao.findBySiape(Integer.parseInt(comprovante.getIdentificador()));
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}
		} else {
			DocenteExternoDao docenteExternoDao = getDAO(DocenteExternoDao.class);
			try {
				parametro.docenteExterno = docenteExternoDao.findByCPF(new Long(comprovante.getIdentificador()));
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}
		}
		
		
		List<Turma> turmasDaDeclaracao = findTurmasDaDeclaracao(comprovante);
		
		List<Integer> ids = new ArrayList<Integer>();
		for (Turma t : turmasDaDeclaracao) {
			ids.add(t.getId());
		}
		
		TurmaDao dao = getDAO(TurmaDao.class);
		turmas = dao.findTurmasByIds(ids.toArray());
				
		try {
			gerarDeclaracao();
			getCurrentRequest().setAttribute("declaracaoDisciplinasMinistradas", this);
			getCurrentRequest().getRequestDispatcher("/ensino/declaracao_disciplinas_ministradas/relatorio.jsp").forward(getCurrentRequest(), getCurrentResponse());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Verifica se ocorreu alguma mudança nas turmas que foram emitidas na declaração
	 * <br> JSP: Não invocado por jsp
	 * @throws ArqException 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		
		acao = Integer.parseInt( findTipoDeclaracao(comprovante) );
		
		List<Turma> turmasDaDeclaracao = findTurmasDaDeclaracao(comprovante);
		
		List<Integer> ids = new ArrayList<Integer>();
		for (Turma t : turmasDaDeclaracao) {
			ids.add(t.getId());
		}
		
		TurmaDao dao = getDAO(TurmaDao.class);
		List<Turma> turmasRecuperadas = dao.findSituacaoesByIds(ids.toArray());
		
	
		String dadosAuxiliares = gerarDadosAuxiliares(turmasRecuperadas);
		
		
		String codigoVerificacao = null;
		try {
			codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(
					comprovante, gerarSemente(dadosAuxiliares, comprovante.getIdentificador()));
		} catch (ArqException e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		if (codigoVerificacao != null && codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
			return true;		
		
		
		return false;
	}

	/**
	 * Retorna o identificador do comprovante.
	 * 
	 * @param comprovante
	 * @return
	 */
	private String findTipoDeclaracao(EmissaoDocumentoAutenticado comprovante) {
		
		String[] conjuntos = comprovante.getDadosAuxiliares().split(",");
		
		// O Identificador está na primeira posição
		String[] chaveValor = conjuntos[0].split("=");
		
		return chaveValor[1];
		
	}

	/**
	 * Recupera as turmas da declaração que esta sendo validada
	 * 
	 * @param comprovante
	 * @return
	 */
	private List<Turma> findTurmasDaDeclaracao(EmissaoDocumentoAutenticado comprovante) {
		
		// Pega as turmas emitidas (id e situação da turma)
		String dadosAuxiliares = comprovante.getDadosAuxiliares();
		
		String[] conjuntos = dadosAuxiliares.split(",");
		
		List<Turma> turmasDaDeclaracao = new ArrayList<Turma>();
		
		// As turmas estão na segunda posição em diante
		for (int i = 1; i < conjuntos.length; i++) {
			String[] chaveValor = conjuntos[i].split("=");
			
			Turma turma = new Turma();
			turma.setId(new Integer(chaveValor[0]));
			turma.setSituacaoTurma(new SituacaoTurma());
			turma.getSituacaoTurma().setId(new Integer(chaveValor[1]));
			
			turmasDaDeclaracao.add(turma);
		}
		return turmasDaDeclaracao;
	}

	/**
	 * Ordena as turmas pelo id
	 */
	private Comparator<Turma> comparatorTurma = new Comparator<Turma>(){

		public int compare(Turma o1, Turma o2) {
			
			if (o1.getId() > o2.getId())
				return 1;
			else
				return 0;
		}
		
	};

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	/**
	 * Retorna as turmas ordenadas por {@link Turma#getAno().Turma#getPeriodo()} e {@link Turma#getDescricaoDisciplina()}  
	 * @return
	 */
	public Collection<Turma> getTurmas() {
		
		if( isEmpty(turmas) )
			return Collections.EMPTY_LIST;
		
		List<Turma> turmasOrdenadas = new ArrayList<Turma>();
		turmasOrdenadas.addAll(turmas);
		
		Collections.sort(turmasOrdenadas, new Comparator<Turma>(){
			public int compare(Turma t1, Turma t2) {
				int retorno = 0;
				retorno = t1.getAno() - t2.getAno();
				if( retorno == 0 )
					retorno = t1.getPeriodo() - t2.getPeriodo();
				if( retorno == 0 )
					retorno = t1.getDescricaoDisciplina().compareTo( t2.getDescricaoDisciplina() );
				return retorno;
			}
		});
		
		return turmasOrdenadas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}	
}
