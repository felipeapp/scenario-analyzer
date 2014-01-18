<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoServidorForm"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.GrupoPesquisaForm"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm"%>

<ul>
	<li>Planos de Trabalho
		<ul>
			<li><html:link
					action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=cadastroPlanoTrabalho&popular=true&aba=iniciacao">Cadastrar</html:link>
			</li>
			<li><html:link
					action="/pesquisa/planoTrabalho/consulta?dispatch=buscar&popular=true&aba=iniciacao">Gerenciar</html:link>
			</li>
			<li><html:link
					action="/pesquisa/finalizarPlanosTrabalho?dispatch=iniciar&aba=iniciacao">Finalizar Planos de uma Cota</html:link>
			</li>
			<li><h:commandLink id="finalizarPlanosSemCota"
					action="#{finalizarPlanoTrabalho.iniciar}"
					value="Finalizar Planos de Trabalho sem Cota"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="aprovarPlanos"
					action="#{aprovarPlanoTrabalho.iniciarBusca}"
					value="Aprovar Planos de Trabalhos Corrigidos"
					onclick="setAba('iniciacao');" /></li>
		</ul>
	</li>

	<li>Concessão de Cotas de Bolsa
		<ul>
			<li><html:link
					action="/pesquisa/distribuirCotasDocentes?dispatch=iniciarDistribuicao&aba=iniciacao">Gerar Distribuição de Cotas</html:link>
			</li>
			<li><html:link
					action="/pesquisa/distribuirCotasDocentes?dispatch=iniciarAjustes&aba=iniciacao">Efetuar Ajustes na Distribuição de Cotas</html:link>
			</li>
			<li><h:commandLink id="limiteCota"
					value="Limite de Cota Excepcional"
					action="#{ limiteCotaExcepcionalMBean.listar }"
					onclick="setAba('iniciacao')" /></li>
		</ul>
	</li>

	<li>Alunos de Iniciação Científica
		<ul>
			<li><html:link
					action="/pesquisa/planoTrabalho/consulta?dispatch=buscar&popular=true&aba=iniciacao">Cadastrar</html:link>
			</li>
			<li><html:link
					action="/pesquisa/buscarMembroProjetoDiscente?dispatch=relatorio&popular=true&aba=iniciacao"> Gerenciar</html:link></li>
			<li><html:link
					action="/pesquisa/relatorioSubstituicoesBolsistas?dispatch=popular&aba=iniciacao"> Relatório de Indicações e Substituições </html:link>
			</li>
			<li><h:commandLink id="listaPresenca" value="Lista de Presença"
					action="#{ listaPresencaPesquisa.visualizaListaPresenca }"
					onclick="setAba('iniciacao')" /></li>
			<li><h:commandLink id="enviarMsg" value="Enviar mensagem"
					action="#{ notificacoesPesquisaBean.iniciar }"
					onclick="setAba('iniciacao')" /></li>
			<li><h:commandLink id="homologarCadastro"
					value="Homologar Cadastro de Bolsistas de Pesquisa no SIPAC"
					action="#{ homologacaoBolsistaPesquisaBean.popular }"
					onclick="setAba('iniciacao')" /></li>
			<li><h:commandLink id="finalizarCadastro"
					value="Finalizar Cadastro de Bolsistas de Pesquisa no SIPAC"
					action="#{ homologacaoBolsistaPesquisaBean.popularFinalizar }"
					onclick="setAba('iniciacao')" /></li>
		</ul>
	</li>
	<li>Relatórios de Iniciação Científica
		<ul>
			<li><html:link
					action="/pesquisa/relatorioBolsaParcial?dispatch=relatorio&popular=false&aba=iniciacao">Relatórios Parciais</html:link>
			</li>
			<li><html:link
					action="/pesquisa/relatorioBolsaFinal?dispatch=relatorio&popular=true&aba=iniciacao">Relatórios Finais </html:link></li>
		</ul>
	</li>
	<li>Congresso de Iniciação Científica
		<ul>
			<li><h:commandLink id="gerenciarCongr" action="#{congressoIniciacaoCientifica.listar}" value="Gerenciar Congressos de Iniciação Científica" onclick="setAba('iniciacao')" /></li>
			<li><html:link action="/pesquisa/resumoCongresso?dispatch=relatorio&popular=true&aba=iniciacao"> Consultar Resumos</html:link></li>
			<li><h:commandLink id="gerenciarAvaliador" action="#{avaliadorCIC.listar}" value="Gerenciar Avaliadores" onclick="setAba('iniciacao');" /></li>
			<li> <h:commandLink id="listaResumoCIC" action="#{autorizacaoResumo.listarResumosComissao}" value="Avaliar Resumos" onclick="setAba('iniciacao');"/> </li>
			<li><h:commandLink id="relAvaliadores" action="#{avaliacaoApresentacaoResumoBean.popularRelatorioAvaliadores}" value="Relatório de Avaliadores de Trabalhos do CIC" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerarNumeracao" action="#{organizacaoPaineis.iniciar}" value="Gerar Numeração dos Painéis de Resumos do CIC" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerenciarPres" action="#{avaliadorCIC.iniciarMarcacaoPresenca}" value="Gerenciar Presença de Avaliadores" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="alterarResCIC" action="#{alterarStatusResumos.iniciarMudancaStatus}" value="Alterar Status de Resumos CIC" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerenciarAusencias" action="#{justificativaCIC.iniciarLista}" value="Validar Justificativas de Ausências" onclick="setAba('iniciacao');" /></li>
		</ul> 
		</li>
	<li>Avaliação de Trabalhos do CIC
		<ul>
			<li><h:commandLink id="gerenciarDistrib"
					action="#{avaliacaoResumoBean.popularDistribuicao}"
					value="Gerenciar Distribuição de Resumos para Correção"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerarDistrib"
					action="#{avaliacaoApresentacaoResumoBean.iniciarDistribuicaoAvaliacoesApresentacaoResumo}"
					value="Gerar Distribuição de Avaliações de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="ajusteDistrib"
					action="#{avaliacaoApresentacaoResumoBean.popularAjustesDistribuicao}"
					value="Efetuar Ajustes na Distribuição de Avaliações de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="relDistrib"
					action="#{avaliacaoApresentacaoResumoBean.popularRelatorioDistribuicao}"
					value="Relatório de Distribuição de Avaliações de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="fichasAva"
					action="#{avaliacaoApresentacaoResumoBean.popularRelatorioFichasAvaliacao}"
					value="Fichas de Avaliação" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="avaliarApreTrab"
					action="#{avaliacaoApresentacaoResumoBean.listarResumosGestor}"
					value="Avaliar Apresentações de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
		</ul>
	</li>
	<li>Declarações
		<ul>
			<li><ufrn:link action="/pesquisa/buscarMembroProjetoDiscente"
					param="<%="dispatch=popular&aba=iniciacao&finalidadeBusca="
						+ MembroProjetoDiscenteForm.EMISSAO_DECLARACAO%>">
	       			Emitir Declaração de Bolsista
	       	</ufrn:link></li>
			<li><ufrn:link action="/pesquisa/buscarMembrosProjeto"
					param="<%="dispatch=listar&popular=true&aba=iniciacao&finalidadeBusca="
						+ MembroProjetoServidorForm.DECLARACAO_COORDENACAO%>">
      				Declaração de Coordenação de Projetos
      		</ufrn:link></li>
			<li><ufrn:link action="/pesquisa/buscarMembrosProjeto"
					param="<%="dispatch=listar&popular=true&aba=iniciacao&finalidadeBusca="
						+ MembroProjetoServidorForm.DECLARACAO_ORIENTACOES%>">
	      				Declaração de Orientações
	      		</ufrn:link></li>
		</ul>
	</li>
</ul>
