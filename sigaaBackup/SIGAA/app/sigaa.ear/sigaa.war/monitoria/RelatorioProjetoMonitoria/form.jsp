<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria"%>

<f:view>
	
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText value="#{relatorioProjetoMonitoria.create}" />
	<h2><ufrn:subSistema /> &gt; Cadastro de Relatório do Projeto</h2>

	<h:messages showDetail="true" showSummary="true"/>

<a4j:region>

	<center><font color="red" size="2"><a4j:status startText="Salvando Relatório..." stopText=""/><br/></font></center>
	
	<h:form id="form">

		<h:inputHidden value="#{relatorioProjetoMonitoria.confirmButton}" />
		<h:inputHidden value="#{relatorioProjetoMonitoria.obj.id}" />
		<h:inputHidden value="#{relatorioProjetoMonitoria.obj.projetoEnsino.id}" />		

		<table class="formulario" width="100%">
			<caption class="listagem"><h:outputText value="#{relatorioProjetoMonitoria.obj.tipoRelatorio.descricao}"/> DE PROJETO DE ENSINO</caption>

			<tr>
				<td><b>PROJETO DE ENSINO:</b><br/>
				<h:outputText value="#{relatorioProjetoMonitoria.obj.projetoEnsino.titulo}" /></td>
			</tr>

			<tr>
				<td>
			
				<b> A) QUANTO AOS OBJETIVOS: </b> <br />

				De acordo com a Resolução nº 013/2006 - CONSEPE, são objetivos do
				programa de monitoria: contribuir para a melhoria do ensino na
				graduação, contribuir para o processo de formação do estudante e
				despertar no monitor o interesse pela carreira docente. <br />

				Com base nos objetivos do programa e do projeto, aponte aqueles que
				foram alcançados e os que não foram, explicitando motivos que
				dificultaram a realização dos mesmos. <br />

				<h:inputTextarea id="txaObjetivosAlcancados" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.objetivosAlcancados}" rows="5" /></td>
			</tr>

			<tr>
				<td><b> B) QUANTO ÁS ATRIBUIÇÕES DOS MONITORES: </b> <br />
				Relacione as atribuições executadas pelos monitores. <br />
				
				<h:inputTextarea id="txaAtribuicoesExecutadas" style="width:98%" 
					value="#{relatorioProjetoMonitoria.obj.atribuicoesExecutadas}" rows="5"	/>
				</td>
			</tr>

			<tr>
				<td><b> C) QUANTO À PARTICIPAÇÃO DOS MONITORES NO SEMINÁRIO
				DE INICIAÇÃO À DOCÊNCIA (SID) : </b> <br />
				Os monitores cumpriram as exigências postas pelo programa de
				monitoria, quanto à apresentação de resultados parciais alcançados
				pelo projeto? <br />

				<h:selectOneRadio
					value="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigencias}"
					id="radioMonitoresCumpriramExigencias">
					<f:selectItem itemLabel="Sim" itemValue="1" />
					<f:selectItem itemLabel="Não" itemValue="2" />
					<f:selectItem itemLabel="Parcialmente" itemValue="3" />
					<a4j:support id="support" event="onchange" actionListener="#{relatorioProjetoMonitoria.habilitarMotivosMonitores}" reRender="txaMonitoresCumpriramExigenciasJustificativa"/>							
				</h:selectOneRadio>
			</tr>

			<tr>
				<td>Se as respostas for não ou parcialmente explicite os
				motivos: <br />

				<h:inputTextarea id="txaMonitoresCumpriramExigenciasJustificativa" 
					value="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigenciasJustificativa}"
					rows="5" style="width:98%" readonly="#{relatorioProjetoMonitoria.readOnly}" />
				</td>
			</tr>

			<tr>
				<td>A participação dos membros do projeto ( coordenador,
				orientador, monitores), no SID, foi satisfatória? Comente. <br />

				<h:selectOneRadio
					value="#{relatorioProjetoMonitoria.obj.participacaoMembrosSid}"
					id="radioParticipacaoMembrosSid">
					<f:selectItem itemLabel="Sim" itemValue="1" />
					<f:selectItem itemLabel="Regular" itemValue="2" />
					<f:selectItem itemLabel="Ruim" itemValue="3" />
					<a4j:support id="support2" event="onchange" actionListener="#{relatorioProjetoMonitoria.habilitarMotivosMembros}" reRender="txaParticipacaoMembrosSidJustificativa"/>							
				</h:selectOneRadio>
			</tr>

			<tr>
				<td>Se a resposta for regular ou ruim explicite os motivos: <br />

				<h:inputTextarea id="txaParticipacaoMembrosSidJustificativa"
					value="#{relatorioProjetoMonitoria.obj.participacaoMembrosSidJustificativa}"
					rows="5" style="width:98%" readonly="#{relatorioProjetoMonitoria.readOnly}" />
				</td>
			</tr>

			<tr>
				<td><b> D) QUANTO À METODOLOGIA APLICADA: </b><br />

				Escreva estratégias que foram desenvolvidas durante a vigência do
				projeto, explicitando-as quanto aos seguintes aspectos: <br />

				* Articulação com o projeto político-pedagógico do curso. <br />


				<h:inputTextarea id="txaArticulacaoPoliticoPedagogico" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.articulacaoPoliticoPedagogico}" rows="5" />
				</td>
			</tr>

			<tr>
				<td>* Ênfase no estímulo à iniciação à docência. <br />

				<h:inputTextarea id="txaEstimuloIniciacaoDocencia" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.estimuloIniciacaoDocencia}" rows="5" />
				</td>
			</tr>

			<tr>
				<td>* Função do monitor como apoio pedagógico ao
				desenvolvimento das atividades. <br />

				<h:inputTextarea id="txaFuncaoMonitor" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.funcaoMonitor}" rows="5" /></td>
			</tr>

			<tr>
				<td>* Integração entre as áreas do conhecimento. <br />


				<h:inputTextarea id="txaIntegracaoEntreAreas" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.integracaoEntreAreas}" rows="5" /></td>
			</tr>

			<tr>
				<td>* Caráter pedagógico inovador. <br />

				<h:inputTextarea id="txaCaraterInovador" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.caraterInovador}" rows="5" /></td>
			</tr>

			<tr>
				<td><b> E) QUANTO AO APRIMORAMENTO DO PROJETO: </b> <br />

				Indique sugestões que levem à superação das dificuldades e
				aperfeiçoamento do projeto. <br />
				<h:inputTextarea id="txaSugestoes" rows="5" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.sugestoes}" /></td>
			</tr>
			
			<c:set var="RELATORIO_PARCIAL" value="<%=String.valueOf(TipoRelatorioMonitoria.RELATORIO_PARCIAL)%>" scope="application"/>
			<c:if test="${relatorioProjetoMonitoria.obj.tipoRelatorio.id == RELATORIO_PARCIAL }">
				<tr>
					<td><b> Deseja renovar este projeto? </b>
	
					<h:selectOneRadio
						value="#{relatorioProjetoMonitoria.obj.desejaRenovarProjeto}" id="radioDesejaRenovarProjeto">
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
					</h:selectOneRadio>
					
				</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2">
					
						<%-- Salvando o relatório a cada 5 min 
							<a4j:poll id="polling" interval="300000" enabled="true" action="#{relatorioProjetoMonitoria.cadastrar}" />
						--%>
						<h:commandButton id="btSalvar" value="Salvar (Rascunho)" action="#{relatorioProjetoMonitoria.cadastrar}" /> 
						<h:commandButton id="btConfirmar" value="Enviar Relatório" action="#{relatorioProjetoMonitoria.enviar}" />
						<h:commandButton id="btCancelar" value="Cancelar" action="#{relatorioProjetoMonitoria.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	  </h:form>
	</a4j:region>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
