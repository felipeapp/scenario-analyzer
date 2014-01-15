/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/11/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.dao.ensino.BancaDefesaDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MembroBanca;
import br.ufrn.sigaa.ensino.dominio.ParticipacaoBancaDefesa;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 * Managed bean para geração do certificado de participação em
 * bancas de graduação.
 *
 * @author Arlindo Rodrigues
 *
 */
@Component("declaracaoParticipacaoBanca") @Scope("request")
public class DeclaracaoParticipacaoBancaMBean extends SigaaAbstractController<ParticipacaoBancaDefesa> implements OperadorDiscente {

	/** Discente do qual será selecionado a banca para emissão do certificado. */
	private DiscenteAdapter discente;

	/** Coleção de bancas do discente. */
	private Collection<BancaDefesa> bancas;

	/** Banca selecionada para emissão do certificado. */
	private BancaDefesa bancaSelecionada;	
	
	/** Utilizado no texto da declaração */
	private String assinatura;

	/** 
	 * Inicia a emissão do certificado de participação em banca de graduação.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp </li>
	 * </ul>
	 * @return
	 */
	public String iniciar() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.DECLARACAO_PARTICIPACAO_BANCA);
		return buscaDiscenteMBean.popular();
	}

	/** Método invocado pelo MBean de busca de discente, após a seleção de um discente da lista de resultados da busca.
	 * <br/><br/>
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {

		bancaSelecionada = getDAO(BancaDefesaDao.class).findByDiscente(discente);

		if (isEmpty(bancaSelecionada)) {
			addMensagemErro("Não há bancas cadastradas para o discente selecionado.");
			return null;
		}
		
		return forward("/ensino/banca_defesa/declaracao/membros_banca.jsp");

	}

	/** Gera o certificado de participação em banca de pós graduação para um dos membros selecioandos.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/banca_defesa/declaracao/membros_banca.jsp </li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String gerarCertificado() throws Exception {
		int id = getParameterInt("id");
		MembroBanca m = getDAO(BancaDefesaDao.class).findByPrimaryKey(id, MembroBanca.class);
				
		SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");

		ParticipacaoBancaDefesa participacao = new ParticipacaoBancaDefesa();	
		participacao.setCurso("CURSO DE " + m.getBanca().getDiscente().getCurso().getNome());
		participacao.setUnidade(m.getBanca().getDiscente().getUnidade().getNome());

		String nomeCoordenador = "";
		CoordenacaoCurso coordenacao = getDAO(CoordenacaoCursoDao.class).findUltimaByCurso(m.getBanca().getDiscente().getCurso());
		if (!isEmpty(coordenacao)) {
			nomeCoordenador = coordenacao.getServidor().getNome();
		}
		participacao.setMembros(m.getBanca().getMembrosBanca());
		participacao.setCoordenador(nomeCoordenador);
		participacao.setDataEmissao(sdf.format(new Date()));
		participacao.setDiscente(m.getBanca().getDiscente().getPessoa().getNome());
		String titulo = m.getBanca().getTitulo();
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
		
		participacao.setTipoParticipacao(m.getTipo().getDescricao());
		participacao.setMembros(m.getBanca().getMembrosBanca());
		participacao.setDataBanca(sdf.format(m.getBanca().getDataDefesa()));
		participacao.setSexo(m.getPessoa().getSexo() == 'M');
		participacao.setNivelBanca("Banca de Conclusão de Curso");

		List<ParticipacaoBancaDefesa> participacoes = new ArrayList<ParticipacaoBancaDefesa>();
		participacoes.add(participacao);
		
		preencherAssinatura(participacao,m);				
		
		obj = participacao;
		return forward("/ensino/banca_defesa/declaracao/declaracao_participacao_banca.jsp");
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
	private void preencherAssinatura(ParticipacaoBancaDefesa participacao, MembroBanca m) throws Exception {
		
		//Pega Coordenador e Vice coordenador do programa.
		Collection<CoordenacaoCurso> coordenadores = getDAO(CoordenacaoCursoDao.class).findCoordViceByCursoNivel(m.getBanca().getDiscente().getGestoraAcademica().getId(), m.getBanca().getDiscente().getNivel());
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
		for(MembroBanca mb : m.getBanca().getMembrosBanca()){
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
			setAssinatura("Prof(a). Dr(a). " + nomeViceCoordenador + "<br> Vice-Coordenador(a) do " + participacao.getCurso() + "- " + RepositorioDadosInstitucionais.get("siglaInstituicao"));
			
		}
		else {		 
			setAssinatura("Prof(a). Dr(a). " + participacao.getCoordenador() + "<br> Coordenador(a) do " + participacao.getCurso() + "- " + RepositorioDadosInstitucionais.get("siglaInstituicao"));
		}
	}
	
	
	/** Método invocado pelo MBean de busca por discente.
	 * Não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		if (discente.isGraduacao())
			this.discente = (DiscenteGraduacao) discente;
		else if (discente.isLato())
			this.discente = (DiscenteLato) discente;
		else if (discente.isTecnico())
			this.discente = (DiscenteTecnico) discente;
	}

	public Collection<BancaDefesa> getBancas() {
		return bancas;
	}

	public void setBancas(Collection<BancaDefesa> bancas) {
		this.bancas = bancas;
	}

	public BancaDefesa getBancaSelecionada() {
		return bancaSelecionada;
	}

	public void setBancaSelecionada(BancaDefesa bancaSelecionada) {
		this.bancaSelecionada = bancaSelecionada;
	}

	public DiscenteAdapter getDiscente() {
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
