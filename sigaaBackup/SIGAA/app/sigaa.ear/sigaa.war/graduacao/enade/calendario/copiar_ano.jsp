<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form>
	<h2><ufrn:subSistema /> &gt; Calend�rio de Aplica��o do ENADE</h2>
	<br/>
 		<div class="descricaoOperacao">
 			<p>Caro Usu�rio,</p>
 			<p>Para agilizar o processo de cadastro de um novo calend�rio de aplica��o do ENADE, voc� poder� aproveitar a lista de cursos de um calend�rio ja cadastrado.</p>
 			<p>Escolha um calend�rio abaixo, ou selecione a op��o de "n�o importar cursos de outro calend�rio"</p>
 		</div>
		<a4j:keepAlive beanName="calendarioEnadeMBean"></a4j:keepAlive>
		<br/>
		
		<c:if test="${ calendarioEnadeMBean.obj.id == 0 }">
		<table class="formulario" width="40%">
			<caption>Filtrar pelo Tipo de Calend�rio do ENADE</caption>
			<tbody>
			<tr>
				<th width="35%">Tipo:</th>
				<td>
					<h:selectOneMenu value="#{ calendarioEnadeMBean.tipoEnade}" id="filtrotipoEnade" 
						valueChangeListener="#{ calendarioEnadeMBean.atualizarOutrosCalendarios}" onchange="submit()">
						<f:selectItem itemValue="" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ participacaoEnade.tipoEnadeCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						 <h:commandButton value="<< Voltar" action="#{calendarioEnadeMBean.listar}" id="btnVoltar"  />
						 <h:commandButton value="Cancelar" action="#{calendarioEnadeMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		</c:if>
		
		<div class="infoAltRem" style="width: 100%">
			<h:commandLink action="#{ calendarioEnadeMBean.importarCursos }" id="importarCursos">
				<h:graphicImage value="#{ctx}/img/adicionar.gif" />
				N�o Importar Cursos de Calend�rios Anteriores
				<f:param name="id" value="0" />
			</h:commandLink>
			
			<img src="/sigaa/img/seta.gif" style="overflow: visible;" />: Selecionar
	 	</div>
		<table class="listagem">
			<caption>Importar Cursos do Calend�rio Anterior</caption>
			<thead>
				<tr>
					<th style="text-align: right;" width="50px">Ano</th>
					<th >Cursos</th>
					<th width="20px"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{calendarioEnadeMBean.outrosCalendarios}" var="item" varStatus="loop" >
					<c:set var="index" value="${loop.index}"/>
					<tr class="${ index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: right;">${item.ano}</td>
						<td>
							<c:forEach items="#{item.cursosGrauAcademico}" var="itemCurso" varStatus="loopCurso" >
								<h:outputText value="," rendered="#{ loopCurso.index > 0 }"/>
								<h:outputText value="#{ itemCurso.curso.descricao }"/> - 
								<h:outputText value="#{ itemCurso.grauAcademico.descricao }"/> 
								( <h:outputText value="#{ itemCurso.curso.municipio }" /> )
							</c:forEach>
						</td>
						<td>
							<h:commandLink action="#{ calendarioEnadeMBean.importarCursos }" id="naoImportarCursos">
								<h:graphicImage value="/img/seta.gif" alt="Selecionar" title="Selecionar" rendered="#{ item.id > 0 }"/>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
