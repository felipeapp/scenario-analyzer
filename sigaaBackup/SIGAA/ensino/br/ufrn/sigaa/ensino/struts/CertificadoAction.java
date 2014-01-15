/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.ensino.struts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Certificado;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Action para geração de certificados para os discentes
 *
 * @author David Ricardo
 *
 */
public class CertificadoAction extends SigaaAbstractAction {

	public ActionForward escolheAluno(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return mapping.findForward("escolhe");
	}

	public ActionForward exibirCertificado(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		prepareMovimento(ArqListaComando.CADASTRAR, req);

		UnidadeDao dao = getDAO(UnidadeDao.class, req);
		PessoaDao pDao = getDAO(PessoaDao.class, req);

		ParametrosGestoraAcademica param;


		Discente d;
		param = getParametrosAcademicos(req);
		int id = RequestUtils.getIntParameter(req, "id");
		d = dao.findByPrimaryKey(id, Discente.class);
		d.setPessoa(pDao.findCompleto(d.getPessoa().getId()));

		if(d.getPessoa().getMunicipio() == null){
			addMensagemErro("Município", req);
		}else if(d.getPessoa().getMunicipio().getUnidadeFederativa() == null){
			addMensagemErro("Estado", req);
		}
		if(d.getPessoa().getPais() == null){
			addMensagemErro("País", req);
		}
		
		if(flushErros(req)){
			return mapping.findForward("escolhe");
		}
		
		
		Certificado cert = new Certificado();
		cert.setNomeDiploma(d.getPessoa().getNome());
		cert.setDataDiploma(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		cert.setDataConclusao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		cert.setEstado(d.getPessoa().getMunicipio().getUnidadeFederativa().getDescricao());
		cert.setHabilitacao("Profissional Técnico em Informática do Ensino Médio");
		cert.setNacionalidade(d.getPessoa().getPais().getNacionalidade());

		if (d.getPessoa().getIdentidade() != null)
			cert.setIdentidade(String.valueOf(d.getPessoa().getIdentidade().getNumero()));
		else
			cert.setIdentidade("0");

		if (d.getPessoa().getDataNascimento() != null)
			cert.setNascimento(new SimpleDateFormat("dd/MM/yyyy").format(d.getPessoa().getDataNascimento()));
		else
			cert.setNascimento("01/01/1980"); // apenas para testes

		cert.setNaturalidade(d.getPessoa().getMunicipio().getNome());
		cert.setNomeMae(d.getPessoa().getNomeMae());
		cert.setNomePai(d.getPessoa().getNomePai());
		cert.setTituloProfissional("TÉCNICO EM INFORMÁTICA");

		ArrayList<Certificado> certificados = new ArrayList<Certificado>();
		certificados.add(cert);

		HashMap<String, String> parametros = new HashMap<String, String>();
		parametros.put("reitor", "José Ivonildo do Rêgo");
		parametros.put("diretorescola", "Edilene Rodrigues da Silva");
		parametros.put("diretorccs", "Juarez da Costa Ferreira");
		parametros.put("secretaria", "Maria das Graças Rocha de Medeiros");

	    JRDataSource jrds = new JRBeanCollectionDataSource(certificados);
	    JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA( param.getCertificado() ), parametros, jrds);

	    res.setContentType("application/pdf");
        res.addHeader("Content-Disposition", "attachment; filename=certificado.pdf");
        JasperExportManager.exportReportToPdfStream(prt,res.getOutputStream());

        /*// registrando a emissão do documento
        EmissaoDocumento emissão = new EmissaoDocumento();
        emissao.setData(new Date());
        emissao.setDiscente(d);
        emissao.setEntrada(getUsuarioLogado(req).getRegistroEntrada());
        emissao.setTipo(EmissaoDocumento.CERTIFICADO_TECNICO);*/

       /* // testando se eh a primeira emissão desse tipo de documento para esse aluno
        EmissaoDocumentoDao docDao = getDAO(EmissaoDocumentoDao.class, req);
		try {
			long qtd = docDao.qtdEmissoes(emissao.getDiscente().getId(), emissao.getTipo());
			if (qtd > 0) {
				emissao.setReimpressao(Boolean.TRUE);
			}
		} finally {
			docDao.close();
		}

        MovimentoCadastro mov = new MovimentoCadastro();
        mov.setCodMovimento(getUltimoComando(req));
        mov.setUsuarioLogado(getUsuarioLogado(req));
        mov.setObjMovimentado(emissao);*/

		/*execute(mov, req);*/

		addMensagem(new MensagemAviso("Certificado gerado com sucesso.", TipoMensagemUFRN.INFORMATION), req);

	    return null;
	}

}
