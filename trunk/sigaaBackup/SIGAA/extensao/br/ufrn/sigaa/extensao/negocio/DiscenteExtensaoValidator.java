package br.ufrn.sigaa.extensao.negocio;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioBolsistaExtensaoDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

public class DiscenteExtensaoValidator {

	/**
	 * Valida a inscri��o do discente em uma a��o de extens�o
	 *
	 * @param discente
	 * @param listaSelecao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaInscricaoSelecao(DiscenteAdapter discente, AtividadeExtensao atividade, ListaMensagens lista) throws ArqException {


		//verificando se o discente est� concluinte ou afastado
		if(	(discente.getStatus() == StatusDiscente.CONCLUIDO)
				|| (discente.getStatus() == StatusDiscente.GRADUANDO)
				|| (discente.getStatus() == StatusDiscente.AFASTADO)
				|| (discente.getStatus() == StatusDiscente.CANCELADO)
				|| (discente.getStatus() == StatusDiscente.EXCLUIDO)
				|| (discente.getStatus() == StatusDiscente.JUBILADO)
				|| (discente.getStatus() == StatusDiscente.TRANCADO)
				|| (discente.getStatus() == StatusDiscente.CADASTRADO))
			lista.addErro("Discentes concluintes ou com algum tipo de afastamento n�o podem participar de a��es de extens�o.");


		/** verificando se o discente j� se inscreveu para a sele��o desta a��o antes */
		InscricaoSelecaoExtensaoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoExtensaoDao.class);
		InscricaoSelecaoExtensao inscricao = dao.findByDiscenteAtividade( discente.getId(), atividade.getId());

		if (inscricao != null && inscricao.getId() != 0) {
			lista.addErro("Voc� j� realizou a inscri��o para esta a��o de extens�o, portanto n�o pode realizar novamente.");
		}

		//verifica se o aluno tem capacidade t�cnica de participar da sele��o
		validaCapacidadeTecnicaDiscente(discente, atividade, lista);		

	}


	/**
	 * TODO adaptar ao processo seletivo de extens�o
	 * 
	 * Valida a capacidade t�cnica do discente candidato a bolsa da a��o de extens�o.
	 * verifica se o discente j� cursou as disciplinas obrigat�rias do projeto e se o 
	 * projeto n�o possui disciplinas obrigat�rias, verifica se o aluno, em todas as disciplinas
	 * do projeto que ele tenha pago, tem m�dia igual ou superior a 7
	 * 
	 * @param discente
	 * @param projeto
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaCapacidadeTecnicaDiscente(DiscenteAdapter discente, AtividadeExtensao atividade, ListaMensagens lista) throws ArqException {		

	}

	/**
	 * Valida a emiss�o de certificado do discente de extens�o.
	 * 
	 * @param discenteExtensao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaEmissaoCertificado(DiscenteExtensao discenteExtensao, ListaMensagens lista) throws ArqException {		
		if ( ParametroHelper.getInstance().getParametroBoolean(ParametrosExtensao.NECESSARIO_ENVIAR_RELATORIO_FINAL) ) {
			RelatorioBolsistaExtensaoDao dao = DAOFactory.getInstance().getDAO(RelatorioBolsistaExtensaoDao.class);
			try {
				boolean enviouRelatorio  = dao.isDiscenteEnviouRelatorio(discenteExtensao.getId(), TipoRelatorioExtensao.RELATORIO_FINAL);
				if (!enviouRelatorio) {
					lista.addErro("O certificado n�o pode ser emitido porque o Relat�rio Final do discente n�o foi enviado." +
					"Cadastre o relat�rio solicitado e tente realizar esta opera��o novamente.");
				}
	
				if (!discenteExtensao.isPassivelEmissaoCertificado()) {
					lista.addErro("O certificado n�o pode ser emitido porque a A��o de Extens�o ainda n�o foi conclu�da.");
				}
	
			}finally{
				dao.close();
			}
		}
	}

}