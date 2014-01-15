<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="busca">

<h2><ufrn:subSistema /> &gt; Consulta de Tutores de Pólos</h2>

<table class="formulario" width="40%">
	<caption>Busca por Tutores</caption>
	<tbody>
		<tr>
			<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorOrientador.buscaCurso }" id="chkCurso"/> </td>
			<td width="5%"><label for="busca:chkCurso">Curso: </label></td>
			<td><h:selectOneMenu value="#{tutorOrientador.curso.id}" id="curso" onfocus="marcaCheckBox('busca:chkCurso')">
			<f:selectItem itemValue="0" itemLabel="Escolha um Curso" />
			<f:selectItems value="#{tutorOrientador.cursos}" />
		</h:selectOneMenu> </td>
		</tr>
		<tr>
			<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorOrientador.buscaPolo }" id="chkPolo"/> </td>
			<td width="5%"><label for="busca:chkPolo">Pólo: </label></td>
			<td><h:selectOneMenu value="#{tutorOrientador.polo.id}" id="polo" onfocus="marcaCheckBox('busca:chkPolo')">
			<f:selectItem itemValue="0" itemLabel="Escolha um Pólo" />
			<f:selectItems value="#{tutorOrientador.polos}" />
		</h:selectOneMenu> </td>
		</tr>
		<tr>
			<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorOrientador.buscaNome }" id="chkNome"/> </td>
			<td width="5%"><label for="busca:chkNome">Nome: </label></td>
			<td><h:inputText value="#{tutorOrientador.nome}" id="nome" size="40" onfocus="marcaCheckBox('busca:chkNome')"/> </td>
		</tr>
		<tr>
			<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorOrientador.buscaSomenteAtivos }" id="chkAtivo"/> </td>
			<td colspan ="2" width="10%"><label for="busca:chkAtivo">Somente Tutores Ativos</label></td>
		</tr>
		<tr>
			<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorOrientador.buscaSomenteComOrientados }" id="chkComOrientados"/> </td>
			<td colspan ="2" width="10%"><label for="busca:chkComOrientados">Somente Tutores com Orientados Associados </label></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3"><h:commandButton value="Buscar" action="#{tutorOrientador.buscar}" /> <h:commandButton
				value="Cancelar" onclick="#{confirm}" action="#{tutorOrientador.cancelar}" /></td>
		</tr>
	</tfoot>
</table>

<br/>



<c:if test="${not empty tutorOrientador.resultadosBusca}">
	<div class="infoAltRem">
	<h:graphicImage alt="Cadastrar Usuário" value="/img/user.png" style="overflow: visible;" />: Cadastrar Usuário
	<h:graphicImage alt="Reativar Usuário" value="/img/refresh.png" style="overflow: visible;" />: Reativar Usuário
	<h:graphicImage alt="Definir Horário" value="/img/clock.png" style="overflow: visible;" />: Definir Horário
	<h:graphicImage alt="Alterar Tutor" value="/img/alterar.gif" style="overflow: visible;" />: Alterar Tutor
	<h:graphicImage alt="Habilitar Tutor" value="/img/check.png" style="overflow: visible;"/>: Habilitar Tutor
	<h:graphicImage alt="Desabilitar Tutor" value="/img/cross.png" style="overflow: visible;"/>: Desabilitar Tutor
	</div>
	
	<table class="listagem">
		<caption>Tutores Encontrados</caption>
		<thead>
			<tr>
				<td>Nome</td>
				<td>Pólo</td>
				<td>Cursos</td>
				<td>Usuário</td>
				<td nowrap="nowrap" style="text-align:right;">Total Orientandos</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="#{tutorOrientador.resultadosBusca}" var="item" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>${item.pessoa.nome}</td>
				<td>${item.poloCurso.polo.cidade.nomeUF}</td>
				<td>
					<c:forEach items="#{item.poloCursos}" var="pc" varStatus="loop">
						<c:if test="${loop.index!=0}"><br/></c:if>
						${pc.curso.descricao}
					</c:forEach>
				</td>
				<td><h:outputText value="#{ item.usuario.login }" rendered="#{ item.usuario.login != null }"/></td>
				<td align="right">${item.totalOrientandos}</td>
				<td width="20">
					<h:commandLink styleClass="noborder"  action="#{tutorOrientador.formUsuario}" rendered="#{ item.usuario == null }" title="Cadastrar Usuário">
						<f:param value="#{item.id}" name="id" />
						<h:graphicImage value="/img/user.png"/> 
					</h:commandLink>
				</td>
				<td width="20">
					<h:commandLink styleClass="noborder"  action="#{tutorOrientador.reativarUsuario}" rendered="#{ item.usuario.inativo }" title="Reativar Usuário">
						<f:param value="#{item.id}" name="id" />
						<h:graphicImage value="/img/refresh.png"/> 
					</h:commandLink>
				</td>
				<td width="20">
					<h:commandLink styleClass="noborder" action="#{tutorOrientador.definirHorario}" title="Definir Horário" rendered="#{item.ativo}">
						<f:param value="#{item.id}" name="id" />
						<h:graphicImage value="/img/clock.png"/> 
					</h:commandLink>
				</td>
				<td width="20">
					<h:commandLink styleClass="noborder"  action="#{tutorOrientador.atualizar}" title="Alterar Tutor">
						<f:param value="#{item.id}" name="id" />
						<h:graphicImage value="/img/alterar.gif"/> 
					</h:commandLink>
				</td>
				<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.SEDIS, SigaaPapeis.TUTOR_EAD } %>">
					<td width="20">
						
						<h:commandLink styleClass="noborder" action="#{tutorOrientador.reativarTutor}" onclick="return confirm('Tem certeza que deseja habilitar esse tutor?');" immediate="true" rendered="#{!item.ativo}" title="Habilitar Tutor">
							<f:param value="#{item.id}" name="id" />
							<h:graphicImage value="/img/check.png"/> 
						</h:commandLink>
					
						<h:commandLink styleClass="noborder" action="#{tutorOrientador.inativar}" onclick="return confirm('Tem certeza que deseja desabilitar esse tutor?');" immediate="true" rendered="#{item.ativo}" title="Desabilitar Tutor">
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
