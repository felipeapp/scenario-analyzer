<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="busca">
<a4j:keepAlive beanName="tutorDistancia" />

<h2><ufrn:subSistema /> &gt; Consulta de Tutores de Pólos</h2>

<table class="formulario" width="40%">
	<caption>Busca por Tutores</caption>
	<tbody>
		<tr>
			<td style="width:1%"><h:selectBooleanCheckbox value="#{ tutorDistancia.buscaAnoPeriodo }" id="chkAnoPeriodo"/> </td>
			<td width="1%"><label for="busca:chkNome">Ano-Período: </label></td>
			<td>			
				<h:inputText value="#{ tutorDistancia.anoTutor }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano" onfocus="marcaCheckBox('busca:chkAnoPeriodo')"/>-
				<h:inputText value="#{ tutorDistancia.periodoTutor }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo" onfocus="marcaCheckBox('busca:chkAnoPeriodo')"/>
			</td>
		</tr>
		<tr>
			<td style="width:1%"><h:selectBooleanCheckbox value="#{ tutorDistancia.buscaNome }" id="chkNome"/> </td>
			<td width="1%"><label for="busca:chkNome">Nome: </label></td>
			<td><h:inputText value="#{tutorDistancia.nome}" id="nome" size="40" onfocus="marcaCheckBox('busca:chkNome')" onkeyup="verificarEnter(this,event);"/> </td>
		</tr>
		<tr>
			<td style="width:1%"><h:selectBooleanCheckbox value="#{ tutorDistancia.buscaDisciplina }" id="chkDisciplina"/> </td>
			<td width="1%"><label for="busca:chkDisciplina">Disciplina: </label></td>
			<td><h:inputText value="#{tutorDistancia.nomeDisciplina}" id="disciplina" size="40" onfocus="marcaCheckBox('busca:chkDisciplina')" onkeyup="verificarEnter(this,event);"/> </td>
		</tr>
		<tr>
			<td> <h:selectBooleanCheckbox value="#{ tutorDistancia.buscaSomenteAtivos }" id="chkAtivo"/> </td>
			<td colspan ="2" width="10%"><label for="busca:chkAtivo">Somente Tutores Ativos</label></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>

			<td colspan="3"><h:commandButton value="Buscar" action="#{tutorDistancia.buscar}" /> <h:commandButton
				value="Cancelar" onclick="#{confirm}" action="#{tutorDistancia.cancelar}" /></td>
		</tr>
	</tfoot>
</table>

<br/>



<c:if test="${not empty tutorDistancia.resultadosBusca}">
	<div class="infoAltRem">
	<h:graphicImage alt="Cadastrar Usuário" value="/img/user.png" style="overflow: visible;" />: Cadastrar Usuário
	<h:graphicImage alt="Alterar Tutor" value="/img/alterar.gif" style="overflow: visible;" />: Alterar Tutor
	<h:graphicImage alt="Habilitar Tutor" value="/img/check.png" style="overflow: visible;"/>: Habilitar Tutor
	<h:graphicImage alt="Desabilitar Tutor" value="/img/cross.png" style="overflow: visible;"/>: Desabilitar Tutor
	</div>
	
	<table class="listagem">
		<caption>Tutores Encontrados ( ${fn:length(tutorDistancia.resultadosBusca)} )</caption>
		<thead>
			<tr>
				<td width="25%">Nome</td>
				<td>Disciplinas</td>
				<td>Usuário</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="#{tutorDistancia.resultadosBusca}" var="item" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>${item.pessoa.nome}</td>
				<td>
					<c:if test="${not empty item.disciplinas}">
						<table>
						<c:forEach items="#{ item.disciplinas }" var="disc">
							<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${disc.descricaoResumida}</td></tr>
						</c:forEach>	
						</table>
					</c:if>	
				</td>
				<td><h:outputText value="#{ item.usuario.login }" rendered="#{ item.usuario.login != null }"/></td>
				<td width="20">
					<h:commandLink styleClass="noborder"  action="#{tutorDistancia.formUsuario}" rendered="#{ item.usuario == null }" title="Cadastrar Usuário">
						<f:param value="#{item.id}" name="id" />
						<h:graphicImage value="/img/user.png"/> 
					</h:commandLink>
				</td>
				<td width="20">
					<h:commandLink styleClass="noborder"  action="#{tutorDistancia.preAlterar}" title="Alterar Tutor">
						<f:param value="#{item.id}" name="idTutor" />
						<h:graphicImage value="/img/alterar.gif"/> 
					</h:commandLink>
				</td>
				<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.SEDIS, SigaaPapeis.TUTOR_EAD } %>">
					<td width="20">
						
						<h:commandLink styleClass="noborder" action="#{tutorDistancia.reativarTutor}" onclick="return confirm('Tem certeza que deseja habilitar esse tutor?');" immediate="true" rendered="#{!item.ativo}" title="Habilitar Tutor">
							<f:param value="#{item.id}" name="id" />
							<h:graphicImage value="/img/check.png"/> 
						</h:commandLink>
					
						<h:commandLink styleClass="noborder" action="#{tutorDistancia.inativarTutor}" onclick="return confirm('Tem certeza que deseja desabilitar esse tutor?');" immediate="true" rendered="#{item.ativo}" title="Desabilitar Tutor">
							<f:param value="#{item.id}" name="id" />
							<h:graphicImage value="/img/cross.png"/> 
						</h:commandLink>
						
					</td>
				</ufrn:checkRole>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
