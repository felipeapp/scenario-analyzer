/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/12/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * 
 * <p>MBean responsavel por gerar declarações de participanção em extensão </p> 
 * 
 * 
 * <p> <strong>Só usar esse MBean para gerar as declarações, por favor.</strong></p>
 * 
 * 
 * <p>Ao contrários do que geralmente ocorre dos casos e uso, concentar tudo num único Mbean 
 * ajuda a menter o sistema. Se o usuário fica vendo duas declarações diferentes dependo do lugar que gerar a declaração.
 * O método que gera o texto da declaração no sistema era para ser único !!!!!!!!!!!!!!!!!!
 * </p>
 * 
 * <p> A autenticação dos documentos é realizada pelo SIGAA através da geração de
 * um código de documento e código de verificação que pode ser validado através
 * do portal público do SIGAA.</p> 
 * 
 * 
 * @author Ilueny Santos
 * @author Jadson Santos
 * 
 ******************************************************************************/
@Component("declaracaoExtensaoMBean")
@Scope("request")
public class DeclaracaoExtensaoMBean extends SigaaAbstractController<ParticipanteAcaoExtensao> implements AutValidator {

	/** nomes dos documentos */
	public static final String DECLARACAO = "trf10189_DeclaracaoProex";

	/** O participante para o qual será emitirdo a declaração.
	 *  O id desse participante é passado via  <f:setPropertyActionListener>    */ 
	private ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();

	/**
	 *  Para emissão de declarações de membros de projeto.
	 *  O id desse participante é passado via  <f:setPropertyActionListener>
	 */
	private MembroProjeto membro = new MembroProjeto();
	
	
	/** Se é o proprio particiapante que está emitindo ou é o coordenador da ação. porque as regras são um pouco diferentes.
	 * setado via  <f:setPropertyActionListener>
	 */
	private Boolean isEmissaoByCoordenador;
	
	
	/** Comprovante de emissão de documento autenticado */
	private EmissaoDocumentoAutenticado comprovante;

	
	/** Usado para verificar se o documento esta sendo validado pela área publica ou se esta sendo feita uma emissão de documento autenticado. */
	private boolean verificando = false;

	
	
	
	/**
	 * <p>Gera a declaração para o participante selecionado.</p>
	 * 
	 * <p>   Esse método é chamando das página JSP abaixo passando com o id do participante já populado, 
	 *    então deve-se buscar as outras informações usadas para emitir a declaração.
	 *  </p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * 
	 *      <li>/sigaa.war/public/extensao/paginaGerenciaMinhaInscricaoCursosEventosExtensao.jsp</li>
	 *      <li>/sigaa.war/public/extensao/paginaGerenciaMinhaParticipacaoCursosEventosExtensao.jsp</li>
	 * </ul>
	 *  
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirDeclaracaoParticipante() throws DAOException, SegurancaException {

		ParticipanteAcaoExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(ParticipanteAcaoExtensaoDao.class);
			
			// Recupera aquelas inforamções usadas para emitir o certificado ou declaração de extensão.
			if(! verificando)
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes(participante.getId()); 
			else{
				// os dados já vem preenchidos pelo método exibir !!!
			}
			
			if(isEmissaoByCoordenador != null && isEmissaoByCoordenador == true){
				participante.verificaEmissaoDeclaracaoCoordenador();
			}else{
				participante.verificaEmissaoDeclaracaoParticipante();
			}
			
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoParticipante();
			
			// montagem do corpo da declaração
			String textoDeclaracao = getTextoDeclaracao(dadosDeclaracao);

			AtividadeExtensao atividade =  
							( ! ValidatorUtil.isEmpty( participante.getAtividadeExtensao()) ) 
							? participante.getAtividadeExtensao()
							: participante.getSubAtividade().getAtividade(); 
			
			inscreDadosDeclaracao(textoDeclaracao, atividade.getCoordenacao().getPessoa().getNomeAbreviado(), true);
			
			return null;
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao tentar emitir a declaração do participante.");
			return null;
		}finally{
			if(dao != null) dao.close();
		}

	}
	
	
	/**
	 * <p>Gera a declaração para o participante selecionado.</p>
	 * 
	 * <p>   Esse método é chamando das página JSP abaixo passando com o id do participante já populado, 
	 *    então deve-se buscar as outras informações usadas para emitir a declaração.
	 *  </p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DocumentosAutenticados/lista.jsp</li>
	 * 		<li>/sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 * 		<li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 * </ul>
	 *  
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirDeclaracaoMembroProjeto() throws DAOException, SegurancaException {

		AtividadeExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(AtividadeExtensaoDao.class);
			
			// Recupera aquelas inforamções usadas para emitir o certificado ou declaração de extensão.
			if(! verificando){
				membro = dao.findByPrimaryKey(membro.getId(), MembroProjeto.class);
				dao.refresh(membro.getProjeto().getCoordenador());
				AtividadeExtensao atividade = dao.findAcaoByProjeto(membro.getProjeto().getId());
				membro.setAtividade(atividade);
			}
			
			if( ! membro.isPassivelEmissaoDeclaracao() ){
				addMensagemErro("Não é possível emitir a declaração para esse participante.");
				return null;
			}
			
			
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoMembroProjeto();
			
			// montagem do corpo da declaração
			String textoDeclaracao = getTextoDeclaracao(dadosDeclaracao);
			
			inscreDadosDeclaracao(textoDeclaracao, membro.getProjeto().getCoordenador().getPessoa().getNomeAbreviado(), false);

			
			return null;

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao tentar emitir a declaração do participante.");
			return null;
		}finally{
			if(dao != null) dao.close();
		}

	}
	
	
	
	
	
	
	//////////////         métodos auxiliares privados                /////////////
	
	
	
	/**
	 * Gera a semente utilizada no código de autenticação da declaração.
	 * Deve-se utilizar para geração da semente, todos os dados variáveis da 
	 * declaração.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return String com a semente gerada.
	 */
	private String gerarSementeDeclaracao(boolean isDeclaracaoParticipante) {
		
		/*
		 * Se mudar o nome, CPF, passaporte, data de nascimento, titulo a atividade, a unidade, a função,
		 * e a data de incio e fim da atividade invalida a daclaração.
		 * 
		 * Na declaração não usa a carga horária do participante, pois como o curso/evento não terminou ainda, a carga horária pode mudar.
		 * 
		 * Somente no certificado é usado a Carga horária.
		 */
		if(isDeclaracaoParticipante){
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoParticipante();
			return dadosDeclaracao.getSemente();
		}else{
			DadosEmissaoDeclaracao dadosDeclaracao =  montaDadosEmissaoDeclaracaoMembroProjeto();
			return dadosDeclaracao.getSemente();
		}
	}
	

	/** Organiza as inforamações de declaração em forma objeto para evitar duplicação de código */
	private DadosEmissaoDeclaracao montaDadosEmissaoDeclaracaoParticipante(){
		
		DadosEmissaoDeclaracao dadosEmissao = new DadosEmissaoDeclaracao();
		
		dadosEmissao.setNomeDeclaracao( participante.getCadastroParticipante().getNome() );
		
		if(participante.getCadastroParticipante().getCpf() != null)
			dadosEmissao.setIdentificacaoDeclaracao( " CPF "+participante.getCadastroParticipante().getCpf());
		else{
			dadosEmissao.setIdentificacaoDeclaracao( "PASSAPORTE "+participante.getCadastroParticipante().getPassaporte()+" DATA NASCIMENTO "+participante.getCadastroParticipante().getDataNascimentoFormatada());
		}
		
		boolean isParticipacaoAtividade = ! ValidatorUtil.isEmpty( participante.getAtividadeExtensao());
		
		boolean iscursoOuEvento = false;
		
		if(isParticipacaoAtividade){
			iscursoOuEvento = (participante.getAtividadeExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
					|| participante.getAtividadeExtensao().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO);
		}else{
			iscursoOuEvento = (participante.getSubAtividade().getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO 
					|| participante.getSubAtividade().getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO);
		}		
		
		dadosEmissao.setTituloAtividade(   ( isParticipacaoAtividade ?  participante.getAtividadeExtensao().getTitulo() : participante.getSubAtividade().getTitulo() ) );
		
		dadosEmissao.setTipoAtividade ( isParticipacaoAtividade ? " na Atividade de Extensão " : " na Mini Atividade de Extensão " );
		dadosEmissao.setUnidadeAtividade( isParticipacaoAtividade ? participante.getAtividadeExtensao().getUnidade().getNome() : participante.getSubAtividade().getAtividade().getUnidade().getNome() );	
		
		String cargaHorariaSeCursoEvento = "";
    	if (iscursoOuEvento ) {
    		int ch = participante.getChCertificadoDeclaracao();
    		if(ch > 0 )
    			cargaHorariaSeCursoEvento = ", cumprinto até o momento uma carga horária de " + participante.getChCertificadoDeclaracao() + " hora(s)"; 
    	}
		
    	dadosEmissao.setCargaHorariaSeCursoEvento(cargaHorariaSeCursoEvento);
    	dadosEmissao.setTipoParticipacao( participante.getTipoParticipacao().getDescricao() );
    	
		String dataInicioFormatada = null;
    	
		if(isParticipacaoAtividade){
			dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(participante.getAtividadeExtensao().getDataInicio());
		} else{
			dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(participante.getSubAtividade().getInicio());
		}
    	
    	
    	String dataFimFormatada = null;
    	
    	if(isParticipacaoAtividade) {
    		dataFimFormatada = Formatador.getInstance().formatarDataDiaMesAno(  participante.getAtividadeExtensao().getDataFim() );
    	} else {
    		dataFimFormatada = Formatador.getInstance().formatarDataDiaMesAno(  participante.getSubAtividade().getFim() );
    	}
		
    	dadosEmissao.setDataInicioFormatada(dataInicioFormatada);
    	dadosEmissao.setDataFimFormatada(dataFimFormatada);
    	
    	return dadosEmissao;
	}

	
	/** Organiza as inforamações de declaração em forma objeto para evitar duplicação de código */
	private DadosEmissaoDeclaracao montaDadosEmissaoDeclaracaoMembroProjeto(){
		
		DadosEmissaoDeclaracao dadosEmissao = new DadosEmissaoDeclaracao();
		
		dadosEmissao.setNomeDeclaracao( membro.getPessoa().getNome() );
		
		boolean iscursoOuEvento = membro.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO
				|| membro.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO;
		
		
		String tipoParticipante = "";
		switch (membro.getCategoriaMembro().getId()) {
		case CategoriaMembro.DOCENTE:
			tipoParticipante = " o(a) Professor(a)";
			dadosEmissao.setIdentificacaoDeclaracao( " SIAPE "+membro.getServidor().getSiape());
			break;
		case CategoriaMembro.DISCENTE:
			tipoParticipante = " o(a) Discente ";
			dadosEmissao.setIdentificacaoDeclaracao( " MATRÍCULA "+membro.getDiscente().getMatricula());
			break;
		case CategoriaMembro.EXTERNO:
			tipoParticipante = "";
			dadosEmissao.setIdentificacaoDeclaracao( " CPF "+membro.getPessoa().getCpfCnpjFormatado());
			break;
		case CategoriaMembro.SERVIDOR:
			tipoParticipante = " o(a) Servidor(a) ";
			dadosEmissao.setIdentificacaoDeclaracao( " SIAPE "+membro.getServidor().getSiape());
			break;
		}
			
		dadosEmissao.setTipoParticipante(tipoParticipante);
		
		dadosEmissao.setTituloAtividade(  membro.getProjeto().getTitulo() );
		
		dadosEmissao.setTipoAtividade ( " na Atividade de Extensão " );
		dadosEmissao.setUnidadeAtividade( membro.getProjeto().getUnidade().getNome() );	
		
		String cargaHorariaSeCursoEvento = "";
    	if (iscursoOuEvento ) {
    		int ch = membro.getAtividade().getCursoEventoExtensao().getCargaHoraria();
    		if(ch > 0 )
    			cargaHorariaSeCursoEvento = ", cumprinto até o momento uma carga horária de " + membro.getAtividade().getCursoEventoExtensao().getCargaHoraria() + " hora(s) semanal"; 
    	}
		
    	dadosEmissao.setCargaHorariaSeCursoEvento(cargaHorariaSeCursoEvento);
    	dadosEmissao.setTipoParticipacao( membro.getFuncaoMembro().getDescricao() );
    	
    	dadosEmissao.setHorasAtividadeDesenvolvida(""+membro.getChCertificadoDeclaracao() );
    	
    	String dataInicioFormatada = null;
    	if (membro.getProjeto().getDataInicio() != null) {
    	    dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(membro.getProjeto().getDataInicio());
    	}
    	
    	String dataFimFormatada = null;
    	if (membro.getProjeto().getDataFim() != null) {
    	   dataFimFormatada  = Formatador.getInstance().formatarDataDiaMesAno(membro.getProjeto().getDataFim());
    	    
    	}
		
    	dadosEmissao.setDataInicioFormatada(dataInicioFormatada);
    	dadosEmissao.setDataFimFormatada(dataFimFormatada);
    	
    	return dadosEmissao;
	}
	
	/**
	 * Retorna o texto(corpo) da declaração.
	 * 
	 * No Forma:
	 * <pre>
	 *  Declaramos para os devidos fins que, o(a) Professor José da Silva, CPF 1234567890,
	 *  está inscrito como participante na Atividade 'Atividade de Teste' cumprinto até o momento uma carga horária de 100 hora(s), promovida pelo(a)
	 *  Curso de corte e costura
	 *  na função de ALUNO(A)
	 *  , com 20 horas de atividades desenvolvidas,
	 *  , que ocorrerá no período de 01/12/2012 a 31/12/2012.
	 * </pre>
	 * 
	 * @return texto utilizado na declaração.
	 */
	private String getTextoDeclaracao(DadosEmissaoDeclaracao dados) {
			
	    	String textoDeclaracao = "";
	    	
	    	textoDeclaracao =  "Declaramos para os devidos fins que,"
	    			+( StringUtils.notEmpty(dados.getTipoParticipante()) ? dados.getTipoParticipante() : "")
	    			+" "+ dados.getNomeDeclaracao();
	    	
			textoDeclaracao += ", " +dados.getIdentificacaoDeclaracao()+", ";	
			
					
			textoDeclaracao +=" está inscrito como participante "
					+ dados.getTipoAtividade()
					+ " "+dados.getTituloAtividade().toUpperCase()+ "" 
					+  ", que ocorrerá no período de "+ dados.getDataInicioFormatada()+ " a "+ dados.getDataFimFormatada()	
					+ dados.getCargaHorariaSeCursoEvento() + ", promovida pelo(a) "
					+ dados.getUnidadeAtividade()
					+ " na função de "+ dados.getTipoParticipacao();
			
			if(StringUtils.notEmpty(dados.getHorasAtividadeDesenvolvida())){
				textoDeclaracao += ", com "+dados.getHorasAtividadeDesenvolvida()+ " hora(s) semanais de atividades desenvolvidas ";
			}
			
			textoDeclaracao += ".";	
			
			return textoDeclaracao;
	}
	
	
	
	
	/** 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws JRException 
	 * @throws SQLException 
	 * @throws IOException */
	private void inscreDadosDeclaracao(String texto, String nomeCoordenador, boolean isDeclaracaoParticipante) throws ArqException, NegocioException, JRException, SQLException, IOException{
		// Gerar declaração
		Connection con = null;
		HashMap<Object, Object> parametros = new HashMap<Object, Object>();

		// gerando código de autenticação...
		if (!verificando) {
			
			comprovante = geraEmissao(
					TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
					(isDeclaracaoParticipante ? ""+participante.getId(): ""+membro.getId() ),                     // O id do participante ou membro identifica o certificado ( serve para buscar no banco e validar as informações da declaração )
					gerarSementeDeclaracao(isDeclaracaoParticipante),                      // a semente para gerar o código de validação, se a semente mudar invalida a declaração            
					(isDeclaracaoParticipante ? "P" : "M"),        // dados auxiliares para saber se a declaração é para participante ou membro do projeto
					(isDeclaracaoParticipante ?SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO: SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO ) ,
					true);

		}

		// setando parâmetros
		parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

		parametros.put("cidade",  RepositorioDadosInstitucionais.get("cidadeInstituicao")+", " + Formatador.getInstance().formatarDataDiaMesAno( comprovante.getDataEmissao()));
		parametros.put("texto", texto);
		parametros.put("coordenacao", nomeCoordenador);
		parametros.put("numero_documento", comprovante.getNumeroDocumento());

		
		con = Database.getInstance().getSigaaConnection();
		JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(DECLARACAO + ".jasper"), parametros, con);

		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=DECLARACAO_PARTICIPANTE_PROEX_"+ (isDeclaracaoParticipante ? ""+participante.getId(): ""+membro.getId() ) + ".pdf");
		JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());

		FacesContext.getCurrentInstance().responseComplete();
		con.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////// Métodos para verifica a auteticidade de docuemento ////////////////
	

	/**
	 * Utilizado na validação da autenticidade da declaração
	 * a partir do portal público.
	 * 
	 * Não chamado diretamente por jsp.
	 * 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante, HttpServletRequest req, HttpServletResponse res) {

		
		this.comprovante = comprovante;
		verificando = true;

		boolean isComprovanteParticipante = true;
		
		if(comprovante.getDadosAuxiliares() != null 
				&& comprovante.getDadosAuxiliares().equalsIgnoreCase("P")){
			isComprovanteParticipante = true;
		}else{
			isComprovanteParticipante = false;
		}
		
		if( isComprovanteParticipante){
			
			ParticipanteAcaoExtensaoDao dao = null;
			try {
				dao = getDAO(ParticipanteAcaoExtensaoDao.class);
				
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( Integer.parseInt(comprovante.getIdentificador() ) );  
	
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					emitirDeclaracaoParticipante();
				}
	
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			
			AtividadeExtensaoDao dao = null;
			
			try {
				
				dao = getDAO(AtividadeExtensaoDao.class);
				membro = dao.findByPrimaryKey( Integer.parseInt(comprovante.getIdentificador() ), MembroProjeto.class);
				dao.refresh(membro.getProjeto().getCoordenador());
				AtividadeExtensao atividade = dao.findAcaoByProjeto(membro.getProjeto().getId());
				membro.setAtividade(atividade);
				
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					emitirDeclaracaoMembroProjeto();
				}
				
			}catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}
	}

	
	/**
	 * Utilizado na validação da autenticidade da declaração
	 * a partir do portal público. Valida o código de autenticação e o número 
	 * do documento.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {

		boolean isComprovanteParticipante = true;
		
		if(comprovante.getDadosAuxiliares() != null && comprovante.getDadosAuxiliares().equalsIgnoreCase("P")){
			isComprovanteParticipante = true;
		}else{
			isComprovanteParticipante = false;
		}
		
		if( isComprovanteParticipante){
			ParticipanteAcaoExtensaoDao dao = null;
			try {
				
				dao = getDAO(ParticipanteAcaoExtensaoDao.class);
				participante = dao.findInformacoesParticipantesEmitirCertificadosEDeclaracoes( Integer.parseInt(comprovante.getIdentificador() ) );  
	
				String codigoVerificacao = "";
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracao(true));
				}
	
				if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
					return true;
				}
	
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			AtividadeExtensaoDao dao = null;
			
			try {
				
				dao = getDAO(AtividadeExtensaoDao.class);
				membro = dao.findByPrimaryKey( Integer.parseInt(comprovante.getIdentificador() ) , MembroProjeto.class);
				dao.refresh(membro.getProjeto().getCoordenador());
				AtividadeExtensao atividade = dao.findAcaoByProjeto(membro.getProjeto().getId());
				membro.setAtividade(atividade);
				
				String codigoVerificacao = "";
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracao(false));
				}
				
				if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
					return true;
				}
				
			}catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return false;
	}
	

	
	////////// sets e gets ////////
	
	public ParticipanteAcaoExtensao getParticipante() {
		return participante;
	}

	public void setParticipante(ParticipanteAcaoExtensao participante) {
		this.participante = participante;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}
	public MembroProjeto getMembro() {
		return membro;
	}
	public void setMembro(MembroProjeto membro) {
		this.membro = membro;
	}
	public Boolean getIsEmissaoByCoordenador() {
		return isEmissaoByCoordenador;
	}
	public void setIsEmissaoByCoordenador(Boolean isEmissaoByCoordenador) {
		this.isEmissaoByCoordenador = isEmissaoByCoordenador;
	}
}





/** Classe para organizar melhor os dados de declaração. */
class DadosEmissaoDeclaracao{
	
	private String nomeDeclaracao;
	private String identificacaoDeclaracao;
	private String tituloAtividade;
	private String tipoAtividade;
	private String unidadeAtividade;
	private String tipoParticipante;
	private String cargaHorariaSeCursoEvento;
	private String tipoParticipacao;
	private String horasAtividadeDesenvolvida;
	private String dataInicioFormatada;
	private String dataFimFormatada;
	
	public DadosEmissaoDeclaracao() {
		super();
	}
	
	public String getSemente() {
		return nomeDeclaracao+identificacaoDeclaracao+tituloAtividade+unidadeAtividade
				+tipoParticipacao+ dataInicioFormatada+dataFimFormatada;
	}


	public String getNomeDeclaracao() {
		return nomeDeclaracao;
	}
	public void setNomeDeclaracao(String nomeDeclaracao) {
		this.nomeDeclaracao = nomeDeclaracao;
	}
	public String getIdentificacaoDeclaracao() {
		return identificacaoDeclaracao;
	}
	public void setIdentificacaoDeclaracao(String identificacaoDeclaracao) {
		this.identificacaoDeclaracao = identificacaoDeclaracao;
	}
	public String getTituloAtividade() {
		return tituloAtividade;
	}
	public void setTituloAtividade(String tituloAtividade) {
		this.tituloAtividade = tituloAtividade;
	}
	public String getTipoAtividade() {
		return tipoAtividade;
	}
	public void setTipoAtividade(String tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}
	public String getUnidadeAtividade() {
		return unidadeAtividade;
	}
	public void setUnidadeAtividade(String unidadeAtividade) {
		this.unidadeAtividade = unidadeAtividade;
	}
	public String getTipoParticipante() {
		return tipoParticipante;
	}
	public void setTipoParticipante(String tipoParticipante) {
		this.tipoParticipante = tipoParticipante;
	}
	public String getCargaHorariaSeCursoEvento() {
		return cargaHorariaSeCursoEvento;
	}
	public void setCargaHorariaSeCursoEvento(String cargaHorariaSeCursoEvento) {
		this.cargaHorariaSeCursoEvento = cargaHorariaSeCursoEvento;
	}
	public String getTipoParticipacao() {
		return tipoParticipacao;
	}
	public void setTipoParticipacao(String tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}
	public String getHorasAtividadeDesenvolvida() {
		return horasAtividadeDesenvolvida;
	}
	public void setHorasAtividadeDesenvolvida(String horasAtividadeDesenvolvida) {
		this.horasAtividadeDesenvolvida = horasAtividadeDesenvolvida;
	}
	public String getDataInicioFormatada() {
		return dataInicioFormatada;
	}
	public void setDataInicioFormatada(String dataInicioFormatada) {
		this.dataInicioFormatada = dataInicioFormatada;
	}
	public String getDataFimFormatada() {
		return dataFimFormatada;
	}
	public void setDataFimFormatada(String dataFimFormatada) {
		this.dataFimFormatada = dataFimFormatada;
	}
	
	
}

