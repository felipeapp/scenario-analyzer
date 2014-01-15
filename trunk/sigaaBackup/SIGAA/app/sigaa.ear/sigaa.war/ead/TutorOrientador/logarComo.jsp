<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Logar como Tutor</h2>

	<div class="descricaoOperacao">
		Selecione um item da lista de usuários e clique em <strong>Logar</strong>
		para acessar o sistema com a visão do tutor. É possível filtrar a
		lista de tutores escolhendo um curso ou um pólo na lista de filtros. 
	</div>

	<h:form id="formulario">
	<h:messages showDetail="true"/>
	<br/>

	<table class="formulario" width="70%">
	<caption>Tutores com usuário cadastrado</caption>
	<tr>
		<th>Tutor:</th>
		<td>
			<h:selectOneMenu value="#{ logarComo.usuario.id }">
				<f:selectItem itemValue="0" itemLabel="Selecione um Usuário" />
				<f:selectItems value="#{ logarComo.tutores }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Filtrar tutores</caption>
				<tr>
					<th nowrap="nowrap">Por Curso:</th>
					<td>
						<h:selectOneMenu value="#{ logarComo.curso.id }" valueChangeListener="#{ logarComo.carregarTutoresCurso }" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="Selecione um Curso" />
							<f:selectItems value="#{tutoriaAluno.cursosComPolo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Por Pólo:</th>
					<td>
						<h:selectOneMenu value="#{ logarComo.polo.id }" valueChangeListener="#{ logarComo.carregarTutoresPolo }" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="Selecione um Pólo" />
							<f:selectItems value="#{tutoriaAluno.polos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Logar" action="#{ logarComo.logar }"/>
			<h:commandButton value="Cancelar" action="#{ logarComo.cancelar }"/>
		</td>
	</tr>
	</tfoot>
	</table>

	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
