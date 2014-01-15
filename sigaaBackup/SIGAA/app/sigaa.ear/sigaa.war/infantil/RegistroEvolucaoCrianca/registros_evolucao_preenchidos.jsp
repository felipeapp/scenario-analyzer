<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> &gt; Registro da Evolução da Criança </h2>

<h:form id="form">
	
	<center>
		<div class="infoAltRem" style="width: 100%;"> 
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma	
		</div>
	</center>
	
	<table class="formulario" width="100%">
		<caption> Turmas Encontradas </caption>
			<c:forEach var="item" items="#{ registroEvolucaoCriancaMBean.matriculas }">
				<tr>
					<td> ${ item.turma.descricaoTurmaInfantil } </td>
					<td>
						<h:commandLink title="Selecionar Turma" action="#{ registroEvolucaoCriancaMBean.selecionarTurma }" >
					        <f:param name="id" value="#{ item.id }"/>
				    		<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
	</table>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>