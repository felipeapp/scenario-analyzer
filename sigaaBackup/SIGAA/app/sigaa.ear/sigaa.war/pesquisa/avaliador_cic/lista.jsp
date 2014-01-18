<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> &gt; Consulta de Avaliadores do CIC</h2>
<h:outputText value="#{avaliadorCIC.create}" />
	<h:form id="busca">
	
	<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{avaliadorCIC.preCadastrar}" value="Cadastrar"/>
			</div>
		</center>
	<table class="formulario" width="70%">
		<caption>Busca por Avaliadores do CIC</caption>
		<tbody>
			<tr>
				<td></td>
				<td align="right" class="obrigatorio">Congresso:&nbsp;&nbsp;</td>
				<td>
					<h:selectOneMenu id="selectCongresso" value="#{avaliadorCIC.obj.congresso.id}" style="width: 350px;">
						<f:selectItems value="#{avaliadorCIC.allCongressosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliadorCIC.filtroArea}" id="checkArea" styleClass="noborder"/> </td>
				<td width="22%" align="right"><label for="checkArea" onclick="$('busca:checkArea').checked = !$('busca:checkArea').checked;">Área de Conhecimento:</label></td>
				<td>
					<h:selectOneMenu id="selectAreaConhecimentoCnpq"
						value="#{avaliadorCIC.obj.area.id}" style="width:350px;"
						onfocus="$('busca:checkArea').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{area.allGrandesAreasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"></td>
				<td align="right" width="22%"><label for="checkTipo" onclick="$('busca:checkTipo').checked = !$('busca:checkTipo').checked;">Tipo:</label></td>
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
			<%-- 
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliadorCIC.filtroRelatorio}" id="checkRelatorio" styleClass="noborder"/> </td>
				<td colspan="2"><label for="checkRelatorio" onclick="$('busca:checkRelatorio').checked = !$('busca:checkRelatorio').checked;"><b>Exibir em formato de relatório<b/></label></td>
			</tr>
			--%>
			<tr>
				<td></td>
				<td width="22%" align="right"><label for="checkStatus" onclick="$('busca:checkStatus').checked;">Tipo do Usuário:</label><span class="obrigatorio"></span> </td>
				<td>
					<h:selectOneRadio value="#{ avaliadorCIC.obj.tipoUsuario }" onfocus="$('busca:checkStatus').checked = true;" id="selectStatus">
						<f:selectItems value="#{ avaliadorCIC.comboStatusPessoa }" />
					</h:selectOneRadio>
				</td>
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
	<c:if test="${not empty avaliadorCIC.lista}">
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover<br/>
		<%-- 	<h:graphicImage value="/img/pesquisa/certificado.png" style="overflow: visible;" />: Emitir Certificado<br/>--%>
			</div>
		</center>
			<table class=listagem>
			<caption class="listagem">Lista de Avaliadores Encontrados (${fn:length(avaliadorCIC.lista)})</caption>
			<thead>
				<tr>
					<td>Docente/Discente</td>
					<td>Área</td>
					<td>Avaliador Resumo</td>
					<td>Avaliador Apresentação</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{avaliadorCIC.lista}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> 
						<h:outputText value="#{ item.docente.pessoa.nome  }" rendered="#{ item.docente.pessoa.nome !=null }" />
						<h:outputText value="#{ item.discente.pessoa.nome  }" rendered="#{ item.docente.pessoa.nome ==null && item.discente.pessoa.nome != null }" />
					</td>
					
					<td>${item.area.nome}</td>
					<td>${item.avaliadorResumo?'Sim':'Não'}</td>
					<td>${item.avaliadorApresentacao?'Sim':'Não'}</td>
						<td width="3%">
							<h:commandLink title="Alterar" action="#{avaliadorCIC.atualizar}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/alterar.gif"/>
							</h:commandLink>
						</td>
						<td width="3%">
							<h:commandLink title="Remover" action="#{avaliadorCIC.remover }" onclick="#{confirmDelete}">
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
