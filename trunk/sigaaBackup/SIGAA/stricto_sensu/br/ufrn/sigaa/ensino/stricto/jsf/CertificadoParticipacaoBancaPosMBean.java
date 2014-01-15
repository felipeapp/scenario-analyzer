/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 05/03/2008
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.ParticipacaoBancaPos;

/**
 * Managed bean para geração do certificado de participação em
 * bancas de pós-graduação.
 *
 * @author David Pereira
 *
 */
@Component("certificadoBancaPos") @Scope("request")
public class CertificadoParticipacaoBancaPosMBean extends SigaaAbstractController<ParticipacaoBancaPos> implements OperadorDiscente {

	/** Discente do qual será selecionado a banca para emissão do certificado. */
	private DiscenteStricto discente;

	/** Coleção de bancas do discente. */
	private Collection<BancaPos> bancas;

	/** Banca selecionada para emissão do certificado. */
	private BancaPos bancaSelecionada;	
	
	/** Utilizado no texto da declaração */
	private String assinatura;

	/** 
	 * Inicia a emissão do certificado de participação em banca de pós-graduação.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp </li>
	 * </ul>
	 * @return
	 */
	public String iniciar() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CERTIFICADO_PARTICIPACAO_BANCA_POS);
		return buscaDiscenteMBean.popular();
	}

	/** Método invocado pelo MBean de busca de discente, após a seleção de um discente da lista de resultados da busca.
	 * <br/><br/>
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {

		bancas = getDAO(BancaPosDao.class).findByDiscente(discente);

		if (isEmpty(bancas)) {
			addMensagemErro("Não há bancas cadastradas para o discente selecionado.");
			return null;
		} else if (bancas.size() > 1) {
			return forward("/stricto/banca_pos/certificado/bancas_discente.jsp");
		} else {
			bancaSelecionada = bancas.iterator().next();
			return forward("/stricto/banca_pos/certificado/membros_banca.jsp");
		}

	}

	/** Seleciona uma das bancas do discente para a emissão do certificado.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/banca_pos/certificado/bancas_discente.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionaBanca() throws DAOException {
		int id = getParameterInt("id");

		bancaSelecionada = getDAO(BancaPosDao.class).findByPrimaryKey(id, BancaPos.class);
		return forward("/stricto/banca_pos/certificado/membros_banca.jsp");
	}

	/** Gera o certificado de participação em banca de pós graduação para um dos membros selecioandos.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/banca_pos/certificado/membros_banca.jsp </li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String gerarCertificado() throws Exception {
		int id = getParameterInt("id");
		MembroBancaPos m = getDAO(BancaPosDao.class).findByPrimaryKey(id, MembroBancaPos.class);

				
		Map<String, Object> hs = new HashMap<String, Object>();
		hs.put("pathSubRelFrente", JasperReportsUtil.getReportSIGAA("CertificadoBancaPosFrente.jasper"));
		hs.put("pathSubRelVerso", JasperReportsUtil.getReportSIGAA("CertificadoBancaPosVerso.jasper"));

		SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");

		ParticipacaoBancaPos participacao = new ParticipacaoBancaPos();	
		participacao.setPrograma("PROGRAMA DE " + m.getBanca().getDadosDefesa().getDiscente().getGestoraAcademica().getNome());
		if( Unidade.UNIDADE_DIREITO_GLOBAL != m.getBanca().getDadosDefesa().getDiscente().getGestoraAcademica().getGestora().getId() )
			participacao.setUnidade("do " + m.getBanca().getDadosDefesa().getDiscente().getGestoraAcademica().getGestora().getNome());

		String nomeCoordenador = "";
		CoordenacaoCurso coordenacao = getDAO(CoordenacaoCursoDao.class).findUltimaByPrograma(m.getBanca().getDadosDefesa().getDiscente().getGestoraAcademica());
		if (!isEmpty(coordenacao)) {
			nomeCoordenador = coordenacao.getServidor().getNome();
		}
		participacao.setMembros(m.getBanca().getMembrosBanca());
		participacao.setCoordenador(nomeCoordenador);
		participacao.setDataEmissao(sdf.format(new Date()));
		participacao.setDiscente(m.getBanca().getDadosDefesa().getDiscente().getPessoa().getNome());
		String titulo = m.getBanca().getDadosDefesa().getTitulo();
		// tratando as tags <strong> e <em> que não aparecem no JasperReports
		titulo = titulo.replaceAll("<strong>", "<b>")
			.replaceAll("</strong>", "</b>")
			.replaceAll("<em>", "<i>")
			.replaceAll("</em>", "</i>");
		participacao.setTitulo(titulo);
		participacao.setRegistroPessoa(m.getPessoa().getId());
		participacao.setParticipante(m.getNome());
		
		if (m.getPessoa().isInternacional() && !isEmpty(m.getPessoa().getPassaporte()) 
				&& m.getPessoa().getCpf_cnpj() == null)
			participacao.setCpfPassaporte("Passaporte "+ m.getPessoa().getPassaporte());
		else
			participacao.setCpfPassaporte("CPF "+m.getPessoa().getCpfCnpjFormatado());
		
		participacao.setTipoParticipacao(m.getTipoDescricaoCompleto());
		participacao.setMembros(m.getBanca().getMembrosBanca());
		participacao.setDataBanca(m.getBanca().getDescricaoDataHora());
		participacao.setTipoBanca(m.getBanca().getTipoDescricao());
		participacao.setSexo(m.getPessoa().getSexo() == 'M');
		if (m.getBanca().getDadosDefesa().getDiscente().getNivel() == NivelEnsino.MESTRADO)
			participacao.setNivelBanca("Dissertação de Mestrado");
		else
			participacao.setNivelBanca("Tese de Doutorado");

		List<ParticipacaoBancaPos> participacoes = new ArrayList<ParticipacaoBancaPos>();
		participacoes.add(participacao);

//		JRDataSource jrds = new JRBeanCollectionDataSource(participacoes);
//		JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("CertificadoParticipacaoBancaPos.jasper"), hs, jrds);
//
//		getCurrentResponse().setContentType("application/pdf");
//		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=certificado_banca_pos.pdf");
//		JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());
//
//		FacesContext.getCurrentInstance().responseComplete();
//		return null;
		
		preencherAssinatura(participacao,m);				
		
		obj = participacao;
		return forward("/stricto/banca_pos/certificado/declaracao_participacao_banca.jsp");
	}

	/**
	 * 
	 * Utilizado para preencher o campo assinatura do MBean, que será utilizado na impressão da declaração.
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>NENHUMA</li>
	 * </ul>
	 * 
	 * @param participacao
	 * @param m
	 * @throws Exception
	 */
	private void preencherAssinatura(ParticipacaoBancaPos participacao, MembroBancaPos m) throws Exception {
		
		//Pega Coordenador e Vice coordenador do programa.
		Collection<CoordenacaoCurso> coordenadores = getDAO(CoordenacaoCursoDao.class).findCoordViceByCursoNivel(m.getBanca().getDadosDefesa().getDiscente().getGestoraAcademica().getId(), m.getBanca().getDadosDefesa().getDiscente().getNivel());
		CoordenacaoCurso coordenador = null;
		CoordenacaoCurso vice = null;
		Boolean coordenadorPresenteNaBanca = false;
		Boolean viceCoordenadorPresenteNaBanca = false;
		
		
		for(CoordenacaoCurso coord : coordenadores) {			
			if(coord.isCoordenador()) { //É o coordenador do programa
				coordenador = coord;				
			}
			else { //É o vice coordenador
				vice = coord;
			}				
		}
		boolean assinaturaCoord = true;
		boolean assinaturaVice = false;
		for(MembroBancaPos mb : m.getBanca().getMembrosBanca()){
			//Verifica se o coordenador está presente na banca, sendo o atual participante.
			if(mb.getServidor()!= null && coordenador!=null && mb.getServidor().getId() == coordenador.getServidor().getId()) { //Coordenador esta presente na banca
				coordenadorPresenteNaBanca = true;
				assinaturaVice = true;
				if (coordenador.getServidor().getPessoa().getId() == participacao.getRegistroPessoa()){
					assinaturaCoord = false;
					break;
				}	
			}
			//Verifica se o Vice coordenador está presente na banca, sendo o atual participante.
			if(mb.getServidor()!= null && vice!=null && mb.getServidor().getId() == vice.getServidor().getId()) { //Vice-coordenador esta presente na banca
				viceCoordenadorPresenteNaBanca = true;
				assinaturaCoord = true;
				if (vice.getServidor().getPessoa().getId() == participacao.getRegistroPessoa()) {
					assinaturaVice = false;
					break;
				}	
			}
		}
		
		//se o coordenador estiver presente na banca e os dois coordenadores não estiverem presentes na banca.
		if( (assinaturaVice || coordenadorPresenteNaBanca) && (!assinaturaCoord || !viceCoordenadorPresenteNaBanca) ) {
			String nomeViceCoordenador = (vice == null ? "" : vice.getServidor().getPessoa().getNome());
			setAssinatura("Prof(a). Dr(a). " + nomeViceCoordenador + "<br> Vice-Coordenador(a) do " + participacao.getPrograma() + "- " + RepositorioDadosInstitucionais.get("siglaInstituicao"));
			
		}
		else {		 
			setAssinatura("Prof(a). Dr(a). " + participacao.getCoordenador() + "<br> Coordenador(a) do " + participacao.getPrograma() + "- " + RepositorioDadosInstitucionais.get("siglaInstituicao"));
		}
	}
	
	
	/** Método invocado pelo MBean de busca por discente.
	 * Não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = (DiscenteStricto) discente;
	}

	/** Retorna a coleção de bancas do discente. 
	 * @return
	 */
	public Collection<BancaPos> getBancas() {
		return bancas;
	}

	/** Seta a coleção de bancas do discente. 
	 * @param bancas
	 */
	public void setBancas(Collection<BancaPos> bancas) {
		this.bancas = bancas;
	}

	/** Retorna a banca selecionada para emissão do certificado. 
	 * @return
	 */
	public BancaPos getBancaSelecionada() {
		return bancaSelecionada;
	}

	/** Seta a banca selecionada para emissão do certificado.
	 * @param bancaSelecionada
	 */
	public void setBancaSelecionada(BancaPos bancaSelecionada) {
		this.bancaSelecionada = bancaSelecionada;
	}

	/** Retorna o discente do qual será selecionado a banca para emissão do certificado. 
	 * @return
	 */
	public DiscenteStricto getDiscente() {
		return discente;
	}

	/** Seta o discente do qual será selecionado a banca para emissão do certificado.
	 * @param discente
	 */
	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	
	/** Retorna o texto que será inserido no campo para assinatura da declaração 
	 * @return
	 */
	public String getAssinatura() {
		return assinatura;
	}
	
	/** Seta o texto que será inserido no campo para assinatura da declaração 
	 * @return
	 */
	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}
	
	

}
