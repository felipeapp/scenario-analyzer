/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Processador responsável pela manutenção
 * dos resumos do congresso de iniciação
 * científica
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorResumoCongresso extends AbstractProcessador {

	/**
	 * Responsável pela execução do processamento dos resumos do Congresso de Iniciação Científica
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		// Validar dados
		validate(mov);
		
		if (SigaaListaComando.ENVIAR_RESUMO_CONGRESSO_IC.equals(mov.getCodMovimento())) {

			ResumoCongresso resumo = (ResumoCongresso) mov;

			// Inicializar DAOs
			CongressoIniciacaoCientificaDao congressoDao = getDAO(CongressoIniciacaoCientificaDao.class, mov);
			ResumoCongressoDao resumoDao = getDAO(ResumoCongressoDao.class, mov);
			AreaConhecimentoCnpqDao areaDao = getDAO(AreaConhecimentoCnpqDao.class, mov);

			// Definir dados do envio
			resumo.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			resumo.setDataEnvio(new Date());

			ResumoCongressoValidator.prepararDadosAutores(resumo.getAutores());

			// Persistir resumo
			if (resumo.getId() == 0) {

				// Buscar congresso ativo
				CongressoIniciacaoCientifica congresso = congressoDao.findAtivo();

				// Gerar código do resumo
				AreaConhecimentoCnpq area = resumo.getAreaConhecimentoCnpq();
				area = areaDao.findByPrimaryKey(area.getId(), AreaConhecimentoCnpq.class);

				if (area.getSigla() == null) {
					NegocioException ne = new NegocioException();
					ne.addErro("A área de conhecimento " + area.getNome()
									+ " não possui uma sigla definida para a geração do código de indentificação do resumo. "
									+ " Por favor,contacte o suporte através do \"Abrir Chamado\" para efetuar a atualização dos dados.");
					throw ne;
				}

				int sequencia = congressoDao.getProximoCodigo(congresso);
				resumo.setCodigo(area.getSigla() + UFRNUtils.completaZeros(sequencia, 4));

				resumo.setStatus(ResumoCongresso.AGUARDANDO_AUTORIZACAO);

				resumo.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());

				resumoDao.create(resumo);

				HistoricoResumoCongresso historico = gerarEntradaHistorico(resumo, mov);
				resumoDao.create(historico);

			} else {

				// Verificar e persistir remoção de autores
				ResumoCongresso resumoGravado = resumoDao.find(resumo.getId());
				resumoGravado.getAutores().size();
				resumoDao.detach(resumoGravado);

				//Remoção dos Autores que não fazem mais parte do Resumo do congresso
				for (AutorResumoCongresso autor : resumoGravado.getAutores()) {
					if (!resumo.getAutores().contains(autor)) {
						resumoDao.remove(autor);
					}
				}

				//Inserção de novos CoAutores no resumo do Congresso.
				for (AutorResumoCongresso autor : resumo.getCoAutores()) {
					if (!resumoGravado.getCoAutores().contains(autor)) {
						resumoDao.create(autor);
					}
				}


				// Verificar se é uma correção de resumo e atualizar seu status
				if (resumoGravado.getStatus() != resumo.getStatus()) {
					resumo.setStatus(resumo.getStatus());
					resumoDao.update(resumo);
				}
				
				if (!(resumoGravado.equals(resumo)) || resumoGravado.getOrientador().getDocente().getId() != resumo.getOrientador().getDocente().getId()) {
					GenericDAO dao = getGenericDAO(mov);
					if (resumoGravado.getStatus() == ResumoCongresso.RECUSADO_NECESSITA_CORRECOES) 
						resumo.setStatus(ResumoCongresso.AGUARDANDO_AUTORIZACAO);
					
					if (resumoGravado.getStatus() == ResumoCongresso.NECESSITA_CORRECOES) 
						resumo.setStatus(ResumoCongresso.CORRIGIDO_AGUARDANDO_APROVACAO);
					
					dao.createOrUpdate(resumo);
					dao.close();
				}
				
				HistoricoResumoCongresso historico = gerarEntradaHistorico(resumo, mov);
				resumoDao.create(historico);
			}
			resumoDao.close();
			areaDao.close();
			congressoDao.close();
			return resumo;
			
		} else if (SigaaListaComando.AUTORIZAR_RESUMO_CONGRESSO_IC.equals(mov.getCodMovimento())) {
			
			ResumoCongresso resumo = (ResumoCongresso) mov;
			GenericDAO dao = getGenericDAO(mov);
			dao.updateField(ResumoCongresso.class, resumo.getId(), "status", ResumoCongresso.SUBMETIDO);
			dao.close();
			
		} else if (SigaaListaComando.RECUSAR_RESUMO_CONGRESSO_IC.equals(mov.getCodMovimento())) {
			
			ResumoCongresso resumo = (ResumoCongresso) mov;
			GenericDAO dao = getGenericDAO(mov);
			dao.updateField(ResumoCongresso.class, resumo.getId(), "status", ResumoCongresso.NAO_AUTORIZADO);
			dao.close();

		} else if (SigaaListaComando.RECUSADO_NECESSITA_CORRECOES.equals(mov.getCodMovimento())) {
		
			ResumoCongresso resumo = (ResumoCongresso) mov;
			GenericDAO dao = getGenericDAO(mov);
			dao.updateFields(ResumoCongresso.class, resumo.getId(), new String[]{"status", "correcao"}, 
					new Object[]{ResumoCongresso.RECUSADO_NECESSITA_CORRECOES, resumo.getCorrecao()});
			dao.close();
		}
		
		return mov;
	}
	
	/**
	 * Gerar um objeto que representa uma entrada no histórico de um plano de trabalho
	 *
	 * @param resumoCongresso
	 * @param mov
	 * @return
	 */
	public static HistoricoResumoCongresso gerarEntradaHistorico(ResumoCongresso resumoCongresso, Movimento mov) {
		HistoricoResumoCongresso historico = new HistoricoResumoCongresso();
		historico.setResumoCongresso(resumoCongresso);
		historico.setStatus(resumoCongresso.getStatus());
		historico.setData( new Date() );
		historico.setRegistroEntrada(  mov.getUsuarioLogado().getRegistroEntrada() );

		return historico;
	}

	/**
	 * Responsável pela validação que é realizada antes da execução do movimento 
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();

		// Inicializar DAOs
		ResumoCongressoDao resumoDao = getDAO(ResumoCongressoDao.class, mov);
		CongressoIniciacaoCientificaDao congressoDao = getDAO(CongressoIniciacaoCientificaDao.class, mov);

		ResumoCongresso resumo = (ResumoCongresso) mov;

		// Buscar congresso ativo
		CongressoIniciacaoCientifica congresso = congressoDao.findAtivo();

		// Validar unicidade de resumo por autor por congresso
		ResumoCongresso resumoCadastrado = resumoDao.findByAutorPrincipal( resumo.getAutor() , congresso);
		if ( resumoCadastrado != null && resumoCadastrado.getId() != resumo.getId() ) {
			erros.addErro("Já existe um resumo cadastrado com este autor para o " +
					congresso.getEdicao() + " Congresso de Iniciação Científica.");
		}

		resumoDao.close();
		congressoDao.close();
		
		// Validar resumo
		erros.addAll( resumo.validate() );
		checkValidation(resumo.validate());
	}

}