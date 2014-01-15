<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
<h:form>

<ufrn:subSistema teste="portalDocente">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</ufrn:subSistema>

<h2> Atender Solicita��o de Turma > Adicionar Reserva a Turma J� Criada</h2>

<c:set var="resultado" value="#{turmaGraduacaoBean.outrasTurmas}" />
<c:if test="${not empty resultado}">
	<center>
		<div class="infoAltRem">
			<h:graphicImage  value="/img/add2.png" style="overflow: visible;" />: Adicionar Outras Reservas a Esta Turma
		</div>
	</center>

	<table class="formulario" id="lista-turmas">
		<caption>Turmas Do Mesmo Componente e do Mesmo Hor�rio Encontradas (${ fn:length(resultado) })</caption>
		<thead>
			<td>Ano.Per�odo</td>
			<td>Disciplina</td>
			<td>Turma</td>
			<td>Situa��o</td>
			<td>Hor�rio</td>
			<td>Local</td>
			<td>Total Alunos</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</thead>
		<tbody>

		<c:forEach items="#{resultado}" var="t" varStatus="s">

				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td width="5%">${t.ano}.${t.periodo}</td>
					<td width="35%">${t.disciplina.descricaoResumida}</td>
					<td width="8%">Turma ${t.codigo}</td>
					<td width="8%">${t.situacaoTurma.descricao}</td>
					<td width="8%">${t.descricaoHorario}</td>
					<td width="10%">${t.local}</td>
					<td width="7%">
						${t.qtdMatriculados } alunos
					</td>
					<td width="0%" align="right">
						<c:if test="${ (acesso.chefeDepartamento || acesso.secretarioDepartamento) && turmaGraduacaoBean.periodoCadastroTurma}">
						<h:commandLink styleClass="noborder" title="Adicionar Outras Reservas" action="#{turmaGraduacaoBean.atualizar}">
							<h:graphicImage url="/img/add2.png"/>
							<f:param name="id" value="#{t.id}"/>
							<f:param name="adicionarReservas" value="true"/>
						</h:commandLink>
						</c:if>
					</td>
				</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="10">
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ analiseSolicitacaoTurma.gerenciarSolicitacoesTodas }" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
</c:if>

<c:if test="${empty resultado}">
	<center><font color="red">Neste semestre n�o h� nenhuma turma criada deste componente neste mesmo hor�rio.</font></center>
</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
