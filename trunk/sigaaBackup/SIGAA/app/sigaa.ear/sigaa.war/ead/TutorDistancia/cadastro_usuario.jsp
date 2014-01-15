<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Usuário para Tutor</h2>

	<h:messages showDetail="true"/>
	<br>
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<h:outputText value="#{tutorDistancia.create}" />
			<caption class="listagem">Cadastro de Tutores</caption>
			<tr>
				<th>Tutor:</th>
				<td><h:outputText value="#{tutorDistancia.obj.pessoa.nome }" /></td>
			</tr>
			<tr>
				<th>Pólos:</th>
				<td>
					<table>
					<c:forEach items="#{ tutorDistancia.obj.polosTurma }" var="polos">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${polos.descricao}</td></tr>
					</c:forEach>	
					</table>	
				</td>
			</tr>	
			<tr>
				<th>Disciplinas:</th>
				<td>
					<c:if test="${not empty tutorDistancia.obj.disciplinas}">
						<table>
						<c:forEach items="#{ tutorDistancia.obj.disciplinas }" var="disc">
							<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${disc.nome}</td></tr>
						</c:forEach>	
						</table>
					</c:if>	
				</td>
			</tr>	
			<tr>
				<th>Login: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{tutorDistancia.obj.usuario.login}" maxlength="20"/></td>
			</tr>
			<tr>
				<th>E-Mail: <span class="required">&nbsp;</span></th>
				<td><h:inputText value="#{tutorDistancia.obj.usuario.email}" maxlength="100"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
					<h:inputHidden value="#{ tutorDistancia.obj.id }"/>
					<h:commandButton value="Cadastrar"
						action="#{tutorDistancia.cadastrarUsuario}" /> <h:commandButton value="Cancelar"
						onclick="#{confirm}" action="#{tutorDistancia.cancelar}" /></td>
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
