<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="consolidacaoTurmaInfantilMBean" />
<f:view>

<h:form id="form">
	<h2> <ufrn:subSistema /> &gt; Consolidação de Turma Infantil</h2>
	<table class="formulario" style="width:50%;">
	  <caption>Informe a turma</caption>
		<tbody>
				<tr>
					<th class="obrigatorio"> Turma:</th>
					<td> 
						<h:selectOneMenu id="nivelTurma" value="#{consolidacaoTurmaInfantilMBean.idTurma}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{turmaInfantilMBean.niveisCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Ano:</th>
					<td><h:inputText value="#{consolidacaoTurmaInfantilMBean.ano}" id="ano"/></td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{consolidacaoTurmaInfantilMBean.buscar }" value="Buscar" />
						<h:commandButton action="#{consolidacaoTurmaInfantilMBean.cancelar }" onclick="#{confirm}" value="Cancelar" />
					</td>
				</tr>
			</tfoot>
		</tbody>
	</table>
	<br/>

	<c:if test="${not empty consolidacaoTurmaInfantilMBean.turmas}">
	<br/>
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Selecionar Turma
	</div>
		<table class="listagem">
			<caption>Turmas Encontradas</caption>
			<thead>
				<tr>
					<th>Turma</th>
					<th></th>
				</tr>
			</thead>
			<c:forEach items="#{consolidacaoTurmaInfantilMBean.turmas}" var="turma" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${ turma.descricaoTurmaInfantil }</td>
						<td>
							<h:commandLink title="Consolidar Turma" action="#{consolidacaoTurmaInfantilMBean.listarAlunos }" id="consolidacao"  rendered="#{!turma.consolidada}">
					        	<f:param name="id" value="#{turma.id}"/>
				    			<h:graphicImage url="/img/seta.gif"  />
							</h:commandLink>
						</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>