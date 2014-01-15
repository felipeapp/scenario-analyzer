<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria"%>

<f:view>
	<h:outputText value="#{relatorioProjetoMonitoria.create}" />

	<h2>Visualiza��o de Relat�rio do Projeto</h2>

		<table class="tabelaRelatorio" width="100%">
			<caption class="listagem"><h:outputText value="#{relatorioProjetoMonitoria.obj.tipoRelatorio.descricao}"/> DE PROJETO DE ENSINO</caption>


			<tr>
				<td><b>PROJETO DE ENSINO:</b><br/>
				<h:outputText value="#{relatorioProjetoMonitoria.obj.projetoEnsino.anoTitulo}" /></td>
			</tr>


			<tr>
				<td><b>COORDENADOR(A) DO PROJETO:</b><br/>
				<h:outputText value="#{relatorioProjetoMonitoria.obj.projetoEnsino.coordenacao.pessoa.nome}" /></td>
			</tr>
			
			<tr>
				<td><b> SITUA��O DO RELAT�RIO: </b><br/>
				<h:outputText id="txaSituacao"	value="#{relatorioProjetoMonitoria.obj.status.descricao}" /></td>
			</tr>

			<tr>
				<td><b> DATA DE ENVIO: </b><br/>
				<fmt:formatDate value="${relatorioProjetoMonitoria.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss"/>
				<h:outputText id="txaAviso" value="RELAT�RIO AINDA N�O ENVIADO PARA PROGRAD!" rendered="#{empty relatorioProjetoMonitoria.obj.dataEnvio}" />
				</td>
			</tr>
			
			<tr>
				<td><hr/></td>
			</tr>
			
			


			<tr>
				<td align="justify"><b> A)QUANTO AOS OBJETIVOS: </b> <br />

				<b><i>1 - De acordo com a Resolu��o n� 013/2006 - CONSEPE, s�o objetivos do
				programa de monitoria: contribuir para a melhoria do ensino na
				gradua��o, contribuir para o processo de forma��o do estudante e
				despertar no monitor o interesse pela carreira docente. <br />

				Com base nos objetivos do programa e do projeto, aponte aqueles que
				foram alcan�ados e os que n�o foram, explicitando motivos que
				dificultaram a realiza��o dos mesmos. </i></b><br /><br />

				<h:outputText id="txaObjetivosAlcancados" value="#{relatorioProjetoMonitoria.obj.objetivosAlcancados}"/></td>
			</tr>


			<tr>
				<td align="justify"><br/><b> B)QUANTO �S ATRIBUI��ES DOS MONITORES: </b> <br />

				<b><i>1 - Relacione as atribui��es executadas pelos monitores. </i></b><br /><br />
					<h:outputText id="txaAtribuicoesExecutadas"	value="#{relatorioProjetoMonitoria.obj.atribuicoesExecutadas}" />
					
				</td>
			</tr>

			<tr>
				<td align="justify"><br/><b> C)QUANTO � PARTICIPA��O DOS MONITORES NO SEMIN�RIO
				DE INICIA��O � DOC�NCIA (SID) : </b> <br />
				<b><i> 1 -Os monitores cumpriram as exig�ncias postas pelo programa de
				monitoria, quanto � apresenta��o de resultados parciais alcan�ados
				pelo projeto?</i></b> <br /><br />

				<h:outputText value="Sim" rendered="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigencias == 1}"/>
				<h:outputText value="N�o" rendered="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigencias == 2}"/>
				<h:outputText value="Parcialmente" rendered="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigencias == 3}"/>
				
			</tr>


			<tr>
				<td align="justify"><b><i> 2 - Se as respostas for n�o ou parcialmente explicite os
				motivos: </i></b><br /><br />

				<h:outputText id="txaMonitoresCumpriramExigenciasJustificativa"	value="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigenciasJustificativa}" />
				</td>
			</tr>




			<tr>
				<td align="justify"><b><i> 3 - A participa��o dos membros do projeto ( coordenador,
				orientador, monitores), no SID, foi satisfat�ria? Comente. </i></b><br /><br />

				<h:outputText value="Sim" rendered="#{relatorioProjetoMonitoria.obj.participacaoMembrosSid == 1}"/>
				<h:outputText value="N�o" rendered="#{relatorioProjetoMonitoria.obj.participacaoMembrosSid == 2}"/>
				<h:outputText value="Ruim" rendered="#{relatorioProjetoMonitoria.obj.participacaoMembrosSid == 3}"/>								
				
			</tr>


			<tr>
				<td align="justify"><b><i>4 - Se a resposta for regular ou ruim explicite os motivos: </i></b><br /><br />
				<h:outputText id="txaParticipacaoMembrosSidJustificativa" value="#{relatorioProjetoMonitoria.obj.participacaoMembrosSidJustificativa}"/>
				</td>
			</tr>



			<tr>
				<td align="justify"><br/><b> D)QUANTO � METODOLOGIA APLICADA: </b><br />

				<b><i>Escreva estrat�gias que foram desenvolvidas durante a vig�ncia do
				projeto, explicitando-as quanto aos seguintes aspectos:</i></b> <br />

				<b><i>1 - Articula��o com o projeto pol�tico-pedag�gico do curso. </i></b><br /><br />

				<h:outputText id="txaArticulacaoPoliticoPedagogico"	value="#{relatorioProjetoMonitoria.obj.articulacaoPoliticoPedagogico}" />
				</td>
			</tr>


			<tr>
				<td align="justify"><b><i>2 - �nfase no est�mulo � inicia��o � doc�ncia. </i></b><br /><br />

				<h:outputText id="txaEstimuloIniciacaoDocencia"	value="#{relatorioProjetoMonitoria.obj.estimuloIniciacaoDocencia}" />
				</td>
			</tr>

			<tr>
				<td align="justify"><b><i>3 - Fun��o do monitor como apoio pedag�gico ao
				desenvolvimento das atividades. </i></b><br /><br />

				<h:outputText id="txaFuncaoMonitor"	value="#{relatorioProjetoMonitoria.obj.funcaoMonitor}" /></td>
			</tr>

			<tr>
				<td align="justify"><b><i>4 - Integra��o entre as �reas do conhecimento. </i></b><br /><br />
				
				<h:outputText id="txaIntegracaoEntreAreas" value="#{relatorioProjetoMonitoria.obj.integracaoEntreAreas}" /></td>
			</tr>

			<tr>
				<td align="justify"><b><i>5 - Car�ter pedag�gico inovador. </i></b><br /><br />

				<h:outputText id="txaCaraterInovador" value="#{relatorioProjetoMonitoria.obj.caraterInovador}" /></td>
			</tr>

			<tr>
				<td align="justify"><br/><b> E)QUANTO AO APRIMORAMENTO DO PROJETO: </b> <br />

				<b><i>Indique sugest�es que levem � supera��o das dificuldades e
				aperfei�oamento do projeto. </i></b><br /><br />
				<h:outputText id="txaSugestoes"	value="#{relatorioProjetoMonitoria.obj.sugestoes}" /></td>
			</tr>


			<c:set var="RELATORIO_PARCIAL" value="<%=String.valueOf(TipoRelatorioMonitoria.RELATORIO_PARCIAL)%>" scope="application"/>
			<c:if test="${relatorioProjetoMonitoria.obj.tipoRelatorio.id == RELATORIO_PARCIAL }">
				<tr>
					<td><b>Deseja renovar este projeto? </b><br />
					<br />
					<h:outputText value="#{relatorioProjetoMonitoria.obj.desejaRenovarProjeto ? 'Sim' : 'N�o'}"/>					
				</tr>
			</c:if>
			
			
			
		</table>
		
		
		<c:set var="avaliacoes" value="${relatorioProjetoMonitoria.obj.avaliacoes}" />
		<c:if test="${not empty avaliacoes}">
		<table class="listagem">
			
			<thead>
				<tr>
					<th>Departamento</th>
					<th>Data Avalia��o</th>
					<th>Recomenda��o</th>
										
			</thead>
		
			<c:forEach items="#{avaliacoes}" var="avaliacao" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${avaliacao.avaliador.servidor.unidade.nome} </td>
					<td> <fmt:formatDate pattern="dd/MM/yyyy" value="${avaliacao.dataAvaliacao}"/> </td>
					<td> 
						<h:outputText value="#{avaliacao.recomendaRenovacao ? 'SIM':'N�O'}" rendered="#{not empty avaliacao.dataAvaliacao}"/>
						<h:outputText value="N�O AVALIADO" rendered="#{empty avaliacao.dataAvaliacao}"/>
					</td>					
				</tr>
			</c:forEach>		
		</table>
		</c:if>
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>