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

	<li>Concess�o de Cotas de Bolsa
		<ul>
			<li><html:link
					action="/pesquisa/distribuirCotasDocentes?dispatch=iniciarDistribuicao&aba=iniciacao">Gerar Distribui��o de Cotas</html:link>
			</li>
			<li><html:link
					action="/pesquisa/distribuirCotasDocentes?dispatch=iniciarAjustes&aba=iniciacao">Efetuar Ajustes na Distribui��o de Cotas</html:link>
			</li>
			<li><h:commandLink id="limiteCota"
					value="Limite de Cota Excepcional"
					action="#{ limiteCotaExcepcionalMBean.listar }"
					onclick="setAba('iniciacao')" /></li>
		</ul>
	</li>

	<li>Alunos de Inicia��o Cient�fica
		<ul>
			<li><html:link
					action="/pesquisa/planoTrabalho/consulta?dispatch=buscar&popular=true&aba=iniciacao">Cadastrar</html:link>
			</li>
			<li><html:link
					action="/pesquisa/buscarMembroProjetoDiscente?dispatch=relatorio&popular=true&aba=iniciacao"> Gerenciar</html:link></li>
			<li><html:link
					action="/pesquisa/relatorioSubstituicoesBolsistas?dispatch=popular&aba=iniciacao"> Relat�rio de Indica��es e Substitui��es </html:link>
			</li>
			<li><h:commandLink id="listaPresenca" value="Lista de Presen�a"
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
	<li>Relat�rios de Inicia��o Cient�fica
		<ul>
			<li><html:link
					action="/pesquisa/relatorioBolsaParcial?dispatch=relatorio&popular=false&aba=iniciacao">Relat�rios Parciais</html:link>
			</li>
			<li><html:link
					action="/pesquisa/relatorioBolsaFinal?dispatch=relatorio&popular=true&aba=iniciacao">Relat�rios Finais </html:link></li>
		</ul>
	</li>
	<li>Congresso de Inicia��o Cient�fica
		<ul>
			<li><h:commandLink id="gerenciarCongr" action="#{congressoIniciacaoCientifica.listar}" value="Gerenciar Congressos de Inicia��o Cient�fica" onclick="setAba('iniciacao')" /></li>
			<li><html:link action="/pesquisa/resumoCongresso?dispatch=relatorio&popular=true&aba=iniciacao"> Consultar Resumos</html:link></li>
			<li><h:commandLink id="gerenciarAvaliador" action="#{avaliadorCIC.listar}" value="Gerenciar Avaliadores" onclick="setAba('iniciacao');" /></li>
			<li> <h:commandLink id="listaResumoCIC" action="#{autorizacaoResumo.listarResumosComissao}" value="Avaliar Resumos" onclick="setAba('iniciacao');"/> </li>
			<li><h:commandLink id="relAvaliadores" action="#{avaliacaoApresentacaoResumoBean.popularRelatorioAvaliadores}" value="Relat�rio de Avaliadores de Trabalhos do CIC" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerarNumeracao" action="#{organizacaoPaineis.iniciar}" value="Gerar Numera��o dos Pain�is de Resumos do CIC" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerenciarPres" action="#{avaliadorCIC.iniciarMarcacaoPresenca}" value="Gerenciar Presen�a de Avaliadores" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="alterarResCIC" action="#{alterarStatusResumos.iniciarMudancaStatus}" value="Alterar Status de Resumos CIC" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerenciarAusencias" action="#{justificativaCIC.iniciarLista}" value="Validar Justificativas de Aus�ncias" onclick="setAba('iniciacao');" /></li>
		</ul> 
		</li>
	<li>Avalia��o de Trabalhos do CIC
		<ul>
			<li><h:commandLink id="gerenciarDistrib"
					action="#{avaliacaoResumoBean.popularDistribuicao}"
					value="Gerenciar Distribui��o de Resumos para Corre��o"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="gerarDistrib"
					action="#{avaliacaoApresentacaoResumoBean.iniciarDistribuicaoAvaliacoesApresentacaoResumo}"
					value="Gerar Distribui��o de Avalia��es de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="ajusteDistrib"
					action="#{avaliacaoApresentacaoResumoBean.popularAjustesDistribuicao}"
					value="Efetuar Ajustes na Distribui��o de Avalia��es de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="relDistrib"
					action="#{avaliacaoApresentacaoResumoBean.popularRelatorioDistribuicao}"
					value="Relat�rio de Distribui��o de Avalia��es de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="fichasAva"
					action="#{avaliacaoApresentacaoResumoBean.popularRelatorioFichasAvaliacao}"
					value="Fichas de Avalia��o" onclick="setAba('iniciacao');" /></li>
			<li><h:commandLink id="avaliarApreTrab"
					action="#{avaliacaoApresentacaoResumoBean.listarResumosGestor}"
					value="Avaliar Apresenta��es de Trabalhos"
					onclick="setAba('iniciacao');" /></li>
		</ul>
	</li>
	<li>Declara��es
		<ul>
			<li><ufrn:link action="/pesquisa/buscarMembroProjetoDiscente"
					param="<%="dispatch=popular&aba=iniciacao&finalidadeBusca="
						+ MembroProjetoDiscenteForm.EMISSAO_DECLARACAO%>">
	       			Emitir Declara��o de Bolsista
	       	</ufrn:link></li>
			<li><ufrn:link action="/pesquisa/buscarMembrosProjeto"
					param="<%="dispatch=listar&popular=true&aba=iniciacao&finalidadeBusca="
						+ MembroProjetoServidorForm.DECLARACAO_COORDENACAO%>">
      				Declara��o de Coordena��o de Projetos
      		</ufrn:link></li>
			<li><ufrn:link action="/pesquisa/buscarMembrosProjeto"
					param="<%="dispatch=listar&popular=true&aba=iniciacao&finalidadeBusca="
						+ MembroProjetoServidorForm.DECLARACAO_ORIENTACOES%>">
	      				Declara��o de Orienta��es
	      		</ufrn:link></li>
		</ul>
	</li>
</ul>
