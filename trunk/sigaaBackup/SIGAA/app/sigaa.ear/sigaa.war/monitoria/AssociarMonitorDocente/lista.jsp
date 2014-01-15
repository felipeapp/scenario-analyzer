<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp" %>

<h2><ufrn:subSistema /> >  Escolha o Projeto de Ensino para associar os Monitores</h2>

	<h:messages showDetail="true"/>

	<h:outputText value="#{associarMonitorDocente.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>
	
	<h:inputHidden value="true" id="somenteCoordenador"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Definir Monitores
	</div>

	<h:form>
	<table class="listagem">
	<caption>Lista de Orientadores Disponívies</caption>

	<c:if test="${empty projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}">
            <tr> <td colspan="5" align="center"> <font color="red">Não há Projetos pendentes de definição de monitores<br/> ou o Usuário atual não é Coordenador de projetos ativos</font> </td></tr>
	</c:if>


	 <c:forEach items="#{projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}" var="projeto">
	 	<tr>
			<td colspan="3"><b> ${projeto.titulo}: </b></td>
		</tr>

	 	<c:forEach items="#{projeto.componentesCurriculares}" var="compCurricular">
			<c:forEach items="#{compCurricular.orientacoes}" var="orientacao">
				<tr>
					<td> ${orientacao.orientador.servidor.pessoa.nome} </td>
					<td> ${orientacao.compCurricular.disciplina.nome} </td>
					<td>
					
						<h:commandLink  action="#{associarMonitorDocente.escolherOrientacao}" style="border: 0;">
						       <f:param name="id" value="#{orientacao.id}"/>
				               <h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
						
					</td>
				</tr>
			</c:forEach>
		</c:forEach>
	 </c:forEach>
	</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>