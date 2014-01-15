/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;
import br.ufrn.sigaa.ensino.stricto.negocio.MovimentoAtaBanca;

@Component("ataBancaMBean")
@Scope("session")
public class AtaBancaMBean extends SigaaAbstractController<BancaPos> implements
		OperadorDiscente {

	// todas as bancas que o discente possuir
	private Collection<BancaPos> bancasDoDiscente;

	// banca selecionado para envio da ata
	private BancaPos bancaSelecionada;

	// membros da banca selecionada
	private List<MembroBancaPos> membrosBanca;

	// arquivo de ata
	private UploadedFile arquivo;

	// aluno que será realizado a operação
	private DiscenteStricto discente;

	// parâmetro que recebe o id da banca selecionada
	private Integer idBanca;
	

	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO);

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean
				.setCodigoOperacao(OperacaoDiscente.ENVIAR_ATA_BANCA_POS);
		prepareMovimento(SigaaListaComando.CADASTRAR_ATA_BANCA_POS);
		return buscaDiscenteMBean.popular();
	}

	public String selecionarArquivo() {

		try {
			if (arquivo != null && arquivo.getSize() != 0) {

				Arquivo arquivoMovimento = new Arquivo(arquivo.getBytes(),
						arquivo.getInputStream(), arquivo.getContentType(),
						arquivo.getName(), arquivo.getSize());

				chamarProcessador(arquivoMovimento);

			} else {
				addMensagemErro("Arquivo de Ata não foi selecionado");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}

		return telaPosUpload();
	}

	
	private String chamarProcessador(Arquivo arquivoMovimento) {
		try {
			MovimentoAtaBanca mov = new MovimentoAtaBanca();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_ATA_BANCA_POS);
			mov.setBanca(bancaSelecionada);
			mov.setAta(arquivoMovimento);
			mov.setUsuarioLogado(getUsuarioLogado());

			executeWithoutClosingSession(mov, getCurrentRequest());

			addMessage("Ata de parecer da banca enviado com sucesso",
					TipoMensagemUFRN.INFORMATION);
			return cancelar();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErroPadrao();
			notifyError(e);
			return null;
		}
	}

	public String selecionaDiscente() throws DAOException {

		if (discente == null) {
			addMensagemErro("Não foi possível carregar dados do discente selecionado");
			return null;
		}

		// carregar bancas do discente
		BancaPosDao bpdao = getDAO(BancaPosDao.class);
		bancasDoDiscente = bpdao.findByDiscente(discente);
		if (isEmpty(bancasDoDiscente)) {
			addMensagemErro("O discente " + discente.getNome()
					+ " não possui banca cadastrada");
			return null;
		}
		return telaListaBanca();
	}

	public String selecionaBanca() throws DAOException {

		if (getIdBanca() == null) {
			addMensagemErro("Não foi possível carregar dados da banca selecionada");
			return null;
		}

		BancaPosDao bpdao = getDAO(BancaPosDao.class);

		// as bancas já foram recuperadas na hora de listar
		for (BancaPos banca : bancasDoDiscente) {
			if (banca.getId() == getIdBanca()) {
				membrosBanca = bpdao.findMembrosByBanca(banca);

				banca.setMembrosBanca(membrosBanca);

				bancaSelecionada = banca;
			}
		}		

		return telaUploadAta();
	}

	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		try {
			this.discente = (DiscenteStricto) getDAO(DiscenteDao.class).findByPK(discente.getId());
		} catch (Exception e) {
			discente = null;
			e.printStackTrace();
		}

	}

	public String cancelar() {
		// removendo da sessão
		resetBean();
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String telaListaBanca() {
		return forward("/stricto/banca_pos/listar_bancas.jsp");
	}

	public String telaUploadAta() {
		return forward("/stricto/banca_pos/upload_ata.jsp");
	}

	public String telaPosUpload() {
		return forward("/verMenuStricto.do");

	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public Collection<BancaPos> getBancasDoDiscente() {
		return bancasDoDiscente;
	}

	public void setBancasDoDiscente(Collection<BancaPos> bancasDoDiscente) {
		this.bancasDoDiscente = bancasDoDiscente;
	}

	public Integer getIdBanca() {
		return idBanca;
	}

	public void setIdBanca(Integer idBanca) {
		this.idBanca = idBanca;
	}

	public BancaPos getBancaSelecionada() {
		return bancaSelecionada;
	}

	public void setBancaSelecionada(BancaPos bancaSelecionada) {
		this.bancaSelecionada = bancaSelecionada;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

}
