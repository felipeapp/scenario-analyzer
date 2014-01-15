<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> &gt; Consulta de Avaliadores do CIC</h2>
<h:outputText value="#{avaliadorCIC.create}" />
	<h:form id="busca">
	<table class="formulario" width="70%">
		<caption>Busca por Avaliadores do CIC</caption>
		<tbody>
			<tr>
				<td></td>
				<td>Congresso:</td>
				<td>
					<h:selectOneMenu id="selectCongresso" value="#{avaliadorCIC.obj.congresso.id}">
						<f:selectItems value="#{avaliadorCIC.allCongressosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliadorCIC.filtroArea}" id="checkArea" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkArea" onclick="$('busca:checkArea').checked = !$('busca:checkArea').checked;">Área de Conhecimento:</label></td>
				<td>
					<h:selectOneMenu id="selectAreaConhecimentoCnpq"
						value="#{avaliadorCIC.obj.area.id}" style="width: 70%;"
						onfocus="$('busca:checkArea').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{area.allGrandesAreasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliadorCIC.filtroTipo}" id="checkTipo" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkTipo" onclick="$('busca:checkTipo').checked = !$('busca:checkTipo').checked;">Tipo:</label></td>
				<td> 
					<h:selectBooleanCheckbox id="checkAvaliadorResumo" value="#{avaliadorCIC.obj.avaliadorResumo}" 
						onclick="$('busca:checkTipo').checked = $('busca:checkAvaliadorResumo').checked || $('busca:checkAvaliadorApresentacao').checked;"/> 
					Avaliador de Resumo
					&nbsp;&nbsp;&nbsp;&nbsp;
				 	<h:selectBooleanCheckbox id="checkAvaliadorApresentacao" value="#{avaliadorCIC.obj.avaliadorApresentacao}" 
				 		onclick="$('busca:checkTipo').checked = $('busca:checkAvaliadorResumo').checked || $('busca:checkAvaliadorApresentacao').checked;"/> 
				 	Avaliador de Apresentação 
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliadorCIC.filtroRelatorio}" id="checkRelatorio" styleClass="noborder"/> </td>
				<td colspan="2"><label for="checkRelatorio" onclick="$('busca:checkRelatorio').checked = !$('busca:checkRelatorio').checked;"><b>Exibir em formato de relatório<b/></label></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3"><h:commandButton value="Buscar" action="#{avaliadorCIC.buscar}" /> <h:commandButton
					value="Cancelar" action="#{avaliadorCIC.cancelar}" onclick="#{confirm}" /></td>
			</tr>
		</tfoot>
	</table>
	<br />
	<c:if test="${not empty avaliadorCIC.resultadosBusca}">
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{avaliadorCIC.preCadastrar}" value="Cadastrar"/>
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover<br/>
		<%-- 	<h:graphicImage value="/img/pesquisa/certificado.png" style="overflow: visible;" />: Emitir Certificado<br/>--%>
			</div>
		</center>
			<table class=listagem>
			<caption class="listagem">Lista de Avaliadores Encontrados (${fn:length(avaliadorCIC.resultadosBusca)})</caption>
			<thead>
				<tr>
					<td>Docente</td>
					<td>Área</td>
					<td>Avaliador Resumo</td>
					<td>Avaliador Apresentação</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{avaliadorCIC.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.docente.pessoa.nome}</td>
					<td>${item.area.nome}</td>
					<td>${item.avaliadorResumo?'Sim':'Não'}</td>
					<td>${item.avaliadorApresentacao?'Sim':'Não'}</td>
						<td width="5%">
							<h:commandLink title="Atualizar" action="#{avaliadorCIC.atualizar}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/alterar.gif"/>
							</h:commandLink>
						</td>
						<td width="5%">
							<h:commandLink title="Remover" action="#{avaliadorCIC.preRemover}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/delete.gif"/>
							</h:commandLink>
						</td>
	<%--  						<td width=20>
								<h:commandLink actionListener="#{avaliadorCIC.emitirCertificado}" title="Emitir Certificado">
									<f:param name="id" value="#{item.id}"/>
									<h:graphicImage url="/img/pesquisa/certificado.png"/>
								</h:commandLink>
							</td>
	--%>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
