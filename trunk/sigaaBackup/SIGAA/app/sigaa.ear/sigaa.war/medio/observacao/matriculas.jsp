<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="observacaoDiscenteSerieMBean"/>
<h2> <ufrn:subSistema /> &gt; Editar Observa��es em S�rie do Discente &gt; Selecionar Matr�cula</h2>
	
<h:form id="form">

	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Selecione uma das matr�culas do discente, para cadastrar observa��es para a mesma.</p>
	</div>
	
	<c:set var="discente" value="#{observacaoDiscenteSerieMBean.discente}"></c:set>
	<%@include file="/medio/discente/info_discente.jsp"%>		
		
	<div class="infoAltRem">
		<html:img page="/img/seta.gif" style="overflow: visible;"/>: Selecionar Matr�cula
	</div>
	
	<table class="listagem" style="width: 80%;">
	  <caption>Matr�culas Encontradas (${fn:length(observacaoDiscenteSerieMBean.matriculasSerie)})</caption>
		<thead>
			<tr>
				<th>Ano</th>
				<th>S�rie</th>
				<th>Turma</th>
				<th>Situa��o</th>
				<th>Tipo</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{observacaoDiscenteSerieMBean.matriculasSerie}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${linha.turmaSerie.ano}</td>
					<td>${linha.turmaSerie.serie.descricaoCompleta}</td>
					<td>${linha.turmaSerie.nome}</td>
					<td>${linha.descricaoSituacao}</td>
					<td>${linha.tipoMatricula}</td>
					<td width="2%" align="right">
						<h:commandLink action="#{observacaoDiscenteSerieMBean.selecionarMatricula}" >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar Matr�cula" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>