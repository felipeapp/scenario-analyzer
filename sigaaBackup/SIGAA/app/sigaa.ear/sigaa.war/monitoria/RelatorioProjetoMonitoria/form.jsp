<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria"%>

<f:view>
	
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText value="#{relatorioProjetoMonitoria.create}" />
	<h2><ufrn:subSistema /> &gt; Cadastro de Relat�rio do Projeto</h2>

	<h:messages showDetail="true" showSummary="true"/>

<a4j:region>

	<center><font color="red" size="2"><a4j:status startText="Salvando Relat�rio..." stopText=""/><br/></font></center>
	
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

				De acordo com a Resolu��o n� 013/2006 - CONSEPE, s�o objetivos do
				programa de monitoria: contribuir para a melhoria do ensino na
				gradua��o, contribuir para o processo de forma��o do estudante e
				despertar no monitor o interesse pela carreira docente. <br />

				Com base nos objetivos do programa e do projeto, aponte aqueles que
				foram alcan�ados e os que n�o foram, explicitando motivos que
				dificultaram a realiza��o dos mesmos. <br />

				<h:inputTextarea id="txaObjetivosAlcancados" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.objetivosAlcancados}" rows="5" /></td>
			</tr>

			<tr>
				<td><b> B) QUANTO �S ATRIBUI��ES DOS MONITORES: </b> <br />
				Relacione as atribui��es executadas pelos monitores. <br />
				
				<h:inputTextarea id="txaAtribuicoesExecutadas" style="width:98%" 
					value="#{relatorioProjetoMonitoria.obj.atribuicoesExecutadas}" rows="5"	/>
				</td>
			</tr>

			<tr>
				<td><b> C) QUANTO � PARTICIPA��O DOS MONITORES NO SEMIN�RIO
				DE INICIA��O � DOC�NCIA (SID) : </b> <br />
				Os monitores cumpriram as exig�ncias postas pelo programa de
				monitoria, quanto � apresenta��o de resultados parciais alcan�ados
				pelo projeto? <br />

				<h:selectOneRadio
					value="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigencias}"
					id="radioMonitoresCumpriramExigencias">
					<f:selectItem itemLabel="Sim" itemValue="1" />
					<f:selectItem itemLabel="N�o" itemValue="2" />
					<f:selectItem itemLabel="Parcialmente" itemValue="3" />
					<a4j:support id="support" event="onchange" actionListener="#{relatorioProjetoMonitoria.habilitarMotivosMonitores}" reRender="txaMonitoresCumpriramExigenciasJustificativa"/>							
				</h:selectOneRadio>
			</tr>

			<tr>
				<td>Se as respostas for n�o ou parcialmente explicite os
				motivos: <br />

				<h:inputTextarea id="txaMonitoresCumpriramExigenciasJustificativa" 
					value="#{relatorioProjetoMonitoria.obj.monitoresCumpriramExigenciasJustificativa}"
					rows="5" style="width:98%" readonly="#{relatorioProjetoMonitoria.readOnly}" />
				</td>
			</tr>

			<tr>
				<td>A participa��o dos membros do projeto ( coordenador,
				orientador, monitores), no SID, foi satisfat�ria? Comente. <br />

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
				<td><b> D) QUANTO � METODOLOGIA APLICADA: </b><br />

				Escreva estrat�gias que foram desenvolvidas durante a vig�ncia do
				projeto, explicitando-as quanto aos seguintes aspectos: <br />

				* Articula��o com o projeto pol�tico-pedag�gico do curso. <br />


				<h:inputTextarea id="txaArticulacaoPoliticoPedagogico" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.articulacaoPoliticoPedagogico}" rows="5" />
				</td>
			</tr>

			<tr>
				<td>* �nfase no est�mulo � inicia��o � doc�ncia. <br />

				<h:inputTextarea id="txaEstimuloIniciacaoDocencia" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.estimuloIniciacaoDocencia}" rows="5" />
				</td>
			</tr>

			<tr>
				<td>* Fun��o do monitor como apoio pedag�gico ao
				desenvolvimento das atividades. <br />

				<h:inputTextarea id="txaFuncaoMonitor" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.funcaoMonitor}" rows="5" /></td>
			</tr>

			<tr>
				<td>* Integra��o entre as �reas do conhecimento. <br />


				<h:inputTextarea id="txaIntegracaoEntreAreas" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.integracaoEntreAreas}" rows="5" /></td>
			</tr>

			<tr>
				<td>* Car�ter pedag�gico inovador. <br />

				<h:inputTextarea id="txaCaraterInovador" style="width:98%"
					value="#{relatorioProjetoMonitoria.obj.caraterInovador}" rows="5" /></td>
			</tr>

			<tr>
				<td><b> E) QUANTO AO APRIMORAMENTO DO PROJETO: </b> <br />

				Indique sugest�es que levem � supera��o das dificuldades e
				aperfei�oamento do projeto. <br />
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
						<f:selectItem itemLabel="N�o" itemValue="false" />
					</h:selectOneRadio>
					
				</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2">
					
						<%-- Salvando o relat�rio a cada 5 min 
							<a4j:poll id="polling" interval="300000" enabled="true" action="#{relatorioProjetoMonitoria.cadastrar}" />
						--%>
						<h:commandButton id="btSalvar" value="Salvar (Rascunho)" action="#{relatorioProjetoMonitoria.cadastrar}" /> 
						<h:commandButton id="btConfirmar" value="Enviar Relat�rio" action="#{relatorioProjetoMonitoria.enviar}" />
						<h:commandButton id="btCancelar" value="Cancelar" action="#{relatorioProjetoMonitoria.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	  </h:form>
	</a4j:region>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
