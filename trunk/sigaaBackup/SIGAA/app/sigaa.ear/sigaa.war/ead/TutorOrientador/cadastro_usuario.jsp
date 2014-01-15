<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Usuário para Tutor</h2>

	<h:messages showDetail="true"/>
	<br>
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<h:outputText value="#{tutorOrientador.create}" />
			<caption class="listagem">Cadastro de Tutores</caption>
			<tr>
				<th>Tutor:</th>
				<td><h:outputText value="#{tutorOrientador.obj.pessoa.nome }" /></td>
			</tr>
			<tr>
				<th>Vínculo:</th>
				<td>${tutorOrientador.obj.vinculo.nome }</td>
			</tr>
			<tr>
				<th>Pólo:</th>
				<td>${tutorOrientador.obj.poloCurso.polo.cidade.nomeUF}</td>
			</tr>
			<tr>
				<th>Cursos:</th>
				<td>
					<c:if test="${not empty tutorOrientador.obj.poloCursos}">
						<table>
						<c:forEach items="#{ tutorOrientador.obj.poloCursos }" var="polosCurso">
							<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${polosCurso.curso.nome}</td></tr>
						</c:forEach>	
						</table>
					</c:if>	
				</td>
			</tr>
			<tr>
				<th>Login: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{tutorOrientador.obj.usuario.login}" maxlength="20"/></td>
			</tr>
			<tr>
				<th>E-Mail: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{tutorOrientador.obj.usuario.email}" maxlength="100"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="4">
					<h:inputHidden value="#{ tutorOrientador.obj.id }"/>
					<h:commandButton value="Cadastrar"
						action="#{tutorOrientador.cadastrarUsuario}" /> <h:commandButton value="Cancelar"
						onclick="#{confirm}" action="#{tutorOrientador.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
